package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import com.aetherteam.nitrogen.fabric.events.LivingEntityEvents;
import com.aetherteam.nitrogen.fabric.events.PlayerEvents;
import com.aetherteam.nitrogen.fabric.events.PlayerTickEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    public abstract void resetAttackStrengthTicker();

    @WrapMethod(method = "tick")
    private void nitrogen_fabric$playerTickEvents(Operation<Void> original) {
        PlayerTickEvents.BEFORE.invoker().beforeTick((Player) (Object) this);
        original.call();
        PlayerTickEvents.AFTER.invoker().afterTick((Player) (Object) this);
    }

    //-- These combine to prevent some issues with attack strength calculations in entire attack method being needed before reset
    @WrapOperation(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"))
    private void nitrogen_fabric$preventResetHere(Player instance, Operation<Void> original) {
        // NO-OP
    }
    @Inject(method = "attack", at = @At(value = "RETURN", ordinal = 3))
    private void nitrogen_fabric$callResetAtEnd(Entity target, CallbackInfo ci) {
        this.resetAttackStrengthTicker();
    }
    //--

    @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
    private float nitrogen_fabric$modifySpeed(float value, @Local(argsOnly = true) BlockState state) {
        var speed = new MutableFloat(value);
        var callback = new CancellableCallbackImpl();

        PlayerEvents.ON_BLOCK_DESTROY.invoker().onDestroy((Player) (Object) this, state, speed, callback);

        return callback.isCanceled() ? -1 : speed.getValue();
    }

    @WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float nitrogen_fabric$adjustDamageAmount(Player instance, DamageSource damageSource, float damageAmount, Operation<Float> original) {
        var newDamage = new MutableFloat(original.call(instance, damageSource, damageAmount));

        LivingEntityEvents.ON_DAMAGE.invoker().modifyDamage((LivingEntity) (Object) this, damageSource, damageAmount, newDamage);

        return newDamage.getValue();
    }
}
