package com.ultreon.devices.tests;

import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.core.laptop.client.ClientLaptopScreen;
import com.ultreon.devices.core.laptop.server.ServerLaptop;
import com.ultreon.mods.lib.client.gui.screen.test.TestLaunchContext;
import com.ultreon.mods.lib.client.gui.screen.test.TestScreen;
import com.ultreon.mods.lib.client.gui.screen.test.TestScreenInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@TestScreenInfo("DV TEST #2")
public class DvTest2 extends Screen implements TestScreen {
    public DvTest2() {
        super(TestLaunchContext.get().title);
    }

    @Override
    protected void init() {
        var serverLaptop = new ServerLaptop();
        ServerLaptop.laptops.put(serverLaptop.getUuid(), serverLaptop);
        var clientLaptop = new ClientLaptop();
        clientLaptop.setUuid(serverLaptop.getUuid());
        ClientLaptop.laptops.put(clientLaptop.getUuid(), clientLaptop);
        Minecraft.getInstance().setScreen(new ClientLaptopScreen(clientLaptop));
    }
}
