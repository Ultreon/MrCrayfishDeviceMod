package dev.ultreon.devices.core.client.debug;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.core.WorldLessBiosImpl;
import dev.ultreon.devices.core.laptop.client.ClientLaptop;
import dev.ultreon.devices.core.laptop.client.ClientLaptopScreen;
import dev.ultreon.devices.core.laptop.server.ServerLaptop;
import dev.ultreon.devices.init.DeviceBlocks;
import dev.ultreon.devices.mineos.client.MineOS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

import static dev.ultreon.devices.OperatingSystems.MINE_OS;

/**
 * Adds a button to the title screen to test system applications that don't require the system
 */
public class ClientAppDebug {
    public static void register() {
        ClientGuiEvent.INIT_POST.register(((screen, access) -> {
            if (DeviceConfig.DEBUG_BUTTON.get()) {
                if (!(screen instanceof TitleScreen)) return;
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = Button.builder(Component.literal("DV TEST"), (button) -> Minecraft.getInstance().setScreen(new MineOS(new LaptopBlockEntity(new BlockPos(0, 0, 0), DeviceBlocks.LAPTOPS.of(DyeColor.WHITE).get().defaultBlockState()), new WorldLessBiosImpl(MINE_OS.get()), true))).bounds(screen.width / 2 - 100, y + rowHeight * -1, 200, 20)
                        .createNarration((output) -> Component.empty())
                        .build();
                access.addRenderableWidget(a);
            }
        }));

        ClientGuiEvent.INIT_POST.register(((screen, access) -> {
            if (DeviceConfig.DEBUG_BUTTON.get()) {
                if (!(screen instanceof TitleScreen)) return;
                var rowHeight = 24;
                var y = screen.height / 4 + 48;

                var a = Button.builder(Component.literal("DV TEST #2"), (button) -> {
                    var serverLaptop = new ServerLaptop();
                    ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
                    var clientLaptop = new ClientLaptop();
                    clientLaptop.setUuid(serverLaptop.getUuid());
                    ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
                    Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
                }).bounds(screen.width / 2 - 100, y + rowHeight * -2, 200, 20)
                        .createNarration((output) -> Component.empty())
                        .build();
                access.addRenderableWidget(a);
            }
        }));
    }
}

