package com.aetherteam.nitrogen.fabric.client.events;

import com.aetherteam.nitrogen.fabric.events.CancellableCallback;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class PlayerRenderEvents {

    public static final Event<PreMain> BEFORE_RENDER = EventFactory.createArrayBacked(PreMain.class, invokers -> (player, renderer, partialTick, poseStack, multiBufferSource, packedLight, callback) -> {
        for (var invoker : invokers) invoker.beforeRendering(player, renderer, partialTick, poseStack, multiBufferSource, packedLight, callback);
    });

    public static final Event<PostMain> AFTER_RENDER = EventFactory.createArrayBacked(PostMain.class, invokers -> (player, renderer, partialTick, poseStack, multiBufferSource, packedLight) -> {
        for (var invoker : invokers) invoker.afterRendering(player, renderer, partialTick, poseStack, multiBufferSource, packedLight);
    });

    public static final Event<PreArm> BEFORE_ARM_RENDER = EventFactory.createArrayBacked(PreArm.class, invokers -> (player, renderer, partialTick, poseStack, multiBufferSource, packedLight, arm, callback) -> {
        for (var invoker : invokers) invoker.beforeRendering(player, renderer, partialTick, poseStack, multiBufferSource, packedLight, arm, callback);
    });

    public static final Event<PostArm> AFTER_ARM_RENDER = EventFactory.createArrayBacked(PostArm.class, invokers -> (player, renderer, partialTick, poseStack, multiBufferSource, arm, packedLight) -> {
        for (var invoker : invokers) invoker.afterRendering(player, renderer, partialTick, poseStack, multiBufferSource, arm, packedLight);
    });

    public interface PreMain {
        void beforeRendering(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, CancellableCallback callback);
    }

    public interface PostMain {
        void afterRendering(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight);
    }

    public interface PreArm {
        void beforeRendering(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, HumanoidArm arm, CancellableCallback callback);
    }

    public interface PostArm {
        void afterRendering(Player player, PlayerRenderer renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, HumanoidArm arm);
    }

}
