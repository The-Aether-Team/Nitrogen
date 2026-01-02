package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.events.ItemAttributeModifierHelper;
import com.aetherteam.nitrogen.fabric.pond.ItemStackExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.function.TriConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackExtension {
    @WrapOperation(
        method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers;forEach(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V")
    )
    private void nitrogen_fabric$modifyAttributeEvent_1(ItemAttributeModifiers instance, EquipmentSlot equipmentSlot, BiConsumer<Holder<Attribute>, AttributeModifier> action, Operation<Void> original) {
        original.call(ItemAttributeModifierHelper.invokeEvent((ItemStack) (Object) this, instance).toRecord(), equipmentSlot, action);
    }


    @WrapOperation(
        method = "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers;forEach(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V")
    )
    private void nitrogen_fabric$modifyAttributeEvent_2(ItemAttributeModifiers instance, EquipmentSlotGroup slotGroup, TriConsumer<Holder<Attribute>, AttributeModifier, ItemAttributeModifiers.Display> action, Operation<Void> original) {
        original.call(ItemAttributeModifierHelper.invokeEvent((ItemStack) (Object) this, instance).toRecord(), slotGroup, action);
    }

    @Override
    public int nitrogen_fabric$getEnchantmentLevel(Holder<Enchantment> enchantment) {
        return ((ItemStack) (Object) this).getEnchantments().getLevel(enchantment);
    }

    @Override
    public Set<Object2IntMap.Entry<Holder<Enchantment>>> nitrogen_fabric$getAllEnchantments(HolderLookup.RegistryLookup<Enchantment> registry) {
        return ((ItemStack) (Object) this).getEnchantments().entrySet();
    }
}
