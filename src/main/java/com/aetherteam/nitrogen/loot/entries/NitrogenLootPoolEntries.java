package com.aetherteam.nitrogen.loot.entries;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NitrogenLootPoolEntries {
    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, Nitrogen.MODID);

    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> RANDOM = LOOT_POOL_ENTRY_TYPES.register("random", () -> new LootPoolEntryType(CompositeEntryBase.createCodec(RandomEntry::new)));
}
