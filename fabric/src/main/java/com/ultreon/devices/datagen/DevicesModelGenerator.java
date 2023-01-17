package com.ultreon.devices.datagen;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class DevicesModelGenerator extends FabricModelProvider {

    public DevicesModelGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        DeviceBlocks.LAPTOPS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_closed"), () -> new Gson().fromJson(String.format(laptopClosedPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_full"), () -> new Gson().fromJson(String.format(laptopFullPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_flitem"), () -> new Gson().fromJson(String.format(laptopFullItemPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(laptopStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get(), "_flitem"));
        });

        DeviceBlocks.OFFICE_CHAIRS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_legs"), () -> new Gson().fromJson(String.format(officeChairFullLegsPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_full"), () -> new Gson().fromJson(String.format(officeChairFullItemPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get(), "_seat"), () -> new Gson().fromJson(String.format(officeChairFullSeatPain(), dye.getName()), JsonElement.class));
            //blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(officeChairStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get(), "_full"));
        });

        DeviceBlocks.ROUTERS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(routerPain(), dye.getName()), JsonElement.class));
            //blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(routerStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get()));
        });

        DeviceBlocks.PRINTERS.getMap().forEach((dye, block) -> {
            blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(printerPain(), dye.getName()), JsonElement.class));
            //blockStateModelGenerator.modelOutput.accept(ModelLocationUtils.getModelLocation(block.get()), () -> new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class));
            blockStateModelGenerator.blockStateOutput.accept(new BlockStateGenerator() {
                @Override
                public Block getBlock() {
                    return block.get();
                }

                @Override
                public JsonElement get() {
                    return new Gson().fromJson(String.format(printerStatePain(), dye.getName()), JsonElement.class);
                }
            });
            blockStateModelGenerator.delegateItemModel(block.get(), ModelLocationUtils.getModelLocation(block.get()));
        });

        //new BlockStateGenerator() {
        //                @Override
        //                public Block getBlock() {
        //                    return block.get();
        //                }
        //
        //                @Override
        //                public JsonElement get() {
        //                    return new Gson().fromJson(String.format(laptopPain(), dye.getName()), JsonElement.class);
        //                }
        //            }
    }

    private String printerPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("printer/printerpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String printerStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("printer/printerstatepain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String routerPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("router/routerpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String routerStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("router/routerstatepain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptop/laptoppain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopClosedPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptop/laptopclosedpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptop/laptopstatepain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopFullPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptop/laptopfullpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String laptopFullItemPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("laptop/laptopfullitempain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String officeChairFullItemPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("office_chair/officechairfullpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String officeChairFullLegsPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("office_chair/officechairlegspain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String officeChairFullSeatPain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("office_chair/officechairseatpain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String officeChairStatePain() {
        try {
            FileInputStream d = new FileInputStream(dataGenerator.getModContainer().findPath("office_chair/officechairstatepain.txt").get().toFile());
            return IOUtils.toString(d, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
    }
}
