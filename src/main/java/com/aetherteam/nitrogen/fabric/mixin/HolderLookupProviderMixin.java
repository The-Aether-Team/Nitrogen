package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.pond.IHolderLookupProviderExtension;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HolderLookup.Provider.class)
public interface HolderLookupProviderMixin extends IHolderLookupProviderExtension {
}
