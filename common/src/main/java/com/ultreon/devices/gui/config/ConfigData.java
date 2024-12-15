package com.ultreon.devices.gui.config;

import io.github.xypercode.craftyconfig.Ranged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ConfigData {
    private final String key;
    private Object currentValue;
    private final Class<?> type;
    private final Object defaultValue;
    private final Ranged ranged;

    public ConfigData(String key, Object currentValue, Class<?> type, Object aDefault, Ranged range) {
        this.key = key;
        this.currentValue = currentValue;
        this.type = type;
        this.defaultValue = aDefault;
        this.ranged = range;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isDefault() {
        return Objects.equals(this.currentValue, this.defaultValue);
    }

    public AbstractWidget createButton(int x, int y, int width) {
        if (this.type == boolean.class) {
            return createBooleanWidget(x, y, width);
        } else if (this.type == byte.class) {
            return createByteWidget(x, y, width);
        } else if (this.type == short.class) {
            return createShortWidget(x, y, width);
        } else if (this.type == int.class) {
            return createIntWidget(x, y, width);
        } else if (this.type == long.class) {
            return createLongWidget(x, y, width);
        } else if (this.type == float.class) {
            return createFloatWidget(x, y, width);
        } else if (this.type == double.class) {
            return createDoubleWidget(x, y, width);
        } else if (this.type == String.class) {
            return createStringWidget(x, y, width);
        } else if (this.type == Byte.class) {
            return createByteWidget(x, y, width);
        } else if (this.type == Short.class) {
            return createShortWidget(x, y, width);
        } else if (this.type == Integer.class) {
            return createIntWidget(x, y, width);
        } else if (this.type == Long.class) {
            return createLongWidget(x, y, width);
        } else if (this.type == Float.class) {
            return createFloatWidget(x, y, width);
        } else if (this.type == Double.class) {
            return createDoubleWidget(x, y, width);
        }

        throw new IllegalArgumentException("Unsupported type: " + this.type);
    }

    @NotNull
    private Checkbox createBooleanWidget(int x, int y, int width) {
        return new Checkbox(x, y, 20, 20, Component.empty(), (boolean) ConfigData.this.currentValue) {
            @Override
            public void onPress() {
                super.onPress();

                try {
                    setCurrentValue(this.selected());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                }
            }
        };
    }

    @NotNull
    private ValueSliderButton createByteWidget(int x, int y, int width) {
        return new ValueSliderButton(x, y, width, 20, Component.literal(String.valueOf(currentValue)), (byte) this.currentValue, this.ranged.min(), this.ranged.max()) {
            @Override
            protected void updateMessage() {
                try {
                    setCurrentValue((byte) this.getValue());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                    this.setMessage(Component.literal(String.valueOf((byte) this.getValue())));
                }
                this.setMessage(Component.literal(String.valueOf((byte) this.getValue())));
            }
        };
    }

    @NotNull
    private ValueSliderButton createShortWidget(int x, int y, int width) {
        return new ValueSliderButton(x, y, width, 20, Component.literal(String.valueOf(currentValue)), (short) this.currentValue, this.ranged.min(), this.ranged.max()) {
            @Override
            protected void updateMessage() {
                try {
                    setCurrentValue((short) this.getValue());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                    this.setMessage(Component.literal(String.valueOf((short) this.getValue())));
                }
                this.setMessage(Component.literal(String.valueOf((short) this.getValue())));
            }
        };
    }

    @NotNull
    private ValueSliderButton createIntWidget(int x, int y, int width) {
        return new ValueSliderButton(x, y, width, 20, Component.literal(String.valueOf(currentValue)), (int) this.currentValue, this.ranged.min(), this.ranged.max()) {
            @Override
            protected void updateMessage() {
                try {
                    setCurrentValue((int) this.getValue());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                    this.setMessage(Component.literal(String.valueOf((int) this.getValue())));
                }
                this.setMessage(Component.literal(String.valueOf((int) this.getValue())));
            }
        };
    }

    @NotNull
    private ValueSliderButton createLongWidget(int x, int y, int width) {
        return new ValueSliderButton(x, y, width, 20, Component.literal(String.valueOf(currentValue)), (long) this.currentValue, this.ranged.min(), this.ranged.max()) {
            @Override
            protected void updateMessage() {
                try {
                    setCurrentValue((long) this.getValue());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                    this.setMessage(Component.literal(String.valueOf((long) this.getValue())));
                }
                this.setMessage(Component.literal(String.valueOf((long) this.getValue())));
            }
        };
    }

    @NotNull
    private ValueSliderButton createFloatWidget(int x, int y, int width) {
        return new ValueSliderButton(x, y, width, 20, Component.literal(String.valueOf(currentValue)), (float) this.currentValue, this.ranged.min(), this.ranged.max()) {
            @Override
            protected void updateMessage() {
                try {
                    setCurrentValue((float) this.getValue());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                    this.setMessage(Component.literal(String.valueOf(currentValue)));
                }
                this.setMessage(Component.literal(String.valueOf(currentValue)));
            }
        };
    }

    @NotNull
    private ValueSliderButton createDoubleWidget(int x, int y, int width) {
        return new ValueSliderButton(x, y, width, 20, Component.literal(String.valueOf(currentValue)), (double) this.currentValue, this.ranged.min(), this.ranged.max()) {
            @Override
            protected void updateMessage() {
                try {
                    setCurrentValue(this.getValue());
                } catch (Exception e) {
                    currentValue = getCurrentValue();
                    this.setMessage(Component.literal(String.valueOf(currentValue)));
                }
                this.setMessage(Component.literal(String.valueOf(currentValue)));
            }
        };
    }

    @NotNull
    private EditBox createStringWidget(int x, int y, int width) {
        EditBox editBox = new EditBox(Minecraft.getInstance().font, x, y, width, 20, Component.literal(String.valueOf(currentValue)));
        editBox.setResponder(editBoxValue -> {
            try {
                setCurrentValue(editBoxValue);
            } catch (Exception e) {
                currentValue = getCurrentValue();
                editBox.setValue((String) this.currentValue);
            }
        });
        return editBox;
    }

    public abstract Object getCurrentValue();

    public abstract void setCurrentValue(Object value);

    public void setFromWidget(AbstractWidget widget) {
        if (this.type == boolean.class) {
            this.currentValue = ((Checkbox) widget).selected();
        } else if (this.type == byte.class) {
            this.currentValue = (byte) ((ValueSliderButton) widget).getValue();
        } else if (this.type == short.class) {
            this.currentValue = (short) ((ValueSliderButton) widget).getValue();
        } else if (this.type == int.class) {
            this.currentValue = (int) ((ValueSliderButton) widget).getValue();
        } else if (this.type == long.class) {
            this.currentValue = (long) ((ValueSliderButton) widget).getValue();
        } else if (this.type == float.class) {
            this.currentValue = (float) ((ValueSliderButton) widget).getValue();
        } else if (this.type == double.class) {
            this.currentValue = ((ValueSliderButton) widget).getValue();
        } else if (this.type == String.class) {
            this.currentValue = ((EditBox) widget).getValue();
        }
    }

    public String getDescription() {
        return Component.translatable("config.devices." + this.key + ".description").getString();
    }
}
