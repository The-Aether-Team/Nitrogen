package com.aetherteam.nitrogen.recipe.serializer;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class BlockStateRecipeSerializer<T extends AbstractBlockStateRecipe> implements RecipeSerializer<T> {
    private final Function3<BlockStateIngredient, BlockPropertyPair, Optional<String>, T> factory;

    private final Codec<T> codec;

    public BlockStateRecipeSerializer(Function3<BlockStateIngredient, BlockPropertyPair, Optional<String>, T> factory) {
        this.factory = factory;
        this.codec = RecordCodecBuilder.create(inst -> inst.group(
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBlockStateRecipe::getIngredient),
                BlockPropertyPair.BLOCKSTATE_CODEC.fieldOf("result").forGetter(AbstractBlockStateRecipe::getResult),
                Codec.STRING.optionalFieldOf("mcfunction").forGetter(AbstractBlockStateRecipe::getFunctionString)
        ).apply(inst, this.factory));
    }

    @Override
    public Codec<T> codec() {
        return this.codec;
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
        BlockStateIngredient ingredient = BlockStateIngredient.fromNetwork(buffer);
        BlockPropertyPair result = BlockStateRecipeUtil.readPair(buffer);
        String functionString = buffer.readUtf();
        Optional<String> function = functionString.isBlank() ? Optional.empty() : Optional.of(functionString);
        return this.factory.apply(ingredient, result, function);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        recipe.getIngredient().toNetwork(buffer);
        BlockStateRecipeUtil.writePair(buffer, recipe.getResult());
        Optional<CommandFunction.CacheableFunction> function = recipe.getFunction();
        buffer.writeUtf(function.isPresent() && function.get().getId() != null ? function.get().getId().toString() : "");
    }
}

