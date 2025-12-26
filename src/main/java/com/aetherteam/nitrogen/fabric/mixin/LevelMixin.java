package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.BlockEvents;
import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "updateNeighborsAt", at = @At("HEAD"))
    private void nitrogen_fabric$runUpdateEvent(BlockPos pos, Block block, CallbackInfo ci){
        var level = (Level) (Object) this;

        var isCancelled = new CancellableCallbackImpl(false);

        BlockEvents.NEIGHBOR_UPDATE.invoker().onNeighborUpdate(level, pos, level.getBlockState(pos), EnumSet.allOf(Direction.class), false, isCancelled);
    }
}
