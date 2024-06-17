package com.aetherteam.nitrogen.recipe.serializer;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class BlockStateRecipeSerializer<T extends AbstractBlockStateRecipe> implements RecipeSerializer<T> {
    private final AbstractBlockStateRecipe.Factory<T> factory;

    private final MapCodec<T> mapCodec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public BlockStateRecipeSerializer(AbstractBlockStateRecipe.Factory<T> factory) {
        this.factory = factory;
        this.mapCodec = RecordCodecBuilder.mapCodec(inst -> inst.group(
            BlockStateIngredient.CODEC.fieldOf("ingredient").forGetter(AbstractBlockStateRecipe::getIngredient),
            BlockPropertyPair.CODEC.fieldOf("result").forGetter(AbstractBlockStateRecipe::getResult),
            ResourceLocation.CODEC.optionalFieldOf("mcfunction").forGetter(AbstractBlockStateRecipe::getFunctionId)
        ).apply(inst, this.factory::create));
        this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
    }

    @Override
    public MapCodec<T> codec() {
        return this.mapCodec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    public T fromNetwork(RegistryFriendlyByteBuf buffer) {
        BlockStateIngredient ingredient = BlockStateIngredient.CONTENTS_STREAM_CODEC.decode(buffer);
        BlockPropertyPair result = BlockStateRecipeUtil.readPair(buffer);
        Optional<ResourceLocation> function = buffer.readOptional(FriendlyByteBuf::readResourceLocation);
        return this.factory.create(ingredient, result, function);
    }

    public void toNetwork(RegistryFriendlyByteBuf  buffer, T recipe) {
        BlockStateIngredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        BlockStateRecipeUtil.writePair(buffer, recipe.getResult());
        buffer.writeOptional(recipe.getFunctionId(), FriendlyByteBuf::writeResourceLocation);
    }
}

