package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.pond.client.ScreenExtension;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements ScreenExtension {
    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    public int imageWidth;

    @Shadow
    public int imageHeight;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Override
    public int nitrogen_fabric$getGuiLeft() {
        return this.leftPos;
    }

    @Override
    public int nitrogen_fabric$getGuiTop() {
        return this.topPos;
    }

    @Override
    public int nitrogen_fabric$getXSize() {
        return this.imageWidth;
    }

    @Override
    public int nitrogen_fabric$getYSize() {
        return this.imageHeight;
    }

    @Override
    @Nullable
    public Slot nitrogen_fabric$getSlotUnderMouse() {
        return this.hoveredSlot;
    }
}
