package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.client.events.PlayerRenderEvents;
import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerRenderer.class)
public abstract class AvatarRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel>{

    public AvatarRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @WrapMethod(method = "renderLeftHand")
    private void nitrogen_fabric$onRenderLeftHand(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ResourceLocation skinTexture, boolean isSleeveVisible, Operation<Void> original){
        var player = Minecraft.getInstance().player;

        var callback = new CancellableCallbackImpl();

        PlayerRenderEvents.BEFORE_ARM_RENDER.invoker().beforeRendering((PlayerRenderer) (Object) this, player, poseStack, bufferSource, packedLight, HumanoidArm.LEFT, callback);

        if(callback.isCanceled()) return;

        original.call(poseStack, bufferSource, packedLight, skinTexture, isSleeveVisible);

        PlayerRenderEvents.AFTER_ARM_RENDER.invoker().afterRendering((PlayerRenderer) (Object) this, player, poseStack, bufferSource, packedLight, HumanoidArm.LEFT);
    }

    @WrapMethod(method = "renderRightHand")
    private void nitrogen_fabric$onRenderRightHand(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ResourceLocation skinTexture, boolean isSleeveVisible, Operation<Void> original){
        var player = Minecraft.getInstance().player;

        var callback = new CancellableCallbackImpl();

        PlayerRenderEvents.BEFORE_ARM_RENDER.invoker().beforeRendering((PlayerRenderer) (Object) this, player, poseStack, bufferSource, packedLight, HumanoidArm.RIGHT, callback);

        if(callback.isCanceled()) return;

        original.call(poseStack, bufferSource, packedLight, skinTexture, isSleeveVisible);

        PlayerRenderEvents.AFTER_ARM_RENDER.invoker().afterRendering((PlayerRenderer) (Object) this, player, poseStack, bufferSource, packedLight, HumanoidArm.RIGHT);
    }
}
