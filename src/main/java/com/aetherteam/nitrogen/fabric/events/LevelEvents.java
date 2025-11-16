package com.aetherteam.nitrogen.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;

public class LevelEvents {
    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, invokers -> entity -> {
        for (var invoker : invokers) invoker.beforeTick(entity);
    });

    public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, invokers -> entity -> {
        for (var invoker : invokers) invoker.afterTick(entity);
    });

    public interface Before {
        void beforeTick(Level level);
    }

    public interface After {
        void afterTick(Level level);
    }
}
