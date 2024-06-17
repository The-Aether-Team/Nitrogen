package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.apache.commons.lang3.tuple.Triple;
import oshi.util.tuples.Quartet;

import java.util.function.Supplier;

/**
 * An abstract packet used by entity attachments for data syncing.
 */
public abstract class SyncEntityPacket<T extends INBTSynchable> extends SyncPacket {
    private final int entityID;

    public SyncEntityPacket(Quartet<Integer, String, INBTSynchable.Type, Object> values) {
        this(values.getA(), values.getB(), values.getC(), values.getD());
    }

    public SyncEntityPacket(int entityID, String key, INBTSynchable.Type type, Object value) {
        super(key, type, value);
        this.entityID = entityID;
    }

    @Override
    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        super.write(buf);
    }

    public static Quartet<Integer, String, INBTSynchable.Type, Object> decodeEntityValues(RegistryFriendlyByteBuf buf) {
        int entityID = buf.readInt();
        Triple<String, INBTSynchable.Type, Object> decodeBase = SyncPacket.decodeValues(buf);
        return new Quartet<>(entityID, decodeBase.getLeft(), decodeBase.getMiddle(), decodeBase.getRight());
    }

    public static <T extends INBTSynchable> void execute(SyncEntityPacket<T> payload, Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && payload.value != null) {
            Entity entity = playerEntity.level().getEntity(payload.entityID);
            if (entity != null && !entity.level().isClientSide()) {
                entity.getData(payload.getAttachment()).getSynchableFunctions().get(payload.key).getMiddle().accept(payload.value);
            }
        } else {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && payload.value != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(payload.entityID);
                if (entity != null && entity.level().isClientSide()) {
                    entity.getData(payload.getAttachment()).getSynchableFunctions().get(payload.key).getMiddle().accept(payload.value);
                }
            }
        }
    }

    public abstract Supplier<AttachmentType<T>> getAttachment();
}
