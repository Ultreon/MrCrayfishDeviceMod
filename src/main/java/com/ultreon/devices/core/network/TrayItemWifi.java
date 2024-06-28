package com.ultreon.devices.core.network;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.ItemList;
import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.Device;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.network.task.TaskConnect;
import com.ultreon.devices.core.network.task.TaskPing;
import com.ultreon.devices.object.TrayItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrCrayfish
 */
public class TrayItemWifi extends TrayItem {
    private int pingTimer;

    public TrayItemWifi() {
        super(Icons.WIFI_NONE);
    }

    private static Layout createWifiMenu(TrayItem item) {
        Layout layout = new Layout.Context(100, 100);
        layout.setBackground((pose, gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> IngameGui.fill(pose, x, y, x + width, y + height, new Color(0.65f, 0.65f, 0.65f, 0.9f).getRGB()));

        ItemList<Device> itemListRouters = new ItemList<>(5, 5, 90, 4);
        itemListRouters.setItems(getRouters());
        itemListRouters.setListItemRenderer(new ListItemRenderer<Device>(16) {
            @Override
            public void render(MatrixStack pose, Device device, AbstractGui gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {
                IngameGui.fill(pose, x, y, x + width, y + height, selected ? Color.DARK_GRAY.getRGB() : Color.GRAY.getRGB());
                RenderUtil.drawStringClipped(pose, device.getName(), x + 16, y + 4, 70, Color.WHITE.getRGB(), false);

                if (device.getPos() == null) return;

                BlockPos laptopPos = Laptop.getPos();
                assert laptopPos != null;
                double distance = Math.sqrt(device.getPos().distSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5, false));
                if (distance > 20) {
                    Icons.WIFI_LOW.draw(pose, mc, x + 3, y + 3);
                } else if (distance > 10) {
                    Icons.WIFI_MED.draw(pose, mc, x + 3, y + 3);
                } else {
                    Icons.WIFI_HIGH.draw(pose, mc, x + 3, y + 3);
                }
            }
        });
        itemListRouters.sortBy((o1, o2) -> {
            BlockPos laptopPos = Laptop.getPos();
            assert o1.getPos() != null;
            assert laptopPos != null;
            double distance1 = Math.sqrt(o1.getPos().distSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5, false));
            assert o2.getPos() != null;
            double distance2 = Math.sqrt(o2.getPos().distSqr(laptopPos.getX() + 0.5, laptopPos.getY() + 0.5, laptopPos.getZ() + 0.5, false));
            return Double.compare(distance1, distance2);
        });
        layout.addComponent(itemListRouters);

        Button buttonConnect = new Button(79, 79, Icons.CHECK);
        buttonConnect.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (itemListRouters.getSelectedItem() != null) {
                    TaskConnect connect = new TaskConnect(Laptop.getPos(), itemListRouters.getSelectedItem().getPos());
                    connect.setCallback((tag, success) -> {
                        if (success) {
                            item.setIcon(Icons.WIFI_HIGH);
                            Laptop.getSystem().closeContext();
                        }
                    });
                    TaskManager.sendTask(connect);
                }
            }
        });
        layout.addComponent(buttonConnect);

        return layout;
    }

    private static List<Device> getRouters() {
        List<Device> routers = new ArrayList<>();

        World level = Minecraft.getInstance().level;
        if (Laptop.isWorldLess()) {
            return new ArrayList<>();
        }

        BlockPos laptopPos = Laptop.getPos();
        int range = DeviceConfig.SIGNAL_RANGE.get();

        for (int y = -range; y < range + 1; y++) {
            for (int z = -range; z < range + 1; z++) {
                for (int x = -range; x < range + 1; x++) {
                    assert laptopPos != null;
                    BlockPos pos = new BlockPos(laptopPos.getX() + x, laptopPos.getY() + y, laptopPos.getZ() + z);
                    assert level != null;
                    TileEntity tileEntity = level.getBlockEntity(pos);
                    if (tileEntity instanceof RouterBlockEntity) {
                        routers.add(new Device((DeviceBlockEntity) tileEntity));
                    }
                }
            }
        }
        return routers;
    }

    @Override
    public void init() {
        this.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (Laptop.getSystem().hasContext()) {
                Laptop.getSystem().closeContext();
            } else {
                Laptop.getSystem().openContext(createWifiMenu(this), mouseX - 100, mouseY - 100);
            }
        });

        runPingTask();
    }

    @Override
    public void tick() {
        if (++pingTimer >= DeviceConfig.PING_RATE.get()) {
            runPingTask();
            pingTimer = 0;
        }
    }

    private void runPingTask() {
        TaskPing task = new TaskPing(Laptop.getPos());
        task.setCallback((tag, success) -> {
            if (success) {
                assert tag != null;
                int strength = tag.getInt("strength");
                switch (strength) {
                    case 2:
                        setIcon(Icons.WIFI_LOW);
                        break;
                    case 1:
                        setIcon(Icons.WIFI_MED);
                        break;
                    case 0:
                        setIcon(Icons.WIFI_HIGH);
                        break;
                    default:
                        setIcon(Icons.WIFI_NONE);
                        break;
                }
            } else {
                setIcon(Icons.WIFI_NONE);
            }
        });
        TaskManager.sendTask(task);
    }
}
