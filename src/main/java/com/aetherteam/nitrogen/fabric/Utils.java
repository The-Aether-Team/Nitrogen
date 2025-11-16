package com.aetherteam.nitrogen.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

public class Utils {

    public static void markAndNotifyBlock(Level level, BlockPos pos, @Nullable LevelChunk levelchunk, BlockState oldState, BlockState newState, int flags, int recursionLeft) {
        Block block = newState.getBlock();
        BlockState blockstate1 = level.getBlockState(pos);

        if (blockstate1 == newState) {
            if (oldState != blockstate1) {
                level.setBlocksDirty(pos, oldState, blockstate1);
            }

            if ((flags & 2) != 0 && (!level.isClientSide || (flags & 4) == 0) && (level.isClientSide || levelchunk.getFullStatus() != null && levelchunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
                level.sendBlockUpdated(pos, oldState, newState, flags);
            }

            if ((flags & 1) != 0) {
                level.blockUpdated(pos, oldState.getBlock());
                if (!level.isClientSide && newState.hasAnalogOutputSignal()) {
                    level.updateNeighbourForOutputSignal(pos, block);
                }
            }

            if ((flags & 16) == 0 && recursionLeft > 0) {
                int i = flags & -34;
                oldState.updateIndirectNeighbourShapes(level, pos, i, recursionLeft - 1);
                newState.updateNeighbourShapes(level, pos, i, recursionLeft - 1);
                newState.updateIndirectNeighbourShapes(level, pos, i, recursionLeft - 1);
            }

            level.onBlockStateChange(pos, oldState, blockstate1);
        }
    }

    public static boolean isAreaLoaded(LevelReader reader, BlockPos center, int range) {
        return reader.hasChunksAt(center.offset(-range, -range, -range), center.offset(range, range, range));
    }
}
