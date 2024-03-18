package com.aetherteam.nitrogen.attachment;

import com.aetherteam.nitrogen.network.BasePacket;
import com.aetherteam.nitrogen.network.PacketRelay;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import oshi.util.tuples.Quintet;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface INBTSynchable {
    /**
     * Sets a value to be synced to the given direction.
     *
     * @param direction The network {@link Direction} to send the packet.
     * @param key       The {@link String} key for the field to sync.
     * @param value     The {@link Object} value to sync to the field.
     */
    default void setSynched(int entityID, Direction direction, String key, Object value) {
        this.setSynched(entityID, direction, key, value, null);
    }

    /**
     * Sets a value to be synced to the given direction.<br><br>
     * Warning for "unchecked" is suppressed because casting from wildcards to classes inside type bounds is fine.
     *
     * @param direction The network {@link Direction} to send the packet.
     * @param key       The {@link String} key for the field to sync.
     * @param value     The {@link Object} value to sync to the field.
     * @param extra     An extra value if necessary. The type of class that needs to be given depends on the direction.<br><br>
     *                  {@link Direction#SERVER} - None.<br><br>
     *                  {@link Direction#CLIENT} - None.<br><br>
     *                  {@link Direction#NEAR} - {@link Quintet}<{@link Double}, {@link Double}, {@link Double}, {@link Double}, {@link ResourceKey}<{@link Level}>>. This represents the 5 values needed for {@link PacketRelay#sendToNear(CustomPacketPayload, double, double, double, double, ResourceKey)}.<br><br>
     *                  {@link Direction#PLAYER} - {@link ServerPlayer}.<br><br>
     *                  {@link Direction#DIMENSION} - {@link ResourceKey}<{@link Level}>>. This represents the dimension to send the packet to.
     */
    @SuppressWarnings("unchecked")
    default void setSynched(int entityID, Direction direction, String key, Object value, @Nullable Object extra) {
        switch (direction) {
            case SERVER ->
                    PacketRelay.sendToServer(this.getSyncPacket(entityID, key, this.getSynchableFunctions().get(key).getLeft(), value));
            case CLIENT ->
                    PacketRelay.sendToAll(this.getSyncPacket(entityID, key, this.getSynchableFunctions().get(key).getLeft(), value));
            case NEAR -> {
                if (extra instanceof Quintet<?, ?, ?, ?, ?> quintet) {
                    Quintet<Double, Double, Double, Double, ResourceKey<Level>> nearValues = (Quintet<Double, Double, Double, Double, ResourceKey<Level>>) quintet;
                    PacketRelay.sendToNear(this.getSyncPacket(entityID, key, this.getSynchableFunctions().get(key).getLeft(), value), nearValues.getA(), nearValues.getB(), nearValues.getC(), nearValues.getD(), nearValues.getE());
                }
            }
            case PLAYER -> {
                if (extra instanceof ServerPlayer serverPlayer) {
                    PacketRelay.sendToPlayer(this.getSyncPacket(entityID, key, this.getSynchableFunctions().get(key).getLeft(), value), serverPlayer);
                }
            }
            case DIMENSION -> {
                if (extra instanceof ResourceKey<?> resourceKey) {
                    ResourceKey<Level> dimensionValue = (ResourceKey<Level>) resourceKey;
                    PacketRelay.sendToDimension(this.getSyncPacket(entityID, key, this.getSynchableFunctions().get(key).getLeft(), value), dimensionValue);
                }
            }
        }
        this.getSynchableFunctions().get(key).getMiddle().accept(value);
    }

    /**
     * Forces all synchable capability values to sync to the given direction.
     *
     * @param direction The network {@link Direction} to send the packet.
     */
    default void forceSync(int entityID, Direction direction) {
        this.forceSync(entityID, direction, null);
    }

    /**
     * Forces all synchable capability values to sync to the given direction.
     *
     * @param direction The network {@link Direction} to send the packet.
     * @param extra     An extra value if necessary. The type of class that needs to be given depends on the direction.<br><br>
     *                  {@link Direction#SERVER} - None.<br><br>
     *                  {@link Direction#CLIENT} - None.<br><br>
     *                  {@link Direction#NEAR} - {@link Quintet}<{@link Double}, {@link Double}, {@link Double}, {@link Double}, {@link ResourceKey}<{@link Level}>>. This represents the 5 values needed for {@link PacketRelay#sendToNear(CustomPacketPayload, double, double, double, double, ResourceKey)}.<br><br>
     *                  {@link Direction#PLAYER} - {@link ServerPlayer}.<br><br>
     *                  {@link Direction#DIMENSION} - {@link ResourceKey}<{@link Level}>>. This represents the dimension to send the packet to.
     */
    default void forceSync(int entityID, Direction direction, @Nullable Object extra) {
        for (Map.Entry<String, Triple<Type, Consumer<Object>, Supplier<Object>>> entry : this.getSynchableFunctions().entrySet()) {
            this.setSynched(entityID, direction, entry.getKey(), entry.getValue().getRight().get(), extra);
        }
    }

    /**
     * @return A {@link Map} of {@link String}s (representing the name of a field), and {@link Triple}s, containing
     * {@link Type}s (representing the data type),
     * {@link Consumer}<{@link Object}>s (representing setter methods), and
     * {@link Supplier}<{@link Object}>s (representing getter methods).
     */
    Map<String, Triple<Type, Consumer<Object>, Supplier<Object>>> getSynchableFunctions();

    /**
     * Creates the packet used for syncing.
     *
     * @param key   The {@link String} key for the field to sync.
     * @param type  The {@link Type} for the field's data type.
     * @param value The {@link Object} value to sync to the field.
     * @return The {@link BasePacket} for syncing.
     */
    BasePacket getSyncPacket(int entityID, String key, INBTSynchable.Type type, Object value);

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
