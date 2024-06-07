package dev.ultreon.devices.mineos.apps.email.object;

import net.minecraft.nbt.CompoundTag;

/**
 * @author MrCrayfish
 */
public record Contact(String nickname, String email) {

    @Override
    public String toString() {
        return nickname;
    }

    public void save(CompoundTag contactTag) {
        contactTag.putString("nickname", nickname);
        contactTag.putString("email", email);
    }
}
