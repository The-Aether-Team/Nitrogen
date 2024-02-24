package com.aetherteam.nitrogen.recipe.serializer;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class BlockStateRecipeSerializer<T extends AbstractBlockStateRecipe> implements RecipeSerializer<T> {
    private final Function3<BlockStateIngredient, BlockPropertyPair, Optional<ResourceLocation>, T> factory;

    private final Codec<T> codec;

    public BlockStateRecipeSerializer(Function3<BlockStateIngredient, BlockPropertyPair, Optional<ResourceLocation>, T> factory) {
        this.factory = factory;
        this.codec = RecordCodecBuilder.create(inst -> inst.group(
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBlockStateRecipe::getIngredient),
                BlockPropertyPair.BLOCKSTATE_CODEC.fieldOf("result").forGetter(AbstractBlockStateRecipe::getResult),
                ResourceLocation.CODEC.optionalFieldOf("mcfunction").forGetter(AbstractBlockStateRecipe::getFunctionId)
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
        Optional<ResourceLocation> function = buffer.readOptional(FriendlyByteBuf::readResourceLocation);
        return this.factory.apply(ingredient, result, function);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        recipe.getIngredient().toNetwork(buffer);
        BlockStateRecipeUtil.writePair(buffer, recipe.getResult());
        buffer.writeOptional(recipe.getFunctionId(), FriendlyByteBuf::writeResourceLocation);
    }
}

