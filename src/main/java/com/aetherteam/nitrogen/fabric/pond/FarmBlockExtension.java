package com.aetherteam.nitrogen.fabric.pond;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface FarmBlockExtension {
    TriState nitrogen_fabric$canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction direction, BlockState plantState);
}
