package com.aetherteam.nitrogen.world.trunkplacer;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class NitrogenTrunkPlacerTypes {
    public static final Holder<TrunkPlacerType<?>> HOOKED_TRUNK_PLACER = Registry.registerForHolder(BuiltInRegistries.TRUNK_PLACER_TYPE, Nitrogen.id("hooked_trunk_placer"), new TrunkPlacerType<>(HookedTrunkPlacer.CODEC));

    public static void init(){};
}
