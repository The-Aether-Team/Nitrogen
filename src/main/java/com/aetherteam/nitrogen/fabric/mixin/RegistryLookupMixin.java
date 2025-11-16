package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.RegistryLookupExtension;
import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HolderLookup.RegistryLookup.class)
public interface RegistryLookupMixin<T> extends RegistryLookupExtension<T> {
    @Mixin(HolderLookup.RegistryLookup.Delegate.class)
    interface DelegateMixin<T> extends RegistryLookupExtension<T> {
        @Shadow
        HolderLookup.RegistryLookup<T> parent();

        @Override
        default <T1> @Nullable T1 nitrogen_fabric$getData(DataMapType<T, T1> type, ResourceKey<T> key) {
            return this.parent().nitrogen_fabric$getData(type, key);
        }
    }
}
