package com.aetherteam.nitrogen.api.users;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserSavedData extends SavedData {
    public static final String FILE_NAME = "users";
    private final Map<UUID, User> storedUsers = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag storedUsersTag = new CompoundTag();
        for (Map.Entry<UUID, User> userEntry : this.storedUsers.entrySet()) {
            CompoundTag userEntryTag = new CompoundTag();
            User user = userEntry.getValue();
            if (user.getHighestPastTier() != null) {
                userEntryTag.putString("HighestPastTier", user.getHighestPastTier().name());
            }
            if (user.getCurrentTier() != null) {
                userEntryTag.putString("CurrentTier", user.getCurrentTier().name());
            }
            if (user.getRenewalDate() != null) {
                userEntryTag.putString("RenewalDate", user.getRenewalDate());
            }
            if (user.getHighestGroup() != null) {
                userEntryTag.putString("HighestGroup", user.getHighestGroup().name());
            }
            storedUsersTag.put(userEntry.getKey().toString(), userEntryTag);
        }
        tag.put("StoredUsers", storedUsersTag);
        return tag;
    }

    public static UserSavedData load(CompoundTag tag) {
        UserSavedData data = UserSavedData.create();
        for (String key : tag.getAllKeys()) {
            if (key.equals("StoredUsers")) {
                CompoundTag storedUsersTag = tag.getCompound(key);
                for (String storedUsersKey : storedUsersTag.getAllKeys()) {
                    CompoundTag userEntryTag = storedUsersTag.getCompound(storedUsersKey);
                    UUID uuid = UUID.fromString(storedUsersKey);
                    User.Tier highestPastTier = null;
                    User.Tier currentTier = null;
                    String renewalDate = null;
                    User.Group highestGroup = null;
                    if (userEntryTag.contains("HighestPastTier")) {
                        highestPastTier = User.Tier.valueOf(userEntryTag.getString("HighestPastTier"));
                    }
                    if (userEntryTag.contains("CurrentTier")) {
                        currentTier = User.Tier.valueOf(userEntryTag.getString("CurrentTier"));
                    }
                    if (userEntryTag.contains("RenewalDate")) {
                        renewalDate = userEntryTag.getString("RenewalDate");
                    }
                    if (userEntryTag.contains("HighestGroup")) {
                        highestGroup = User.Group.valueOf(userEntryTag.getString("HighestGroup"));
                    }
                    data.storedUsers.put(uuid, new User(highestPastTier, currentTier, renewalDate, highestGroup));
                }
            }
        }
        return data;
    }

    public static UserSavedData create() {
        return new UserSavedData();
    }

    public static UserSavedData compute(DimensionDataStorage dataStorage) {
        return dataStorage.computeIfAbsent(UserSavedData::load, UserSavedData::create, FILE_NAME);
    }

    Map<UUID, User> getStoredUsers() {
        return ImmutableMap.copyOf(this.storedUsers);
    }

    void modifyStoredUsers(UUID uuid, User user) {
        this.storedUsers.put(uuid, user);
        this.setDirty();
    }

    void removeStoredUsers(UUID uuid) {
        this.storedUsers.remove(uuid);
        this.setDirty();
    }
}
