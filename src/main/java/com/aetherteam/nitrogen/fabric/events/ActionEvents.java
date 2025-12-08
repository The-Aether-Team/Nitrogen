package com.aetherteam.nitrogen.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ActionEvents {

    public static final Event<OnBlockStrip> ON_BLOCK_STRIP = EventFactory.createArrayBacked(OnBlockStrip.class, invokers -> (levelAccessor, unstrippedState, itemStack, context) -> {
        for (var invoker : invokers) {
            invoker.onStrip(levelAccessor, unstrippedState, itemStack, context);
        }
    });

    public interface OnBlockStrip {
        void onStrip(LevelAccessor levelAccessor, BlockState unstrippedState, ItemStack itemStack, UseOnContext context);
    }
}
