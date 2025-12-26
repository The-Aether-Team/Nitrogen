package com.aetherteam.nitrogen.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableLong;

public class LevelEvents {
    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, invokers -> entity -> {
        for (var invoker : invokers) invoker.beforeTick(entity);
    });

    public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, invokers -> entity -> {
        for (var invoker : invokers) invoker.afterTick(entity);
    });

    public static final Event<OnTimeUpdate> ON_TIME_UPDATE = EventFactory.createArrayBacked(OnTimeUpdate.class, invokers -> (dayTime, time) -> {
        for (var invoker : invokers) invoker.updateTime(dayTime, time);
    });

    public interface Before {
        void beforeTick(Level level);
    }

    public interface After {
        void afterTick(Level level);
    }

    public interface OnTimeUpdate {
        void updateTime(long dayTime, MutableLong time);
    }
}
