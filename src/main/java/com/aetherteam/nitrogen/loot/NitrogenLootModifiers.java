package com.aetherteam.nitrogen.loot;

import com.aetherteam.nitrogen.Nitrogen;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class NitrogenLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Nitrogen.MODID);

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<AddDungeonLootModifier>> ADD_DUNGEON_LOOT = GLOBAL_LOOT_MODIFIERS.register("add_dungeon_loot", () -> AddDungeonLootModifier.CODEC);
}
