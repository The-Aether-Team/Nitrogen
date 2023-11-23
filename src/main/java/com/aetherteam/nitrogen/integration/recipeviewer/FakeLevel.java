package com.aetherteam.nitrogen.integration.recipeviewer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.lighting.LevelLightEngine;

/**
 * A fake level used for rendering.
 */
public abstract class FakeLevel implements BlockAndTintGetter {
    @Override
    public float getShade(Direction direction, boolean bl) {
        return 1.0F;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos pos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int i) {
        return 15;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            Holder<Biome> biome = Minecraft.getInstance().level.getBiome(pos);
            return colorResolver.getColor(biome.value(), 0, 0);
        } else {
            return -1;
        }
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }
}
