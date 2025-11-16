package com.aetherteam.nitrogen.fabric.mixin.client.fabric;

import com.aetherteam.nitrogen.fabric.pond.client.ButtonListExtension;
import com.aetherteam.nitrogen.fabric.pond.client.ScreenExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = Screen.class, priority = 1100)
public abstract class ScreenMixin implements ScreenExtension {
    @Shadow
    @Nullable
    protected Minecraft minecraft;

    @Override
    public Minecraft nitrogen_fabric$getMinecraft() {
        return this.minecraft;
    }

    @WrapOperation(method = "fabric_getButtons", at = @At(value = "NEW", target = "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)Lnet/fabricmc/fabric/impl/client/screen/ButtonList;", remap = false), remap = false)
    private ButtonList nitrogen_fabric$addScreenForEventHook(List drawables, List selectables, List children, Operation<ButtonList> original) {
        var list = original.call(drawables, selectables, children);

        ((ButtonListExtension) (Object) list).setScreen((Screen) (Object) this);

        return list;
    }
}
