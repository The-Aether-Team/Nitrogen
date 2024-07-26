package com.aetherteam.nitrogen.loot;

import com.aetherteam.nitrogen.Nitrogen;
import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.PortingLibLoot;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

public class NitrogenLootModifiers {
    public static final LazyRegistrar<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = LazyRegistrar.create(PortingLibLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS_KEY, Nitrogen.MODID);

    public static final RegistryObject<Codec<AddDungeonLootModifier>> ADD_DUNGEON_LOOT = GLOBAL_LOOT_MODIFIERS.register("add_dungeon_loot", () -> AddDungeonLootModifier.CODEC);
}
