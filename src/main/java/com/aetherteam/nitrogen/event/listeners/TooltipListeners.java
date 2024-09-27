package com.aetherteam.nitrogen.event.listeners;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = Nitrogen.MODID)
public class TooltipListeners {
    public static Map<Holder<Item>, TooltipPredicate> PREDICATES = new HashMap<>();

    @SubscribeEvent
    public static void onTooltipCreationLowPriority(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        List<Component> itemTooltips = event.getToolTip();
        addAbilityTooltips(itemStack, itemTooltips);
    }

    public static void addAbilityTooltips(ItemStack stack, List<Component> components) {
        for (int i = 1; i <= 5; i++) {
            String string = stack.getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + i;
            if (I18n.exists(string)) {
                Component component = Component.translatable(string);
                if (PREDICATES.containsKey(stack.getItemHolder())) {
                    component = PREDICATES.get(stack.getItemHolder()).override(stack, components, component);
                }
                components.add(i, component);
            }
        }
    }

    @FunctionalInterface
    public interface TooltipPredicate {
        Component override(ItemStack itemStack, List<Component> components, Component component);
    }
}
