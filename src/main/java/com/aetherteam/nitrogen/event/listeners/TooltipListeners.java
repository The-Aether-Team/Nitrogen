package com.aetherteam.nitrogen.event.listeners;

import com.aetherteam.nitrogen.Nitrogen;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TooltipListeners {
    public static Map<Holder<Item>, TooltipPredicate> PREDICATES = new HashMap<>();

    public static void onTooltipCreationLowPriority() {
        ItemTooltipCallback.EVENT.register((itemStack, context, tooltipType, itemTooltips) -> {
            addAbilityTooltips(Minecraft.getInstance().player, itemStack, itemTooltips, context);
        });
    }

    public static void addAbilityTooltips(Player player, ItemStack stack, List<Component> components, Item.TooltipContext context) {
        for (int i = 1; i <= 5; i++) {
            String string = stack.getItem().getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + i;
            if (I18n.exists(string)) {
                Component component = Component.translatable(string);
                if (PREDICATES.containsKey(stack.getItemHolder())) {
                    component = PREDICATES.get(stack.getItemHolder()).override(player, stack, components, context, component);
                }
                components.add(i, component);
            }
        }
    }

    @FunctionalInterface
    public interface TooltipPredicate {
        Component override(Player player, ItemStack stack, List<Component> components, Item.TooltipContext context, Component component);
    }
}
