package com.aetherteam.nitrogen.fabric.mixin.client.fabric;

import com.aetherteam.nitrogen.fabric.client.events.ScreenKeyboardEventsExtension;
import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = KeyboardHandler.class, priority = 1500)
public abstract class KeyboardMixinMixin {
    @TargetHandler(mixin = "net.fabricmc.fabric.mixin.screen.KeyboardMixin", name = "invokeKeyPressedEvents")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;call([Ljava/lang/Object;)Ljava/lang/Object;"))
    private <R> R nitrogen_fabric$savePressedResult(Operation instance, Object[] objects, Operation<R> original, @Local(argsOnly = true) Screen screen) {
        var bl = original.call(instance, objects);

        ScreenKeyboardEventsExtension.setPressedResult(screen, (boolean) bl);

        return bl;
    }

    @TargetHandler(mixin = "net.fabricmc.fabric.mixin.screen.KeyboardMixin", name = "invokeKeyReleasedEvents")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lcom/llamalad7/mixinextras/injector/wrapoperation/Operation;call([Ljava/lang/Object;)Ljava/lang/Object;"))
    private <R> R nitrogen_fabric$saveReleasedResult(Operation instance, Object[] objects, Operation<R> original, @Local(argsOnly = true) Screen screen) {
        var bl = original.call(instance, objects);

        ScreenKeyboardEventsExtension.setReleasedResult(screen, (boolean) bl);

        return bl;
    }
}
