package com.aetherteam.nitrogen.api.users;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class UserSavedData extends SavedData { //TODO VERIFY
    public static final SavedDataType<UserSavedData> TYPE = new SavedDataType<>(
        "nitrogen_users",
        UserSavedData::new,
        ctx -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(UUIDUtil.CODEC, User.CODEC).fieldOf("stored_users").forGetter(UserSavedData::getStoredUsers)
        ).apply(instance, UserSavedData::new))
    );
    private Map<UUID, User> storedUsers;

    private UserSavedData(SavedData.Context ctx) {
        this(new HashMap<>());
    }

    private UserSavedData(Map<UUID, User> storedUsers) {
        this.storedUsers = storedUsers;
    }

//    /**
//     * Saves user data to the world in a file named "users.dat".
//     *
//     * @param tag The {@link CompoundTag} to save the data to.
//     * @return A {@link CompoundTag} with the data.
//     */
//    @Override
//    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
//        CompoundTag storedUsersTag = new CompoundTag();
//        for (Map.Entry<UUID, User> userEntry : this.storedUsers.entrySet()) {
//            CompoundTag userEntryTag = new CompoundTag();
//            User user = userEntry.getValue();
//            if (user.getHighestPastTier() != null) {
//                userEntryTag.putString("HighestPastTier", user.getHighestPastTier().name());
//            }
//            if (user.getCurrentTier() != null) {
//                userEntryTag.putString("CurrentTier", user.getCurrentTier().name());
//            }
//            if (user.getRenewalDate() != null) {
//                userEntryTag.putString("RenewalDate", user.getRenewalDate());
//            }
//            if (user.getHighestGroup() != null) {
//                userEntryTag.putString("HighestGroup", user.getHighestGroup().name());
//            }
//            storedUsersTag.put(userEntry.getKey().toString(), userEntryTag);
//        }
//        tag.put("StoredUsers", storedUsersTag);
//        return tag;
//    }
//
//    /**
//     * Loads user data from the world from a {@link CompoundTag} representing the data in "users.dat".
//     *
//     * @param tag The {@link CompoundTag}.
//     * @return The {@link UserSavedData} created from the world data.
//     */
//    public static UserSavedData load(CompoundTag tag) {
//        UserSavedData data = UserSavedData.create();
//        for (String key : tag.keySet()) {
//            if (key.equals("StoredUsers")) {
//                Optional<CompoundTag> storedUsersOptional = tag.getCompound(key);
//                if (storedUsersOptional.isPresent()) {
//                    CompoundTag storedUsersTag = storedUsersOptional.get();
//                    for (String storedUsersKey : storedUsersTag.keySet()) {
//                        Optional<CompoundTag> userEntryOptional = storedUsersTag.getCompound(storedUsersKey);
//                        if (userEntryOptional.isPresent()) {
//                            CompoundTag userEntryTag = userEntryOptional.get();
//                            UUID uuid = UUID.fromString(storedUsersKey);
//                            User.Tier highestPastTier = null;
//                            User.Tier currentTier = null;
//                            String renewalDate = null;
//                            User.Group highestGroup = null;
//
//                            Optional<String> highestPastTierOptional = userEntryTag.getString("HighestPastTier");
//                            Optional<String> currentTierOptional = userEntryTag.getString("CurrentTier");
//                            Optional<String> renewalDateOptional = userEntryTag.getString("RenewalDate");
//                            Optional<String> highestGroupOptional = userEntryTag.getString("HighestGroup");
//
//                            if (highestPastTierOptional.isPresent()) {
//                                highestPastTier = User.Tier.valueOf(highestPastTierOptional.get());
//                            }
//                            if (currentTierOptional.isPresent()) {
//                                currentTier = User.Tier.valueOf(currentTierOptional.get());
//                            }
//                            if (renewalDateOptional.isPresent()) {
//                                renewalDate = renewalDateOptional.get();
//                            }
//                            if (highestGroupOptional.isPresent()) {
//                                highestGroup = User.Group.valueOf(highestGroupOptional.get());
//                            }
//                            data.storedUsers.put(uuid, new User(highestPastTier, currentTier, renewalDate, highestGroup));
//                        }
//                    }
//                }
//            }
//        }
//        return data;
//    }
//
//    public static UserSavedData create() {
//        return new UserSavedData();
//    }
//
//    /**
//     * Loads or creates the "users.dat" file.
//     *
//     * @param dataStorage The {@link DimensionDataStorage} of the world.
//     * @return The {@link UserSavedData} corresponding to the data file.
//     */
//    public static UserSavedData compute(DimensionDataStorage dataStorage) {
//        return dataStorage.computeIfAbsent(new SavedData.Factory<>(UserSavedData::new, (compoundTag, provider) -> UserSavedData.load(compoundTag)), FILE_NAME);
//    }

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
