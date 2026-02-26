package com.aetherteam.nitrogen.integration.rei.categories.block;

//import com.aetherteam.nitrogen.integration.rei.REIClientUtils;
//import com.aetherteam.nitrogen.integration.rei.categories.AbstractRecipeCategory;
//import com.aetherteam.nitrogen.integration.rei.displays.BlockStateRecipeDisplay;
//import com.aetherteam.nitrogen.recipe.BlockPropertyPair;
//import com.aetherteam.nitrogen.recipe.recipes.AbstractBlockStateRecipe;
//import me.shedaniel.math.Point;
//import me.shedaniel.math.Rectangle;
//import me.shedaniel.rei.api.client.gui.Renderer;
//import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
//import me.shedaniel.rei.api.client.gui.widgets.Widget;
//import me.shedaniel.rei.api.client.gui.widgets.Widgets;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//
//import java.util.List;
//
//public abstract class AbstractBlockStateRecipeCategory<R extends AbstractBlockStateRecipe> extends AbstractRecipeCategory<BlockStateRecipeDisplay<R>> {
//    public AbstractBlockStateRecipeCategory(String id, CategoryIdentifier<BlockStateRecipeDisplay<R>> uid, int width, int height, Renderer icon) {
//        super(id, uid, width, height, icon);
//    }
//
//    @Override
//    public List<Widget> setupDisplay(BlockStateRecipeDisplay<R> display, Rectangle bounds) {
//        AbstractBlockStateRecipe recipe = display.getRecipe();
//
//        BlockPropertyPair recipeResult = recipe.getResult();
//        BlockPropertyPair[] pairs = recipe.getIngredient().getPairs();
//
//        List<Widget> widgets = super.setupDisplay(display, bounds);
//
//        var startingPoint = startingOffset(bounds);
//
//        if (pairs != null) {
//            widgets.add(
//                Widgets.createSlot(new Point(startingPoint.x + 8, startingPoint.y + 6))
//                    .markInput()
//                    .entries(REIClientUtils.setupRendering(display.getInputEntries().get(0), pairs, tooltip -> populateTooltip(display, tooltip)))
//            );
//            widgets.add(
//                Widgets.createArrow(new Point(bounds.getCenterX() - (24 / 2), bounds.getCenterY() - (17 / 2)))
//            );
//            widgets.add(
//                Widgets.createSlot(new Point(startingPoint.x + 60, startingPoint.y + 6))
//                    .markOutput()
//                    .entries(REIClientUtils.setupRendering(display.getOutputEntries().get(0), recipeResult, null))
//            );
//        }
//
//        return widgets;
//    }
//
//    protected void populateTooltip(BlockStateRecipeDisplay<R> display, Tooltip tooltip) {
//    }
//}
