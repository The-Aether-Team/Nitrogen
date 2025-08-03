package com.aetherteam.nitrogen.integration.rei.categories;

//import me.shedaniel.math.Point;
//import me.shedaniel.math.Rectangle;
//import me.shedaniel.rei.api.client.gui.Renderer;
//import me.shedaniel.rei.api.client.gui.widgets.Widget;
//import me.shedaniel.rei.api.client.gui.widgets.Widgets;
//import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import me.shedaniel.rei.api.common.display.Display;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class AbstractRecipeCategory<T extends Display> implements DisplayCategory<T> {
//    protected final String id;
//    protected final CategoryIdentifier<T> uid;
//
//    protected final int width;
//    protected final int height;
//
//    protected final int padding = 5;
//
//    protected final Renderer icon;
//
//    public AbstractRecipeCategory(String id, CategoryIdentifier<T> uid, int width, int height, Renderer icon) {
//        this.id = id;
//        this.uid = uid;
//        this.width = width;
//        this.height = height;
//        this.icon = icon;
//    }
//
//    @Override
//    public CategoryIdentifier<T> getCategoryIdentifier() {
//        return this.uid;
//    }
//
//    @Override
//    public Renderer getIcon() {
//        return this.icon;
//    }
//
//    @Override
//    public List<Widget> setupDisplay(T display, Rectangle bounds) {
//        List<Widget> widgets = new ArrayList<>();
//        widgets.add(Widgets.createRecipeBase(new Rectangle(bounds.x, bounds.y, getDisplayWidth(display), getDisplayHeight())));
//        return widgets;
//    }
//
//    @Override
//    public int getDisplayHeight() {
//        return this.height + (this.padding * 2);
//    }
//
//    @Override
//    public int getDisplayWidth(T display) {
//        return this.width + (this.padding * 2);
//    }
//
//    protected Point startingOffset(Rectangle bounds) {
//        return new Point(bounds.x + this.padding, bounds.y + this.padding);
//    }
//}
