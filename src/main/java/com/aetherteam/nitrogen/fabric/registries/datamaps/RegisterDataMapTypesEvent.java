/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.registries.datamaps;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Event fired on the mod event bus, in order to register {@link DataMapType data map types}.
 */
public class RegisterDataMapTypesEvent {
    public static final Event<CallBack> EVENT = EventFactory.createArrayBacked(RegisterDataMapTypesEvent.CallBack.class, callBacks -> event -> {
        for (var callBack : callBacks) callBack.registerMaps(event);
    });

    private final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> attachments;

    @ApiStatus.Internal
    public RegisterDataMapTypesEvent(Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> attachments) {
        this.attachments = attachments;
    }

    /**
     * Register a registry data map.
     *
     * @param type the data map type to register
     * @param <T>  the type of the data map
     * @param <R>  the type of the registry
     * @throws IllegalArgumentException      if a type with the same ID has already been registered for that registry
     * @throws UnsupportedOperationException if the registry is a non-synced datapack registry and the data map is synced
     */
    public <T, R> void register(DataMapType<R, T> type) {
        final var registry = type.registryKey();
        if (DynamicRegistries.getDynamicRegistries().stream().anyMatch(data -> data.key().equals(registry))) {
            if (type.networkCodec() != null && !RegistrySynchronization.NETWORKABLE_REGISTRIES.contains(registry)) {
                throw new UnsupportedOperationException("Cannot register synced data map " + type.id() + " for datapack registry " + registry.location() + " that is not synced!");
            }
        }

        final var map = attachments.computeIfAbsent((ResourceKey) registry, k -> new HashMap<>());
        if (map.containsKey(type.id())) {
            throw new IllegalArgumentException("Tried to register data map type with ID " + type.id() + " to registry " + registry.location() + " twice");
        }
        map.put(type.id(), type);
    }

    public interface CallBack {
        void registerMaps(RegisterDataMapTypesEvent event);
    }
}
