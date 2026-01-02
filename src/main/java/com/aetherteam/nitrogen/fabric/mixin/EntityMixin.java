package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.entity.IEntityWithComplexSpawn;
import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import com.aetherteam.nitrogen.fabric.events.EntityEvents;
import com.aetherteam.nitrogen.fabric.events.EntityTickEvents;
import com.aetherteam.nitrogen.fabric.network.payload.AdvancedAddEntityPayload;
import com.aetherteam.nitrogen.fabric.pond.EntityExtension;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtension {
    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @WrapOperation(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    private void nitrogen_fabric$entityTickEvents(Entity instance, Operation<Void> original) {
        var shouldCancelEvent = new MutableBoolean(false);

        EntityTickEvents.BEFORE.invoker().beforeTick(instance, shouldCancelEvent);

        if (shouldCancelEvent.getValue()) return;

        original.call(instance);

        EntityTickEvents.AFTER.invoker().afterTick(instance);
    }

    @Inject(method = "teleport", at = @At("HEAD"))
    private void nitrogen_fabric$beforeDimensionChange(TeleportTransition transition, CallbackInfoReturnable<Entity> cir) {
        EntityEvents.BEFORE_DIMENSION_CHANGE.invoker().beforeChange((Entity) (Object) this, transition.newLevel().dimension());
    }

    @Override
    public boolean nitrogen_fabric$isInFluidType() {
        for (var value : this.fluidHeight.values()) {
            if (value > 0.0) return true;
        }

        return false;
    }

    @Definition(id = "vehicle", field = "Lnet/minecraft/world/entity/Entity;vehicle:Lnet/minecraft/world/entity/Entity;")
    @Expression("this.vehicle = null")
    @Inject(method = "removeVehicle", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.BEFORE), cancellable = true)
    private void nitrogen_fabric$entityMountEvent_remove(CallbackInfo ci) {
        if (shouldPerformAction(((Entity) (Object) this), false)) return;

        ci.cancel();
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;canRide(Lnet/minecraft/world/entity/Entity;)Z"), cancellable = true)
    private void nitrogen_fabric$entityMountEvent_add(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (shouldPerformAction(((Entity) (Object) this), false)) return;

        cir.setReturnValue(false);
    }

    @Unique
    private static boolean shouldPerformAction(Entity entityMounting, boolean isDismounting) {
        var callback = new CancellableCallbackImpl();

        EntityEvents.ENTITY_MOUNT.invoker().onMount(entityMounting, entityMounting.getVehicle(), isDismounting, callback);

        if (!callback.isCanceled()) return true;

        entityMounting.absSnapTo(entityMounting.getX(), entityMounting.getY(), entityMounting.getZ(), entityMounting.yRotO, entityMounting.xRotO);

        return false;
    }

    @Override
    public void nitrogen_fabric$sendPairingData(ServerPlayer serverPlayer, Consumer<CustomPacketPayload> bundleBuilder) {
        if (this instanceof IEntityWithComplexSpawn) {
            bundleBuilder.accept(new AdvancedAddEntityPayload((Entity)(Object) this));
        }
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void nitrogen_fabric$captureDroppedStack(ServerLevel level, ItemStack stack, Vec3 offset, CallbackInfoReturnable<ItemEntity> cir, @Local() ItemEntity itemEntity) {
        EntityEvents.ON_SPAWNED_ITEM_STACK.invoker().onSpawn((Entity) (Object) this, stack, itemEntity);
    }
}
