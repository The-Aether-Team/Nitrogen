/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.network.tasks;

import com.aetherteam.nitrogen.fabric.network.payload.KnownRegistryDataMapsPayload;
import com.aetherteam.nitrogen.fabric.registries.RegistryManager;
import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Internal
public record RegistryDataMapNegotiation(ServerConfigurationPacketListenerImpl listener) implements ConfigurationTask {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("neoforge", "registry_data_map_negotiation");
    public static final Type TYPE = new Type(ID.toString());

    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public void start(Consumer<Packet<?>> sender) {
        if (!ServerConfigurationNetworking.canSend(listener, KnownRegistryDataMapsPayload.TYPE)) {
            final var mandatory = RegistryManager.getDataMaps().values()
                .stream()
                .flatMap(map -> map.values().stream())
                .filter(DataMapType::mandatorySync)
                .map(type -> type.id() + " (" + type.registryKey().location() + ")")
                .toList();
            if (!mandatory.isEmpty()) {
                // Use plain components as vanilla connections will be missing our translation keys
                listener.disconnect(Component.literal("This server does not support vanilla clients as it has mandatory registry data maps: ")
                    .append(Component.literal(String.join(", ", mandatory)).withStyle(ChatFormatting.GOLD)));
            } else {
                listener.completeTask(TYPE);
            }

            return;
        }

        final Map<ResourceKey<? extends Registry<?>>, List<KnownRegistryDataMapsPayload.KnownDataMap>> dataMaps = new HashMap<>();
        RegistryManager.getDataMaps().forEach((key, attach) -> {
            final List<KnownRegistryDataMapsPayload.KnownDataMap> list = new ArrayList<>();
            attach.forEach((id, val) -> {
                if (val.networkCodec() != null) {
                    list.add(new KnownRegistryDataMapsPayload.KnownDataMap(id, val.mandatorySync()));
                }
            });
            dataMaps.put(key, list);
        });
        sender.accept(ServerConfigurationNetworking.createS2CPacket(new KnownRegistryDataMapsPayload(dataMaps)));
    }
}
