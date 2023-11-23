package com.aetherteam.nitrogen.integration.rei.categories.block;

import com.aetherteam.nitrogen.integration.rei.BlockStateRenderer;
import com.aetherteam.nitrogen.integration.rei.FluidStateRenderer;
import com.aetherteam.nitrogen.integration.rei.categories.AbstractRecipeCategory;
import com.aetherteam.nitrogen.integration.rei.displays.BlockStateDisplay;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBlockStateRecipeCategory<T extends BlockStateDisplay> extends AbstractRecipeCategory<T> {
    public AbstractBlockStateRecipeCategory(String id, CategoryIdentifier<T> uid, Renderer background, Renderer icon) {
        super(id, uid, background, icon);
    }

    @Override
    public List<Widget> setupDisplay(T display, Rectangle bounds) {
        AbstractBlockStateRecipe recipe = display.getRecipe();
        BlockStateIngredient recipeIngredients = recipe.getIngredient();
        BlockPropertyPair recipeResult = recipe.getResult();
        BlockPropertyPair[] pairs = recipeIngredients.getPairs();
        List<Widget> widgets = new ArrayList<>(super.setupDisplay(display, bounds));
        if (pairs != null) {
            for (EntryStack<?> entry : display.getInputEntries().get(0)) {
                ClientEntryStacks.setTooltipProcessor(entry, (entryStack, tooltip) -> populateAdditionalInformation(display, tooltip));
                if (entry.getType() == VanillaEntryTypes.FLUID)
                    ClientEntryStacks.setRenderer(entry, new FluidStateRenderer());
                else if (entry.getType() == VanillaEntryTypes.ITEM)
                    ClientEntryStacks.setRenderer(entry, new BlockStateRenderer(pairs));
            }
            widgets.add(Widgets.createSlot(new Point(bounds.getX() + 8, bounds.getY() + 6)).markInput().entries(display.getInputEntries().get(0)));

            for (EntryStack<?> entry : display.getOutputEntries().get(0)) {
                if (entry.getType() == VanillaEntryTypes.FLUID)
                    ClientEntryStacks.setRenderer(entry, new FluidStateRenderer());
                else if (entry.getType() == VanillaEntryTypes.ITEM)
                    ClientEntryStacks.setRenderer(entry, new BlockStateRenderer(recipeResult));
            }
            widgets.add(Widgets.createSlot(new Point(bounds.getX() + 60, bounds.getY() + 6)).markOutput().entries(display.getOutputEntries().get(0)));
        }
        return widgets;
    }

    protected Tooltip populateAdditionalInformation(T display, Tooltip tooltip) {
        return tooltip;
    }
}
