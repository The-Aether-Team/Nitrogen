package com.aetherteam.nitrogen;

import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.data.NitrogenDataGenerators;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.aetherteam.nitrogen.network.packet.serverbound.TriggerUpdateInfoPacket;
import com.aetherteam.nitrogen.world.foliageplacer.NitrogenFoliagePlacerTypes;
import com.aetherteam.nitrogen.world.trunkplacer.NitrogenTrunkPlacerTypes;
import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.LootEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

public class Nitrogen implements ModInitializer {
    public static final String MODID = "nitrogen_internals";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    public static MinecraftServer SERVER_INSTANCE = null;

    @Override
    public void onInitialize() {
        this.registerPackets();

        //NitrogenLootModifiers.init();
        NitrogenFoliagePlacerTypes.init();
        NitrogenTrunkPlacerTypes.init();

        //--

        ServerLifecycleEvents.SERVER_STARTED.register(Nitrogen::serverAboutToStart);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            playerLoggedIn(handler.player, sender);
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER_INSTANCE = server);

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER_INSTANCE = null);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private void registerPackets() {
        PayloadTypeRegistry.playS2C().register(UpdateUserInfoPacket.TYPE, UpdateUserInfoPacket.STREAM_CODEC);

        PayloadTypeRegistry.playC2S().register(TriggerUpdateInfoPacket.TYPE, TriggerUpdateInfoPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TriggerUpdateInfoPacket.TYPE, TriggerUpdateInfoPacket::execute);
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
    public static void playerLoggedIn(Player player, PacketSender sender) {
        if (player instanceof ServerPlayer serverPlayer) {
            UUID uuid = serverPlayer.getGameProfile().getId();
            Map<UUID, User> userData = UserData.Server.getStoredUsers();
            User user;
            if (userData.containsKey(uuid)) {
                user = userData.get(uuid);
                if (user != null && user.getRenewalDate() != null && isAfterRenewalTime(user)) { // Check renewal time.
                    UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, uuid);
                } else { // Sync to client.
                    sender.sendPacket(new UpdateUserInfoPacket(user));
                }
            } else { // Query database if no User is found with the server.
                UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, uuid);
            }
        }
    }

    /**
     * Checks if the current time is past the time when a {@link User}'s information has to be re-verified.
     *
     * @param user The {@link User}.
     * @return The {@link Boolean} result.
     */
    private static boolean isAfterRenewalTime(User user) {
        ZonedDateTime renewalDateTime = LocalDateTime.parse(user.getRenewalDate(), User.DATE_FORMAT).atZone(ZoneId.of("UTC"));
        ZonedDateTime currentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        return currentDateTime.isAfter(renewalDateTime);
    }
}
