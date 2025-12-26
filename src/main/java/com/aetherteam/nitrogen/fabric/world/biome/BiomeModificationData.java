/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import java.util.function.Function;
import java.util.function.Predicate;

public interface BiomeModificationData {
    Codec<BiomeModificationData> DIRECT_CODEC = BiomeModificationDataRegistries.BIOME_MODIFIER_CODEC.byNameCodec()
            .dispatch(BiomeModificationData::codec, Function.identity());

    void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext);

    ModificationPhase phase();

    Predicate<BiomeSelectionContext> selector();

    MapCodec<? extends BiomeModificationData> codec();
}
