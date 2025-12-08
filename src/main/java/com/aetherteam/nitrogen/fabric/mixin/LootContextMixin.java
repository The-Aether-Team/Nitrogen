package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.LootContextExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayDeque;
import java.util.Deque;

@Mixin(LootContext.class)
public abstract class LootContextMixin implements LootContextExtension {
    @Nullable
    @Unique
    private Deque<ResourceLocation> nitrogen_fabric$tableId = new ArrayDeque<>();

    @Override
    public @Nullable ResourceLocation nitrogen_fabric$getTableId() {
        if (this.nitrogen_fabric$tableId.isEmpty()) return null;

        return this.nitrogen_fabric$tableId.peek();
    }

    @Override
    public void nitrogen_fabric$pushTableId(ResourceLocation tableId) {
        this.nitrogen_fabric$tableId.push(tableId);
    }

    @Override
    public void nitrogen_fabric$popTableId() {
        this.nitrogen_fabric$tableId.pop();
    }
}
