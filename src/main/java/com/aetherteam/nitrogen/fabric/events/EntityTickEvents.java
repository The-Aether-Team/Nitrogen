package com.aetherteam.nitrogen.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class EntityTickEvents {

    public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, invokers -> (entity, isCancelled) -> {
        for (var invoker : invokers) invoker.beforeTick(entity, isCancelled);
    });

    public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, invokers -> entity -> {
        for (var invoker : invokers) invoker.afterTick(entity);
    });

    public interface Before {
        void beforeTick(Entity entity, MutableBoolean isCancelled);
    }

    public interface After {
        void afterTick(Entity entity);
    }
}
