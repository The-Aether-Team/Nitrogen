package com.aetherteam.nitrogen.api.users;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class UserSavedData extends SavedData {
    public static final String FILE_NAME = "users";
    private final Map<UUID, User> storedUsers = new HashMap<>();

    /**
     * Saves user data to the world in a file named "users.dat".
     *
     * @param tag The {@link CompoundTag} to save the data to.
     * @return A {@link CompoundTag} with the data.
     */
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
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

    /**
     * Loads user data from the world from a {@link CompoundTag} representing the data in "users.dat".
     *
     * @param tag The {@link CompoundTag}.
     * @return The {@link UserSavedData} created from the world data.
     */
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

    /**
     * Loads or creates the "users.dat" file.
     *
     * @param dataStorage The {@link DimensionDataStorage} of the world.
     * @return The {@link UserSavedData} corresponding to the data file.
     */
    public static UserSavedData compute(DimensionDataStorage dataStorage) {
        return dataStorage.computeIfAbsent(new SavedData.Factory<>(UserSavedData::new, (compoundTag, provider) -> UserSavedData.load(compoundTag)), FILE_NAME);
    }

    /**
     * @return A {@link Map} of player {@link UUID}s and {@link User}s retrieved from "users.dat".
     */
    Map<UUID, User> getStoredUsers() {
        return ImmutableMap.copyOf(this.storedUsers);
    }

    /**
     * Modifies the {@link User} data stored in the world data.
     *
     * @param uuid The {@link UUID} of the player that the {@link User} belongs to.
     * @param user The {@link User}.
     */
    void modifyStoredUsers(UUID uuid, User user) {
        this.storedUsers.put(uuid, user);
        this.setDirty();
    }

    /**
     * Removes an entry for a player's {@link User} data from the world data.
     *
     * @param uuid The player's {@link UUID}.
     */
    void removeStoredUsers(UUID uuid) {
        this.storedUsers.remove(uuid);
        this.setDirty();
    }
}
