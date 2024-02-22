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

public class BlockStateRecipeSerializer<T extends AbstractBlockStateRecipe> implements RecipeSerializer<T> {
    private final Function3<BlockStateIngredient, BlockPropertyPair, String, T> factory;

    private final MapCodec<T> flatCodec;
    private final Codec<T> codec;

    public BlockStateRecipeSerializer(Function3<BlockStateIngredient, BlockPropertyPair, String, T> factory) {
        this.factory = factory;

        this.flatCodec = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBlockStateRecipe::getIngredient),
                BlockPropertyPair.BLOCKSTATE_CODEC.fieldOf("result").forGetter(AbstractBlockStateRecipe::getResult),
                Codec.STRING.fieldOf("mcfunction").forGetter(AbstractBlockStateRecipe::getFunctionString)
        ).apply(inst, this.factory));

        this.codec = this.flatCodec.codec();
    }
    public MapCodec<T> flatCodec() {
        return this.flatCodec;
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
        return this.factory.apply(ingredient, result, functionString);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        recipe.getIngredient().toNetwork(buffer);
        BlockStateRecipeUtil.writePair(buffer, recipe.getResult());
        CommandFunction.CacheableFunction function = recipe.getFunction();
        buffer.writeUtf(function != null && function.getId() != null ? function.getId().toString() : "");
    }
}

