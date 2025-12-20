package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.NetworkRegisterHelper;
import com.aetherteam.nitrogen.fabric.networking.CommonPayloadContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(NetworkRegisterHelper.class)
public abstract class NetworkRegisterHelperMixin {
    @Inject(method = "playToClient", at = @At("TAIL"))
    private <T extends CustomPacketPayload> void registerClientHandle(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec, BiConsumer<T, CommonPayloadContext> consumer, CallbackInfo ci) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            consumer.accept(payload, new CommonPayloadContext(context.player(), context.responseSender()));
        });
    }
}
