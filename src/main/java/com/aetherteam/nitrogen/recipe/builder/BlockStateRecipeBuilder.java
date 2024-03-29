package com.aetherteam.nitrogen.recipe.builder;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.aetherteam.nitrogen.recipe.serializer.BlockStateRecipeSerializer;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public class BlockStateRecipeBuilder implements RecipeBuilder {
    private final BlockPropertyPair result;
    private final BlockStateIngredient ingredient;
    private final BlockStateRecipeSerializer<?> serializer;
    private Optional<ResourceLocation> function = Optional.empty();

    public BlockStateRecipeBuilder(BlockPropertyPair result, BlockStateIngredient ingredient, BlockStateRecipeSerializer<?> serializer) {
        this.result = result;
        this.ingredient = ingredient;
        this.serializer = serializer;
    }

    public static BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, Block resultBlock, BlockStateRecipeSerializer<?> serializer) {
        return recipe(ingredient, BlockPropertyPair.of(resultBlock, Optional.empty()), serializer);
    }

    public static BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, Block resultBlock, Optional<Map<Property<?>, Comparable<?>>> resultProperties, BlockStateRecipeSerializer<?> serializer) {
        return recipe(ingredient, BlockPropertyPair.of(resultBlock, resultProperties), serializer);
    }

    public static BlockStateRecipeBuilder recipe(BlockStateIngredient ingredient, BlockPropertyPair result, BlockStateRecipeSerializer<?> serializer) {
        return new BlockStateRecipeBuilder(result, ingredient, serializer);
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

    public BlockStateRecipeSerializer<?> getSerializer() {
        return this.serializer;
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
        output.accept(new BlockStateRecipeBuilder.Result(id, this.ingredient, this.result, this.serializer, this.function));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final BlockStateIngredient ingredient;
        private final BlockPropertyPair result;
        private final RecipeSerializer<? extends AbstractBlockStateRecipe> serializer;
        private final Optional<ResourceLocation> function;

        public Result(ResourceLocation id, BlockStateIngredient ingredient, BlockPropertyPair result, RecipeSerializer<? extends AbstractBlockStateRecipe> serializer) {
            this(id, ingredient, result, serializer, Optional.empty());
        }

        public Result(ResourceLocation id, BlockStateIngredient ingredient, BlockPropertyPair result, RecipeSerializer<? extends AbstractBlockStateRecipe> serializer, Optional<ResourceLocation> function) {
            this.id = id;
            this.ingredient = ingredient;
            this.result = result;
            this.serializer = serializer;
            this.function = function;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", this.ingredient.toJson(false));
            if (this.result.properties().isEmpty()) {
                json.add("result", BlockStateIngredient.of(this.result.block()).toJson(false));
            } else {
                json.add("result", BlockStateIngredient.of(this.result).toJson(false));
            }
            this.function.ifPresent(resourceLocation -> json.addProperty("mcfunction", resourceLocation.toString()));
        }

        @Override
        public RecipeSerializer<?> type() {
            return this.serializer;
        }

        @Override
        public ResourceLocation id() {
            return this.id;
        }

        @Nullable
        @Override
        public AdvancementHolder advancement() {
            return null;
        }
    }
}

