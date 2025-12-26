package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.ActionEvents;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
    @Nullable
    private UseOnContext nitrogen_fabric$context = null;

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;evaluateNewBlockState(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
    private void nitrogen_fabric$cacheContext(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        this.nitrogen_fabric$context = context;
    }

    @WrapOperation(method = "evaluateNewBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/AxeItem;getStripped(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;"))
    private Optional<BlockState> nitrogen_fabric$onLogStripping(AxeItem instance, BlockState unstrippedState, Operation<Optional<BlockState>> original, @Local(argsOnly = true) Level level) {
        if (nitrogen_fabric$context != null) {
            ActionEvents.ON_BLOCK_STRIP.invoker().onStrip(level, unstrippedState, nitrogen_fabric$context.getItemInHand(), nitrogen_fabric$context);

            nitrogen_fabric$context = null;
        }

        return original.call(instance, unstrippedState);
    }
}
