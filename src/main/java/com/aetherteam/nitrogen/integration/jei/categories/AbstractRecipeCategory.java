package com.aetherteam.nitrogen.integration.jei.categories;

//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.resources.ResourceLocation;
//
///**
// * A basic abstract class for JEI recipe categories.
// */
//public abstract class AbstractRecipeCategory<T> implements IRecipeCategory<T> {
//    protected final String id;
//    protected final ResourceLocation uid;
//    protected final IDrawable background;
//    protected final IDrawable icon;
//    protected final RecipeType<T> recipeType;
//
//    public AbstractRecipeCategory(String id, ResourceLocation uid, IDrawable background, IDrawable icon, RecipeType<T> recipeType) {
//        this.id = id;
//        this.uid = uid;
//        this.background = background;
//        this.icon = icon;
//        this.recipeType = recipeType;
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
//    public ResourceLocation getUid() {
//        return this.uid;
//    }
//
//    @Override
//    public RecipeType<T> getRecipeType() {
//        return this.recipeType;
//    }
//}
