package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.IHolderExtension;
import com.aetherteam.nitrogen.fabric.pond.IWithData;
import com.aetherteam.nitrogen.fabric.registries.datamaps.DataMapType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.class)
public interface HolderMixin<T> extends IHolderExtension<T>, IWithData<T> {
    @Mixin(Holder.Reference.class)
    abstract class HolderReferenceMixin<T> implements IHolderExtension<T>, IWithData<T>  {
        @Shadow
        @Nullable
        private ResourceKey<T> key;

        @Shadow
        @Final
        private HolderOwner<T> owner;

        @Override
        public HolderLookup.@Nullable RegistryLookup<T> nitrogen_fabric$unwrapLookup() {
            return this.owner instanceof HolderLookup.RegistryLookup<T> rl ? rl : null;
        }

        @Override
        @Nullable
        public ResourceKey<T> nitrogen_fabric$getKey() {
            return this.key;
        }

        @Override
        public <T1> @Nullable T1 nitrogen_fabric$getData(DataMapType<T, T1> type) {
            if (owner instanceof HolderLookup.RegistryLookup<T> lookup) {
                return lookup.nitrogen_fabric$getData(type, key);
            }
            return null;
        }
    }
}
