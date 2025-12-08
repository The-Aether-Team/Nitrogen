package com.aetherteam.nitrogen.fabric.mixin.client.fabric;

import com.aetherteam.nitrogen.fabric.pond.client.ButtonListExtension;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ButtonList.class)
public abstract class ButtonListMixin implements ButtonListExtension {
    private Screen nitrogen_fabric$screen = null;

    @Override
    public void nitrogen_fabric$setScreen(Screen screen) {
        this.nitrogen_fabric$screen = screen;
    }

    @WrapMethod(method = "set(ILnet/minecraft/client/gui/components/AbstractWidget;)Lnet/minecraft/client/gui/components/AbstractWidget;")
    private AbstractWidget nitrogen_fabric$hookButtonAdd1(int index, AbstractWidget element, Operation<AbstractWidget> original) {
        if (nitrogen_fabric$screen != null) {
            var newElement = nitrogen_fabric$screen.nitrogen_fabric$onScreensWidgetAdd(element);

            if (newElement == null) return element;

            element = newElement;
        }

        return original.call(index, element);
    }

    @WrapMethod(method = "add(ILnet/minecraft/client/gui/components/AbstractWidget;)V")
    private void nitrogen_fabric$hookButtonAdd2(int index, AbstractWidget element, Operation<AbstractWidget> original) {
        if (nitrogen_fabric$screen != null) {
            var newElement = nitrogen_fabric$screen.nitrogen_fabric$onScreensWidgetAdd(element);

            if (newElement == null) return;

            element = newElement;
        }

        original.call(index, element);
    }
}
