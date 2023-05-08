package com.aetherteam.nitrogen.api.users;

import com.aetherteam.nitrogen.Nitrogen;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class UserData {
    public static class Client {
        private static User CLIENT_USER;

        public static User getClientUser() {
            return CLIENT_USER;
        }

        public static void setClientUser(User clientUser) {
            CLIENT_USER = clientUser;
        }
    }

    public static class Server {
        private static final Map<UUID, User> STORED_USERS = new HashMap<>();

        public static void initializeFromCache(MinecraftServer server) {
            STORED_USERS.putAll(getSavedMap(server));
        }

        public static User queryUser(MinecraftServer server, UUID uuid) {
            try {
                URL url = new URL("https://www.aether-mod.net/api/verify/" + uuid);
                URLConnection connection = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String query = reader.readLine();
                reader.close();

                JsonElement jsonElement = JsonParser.parseString(query);
                if (jsonElement != null) {
                    JsonObject json = jsonElement.getAsJsonObject();

                    int currentTierId = json.get("currentTier").getAsInt();
                    User.Tier currentTier = null;
                    if (currentTierId != -1) {
                        currentTier = User.Tier.byId(currentTierId);
                    }

                    JsonArray pastTiersArray = json.getAsJsonArray("pastTiers");
                    User.Tier highestPastTier = null;
                    int pastTierLevel = 0;
                    for (int pastTierId : pastTiersArray.asList().stream().map((JsonElement::getAsInt)).toList()) {
                        if (pastTierId != -1) {
                            User.Tier pastTier = User.Tier.byId(pastTierId);
                            if (pastTier != null) {
                                if (pastTier.getLevel() > pastTierLevel) {
                                    pastTierLevel = pastTier.getLevel();
                                    highestPastTier = pastTier;
                                }
                            }
                        }
                    }

                    String renewalDate = json.get("renewsAt").getAsString();

                    JsonArray groupsArray = json.getAsJsonArray("groups");
                    User.Group highestGroup = null;
                    int groupLevel = 0;
                    for (String groupName : groupsArray.asList().stream().map((JsonElement::getAsString)).toList()) {
                        User.Group group = User.Group.valueOf(groupName.toUpperCase(Locale.ROOT));
                        if (group.getLevel() > groupLevel) {
                            groupLevel = group.getLevel();
                            highestGroup = group;
                        }
                    }

                    User user = new User(currentTier, highestPastTier, renewalDate, highestGroup);
                    modifySavedData(server, uuid, user);
                    STORED_USERS.put(uuid, user);
                    return user;
                }
            } catch (IOException e) {
                Nitrogen.LOGGER.info(e.getMessage());
            }
            return null;
        }

        public static Map<UUID, User> getStoredUsers() {
            return ImmutableMap.copyOf(STORED_USERS);
        }
    }

    protected static Map<UUID, User> getSavedMap(MinecraftServer server) {
        return getSavedData(server).getStoredUsers();
    }

    protected static void modifySavedData(MinecraftServer server, UUID uuid, User user) {
        getSavedData(server).modifyStoredUsers(uuid, user);
    }

    protected static void removeSavedData(MinecraftServer server, UUID uuid) {
        getSavedData(server).removeStoredUsers(uuid);
    }

    protected static UserSavedData getSavedData(MinecraftServer server) {
        return UserSavedData.compute(server.overworld().getDataStorage());
    }
}
