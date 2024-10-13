package com.aetherteam.nitrogen.recipe.builder;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.Optional;

public class BlockStateRecipeBuilder implements RecipeBuilder {
    private final BlockPropertyPair result;
    private final BlockStateIngredient ingredient;
    private Optional<ResourceLocation> function = Optional.empty();
    private final AbstractBlockStateRecipe.Factory<?> factory;

    public BlockStateRecipeBuilder(BlockPropertyPair result, BlockStateIngredient ingredient, AbstractBlockStateRecipe.Factory<?> factory) {
        this.result = result;
        this.ingredient = ingredient;
        this.factory = factory;
    }

    public static <T extends AbstractBlockStateRecipe> BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, Block resultBlock, AbstractBlockStateRecipe.Factory<T> factory) {
        return recipe(ingredient, BlockPropertyPair.of(resultBlock, Optional.empty()), factory);
    }

    public static <T extends AbstractBlockStateRecipe> BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, Block resultBlock, Optional<Reference2ObjectArrayMap<Property<?>, Comparable<?>>> resultProperties, AbstractBlockStateRecipe.Factory<T> factory) {
        return recipe(ingredient, BlockPropertyPair.of(resultBlock, resultProperties), factory);
    }

    public static <T extends AbstractBlockStateRecipe> BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, BlockPropertyPair result, AbstractBlockStateRecipe.Factory<T> factory) {
        return new BlockStateRecipeBuilder(result, ingredient, factory);
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    public RecipeBuilder function(Optional<ResourceLocation> function) {
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
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public RecipeBuilder unlockedBy(String criterionName, Criterion<?> criterionTrigger) {
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        AbstractBlockStateRecipe recipe = this.factory.create(this.getIngredient(), this.getResultPair(), this.function);
        output.accept(id, recipe, null);
    }
}

