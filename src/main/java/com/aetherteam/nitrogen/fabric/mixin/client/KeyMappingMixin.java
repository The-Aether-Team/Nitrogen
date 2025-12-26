package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.pond.client.KeyMappingExtension;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin implements KeyMappingExtension {

    @Shadow
    private InputConstants.Key key;

    @Override
    public InputConstants.Key nitrogen_fabric$getKey() {
        return this.key;
    }
}
