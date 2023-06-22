package com.ultreon.devices.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ultreon.devices.init.DeviceItems;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.locale.Language;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DevicesLanguageGenerator extends FabricLanguageProvider {
    private final String languageCode;
    protected DevicesLanguageGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
        this.languageCode = "en_us";
        l = new LanguageManager(this.languageCode);
    }

    protected DevicesLanguageGenerator(FabricDataGenerator dataGenerator, String languageCode) {
        super(dataGenerator, languageCode);
        this.languageCode = languageCode;
        l = new LanguageManager(this.languageCode);
    }
    private final LanguageManager l;

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        if (this.languageCode.equals("en_pt")) {
            createTranslationsForPirateSpeak(translationBuilder);
        } else if (this.languageCode.startsWith("en_")) { // engurishu
            createTranslationsForEnglish(translationBuilder);
        } else if (this.languageCode.startsWith("nl_")) { // dutch
            //createTranslationsForDutch(translationBuilder);
        } else if (this.languageCode.equals("lol_us")) { // lolcat
            createTranslationsForLOLCAT(translationBuilder);
        } else if (this.languageCode.startsWith("ja_")) {
            createTranslationsForJapanese(translationBuilder);
        }
    }

    private void createTranslationsForPirateSpeak(TranslationBuilder translationBuilder) {
        createTranslationsFromTemplate(translationBuilder, "en_pt");
    }

    private void createTranslationsForJapanese(TranslationBuilder translationBuilder) {
        createTranslationsFromTemplate(translationBuilder, "ja");
    }

    private JsonObject getJSON(Path path) {
        try {
            FileInputStream d = new FileInputStream(path.toFile());
            var json = new Gson().fromJson(IOUtils.toString(d, StandardCharsets.UTF_8), JsonObject.class);
            if (!path.endsWith("en.json")) {
                var eng = getJSON(dataGenerator.getModContainer().findPath("translations/en.json").get());
                for (String s : eng.keySet()) {
                    if (!json.has(s)) {
                        eng.add(s, eng.get(s));
                    }
                }
            }
            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createTranslationsFromTemplate(TranslationBuilder translationBuilder, String file) {
        @NotNull
        var pathode = getJSON(dataGenerator.getModContainer().findPath("translations/" + file + ".json").get());

        DeviceItems.LAPTOPS.getMap().forEach((dye, item) -> {
            var laptop = pathode.get("laptop").getAsString();
            var laptop_block = String.format(pathode.get("laptop_block").getAsString(), laptop,get(dye));
            translationBuilder.add(item.get().getDescriptionId(), laptop_block);
        });

        DeviceItems.PRINTERS.getMap().forEach((dye, item) -> {
            var printer = pathode.get("printer").getAsString();
            var printer_block = String.format(pathode.get("printer_block").getAsString(), printer,get(dye));
            translationBuilder.add(item.get().getDescriptionId(), printer_block);
        });

        DeviceItems.FLASH_DRIVE.getMap().forEach((dye, item) -> {
            var flash_drive = pathode.get("flash_drive").getAsString();
            var flash_drive_item = String.format(pathode.get("flash_drive_item").getAsString(), flash_drive,get(dye));
            translationBuilder.add(item.get().getDescriptionId(), flash_drive_item);
        });

        DeviceItems.ROUTERS.getMap().forEach((dye, item) -> {
            var router = pathode.get("router").getAsString();
            var router_block = String.format(pathode.get("router_block").getAsString(), router, get(dye));
            translationBuilder.add(item.get().getDescriptionId(), router_block);
        });

        DeviceItems.OFFICE_CHAIRS.getMap().forEach((dye, item) -> {
            var office_chair = pathode.get("office_chair").getAsString();
            var office_chair_block = String.format(pathode.get("office_chair_block").getAsString(), office_chair, get(dye));
            translationBuilder.add(item.get().getDescriptionId(), office_chair_block);
        });
    }

    private void createTranslationsForLOLCAT(TranslationBuilder translationBuilder) {
        createTranslationsFromTemplate(translationBuilder, "lol");
    }

    private void createTranslationsForDutch(TranslationBuilder translationBuilder) { // TODO: @Qboi123
        createTranslationsFromTemplate(translationBuilder, "nl_nl");
    }

    private void createTranslationsForEnglish(TranslationBuilder translationBuilder) {
        //System.out.println(dataGenerator.getModContainer().getRootPaths());
        try {
            translationBuilder.add(dataGenerator.getModContainer().findPath("en_us_existing.json").orElseThrow());
        } catch (Exception e) {e.printStackTrace();}
        createTranslationsFromTemplate(translationBuilder, "en");
    }
    
    private String get(DyeColor dye) {
        if(differentLanguageCode()) {
            return grabIt("color.minecraft." + dye.getName());
        }
        return I18n.get("color.minecraft." + dye.getName());
    }

    private String grabIt(String key) {
        try {
            String string = String.format("/assets/minecraft/lang/%s.json", this.languageCode);
            var d = Paths.get("lang/" + this.languageCode + ".json").toFile();
            var q = new BufferedReader(new FileReader(d));
            var gg = new Gson().fromJson(q, JsonObject.class);
            return gg.get(key).getAsString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean differentLanguageCode() {
        return !this.languageCode.startsWith("en_") && !this.languageCode.equals("en_pt");
    }
}
