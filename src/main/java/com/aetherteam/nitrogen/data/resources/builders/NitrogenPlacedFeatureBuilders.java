package com.aetherteam.nitrogen.data.resources.builders;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class NitrogenPlacedFeatureBuilders {
    /**
     * Copy of {@link net.minecraft.data.worldgen.placement.VegetationPlacements#treePlacement(PlacementModifier)}
     */
    public static List<PlacementModifier> treePlacement(PlacementModifier count) {
        return treePlacementBase(count).build();
    }

    /**
     * Based on {@link net.minecraft.data.worldgen.placement.VegetationPlacements#treePlacementBase(PlacementModifier)}
     */
    private static ImmutableList.Builder<PlacementModifier> treePlacementBase(PlacementModifier count) {
        return ImmutableList.<PlacementModifier>builder()
                .add(count)
                .add(SurfaceWaterDepthFilter.forMaxDepth(0))
                .add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR)
                .add(BiomeFilter.biome());
    }

    /**
     * Copy of {@link net.minecraft.data.worldgen.placement.OrePlacements#commonOrePlacement(int, PlacementModifier)}.
     */
    public static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }

    /**
     * Copy of {@link net.minecraft.data.worldgen.placement.OrePlacements#orePlacement(PlacementModifier, PlacementModifier)}.
     */
    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier heightRange) {
        return List.of(count, InSquarePlacement.spread(), heightRange, BiomeFilter.biome());
    }
}
