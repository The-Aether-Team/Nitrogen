package com.aetherteam.nitrogen.network;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.aetherteam.nitrogen.network.packet.serverbound.TriggerUpdateInfoPacket;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class NitrogenPacketHandler {
    public static final SimpleChannel INSTANCE = new SimpleChannel(new ResourceLocation(Nitrogen.MODID, "main"));

    private static int index;

    public static synchronized void register() {
        INSTANCE.initServerListener();
        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> INSTANCE::initClientListener);

        // CLIENT
        registerClientbound(UpdateUserInfoPacket.class, UpdateUserInfoPacket::decode);

        // SERVER
        registerServerbound(TriggerUpdateInfoPacket.class, TriggerUpdateInfoPacket::decode);
    }

    private static <MSG extends ClientPacket> void registerClientbound(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.registerS2CPacket(packet, index++, decoder);
    }

    private static <MSG extends ServerPacket> void registerServerbound(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.registerC2SPacket(packet, index++, decoder);
    }
}
