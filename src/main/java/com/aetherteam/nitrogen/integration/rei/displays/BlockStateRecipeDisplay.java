package com.aetherteam.nitrogen.integration.rei.displays;

import com.aetherteam.nitrogen.integration.rei.REIUtils;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;

import java.util.List;

public class BlockStateRecipeDisplay<R extends AbstractBlockStateRecipe> extends BasicDisplay {
    private final CategoryIdentifier<?> categoryIdentifier;

    private final R recipe;

    public BlockStateRecipeDisplay(CategoryIdentifier<? extends BlockStateRecipeDisplay<R>> categoryIdentifier, R recipe) {
        super(getInputs(recipe), getOutputs(recipe));

        this.categoryIdentifier = categoryIdentifier;
        this.recipe = recipe;
    }

    private static List<EntryIngredient> getInputs(AbstractBlockStateRecipe recipe) {
        BlockPropertyPair[] pairs = recipe.getIngredient().getPairs();

        // Sets up input slots.
        return (pairs != null) ? REIUtils.toIngredientList(pairs) : List.of();
    }

    // Sets up output slots.
    private static List<EntryIngredient> getOutputs(AbstractBlockStateRecipe recipe) {
        return recipe.getIngredient().getPairs() != null ? REIUtils.toIngredientList(recipe.getResult()) : List.of();
    }

    public R getRecipe() {
        return this.recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return this.categoryIdentifier;
    }
}
