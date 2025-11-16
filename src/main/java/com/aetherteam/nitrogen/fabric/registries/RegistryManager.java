/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.registries;

import com.aetherteam.nitrogen.fabric.mixin.accessor.ConnectionAccessor;
import com.aetherteam.nitrogen.fabric.mixin.accessor.ServerCommonPacketListenerImplAccessor;
import com.aetherteam.nitrogen.fabric.network.PacketDistributor;
import com.aetherteam.nitrogen.fabric.network.payload.KnownRegistryDataMapsReplyPayload;
import com.aetherteam.nitrogen.fabric.network.payload.RegistryDataMapSyncPayload;
import com.aetherteam.nitrogen.fabric.network.tasks.RegistryDataMapNegotiation;
import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import com.aetherteam.nitrogen.fabric.registries.datamaps.RegisterDataMapTypesEvent;
import io.netty.util.AttributeKey;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class RegistryManager {
    private static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMaps = Map.of();

    @Nullable
    public static <R> DataMapType<R, ?> getDataMap(ResourceKey<? extends Registry<R>> registry, ResourceLocation key) {
        final var map = dataMaps.get(registry);
        return map == null ? null : (DataMapType<R, ?>) map.get(key);
    }

    /**
     * {@return a view of all registered data maps}
     */
    public static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> getDataMaps() {
        return dataMaps;
    }

    public static void initDataMaps() {
        final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMapTypes = new HashMap<>();
        RegisterDataMapTypesEvent.EVENT.invoker().registerMaps(new RegisterDataMapTypesEvent(dataMapTypes));
        dataMaps = new IdentityHashMap<>();
        dataMapTypes.forEach((key, values) -> dataMaps.put(key, Collections.unmodifiableMap(values)));
        dataMaps = Collections.unmodifiableMap(dataMapTypes);
    }

    public static final AttributeKey<Map<ResourceKey<? extends Registry<?>>, Collection<ResourceLocation>>> ATTRIBUTE_KNOWN_DATA_MAPS = AttributeKey.valueOf("neoforge:known_data_maps");

    @ApiStatus.Internal
    public static void handleKnownDataMapsReply(final KnownRegistryDataMapsReplyPayload payload, ServerConfigurationNetworking.Context context) {
        var channel = ((ConnectionAccessor) ((ServerCommonPacketListenerImplAccessor) context.networkHandler()).nitrogen_fabric$connection()).nitrogen_fabric$getChannel();
        channel.attr(RegistryManager.ATTRIBUTE_KNOWN_DATA_MAPS).set(payload.dataMaps());
        context.networkHandler().completeTask(RegistryDataMapNegotiation.TYPE);
    }

    public static <T> void handleSync(ServerPlayer player, Registry<T> registry, Collection<ResourceLocation> attachments) {
        if (attachments.isEmpty() || !(registry instanceof MappedRegistry<T> mappedRegistry)) return;
        final Map<ResourceLocation, Map<ResourceKey<T>, ?>> att = new HashMap<>();
        attachments.forEach(key -> {
            final var attach = RegistryManager.getDataMap(registry.key(), key);
            if (attach == null || attach.networkCodec() == null) return;
            att.put(key, mappedRegistry.nitrogen_fabric$getDataMap(attach));
        });
        if (!att.isEmpty()) {
            PacketDistributor.sendToPlayer(player, new RegistryDataMapSyncPayload<>(registry.key(), att));
        }
    }
}
