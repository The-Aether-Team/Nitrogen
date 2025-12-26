package com.aetherteam.nitrogen.fabric;

import com.aetherteam.nitrogen.fabric.networking.CommonPayloadContext;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.BiConsumer;

public final class NetworkRegisterHelper {

    public static final NetworkRegisterHelper INSTANCE = new NetworkRegisterHelper();

    private NetworkRegisterHelper(){}

    // CLIENTBOUND
    public <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, CommonPayloadContext> consumer) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    public <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, CommonPayloadContext> consumer) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            consumer.accept(payload, new CommonPayloadContext(context.player(), context.responseSender()));
        });
    }

    public <T extends CustomPacketPayload> void playBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, CommonPayloadContext> consumer) {
        playToClient(type, codec, consumer);
        playToServer(type, codec, consumer);
    }

}
