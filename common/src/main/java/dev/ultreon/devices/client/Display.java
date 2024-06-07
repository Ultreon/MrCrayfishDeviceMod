package dev.ultreon.devices.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.BiosImpl;
import dev.ultreon.devices.mineos.apps.system.DisplayResolution;
import dev.ultreon.devices.mineos.apps.system.PredefinedResolution;
import dev.ultreon.devices.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class Display extends Screen {
    public static final ResourceLocation LAPTOP_GUI = UltreonDevicesMod.res("textures/gui/laptop.png");
    public static final int BORDER = 10;
    private static Display instance;
    private final Window window;
    private ComputerBlockEntity computer = null;

    private GuiGraphics graphics;
    private int mouseX;
    private int mouseY;
    private float partialTicks;
    private int screenWidth;
    private int screenHeight;
    private Consumer<Display> disconnectListener;
    private Consumer<Display> connectListener;
    private boolean connected;
    private BiosImpl bios;
    private OperatingSystem os;

    static {
        LifecycleEvent.SERVER_STOPPING.register(instance1 -> {
            close();
        });
    }

    private Display(DisplayResolution resolution) {
        super(Component.literal("MineOS GuiGraphics"));

        this.screenWidth = resolution.width();
        this.screenHeight = resolution.height();

        this.window = Minecraft.getInstance().getWindow();
    }

    private Display(ComputerBlockEntity computer) {
        this(PredefinedResolution.PREDEFINED_384x216);
        this.computer = computer;
        this.bios = computer.getBios();
        this.os = this.bios.getRunningOS();
    }

    public static Screen open(ComputerBlockEntity computer) {
        instance = new Display(computer);
        instance.os.connectDisplay(instance);
        return instance;
    }

    public static void open(DisplayResolution resolution) {
        instance = new Display(resolution);
    }

    public static Display get() {
        return instance;
    }

    public static void close() {
        if (instance == null) return;

        instance.onClose();
        instance.os.disconnectDisplay();
        instance = null;
    }

    public static boolean isOpen() {
        return instance != null;
    }

    void begin(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.graphics = graphics;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.partialTicks = partialTicks;
    }

    void end() {
        this.graphics = null;
        this.mouseX = 0;
        this.mouseY = 0;
        this.partialTicks = 0;
    }

    public void setResolution(DisplayResolution resolution) {
        this.screenWidth = resolution.width();
        this.screenHeight = resolution.height();
    }

    public boolean isPresent() {
        return true;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public int getMaxWidth() {
        return window.getGuiScaledWidth();
    }

    public int getMaxHeight() {
        return window.getGuiScaledHeight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.begin(graphics, mouseX, mouseY, partialTick);
        this.renderBezels();

        int x = getX();
        int y = getY();
        graphics.pose().pushPose();

        PoseStack.Pose last = graphics.pose().last();

        try {
            if (GLHelper.pushScissor(graphics, x, y, screenWidth, screenHeight)) {
                OperatingSystem runningOS = this.bios.getRunningOS();
                if (mouseX < x || mouseX > x + screenWidth) {
                    mouseX = Integer.MAX_VALUE;
                }
                if (mouseY < y || mouseY > y + screenHeight) {
                    mouseY = Integer.MAX_VALUE;
                }

                var posX = mouseX - getX();
                var posY = mouseY - getY();

                if (runningOS != null) {
                    graphics.pose().translate(x, y, 0);
                    runningOS.getScreen().render(graphics, posX, posY, partialTick);
                }
                GLHelper.popScissor();
            }
        } catch (Exception e) {
            while (graphics.pose().last() != last) {
                graphics.pose().popPose();
            }

            GLHelper.clearScissorStack();
            RenderSystem.disableScissor();

            bios.onFault(e);

            return;
        }

        if (graphics.pose().last() != last) {
            UltreonDevicesMod.LOGGER.error("Pose stack leakage!");

            do {
                graphics.pose().popPose();
            } while (graphics.pose().last() != last);

            bios.onFault(new IllegalStateException("Pose stack leakage!"));
        }

        if (GLHelper.clearScissorStack()) {
            UltreonDevicesMod.LOGGER.error("Scissor stack leakage!");
            bios.onFault(new IllegalStateException("Scissor stack leakage!"));
        }
        RenderSystem.disableScissor();

        graphics.pose().popPose();
        this.end();
    }

    @Override
    public void tick() {
        super.tick();

        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().tick();
            }
        } catch (Exception e) {
            bios.onFault(e);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var posX = mouseX - getX();
        var posY = mouseY - getY();

        if (posX < 0 || posY < 0 || posX >= screenWidth || posY >= screenHeight)
            return false;

        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().mouseClicked(posX, posY, button);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        var posX = mouseX - getX();
        var posY = mouseY - getY();

        if (posX < 0 || posY < 0 || posX >= screenWidth || posY >= screenHeight)
            return false;

        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().mouseReleased(mouseX, mouseY, button);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        var posX = mouseX - getX();
        var posY = mouseY - getY();

        if (posX < 0 || posY < 0 || posX >= screenWidth || posY >= screenHeight)
            return false;

        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().mouseDragged(posX, posY, button, dragX, dragY);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        var posX = mouseX - getX();
        var posY = mouseY - getY();

        if (posX < 0 || posY < 0 || posX >= screenWidth || posY >= screenHeight)
            return false;

        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().mouseScrolled(mouseX, mouseY, deltaX, deltaY);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE) {
            close();
            return true;
        }

        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().keyPressed(keyCode, scanCode, modifiers);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().keyReleased(keyCode, scanCode, modifiers);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().charTyped(codePoint, modifiers);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }

        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().mouseMoved(mouseX, mouseY);
            }
        } catch (Exception e) {
            bios.onFault(e);
        }
    }

    @Override
    public void afterMouseAction() {
        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().afterMouseAction();
            }
        } catch (Exception e) {
            bios.onFault(e);
        }
    }

    @Override
    public void afterKeyboardAction() {
        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().afterKeyboardAction();
            }
        } catch (Exception e) {
            bios.onFault(e);
        }
    }

    @Override
    public void afterMouseMove() {
        try {
            OperatingSystem os = this.bios.getRunningOS();
            if (os != null) {
                os.getScreen().afterMouseMove();
            }
        } catch (Exception e) {
            bios.onFault(e);
        }
    }

    private int getX() {
        return this.width / 2 - screenWidth / 2;
    }

    private int getY() {
        return this.height / 2 - screenHeight / 2;
    }

    protected void revalidateDisplay() {
        this.disconnect();
        this.connect(os);
    }

    private void disconnect() {
        os.disconnectDisplay();

        this.disconnectListener.accept(this);
        this.connected = false;
    }

    private void connect(OperatingSystem os) {
        this.connectListener.accept(this);
        this.connected = true;
        os.connectDisplay(this);
    }

    public void renderBezels() {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int deviceWidth = this.screenWidth + BORDER * 2;
        int deviceHeight = this.screenHeight + BORDER * 2;
        int posX = (this.width - deviceWidth) / 2;
        int posY = (this.height - deviceHeight) / 2;

        // Corners
        this.graphics.blit(LAPTOP_GUI, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        this.graphics.blit(LAPTOP_GUI, posX + deviceWidth - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        this.graphics.blit(LAPTOP_GUI, posX + deviceWidth - BORDER, posY + deviceHeight - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        this.graphics.blit(LAPTOP_GUI, posX, posY + deviceHeight - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        // Edges
        this.graphics.blit(LAPTOP_GUI, posX + BORDER, posY, this.screenWidth, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        this.graphics.blit(LAPTOP_GUI, posX + deviceWidth - BORDER, posY + BORDER, BORDER, this.screenHeight, 11, 10, BORDER, 1, 256, 256); // RIGHT
        this.graphics.blit(LAPTOP_GUI, posX + BORDER, posY + deviceHeight - BORDER, this.screenWidth, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        this.graphics.blit(LAPTOP_GUI, posX, posY + BORDER, BORDER, this.screenHeight, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        this.graphics.blit(LAPTOP_GUI, posX + BORDER, posY + BORDER, this.screenWidth, this.screenHeight, 10, 10, 1, 1, 256, 256);
    }

    public void setDisconnectListener(Consumer<Display> disconnectListener) {
        this.disconnectListener = disconnectListener;
    }

    public void setConnectListener(Consumer<Display> connectListener) {
        this.connectListener = connectListener;
    }

    public ComputerBlockEntity getComputer() {
        return computer;
    }

    public PoseStack getPose() {
        return graphics.pose();
    }

    public PoseStack pose() {
        return graphics.pose();
    }

    public OperatingSystem getOS() {
        return os;
    }
}
