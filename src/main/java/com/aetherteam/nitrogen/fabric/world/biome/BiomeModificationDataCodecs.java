/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.world.biome;

import com.aetherteam.nitrogen.fabric.registries.DeferredHolder;
import com.aetherteam.nitrogen.fabric.registries.DeferredRegister;
import com.mojang.serialization.MapCodec;

import static com.aetherteam.nitrogen.fabric.world.biome.BiomeModificationImpls.*;

public class BiomeModificationDataCodecs {
    public static final DeferredRegister<MapCodec<? extends BiomeModificationData>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(BiomeModificationDataRegistries.BIOME_MODIFIER_CODEC_KEY, "neoforge");

    /**
     * Noop biome modifier. Can be used in a biome modifier json with "type": "neoforge:none".
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<NoneBiomeModification>> NONE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("none", () -> MapCodec.unit(NoneBiomeModification.INSTANCE));

    /**
     * Stock biome modifier for adding features to biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<AddFeaturesBiomeModification>> ADD_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_features", () -> AddFeaturesBiomeModification.CODEC);

    /**
     * Stock biome modifier for removing features from biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<RemoveFeaturesBiomeModification>> REMOVE_FEATURES_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_features", () -> RemoveFeaturesBiomeModification.CODEC);

    /**
     * Stock biome modifier for adding mob spawns to biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<AddSpawnsBiomeModification>> ADD_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_spawns", () -> AddSpawnsBiomeModification.CODEC);

    /**
     * Stock biome modifier for removing mob spawns from biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<RemoveSpawnsBiomeModification>> REMOVE_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_spawns", () -> RemoveSpawnsBiomeModification.CODEC);

    /**
     * Stock biome modifier for adding carvers to biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<AddCarversBiomeModification>> ADD_CARVERS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_carvers", () -> AddCarversBiomeModification.CODEC);

    /**
     * Stock biome modifier for removing carvers from biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<RemoveCarversBiomeModification>> REMOVE_CARVERS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_carvers", () -> RemoveCarversBiomeModification.CODEC);

    /**
     * Stock biome modifier for adding mob spawn costs to biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<AddSpawnCostsBiomeModification>> ADD_SPAWN_COSTS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_spawn_costs", () -> AddSpawnCostsBiomeModification.CODEC);

    /**
     * Stock biome modifier for removing mob spawn costs from biomes.
     */
    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<RemoveSpawnCostsBiomeModification>> REMOVE_SPAWN_COSTS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("remove_spawn_costs", () -> RemoveSpawnCostsBiomeModification.CODEC);
}
