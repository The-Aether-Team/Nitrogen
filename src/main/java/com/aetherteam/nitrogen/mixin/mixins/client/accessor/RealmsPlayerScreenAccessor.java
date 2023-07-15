package com.aetherteam.nitrogen.mixin.mixins.client.accessor;

import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RealmsPlayerScreen.class)
public interface RealmsPlayerScreenAccessor {
    @Mutable
    @Accessor("OPTIONS_BACKGROUND")
    static void nitrogen$setOptionsBackground(ResourceLocation location) {
        throw new AssertionError();
    }
}
