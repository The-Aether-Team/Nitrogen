package com.aetherteam.nitrogen.integration.rei.categories;

import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;

public abstract class AbstractRecipeCategory<T extends Display> implements DisplayCategory<T> {
    protected final String id;
    protected final CategoryIdentifier<T> uid;
    protected final Renderer background;
    protected final Renderer icon;

    public AbstractRecipeCategory(String id, CategoryIdentifier<T> uid, Renderer background, Renderer icon) {
        this.id = id;
        this.uid = uid;
        this.background = background;
        this.icon = icon;
    }

    @Override
    public CategoryIdentifier<T> getCategoryIdentifier() {
        return this.uid;
    }

    @Override
    public Renderer getIcon() {
        return this.icon;
    }
}
