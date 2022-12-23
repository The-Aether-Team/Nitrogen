package com.gildedgames.nitrogen;

import com.gildedgames.nitrogen.api.users.RoleData;
import com.gildedgames.nitrogen.api.users.User;
import com.gildedgames.nitrogen.network.PacketDistributor;
import com.gildedgames.nitrogen.network.NitrogenPacketHandler;
import com.gildedgames.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

@Mod(Nitrogen.MODID)
@Mod.EventBusSubscriber(modid = Nitrogen.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Nitrogen {
    public static final String MODID = "nitrogen";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * This is a test JavaDoc, please ignore.
     * @author bconlon
     */
    public Nitrogen() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        NitrogenPacketHandler.register();
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            UUID uuid = serverPlayer.getUUID();
            Map<UUID, User> userData = RoleData.Server.getStoredUsers();
            if (userData.containsKey(uuid)) {
                User user = userData.get(uuid);
                PacketDistributor.sendToPlayer(NitrogenPacketHandler.INSTANCE, new UpdateUserInfoPacket(user), serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        RoleData.Server.initializeForTesting();
    }
}
