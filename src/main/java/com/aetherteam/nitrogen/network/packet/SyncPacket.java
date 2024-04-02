package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.capability.INBTSynchable;
import com.aetherteam.nitrogen.network.BasePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

import java.util.UUID;

/**
 * An abstract packet that is extended by other packets that are meant to be used for capability syncing through {@link INBTSynchable}.
 */
public abstract class SyncPacket implements BasePacket {
    protected final String key;
    protected final INBTSynchable.Type type;
    protected final Object value;

    public SyncPacket(Triple<String, INBTSynchable.Type, Object> values) {
        this(values.getLeft(), values.getMiddle(), values.getRight());
    }

    public SyncPacket(String key, INBTSynchable.Type type, Object value) {
        this.key = key;
        this.type = type;
        this.value = value;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.key);
        buf.writeInt(this.type.ordinal());
        if (this.value != null) {
            buf.writeBoolean(true);
            switch (this.type) {
                case INT -> buf.writeInt((int) this.value);
                case FLOAT -> buf.writeFloat((float) this.value);
                case DOUBLE -> buf.writeDouble((double) this.value);
                case BOOLEAN -> buf.writeBoolean((boolean) this.value);
                case UUID -> buf.writeUUID((UUID) this.value);
                case ITEM_STACK -> buf.writeItem((ItemStack) this.value);
                case COMPOUND_TAG -> buf.writeNbt((CompoundTag) this.value);
            }
        } else {
            buf.writeBoolean(false);
        }
    }

    public static Triple<String, INBTSynchable.Type, Object> decodeValues(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        int typeId = buf.readInt();
        INBTSynchable.Type type = INBTSynchable.Type.values()[typeId];
        Object value = null;
        boolean notNull = buf.readBoolean();
        if (notNull) {
            switch (type) {
                case INT -> value = buf.readInt();
                case FLOAT -> value = buf.readFloat();
                case DOUBLE -> value = buf.readDouble();
                case BOOLEAN -> value = buf.readBoolean();
                case UUID -> value = buf.readUUID();
                case ITEM_STACK -> value = buf.readItem();
                case COMPOUND_TAG -> value = buf.readNbt();
            }
        }
        return Triple.of(key, type, value);
    }
}
