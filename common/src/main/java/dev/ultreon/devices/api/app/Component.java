package dev.ultreon.devices.api.app;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.mineos.client.MineOS;
import dev.ultreon.devices.mineos.apps.system.object.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Component {
    /**
     * The default components textures
     */
    public static final ResourceLocation COMPONENTS_GUI = new ResourceLocation("devices:textures/gui/components.png");
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    /**
     * The raw x position of the component. This is not relative to the application.
     */
    public int xPosition;
    /**
     * The raw y position of the component.  This is not relative to the application.
     */
    public int yPosition;
    /**
     * The relative x position from the left.
     */
    public int left;
    /**
     * The relative y position from the top.
     */
    public int top;
    /**
     * Is the component enabled?
     */
    protected boolean enabled = true;
    /**
     * Is the component visible?
     */
    protected boolean visible = true;

    /**
     * The default constructor for a component.
     * <p>
     * Laying out components is simply relative positioning.
     * So for "left" (x position),
     * specifically how many pixels from the left of the application window you want it to be positioned at.
     * The top is the same, but instead of the top (y position).
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Component(int left, int top) {
        super();
        this.left = left;
        this.top = top;
    }

    public Component(CompoundTag tag) {
        this.xPosition = tag.getInt("x");
        this.yPosition = tag.getInt("y");
        this.left = tag.getInt("left");
        this.top = tag.getInt("top");
        this.visible = tag.getBoolean("visible");
        this.enabled = tag.getBoolean("enabled");
    }

    protected static int color(int personalColor, int systemColor) {
        return personalColor >= 0 ? personalColor : systemColor;
    }

    protected static ListTag writeComponents(List<Component> components) {
        ListTag tag = new ListTag();
        for (Component component : components) {
            tag.add(component.writeState());
        }
        return tag;
    }

    protected static List<Component> readComponents(ListTag components) {
        List<Component> list = new java.util.ArrayList<>();
        for (Tag tag : components) {
            if (!(tag instanceof CompoundTag compoundTag)) continue;
            Component e = Component.readState(compoundTag);
            if (e == null) continue; // Failed to load
            list.add(e);
        }

        return list;
    }

    private static @Nullable Component readState(CompoundTag tag) {
        String className = tag.getString("type");

        // Load the class in an uninitialized state
        Class<?> uninitClass;
        try {
            uninitClass = Class.forName(className, false, MineOS.getOpened().getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            UltreonDevicesMod.LOGGER.warn("Failed to load component: class {} doesn't exist.", className, e);
            return null;
        }

        // Check if the class extends Component
        Class<?> superClass = uninitClass.getSuperclass();
        while (superClass != null) {
            if (superClass == Component.class) break;
            superClass = superClass.getSuperclass();
        }
        if (superClass == null) {
            UltreonDevicesMod.LOGGER.warn("Class security check failed: class {} does not extend Component, ignoring class.", className);
            return null;
        }
        try {
            return (Component) uninitClass.getConstructor(CompoundTag.class).newInstance(tag);
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.warn("Failed to load component: class {} failed to initialize.", className, e);
            return null;
        }
    }

    /**
     * Called when this component is added to a Layout. You can add
     * subcomponents through this method. Use {@link Layout#addComponent(Component)}
     *
     * @param layout the layout this component is added to
     */
    protected void init(Layout layout) {
    }

    /**
     * Called when the Layout, this component is bound to is set as the current layout in an
     * application.
     */
    protected void handleLoad() {
    }

    /**
     * TODO: finish docs
     */
    protected void handleUnload() {
    }

    /**
     * Called when the game ticks
     */
    protected void handleTick() {
    }

    /**
     * The main render loop. This is where you draw your component.
     *
     * @param graphics     gui graphics helper
     * @param laptop       a MineOS instance
     * @param mc           a Minecraft instance
     * @param mouseX       the current x position of the mouse
     * @param mouseY       the current y position of the mouse
     * @param windowActive if the window is active (at front)
     * @param partialTicks percentage passed in-between two ticks
     */
    protected void render(GuiGraphics graphics, MineOS laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
    }

    /**
     * The overlay render loop. Renders over the top of the main render
     * loop.
     *
     * @param graphics     gui graphics helper
     * @param laptop       a MineOS instance
     * @param mc           a Minecraft instance
     * @param mouseX       the current x position of the mouse
     * @param mouseY       the current y position of the mouse
     * @param windowActive if the window is active (at front)
     */
    protected void renderOverlay(GuiGraphics graphics, MineOS laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
    }

    /**
     * Called when you mouse button has been pressed. You have to do
     * your own checking to test if it was within the component's
     * bounds.
     *
     * @param mouseX      the current x position of the mouse
     * @param mouseY      the current y position of the mouse
     * @param mouseButton the clicked mouse button
     */
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {

    }

    /**
     * Called when a key is typed from your keyboard.
     *
     * @param character the typed character
     * @param code      the typed character code
     */
    @Deprecated
    protected void handleKeyTyped(char character, int code) {

    }

    /**
     * Called when a key is released from your keyboard.
     *
     * @param character the released character
     * @param code      the released character code
     */
    @Deprecated
    protected void handleKeyReleased(char character, int code) {

    }

    /**
     * Called when a character is typed from your keyboard.
     *
     * @param codePoint the typed character
     * @param modifiers the typed character modifiers
     */
    public void handleCharTyped(char codePoint, int modifiers) {

    }

    /**
     * Called when a key is pressed from your keyboard.
     *
     * @param keyCode   the pressed key code
     * @param scanCode  the pressed key scan code
     * @param modifiers the pressed key modifiers
     */
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {

    }

    /**
     * Called when a key is released from your keyboard.
     *
     * @param keyCode   the released key code
     * @param scanCode  the released key scan code
     * @param modifiers the released key modifiers
     */
    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {

    }

    /**
     * Called when you drag the mouse with a button pressed down.
     *
     * @param mouseX      the current x position of the mouse
     * @param mouseY      the current y position of the mouse
     * @param mouseButton the pressed mouse button
     */
    protected void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {

    }

    /**
     * Called when you release the currently pressed mouse button. You have to do
     * your own checking to test if it was within the component's
     * bounds.
     *
     * @param mouseX      the x position of the release
     * @param mouseY      the y position of the release
     * @param mouseButton the button that was released
     */
    protected void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    //TODO document this plz
    protected void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
    }

    /**
     * This method should be ignored. Used for the core.
     * It will probably be removed in the future.
     */
    @ApiStatus.Internal
    protected void updateComponents(int x, int y) {
        this.xPosition = x + left;
        this.yPosition = y + top;
    }

    /**
     * Sets whether this component is enabled. You should respect
     * this value if you create your own custom components.
     *
     * @param enabled if this component should be enabled or not
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets whether this component is visible. You should respect
     * this value if you create your own custom components.
     *
     * @param visible if this component should be visible or not
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the laptop's Color scheme. A simple helper method to clean up code.
     *
     * @return the color scheme
     */
    protected ColorScheme getColorScheme() {
        return MineOS.getOpened().getSettings().getColorScheme();
    }

    public void drawVerticalLine(GuiGraphics graphics, int x, int y1, int y2, int rgb) {
        graphics.fill(x, y1, x + 1, y2, rgb);
    }

    public void drawHorizontalLine(GuiGraphics graphics, int x1, int x2, int y, int rgb) {
        graphics.fill(x1, y, x2, y + 1, rgb);
    }

    public CompoundTag writeState() {
        CompoundTag tag = new CompoundTag();
        tag.putString("type", getClass().getName());
        tag.putInt("x", xPosition);
        tag.putInt("y", yPosition);
        tag.putInt("left", left);
        tag.putInt("top", top);
        tag.putBoolean("visible", visible);
        tag.putBoolean("enabled", enabled);
        return tag;
    }
}
