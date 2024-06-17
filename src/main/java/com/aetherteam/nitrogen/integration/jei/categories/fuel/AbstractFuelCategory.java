package com.aetherteam.nitrogen.integration.jei.categories.fuel;

//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.gui.drawable.IDrawableAnimated;
//import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.Comparator;
//import java.util.List;
//
///**
// * An abstract class for JEI fuel recipe categories.
// */
//public abstract class AbstractFuelCategory implements IRecipeCategory<FuelRecipe> {
//    private final IDrawable background;
//    private final IDrawable icon;
//    private final LoadingCache<Integer, IDrawableAnimated> cachedFuelIndicator;
//
//    public AbstractFuelCategory(IGuiHelper helper, List<String> craftingStations) {
//        String longestString = craftingStations.stream().max(Comparator.comparingInt(String::length)).get();
//        Component longestStationName = Component.literal(longestString);
//
//        Font fontRenderer = Minecraft.getInstance().font;
//        Component maxBurnTimeText = createBurnTimeText(10000, longestStationName);
//        int maxStringWidth = fontRenderer.width(maxBurnTimeText.getString());
//        int backgroundHeight = 34;
//        int textPadding = 20;
//
//        this.background = helper.drawableBuilder(this.getBackgroundTexture(), 55, 36, 18, backgroundHeight).addPadding(0, 0, 0, textPadding + maxStringWidth).build();
//        this.icon = helper.drawableBuilder(this.getIconTexture(), 0, 0, 14, 14).setTextureSize(14, 14).build();
//
//        this.cachedFuelIndicator = CacheBuilder.newBuilder().maximumSize(25)
//            .build(new CacheLoader<>() {
//                @Override
//                public IDrawableAnimated load(Integer burnTime) {
//                    return helper.drawableBuilder(AbstractFuelCategory.this.getIconTexture(), 0, 0, 14, 14).setTextureSize(14, 14).buildAnimated(burnTime, IDrawableAnimated.StartDirection.TOP, true);
//                }
//            });
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return this.background;
//    }
//
//    @Override
//    public IDrawable getIcon() {
//        return this.icon;
//    }
//
//    public abstract ResourceLocation getBackgroundTexture();
//
//    public abstract ResourceLocation getIconTexture();
//
//    @Override
//    public void setRecipe(IRecipeLayoutBuilder builder, FuelRecipe recipe, IFocusGroup focuses) {
//        builder.addSlot(RecipeIngredientRole.INPUT, 1, 17).addItemStacks(recipe.getInput());
//    }
//
//    @Override
//    public void draw(FuelRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
//        int burnTime = recipe.getBurnTime();
//        IDrawableAnimated fuelIndicator = this.cachedFuelIndicator.getUnchecked(burnTime);
//        fuelIndicator.draw(guiGraphics, 2, -1);
//
//        Font font = Minecraft.getInstance().font;
//        Component burnTimeText = createBurnTimeText(recipe.getBurnTime(), recipe.getUsage().getName());
//        int stringWidth = font.width(burnTimeText);
//        guiGraphics.drawString(font, burnTimeText, this.background.getWidth() - stringWidth, 14, 0xFF808080, false);
//    }
//
//    private static Component createBurnTimeText(int burnTime, Component usage) {
//        return Component.translatable("gui.jei.category.smelting.time.seconds", burnTime / 20).append(" (").append(usage).append(")");
//    }
//}
