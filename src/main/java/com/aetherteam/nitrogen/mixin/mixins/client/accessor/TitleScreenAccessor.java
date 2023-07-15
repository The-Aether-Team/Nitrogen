package com.aetherteam.nitrogen.mixin.mixins.client.accessor;

import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {
    @Accessor("splash")
    String nitrogen$getSplash();

    @Accessor("splash")
    void nitrogen$setSplash(String splash);

    @Mutable
    @Accessor("fading")
    void nitrogen$setFading(boolean fading);

    @Accessor("fadeInStart")
    void nitrogen$setFadeInStart(long fadeInStart);
}
