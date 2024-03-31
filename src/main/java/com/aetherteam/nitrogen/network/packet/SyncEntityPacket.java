package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.attachment.INBTSynchable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
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
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        super.write(buf);
    }

    public static Quartet<Integer, String, INBTSynchable.Type, Object> decodeEntityValues(FriendlyByteBuf buf) {
        int entityID = buf.readInt();
        Triple<String, INBTSynchable.Type, Object> decodeBase = SyncPacket.decodeValues(buf);
        return new Quartet<>(entityID, decodeBase.getLeft(), decodeBase.getMiddle(), decodeBase.getRight());
    }

    @Override
    public void execute(Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && this.value != null) {
            Entity entity = playerEntity.level().getEntity(this.entityID);
            if (entity != null && !entity.level().isClientSide()) {
                entity.getData(this.getAttachment()).getSynchableFunctions().get(this.key).getMiddle().accept(this.value);
            }
        } else {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && this.value != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
                if (entity != null && entity.level().isClientSide()) {
                    entity.getData(this.getAttachment()).getSynchableFunctions().get(this.key).getMiddle().accept(this.value);
                }
            }
        }
    }

    public abstract Supplier<AttachmentType<T>> getAttachment();
}
