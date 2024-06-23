package com.ultreon.devices.object;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.ultreon.devices.Devices;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class AppInfo {
    public static final Comparator<AppInfo> SORT_NAME = Comparator.comparing(AppInfo::getName);

    private transient final ResourceLocation APP_ID;

    private final transient boolean systemApp;

    private static final TintProvider DEFAULT_TINT_PROVIDER = (i, o) -> {
        switch (o) {
            case 0:
                return new Color(255, 255, 255).getRGB();
            case 1:
                return Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor();
            case 2:
                return Laptop.getSystem().getSettings().getColorScheme().getBackgroundSecondaryColor();
            default:
                return new Color(255, 255, 255).getRGB();
        }
    };

    private TintProvider tintProvider = DEFAULT_TINT_PROVIDER;

    private String name;
    private String author;
    private String description;
    private String version;
    private Icon icon;
    private String[] screenshots;
    private Support support;

    public AppInfo(ResourceLocation identifier, boolean isSystemApp) {
        this.APP_ID = identifier;
        this.systemApp = isSystemApp;
    }

    /**
     * Gets the id of the application
     *
     * @return the app resource location
     */
    public ResourceLocation getId() {
        return APP_ID;
    }

    /**
     * Gets the formatted version of the application's id
     *
     * @return a formatted id
     */
    public String getFormattedId() {
        return APP_ID.getNamespace() + "." + APP_ID.getPath();
    }

    /**
     * Gets the name of the application
     *
     * @return the application name
     */
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public int getTint(int i) {
        return this.tintProvider != null ? this.tintProvider.getTintColor(this, i) : DEFAULT_TINT_PROVIDER.getTintColor(this, i);
    }

    public static class Icon {
        Glyph base;
        Glyph overlay0;
        Glyph overlay1;
        public static class Glyph {
            private ResourceLocation resourceLocation;
            private int u = -1;
            private int v = -1;
            private int type;
            private Glyph(ResourceLocation res) {
                this.resourceLocation = res;
            }
            private static Glyph of(ResourceLocation res) {
                return new Glyph(res);
            }

            public ResourceLocation getResourceLocation() {
                return resourceLocation;
            }

            public void setU(int u) {
                this.u = u;
            }

            public void setV(int v) {
                this.v = v;
            }

            public int getU() {
                return u;
            }

            public int getV() {
                return v;
            }

            public int getType() {
                return type;
            }
        }

        private Icon(AppInfo info) {
            this.base = Glyph.of(new ResourceLocation(info.APP_ID.getNamespace(), "textures/app/icon/base/" + info.APP_ID.getPath() + ".png"));
            this.base.type = 0;
            this.overlay0 = Glyph.of(new ResourceLocation(info.APP_ID.getNamespace(), "textures/app/icon/overlay0/" + info.APP_ID.getPath() + ".png"));
            this.overlay0.type = 1;
            this.overlay1 = Glyph.of(new ResourceLocation(info.APP_ID.getNamespace(), "textures/app/icon/overlay1/" + info.APP_ID.getPath() + ".png"));
            this.overlay1.type = 2;
        }

        public Glyph getBase() {
            return base;
        }

        public Glyph getOverlay0() {
            return overlay0;
        }

        public Glyph getOverlay1() {
            return overlay1;
        }
    }

    public String[] getScreenshots() {
        return screenshots;
    }

    public Support getSupport() {
        return support;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof AppInfo)) return false;
        AppInfo info = (AppInfo) obj;
        return this == info || getFormattedId().equals(info.getFormattedId());
    }

    public void reload() {
        resetInfo();
        InputStream stream = Devices.class.getResourceAsStream("/assets/" + APP_ID.getNamespace() + "/apps/" + APP_ID.getPath() + ".json");

        if (stream == null)
            throw new RuntimeException("Missing app info json for '" + APP_ID + "'");

        Reader reader = new InputStreamReader(stream);
        JsonElement obj = JsonParser.parseReader(reader);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(AppInfo.class, new Deserializer(this));
        Gson gson = builder.create();
        gson.fromJson(obj, AppInfo.class);
    }

    private void resetInfo() {
        name = null;
        author = null;
        description = null;
        version = null;
        icon = null;
        screenshots = null;
        support = null;
    }

    private static class Support {
        private String paypal;
        private String patreon;
        private String twitter;
        private String youtube;
    }

    public static class Deserializer implements JsonDeserializer<AppInfo> {
        private static final Pattern LANG = Pattern.compile("\\$\\{[a-z]+}");

        private final AppInfo info;

        public Deserializer(AppInfo info) {
            this.info = info;
        }

        @Override
        public AppInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            try {
                info.name = convertToLocal(json.getAsJsonObject().get("name").getAsString());
                info.author = convertToLocal(json.getAsJsonObject().get("author").getAsString());
                info.description = convertToLocal(json.getAsJsonObject().get("description").getAsString());
                info.version = json.getAsJsonObject().get("version").getAsString();
                if (json.getAsJsonObject().has("screenshots") && json.getAsJsonObject().get("screenshots").isJsonArray()) {
                    info.screenshots = context.deserialize(json.getAsJsonObject().get("screenshots"), new TypeToken<String[]>() {
                    }.getType());
                }

                if (json.getAsJsonObject().has("icon") && json.getAsJsonObject().get("icon").isJsonPrimitive()) {
                    Devices.LOGGER.warn("{} uses removed \"icon\"! Please advise {} to fix the icon!", info.name, info.author);
                }

                if (json.getAsJsonObject().has("support") && json.getAsJsonObject().get("support").getAsJsonObject().size() > 0) {
                    JsonObject supportObj = json.getAsJsonObject().get("support").getAsJsonObject();
                    Support support = new Support();

                    if (supportObj.has("paypal")) {
                        support.paypal = supportObj.get("paypal").getAsString();
                    }
                    if (supportObj.has("patreon")) {
                        support.patreon = supportObj.get("patreon").getAsString();
                    }
                    if (supportObj.has("twitter")) {
                        support.twitter = supportObj.get("twitter").getAsString();
                    }
                    if (supportObj.has("youtube")) {
                        support.youtube = supportObj.get("youtube").getAsString();
                    }

                    info.support = support;
                }
                info.icon = new Icon(info);
            } catch (JsonParseException e) {
                Devices.LOGGER.error("Malformed app info json for '" + info.getFormattedId() + "'");
            }

            return info;
        }

        private String convertToLocal(String s) {
            Matcher m = LANG.matcher(s);
            while (m.find()) {
                String found = m.group();
                s = s.replace(found, I18n.get("app." + info.getFormattedId() + "." + found.substring(2, found.length() - 1)));
            }
            return s;
        }
    }

    @ApiStatus.Experimental
    public interface TintProvider {
        int getTintColor(AppInfo info, int o);
    }
}
