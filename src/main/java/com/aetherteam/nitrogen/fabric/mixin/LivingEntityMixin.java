package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.*;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    @Nullable
    protected EntityReference<Player> lastHurtByPlayer;

    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "getVisibilityPercent", at = @At("RETURN"))
    private double nitrogen_fabric$adjustEntityVisibility(double original, @Local(argsOnly = true) @Nullable Entity lookingEntity) {
        var value = new MutableDouble(original);

        LivingEntityEvents.ON_VISIBILITY_CALCULATED.invoker().adjustVisibility((LivingEntity) (Object) this, lookingEntity, value);

        return value.getValue();
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void nitrogen_fabric$adjustFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true, ordinal = 0) LocalDoubleRef fallDistanceRef, @Local(argsOnly = true, ordinal = 0) LocalFloatRef multiplierRef) {
        var helper = new FallHelper(fallDistance, damageMultiplier);

        LivingEntityEvents.ON_FALL.invoker().onFall((LivingEntity) (Object) this, helper);

        if (helper.isCanceled()) cir.setReturnValue(false);

        fallDistanceRef.set(helper.getDistance());
        multiplierRef.set(helper.getDamageMultiplier());
    }

    @WrapOperation(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElse(Ljava/lang/Object;)Ljava/lang/Object;"))
    private <T> T nitrogen_fabric$alwaysReturnTrue(Optional instance, T other, Operation<T> original, @Local(argsOnly = true) DamageSource damageSource, @Local(ordinal = 0) BlocksAttacks blocksAttacks, @Share(namespace = "nitrogen", value = "originalBlocked") LocalBooleanRef ref) {
        original.call(instance, other);

        ref.set(!blocksAttacks.bypassedBy().map(damageSource::is).orElse(false));

        return (T) (Boolean) false;
    }

    @WrapOperation(method = "applyItemBlocking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BlocksAttacks;hurtBlockingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;F)V"))
    private void nitrogen_fabric$checkIfBlock(BlocksAttacks instance, Level level, ItemStack stack, LivingEntity entity, InteractionHand hand, float blockedDamage, Operation<Void> original,
                                              @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float damageAmount, @Share(namespace = "nitrogen", value = "originalBlocked") LocalBooleanRef ref,
                                              @Cancellable CallbackInfoReturnable<Float> cir) {
        var helper = new ShieldBlockHelper(damageSource, damageAmount, blockedDamage, ref.get());

        LivingEntityEvents.ON_SHIELD_BLOCK.invoker().onBlock((LivingEntity) (Object) this, helper);

        if (!helper.isCanceled()) {
            cir.setReturnValue(0.0f);

            return;
        }

        blockedDamage = helper.blockedDamage();

        original.call(instance, level, stack, entity, hand, blockedDamage);
    }

    @WrapOperation(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
    private void nitrogen_fabric$adjustExperienceAmount(ServerLevel level, Vec3 pos, int amount, Operation<Void> original) {
        var helper = new ExperienceDropHelper(amount);

        LivingEntityEvents.ON_EXPERIENCE_DROP.invoker().onExperienceDrop((LivingEntity) (Object) this, this.lastHurtByPlayer.getEntity(level, Player.class), helper);

        original.call(level, pos, helper.getFinalExperienceAmount());
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void nitrogen_fabric$onSwing(InteractionHand hand, boolean updateSelf, CallbackInfo ci) {
        var stack = this.getItemInHand(hand);

        var callback = new CancellableCallbackImpl(false);

        LivingEntityEvents.ON_SWING.invoker().onSwing(stack, (LivingEntity)(Object) this, hand, callback);

        if (callback.isCanceled()) ci.cancel();
    }

    @WrapOperation(
        method = {"addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", "forceAddEffect"},
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z")
    )
    private boolean nitrogen_fabric$adjustIfEffectIsApplicable(LivingEntity instance, MobEffectInstance effectInstance, Operation<Boolean> original) {
        var result = LivingEntityEvents.ON_EFFECT.invoker().onEffect(instance, effectInstance, TriState.DEFAULT);

        return result != TriState.DEFAULT ? result.get() : original.call(instance, effectInstance);
    }

    @WrapOperation(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float nitrogen_fabric$adjustDamageAmount(LivingEntity instance, DamageSource damageSource, float damageAmount, Operation<Float> original) {
        var newDamage = new MutableFloat(original.call(instance, damageSource, damageAmount));

        LivingEntityEvents.ON_DAMAGE.invoker().modifyDamage((LivingEntity) (Object) this, damageSource, damageAmount, newDamage);

        return newDamage.getValue();
    }
}
