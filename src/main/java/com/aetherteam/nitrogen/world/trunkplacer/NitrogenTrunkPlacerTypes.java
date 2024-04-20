package com.aetherteam.nitrogen.world.trunkplacer;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NitrogenTrunkPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, Nitrogen.MODID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<HookedTrunkPlacer>> HOOKED_TRUNK_PLACER = TRUNK_PLACERS.register("hooked_trunk_placer", () -> new TrunkPlacerType<>(HookedTrunkPlacer.CODEC));
}
