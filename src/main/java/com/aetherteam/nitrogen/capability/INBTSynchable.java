package com.aetherteam.nitrogen.capability;

import com.aetherteam.nitrogen.network.BasePacket;
import com.aetherteam.nitrogen.network.PacketRelay;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Triple;
import oshi.util.tuples.Quintet;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface INBTSynchable<T extends Tag> extends INBTSerializable<T> {
    default void setSynched(Direction direction, String key, Object value) {
        this.setSynched(direction, key, value, null);
    }

    default void setSynched(Direction direction, String key, Object value, Object extra) {
        switch(direction) {
            case SERVER -> PacketRelay.sendToServer(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value));
            case CLIENT -> PacketRelay.sendToAll(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value));
            case NEAR -> {
                if (extra instanceof Quintet<?,?,?,?,?> quintet) {
                    Quintet<Double, Double, Double, Double, ResourceKey<Level>> nearValues = (Quintet<Double, Double, Double, Double, ResourceKey<Level>>) quintet;
                    PacketRelay.sendToNear(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value), nearValues.getA(), nearValues.getB(), nearValues.getC(), nearValues.getD(), nearValues.getE());
                }
            }
            case PLAYER -> {
                if (extra instanceof ServerPlayer serverPlayer) {
                    PacketRelay.sendToPlayer(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value), serverPlayer);
                }
            }
            case DIMENSION -> {
                if (extra instanceof ResourceKey<?> resourceKey) {
                    ResourceKey<Level> dimensionValue = (ResourceKey<Level>) resourceKey;
                    PacketRelay.sendToDimension(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value), dimensionValue);
                }
            }
        }
        this.getSynchableFunctions().get(key).getMiddle().accept(value);
    }

    default void forceSync(Direction direction) {
        this.forceSync(direction, null);
    }

    default void forceSync(Direction direction, Object extra) {
        for (Map.Entry<String, Triple<Type, Consumer<Object>, Supplier<Object>>> entry : this.getSynchableFunctions().entrySet()) {
            this.setSynched(direction, entry.getKey(), entry.getValue().getRight().get(), extra);
        }
    }

    Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions();
    BasePacket getSyncPacket(String key, INBTSynchable.Type type, Object value);
    SimpleChannel getPacketChannel();

    enum Direction {
        SERVER,
        CLIENT,
        NEAR,
        PLAYER,
        DIMENSION
    }

    enum Type {
        INT,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        UUID
    }
}
