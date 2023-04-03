package com.aetherteam.nitrogen;

import com.aetherteam.nitrogen.api.users.UserData;
import com.aetherteam.nitrogen.api.users.User;
import com.aetherteam.nitrogen.data.generators.NitrogenLanguageData;
import com.aetherteam.nitrogen.network.PacketDistributor;
import com.aetherteam.nitrogen.network.NitrogenPacketHandler;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.data.event.GatherDataEvent;
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
        modEventBus.addListener(this::dataSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        NitrogenPacketHandler.register();
    }

    public void dataSetup(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        // Client Data
        generator.addProvider(event.includeClient(), new NitrogenLanguageData(packOutput));
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            UUID uuid = serverPlayer.getUUID();
            Map<UUID, User> userData = UserData.Server.getStoredUsers();
            if (userData.containsKey(uuid)) {
                User user = userData.get(uuid);
                PacketDistributor.sendToPlayer(NitrogenPacketHandler.INSTANCE, new UpdateUserInfoPacket(user), serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void serverAboutToStart(ServerAboutToStartEvent event) {
        UserData.Server.initializeForTesting();
    }
}
