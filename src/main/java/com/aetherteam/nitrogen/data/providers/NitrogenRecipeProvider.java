package com.aetherteam.nitrogen.data.providers;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NitrogenRecipeProvider extends RecipeProvider {
    protected final String id;

    public NitrogenRecipeProvider(PackOutput output, String id) {
        super(output);
        this.id = id;
    }

    protected ResourceLocation name(String name) {
        return new ResourceLocation(this.id, name);
    }

    protected void oreBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> consumer, RecipeCategory itemCategory, ItemLike item, RecipeCategory blockCategory, ItemLike block, String itemRecipeName, String itemGroup) {
        ShapelessRecipeBuilder.shapeless(itemCategory, item, 9).requires(block).group(itemGroup).unlockedBy(getHasName(block), has(block)).save(consumer, this.name(itemRecipeName));
        ShapedRecipeBuilder.shaped(blockCategory, block).define('#', item).pattern("###").pattern("###").pattern("###").unlockedBy(getHasName(item), has(item)).save(consumer, this.name(getSimpleRecipeName(block)));
    }

    protected RecipeBuilder stairs(Supplier<? extends Block> stairs, Supplier<? extends Block> material) {
        return stairBuilder(stairs.get(), Ingredient.of(material.get())).unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHelmet(Supplier<? extends Item> helmet, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet.get())
                .define('#', material.get())
                .pattern("###")
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeChestplate(Supplier<? extends Item> chestplate, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate.get())
                .define('#', material.get())
                .pattern("# #")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeLeggings(Supplier<? extends Item> leggings, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings.get())
                .define('#', material.get())
                .pattern("###")
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeBoots(Supplier<? extends Item> boots, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots.get())
                .define('#', material.get())
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeGloves(Supplier<? extends Item> gloves, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, gloves.get())
                .define('#', material.get())
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeGlovesWithTag(Supplier<? extends Item> gloves, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, gloves.get())
                .define('#', materialTag)
                .pattern("# #")
                .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeRing(Supplier<? extends Item> ring, Item material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ring.get())
                .define('#', material)
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .unlockedBy(getHasName(material), has(material));
    }

    protected SimpleCookingRecipeBuilder smeltingOreRecipe(ItemLike result, ItemLike ingredient, float experience) {
        return SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, 200)
                .unlockedBy(getHasName(ingredient), has(ingredient));
    }

    protected SimpleCookingRecipeBuilder blastingOreRecipe(ItemLike result, ItemLike ingredient, float experience) {
        return SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, 100)
                .unlockedBy(getHasName(ingredient), has(ingredient));
    }

    protected void stonecuttingRecipe(Consumer<FinishedRecipe> consumer, RecipeCategory category, ItemLike item, ItemLike ingredient) {
        this.stonecuttingRecipe(consumer, category, item, ingredient, 1);
    }

    protected void stonecuttingRecipe(Consumer<FinishedRecipe> consumer, RecipeCategory category, ItemLike item, ItemLike ingredient, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), category, item, count).unlockedBy(getHasName(ingredient), has(ingredient)).save(consumer, this.name(getConversionRecipeName(item, ingredient) + "_stonecutting"));
    }

    protected BlockPropertyPair pair(Block resultBlock, Map<Property<?>, Comparable<?>> resultProperties) {
        return BlockPropertyPair.of(resultBlock, resultProperties);
    }
}
