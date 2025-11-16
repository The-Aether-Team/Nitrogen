package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface LootContextExtension {

    @Nullable
    ResourceLocation getTableId();

    void pushTableId(ResourceLocation tableId);

    void popTableId();
}
