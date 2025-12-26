package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import com.aetherteam.nitrogen.fabric.events.EntityEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;thunderHit(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LightningBolt;)V"))
    private void nitrogen_fabric$onEntityStruckEvent(Entity instance, ServerLevel level, LightningBolt lightning, Operation<Void> original) {
        var callback = new CancellableCallbackImpl();

        EntityEvents.STRUCK_BY_LIGHTNING.invoker().onStrike(instance, lightning, callback);

        if (!callback.isCanceled()) original.call(instance, level, lightning);
    }
}
