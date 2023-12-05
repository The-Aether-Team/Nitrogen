package com.aetherteam.nitrogen.integration.recipeviewer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * A fake level used for rendering.
 */
public class FakeFluidLevel extends FakeLevel {
    private final FluidState fluidState;

    public FakeFluidLevel(FluidState fluidState) {
        this.fluidState = fluidState;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(BlockPos.ZERO)) {
            return this.fluidState.createLegacyBlock();
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (pos.equals(BlockPos.ZERO)) {
            return this.fluidState;
        } else {
            return Fluids.EMPTY.defaultFluidState();
        }
    }
}
