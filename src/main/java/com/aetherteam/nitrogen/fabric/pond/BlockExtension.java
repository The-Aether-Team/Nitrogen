package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface BlockExtension {

    default boolean nitrogen_fabric$supportsExternalFaceHiding(BlockState state) {
        return true;
    }

    default boolean nitrogen_fabric$hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir) {
        return false;
    }

    @Nullable
    default Float nitrogen_fabric$getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return null;
    }

    @Nullable
    default Float nitrogen_fabric$getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return null;
    }

    default boolean nitrogen_fabric$onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        return false;
    }

    static <T> T throwUnimplementedException() {
        throw new IllegalStateException("Injected Interface method not implement!");
    }
}
