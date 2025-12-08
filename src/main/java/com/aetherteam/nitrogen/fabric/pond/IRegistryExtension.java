package com.aetherteam.nitrogen.fabric.pond;

import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IRegistryExtension<T> {

    @Nullable
    default <A> A nitrogen_fabric$getData(DataMapType<T, A> type, ResourceKey<T> key) {
        final var innerMap = ((FullDataMapAccess<T>) (this)).nitrogen_fabric$getDataMaps().get(type);
        return innerMap == null ? null : (A) innerMap.get(key);
    }

    default <A> Map<ResourceKey<T>, A> nitrogen_fabric$getDataMap(DataMapType<T, A> type) {
        return (Map<ResourceKey<T>, A>) ((FullDataMapAccess<T>) (this)).nitrogen_fabric$getDataMaps().getOrDefault(type, Map.of());
    }
}
