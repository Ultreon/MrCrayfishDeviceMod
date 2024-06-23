package com.ultreon.devices.core.client.debug;

/**
 * Adds a button to the title screen to test system applications that don't require the system
 */
public class ClientAppDebug {
    public static void register() {
//        if (DeviceConfig.DEBUG_BUTTON.get()) {
//            ClientGuiEvent.INIT_POST.register(((screen, access) -> {
//                if (!(screen instanceof TitleScreen)) return;
//                int rowHeight = 24;
//                var y = screen.height / 4 + 48;
//
//                Button a = new Button(screen.width / 2 - 100, y + rowHeight * -2, 200, 20, new StringTextComponent("DV TEST"), (button) -> {
//                    Minecraft.getInstance().setScreen(new Laptop(new LaptopBlockEntity(), true));
//                }, Button.NO_TOOLTIP);
//                access.addRenderableWidget(a);
//            }));
//
//
//            ClientGuiEvent.INIT_POST.register(((screen, access) -> {
//                if (!(screen instanceof TitleScreen)) return;
//                int rowHeight = 24;
//                var y = screen.height / 4 + 48;
//
//                Button a = new Button(screen.width / 2 - 100, y + rowHeight * -3, 200, 20, new StringTextComponent("DV TEST #2"), (button) -> {
//                    ServerLaptop serverLaptop = new ServerLaptop();
//                    ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
//                    ClientLaptop clientLaptop = new ClientLaptop();
//                    clientLaptop.setUuid(serverLaptop.getUuid());
//                    ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
//                    Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
//                }, Button.NO_TOOLTIP);
//                access.addRenderableWidget(a);
//            }));
//        }
    }
}
