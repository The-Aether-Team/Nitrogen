package com.aetherteam.nitrogen.integration.rei.displays;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;

public class FuelDisplay extends BasicDisplay {
    private final CategoryIdentifier<FuelDisplay> id;
    private final int burnTime;
    private final Block usage;

    public FuelDisplay(CategoryIdentifier<FuelDisplay> id, Collection<ItemStack> input, int burnTime, Block usage) {
        super(List.of(EntryIngredients.ofItemStacks(input)), List.of());
        this.id = id;
        this.burnTime = burnTime;
        this.usage = usage;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public Block getUsage() {
        return usage;
    }

    @Override
    public CategoryIdentifier<FuelDisplay> getCategoryIdentifier() {
        return this.id;
    }
}
