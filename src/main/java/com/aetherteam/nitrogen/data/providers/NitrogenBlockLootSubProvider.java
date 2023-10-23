package com.aetherteam.nitrogen.data.providers;

import io.github.fabricators_of_create.porting_lib.data.ModdedBlockLootSubProvider;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public abstract class NitrogenBlockLootSubProvider extends ModdedBlockLootSubProvider {
    public NitrogenBlockLootSubProvider(Set<Item> items, FeatureFlagSet flags) {
        super(items, flags);
    }

    public void dropNone(Block block) {
        this.add(block, noDrop());
    }

    public void dropWithFortune(Block block, Item drop) {
        this.add(block, (result) -> this.createOreDrop(result, drop));
    }

    public LootTable.Builder droppingNameableBlockEntityTable(Block block) {
        return LootTable.lootTable().withPool(this.applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))))
        );
    }

    public static LootTable.Builder createForgeSilkTouchOrShearsDispatchTable(Block pBlock, LootPoolEntryContainer.Builder<?> pBuilder) {
        return createSelfDropDispatchTable(pBlock, MatchTool.toolMatches(ItemPredicate.Builder.item().of(Tags.Items.SHEARS)).or(HAS_SILK_TOUCH), pBuilder);
    }
}
