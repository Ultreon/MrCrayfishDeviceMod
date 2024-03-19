package io.github.xypercode.craftyconfig;

import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import de.marhali.json5.*;
import de.marhali.json5.exception.Json5Exception;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The base class for all configuration files.
 * Files are stored in the path provided in {@link FabricLoader#getConfigDir()}.
 * Those files are also automatically reloaded when they are modified.
 * Configs are saved in JSON5 format. See {@link Json5} for more information.
 *
 * @see <a href="https://spec.json5.org/">JSON5 Specification</a>
 * @see Json5
 * @author <a href="https://github.com/XyperCode">XyperCode</a>
 */
public abstract class CraftyConfig {
    private static final Map<String, CraftyConfig> CONFIGS = new HashMap<>();
    private static final ScheduledExecutorService WATCHER = Executors.newSingleThreadScheduledExecutor();
    private static final WatchService WATCH_SERVICE;

    static {
        // Schedule the CraftyConfig update task to run every 2 seconds with an initial delay of 10 seconds
        WATCHER.scheduleAtFixedRate(CraftyConfig::update, 10, 2, TimeUnit.SECONDS);

        try {
            // Create a watch service for the default file system
            WATCH_SERVICE = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            // Throw a runtime exception if an IOException occurs
            throw new RuntimeException(e);
        }

        // Add a shutdown hook to handle cleanup tasks
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Save all the CraftyConfig instances
            for (CraftyConfig config : CONFIGS.values()) {
                config.save();
            }

            // Shutdown the scheduled task
            WATCHER.shutdown();

            try {
                // Close the watch service
                WATCH_SERVICE.close();
            } catch (IOException e) {
                // Throw a runtime exception if an IOException occurs
                throw new RuntimeException(e);
            }
        }));
    }

    private final Map<String, ConfigEntry> entriesMap;
    private final Map<String, Object> defaultsMap;
    private final Map<String, Class<?>> typesMap;
    private final Map<String, Field> fieldsMap;
    private final Map<String, Ranged> rangesMap;
    private final Path configPath;
    public final Event<LoadConfig> event = EventFactory.createLoop();

    private static WatchKey watchKey;

    /**
     * Constructor for CraftyConfig class.
     * Initializes the configuration based on annotations present in the class fields.
     */
    public CraftyConfig() {

        // Get the class of the config
        Class<? extends CraftyConfig> configClass = getClass();
        // Get the ConfigInfo annotation of the class
        ConfigInfo annotation = configClass.getAnnotation(ConfigInfo.class);

        // Check if the annotation is missing
        if (annotation == null)
            throw new IllegalStateException("Class " + configClass + " is not annotated with @ConfigInfo");

        // Set the file name for the configuration
        this.configPath = Platform.getConfigFolder().resolve(annotation.fileName() + ".json5");

        // Get all declared fields of the class
        Field[] declaredFields = configClass.getDeclaredFields();

        // Initialize maps for configuration entries, defaults, types, fields, and ranges
        entriesMap = new HashMap<>();
        defaultsMap = new HashMap<>();
        typesMap = new HashMap<>();
        fieldsMap = new HashMap<>();
        rangesMap = new HashMap<>();

        // Process each field for configuration entry annotation
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(ConfigEntry.class)) {
                // Check field modifiers
                if (!Modifier.isPublic(field.getModifiers()))
                    throw new IllegalStateException("Field " + field + " is not public but is annotated with @ConfigEntry");
                if (Modifier.isFinal(field.getModifiers()))
                    throw new IllegalStateException("Field " + field + " is final but is annotated with @ConfigEntry");
                if (!Modifier.isStatic(field.getModifiers()))
                    throw new IllegalStateException("Field " + field + " is not static but is annotated with @ConfigEntry");

                // Process the entry
                processEntry(field);
            }
        }

        // Add the configuration to the global map
        CONFIGS.put(annotation.fileName() + ".json5", this);
    }

    /**
     * Get the CraftyConfig instance associated with the given file name.
     *
     * @param fileName The name of the file to get the CraftyConfig instance for.
     * @return The CraftyConfig instance associated with the given file name.
     */
    public static CraftyConfig getConfig(String fileName) {
        return CONFIGS.get(fileName);
    }

    /**
     * Reset all CraftyConfig instances.
     */
    public static void resetAll() {
        CONFIGS.values().forEach(CraftyConfig::reset);
    }

    /**
     * Save all CraftyConfig instances.
     */
    public static void saveAll() {
        CONFIGS.values().forEach(CraftyConfig::save);
    }

    /**
     * Load all CraftyConfig instances.
     */
    public static void loadAll() {
        CONFIGS.values().forEach(CraftyConfig::load);
    }

    public static Collection<? extends CraftyConfig> getConfigs() {
        return CONFIGS.values();
    }

    /**
     * Load the configuration from the specified file, replacing any existing values with defaults if necessary.
     *
     * @return true if the configuration was loaded successfully, false otherwise
     * @throws IOException if an I/O error occurs
     */
    protected boolean loadUnsafe() throws IOException {
        // Parse the JSON5 file into a Json5Element
        Json5Element root = Reference.JSON5.parse(Files.readString(this.configPath, StandardCharsets.UTF_8));

        // Check if the root element is an object
        if (!(root instanceof Json5Object)) {
            throw new Json5Exception("Root element is not an object");
        }

        boolean success = true;
        // Iterate through the defaultsMap entries and update the configuration values
        for (Map.Entry<String, Object> entry : defaultsMap.entrySet()) {
            String path = entry.getKey();
            Object defaultValue = entry.getValue();
            ConfigEntry configEntry = entriesMap.get(path);
            Field field = fieldsMap.get(path);
            Class<?> type = typesMap.get(path);

            // Skip processing if any of the required elements are null
            if (field == null || type == null || configEntry == null) continue;

            try {
                // Get the current value for the config entry
                Json5Element current = getElement(root, path);
                if (current == null) {
                    // Set the default value if the current value is null
                    field.set(null, defaultValue);
                }

                // Parse the current value based on the type and update the field with the parsed value
                Object value = parseValue(current, type, rangesMap.get(path));
                if (configEntry.defaulted() || defaultsMap.get(path) != null) {
                    // Set the value if it's a default or if there's an existing default value
                    field.set(null, value);
                } else {
                    // Set the defaults for the field if necessary
                    this.setDefaults(field, type);
                }
            } catch (IllegalAccessException e) {
                // Throw an exception if there's an access error
                throw new IllegalStateException("Failed to load config entry " + path, e);
            } catch (Exception e) {
                // Log an error and set success to false if an error occurs during processing
                Devices.LOGGER.error("Failed to load config entry " + path, e);
                success = false;
            }
        }

        return success;
    }

    /**
     * Saves the configurations to a file. Any missing fields will be filled with default values.
     *
     * @throws IOException if an I/O error occurs
     */
    protected void saveUnsafe() throws IOException {
        // Create a Json5Object to hold the configurations
        Json5Object root = new Json5Object();

        // Iterate through the default configurations
        for (Map.Entry<String, Object> entry : defaultsMap.entrySet()) {
            String path = entry.getKey();
            ConfigEntry configEntry = entriesMap.get(path);
            Field field = fieldsMap.get(path);
            Class<?> type = typesMap.get(path);

            // Skip if any necessary information is missing
            if (field == null || type == null || configEntry == null) continue;

            try {
                // Get the value of the field
                Object value = field.get(null);
                String comment = configEntry.comment();

                // Set the comment based on whether it is blank or not
                if (comment.isBlank()) {
                    comment = "Default value: " + serializeValue(value, type);
                } else {
                    comment += "\n\nDefault value: " + serializeValue(value, type);
                }

                // Serialize the value and add it to the Json5Object
                this.setElement(root, path, value == null ? Json5Null.INSTANCE : serializeValue(value, type), comment);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to save config entry " + path, e);
            } catch (Exception e) {
                Devices.LOGGER.error("Failed to save config entry " + path, e);
            }
        }

        // Write the serialized configurations to a file
        Files.writeString(this.configPath, serialize(root), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * Disables the watcher, saves the file, and then re-enables the watcher.
     */
    public void save() {
        disableWatcher();

        try {
            saveUnsafe();
        } catch (Exception e) {
            Devices.LOGGER.error("Failed to save config file " + this.configPath, e);
        }

        enableWatcher();
    }

    /**
     * Disables the watcher by canceling the watch key if it's not null.
     */
    private static void disableWatcher() {
        if (watchKey == null) return;

        watchKey.cancel();
        watchKey = null;
    }

    /**
     * Enable file watcher on the configuration directory.
     */
    private static void enableWatcher() {
        try {
            // Register the watch service for entry modify, delete, and create events
            watchKey = Platform.getConfigFolder().register(WATCH_SERVICE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            // Log error if failed to create watch service
            Devices.LOGGER.error("Failed to create watch service", e);
        }
    }

    /**
     * Reset the configuration to default values and delete the configuration file.
     */
    public void reset() {
        // Delete the configuration file if it exists or do nothing if it doesn't
        try {
            Files.deleteIfExists(this.configPath);
        } catch (IOException e) {
            // Log an error if the deletion fails
            Devices.LOGGER.error("Failed to reset config file " + this.configPath, e);
        }

        // Disable the watcher to prevent unnecessary notifications
        disableWatcher();

        // Set default values for all configuration fields
        this.fieldsMap.forEach((ignored, field) -> {
            try {
                this.setDefaults(field, field.getType());
            } catch (IllegalAccessException e) {
                // Log an error if setting default value fails
                Devices.LOGGER.error("Failed to reset config entry " + field.getName(), e);
            }
        });

        // Save the configuration to the file
        this.save();

        // Enable the watcher to monitor future changes
        enableWatcher();
    }

    /**
     * This method updates the configuration files when there are modifications or new files in the watch directory.
     */
    public static void update() {
        // Get the list of watch events
        List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

        // Process each watch event
        if (watchEvents != null) {
            for (WatchEvent<?> watchEvent : watchEvents) {
                // Get the context of the watch event
                Object context = watchEvent.context();

                // Skip if the context is not a Path
                if (!(context instanceof Path)) continue;

                // Skip if the event kind is not ENTRY_MODIFY or ENTRY_CREATE
                if (watchEvent.kind() != StandardWatchEventKinds.ENTRY_MODIFY && watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE)
                    continue;

                // Get the file name from the context
                String fileName = context.toString();
                // Skip if the file is not a .json5 file
                if (!fileName.endsWith(".json5")) continue;

                // Log the reloading of the config file
                Devices.LOGGER.info("Reloading config file " + context);

                // Get the config file from the CONFIGS map
                CraftyConfig craftyConfig = CONFIGS.get(fileName);
                if (craftyConfig == null) continue;

                // Disable the watcher while updating the config file
                disableWatcher();
                try {
                    // Attempt to load the config file
                    if (!craftyConfig.loadUnsafe()) {
                        // Log an error if failed to reload and save the config file
                        Devices.LOGGER.error("Failed to reload config file " + context);
                        craftyConfig.save();
                    }
                } catch (IOException e) {
                    // Log an error if failed to reload due to an exception and save the config file
                    Devices.LOGGER.error("Failed to reload config file " + context, e);
                    craftyConfig.save();
                }
                // Enable the watcher after updating the config file
                enableWatcher();
            }
        }
    }

    /**
     * Serialize a Json5Object to a CharSequence.
     *
     * @param root The Json5Object to serialize
     * @return The serialized Json5Object as a CharSequence
     */
    private CharSequence serialize(Json5Object root) throws IOException {
        // Serialize the Json5Object into a CharSequence
        return Reference.JSON5.serialize(root);
    }

    /**
     * Sets the element at the specified path in the JSON object, with an optional comment.
     *
     * @param root    the root JSON object
     * @param path    the path to the element
     * @param value   the value to set
     * @param comment the comment to associate with the element
     */
    @SuppressWarnings({"ConditionCoveredByFurtherCondition"})
    private void setElement(Json5Object root, String path, Json5Element value, String comment) {
        // Split the path into parts
        String[] parts = path.split("\\.");
        Json5Object current = root;

        // Traverse the path and create any missing objects
        for (int i = 0; i < parts.length - 1; i++) {
            Json5Element tempCurrent = current.get(parts[i]);
            if (tempCurrent == null || !(tempCurrent instanceof Json5Object object)) {
                // Create a new JSON object if the current element is missing
                Json5Object newValue = new Json5Object();
                tempCurrent = newValue;
                current.add(parts[i], tempCurrent);

                current = newValue;
                continue;
            }

            current = object;
        }

        // Set the value and associate the comment
        current.add(parts[parts.length - 1], value);
        current.setComment(parts[parts.length - 1], comment);
    }

    /**
     * Serializes the given value based on the provided type and returns the corresponding Json5Element.
     *
     * @param value The value to be serialized.
     * @param type  The type of the value.
     * @return The serialized Json5Element.
     * @throws IllegalStateException if the type is unsupported.
     */
    private Json5Element serializeValue(Object value, Class<?> type) {
        // Serialize based on the type
        if (type == String.class) {
            return new Json5String((String) value);
        } else if (type == Number.class) {
            return new Json5Number((Number) value);
        } else if (type == Boolean.class) {
            return new Json5Boolean((Boolean) value);
        } else if (type == UUID.class) {
            return new Json5String(value.toString());
        } else if (type == BigInteger.class) {
            return new Json5String(value.toString());
        } else if (type == BigDecimal.class) {
            return new Json5String(value.toString());
        } else if (type == Character.class) {
            return new Json5String(((Character) value).toString());
        } else if (type == Byte.class) {
            return new Json5Number((Byte) value);
        } else if (type == Short.class) {
            return new Json5Number((Short) value);
        } else if (type == Integer.class) {
            return new Json5Number((Integer) value);
        } else if (type == Long.class) {
            return new Json5Number((Long) value);
        } else if (type == Float.class) {
            return new Json5Number((Float) value);
        } else if (type == Double.class) {
            return new Json5Number((Double) value);
        } else if (type == boolean.class) {
            return new Json5Boolean((boolean) value);
        } else if (type == byte.class) {
            return new Json5Number((byte) value);
        } else if (type == short.class) {
            return new Json5Number((short) value);
        } else if (type == int.class) {
            return new Json5Number((int) value);
        } else if (type == long.class) {
            return new Json5Number((long) value);
        } else if (type == float.class) {
            return new Json5Number((float) value);
        } else if (type == double.class) {
            return new Json5Number((double) value);
        } else if (type == ResourceLocation.class) {
            return new Json5String(((ResourceLocation) value).toString());
        } else if (type == Json5Object.class) {
            return (Json5Object) value;
        } else if (type == Json5Array.class) {
            return (Json5Array) value;
        } else if (type == Json5Element.class) {
            return (Json5Element) value;
        } else {
            throw new IllegalStateException("Unsupported type " + type);
        }
    }

    /**
     * Sets default values for different types of fields.
     *
     * @param field the field to set the default value for
     * @param type the type of the field
     * @throws IllegalAccessException if the default value cannot be set
     */
    private void setDefaults(Field field, Class<?> type) throws IllegalAccessException {
        if (type == String.class) {
            field.set(null, "");
        } else if (type == Number.class) {
            field.set(null, 0);
        } else if (type == Boolean.class) {
            field.set(null, false);
        } else if (type == UUID.class) {
            field.set(null, new UUID(0, 0));
        } else if (type == BigInteger.class) {
            field.set(null, BigInteger.ZERO);
        } else if (type == BigDecimal.class) {
            field.set(null, new BigDecimal(0));
        } else if (type == Character.class) {
            field.set(null, (char) 0x20);
        } else if (type == Byte.class) {
            field.set(null, (byte) 0);
        } else if (type == Short.class) {
            field.set(null, (short) 0);
        } else if (type == Integer.class) {
            field.set(null, 0);
        } else if (type == Long.class) {
            field.set(null, 0L);
        } else if (type == Float.class) {
            field.set(null, 0F);
        } else if (type == Double.class) {
            field.set(null, 0D);
        } else if (type == boolean.class) {
            field.set(null, false);
        } else if (type == char.class) {
            field.set(null, (char) 0x20);
        } else if (type == byte.class) {
            field.set(null, (byte) 0);
        } else if (type == short.class) {
            field.set(null, (short) 0);
        } else if (type == int.class) {
            field.set(null, 0);
        } else if (type == long.class) {
            field.set(null, 0L);
        } else if (type == float.class) {
            field.set(null, 0F);
        } else if (type == double.class) {
            field.set(null, 0D);
        } else if (type == Json5Element.class) {
            field.set(null, Json5Null.INSTANCE);
        } else if (type == Json5Object.class) {
            field.set(null, new Json5Object());
        } else if (type == Json5Array.class) {
            field.set(null, new Json5Array());
        } else if (type == Json5String.class) {
            field.set(null, new Json5String(""));
        } else if (type == Json5Number.class) {
            field.set(null, new Json5Number(0));
        } else if (type == Json5Boolean.class) {
            field.set(null, new Json5Boolean(false));
        } else if (type == Json5Hexadecimal.class) {
            field.set(null, new Json5Hexadecimal(BigInteger.ZERO));
        } else {
            throw new IllegalStateException("Default value not available for " + type);
        }
    }

    /**
     * Parses the given Json5Element into the specified type taking into account any range restrictions.
     *
     * @param element the Json5Element to be parsed
     * @param type the target type to parse the element into
     * @param ranged specifies if there are range restrictions for numeric types
     * @return the parsed value of the element into the specified type
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object parseValue(Json5Element element, Class<?> type, Ranged ranged) {
        if (Json5Element.class.isAssignableFrom(type)) {
            return element;
        } else if (type == String.class && element instanceof Json5String) {
            return element.getAsString();
        } else if (type == Number.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged);
            return element.getAsNumber();
        } else if (type == Boolean.class && element instanceof Json5Boolean) {
            return element.getAsBoolean();
        } else if (type == UUID.class && element instanceof Json5String) {
            return UUID.fromString(element.getAsString());
        } else if (type == BigInteger.class && element instanceof Json5Number) {
            return element.getAsBigInteger();
        } else if (type == BigDecimal.class && element instanceof Json5Number) {
            return element.getAsBigDecimal();
        } else if (type == Byte.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).byteValue();
            return element.getAsByte();
        } else if (type == Short.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).shortValue();
            return element.getAsShort();
        } else if (type == Integer.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).intValue();
            return element.getAsInt();
        } else if (type == Long.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).longValue();
            return element.getAsLong();
        } else if (type == Float.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).floatValue();
            return element.getAsFloat();
        } else if (type == Double.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).doubleValue();
            return element.getAsDouble();
        } else if (type == Character.class && element instanceof Json5String) {
            return element.getAsString().charAt(0);
        } else if (type == boolean.class && element instanceof Json5Boolean) {
            return element.getAsBoolean();
        } else if (type == char.class && element instanceof Json5String) {
            return element.getAsString().charAt(0);
        } else if (type == byte.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).byteValue();
            return element.getAsByte();
        } else if (type == short.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).shortValue();
            return element.getAsShort();
        } else if (type == int.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).intValue();
            return element.getAsInt();
        } else if (type == long.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).longValue();
            return element.getAsLong();
        } else if (type == float.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).floatValue();
            return element.getAsFloat();
        } else if (type == double.class && element instanceof Json5Number) {
            if (ranged != null) return parseNumber((Json5Number) element, ranged).doubleValue();
            return element.getAsDouble();
        } else if (type == ResourceLocation.class && element instanceof Json5String) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(element.getAsString());
            if (resourceLocation == null) throw new IllegalArgumentException("Invalid resourceLocation: " + element.getAsString());
            return resourceLocation;
        } else if (type == Enum.class && element instanceof Json5String) {
            return Enum.valueOf((Class<Enum>) type, element.getAsString());
        } else if (type == Json5Element.class) {
            return element;
        } else {
            throw new IllegalStateException("Unsupported type " + type);
        }
    }

    /**
     * Parses a number from a Json5Number object within the specified range.
     *
     * @param element the Json5Number object to parse
     * @param ranged the range within which the parsed number should fall
     * @return the parsed number within the specified range
     */
    private Number parseNumber(Json5Number element, Ranged ranged) {
        if (ranged == null) return element.getAsNumber();
        if (ranged.min() > element.getAsNumber().doubleValue()) return ranged.min();
        if (ranged.max() < element.getAsNumber().doubleValue()) return ranged.max();
        return element.getAsNumber();
    }

    /**
     * Retrieves a nested element in a JSON-like structure based on the provided path.
     *
     * @param root The root Json5Element where the search starts.
     * @param path The path to the desired element separated by dots.
     * @return The element found at the specified path, or null if not found.
     */
    private Json5Element getElement(Json5Element root, String path) {
        // Split the path into individual elements
        String[] pathElements = path.split("\\.");

        // Start traversal from the root
        Json5Element current = root;

        // Navigate through the structure until the second last element
        for (int i = 0; i < pathElements.length - 1; i++) {
            if (current instanceof Json5Object) {
                current = ((Json5Object) current).get(pathElements[i]);
                continue;
            }

            return null;
        }

        // Return the element at the last path element, if it exists
        return current instanceof Json5Object ? ((Json5Object) current).get(pathElements[pathElements.length - 1]) : null;
    }
    /**
     * Process the given field and perform various operations like setting accessibility,
     * updating maps, and setting default values.
     *
     * @param field the field to be processed
     */
    private void processEntry(Field field) {
        // Get the ConfigEntry annotation from the field
        ConfigEntry configEntry = field.getAnnotation(ConfigEntry.class);
        // If the field has the Ranged annotation, update rangesMap
        if (field.isAnnotationPresent(Ranged.class)) {
            Ranged ranged = field.getAnnotation(Ranged.class);
            // Validate the range values
            if (ranged.min() > ranged.max()) {
                throw new IllegalArgumentException("Ranged min cannot be greater than max");
            }
            this.rangesMap.put(configEntry.path(), ranged);
        }
        // Set the field to be accessible
        field.setAccessible(true);

        // Update entriesMap with the configEntry path and the configEntry itself
        entriesMap.put(configEntry.path(), configEntry);
        try {
            // If the field is defaulted or has a non-null value, update defaultsMap with the value
            if (configEntry.defaulted() || field.get(null) != null) {
                Object value = field.get(null);
                if (value != null) defaultsMap.put(configEntry.path(), value);
            } else {
                Class<?> type = field.getType();

                defineDefaults(field, type, configEntry);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        fieldsMap.put(configEntry.path(), field);
        typesMap.put(configEntry.path(), field.getType());
    }

    /**
     * Defines default values for different types of fields.
     *
     * @param  field        the field to define defaults for
     * @param  type         the type of the field
     * @param  configEntry  the configuration entry for the field
     */
    private void defineDefaults(Field field, Class<?> type, ConfigEntry configEntry) {
        if (type == Json5String.class) {
            defaultsMap.put(configEntry.path(), new Json5String(""));
        } else if (type == Json5Number.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0));
        } else if (type == Json5Hexadecimal.class) {
            defaultsMap.put(configEntry.path(), new Json5Hexadecimal(BigInteger.ZERO));
        } else if (type == Json5Boolean.class) {
            defaultsMap.put(configEntry.path(), new Json5Boolean(false));
        } else if (type == Json5Null.class) {
            defaultsMap.put(configEntry.path(), Json5Null.INSTANCE);
        } else if (type == Json5Array.class) {
            defaultsMap.put(configEntry.path(), new Json5Array());
        } else if (type == Json5Object.class) {
            defaultsMap.put(configEntry.path(), new Json5Object());
        } else if (type == String.class) {
            defaultsMap.put(configEntry.path(), new Json5String(""));
        } else if (type == Number.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0));
        } else if (type == Boolean.class) {
            defaultsMap.put(configEntry.path(), new Json5Boolean(false));
        } else if (type == Character.class) {
            defaultsMap.put(configEntry.path(), new Json5String(" "));
        } else if (type == Byte.class) {
            defaultsMap.put(configEntry.path(), new Json5Number((byte) 0));
        } else if (type == Short.class) {
            defaultsMap.put(configEntry.path(), new Json5Number((short) 0));
        } else if (type == Integer.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0));
        } else if (type == Long.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0L));
        } else if (type == Float.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0f));
        } else if (type == Double.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0d));
        } else if (type == boolean.class) {
            defaultsMap.put(configEntry.path(), new Json5Boolean(false));
        } else if (type == char.class) {
            defaultsMap.put(configEntry.path(), new Json5String(" "));
        } else if (type == byte.class) {
            defaultsMap.put(configEntry.path(), new Json5Number((byte) 0));
        } else if (type == short.class) {
            defaultsMap.put(configEntry.path(), new Json5Number((short) 0));
        } else if (type == int.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0));
        } else if (type == long.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0L));
        } else if (type == float.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0f));
        } else if (type == double.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(0d));
        } else if (type == BigInteger.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(BigInteger.ZERO));
        } else if (type == BigDecimal.class) {
            defaultsMap.put(configEntry.path(), new Json5Number(BigDecimal.ZERO));
        } else if (type == Json5Element.class) {
            defaultsMap.put(configEntry.path(), Json5Null.INSTANCE);
        } else if (type == UUID.class) {
            defaultsMap.put(configEntry.path(), new Json5String("00000000-0000-0000-0000-000000000000"));
        } else if (type == Enum.class) {
            throw new IllegalStateException("Enums require a default value to be set (field " + field.getName() + ")");
        } else if (type == ResourceLocation.class) {
            throw new IllegalStateException("ResourceLocations require a default value to be set (field " + field.getName() + ")");
        } else {
            throw new IllegalStateException("Unsupported default type " + type);
        }
    }

    /**
     * Loads the configuration file, handles exceptions, and then saves the configuration.
     */
    public void load() {
        try {
            this.loadUnsafe();
        } catch (NoSuchFileException ignored) {
            // File not found, can be ignored
        } catch (Exception e) {
            // Log error if failed to load the config file
            Devices.LOGGER.error("Failed to load config file", e);
            return;
        }

        this.save();
    }

    /**
     * Retrieves the value of the field at the specified path.
     *
     * @param path The path of the field to retrieve.
     * @return The value of the field.
     * @throws IllegalArgumentException If the field does not exist.
     * @throws RuntimeException If there is an error accessing the field.
     */
    public Object get(String path) {
        // Get the field from the fieldsMap
        Field field = fieldsMap.get(path);

        // Check if the field exists
        if (field == null) {
            throw new IllegalArgumentException("Config entry " + path + " does not exist");
        }

        try {
            // Get the value of the field
            return field.get(this);
        } catch (IllegalAccessException e) {
            // Wrap the exception in a RuntimeException and rethrow it
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the class type of the field at the specified path.
     *
     * @param path the path of the field
     * @return the class type of the field
     * @throws IllegalArgumentException if the field does not exist
     */
    public Class<?> getType(String path) {
        // Get the field at the specified path
        Field field = fieldsMap.get(path);

        // If the field does not exist, throw an exception
        if (field == null) {
            throw new IllegalArgumentException("Config entry " + path + " does not exist");
        }

        // Return the class type of the field
        return field.getType();
    }

    /**
     * Retrieves the default value associated with the provided path.
     *
     * @param path The path to the config entry
     * @return The default value for the config entry
     * @throws IllegalArgumentException if the config entry does not exist
     */
    public Object getDefault(String path) {
        Field field = fieldsMap.get(path);
        if (field == null) {
            throw new IllegalArgumentException("Config entry " + path + " does not exist");
        }

        return defaultsMap.get(path);
    }

    /**
     * Checks if the given path exists in the fields map.
     *
     * @param path The path to check.
     * @return True if the path exists, false otherwise.
     */
    public boolean contains(String path) {
        return fieldsMap.containsKey(path);
    }

    /**
     * Retrieves all fields and their values from the object.
     *
     * @return A map containing field names as keys and their corresponding values
     */
    public Map<String, Object> getAll() {
        // Create a map to store field names and values
        Map<String, Object> map = new HashMap<>();

        // Iterate through all fields in the object
        for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
            try {
                // Put the field name and its value into the map
                map.put(entry.getKey(), entry.getValue().get(this));
            } catch (IllegalAccessException e) {
                // Throw a runtime exception if access to the field is not allowed
                throw new RuntimeException(e);
            }
        }

        // Return the map containing all fields and their values
        return map;
    }

    /**
     * Returns an unmodifiable map of default values.
     *
     * @return unmodifiable map of default values
     */
    public Map<String, Object> getDefaults() {
        return Collections.unmodifiableMap(defaultsMap);
    }

    /**
     * Sets the value of a config entry specified by its path.
     *
     * @param path The path of the config entry.
     * @param value The value to set.
     * @throws IllegalArgumentException If the config entry does not exist or if the value cannot be set.
     */
    public void set(String path, Object value) {
        // Get the field associated with the given path
        Field field = fieldsMap.get(path);

        // If the field does not exist, throw an exception
        if (field == null) {
            throw new IllegalArgumentException("Config entry " + path + " does not exist");
        }

        try {
            // Set the value of the field
            field.set(this, value);
        } catch (IllegalAccessException e) {
            // If an IllegalAccessException is caught, rethrow it as a RuntimeException
            throw new RuntimeException(e);
        } catch (Exception e) {
            // If any other exception is caught, throw an IllegalArgumentException with a specific error message
            throw new IllegalArgumentException("Failed to set config entry " + path, e);
        } finally {
            // Save the config after setting the value
            this.save();
        }
    }

    /**
     * Resets the config entry based on the provided path.
     *
     * @param path The path of the config entry to reset
     */
    public void reset(String path) {
        // Get the field from the fields map based on the path
        Field field = fieldsMap.get(path);

        // If the field is not found, throw an IllegalArgumentException
        if (field == null) {
            throw new IllegalArgumentException("Config entry " + path + " does not exist");
        }

        try {
            // Set the defaults for the field
            this.setDefaults(field, field.getType());
        } catch (IllegalAccessException e) {
            // If there's an IllegalAccessException, throw a RuntimeException
            throw new RuntimeException(e);
        } catch (Exception e) {
            // If there's any other exception, throw an IllegalArgumentException with the original cause
            throw new IllegalArgumentException("Failed to reset config entry " + path, e);
        }
    }

    public String getFileName() {
        return configPath.getFileName().toString();
    }

    public Mod getMod() {
        return Platform.getMod(Devices.MOD_ID);
    }

    public Path getConfigPath() {
        return configPath;
    }

    public Ranged getRange(String key) {
        return rangesMap.get(key);
    }

    /**
     * Event that is called when the config file is loaded or reloaded.
     *
     * @see #event
     */
    @FunctionalInterface
    public interface LoadConfig {
        /**
         * Called when the config file is loaded or reloaded.
         *
         * @throws IOException if an I/O error occurs
         */
        void load() throws IOException;
    }
}