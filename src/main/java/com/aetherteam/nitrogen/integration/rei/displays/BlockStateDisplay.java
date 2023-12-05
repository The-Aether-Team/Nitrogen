package com.aetherteam.nitrogen.integration.rei.displays;

import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockStateDisplay extends BasicDisplay {

    private final CategoryIdentifier<? extends AbstractBlockStateRecipe> id;
    private final AbstractBlockStateRecipe recipe;

    public BlockStateDisplay(CategoryIdentifier<? extends AbstractBlockStateRecipe> id, AbstractBlockStateRecipe recipe) {
        super(getInputs(recipe), getOutputs(recipe));
        this.id = id;
        this.recipe = recipe;
    }

    /**
     * Warning for "deprecation" is suppressed because the non-sensitive version of {@link net.minecraft.world.level.block.Block#getCloneItemStack(BlockGetter, BlockPos, BlockState)} is needed in this context.
     */
    private static ItemStack setupIngredient(BlockPropertyPair recipeResult) {
        ItemStack stack = ItemStack.EMPTY;
        if (Minecraft.getInstance().level != null) {
            BlockState resultState = recipeResult.block().defaultBlockState();
            for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : recipeResult.properties().entrySet()) {
                resultState = BlockStateRecipeUtil.setHelper(propertyEntry, resultState);
            }
            stack = recipeResult.block().getCloneItemStack(Minecraft.getInstance().level, BlockPos.ZERO, resultState);
        }
        return stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
    }

    private static List<EntryIngredient> getInputs(AbstractBlockStateRecipe recipe) {
        BlockStateIngredient recipeIngredients = recipe.getIngredient();
        BlockPropertyPair[] pairs = recipeIngredients.getPairs();
        if (pairs != null) {
            // Sets up input slots.
            List<EntryStack<?>> inputIngredients = new ArrayList<>();
            for (BlockPropertyPair pair : pairs) {
                if (pair.block() instanceof LiquidBlock liquidBlock) {
                    inputIngredients.add(EntryStacks.of(liquidBlock.getFluidState(liquidBlock.defaultBlockState()).getType(), FluidConstants.BLOCK));
                } else {
                    inputIngredients.add(EntryStacks.of(setupIngredient(pair)));
                }
            }
            return List.of(EntryIngredient.of(inputIngredients));
        }
        return List.of();
    }

    private static List<EntryIngredient> getOutputs(AbstractBlockStateRecipe recipe) {
        BlockStateIngredient recipeIngredients = recipe.getIngredient();
        BlockPropertyPair recipeResult = recipe.getResult();
        BlockPropertyPair[] pairs = recipeIngredients.getPairs();
        if (pairs != null) {
            // Sets up output slots.
            EntryStack<?> outputIngredient;
            if (recipeResult.block() instanceof LiquidBlock liquidBlock) {
                outputIngredient = EntryStacks.of(liquidBlock.getFluidState(liquidBlock.defaultBlockState()).getType(), FluidConstants.BLOCK);
            } else {
                outputIngredient = EntryStacks.of(setupIngredient(recipeResult));
            }
            return List.of(EntryIngredient.of(outputIngredient));
        }
        return List.of();
    }

    @Override
    public CategoryIdentifier<? extends AbstractBlockStateRecipe> getCategoryIdentifier() {
        return this.id;
    }

    public AbstractBlockStateRecipe getRecipe() {
        return this.recipe;
    }
}
