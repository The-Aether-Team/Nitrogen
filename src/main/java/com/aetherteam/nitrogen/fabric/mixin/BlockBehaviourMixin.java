package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.BlockExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @WrapOperation(method = "onExplosionHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;wasExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"))
    private void nitrogen_fabric$onExplosion(Block instance, Level level, BlockPos pos, Explosion explosion, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
        if (instance instanceof BlockExtension ext && ext.nitrogen_fabric$onBlockExploded(state, level, pos, explosion)) return;

        original.call(instance, level, pos, explosion);
    }
}
