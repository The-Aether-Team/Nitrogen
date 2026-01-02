package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.ItemAttributeModifierHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.AttributeModifiersPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SingleComponentItemPredicate.class)
public interface ItemAttributeModifiersPredicateMixin<T> {
    @WrapOperation(
        method = "matches(Lnet/minecraft/core/component/DataComponentGetter;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentGetter;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;")
    )
    private T nitrogen_fabric$modifyAttributeEvent(DataComponentGetter instance, DataComponentType<? extends T> dataComponentType, Operation<T> original) {
        var value = original.call(instance, dataComponentType);

        if (instance instanceof ItemStack stack) {
            if (((SingleComponentItemPredicate<T>) (Object) this) instanceof AttributeModifiersPredicate) {
                value = (T) ItemAttributeModifierHelper.invokeEvent(stack, (ItemAttributeModifiers) value).toRecord();
            }
        }

        return value;
    }
}
