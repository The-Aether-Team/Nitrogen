package com.aetherteam.nitrogen.integration.rei.categories.fuel;

//import com.aetherteam.nitrogen.integration.rei.categories.AbstractRecipeCategory;
//import com.aetherteam.nitrogen.integration.rei.displays.FuelDisplay;
//import me.shedaniel.math.Point;
//import me.shedaniel.math.Rectangle;
//import me.shedaniel.rei.api.client.gui.widgets.Widget;
//import me.shedaniel.rei.api.client.gui.widgets.Widgets;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import org.apache.commons.lang3.mutable.MutableDouble;
//
//import java.util.List;
//
//public abstract class AbstractFuelCategory extends AbstractRecipeCategory<FuelDisplay> {
//    private final ResourceLocation texture;
//    private final ResourceLocation backgroundTexture;
//
//    public AbstractFuelCategory(CategoryIdentifier<FuelDisplay> categoryIdentifier, ResourceLocation texture, ResourceLocation backgroundTexture) {
//        super("", categoryIdentifier, 140, 37, (graphics, bounds, mouseX, mouseY, delta) -> graphics.blitSprite(texture, bounds.x + 1, bounds.y, 14, 14));
//        this.texture = texture;
//        this.backgroundTexture = backgroundTexture;
//    }
//
//    public ResourceLocation getTexture() {
//        return this.texture;
//    }
//
//    public ResourceLocation getBackgroundTexture() {
//        return this.backgroundTexture;
//    }
//
//    @Override
//    public List<Widget> setupDisplay(FuelDisplay display, Rectangle bounds) {
//        List<Widget> widgets = super.setupDisplay(display, bounds);
//
//        widgets.add(
//                Widgets.createLabel(new Point(bounds.x + 26, bounds.getMaxY() - 15), createBurnTimeText(display.getBurnTime(), display.getUsage().getName()))
//                        .color(0xFF404040, 0xFFBBBBBB).noShadow().leftAligned()
//        );
//
//        int burnTime = display.getBurnTime();
//        MutableDouble lastTick = new MutableDouble(0);
//
//        Point startingPoint = startingOffset(bounds);
//
//        widgets.add(
//                Widgets.wrapRenderer(
//                        new Rectangle(startingPoint.x + 2, startingPoint.y + 4, 14, 14),
//                        (graphics, bound, mouseX, mouseY, delta) -> {
//                            lastTick.getAndAdd(delta);
//
//                            if (lastTick.getValue() > burnTime) {
//                                lastTick.setValue(0);
//                            }
//
//                            int height = (int) Math.round(11 * (lastTick.getValue() / burnTime));
//
//                            graphics.blitSprite(this.getBackgroundTexture(), bound.x - 1, bound.y - 3, 14, 14);
//
//                            int yPosition = bound.y + height;
//
//                            graphics.enableScissor(bound.x, yPosition - 3, bound.x + 14, yPosition + 11);
//                            graphics.blitSprite(this.getTexture(), bound.x, bound.y - 3, 14, 14);
//                            graphics.disableScissor();
//                        }
//                )
//        );
//
//        widgets.add(Widgets.createSlot(new Point(bounds.x + 6, startingPoint.y + 18)).entries(display.getInputEntries().get(0)).markInput());
//
//        return widgets;
//    }
//
//    private static Component createBurnTimeText(int burnTime, Component usage) {
//        return Component.translatable("category.rei.campfire.time", burnTime / 20).append(" (").append(usage).append(")");
//    }
//}
