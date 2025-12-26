package com.aetherteam.nitrogen.world.foliageplacer;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class NitrogenFoliagePlacerTypes {

    public static final Holder<FoliagePlacerType<?>> AETHER_PINE_FOLIAGE_PLACER = Registry.registerForHolder(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Nitrogen.id("aether_pine_foliage_placer"), new FoliagePlacerType<>(AetherPineFoliagePlacer.CODEC));
    public static final Holder<FoliagePlacerType<?>> HOOKED_FOLIAGE_PLACER = Registry.registerForHolder(BuiltInRegistries.FOLIAGE_PLACER_TYPE, Nitrogen.id("hooked_foliage_placer"), new FoliagePlacerType<>(HookedFoliagePlacer.CODEC));

    public static void init() {}
}
