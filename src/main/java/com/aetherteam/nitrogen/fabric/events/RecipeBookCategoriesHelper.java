package com.aetherteam.nitrogen.fabric.events;

import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;

import java.util.*;

public class RecipeBookCategoriesHelper {

    public static final RecipeBookCategoriesHelper INSTANCE = new RecipeBookCategoriesHelper();

    private final Map<ExtendedRecipeBookCategory, List<RecipeBookCategory>> searchCategories = new LinkedHashMap<>();

    public void register(ExtendedRecipeBookCategory searchCategory, RecipeBookCategory... includedCategories) {
        if (includedCategories.length == 0) throw new IllegalArgumentException("Must at least have some included categories");
        if (searchCategories.containsKey(searchCategory)) throw new IllegalArgumentException("Given search category has already been registered: " + searchCategory);
        searchCategories.put(searchCategory, List.of(includedCategories));
    }

    public Map<ExtendedRecipeBookCategory, List<RecipeBookCategory>> getSearchCategories() {
        return Collections.unmodifiableMap(searchCategories);
    }
}
