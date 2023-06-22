package com.jab125.clientint;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jab125.clientint.mixin.LanguageManagerAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

//import static net.minecraft.client.resources.language.LanguageManager.DEFAULT_LANGUAGE;
import static net.minecraft.client.resources.language.LanguageManager.DEFAULT_LANGUAGE_CODE;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var qq = new IdentifiableResourceReloadListener() {
            Map<String, LanguageInfo> languages;
            private String currentCode = "en_us";
            private LanguageInfo currentLanguage;
            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return CompletableFuture.runAsync(() -> {
                    this.languages = extractLanguages(resourceManager.listPacks());
                   // extractAllFrom(languages, resourceManager.listPacks(), resourceManager);
//                    LanguageInfo languageInfo = this.languages.getOrDefault(DEFAULT_LANGUAGE_CODE, LanguageManagerAccessor.getDEFAULT_LANGUAGE());
//                    this.currentLanguage = this.languages.getOrDefault(this.currentCode, languageInfo);
//                    ArrayList<LanguageInfo> list = Lists.newArrayList(languageInfo);
//                    if (this.currentLanguage != languageInfo) {
//                        list.add(this.currentLanguage);
//                    }
                    //this.languages.forEach(((s, languageInfo1) -> {
                        loadFrom(resourceManager, this.languages.values());
                        System.out.println("loaded");
                  //  }));
                }).thenCompose(preparationBarrier::wait);
            }

            @Override
            public ResourceLocation getFabricId() {
                return null;
            }
        };
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(qq);
    }

    private void extractAllFrom(Map<String, LanguageInfo> languages, Stream<PackResources> packResourcesStream, ResourceManager resourceManager) {
        languages.forEach(((s, languageInfo) -> {
            System.out.println(s + ", " + languageInfo);
        }));
        var map = new HashMap<String, LanguageInfo>();
        packResourcesStream.forEach((packResources -> {
                    try {
                        LanguageMetadataSection languageMetadataSection = (LanguageMetadataSection) packResources.getMetadataSection(LanguageMetadataSection.SERIALIZER);
                        if (languageMetadataSection != null) {

                            for (LanguageInfo languageInfo : languageMetadataSection.getLanguages()) {
                                map.putIfAbsent(languageInfo.getCode(), languageInfo);
                            }

                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
       // System.exit(0);
    }

    public static void loadFrom(ResourceManager resourceManager, Collection<LanguageInfo> languageInfo) {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<String, String> map = Maps.newHashMap();
        boolean bl = false;
        for (LanguageInfo languageInfo2 : languageInfo) {
            bl |= languageInfo2.isBidirectional();
            String string = languageInfo2.getCode();
            String string2 = String.format(Locale.ROOT, "lang/%s.json", string);
            JsonObject gg = new JsonObject();
            for (String string3 : resourceManager.getNamespaces()) {
                try {
                    ResourceLocation resourceLocation = new ResourceLocation(string3, string2);
                    //ClientLanguage.appendFrom(string, resourceManager.getResourceStack(resourceLocation), map);
                    var q = resourceManager.getResourceStack(resourceLocation);
                    for (Resource resource : q) {
                        var fgh = new BufferedReader(new InputStreamReader(resource.open()));
                        var sf = gson.fromJson(fgh, JsonObject.class);
                        for (String s : sf.keySet()) {
                            gg.add(s, sf.get(s));
                        }
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            try {
                Paths.get(string2).toFile().createNewFile();
                FileWriter df;
                (df = new FileWriter(Paths.get(string2).toFile())).write(gson.toJson(gg));
                df.close();
                System.out.println("written to" + Paths.get(string2).toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      //  return new ClientLanguage(ImmutableMap.copyOf(map), bl);
    }

    private static Map<String, LanguageInfo> extractLanguages(Stream<PackResources> packResources) {
        Map<String, LanguageInfo> map = Maps.newHashMap();
        packResources.forEach((packResourcesx) -> {
            try {
                LanguageMetadataSection languageMetadataSection = (LanguageMetadataSection)packResourcesx.getMetadataSection(LanguageMetadataSection.SERIALIZER);
                if (languageMetadataSection != null) {

                    for (LanguageInfo languageInfo : languageMetadataSection.getLanguages()) {
                        map.putIfAbsent(languageInfo.getCode(), languageInfo);
                    }
                }
            } catch (IOException | RuntimeException var5) {
              //  System.out.println("Unable to parse language metadata section of resourcepack: {}", packResourcesx.getName(), var5);
            }

        });
        return ImmutableMap.copyOf(map);
    }
}
