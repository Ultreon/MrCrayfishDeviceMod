package com.ultreon.devices.api.io;

import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public final class MimeType {
    public static final MimeType TEXT_PLAIN = new MimeType("text", "plain");
    public static final MimeType APPLICATION_JSON = new MimeType("application", "json");
    public static final MimeType APPLICATION_XML = new MimeType("application", "xml");
    public static final MimeType APPLICATION_OCTET_STREAM = new MimeType("application", "octet-stream");
    public static final MimeType TEXT_NOTE_STASH = new MimeType("text", "note-stash");
    public static final MimeType IMAGE_MC_IMG = new MimeType("image", "mc-img");
    private final String type;
    private final String subType;

    public MimeType(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    public static MimeType of(CompoundNBT mimeType) {
        return new MimeType(mimeType.getString("type"), mimeType.getString("subType"));
    }

    public CompoundNBT toNbt() {
        CompoundNBT mimeType = new CompoundNBT();
        mimeType.putString("type", type);
        mimeType.putString("subType", subType);
        return mimeType;
    }

    public String type() {
        return type;
    }

    public String subType() {
        return subType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        MimeType that = (MimeType) obj;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.subType, that.subType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subType);
    }

    @Override
    public String toString() {
        return "MimeType[" +
                "type=" + type + ", " +
                "subType=" + subType + ']';
    }

}
