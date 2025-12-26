package com.aetherteam.nitrogen.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public record BossRoomTracker(Vec3 originCoordinates, Vec3 minBounds, Vec3 maxBounds, List<UUID> dungeonPlayers) {
    public static final Codec<BossRoomTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3.CODEC.fieldOf("origin_coordinates").forGetter(BossRoomTracker::originCoordinates),
        Vec3.CODEC.fieldOf("min_bounds").forGetter(BossRoomTracker::minBounds),
        Vec3.CODEC.fieldOf("max_bounds").forGetter(BossRoomTracker::maxBounds),
        UUIDUtil.CODEC.listOf().fieldOf("dungeon_players").forGetter(BossRoomTracker::dungeonPlayers)
    ).apply(instance, (a, b, c, d) -> new BossRoomTracker(a, b, c, new ArrayList<>(d))));

    /**
     * @return Whether the dungeon boss is within the room bounds, as a {@link Boolean}.
     */
    public <T extends Mob & BossMob<T>> boolean isBossWithinRoom(@Nullable T boss) {
        if (boss != null) {
            return this.roomBounds().contains(boss.position());
        }
        return false;
    }

    /**
     * Checks whether a player is within the room bounds.
     *
     * @param entity The player {@link Entity}.
     * @return The {@link Boolean} result.
     */
    public <T extends Mob & BossMob<T>> boolean isPlayerWithinRoom(@Nullable T boss, Entity entity) {
        if (boss != null) {
            return this.roomBounds().contains(entity.position());
        }
        return false;
    }

    /**
     * Checks whether a player is within the interior of the room bounds, not including the wall, floor, or ceiling positions.
     *
     * @param entity The player {@link Entity}.
     * @return The {@link Boolean} result.
     */
    public <T extends Mob & BossMob<T>> boolean isPlayerWithinRoomInterior(@Nullable T boss, Entity entity) {
        if (boss != null) {
            return this.roomBounds().deflate(1.0, 1.0, 1.0).contains(entity.position());
        }
        return false;
    }

    /**
     * Checks whether this player is tracked within the list of players inside the boss room.
     *
     * @param player The {@link Player}.
     * @return The {@link Boolean} result.
     */
    public <T extends Mob & BossMob<T>> boolean isPlayerTracked(@Nullable T boss, Player player) {
        if (boss != null) {
            return this.dungeonPlayers().contains(player.getUUID());
        }
        return false;
    }

    /**
     * Tracks whether players enter or leave the boss room.
     * If there are living players in the boss room, then they are tracked to {@link BossRoomTracker#dungeonPlayers()}.
     * If any players die, leave the room, or no longer exist, then they are removed from tracking.
     */
    public <T extends Mob & BossMob<T>> void trackPlayers(@Nullable T boss) {
        if (boss != null) {
            boss.level().getEntities(EntityType.PLAYER, this.roomBounds(), Entity::isAlive).forEach(player -> {
                if (!this.isPlayerTracked(boss, player)) {
                    boss.onDungeonPlayerAdded(player);
                    this.dungeonPlayers().add(player.getUUID());
                }
            });
            this.dungeonPlayers().removeIf(uuid -> {
                Player player = boss.level().getPlayerByUUID(uuid);
                boolean shouldRemove = player != null && (!this.isPlayerWithinRoom(boss, player) || !player.isAlive());
                if (shouldRemove) {
                    boss.onDungeonPlayerRemoved(player);
                }
                return shouldRemove;
            });
        }
    }

    /**
     * Marks every player in the boss room as having killed the boss, so they all get achievements.
     *
     * @param damageSource The {@link DamageSource} used to kill the boss.
     */
    public <T extends Mob & BossMob<T>> void grantAdvancements(@Nullable T boss, DamageSource damageSource) {
        if (boss != null) {
            for (UUID uuid : this.dungeonPlayers()) {
                Player player = boss.level().getPlayerByUUID(uuid);
                if (player != null) {
                    player.awardKillScore(boss, damageSource);
                }
            }
        }
    }

    /**
     * Iterates on every block within the bounds of the dungeon
     *
     * @param function A {@link Function} of two {@link BlockState}s, used to modify blocks within the room.
     */
    public <T extends Mob & BossMob<T>> void modifyRoom(@Nullable T boss, ModifyPosition function) {
        if (boss != null) {
            AABB bounds = this.roomBounds();
            Level level = boss.level();
            for (BlockPos pos : BlockPos.betweenClosed((int) bounds.minX, (int) bounds.minY, (int) bounds.minZ, (int) bounds.maxX, (int) bounds.maxY, (int) bounds.maxZ)) {
                BlockState state = level.getBlockState(pos);
                BlockState newState = function.convertBlock(level, pos, state);
                if (newState != null) {
                    level.setBlock(pos, newState, 1 | 2);
                }
            }
        }
    }

    public AABB roomBounds() {
        return new AABB(this.minBounds(), this.maxBounds());
    }

    @FunctionalInterface
    public interface ModifyPosition {
        BlockState convertBlock(Level level, BlockPos blockPos, BlockState oldState);
    }
}

