package com.aetherteam.nitrogen.api.users;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.network.NitrogenPacketHandler;
import com.aetherteam.nitrogen.network.PacketRelay;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class UserData {
    public static class Client {
        private static User CLIENT_USER;

        /**
         * @return The client's {@link User}.
         */
        public static User getClientUser() {
            return CLIENT_USER;
        }

        /**
         * Sets the client's {@link User}.
         * @param clientUser The {@link User}.
         */
        public static void setClientUser(User clientUser) {
            CLIENT_USER = clientUser;
        }
    }

    public static class Server {
        private static final Map<UUID, User> STORED_USERS = new HashMap<>();

        /**
         * Initializes data to {@link Server#STORED_USERS} from the data stored in the world files.
         * @param server The {@link MinecraftServer}.
         */
        public static void initializeFromCache(MinecraftServer server) {
            STORED_USERS.putAll(getSavedMap(server));
        }

        /**
         * Queries the Patreon database to see if a UUID for a player is stored in it.
         * @param server The {@link MinecraftServer}.
         * @param serverPlayer The {@link ServerPlayer} to query for.
         * @param uuid The {@link UUID} to query with.
         */
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

        /**
         * Parse the response from the Patreon database.
         * @param server The {@link MinecraftServer}.
         * @param serverPlayer The {@link ServerPlayer} to query for.
         * @param uuid The {@link UUID} to query with.
         * @param response The database response {@link String}.
         */
        private static void parseUserData(MinecraftServer server, ServerPlayer serverPlayer, UUID uuid, String response) {
            JsonElement jsonElement = JsonParser.parseString(response); // Format the response to JSON.
            if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                JsonObject json = jsonElement.getAsJsonObject();

                // Attempt to parse the current Patreon tier associated with the UUID.
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

                // Attempt to parse the past highest Patreon tier associated with the UUID.
                User.Tier highestPastTier = null;
                JsonElement pastTiersElement = json.get("pastTiers");
                if (pastTiersElement != null && !pastTiersElement.isJsonNull() && pastTiersElement.isJsonArray()) {
                    JsonArray pastTiersArray = pastTiersElement.getAsJsonArray();
                    int pastTierLevel = 0;
                    try {
                        for (JsonElement pastTierElement : pastTiersArray) {
                            int pastTierId = pastTierElement.getAsInt();
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

                // Attempt to parse the highest group associated with the UUID.
                User.Group highestGroup = null;
                JsonElement groupsElement = json.get("groups");
                if (groupsElement != null && !groupsElement.isJsonNull() && groupsElement.isJsonArray()) {
                    JsonArray groupsArray = groupsElement.getAsJsonArray();
                    int groupLevel = 0;
                    try {
                        for (JsonElement groupElement :groupsArray) {
                            String groupName = groupElement.getAsString();
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

                // Create a User for the player, store the data to the server, and send it to the client.
                if (currentTier != null || highestPastTier != null || highestGroup != null) {
                    User user = new User(currentTier, highestPastTier, ZonedDateTime.now(ZoneId.of("UTC")).plusDays(1).format(User.DATE_FORMAT), highestGroup);
                    modifySavedData(server, uuid, user);
                    STORED_USERS.put(uuid, user);
                    PacketRelay.sendToPlayer(NitrogenPacketHandler.INSTANCE, new UpdateUserInfoPacket(user), serverPlayer);
                }
            }
        }

        /**
         * @return A {@link Map} of {@link UUID}s paired with {@link User}s on the server.
         */
        public static Map<UUID, User> getStoredUsers() {
            return ImmutableMap.copyOf(STORED_USERS);
        }
    }

    /**
     * Calls {@link UserSavedData#getStoredUsers()}.
     * @param server The {@link MinecraftServer}.
     * @return A {@link Map} of {@link UUID}s and {@link User}s.
     */
    private static Map<UUID, User> getSavedMap(MinecraftServer server) {
        return getSavedData(server).getStoredUsers();
    }

    /**
     * Calls {@link UserSavedData#modifyStoredUsers(UUID, User)}.
     * @param server The {@link MinecraftServer}.
     * @param uuid The player {@link UUID}.
     * @param user The {@link User}.
     */
    private static void modifySavedData(MinecraftServer server, UUID uuid, User user) {
        getSavedData(server).modifyStoredUsers(uuid, user);
    }

    /**
     * Calls {@link UserSavedData#removeStoredUsers(UUID)}.
     * @param server The {@link MinecraftServer}.
     * @param uuid The player {@link UUID}.
     */
    private static void removeSavedData(MinecraftServer server, UUID uuid) {
        getSavedData(server).removeStoredUsers(uuid);
    }

    /**
     * @param server The {@link MinecraftServer}.
     * @return The {@link UserSavedData} for the "users.dat" file of the world.
     */
    private static UserSavedData getSavedData(MinecraftServer server) {
        return UserSavedData.compute(server.overworld().getDataStorage());
    }
}
