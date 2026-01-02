package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.FarmBlockExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VegetationBlock.class)
public abstract class VegetationBlockMixin {
    @WrapOperation(method = "canSurvive", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/VegetationBlock;mayPlaceOn(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean nitrogen_fabric$checkCropSurvival(VegetationBlock instance, BlockState state, BlockGetter level, BlockPos pos, Operation<Boolean> original, @Local(argsOnly = true) BlockPos plantPos, @Local(argsOnly = true) BlockState plantState) {
        if (state.getBlock() instanceof FarmBlockExtension extension) {
            var result = extension.nitrogen_fabric$canSustainPlant(state, level, pos, Direction.DOWN, plantState);

            if (result != TriState.DEFAULT) return result.get();
        }

        return original.call(instance, state, level, pos);
    }
}
