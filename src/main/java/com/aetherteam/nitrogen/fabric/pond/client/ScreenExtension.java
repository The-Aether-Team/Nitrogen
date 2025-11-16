package com.aetherteam.nitrogen.fabric.pond.client;

import com.aetherteam.nitrogen.fabric.mixin.client.fabric.ButtonListMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public interface ScreenExtension {

    default Minecraft nitrogen_fabric$getMinecraft() {
        return throwUnimplementedException();
    }

    @Nullable
    default Slot nitrogen_fabric$getSlotUnderMouse() {
        return null;
    }

    default int nitrogen_fabric$getGuiLeft() {
        return 0;
    }

    default int nitrogen_fabric$getGuiTop() {
        return 0;
    }

    default int nitrogen_fabric$getXSize() {
        return 0;
    }

    default int nitrogen_fabric$getYSize() {
        return 0;
    }

    private static <T> T throwUnimplementedException() {
        throw new IllegalStateException("Injected Interface method not implement!");
    }

    ///
    /// Method invoked within [ButtonListMixin]
    ///
    default AbstractWidget onScreensWidgetAdd(AbstractWidget abstractWidget) {
        return abstractWidget;
    }
}
