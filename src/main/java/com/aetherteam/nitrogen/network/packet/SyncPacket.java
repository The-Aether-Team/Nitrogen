package com.aetherteam.nitrogen.network.packet;

import com.aetherteam.nitrogen.capability.INBTSynchable;
import com.aetherteam.nitrogen.network.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import oshi.util.tuples.Quartet;

import java.util.UUID;

public abstract class SyncPacket<T extends INBTSynchable<CompoundTag>> implements BasePacket {
    private final int entityID;
    private final String key;
    private final INBTSynchable.Type type;
    private final Object value;

    public SyncPacket(Quartet<Integer, String, INBTSynchable.Type, Object> values) {
        this(values.getA(), values.getB(), values.getC(), values.getD());
    }

    public SyncPacket(int entityID, String key, INBTSynchable.Type type, Object value) {
        this.entityID = entityID;
        this.key = key;
        this.type = type;
        this.value = value;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
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
            }
        } else {
            buf.writeBoolean(false);
        }
    }

    public static Quartet<Integer, String, INBTSynchable.Type, Object> decoded(FriendlyByteBuf buf) {
        int entityID = buf.readInt();
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
            }
        }
        return new Quartet<>(entityID, key, type, value);
    }

    @Override
    public void execute(Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && this.value != null) {
            Entity entity = playerEntity.getLevel().getEntity(this.entityID);
            if (entity != null && !entity.getLevel().isClientSide()) {
                this.getCapability(entity).ifPresent((synchable) -> synchable.getSynchableFunctions().get(this.key).getMiddle().accept(this.value));
            }
        } else {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && this.value != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(this.entityID);
                if (entity != null && entity.getLevel().isClientSide()) {
                    this.getCapability(entity).ifPresent((synchable) -> synchable.getSynchableFunctions().get(this.key).getMiddle().accept(this.value));
                }
            }
        }
    }

    protected abstract LazyOptional<T> getCapability(Entity entity);
}
