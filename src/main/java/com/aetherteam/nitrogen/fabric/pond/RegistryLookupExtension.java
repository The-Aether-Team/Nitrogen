package com.aetherteam.nitrogen.fabric.pond;

import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface RegistryLookupExtension<R> {

    @Nullable
    default <T> T nitrogen_fabric$getData(DataMapType<R, T> type, ResourceKey<R> key) {
        return null;
    }
}
