package com.aetherteam.nitrogen.integration.rei.categories.fuel;

import com.aetherteam.nitrogen.integration.rei.categories.AbstractRecipeCategory;
import com.aetherteam.nitrogen.integration.rei.displays.FuelDisplay;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableDouble;

import java.util.List;

public abstract class AbstractFuelCategory extends AbstractRecipeCategory<FuelDisplay> {
    private final ResourceLocation texture;

    public AbstractFuelCategory(CategoryIdentifier<FuelDisplay> categoryIdentifier, ResourceLocation texture) {
        super("", categoryIdentifier, 140, 37, (graphics, bounds, mouseX, mouseY, delta) -> {
            graphics.blit(texture, bounds.x + 1, bounds.y, 176, 0, 14, 13);
        });
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public List<Widget> setupDisplay(FuelDisplay display, Rectangle bounds) {
        var widgets = super.setupDisplay(display, bounds);

        widgets.add(
                Widgets.createLabel(new Point(bounds.x + 26, bounds.getMaxY() - 15), createBurnTimeText(display.getBurnTime(), display.getUsage().getName()))
                        .color(0xFF404040, 0xFFBBBBBB).noShadow().leftAligned()
        );

        var burnTime = display.getBurnTime();
        var lastTick = new MutableDouble(0);

        var startingPoint = startingOffset(bounds);

        widgets.add(
                Widgets.wrapRenderer(
                        new Rectangle(startingPoint.x + 2, startingPoint.y + 4, 14, 11),
                        (graphics, bound, mouseX, mouseY, delta) -> {
                            lastTick.getAndAdd(delta);

                            if (lastTick.getValue() > burnTime) {
                                lastTick.setValue(0);
                            }

                            var height = (int) Math.round(11 * (lastTick.getValue() / burnTime));

                            graphics.blit(this.getTexture(), bound.x, bound.y, 190, 3, 13, 11);

                            var yPosition = bound.y + height;

                            graphics.enableScissor(bound.x, yPosition, bound.x + 14, yPosition + 11);
                            graphics.blit(this.getTexture(), bound.x, bound.y, 176, 3, 14, 11);
                            graphics.disableScissor();
                        }
                )
        );

        widgets.add(Widgets.createSlot(new Point(bounds.x + 6, startingPoint.y + 18)).entries(display.getInputEntries().get(0)).markInput());

        return widgets;
    }

    private static Component createBurnTimeText(int burnTime, Component usage) {
        return Component.translatable("category.rei.campfire.time", burnTime / 20).append(" (").append(usage).append(")");
    }
}
