package com.aetherteam.nitrogen.fabric.pond;

import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Internal
public interface FullDataMapAccess<T> {

    default void nitrogen_fabric$setDataMaps(Consumer<Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>>> builder) {
        var dataMaps = new IdentityHashMap<DataMapType<T, ?>, Map<ResourceKey<T>, ?>>();

        builder.accept(dataMaps);

        this.nitrogen_fabric$setDataMaps(dataMaps);
    }

    void nitrogen_fabric$setDataMaps(Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> dataMaps);

    Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> nitrogen_fabric$getDataMaps();
}
