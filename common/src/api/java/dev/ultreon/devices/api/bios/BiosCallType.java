package dev.ultreon.devices.api.bios;

import dev.ultreon.devices.api.bios.efi.*;

public enum BiosCallType {
    POWER_OFF(void.class),
    REBOOT(void.class),
    ENTER_SLEEP(void.class),
    EXIT_SLEEP(void.class),

    REBOOT_TO_FIRMWARE(void.class),

    GET_DEVICE_ID(void.class),
    GET_RUNNING_OS(String.class),
    GET_MAIN_DRIVE(VEFI_DiskInfo.class),
    SET_MAIN_DRIVE(void.class, VEFI_DiskInfo.class),
    ADD_SYSTEM(void.class, VEFI_File.class),
    SEND_NOTIFICATION(VEFI_Notification.class),
    GET_DEVICE_INFO(VEFI_DeviceInfo.class),

    GET_VIDEO_INFO(VEFI_VideoSize.class),
    OPEN_DISPLAY(int.class),
    CLOSE_DISPLAY(void.class),
    GET_BOOT_DEVICE_ID(VEFI_DeviceID.class),
    FRAMEBUFFER_CALL(FrameBufferCall.class),
    ENTER_USERSPACE(void.class, String.class, String.class, ClassLoader.class),;

    public final Class<?> returnType;
    public final Class<?>[] parameters;

    BiosCallType(Class<?> returnType) {
        this(returnType, new Class<?>[0]);
    }

    BiosCallType(Class<?> returnType, Class<?>... parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }
}
