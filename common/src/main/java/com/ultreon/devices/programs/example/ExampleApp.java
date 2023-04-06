package com.ultreon.devices.programs.example;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.component.*;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.programs.example.task.TaskNotificationTest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

@SuppressWarnings({"FieldCanBeLocal", "SpellCheckingInspection"})
public class ExampleApp extends Application {
    @Environment(EnvType.CLIENT)
    private Label label;
    @Environment(EnvType.CLIENT)
    private Button button;
    @Environment(EnvType.CLIENT)
    private Button leftButton;
    @Environment(EnvType.CLIENT)
    private Button upButton;
    @Environment(EnvType.CLIENT)
    private Button rightButton;
    @Environment(EnvType.CLIENT)
    private Button downButton;
    @Environment(EnvType.CLIENT)
    private ItemList<String> itemList;
    @Environment(EnvType.CLIENT)
    private CheckBox checkBoxOn;
    @Environment(EnvType.CLIENT)
    private CheckBox checkBoxOff;
    @Environment(EnvType.CLIENT)
    private ProgressBar progressBar;
    @Environment(EnvType.CLIENT)
    private Slider slider;
    @Environment(EnvType.CLIENT)
    private Spinner spinner;
    @Environment(EnvType.CLIENT)
    private TextField textField;
    @Environment(EnvType.CLIENT)
    private TextArea textArea;
    @Environment(EnvType.CLIENT)
    private Text text;
    @Environment(EnvType.CLIENT)
    private Image image;
    @Environment(EnvType.CLIENT)

    public ExampleApp() {
        //super("example", "UI Components");
        this.setDefaultWidth(270);
        this.setDefaultHeight(140);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void init(@Nullable CompoundTag intent) {
        label = new Label("Label", 5, 5);
        super.addComponent(label);

        button = new Button(5, 18, "Button");
        button.setSize(63, 20);
        button.setClickListener((mouseX, mouseY, mouseButton) -> itemList.addItem("Henlo"));
        super.addComponent(button);

        leftButton = new Button(5, 43, Icons.CHEVRON_LEFT);
        leftButton.setPadding(1);
        leftButton.setClickListener((mouseX, mouseY, mouseButton) -> itemList.removeItem(itemList.getSelectedIndex()));
        super.addComponent(leftButton);

        upButton = new Button(22, 43, Icons.CHEVRON_UP);
        upButton.setPadding(1);
        super.addComponent(upButton);

        rightButton = new Button(39, 43, Icons.CHEVRON_RIGHT);
        rightButton.setPadding(1);
        super.addComponent(rightButton);

        downButton = new Button(56, 43, Icons.CHEVRON_DOWN);
        downButton.setPadding(1);
        downButton.setClickListener((mouseX, mouseY, mouseButton) ->
                TaskManager.sendTask(new TaskNotificationTest()));
        super.addComponent(downButton);

        itemList = new ItemList<>(5, 60, 63, 4);
        itemList.addItem("Item #1");
        itemList.addItem("Item #2");
        itemList.addItem("Item #3");
        super.addComponent(itemList);

        //RadioGroup group = new RadioGroup();
        checkBoxOff = new CheckBox("Off", 5, 122);
        //checkBoxOff.setRadioGroup(group);
        super.addComponent(checkBoxOff);

        checkBoxOn = new CheckBox("On", 42, 122);
        checkBoxOn.setSelected(true);
        //checkBoxOn.setRadioGroup(group);
        super.addComponent(checkBoxOn);

        textField = new TextField(88, 5, 80);
        textField.setPlaceholder("Text Field");
        textField.setIcon(Icons.USER);
        super.addComponent(textField);

        textArea = new TextArea(88, 25, 80, 60);
        textArea.setPlaceholder("Text Area");
        super.addComponent(textArea);

        progressBar = new ProgressBar(88, 90, 80, 16);
        progressBar.setProgress(75);
        super.addComponent(progressBar);

        slider = new Slider(88, 111, 80);
        slider.setSlideListener(percentage -> progressBar.setProgress((int) (100 * percentage)));
        super.addComponent(slider);

        spinner = new Spinner(56, 3);
        super.addComponent(spinner);

        text = new Text("", 180, 5, 90);
        text.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        super.addComponent(text);

        image = new Image(180, 100, 85, 35, "https://minecraft.net/static/pages/img/minecraft-hero-og.c5517b7973e1.jpg");
        image.setAlpha(0.8F);
        super.addComponent(image);
    }

    @Override
    public void load(CompoundTag tagCompound) {

    }

    @Override
    public void save(CompoundTag tagCompound) {

    }

}
