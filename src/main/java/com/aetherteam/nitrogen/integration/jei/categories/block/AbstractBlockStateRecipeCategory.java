package com.aetherteam.nitrogen.integration.jei.categories.block;

import com.aetherteam.nitrogen.integration.jei.BlockStateIngredientRenderer;
import com.aetherteam.nitrogen.integration.jei.FluidStateIngredientRenderer;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.common.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractBlockStateRecipeCategory<T extends AbstractBlockStateRecipe> extends AbstractRecipeCategory<T> {
    protected final IPlatformFluidHelper<?> fluidHelper;

    public AbstractBlockStateRecipeCategory(IRecipeType<T> recipeType, Component name, IDrawable icon, IPlatformFluidHelper<?> fluidHelper) {
        super(recipeType, name, icon, 84, 28);
        this.fluidHelper = fluidHelper;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focusGroup) {
        BlockStateIngredient recipeIngredients = recipe.getIngredient();
        BlockPropertyPair recipeResult = recipe.getResult();
        BlockPropertyPair[] pairs = recipeIngredients.getPairs();
        if (pairs != null) {
            // Sets up input slots.
            List<Object> inputIngredients = new ArrayList<>();
            for (BlockPropertyPair pair : pairs) {
                if (pair.block() instanceof LiquidBlock liquidBlock) {
                    inputIngredients.add(this.fluidHelper.create(liquidBlock.fluid.builtInRegistryHolder(), 1000));
                } else {
                    inputIngredients.add(this.setupIngredient(pair));
                }
            }
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 6).setStandardSlotBackground().addIngredientsUnsafe(inputIngredients).addRichTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip))
                .setCustomRenderer(Services.PLATFORM.getFluidHelper().getFluidIngredientType(), new FluidStateIngredientRenderer(Services.PLATFORM.getFluidHelper())).setCustomRenderer(VanillaTypes.ITEM_STACK, new BlockStateIngredientRenderer(pairs));

            // Sets up output slots.
            Object outputIngredient;
            if (recipeResult.block() instanceof LiquidBlock liquidBlock) {
                outputIngredient = this.fluidHelper.create(liquidBlock.fluid.builtInRegistryHolder(), 1000);
            } else {
                outputIngredient = this.setupIngredient(recipeResult);
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 6).setStandardSlotBackground().addIngredientsUnsafe(List.of(outputIngredient))
                .setCustomRenderer(Services.PLATFORM.getFluidHelper().getFluidIngredientType(), new FluidStateIngredientRenderer(Services.PLATFORM.getFluidHelper())).setCustomRenderer(VanillaTypes.ITEM_STACK, new BlockStateIngredientRenderer(recipeResult));
        }
    }

    private ItemStack setupIngredient(BlockPropertyPair recipeResult) {
        ItemStack stack = ItemStack.EMPTY;
        if (Minecraft.getInstance().level != null) {
            BlockState resultState = recipeResult.block().defaultBlockState();
            if (recipeResult.properties().isPresent()) {
                for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : recipeResult.properties().get().entrySet()) {
                    resultState = BlockStateRecipeUtil.setHelper(propertyEntry, resultState);
                }
            }
            stack = recipeResult.block().getCloneItemStack(Minecraft.getInstance().level, BlockPos.ZERO, resultState, true, Minecraft.getInstance().player);
        }
        return stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(30, 6);
    }

    protected void populateAdditionalInformation(T recipe, ITooltipBuilder tooltip) {
    }
}
