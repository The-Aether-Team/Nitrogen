package com.aetherteam.nitrogen.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.Optional;

public interface BossMob<T extends Mob & BossMob<T>> {
    TargetingConditions NON_COMBAT = TargetingConditions.forNonCombat();

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    Component getBossName();

    void setBossName(Component component);

    boolean isBossFight();

    void setBossFight(boolean isFighting);

    @Nullable
    BossRoomTracker getDungeon();

    void setDungeon(@Nullable BossRoomTracker dungeon);

    int getDeathScore();

    /**
     * Should be called from {@link Mob#customServerAiStep()}.<br>
     * This is used to track the contents of the dungeon room.
     * If any of the tracked players leave or if the boss leaves, then the boss fight resets.
     */
    default void trackDungeon() {
        if (this.getDungeon() != null) {
            this.getDungeon().trackPlayers(this.self());
            if (this.isBossFight() && (this.getDungeon().dungeonPlayers().isEmpty() || !this.getDungeon().isBossWithinRoom(this.self()))) {
                this.reset();
            }
        }
    }

    /**
     * Displays a message when the player tries to start a boss fight but is outside of the room.
     *
     * @param player The {@link ServerPlayer}.
     */
    default void displayTooFarMessage(ServerPlayer player) {
        player.sendSystemMessage(Component.translatable("gui.nitrogen_internals.boss.message.far"));
    }

    default void onDungeonPlayerAdded(@Nullable Player player) {
    }

    default void onDungeonPlayerRemoved(@Nullable Player player) {
    }

    void reset();

    /**
     * Called when the boss is defeated to change all blocks to unlocked blocks.
     */
    default void tearDownRoom() {
        if (this.getDungeon() != null) {
            this.getDungeon().modifyRoom(this.self(), this::convertBlock);
        }
    }

    void closeRoom();

    void openRoom();

    @Nullable
    BlockState convertBlock(BlockState state);

    default void addBossSaveData(ValueOutput output) {
        output.storeNullable("BossName", ComponentSerialization.CODEC, this.getBossName());
        output.putBoolean("BossFight", this.isBossFight());
        output.storeNullable("Dungeon", BossRoomTracker.CODEC, this.getDungeon());
    }

    default void readBossSaveData(ValueInput input) {
        input.read("BossName", ComponentSerialization.CODEC).ifPresent(this::setBossName);
        this.setBossFight(input.getBooleanOr("BossFight", false));
        input.read("Dungeon", BossRoomTracker.CODEC).ifPresent(this::setDungeon);
    }

    default void writeBossSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeOptional(Optional.ofNullable(this.getBossName()), (buf, val) -> buf.writeJsonWithCodec(ComponentSerialization.CODEC, val));
        buffer.writeBoolean(this.isBossFight());
        buffer.writeOptional(Optional.ofNullable(this.getDungeon()), (buf, val) -> buf.writeJsonWithCodec(BossRoomTracker.CODEC, val));
    }

    default void readBossSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.readOptional((buf) -> buf.readLenientJsonWithCodec(ComponentSerialization.CODEC)).ifPresent(this::setBossName);
        this.setBossFight(buffer.readBoolean());
        buffer.readOptional((buf) -> buf.readLenientJsonWithCodec(BossRoomTracker.CODEC)).ifPresent(this::setDungeon);
    }
}

