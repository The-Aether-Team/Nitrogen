package com.aetherteam.nitrogen.fabric.client.events;

import com.aetherteam.nitrogen.fabric.client.dim.ReceivingLevelScreenFactory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientDimensionEvents {

    public static final Event<OnLevelTransitionScreen> ON_LEVEL_TRANSITION_SCREEN = EventFactory.createArrayBacked(OnLevelTransitionScreen.class, invokers -> (toDimension, fromDimension) -> {
        for (var invoker : invokers) {
            var factory = invoker.getAlternativeScreen(toDimension, fromDimension);

            if (factory != null) return factory;
        }

        return null;
    });

    public interface OnLevelTransitionScreen {
        @Nullable ReceivingLevelScreenFactory getAlternativeScreen(@Nullable ResourceKey<Level> toDimension, @Nullable ResourceKey<Level> fromDimension);
    }
}
