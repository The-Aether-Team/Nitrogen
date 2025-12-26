/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.world.biome;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

public interface BiomesModificationData extends BiomeModificationData {

    HolderSet<Biome> biomes();

    @Override
    default Predicate<BiomeSelectionContext> selector() {
        return ctx -> biomes().contains(ctx.getBiomeRegistryEntry());
    }
}
