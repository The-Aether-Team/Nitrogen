package com.aetherteam.nitrogen.world.biomemodifier;

import com.aetherteam.nitrogen.Nitrogen;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class NitrogenBiomeModifierSerializers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Nitrogen.MODID);

    public static final DeferredHolder<Codec<? extends BiomeModifier>, Codec<AddMobChargeBiomeModifier>> ADD_MOB_CHARGE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_mob_charge", () -> AddMobChargeBiomeModifier.CODEC);
}
