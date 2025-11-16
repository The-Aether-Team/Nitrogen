package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.BlockStateExtension;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public abstract class BlockStateMixin extends BlockBehaviour.BlockStateBase implements BlockStateExtension {
    protected BlockStateMixin(Block owner, Reference2ObjectArrayMap<Property<?>, Comparable<?>> values, MapCodec<BlockState> propertiesCodec) {
        super(owner, values, propertiesCodec);
    }

    @Override
    public boolean nitrogen_fabric$hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
        return this.getBlock().nitrogen_fabric$hidesNeighborFace(level, pos, ((BlockState)(Object)this), neighborState, dir);
    }

    @Override
    public boolean nitrogen_fabric$supportsExternalFaceHiding() {
        return this.getBlock().nitrogen_fabric$supportsExternalFaceHiding((BlockState)(Object)this);
    }

    @Override
    public Float nitrogen_fabric$getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion) {
        return this.getBlock().nitrogen_fabric$getExplosionResistance(((BlockState)(Object)this), level, pos, explosion);
    }

    @Override
    public Float nitrogen_fabric$getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return this.getBlock().nitrogen_fabric$getFriction(((BlockState)(Object)this), level, pos, entity);
    }
}
