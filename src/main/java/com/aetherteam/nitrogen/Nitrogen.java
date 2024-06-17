package com.aetherteam.nitrogen;

import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.data.NitrogenDataGenerators;
import com.aetherteam.nitrogen.loot.modifiers.NitrogenLootModifiers;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.aetherteam.nitrogen.network.packet.serverbound.TriggerUpdateInfoPacket;
import com.aetherteam.nitrogen.world.biomemodifier.NitrogenBiomeModifierSerializers;
import com.aetherteam.nitrogen.world.foliageplacer.NitrogenFoliagePlacerTypes;
import com.aetherteam.nitrogen.world.trunkplacer.NitrogenTrunkPlacerTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Mod(Nitrogen.MODID)
@EventBusSubscriber(modid = Nitrogen.MODID, bus = EventBusSubscriber.Bus.GAME)
public class Nitrogen {
    public static final String MODID = "nitrogen_internals";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Nitrogen(ModContainer mod, IEventBus bus, Dist dist) {
        bus.addListener(NitrogenDataGenerators::onInitializeDataGenerator);
        bus.addListener(this::registerPackets);

        DeferredRegister<?>[] registers = {
            NitrogenLootModifiers.GLOBAL_LOOT_MODIFIERS,
            NitrogenBiomeModifierSerializers.BIOME_MODIFIER_SERIALIZERS,
            NitrogenFoliagePlacerTypes.FOLIAGE_PLACERS,
            NitrogenTrunkPlacerTypes.TRUNK_PLACERS
        };

        for (DeferredRegister<?> register : registers) {
            register.register(bus);
        }
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();

        registrar.playToClient(UpdateUserInfoPacket.TYPE, UpdateUserInfoPacket.STREAM_CODEC, UpdateUserInfoPacket::execute);
        registrar.playToServer(TriggerUpdateInfoPacket.TYPE, TriggerUpdateInfoPacket.STREAM_CODEC, TriggerUpdateInfoPacket::execute);
    }

    /**
     * @see UserData.Server#initializeFromCache(MinecraftServer).
     */
    @SubscribeEvent
    public static void serverAboutToStart(ServerStartingEvent event) {
        UserData.Server.initializeFromCache(event.getServer());
    }

    /**
     * Checks if a player has a corresponding {@link User} when logging in. If they do, then that is synced to the client.
     * If they don't, or if they are past their renewal time, the server will query the Patreon database through {@link UserData.Server#sendUserRequest(MinecraftServer, ServerPlayer, UUID)}.
     */
    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            UUID uuid = serverPlayer.getGameProfile().getId();
            Map<UUID, User> userData = UserData.Server.getStoredUsers();
            User user;
            if (userData.containsKey(uuid)) {
                user = userData.get(uuid);
                if (user != null && user.getRenewalDate() != null && isAfterRenewalTime(user)) { // Check renewal time.
                    UserData.Server.sendUserRequest(serverPlayer.getServer(), serverPlayer, uuid);
                } else { // Sync to client.
                    PacketDistributor.sendToPlayer(serverPlayer, new UpdateUserInfoPacket(user));
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
