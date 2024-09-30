package com.aetherteam.nitrogen.event.listeners;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Nitrogen.MODID)
public class TooltipListeners {
    public static Map<ItemHolder, TooltipPredicate> PREDICATES = new HashMap<>();

    @SubscribeEvent
    public static void onTooltipCreationLowPriority(ItemTooltipEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        List<Component> itemTooltips = event.getToolTip();
        addAbilityTooltips(player, itemStack, itemTooltips);
    }

    public static void addAbilityTooltips(Player player, ItemStack stack, List<Component> components) {
        for (int i = 1; i <= 5; i++) {
            String string = stack.getDescriptionId() + "." + Nitrogen.MODID + ".ability.tooltip." + i;
            if (I18n.exists(string)) {
                Component component = Component.translatable(string);
                ItemHolder holder = new ItemHolder(stack::getItem);
                if (PREDICATES.containsKey(holder)) {
                    component = PREDICATES.get(holder).override(player, stack, components, component);
                }
                components.add(i, component);
            }
        }
    }

    public record ItemHolder(Supplier<Item> supplier) {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Supplier<?> other) {
                return this.supplier().get() == other.get() || this.supplier().get().equals(other.get());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.supplier().get().hashCode();
        }
    }

    @FunctionalInterface
    public interface TooltipPredicate {
        Component override(Player player, ItemStack stack, List<Component> components,  Component component);
    }
}
