/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.network.payload;

import com.aetherteam.nitrogen.fabric.entity.IEntityWithComplexSpawn;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

/**
 * Payload that can be sent from the server to the client to add an entity to the world, with custom data.
 *
 * @param entityId      The id of the entity to add.
 * @param customPayload The custom data of the entity to add.
 */
public record AdvancedAddEntityPayload(int entityId, byte[] customPayload) implements CustomPacketPayload {
    public static final Type<AdvancedAddEntityPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("neoforge", "advanced_add_entity"));
    public static final StreamCodec<FriendlyByteBuf, AdvancedAddEntityPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        AdvancedAddEntityPayload::entityId,
        ByteBufCodecs.BYTE_ARRAY,
        AdvancedAddEntityPayload::customPayload,
        AdvancedAddEntityPayload::new);

    public AdvancedAddEntityPayload(Entity e) {
        this(e.getId(), writeCustomData(e));
    }

    private static byte[] writeCustomData(final Entity entity) {
        return entity instanceof IEntityWithComplexSpawn additionalSpawnData
            ? writeCustomData(additionalSpawnData::writeSpawnData, entity.registryAccess())
            : new byte[0];
    }

    public static byte[] writeCustomData(Consumer<RegistryFriendlyByteBuf> dataWriter, RegistryAccess registryAccess) {
        final var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        try {
            dataWriter.accept(buf);
            buf.readerIndex(0);
            final byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            return data;
        } finally {
            buf.release();
        }
    }

    @Override
    public Type<AdvancedAddEntityPayload> type() {
        return TYPE;
    }
}
