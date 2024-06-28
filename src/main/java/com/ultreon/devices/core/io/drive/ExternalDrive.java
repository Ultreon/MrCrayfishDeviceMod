package com.ultreon.devices.core.io.drive;

import com.ultreon.devices.core.io.ServerFolder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author MrCrayfish
 */
public final class ExternalDrive extends AbstractDrive {
    private static final Predicate<CompoundNBT> PREDICATE_DRIVE_TAG = tag -> tag.contains("name", Constants.NBT.TAG_STRING) && tag.contains("uuid", Constants.NBT.TAG_STRING) && tag.contains("root", Constants.NBT.TAG_COMPOUND);

    private ExternalDrive() {
    }

    public ExternalDrive(String displayName) {
        super(displayName);
    }

    @Nullable
    public static AbstractDrive fromTag(CompoundNBT driveTag) {
        if (!PREDICATE_DRIVE_TAG.test(driveTag)) return null;

        AbstractDrive drive = new ExternalDrive();
        drive.name = driveTag.getString("name");
        drive.uuid = UUID.fromString(driveTag.getString("uuid"));

        CompoundNBT folderTag = driveTag.getCompound("root");
        drive.root = ServerFolder.fromTag(folderTag.getString("file_name"), folderTag.getCompound("data"));

        return drive;
    }

    @Override
    public CompoundNBT toTag() {
        CompoundNBT driveTag = new CompoundNBT();
        driveTag.putString("name", name);
        driveTag.putString("uuid", uuid.toString());

        CompoundNBT folderTag = new CompoundNBT();
        folderTag.putString("file_name", root.getName());
        folderTag.put("data", root.toTag());
        driveTag.put("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType() {
        return Type.EXTERNAL;
    }
}
