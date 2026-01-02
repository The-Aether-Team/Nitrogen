package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.ProjectileEvents;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {
    @Definition(id = "isAlive", method = "Lnet/minecraft/world/entity/projectile/AbstractArrow;isAlive()Z")
    @Expression("this.isAlive()")
    @ModifyExpressionValue(method = "stepMoveAndHit", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 2))
    private boolean nitrogen$neoParityAdjustExpression(boolean original, @Local() EntityHitResult hitResult) {
        return original && hitResult.getType() != HitResult.Type.MISS;
    }

    @Inject(method = "stepMoveAndHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;position()Lnet/minecraft/world/phys/Vec3;"))
    private void nitrogen$setupStorageForDeflect(BlockHitResult hitResult, CallbackInfo ci,
                                                 @Share(namespace = "nitrogen", value = "deflection_instance") LocalRef<@Nullable ProjectileDeflection> deflection) {
        deflection.set(null); // TODO: MAY NOT BE NEEDED
    }

    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection nitrogen$projectileImpactEvent(AbstractArrow instance, HitResult hitResult, Operation<ProjectileDeflection> original,
                                                              @Share(namespace = "nitrogen", value = "deflection_instance") LocalRef<@Nullable ProjectileDeflection> deflection) {
        deflection.set(ProjectileEvents.adjustDeflection(instance, hitResult, ProjectileEvents.EMPTY_DEFLECTION, () -> original.call(instance, hitResult)));

        return deflection.get();
    }

    @Inject(method = "stepMoveAndHit",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private void nitrogen$preventImpulseFlagging(BlockHitResult hitResult, CallbackInfo ci, @Share(namespace = "nitrogen", value = "deflection_instance") LocalRef<@Nullable ProjectileDeflection> deflection) {
        if (deflection.get() == ProjectileEvents.EMPTY_DEFLECTION) ci.cancel();
    }

    // TODO: DOUBLE CHECK IF THE GIVEN INJECTION WORKS AS INTENDED BUT MAY CAUSE MORE ISSUES IF PEOPLE NEED TO HOOK INTO AFTER EFFECT BUT THYE COULD USE WRAP METHOD SO :SHRUG:
//    @Definition(id = "hasImpulse", field = "Lnet/minecraft/world/entity/projectile/AbstractArrow;hasImpulse:Z")
//    @Expression("this.hasImpulse = true")
//    @WrapOperation(method = "stepMoveAndHit", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
//    private void nitrogen$preventImpulseFlagging(AbstractArrow instance, boolean value, Operation<Void> original, @Share(namespace = "nitrogen", value = "deflection_instance") LocalRef<@Nullable ProjectileDeflection> deflection) {
//        if (deflection.get() != ProjectileEvents.EMPTY_DEFLECTION) original.call(instance, value);
//    }
}
