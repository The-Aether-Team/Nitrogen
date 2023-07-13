package com.aetherteam.nitrogen.api.users;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.network.NitrogenPacketHandler;
import com.aetherteam.nitrogen.network.PacketRelay;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public final class UserData {
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

        public static void sendUserRequest(MinecraftServer server, ServerPlayer serverPlayer, UUID uuid) {
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.aether-mod.net/api/verify/" + uuid))
                    .timeout(Duration.ofMinutes(2))
                    .header("Content-Type", "application/json")
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept((response) -> parseUserData(server, serverPlayer, uuid, response));
        }

        private static void parseUserData(MinecraftServer server, ServerPlayer serverPlayer, UUID uuid, String response) {
            JsonElement jsonElement = JsonParser.parseString(response);
            if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                JsonObject json = jsonElement.getAsJsonObject();

                User.Tier currentTier = null;
                try {
                    JsonElement currentTierElement = json.get("currentTier");
                    if (currentTierElement != null && !currentTierElement.isJsonNull()) {
                        int currentTierId = currentTierElement.getAsInt();
                        if (currentTierId != -1) {
                            currentTier = User.Tier.byId(currentTierId);
                        }
                    }
                } catch (NumberFormatException e) {
                    Nitrogen.LOGGER.error(e.getMessage());
                }

                User.Tier highestPastTier = null;
                JsonElement pastTiersElement = json.get("pastTiers");
                if (pastTiersElement != null && !pastTiersElement.isJsonNull() && pastTiersElement.isJsonArray()) {
                    JsonArray pastTiersArray = pastTiersElement.getAsJsonArray();
                    int pastTierLevel = 0;
                    try {
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
                    } catch (NumberFormatException e) {
                        Nitrogen.LOGGER.error(e.getMessage());
                    }
                }

                User.Group highestGroup = null;
                JsonElement groupsElement = json.get("groups");
                if (groupsElement != null && !groupsElement.isJsonNull() && groupsElement.isJsonArray()) {
                    JsonArray groupsArray = groupsElement.getAsJsonArray();
                    int groupLevel = 0;
                    try {
                        for (String groupName : groupsArray.asList().stream().map((JsonElement::getAsString)).toList()) {
                            User.Group group = User.Group.valueOf(groupName.toUpperCase(Locale.ROOT));
                            if (group.getLevel() > groupLevel) {
                                groupLevel = group.getLevel();
                                highestGroup = group;
                            }
                        }
                    } catch (NumberFormatException e) {
                        Nitrogen.LOGGER.error(e.getMessage());
                    }
                }

                if (currentTier != null || highestPastTier != null || highestGroup != null) {
                    User user = new User(currentTier, highestPastTier, ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).format(User.DATE_FORMAT), highestGroup);
                    modifySavedData(server, uuid, user);
                    STORED_USERS.put(uuid, user);
                    PacketRelay.sendToPlayer(NitrogenPacketHandler.INSTANCE, new UpdateUserInfoPacket(user), serverPlayer);
                }
            }
        }

        public static Map<UUID, User> getStoredUsers() {
            return ImmutableMap.copyOf(STORED_USERS);
        }
    }

    private static Map<UUID, User> getSavedMap(MinecraftServer server) {
        return getSavedData(server).getStoredUsers();
    }

    private static void modifySavedData(MinecraftServer server, UUID uuid, User user) {
        getSavedData(server).modifyStoredUsers(uuid, user);
    }

    private static void removeSavedData(MinecraftServer server, UUID uuid) {
        getSavedData(server).removeStoredUsers(uuid);
    }

    private static UserSavedData getSavedData(MinecraftServer server) {
        return UserSavedData.compute(server.overworld().getDataStorage());
    }
}
