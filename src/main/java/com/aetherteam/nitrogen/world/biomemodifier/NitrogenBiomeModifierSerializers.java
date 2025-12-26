package com.aetherteam.nitrogen.world.biomemodifier;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.fabric.registries.DeferredHolder;
import com.aetherteam.nitrogen.fabric.registries.DeferredRegister;
import com.aetherteam.nitrogen.fabric.world.biome.BiomeModificationData;
import com.aetherteam.nitrogen.fabric.world.biome.BiomeModificationDataRegistries;
import com.mojang.serialization.MapCodec;
public class NitrogenBiomeModifierSerializers {
    public static final DeferredRegister<MapCodec<? extends BiomeModificationData>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(BiomeModificationDataRegistries.BIOME_MODIFIER_CODEC_KEY, Nitrogen.MODID);

    public static final DeferredHolder<MapCodec<? extends BiomeModificationData>, MapCodec<AddMobChargeBiomeModifier>> ADD_MOB_CHARGE_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("add_mob_charge", () -> AddMobChargeBiomeModifier.CODEC);
}
