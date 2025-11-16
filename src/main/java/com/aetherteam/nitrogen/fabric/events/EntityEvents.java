package com.aetherteam.nitrogen.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

public class EntityEvents {

    public static final Event<StruckByLightningEvent> STRUCK_BY_LIGHTNING = EventFactory.createArrayBacked(StruckByLightningEvent.class, invokers -> (entity, lightning, callback) -> {
        for (var invoker : invokers) invoker.onStrike(entity, lightning, callback);
    });

    public static final Event<BeforeDimensionChange> BEFORE_DIMENSION_CHANGE = EventFactory.createArrayBacked(BeforeDimensionChange.class, invokers -> (entity, targetDimension) -> {
        for (var invoker : invokers) invoker.beforeChange(entity, targetDimension);
    });

    public static final Event<EntityMount> ENTITY_MOUNT = EventFactory.createArrayBacked(EntityMount.class, invokers -> (entityMounting, entityBeingMounted, isDismounting, callback) -> {
        for (var invoker : invokers) invoker.onMount(entityMounting, entityBeingMounted, isDismounting, callback);
    });

    public interface StruckByLightningEvent {
        void onStrike(Entity entity, LightningBolt lightning, CancellableCallback callback);
    }

    public interface BeforeDimensionChange {
        void beforeChange(Entity entity, ResourceKey<Level> targetDimension);
    }

    public interface EntityMount {
        void onMount(Entity entityMounting, Entity entityBeingMounted, boolean isDismounting, CancellableCallback callback);
    }

}
