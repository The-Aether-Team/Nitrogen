/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.world.biome;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;

import java.util.function.Predicate;

public class NoneBiomeModification implements BiomeModificationData {
    public static final NoneBiomeModification INSTANCE = new NoneBiomeModification();

    @Override
    public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
        // NO OP
    }

    @Override
    public ModificationPhase phase() {
        return null;
    }

    @Override
    public Predicate<BiomeSelectionContext> selector() {
        return (ctx) -> false;
    }

    @Override
    public MapCodec<? extends BiomeModificationData> codec() {
        return BiomeModificationDataCodecs.NONE_BIOME_MODIFIER_TYPE.get();
    }
}
