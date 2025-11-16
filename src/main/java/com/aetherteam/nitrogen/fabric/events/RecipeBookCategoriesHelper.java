package com.aetherteam.nitrogen.fabric.events;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RecipeBookCategoriesHelper {

    public static final RecipeBookCategoriesHelper INSTANCE = new RecipeBookCategoriesHelper();

    public final Map<RecipeBookCategories, ImmutableList<RecipeBookCategories>> aggregateCategories = new HashMap<>();
    public final Map<RecipeBookType, ImmutableList<RecipeBookCategories>> typeCategories = new HashMap<>();
    public final Map<RecipeType<?>, Function<RecipeHolder<?>, RecipeBookCategories>> recipeCategoryLookups = new HashMap<>();

    /**
     * Registers the list of categories that compose an aggregate category.
     */
    public void registerAggregateCategory(RecipeBookCategories category, List<RecipeBookCategories> others) {
        aggregateCategories.put(category, ImmutableList.copyOf(others));
    }

    /**
     * Registers the list of categories that compose a recipe book.
     */
    public void registerBookCategories(RecipeBookType type, List<RecipeBookCategories> categories) {
        typeCategories.put(type, ImmutableList.copyOf(categories));
    }

    /**
     * Registers a category lookup for a certain recipe type.
     */
    public void registerRecipeCategoryFinder(RecipeType<?> type, Function<RecipeHolder<?>, RecipeBookCategories> lookup) {
        recipeCategoryLookups.put(type, lookup);
    }

    public static void setupCategories() {

    }
}
