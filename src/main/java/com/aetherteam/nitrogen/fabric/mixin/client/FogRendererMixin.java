package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.client.events.FogAdjustmentHelper;
import com.aetherteam.nitrogen.fabric.client.events.FogColorHelper;
import com.aetherteam.nitrogen.fabric.client.events.FogEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @WrapOperation(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;isApplicable(Lnet/minecraft/world/level/material/FogType;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean nitrogen_fabric$captureFogEnv(FogEnvironment instance, FogType fogType, Entity entity, Operation<Boolean> original, @Share(namespace = "nitrogen", value = "chosenEnv") LocalRef<FogEnvironment> ref) {
        var bl = original.call(instance, fogType, entity);

        if (bl) {
            ref.set(instance);
        }

        return bl;
    }

    @Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getDevice()Lcom/mojang/blaze3d/systems/GpuDevice;"))
    private static void nitrogen_fabric$onFogRenderering(Camera camera, int renderDistance, boolean isFoggy, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level, CallbackInfoReturnable<Vector4f> cir,
                                                         @Share(namespace = "nitrogen", value = "chosenEnv") LocalRef<FogEnvironment> ref,
                                                         @Local FogType fogType, @Local FogData fogData, @Local(ordinal = 1) float partialTick){
        var helper = new FogAdjustmentHelper(camera, partialTick, ref.get(), fogType, fogData);

        FogEvents.ON_FOG_RENDER.invoker().onRenderer(helper);
    }

    @WrapOperation(method = "computeFogColor", at = @At(value = "NEW", target = "(FFFF)Lorg/joml/Vector4f;"))
    private static Vector4f nitrogen_fabric$adjustFogColor(float r, float g, float b, float a, Operation<Vector4f> original,
                                                           @Local(argsOnly = true) Camera activeRenderInfo, @Local(argsOnly = true, ordinal = 0) float partialTicks){
        var helper = new FogColorHelper(activeRenderInfo, partialTicks, r, g, b);

        FogEvents.ON_FOG_COLORING.invoker().onColor(helper);

        r = helper.getRed();
        g = helper.getGreen();
        b = helper.getBlue();

        return original.call(r, g, b, a);
    }
}
