package com.aetherteam.nitrogen.fabric.client.events;

import com.aetherteam.nitrogen.fabric.events.CancellableCallback;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class PlayerRenderEvents {

    public static final Event<PreMain> BEFORE_RENDER = EventFactory.createArrayBacked(PreMain.class, invokers -> (renderer, state, poseStack, collector, cameraState, callback) -> {
        for (var invoker : invokers) invoker.beforeRendering(renderer, state, poseStack, collector, cameraState, callback);
    });

    public static final Event<PostMain> AFTER_RENDER = EventFactory.createArrayBacked(PostMain.class, invokers -> (renderer, state, poseStack, collector, cameraState) -> {
        for (var invoker : invokers) invoker.afterRendering(renderer, state, poseStack, collector, cameraState);
    });

    public static final Event<PreArm> BEFORE_ARM_RENDER = EventFactory.createArrayBacked(PreArm.class, invokers -> (player, renderer, poseStack, multiBufferSource, packedLight, arm, callback) -> {
        for (var invoker : invokers) invoker.beforeRendering(player, renderer, poseStack, multiBufferSource, packedLight, arm, callback);
    });

    public static final Event<PostArm> AFTER_ARM_RENDER = EventFactory.createArrayBacked(PostArm.class, invokers -> (player, renderer, poseStack, multiBufferSource, arm, packedLight) -> {
        for (var invoker : invokers) invoker.afterRendering(player, renderer, poseStack, multiBufferSource, arm, packedLight);
    });

    public interface PreMain {
        void beforeRendering(PlayerRenderer renderer, PlayerRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CancellableCallback callback);
    }

    public interface PostMain {
        void afterRendering(PlayerRenderer renderer, PlayerRenderState state, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);
    }

    public interface PreArm {
        void beforeRendering(PlayerRenderer renderer, Player player, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, HumanoidArm arm, CancellableCallback callback);
    }

    public interface PostArm {
        void afterRendering(PlayerRenderer renderer, Player player, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, HumanoidArm arm);
    }

    static {
        LivingEntityRenderEvents.BEFORE_RENDER.register((renderer, state, poseStack, bufferSource, packedLight, callback) -> {
            if (!(renderer instanceof PlayerRenderer avatarRenderer) || !(state instanceof PlayerRenderState avatarState)) return;

            BEFORE_RENDER.invoker().beforeRendering(avatarRenderer, avatarState, poseStack, bufferSource, packedLight, callback);
        });

        LivingEntityRenderEvents.AFTER_RENDER.register((renderer, state, poseStack, bufferSource, packedLight) -> {
            if (!(renderer instanceof PlayerRenderer avatarRenderer) || !(state instanceof PlayerRenderState avatarState)) return;

            AFTER_RENDER.invoker().afterRendering(avatarRenderer, avatarState, poseStack, bufferSource, packedLight);
        });
    }

}
