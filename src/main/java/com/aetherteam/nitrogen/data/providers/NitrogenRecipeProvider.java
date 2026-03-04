package com.aetherteam.nitrogen.data.providers;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;
import java.util.function.Supplier;

public abstract class NitrogenRecipeProvider extends RecipeProvider {
    protected final String id;

    public NitrogenRecipeProvider(HolderLookup.Provider provider, RecipeOutput output, String id) {
        super(provider, output);
        this.id = id;
    }

    protected ResourceKey<Recipe<?>> name(String name) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(this.id, name));
    }

    protected void oreBlockStorageRecipesRecipesWithCustomUnpacking(HolderGetter<Item> holderGetter, RecipeOutput output, RecipeCategory itemCategory, ItemLike item, RecipeCategory blockCategory, ItemLike block, String itemRecipeName, String itemGroup) {
        ShapelessRecipeBuilder.shapeless(holderGetter, itemCategory, item, 9).requires(block).group(itemGroup).unlockedBy(getHasName(block), has(block)).save(output, this.name(itemRecipeName));
        ShapedRecipeBuilder.shaped(holderGetter, blockCategory, block).define('#', item).pattern("###").pattern("###").pattern("###").unlockedBy(getHasName(item), has(item)).save(output, this.name(getSimpleRecipeName(block)));
    }

    protected ShapedRecipeBuilder fence(HolderGetter<Item> holderGetter, Supplier<? extends Block> fence, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.DECORATIONS, fence.get(), 3)
            .group("wooden_fence")
            .define('M', material.get())
            .define('/', sticks)
            .pattern("M/M")
            .pattern("M/M")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder fenceGate(HolderGetter<Item> holderGetter, Supplier<? extends Block> fenceGate, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.REDSTONE, fenceGate.get())
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

    protected ShapedRecipeBuilder makePickaxeWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> pickaxe, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, pickaxe.get())
            .define('#', material)
            .define('/', sticks)
            .pattern("###")
            .pattern(" / ")
            .pattern(" / ")
            .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makePickaxeWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> pickaxe, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, pickaxe.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("###")
            .pattern(" / ")
            .pattern(" / ")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeAxeWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> axe, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, axe.get())
            .define('#', material)
            .define('/', sticks)
            .pattern("##")
            .pattern("#/")
            .pattern(" /")
            .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeAxeWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> axe, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, axe.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("##")
            .pattern("#/")
            .pattern(" /")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeShovelWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> shovel, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, shovel.get())
            .define('#', material)
            .define('/', sticks)
            .pattern("#")
            .pattern("/")
            .pattern("/")
            .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeShovelWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> shovel, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, shovel.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("#")
            .pattern("/")
            .pattern("/")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHoeWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> hoe, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, hoe.get())
            .define('#', material)
            .define('/', sticks)
            .pattern("##")
            .pattern(" /")
            .pattern(" /")
            .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeHoeWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> hoe, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, hoe.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("##")
            .pattern(" /")
            .pattern(" /")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeSwordWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> sword, TagKey<Item> material, Ingredient sticks, String has) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, sword.get())
            .define('#', material)
            .define('/', sticks)
            .pattern("#")
            .pattern("#")
            .pattern("/")
            .unlockedBy(has, has(material));
    }

    protected ShapedRecipeBuilder makeSwordWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> sword, Supplier<? extends Block> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, sword.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("#")
            .pattern("#")
            .pattern("/")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makePickaxe(HolderGetter<Item> holderGetter, Supplier<? extends Item> pickaxe, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, pickaxe.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("###")
            .pattern(" / ")
            .pattern(" / ")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeAxe(HolderGetter<Item> holderGetter, Supplier<? extends Item> axe, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, axe.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("##")
            .pattern("#/")
            .pattern(" /")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeShovel(HolderGetter<Item> holderGetter, Supplier<? extends Item> shovel, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, shovel.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("#")
            .pattern("/")
            .pattern("/")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHoe(HolderGetter<Item> holderGetter, Supplier<? extends Item> hoe, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.TOOLS, hoe.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("##")
            .pattern(" /")
            .pattern(" /")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeSword(HolderGetter<Item> holderGetter, Supplier<? extends Item> sword, Supplier<? extends Item> material, Ingredient sticks) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, sword.get())
            .define('#', material.get())
            .define('/', sticks)
            .pattern("#")
            .pattern("#")
            .pattern("/")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHelmetWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> helmet, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, helmet.get())
            .define('#', material.get())
            .pattern("###")
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeChestplateWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> chestplate, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, chestplate.get())
            .define('#', material.get())
            .pattern("# #")
            .pattern("###")
            .pattern("###")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeLeggingsWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> leggings, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, leggings.get())
            .define('#', material.get())
            .pattern("###")
            .pattern("# #")
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeBootsWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> boots, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, boots.get())
            .define('#', material.get())
            .pattern("# #")
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeGlovesWithBlock(HolderGetter<Item> holderGetter, Supplier<? extends Item> gloves, Supplier<? extends Block> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, gloves.get())
            .define('#', material.get())
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeHelmetWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> helmet, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, helmet.get())
            .define('#', materialTag)
            .pattern("###")
            .pattern("# #")
            .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeChestplateWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> chestplate, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, chestplate.get())
            .define('#', materialTag)
            .pattern("# #")
            .pattern("###")
            .pattern("###")
            .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeLeggingsWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> leggings, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, leggings.get())
            .define('#', materialTag)
            .pattern("###")
            .pattern("# #")
            .pattern("# #")
            .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeBootsWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> boots, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, boots.get())
            .define('#', materialTag)
            .pattern("# #")
            .pattern("# #")
            .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeGlovesWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> gloves, TagKey<Item> materialTag, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, gloves.get())
            .define('#', materialTag)
            .pattern("# #")
            .unlockedBy("has_" + unlockName, has(materialTag));
    }

    protected ShapedRecipeBuilder makeHelmet(HolderGetter<Item> holderGetter, Supplier<? extends Item> helmet, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, helmet.get())
            .define('#', material.get())
            .pattern("###")
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeChestplate(HolderGetter<Item> holderGetter, Supplier<? extends Item> chestplate, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, chestplate.get())
            .define('#', material.get())
            .pattern("# #")
            .pattern("###")
            .pattern("###")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeLeggings(HolderGetter<Item> holderGetter, Supplier<? extends Item> leggings, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, leggings.get())
            .define('#', material.get())
            .pattern("###")
            .pattern("# #")
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeBoots(HolderGetter<Item> holderGetter, Supplier<? extends Item> boots, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, boots.get())
            .define('#', material.get())
            .pattern("# #")
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeGloves(HolderGetter<Item> holderGetter, Supplier<? extends Item> gloves, Supplier<? extends Item> material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, gloves.get())
            .define('#', material.get())
            .pattern("# #")
            .unlockedBy(getHasName(material.get()), has(material.get()));
    }

    protected ShapedRecipeBuilder makeRing(HolderGetter<Item> holderGetter, Supplier<? extends Item> ring, Item material) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, ring.get())
            .define('#', material)
            .pattern(" # ")
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy(getHasName(material), has(material));
    }

    protected ShapedRecipeBuilder makePendant(HolderGetter<Item> holderGetter, Supplier<? extends Item> pendant, Item material, Ingredient string) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, pendant.get())
            .define('S', string)
            .define('#', material)
            .pattern("SSS")
            .pattern("S S")
            .pattern(" # ")
            .unlockedBy(getHasName(material), has(material));
    }

    protected ShapedRecipeBuilder makeRingWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> ring, TagKey<Item> material, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, ring.get())
            .define('#', material)
            .pattern(" # ")
            .pattern("# #")
            .pattern(" # ")
            .unlockedBy("has_" + unlockName, has(material));
    }

    protected ShapedRecipeBuilder makePendantWithTag(HolderGetter<Item> holderGetter, Supplier<? extends Item> pendant, TagKey<Item> material, Ingredient string, String unlockName) {
        return ShapedRecipeBuilder.shaped(holderGetter, RecipeCategory.COMBAT, pendant.get())
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

    protected BlockPropertyPair pair(Block resultBlock, Optional<Reference2ObjectArrayMap<Property<?>, Comparable<?>>> resultProperties) {
        return BlockPropertyPair.of(resultBlock, resultProperties);
    }
}
