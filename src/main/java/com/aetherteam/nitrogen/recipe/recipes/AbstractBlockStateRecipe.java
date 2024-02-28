package com.aetherteam.nitrogen.recipe.recipes;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractBlockStateRecipe implements BlockStateRecipe {
    protected final RecipeType<?> type;
    protected final BlockStateIngredient ingredient;
    protected final BlockPropertyPair result;
    protected final Optional<CommandFunction.CacheableFunction> function;
    private final Optional<ResourceLocation> functionId;

    public AbstractBlockStateRecipe(RecipeType<?> type, BlockStateIngredient ingredient, BlockPropertyPair result, Optional<ResourceLocation> functionId) {
        this.type = type;
        this.ingredient = ingredient;
        this.result = result;
        this.functionId = functionId.isEmpty() ? Optional.empty() : functionId;
        this.function = BlockStateRecipeUtil.buildFunction(this.functionId);
    }

    /**
     * Replaces an old {@link BlockState} with a new one from {@link AbstractBlockStateRecipe#getResultState(BlockState)}. Also executes a mcfunction if the recipe has one.
     * @param level The {@link Level} the recipe is performed in.
     * @param pos The {@link BlockPos} the recipe is performed at.
     * @param oldState The original {@link BlockState} being interacted with.
     * @return Whether the new {@link BlockState} was set.
     */
    public boolean set(Level level, BlockPos pos, BlockState oldState) {
        if (this.matches(level, pos, oldState)) {
            BlockState newState = this.getResultState(oldState);
            level.setBlockAndUpdate(pos, newState);
            BlockStateRecipeUtil.executeFunction(level, pos, this.getFunction());
            return true;
        }
        return false;
    }

    public boolean matches(Level level, BlockPos pos, BlockState state) {
        return this.getIngredient().test(state);
    }

    /**
     * Sets up a new {@link BlockState} with the result {@link BlockPropertyPair#block()} and the original {@link BlockState}'s properties.
     * Then the new {@link BlockState}'s properties are modified based on the result {@link BlockPropertyPair#properties()} using {@link BlockStateRecipeUtil#setHelper(Map.Entry, BlockState)}.
     * @param originalState The original {@link BlockState} being interacted with.
     * @return The new result {@link BlockState}.
     */
    @Override
    public BlockState getResultState(BlockState originalState) {
        BlockState resultState = this.getResult().block().withPropertiesOf(originalState);
        if (this.getResult().properties().isPresent()) {
            for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : this.getResult().properties().get().entrySet()) {
                resultState = BlockStateRecipeUtil.setHelper(propertyEntry, resultState);
            }
        }
        return resultState;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }

    @Override
    public BlockStateIngredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public BlockPropertyPair getResult() {
        return this.result;
    }

    @Override
    public Optional<CommandFunction.CacheableFunction> getFunction() {
        return this.function;
    }

    @Override
    public Optional<ResourceLocation> getFunctionId() {
        return this.functionId;
    }
}

