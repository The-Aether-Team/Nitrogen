package com.aetherteam.nitrogen.event.listeners;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Nitrogen.MODID)
public class TooltipListeners {
    public static Map<Supplier<Item>, TooltipPredicate> PREDICATES = new HashMap<>();

    @SubscribeEvent
    public static void onTooltipCreationLowPriority(ItemTooltipEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        List<Component> itemTooltips = event.getToolTip();
        addAbilityTooltips(player, itemStack, itemTooltips);
    }

    public static void addAbilityTooltips(Player player, ItemStack stack, List<Component> components) {
        TooltipPredicate predicate = null;
        for (Map.Entry<Supplier<Item>, TooltipPredicate> entry : PREDICATES.entrySet()) {
            if (entry.getKey().get() == stack.getItem()) {
                predicate = entry.getValue();
                break;
            }
        }
        for (int i = 1; i <= 5; i++) {
            String string = stack.getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + i;
            if (I18n.exists(string)) {
                Component component = Component.translatable(string);
                if (predicate != null) {
                    component = predicate.override(player, stack, components, component);
                }
                components.add(i, component);
            }
        }
    }

    @FunctionalInterface
    public interface TooltipPredicate {
        Component override(Player player, ItemStack stack, List<Component> components,  Component component);
    }
}
