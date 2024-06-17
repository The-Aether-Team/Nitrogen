package com.aetherteam.nitrogen.world.biomemodifier;

import com.aetherteam.nitrogen.Nitrogen;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class NitrogenBiomeModifierSerializers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Nitrogen.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<AddMobChargeBiomeModifier>> ADD_MOB_CHARGE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_mob_charge", () -> AddMobChargeBiomeModifier.CODEC);
}
