package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface LootContextExtension {

    @Nullable
    ResourceLocation nitrogen_fabric$getTableId();

    void nitrogen_fabric$pushTableId(ResourceLocation tableId);

    void nitrogen_fabric$popTableId();
}
