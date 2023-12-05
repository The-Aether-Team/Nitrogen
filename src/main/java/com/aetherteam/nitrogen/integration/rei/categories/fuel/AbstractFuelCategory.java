package com.aetherteam.nitrogen.integration.rei.categories.fuel;

import com.aetherteam.nitrogen.integration.rei.displays.FuelDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;

/**
 * An abstract class for REI fuel recipe categories.
 */
public abstract class AbstractFuelCategory implements DisplayCategory<FuelDisplay> {
    private final Renderer background;
    private final Renderer icon;
//    private final LoadingCache<Integer, IDrawableAnimated> cachedFuelIndicator;

    public AbstractFuelCategory(List<String> craftingStations) {
        String longestString = craftingStations.stream().max(Comparator.comparingInt(String::length)).get();
        Component longestStationName = Component.literal(longestString);

        Font fontRenderer = Minecraft.getInstance().font;
        Component maxBurnTimeText = createBurnTimeText(10000, longestStationName);
        int maxStringWidth = fontRenderer.width(maxBurnTimeText.getString());
        int backgroundHeight = 34;
        int textPadding = 20;

        this.background = Widgets.padded(0, 0, 0, textPadding + maxStringWidth, Widgets.withBounds(Widgets.createTexturedWidget(this.getTexture(), 0, 0, 55, 36, 18, backgroundHeight)));
        this.icon = Widgets.createTexturedWidget(this.getTexture(), 0, 0, 176, 0, 14, 13);

//        this.cachedFuelIndicator = CacheBuilder.newBuilder().maximumSize(25) TODO: PR animated api to rei
//                .build(new CacheLoader<>() {
//                    @Override
//                    public IDrawableAnimated load(Integer burnTime) {
//                        return helper.drawableBuilder(AbstractFuelCategory.this.getTexture(), 176, 0, 14, 13).buildAnimated(burnTime, IDrawableAnimated.StartDirection.TOP, true);
//                    }
//                });
    }

//    @Override
//    public IDrawable getBackground() {
//        return this.background;
//    }

    @Override
    public Renderer getIcon() {
        return this.icon;
    }

    public abstract ResourceLocation getTexture();

    private static Component createBurnTimeText(int burnTime, Component usage) {
        return Component.translatable("gui.jei.category.smelting.time.seconds", burnTime / 20).append(" (").append(usage).append(")");
    }
}
