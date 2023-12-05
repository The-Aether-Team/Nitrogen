package com.aetherteam.nitrogen.integration.recipeviewer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * A fake level used for rendering.
 */
public class FakeBlockLevel extends FakeLevel {
    private final BlockState blockState;

    public FakeBlockLevel(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(BlockPos.ZERO)) {
            return this.blockState;
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }
}
