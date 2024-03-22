package com.aetherteam.nitrogen.integration.jei.categories.block;

import com.aetherteam.nitrogen.integration.jei.BlockStateRenderer;
import com.aetherteam.nitrogen.integration.jei.FluidStateRenderer;
import com.aetherteam.nitrogen.integration.jei.categories.AbstractRecipeCategory;
import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
import com.aetherteam.nitrogen.recipe.BlockStateIngredient;
import com.aetherteam.nitrogen.recipe.BlockStateRecipeUtil;
import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.common.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    public AbstractBlockStateRecipeCategory(String id, ResourceLocation uid, IDrawable background, IDrawable icon, RecipeType<T> recipeType, IPlatformFluidHelper<?> fluidHelper) {
        super(id, uid, background, icon, recipeType);
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
                    inputIngredients.add(this.fluidHelper.create(liquidBlock.getFluid(), 1000));
                } else {
                    inputIngredients.add(this.setupIngredient(pair));
                }
            }
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 6).addIngredientsUnsafe(inputIngredients).addTooltipCallback((recipeSlotView, tooltip) -> this.populateAdditionalInformation(recipe, tooltip))
                .setCustomRenderer(Services.PLATFORM.getFluidHelper().getFluidIngredientType(), new FluidStateRenderer(Services.PLATFORM.getFluidHelper())).setCustomRenderer(VanillaTypes.ITEM_STACK, new BlockStateRenderer(pairs));

            // Sets up output slots.
            Object outputIngredient;
            if (recipeResult.block() instanceof LiquidBlock liquidBlock) {
                outputIngredient = this.fluidHelper.create(liquidBlock.getFluid(), 1000);
            } else {
                outputIngredient = this.setupIngredient(recipeResult);
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 6).addIngredientsUnsafe(List.of(outputIngredient))
                .setCustomRenderer(Services.PLATFORM.getFluidHelper().getFluidIngredientType(), new FluidStateRenderer(Services.PLATFORM.getFluidHelper())).setCustomRenderer(VanillaTypes.ITEM_STACK, new BlockStateRenderer(recipeResult));
        }
    }

    /**
     * Warning for "deprecation" is suppressed because the non-sensitive version of {@link net.minecraft.world.level.block.Block#getCloneItemStack(net.minecraft.world.level.LevelReader, BlockPos, BlockState)} is needed in this context.
     */
    @SuppressWarnings("deprecation")
    private ItemStack setupIngredient(BlockPropertyPair recipeResult) {
        ItemStack stack = ItemStack.EMPTY;
        if (Minecraft.getInstance().level != null) {
            BlockState resultState = recipeResult.block().defaultBlockState();
            if (recipeResult.properties().isPresent()) {
                for (Map.Entry<Property<?>, Comparable<?>> propertyEntry : recipeResult.properties().get().entrySet()) {
                    resultState = BlockStateRecipeUtil.setHelper(propertyEntry, resultState);
                }
            }
            stack = recipeResult.block().getCloneItemStack(Minecraft.getInstance().level, BlockPos.ZERO, resultState);
        }
        return stack.isEmpty() ? new ItemStack(Blocks.STONE) : stack;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
    }

    protected void populateAdditionalInformation(T recipe, List<Component> tooltip) {
    }
}
