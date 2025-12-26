package com.aetherteam.nitrogen.loot.modifiers;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.fabric.loot.IGlobalLootModifier;
import com.aetherteam.nitrogen.fabric.loot.LootTableModificationAPI;
import com.aetherteam.nitrogen.fabric.registries.DeferredHolder;
import com.aetherteam.nitrogen.fabric.registries.DeferredRegister;
import com.mojang.serialization.MapCodec;

public class NitrogenLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = DeferredRegister.create(LootTableModificationAPI.GLOBAL_LOOT_MODIFIER_CODEC_KEY, Nitrogen.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddDungeonLootModifier>> ADD_DUNGEON_LOOT = GLOBAL_LOOT_MODIFIERS.register("add_dungeon_loot", () -> AddDungeonLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddEntityDropsModifier>> ADD_ENTITY_DROPS = GLOBAL_LOOT_MODIFIERS.register("add_entity_drops", () -> AddEntityDropsModifier.CODEC);
}
