package com.aetherteam.nitrogen.data.resources.builders;

//import net.minecraft.data.worldgen.BootstapContext;
//import net.minecraft.data.worldgen.features.FeatureUtils;
//import net.minecraft.data.worldgen.placement.PlacementUtils;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
//import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
//import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
//
//public class NitrogenConfiguredFeatureBuilders {
//    /**
//     * [CODE COPY] - {@link net.minecraft.data.worldgen.features.VegetationFeatures#grassPatch(BlockStateProvider, int)}.
//     */
//    public static RandomPatchConfiguration grassPatch(BlockStateProvider block, int tries) {
//        return FeatureUtils.simpleRandomPatchConfiguration(tries, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(block)));
//    }
//
//    /**
//     * [CODE COPY] - {@link net.minecraft.data.worldgen.features.VegetationFeatures#bootstrap(BootstapContext)}.<br><br>
//     * Based on the registration entry for {@link net.minecraft.data.worldgen.features.VegetationFeatures#PATCH_TALL_GRASS}
//     */
//    public static RandomPatchConfiguration tallGrassPatch(BlockStateProvider block) {
//        return FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(block));
//    }
//}
