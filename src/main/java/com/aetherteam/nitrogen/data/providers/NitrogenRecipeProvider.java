package com.aetherteam.nitrogen.data.providers;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import net.minecraft.core.HolderLookup;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class NitrogenRecipeProvider extends RecipeProvider {
    protected final String id;

    public NitrogenRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String id) {
        super(output, lookupProvider);
        this.id = id;
    }

    protected ResourceLocation name(String name) {
        return new ResourceLocation(this.id, name);
    }

    protected void oreBlockStorageRecipesRecipesWithCustomUnpacking(RecipeOutput output, RecipeCategory itemCategory, ItemLike item, RecipeCategory blockCategory, ItemLike block, String itemRecipeName, String itemGroup) {
        ShapelessRecipeBuilder.shapeless(itemCategory, item, 9).requires(block).group(itemGroup).unlockedBy(getHasName(block), has(block)).save(output, this.name(itemRecipeName));
        ShapedRecipeBuilder.shaped(blockCategory, block).define('#', item).pattern("###").pattern("###").pattern("###").unlockedBy(getHasName(item), has(item)).save(output, this.name(getSimpleRecipeName(block)));
    }

    protected ShapedRecipeBuilder fence(Supplier<? extends Block> fence, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, fence.get(), 3)
                .group("wooden_fence")
                .define('M', material.get())
                .define('/', sticks)
                .pattern("M/M")
                .pattern("M/M")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder fenceGate(Supplier<? extends Block> fenceGate, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, fenceGate.get())
                .group("wooden_fence_gate")
                .define('M', material.get())
                .define('/', sticks)
                .pattern("/M/")
                .pattern("/M/")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected RecipeBuilder stairs(Supplier<? extends Block> stairs, Supplier<? extends Block> material) {
        return stairBuilder(stairs.get(), Ingredient.of(material.get())).unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makePickaxeWithTag(Supplier<? extends Item> pickaxe, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe.get())
                .define('#', material)
                .define('/', sticks)
                .pattern("###")
                .pattern(" / ")
                .pattern(" / ")
                .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makePickaxeWithBlock(Supplier<? extends Item> pickaxe, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("###")
                .pattern(" / ")
                .pattern(" / ")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeAxeWithTag(Supplier<? extends Item> axe, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe.get())
                .define('#', material)
                .define('/', sticks)
                .pattern("##")
                .pattern("#/")
                .pattern(" /")
                .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeAxeWithBlock(Supplier<? extends Item> axe, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("##")
                .pattern("#/")
                .pattern(" /")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeShovelWithTag(Supplier<? extends Item> shovel, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel.get())
                .define('#', material)
                .define('/', sticks)
                .pattern("#")
                .pattern("/")
                .pattern("/")
                .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeShovelWithBlock(Supplier<? extends Item> shovel, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("#")
                .pattern("/")
                .pattern("/")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHoeWithTag(Supplier<? extends Item> hoe, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoe.get())
                .define('#', material)
                .define('/', sticks)
                .pattern("##")
                .pattern(" /")
                .pattern(" /")
                .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeHoeWithBlock(Supplier<? extends Item> hoe, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoe.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("##")
                .pattern(" /")
                .pattern(" /")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeSwordWithTag(Supplier<? extends Item> sword, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword.get())
                .define('#', material)
                .define('/', sticks)
                .pattern("#")
                .pattern("#")
                .pattern("/")
                .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeSwordWithBlock(Supplier<? extends Item> sword, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("#")
                .pattern("#")
                .pattern("/")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makePickaxe(Supplier<? extends Item> pickaxe, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("###")
                .pattern(" / ")
                .pattern(" / ")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeAxe(Supplier<? extends Item> axe, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("##")
                .pattern("#/")
                .pattern(" /")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeShovel(Supplier<? extends Item> shovel, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("#")
                .pattern("/")
                .pattern("/")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHoe(Supplier<? extends Item> hoe, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoe.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("##")
                .pattern(" /")
                .pattern(" /")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeSword(Supplier<? extends Item> sword, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword.get())
                .define('#', material.get())
                .define('/', sticks)
                .pattern("#")
                .pattern("#")
                .pattern("/")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHelmetWithBlock(Supplier<? extends Item> helmet, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet.get())
                .define('#', material.get())
                .pattern("###")
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeChestplateWithBlock(Supplier<? extends Item> chestplate, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate.get())
                .define('#', material.get())
                .pattern("# #")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeLeggingsWithBlock(Supplier<? extends Item> leggings, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings.get())
                .define('#', material.get())
                .pattern("###")
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeBootsWithBlock(Supplier<? extends Item> boots, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots.get())
                .define('#', material.get())
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeGlovesWithBlock(Supplier<? extends Item> gloves, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, gloves.get())
                .define('#', material.get())
                .pattern("# #")
                .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHelmetWithTag(Supplier<? extends Item> helmet, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet.get())
                .define('#', materialTag)
                .pattern("###")
                .pattern("# #")
                .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeChestplateWithTag(Supplier<? extends Item> chestplate, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate.get())
                .define('#', materialTag)
                .pattern("# #")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeLeggingsWithTag(Supplier<? extends Item> leggings, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings.get())
                .define('#', materialTag)
                .pattern("###")
                .pattern("# #")
                .pattern("# #")
                .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeBootsWithTag(Supplier<? extends Item> boots, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots.get())
                .define('#', materialTag)
                .pattern("# #")
                .pattern("# #")
                .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeGlovesWithTag(Supplier<? extends Item> gloves, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, gloves.get())
                .define('#', materialTag)
                .pattern("# #")
                .unlockedBy("has_" + unlockName, has(materialTag));
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

    protected ShapedRecipeBuilder makeRing(Supplier<? extends Item> ring, Item material) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ring.get())
                .define('#', material)
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .unlockedBy(getHasName(material), has(material));
    }

    protected ShapedRecipeBuilder makePendant(Supplier<? extends Item> pendant, Item material, Ingredient string) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, pendant.get())
                .define('S', string)
                .define('#', material)
                .pattern("SSS")
                .pattern("S S")
                .pattern(" # ")
                .unlockedBy(getHasName(material), has(material));
    }

    protected ShapedRecipeBuilder makeRingWithTag(Supplier<? extends Item> ring, TagKey<Item> material, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ring.get())
                .define('#', material)
                .pattern(" # ")
                .pattern("# #")
                .pattern(" # ")
                .unlockedBy("has_" + unlockName, has(material));
    }

    protected ShapedRecipeBuilder makePendantWithTag(Supplier<? extends Item> pendant, TagKey<Item> material, Ingredient string, String unlockName) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, pendant.get())
                .define('S', string)
                .define('#', material)
                .pattern("SSS")
                .pattern("S S")
                .pattern(" # ")
                .unlockedBy("has_" + unlockName, has(material));
    }

    protected SimpleCookingRecipeBuilder smeltingOreRecipe(ItemLike result, ItemLike ingredient, float experience) {
        return SimpleCookingRecipeBuilder.smelting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, 200)
                .unlockedBy(getHasName(ingredient), has(ingredient));
    }

    protected SimpleCookingRecipeBuilder blastingOreRecipe(ItemLike result, ItemLike ingredient, float experience) {
        return SimpleCookingRecipeBuilder.blasting(Ingredient.of(ingredient), RecipeCategory.MISC, result, experience, 100)
                .unlockedBy(getHasName(ingredient), has(ingredient));
    }

    protected void stonecuttingRecipe(RecipeOutput output, RecipeCategory category, ItemLike item, ItemLike ingredient) {
        this.stonecuttingRecipe(output, category, item, ingredient, 1);
    }

    protected void stonecuttingRecipe(RecipeOutput output, RecipeCategory category, ItemLike item, ItemLike ingredient, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), category, item, count).unlockedBy(getHasName(ingredient), has(ingredient)).save(output, this.name(getConversionRecipeName(item, ingredient) + "_stonecutting"));
    }

    protected BlockPropertyPair pair(Block resultBlock, Optional<Map<Property<?>, Comparable<?>>> resultProperties) {
        return BlockPropertyPair.of(resultBlock, resultProperties);
    }
}
