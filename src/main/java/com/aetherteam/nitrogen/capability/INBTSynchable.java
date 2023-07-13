package com.aetherteam.nitrogen.capability;

import com.aetherteam.nitrogen.network.BasePacket;
import com.aetherteam.nitrogen.network.PacketRelay;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface INBTSynchable<T extends Tag> extends INBTSerializable<T> {
    default void setSynched(Direction direction, String key, Object value) {
        if (direction == Direction.SERVER) {
            PacketRelay.sendToServer(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value));
        } else {
            PacketRelay.sendToAll(this.getPacketChannel(), this.getSyncPacket(key, this.getSynchableFunctions().get(key).getLeft(), value));
        }
        this.getSynchableFunctions().get(key).getMiddle().accept(value);
    }

    default void forceSync(Direction direction) {
        for (Map.Entry<String, Triple<Type, Consumer<Object>, Supplier<Object>>> entry : this.getSynchableFunctions().entrySet()) {
            this.setSynched(direction, entry.getKey(), entry.getValue().getRight().get());
        }
    }

    Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions();
    BasePacket getSyncPacket(String key, INBTSynchable.Type type, Object value);
    SimpleChannel getPacketChannel();

    enum Direction {
        CLIENT,
        SERVER
    }

    enum Type {
        INT,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        UUID
    }
}
