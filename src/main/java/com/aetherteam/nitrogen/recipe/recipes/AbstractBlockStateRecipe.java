package com.aetherteam.nitrogen.recipe.recipes;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CacheableFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractBlockStateRecipe implements BlockStateRecipe {
    protected final RecipeType<? extends AbstractBlockStateRecipe> type;
    protected final BlockStateIngredient ingredient;
    protected final BlockPropertyPair result;
    protected final Optional<CacheableFunction> function;
    private final Optional<Identifier> functionId;

    public AbstractBlockStateRecipe(RecipeType<? extends AbstractBlockStateRecipe> type, BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> functionId) {
        this.type = type;
        this.ingredient = ingredient;
        this.result = result;
        this.functionId = functionId.isEmpty() ? Optional.empty() : functionId;
        this.function = BlockStateRecipeUtil.buildFunction(this.functionId);
    }

    /**
     * Replaces an old {@link BlockState} with a new one from {@link AbstractBlockStateRecipe#getResultState(BlockState)}. Also executes a mcfunction if the recipe has one.
     *
     * @param level    The {@link Level} the recipe is performed in.
     * @param pos      The {@link BlockPos} the recipe is performed at.
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
     *
     * @param originalState The original {@link BlockState} being interacted with.
     * @return The new result {@link BlockState}.
     */
    @Override
    public BlockState getResultState(BlockState originalState) {
        BlockState resultState = this.getResult().block().withPropertiesOf(originalState);
        if (this.getResult().properties().isPresent()) {
            for (Property.Value<?> propertyEntry : this.getResult().properties().get()) {
                resultState = BlockStateRecipeUtil.setHelper(propertyEntry, resultState);
            }
        }
        return resultState;
    }

    @Override
    public RecipeType<? extends AbstractBlockStateRecipe> getType() {
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
    public Optional<CacheableFunction> getFunction() {
        return this.function;
    }

    @Override
    public Optional<Identifier> getFunctionId() {
        return this.functionId;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    public static <T extends AbstractBlockStateRecipe> MapCodec<T> codec(AbstractBlockStateRecipe.Factory<T> factory) {
        return RecordCodecBuilder.mapCodec((i) -> {
            Products.P3<RecordCodecBuilder.Mu<T>, BlockStateIngredient, BlockPropertyPair, Optional<Identifier>> var10000 = i.group(
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBlockStateRecipe::getIngredient),
                BlockPropertyPair.CODEC.fieldOf("result").forGetter(AbstractBlockStateRecipe::getResult),
                Identifier.CODEC.optionalFieldOf("mcfunction").forGetter(AbstractBlockStateRecipe::getFunctionId)
            );
            Objects.requireNonNull(factory);
            return var10000.apply(i, factory::create);
        });
    }

    public static <T extends AbstractBlockStateRecipe> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(AbstractBlockStateRecipe.Factory<T> factory) {
        return StreamCodec.composite(
            BlockStateIngredient.CONTENTS_STREAM_CODEC, AbstractBlockStateRecipe::getIngredient,
            BlockPropertyPair.STREAM_CODEC, AbstractBlockStateRecipe::getResult,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), AbstractBlockStateRecipe::getFunctionId,
            factory::create);
    }

    public interface Factory<T extends AbstractBlockStateRecipe> {
        T create(BlockStateIngredient ingredient, BlockPropertyPair result, Optional<Identifier> functionId);
    }
}

