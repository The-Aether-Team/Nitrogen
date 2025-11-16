/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.world.biome;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import static net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import static net.minecraft.world.level.levelgen.GenerationStep.Carving;
import static net.minecraft.world.level.levelgen.GenerationStep.Decoration;

public class BiomeModificationImpls {
    /**
     * <p>Stock biome modifier that adds features to biomes. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:add_features", // required
     *   "biomes": "#namespace:your_biome_tag" // accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "features": "namespace:your_feature", // accepts a placed feature id, [list of placed feature ids], or #namespace:feature_tag
     *   "step": "underground_ores" // accepts a Decoration enum name
     * }
     * </pre>
     *
     * <p>Be wary of using this to add vanilla PlacedFeatures to biomes, as doing so may cause a feature cycle violation.</p>
     *
     * @param biomes   Biomes to add features to.
     * @param features PlacedFeatures to add to biomes.
     * @param step     Decoration step to run features in.
     */
    public record AddFeaturesBiomeModification(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Decoration step) implements BiomesModificationData {

        public static final MapCodec<AddFeaturesBiomeModification> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder
                        .group(
                                Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddFeaturesBiomeModification::biomes),
                                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(AddFeaturesBiomeModification::features),
                                Decoration.CODEC.fieldOf("step").forGetter(AddFeaturesBiomeModification::step))
                        .apply(builder, AddFeaturesBiomeModification::new));

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var settings = modificationContext.getGenerationSettings();

            this.features.forEach(holder -> settings.addFeature(this.step, holder.nitrogen_fabric$getKey()));
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.ADDITIONS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.ADD_FEATURES_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that removes features from biomes. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:remove_features", // required
     *   "biomes": "#namespace:your_biome_tag", // accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "features": "namespace:your_feature", // accepts a placed feature id, [list of placed feature ids], or #namespace:feature_tag
     *   "steps": "underground_ores" OR ["underground_ores", "vegetal_decoration"] // one or more decoration steps; optional field, defaults to all steps if not specified
     * }
     * </pre>
     *
     * @param biomes   Biomes to remove features from.
     * @param features PlacedFeatures to remove from biomes.
     * @param steps    Decoration steps to remove features from.
     */
    public record RemoveFeaturesBiomeModification(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features, Set<Decoration> steps) implements BiomesModificationData {
        public static final MapCodec<RemoveFeaturesBiomeModification> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder
                        .group(
                                Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveFeaturesBiomeModification::biomes),
                                PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(RemoveFeaturesBiomeModification::features),
                                Codec.<List<Decoration>, Decoration>either(Decoration.CODEC.listOf(), Decoration.CODEC).<Set<Decoration>>xmap(
                                        either -> either.map(Set::copyOf, Set::of), // convert list/singleton to set when decoding
                                        set -> set.size() == 1 ? Either.right(set.toArray(Decoration[]::new)[0]) : Either.left(List.copyOf(set))).optionalFieldOf("steps", EnumSet.allOf(Decoration.class)).forGetter(RemoveFeaturesBiomeModification::steps))
                        .apply(builder, RemoveFeaturesBiomeModification::new));
        /**
         * Creates a modifier that removes the given features from all decoration steps in the given biomes.
         *
         * @param biomes   Biomes to remove features from.
         * @param features PlacedFeatures to remove from biomes.
         */
        public static RemoveFeaturesBiomeModification allSteps(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features) {
            return new RemoveFeaturesBiomeModification(biomes, features, EnumSet.allOf(Decoration.class));
        }

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var generationSettings = modificationContext.getGenerationSettings();
            for (Decoration step : this.steps) {
                for (Holder<PlacedFeature> feature : features) {
                    generationSettings.removeFeature(step, feature.nitrogen_fabric$getKey());
                }
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.REMOVALS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.REMOVE_FEATURES_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that adds a mob spawn to a biome. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:add_spawns", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "spawners":
     *   {
     *     "type": "namespace:entity_type", // Type of mob to spawn
     *     "weight": 100, // int, spawn weighting
     *     "minCount": 1, // int, minimum pack size
     *     "maxCount": 4, // int, maximum pack size
     *   }
     * }
     * </pre>
     *
     * <p>Optionally accepts a list of spawner objects instead of a single spawner:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:add_spawns", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "spawners":
     *   [
     *     {
     *       "type": "namespace:entity_type", // Type of mob to spawn
     *       "weight": 100, // int, spawn weighting
     *       "minCount": 1, // int, minimum pack size
     *       "maxCount": 4, // int, maximum pack size
     *     },
     *     {
     *       // additional spawner object
     *     }
     *   ]
     * }
     * </pre>
     *
     * @param biomes   Biomes to add mob spawns to.
     * @param spawners List of Weighted SpawnerDatas specifying EntityType, weight, and pack size.
     */
    public record AddSpawnsBiomeModification(HolderSet<Biome> biomes, List<SpawnerData> spawners) implements BiomesModificationData {

        public static final MapCodec<AddSpawnsBiomeModification> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder
                        .group(
                                Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnsBiomeModification::biomes),
                                // Allow either a list or single spawner, attempting to decode the list format first.
                                // Uses the better EitherCodec that logs both errors if both formats fail to parse.
                                Codec.either(SpawnerData.CODEC.listOf(), SpawnerData.CODEC).xmap(
                                        either -> either.map(Function.identity(), List::of), // convert list/singleton to list when decoding
                                        list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list) // convert list to singleton/list when encoding
                                ).fieldOf("spawners").forGetter(AddSpawnsBiomeModification::spawners))
                        .apply(builder, AddSpawnsBiomeModification::new));

        /**
         * Convenience method for using a single {@linkplain SpawnerData}s.
         *
         * @param biomes  Biomes to add mob spawns to.
         * @param spawner SpawnerData specifying EntityTYpe, weight, and pack size.
         * @return AddSpawnsBiomeModifier that adds a single spawn entry to the specified biomes.
         */
        public static AddSpawnsBiomeModification singleSpawn(HolderSet<Biome> biomes, SpawnerData spawner) {
            return new AddSpawnsBiomeModification(biomes, List.of(spawner));
        }

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var spawns = modificationContext.getSpawnSettings();
            for (SpawnerData spawner : this.spawners) {
                EntityType<?> type = spawner.type;
                spawns.addSpawn(type.getCategory(), spawner);
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.ADDITIONS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that removes mob spawns from a biome. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:remove_spawns", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "entity_types": #namespace:entitytype_tag // Accepts an entity type, [list of entity types], or #namespace:entitytype_tag
     * }
     * </pre>
     *
     * @param biomes      Biomes to remove mob spawns from.
     * @param entityTypes EntityTypes to remove from spawn lists.
     */
    public record RemoveSpawnsBiomeModification(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements BiomesModificationData {

        public static final MapCodec<RemoveSpawnsBiomeModification> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder
                        .group(
                                Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveSpawnsBiomeModification::biomes),
                                RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(RemoveSpawnsBiomeModification::entityTypes))
                        .apply(builder, RemoveSpawnsBiomeModification::new));

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            for (MobCategory category : MobCategory.values()) {
                modificationContext.getSpawnSettings().removeSpawns(
                        (mobCategory, spawnerData) -> mobCategory.equals(category) && this.entityTypes.contains(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(spawnerData.type))
                );
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.REMOVALS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.REMOVE_SPAWNS_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that adds carvers to biomes (from the configured_carver json registry). Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:add_carvers", // required
     *   "biomes": "#namespace:your_biome_tag" // accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "carvers": "namespace:your_carver", // accepts a configured carver id, [list of configured carver ids], or #namespace:carver_tag
     *   "step": "air" // Carving step, can be "air" or "liquid"
     * }
     * </pre>
     *
     * @param biomes  Biomes to add features to.
     * @param carvers ConfiguredWorldCarvers to add to biomes.
     */
    public record AddCarversBiomeModification(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, Carving step) implements BiomesModificationData {
        public static final MapCodec<AddCarversBiomeModification> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddCarversBiomeModification::biomes),
                ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(AddCarversBiomeModification::carvers),
                Carving.CODEC.fieldOf("step").forGetter(AddCarversBiomeModification::step)
        ).apply(builder, AddCarversBiomeModification::new));

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var generationSettings = modificationContext.getGenerationSettings();
            for (Holder<ConfiguredWorldCarver<?>> carver : this.carvers) {
                generationSettings.removeCarver(step, carver.nitrogen_fabric$getKey());
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.ADDITIONS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.ADD_CARVERS_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that removes carvers from biomes. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:remove_carvers", // required
     *   "biomes": "#namespace:your_biome_tag", // accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "carvers": "namespace:your_carver", // accepts a configured carver id, [list of configured carver ids], or #namespace:carver_tag
     *   "steps": "air" OR "liquid" OR ["air", "liquid"] // one or more carving steps; optional field, defaults to all steps if not specified
     * }
     * </pre>
     *
     * @param biomes  Biomes to remove carvers from.
     * @param carvers ConfiguredWorldCarvers to remove from biomes.
     */
    public record RemoveCarversBiomeModification(HolderSet<Biome> biomes, HolderSet<ConfiguredWorldCarver<?>> carvers, Set<Carving> steps) implements BiomesModificationData {

        public static final MapCodec<RemoveCarversBiomeModification> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveCarversBiomeModification::biomes),
                        ConfiguredWorldCarver.LIST_CODEC.fieldOf("carvers").forGetter(RemoveCarversBiomeModification::carvers),
                        Codec.either(Carving.CODEC.listOf(), Carving.CODEC).xmap(
                                either -> either.map(Set::copyOf, Set::of),
                                set -> set.size() == 1 ? Either.right(set.toArray(Carving[]::new)[0]) : Either.left(List.copyOf(set))).optionalFieldOf("steps", EnumSet.allOf(Carving.class)).forGetter(RemoveCarversBiomeModification::steps))
                .apply(builder, RemoveCarversBiomeModification::new));

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var generationSettings = modificationContext.getGenerationSettings();
            for (Carving step : steps) {
                for (Holder<ConfiguredWorldCarver<?>> carver : this.carvers) {
                    generationSettings.removeCarver(step, carver.nitrogen_fabric$getKey());
                }
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.REMOVALS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.REMOVE_CARVERS_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier at adds spawn costs to a biome. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:add_spawn_costs", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "entity_types": #namespace:entitytype_tag, // Accepts an entity type, [list of entity types], or #namespace:entitytype_tag
     *   "spawn_cost": {
     *     "energy_budget": 1.0, // double
     *     "charge": 1.0 // double
     *   }
     * }
     * </pre>
     *
     * @param biomes      Biomes to add spawn costs to.
     * @param entityTypes EntityTypes to add spawn costs for.
     * @param spawnCost   MobSpawnCost to add for those entity types.
     */
    public record AddSpawnCostsBiomeModification(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes, MobSpawnCost spawnCost) implements BiomesModificationData {

        public static final MapCodec<AddSpawnCostsBiomeModification> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddSpawnCostsBiomeModification::biomes),
                RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(AddSpawnCostsBiomeModification::entityTypes),
                MobSpawnCost.CODEC.fieldOf("spawn_cost").forGetter(AddSpawnCostsBiomeModification::spawnCost)
        ).apply(builder, AddSpawnCostsBiomeModification::new));

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var spawnBuilder = modificationContext.getSpawnSettings();
            for (var entityType : entityTypes) {
                spawnBuilder.setSpawnCost(entityType.value(), spawnCost.charge(), spawnCost.energyBudget());
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.ADDITIONS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.ADD_SPAWN_COSTS_BIOME_MODIFIER_TYPE.get();
        }
    }

    /**
     * <p>Stock biome modifier that removes mob spawn costs from a biome. Has the following json format:</p>
     *
     * <pre>
     * {
     *   "type": "neoforge:remove_spawn_costs", // Required
     *   "biomes": "#namespace:biome_tag", // Accepts a biome id, [list of biome ids], or #namespace:biome_tag
     *   "entity_types": #namespace:entitytype_tag // Accepts an entity type, [list of entity types], or #namespace:entitytype_tag
     * }
     * </pre>
     *
     * @param biomes      Biomes to remove mob spawns from.
     * @param entityTypes EntityTypes to remove from spawn lists.
     */
    public record RemoveSpawnCostsBiomeModification(HolderSet<Biome> biomes, HolderSet<EntityType<?>> entityTypes) implements BiomesModificationData {

        public static final MapCodec<RemoveSpawnCostsBiomeModification> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveSpawnCostsBiomeModification::biomes),
                RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(RemoveSpawnCostsBiomeModification::entityTypes)
        ).apply(builder, RemoveSpawnCostsBiomeModification::new));

        @Override
        public void modify(BiomeSelectionContext selectionContext, BiomeModificationContext modificationContext) {
            var spawnBuilder = modificationContext.getSpawnSettings();
            for (var entityType : entityTypes) {
                spawnBuilder.clearSpawnCost(entityType.value());
            }
        }

        @Override
        public ModificationPhase phase() {
            return ModificationPhase.REMOVALS;
        }

        @Override
        public MapCodec<? extends BiomeModificationData> codec() {
            return BiomeModificationDataCodecs.REMOVE_SPAWN_COSTS_BIOME_MODIFIER_TYPE.get();
        }
    }
}
