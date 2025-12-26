/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.registries.datamaps;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public class DataMapsUpdatedEvent {
    public static final Event<CallBack> EVENT = EventFactory.createArrayBacked(CallBack.class, callBacks -> event -> {
        for (var callBack : callBacks) callBack.onUpdate(event);
    });

    private final RegistryAccess registryAccess;
    private final Registry<?> registry;
    private final UpdateCause cause;

    @ApiStatus.Internal
    public DataMapsUpdatedEvent(RegistryAccess registryAccess, Registry<?> registry, UpdateCause cause) {
        this.registryAccess = registryAccess;
        this.registry = registry;
        this.cause = cause;
    }

    /**
     * {@return a registry access}
     */
    public RegistryAccess getRegistries() {
        return registryAccess;
    }

    /**
     * {@return the registry that had its data maps updated}
     */
    public Registry<?> getRegistry() {
        return registry;
    }

    /**
     * {@return the key of the registry that had its data maps updated}
     */
    public ResourceKey<? extends Registry<?>> getRegistryKey() {
        return registry.key();
    }

    /**
     * Runs the given {@code consumer} if the registry is of the given {@code type}.
     *
     * @param type     the registry key
     * @param consumer the consumer
     * @param <T>      the registry type
     */
    @SuppressWarnings("unchecked")
    public <T> void ifRegistry(ResourceKey<Registry<T>> type, Consumer<Registry<T>> consumer) {
        if (getRegistryKey() == type) {
            consumer.accept((Registry<T>) registry);
        }
    }

    /**
     * {@return the reason for the update}
     */
    public UpdateCause getCause() {
        return cause;
    }

    public enum UpdateCause {
        /**
         * The data maps have been synced to the client.
         *
         * @implNote An event with this cause is <i>not</i> fired for the host of a single-player world, or for any {@linkplain Connection#isMemoryConnection() in-memory} connections.
         */
        CLIENT_SYNC,
        /**
         * The data maps have been reloaded on the server.
         *
         * @implNote Events with this cause are fired during the {@linkplain SimplePreparableReloadListener#apply(Object, ResourceManager, ProfilerFiller) apply phase}
         *           of a reload listener, and <strong>not</strong> after the reload is complete.
         */
        SERVER_RELOAD
    }

    public interface CallBack {
        void onUpdate(DataMapsUpdatedEvent event);
    }
}
