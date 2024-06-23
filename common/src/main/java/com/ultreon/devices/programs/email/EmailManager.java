package com.ultreon.devices.programs.email;

import com.google.common.collect.HashBiMap;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.WorldSavedData;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Notification;
import com.ultreon.devices.programs.email.object.Email;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

;

/**
 * @author MrCrayfish
 */
public class EmailManager implements WorldSavedData {
    public static final EmailManager INSTANCE = new EmailManager();
    private final HashBiMap<UUID, String> uuidToName = HashBiMap.create();
    private final Map<String, List<Email>> nameToInbox = new HashMap<>();
    @OnlyIn(Dist.CLIENT)
    private List<Email> inbox;

    public boolean addEmailToInbox(Email email, String to) {
        if (nameToInbox.containsKey(to)) {
            nameToInbox.get(to).add(0, email);
            sendNotification(to, email);
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Email> getInbox() {
        if (inbox == null) {
            inbox = new ArrayList<>();
        }
        return inbox;
    }

    public List<Email> getEmailsForAccount(PlayerEntity player) {
        if (uuidToName.containsKey(player.getUUID())) {
            return nameToInbox.get(uuidToName.get(player.getUUID()));
        }
        return new ArrayList<Email>();
    }

    public boolean addAccount(PlayerEntity player, String name) {
        if (!uuidToName.containsKey(player.getUUID())) {
            if (!uuidToName.containsValue(name)) {
                uuidToName.put(player.getUUID(), name);
                nameToInbox.put(name, new ArrayList<Email>());
                return true;
            }
        }
        return false;
    }

    public boolean hasAccount(UUID uuid) {
        return uuidToName.containsKey(uuid);
    }

    public String getName(PlayerEntity player) {
        return uuidToName.get(player.getUUID());
    }

    public void load(CompoundNBT nbt) {
        nameToInbox.clear();

        ListNBT inboxes = (ListNBT) nbt.get("Inboxes");
        for (int i = 0; i < inboxes.size(); i++) {
            CompoundNBT inbox = inboxes.getCompound(i);
            String name = inbox.getString("Name");

            List<Email> emails = new ArrayList<Email>();
            ListNBT emailTagList = (ListNBT) inbox.get("Emails");
            for (int j = 0; j < emailTagList.size(); j++) {
                CompoundNBT emailTag = emailTagList.getCompound(j);
                Email email = Email.readFromNBT(emailTag);
                emails.add(email);
            }
            nameToInbox.put(name, emails);
        }

        uuidToName.clear();

        ListNBT accounts = (ListNBT) nbt.get("Accounts");
        for (int i = 0; i < accounts.size(); i++) {
            CompoundNBT account = accounts.getCompound(i);
            UUID uuid = UUID.fromString(account.getString("UUID"));
            String name = account.getString("Name");
            uuidToName.put(uuid, name);
        }
    }

    public void save(CompoundNBT nbt) {
        ListNBT inboxes = new ListNBT();
        for (String key : nameToInbox.keySet()) {
            CompoundNBT inbox = new CompoundNBT();
            inbox.putString("Name", key);

            ListNBT emailTagList = new ListNBT();
            List<Email> emails = nameToInbox.get(key);
            for (Email email : emails) {
                CompoundNBT emailTag = new CompoundNBT();
                email.save(emailTag);
                emailTagList.add(emailTag);
            }
            inbox.put("Emails", emailTagList);
            inboxes.add(inbox);
        }
        nbt.put("Inboxes", inboxes);

        ListNBT accounts = new ListNBT();
        for (UUID key : uuidToName.keySet()) {
            CompoundNBT account = new CompoundNBT();
            account.putString("UUID", key.toString());
            account.putString("Name", Objects.requireNonNull(uuidToName.get(key)));
            accounts.add(account);
        }
        nbt.put("Accounts", accounts);
    }

    public void clear() {
        nameToInbox.clear();
        uuidToName.clear();
        inbox.clear();
    }

    private void sendNotification(String name, Email email) {
        MinecraftServer server = Devices.getServer();
        UUID id = uuidToName.inverse().get(name);
        if (id != null) {
            ServerPlayerEntity player = server.getPlayerList().getPlayer(id);
            if (player != null) {
                Notification notification = new Notification(Icons.MAIL, "New Email!", "from " + email.getAuthor());
                notification.pushTo(player);
            }
        }
    }
}
