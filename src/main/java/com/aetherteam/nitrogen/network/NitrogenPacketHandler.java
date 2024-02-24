package com.aetherteam.nitrogen.network;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import com.aetherteam.nitrogen.network.packet.serverbound.TriggerUpdateInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.MessageFunctions;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class NitrogenPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Nitrogen.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {
        // CLIENT
        register(UpdateUserInfoPacket.class, UpdateUserInfoPacket::decode);

        // SERVER
        register(TriggerUpdateInfoPacket.class, TriggerUpdateInfoPacket::decode);
    }

    private static <MSG extends BasePacket> void register(final Class<MSG> packet, MessageFunctions.MessageDecoder<MSG> decoder) {
        INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumerMainThread(BasePacket::handle).add();
    }
}
