package com.aetherteam.nitrogen.fabric.mixin.fabric;

import com.aetherteam.nitrogen.fabric.conditions.NeoConditionConversion;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ResourceConditionsImpl.class)
public abstract class ResourceConditionsImplMixin {
    @WrapOperation(method = "applyResourceConditions", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;has(Ljava/lang/String;)Z"))
    private static boolean allowNeoConditions(JsonObject instance, String memberName, Operation<Boolean> original) {
        return original.call(instance, memberName) || original.call(instance, NeoConditionConversion.NEO_CONDITIONS_KEY);
    }

    @WrapOperation(method = "applyResourceConditions", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonObject;get(Ljava/lang/String;)Lcom/google/gson/JsonElement;"))
    private static JsonElement getPossibleNeoConditions(JsonObject instance, String memberName, Operation<JsonElement> original) {
        var value = original.call(instance, memberName);

        if (value == null) {
            value = original.call(instance, NeoConditionConversion.NEO_CONDITIONS_KEY);
        }

        return value;
    }
}
