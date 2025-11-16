/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.world.biome;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BiomeModificationDataRegistries {
    public static final ResourceKey<Registry<MapCodec<? extends BiomeModificationData>>> BIOME_MODIFIER_CODEC_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("neoforge", "biome_modifier_codec"));
    public static final Registry<MapCodec<? extends BiomeModificationData>> BIOME_MODIFIER_CODEC = FabricRegistryBuilder.createSimple(BIOME_MODIFIER_CODEC_KEY).buildAndRegister();

    public static final ResourceKey<Registry<BiomeModificationData>> BIOME_MODIFIERS_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("neoforge", "biome_modifier"));

    public static void init() {
        DynamicRegistries.registerSynced(BIOME_MODIFIERS_KEY, BiomeModificationData.DIRECT_CODEC);
        BiomeModificationDataCodecs.BIOME_MODIFIER_SERIALIZERS.addEntriesToRegistry();
    }
}
