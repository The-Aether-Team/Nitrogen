/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.registries;

import com.aetherteam.nitrogen.fabric.network.payload.KnownRegistryDataMapsPayload;
import com.aetherteam.nitrogen.fabric.network.payload.KnownRegistryDataMapsReplyPayload;
import com.aetherteam.nitrogen.fabric.network.payload.RegistryDataMapSyncPayload;
import com.aetherteam.nitrogen.fabric.pond.FullDataMapAccess;
import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapsUpdatedEvent;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class ClientRegistryManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <R> void handleDataMapSync(final RegistryDataMapSyncPayload<R> payload, final ClientPlayNetworking.Context context) {
        try {
            var regAccess = Minecraft.getInstance().level.registryAccess();
            var registry = regAccess.lookupOrThrow(payload.registryKey());
            var access = (FullDataMapAccess<R>) regAccess.lookupOrThrow(payload.registryKey());

            access.nitrogen_fabric$setDataMaps(dataMaps -> payload.dataMaps().forEach((attachKey, maps) -> dataMaps.put(RegistryManager.getDataMap(payload.registryKey(), attachKey), Collections.unmodifiableMap(maps))));

            DataMapsUpdatedEvent.EVENT.invoker().onUpdate(new DataMapsUpdatedEvent(regAccess, registry, DataMapsUpdatedEvent.UpdateCause.CLIENT_SYNC));
        } catch (Throwable t) {
            LOGGER.error("Failed to handle registry data map sync: ", t);
            context.responseSender().disconnect(Component.translatable("neoforge.network.data_maps.failed", payload.registryKey().location().toString(), t.toString()));
        }
    }

    public static void handleKnownDataMaps(final KnownRegistryDataMapsPayload payload, ClientConfigurationNetworking.Context context) {
        record MandatoryEntry(ResourceKey<? extends Registry<?>> registry, ResourceLocation id) {}
        final Set<MandatoryEntry> ourMandatory = new HashSet<>();
        RegistryManager.getDataMaps().forEach((reg, values) -> values.values().forEach(attach -> {
            if (attach.mandatorySync()) {
                ourMandatory.add(new MandatoryEntry(reg, attach.id()));
            }
        }));

        final Set<MandatoryEntry> theirMandatory = new HashSet<>();
        payload.dataMaps().forEach((reg, values) -> values.forEach(attach -> {
            if (attach.mandatory()) {
                theirMandatory.add(new MandatoryEntry(reg, attach.id()));
            }
        }));

        final List<Component> messages = new ArrayList<>();
        final var missingOur = Sets.difference(ourMandatory, theirMandatory);
        if (!missingOur.isEmpty()) {
            messages.add(Component.translatable("neoforge.network.data_maps.missing_our", Component.literal(missingOur.stream()
                .map(e -> e.id() + " (" + e.registry().location() + ")")
                .collect(Collectors.joining(", "))).withStyle(ChatFormatting.GOLD)));
        }

        final var missingTheir = Sets.difference(theirMandatory, ourMandatory);
        if (!missingTheir.isEmpty()) {
            messages.add(Component.translatable("neoforge.network.data_maps.missing_their", Component.literal(missingTheir.stream()
                .map(e -> e.id() + " (" + e.registry().location() + ")")
                .collect(Collectors.joining(", "))).withStyle(ChatFormatting.GOLD)));
        }

        if (!messages.isEmpty()) {
            MutableComponent message = Component.empty();
            final var itr = messages.iterator();
            while (itr.hasNext()) {
                message = message.append(itr.next());
                if (itr.hasNext()) {
                    message = message.append("\n");
                }
            }

            context.responseSender().disconnect(message);
            return;
        }

        final var known = new HashMap<ResourceKey<? extends Registry<?>>, Collection<ResourceLocation>>();
        RegistryManager.getDataMaps().forEach((key, vals) -> known.put(key, vals.keySet()));
        context.responseSender().sendPacket(new KnownRegistryDataMapsReplyPayload(known));
    }
}
