package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.ItemAttributeModifierHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin {
    @WrapOperation(method = "getApproximateAttackDamageWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object nitrogen_fabric$modifyAttributeEvent(ItemStack instance, DataComponentType dataComponentType, Object object, Operation<Object> original) {
        var attributeInstance = (ItemAttributeModifiers) original.call(instance, dataComponentType, object);

        var event = ItemAttributeModifierHelper.invokeEvent(instance, attributeInstance);

        return new ItemAttributeModifiers(event.getModifiers(), attributeInstance.showInTooltip());
    }
}
