package com.aetherteam.nitrogen.fabric.client.events;

import com.aetherteam.nitrogen.fabric.events.CancellableCallback;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class LivingEntityRenderEvents {

    public static final Event<PreMainGeneric> BEFORE_RENDER = EventFactory.createArrayBacked(PreMainGeneric.class, invokers -> (renderer, state, poseStack, bufferSource, packedLight, callback) -> {
        for (var invoker : invokers) invoker.beforeRendering(renderer, state, poseStack, bufferSource, packedLight, callback);
    });

    public static final Event<PostMainGeneric> AFTER_RENDER = EventFactory.createArrayBacked(PostMainGeneric.class, invokers -> (renderer, state, poseStack, bufferSource, packedLight) -> {
        for (var invoker : invokers) invoker.afterRendering(renderer, state, poseStack, bufferSource, packedLight);
    });

    public interface PreMainGeneric {
        void beforeRendering(LivingEntityRenderer<?, ?, ?> renderer, LivingEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CancellableCallback callback);
    }

    public interface PostMainGeneric {
        void afterRendering(LivingEntityRenderer<?, ?, ?> renderer, LivingEntityRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);
    }
}
