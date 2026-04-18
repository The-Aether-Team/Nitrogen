package com.aetherteam.nitrogen.recipe.builder;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;

public class BlockStateRecipeBuilder implements RecipeBuilder {
    private final BlockPropertyPair result;
    private final BlockStateIngredient ingredient;
    private Optional<Identifier> function = Optional.empty();
    private final AbstractBlockStateRecipe.Factory<?> factory;

    public BlockStateRecipeBuilder(BlockPropertyPair result, BlockStateIngredient ingredient, AbstractBlockStateRecipe.Factory<?> factory) {
        this.result = result;
        this.ingredient = ingredient;
        this.factory = factory;
    }

    public static <T extends AbstractBlockStateRecipe> BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, Block resultBlock, AbstractBlockStateRecipe.Factory<T> factory) {
        return recipe(ingredient, BlockPropertyPair.of(resultBlock, Optional.empty()), factory);
    }

    public static <T extends AbstractBlockStateRecipe> BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, Block resultBlock, Optional<HashSet<Property.Value<?>>> resultProperties, AbstractBlockStateRecipe.Factory<T> factory) {
        return recipe(ingredient, BlockPropertyPair.of(resultBlock, resultProperties), factory);
    }

    public static <T extends AbstractBlockStateRecipe> BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, BlockPropertyPair result, AbstractBlockStateRecipe.Factory<T> factory) {
        return new BlockStateRecipeBuilder(result, ingredient, factory);
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, this.result.typeHolder().unwrapKey().orElseThrow().identifier());
    }

    public RecipeBuilder function(Optional<Identifier> function) {
        this.function = function;
        return this;
    }

    public BlockPropertyPair getResultPair() {
        return this.result;
    }

    public BlockStateIngredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
        AbstractBlockStateRecipe recipe = this.factory.create(this.getIngredient(), this.getResultPair(), this.function);
        recipeOutput.accept(resourceKey, recipe, null);
    }
}

