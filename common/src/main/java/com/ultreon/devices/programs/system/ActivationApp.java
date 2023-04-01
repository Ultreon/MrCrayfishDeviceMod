package com.ultreon.devices.programs.system;

import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.api.app.component.TextField;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.programs.system.activation.TaskActivateMineOS;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Codename: Apr1l
 */
public class ActivationApp extends SystemApp {
    private static final String VALID_CHARS = "0123456789abcdefABCDEF";
    private TextField part1;
    private TextField part2;
    private TextField part3;
    private TextField part4;
    private TextField part5;

    public ActivationApp() {
        super();
        setDefaultWidth(10 + length(8) + 9 + length(4) + 9 + length(4) + 9 + length(4) + 9 + length(12) + 10);
        setDefaultHeight(68);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        // 00000000-0000-0000-0000-000000000000
        var description = new Label("Enter product key to activate:", 10, 10);
        var sepLen = 10;

        part1 = new TextField(10, 25, length(8));
        part1.setPlaceholder("00000000");
        part1.setFilter(this::isValidChar);
        part1.setMaxLength(8);

        part2 = new TextField(10 + length(8) + sepLen, 25, length(4));
        part2.setPlaceholder("0000");
        part2.setFilter(this::isValidChar);
        part2.setMaxLength(4);

        part3 = new TextField(10 + length(8) + sepLen + length(4) + sepLen, 25, length(4));
        part3.setPlaceholder("0000");
        part3.setFilter(this::isValidChar);
        part3.setMaxLength(4);

        part4 = new TextField(10 + length(8) + sepLen + length(4) + sepLen + length(4) + sepLen, 25, length(4));
        part4.setPlaceholder("0000");
        part4.setFilter(this::isValidChar);
        part4.setMaxLength(4);

        part5 = new TextField(10 + length(8) + sepLen + length(4) + sepLen + length(4) + sepLen + length(4) + sepLen, 25, length(12));
        part5.setPlaceholder("000000000000");
        part5.setFilter(this::isValidChar);
        part5.setMaxLength(12);

        var sep1 = new Label("-", 10 + length(8) + 2, 29);
        var sep2 = new Label("-", 10 + length(8) + sepLen + length(4) + 2, 29);
        var sep3 = new Label("-", 10 + length(8) + sepLen + length(4) + sepLen + length(4) + 2, 29);
        var sep4 = new Label("-", 10 + length(8) + sepLen + length(4) + sepLen + length(4) + sepLen + length(4) + 2, 29);

        var registerBtn = new Button(10 + length(8) + sepLen + length(4) + sepLen + length(4) + sepLen + length(4) + sepLen + length(12) - 70, 45, 70, 18, "Activate");
        registerBtn.setIcon(Icons.KEY);
        registerBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            var license = getLicense();
            if (license == null) {
                openDialog(new Dialog.Message("Invalid license."));
                return;
            }
            var activateTask = new TaskActivateMineOS(license);
            activateTask.setCallback((nbt, success) -> {
                if (success) {
                    getWindow().close();
                } else {
                    openDialog(new Dialog.Message("Incorrect license."));
                }
            });
            TaskManager.sendTask(activateTask);
        });

        super.addComponent(description);
        super.addComponent(part1);
        super.addComponent(part2);
        super.addComponent(part3);
        super.addComponent(part4);
        super.addComponent(part5);
        super.addComponent(sep1);
        super.addComponent(sep2);
        super.addComponent(sep3);
        super.addComponent(sep4);
        super.addComponent(registerBtn);
    }

    private boolean isValidChar(char c) {
        return VALID_CHARS.indexOf(c) != -1;
    }

    private UUID getLicense() {
        try {
            return UUID.fromString(part1.getText() + "-" + part2.getText() + "-" + part3.getText() + "-" + part4.getText() + "-" + part5.getText());
        } catch (Exception e) {
            return null;
        }
    }

    private int length(int i) {
        return 6 * i - 1 + 9;
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
