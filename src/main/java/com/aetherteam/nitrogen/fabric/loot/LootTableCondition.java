/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.loot;

import com.aetherteam.nitrogen.fabric.pond.LootContextExtension;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Objects;

public record LootTableCondition(ResourceLocation lootTableId) implements LootItemCondition {
    public static final LootItemConditionType TYPE = new LootItemConditionType(
        RecordCodecBuilder.<LootTableCondition>mapCodec(instance -> {
            return instance.group(ResourceLocation.CODEC.fieldOf("loot_table_id").forGetter(LootTableCondition::lootTableId))
                .apply(instance, LootTableCondition::new);
        })
    );

    @Override
    public LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext context) {
        return Objects.equals(((LootContextExtension) context).getTableId(), lootTableId);
    }

    public static Builder builder(ResourceLocation lootTableId) {
        return new Builder(lootTableId);
    }

    public record Builder(ResourceLocation lootTableId) implements LootItemCondition.Builder {
        @Override
        public LootItemCondition build() {
            return new LootTableCondition(lootTableId);
        }
    }
}
