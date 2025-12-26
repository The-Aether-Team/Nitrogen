package com.aetherteam.nitrogen.fabric.pond;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface IHolderExtension<T> {
    /**
     * {@return the holder that this holder wraps}
     *
     * Used by {@link Registry#safeCastToReference} to resolve the underlying {@link Holder.Reference} for delegating holders.
     */
    default Holder<T> nitrogen_fabric$getDelegate() {
        return (Holder<T>) this;
    }

    /**
     * Attempts to resolve the underlying {@link HolderLookup.RegistryLookup} from a {@link Holder}.
     * <p>
     * This will only succeed if the underlying holder is a {@link Holder.Reference}.
     */
    @Nullable
    default HolderLookup.RegistryLookup<T> nitrogen_fabric$unwrapLookup() {
        return null;
    }

    /**
     * Get the resource key held by this Holder, or null if none is present. This method will be overriden
     * by Holder implementations to avoid allocation associated with {@link Holder#unwrapKey()}
     */
    @Nullable
    default ResourceKey<T> nitrogen_fabric$getKey() {
        return ((Holder<T>) this).unwrapKey().orElse(null);
    }
}
