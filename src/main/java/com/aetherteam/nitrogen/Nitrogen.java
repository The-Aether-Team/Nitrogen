package com.aetherteam.nitrogen;

import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.network.NitrogenPacketHandler;
import com.aetherteam.nitrogen.network.PacketRelay;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

public class Nitrogen implements ModInitializer {
    public static final String MODID = "nitrogen_internals";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        NitrogenPacketHandler.register();
        ServerLifecycleEvents.SERVER_STARTED.register(Nitrogen::serverAboutToStart);
        ServerLoginConnectionEvents.QUERY_START.register(Nitrogen::playerLoggedIn);
    }

    /**
     * @see UserData.Server#initializeFromCache(MinecraftServer).
     */
    public static void serverAboutToStart(MinecraftServer server) {
        UserData.Server.initializeFromCache(server);
    }

    /**
     * Checks if a player has a corresponding {@link User} when logging in. If they do, then that is synced to the client.
     * If they don't, or if they are past their renewal time, the server will query the Patreon database through {@link UserData.Server#sendUserRequest(MinecraftServer, ServerPlayer, UUID)}.
     */
    public static void playerLoggedIn(ServerLoginPacketListenerImpl handler, MinecraftServer server, PacketSender sender, ServerLoginNetworking.LoginSynchronizer synchronizer) {
        Player player = server.getPlayerList().getPlayerByName(handler.getUserName());
        if (player instanceof ServerPlayer serverPlayer) {
            UUID uuid = serverPlayer.getGameProfile().getId();
            Map<UUID, User> userData = UserData.Server.getStoredUsers();
            User user;
            if (userData.containsKey(uuid)) {
                user = userData.get(uuid);
                if (user != null && user.getRenewalDate() != null && isAfterRenewalTime(user)) { // Check renewal time.
                    UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, uuid);
                } else { // Sync to client.
                    PacketRelay.sendToPlayer(NitrogenPacketHandler.INSTANCE, new UpdateUserInfoPacket(user), serverPlayer);
                }
            } else { // Query database if no User is found with the server.
                UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, uuid);
            }
        }
    }

    /**
     * Checks if the current time is past the time when a {@link User}'s information has to be re-verified.
     * @param user The {@link User}.
     * @return The {@link Boolean} result.
     */
    private static boolean isAfterRenewalTime(User user) {
        ZonedDateTime renewalDateTime = LocalDateTime.parse(user.getRenewalDate(), User.DATE_FORMAT).atZone(ZoneId.of("UTC"));
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        return currentDateTime.isAfter(renewalDateTime);
    }
}
