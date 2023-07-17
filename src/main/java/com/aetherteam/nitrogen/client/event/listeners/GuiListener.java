package com.aetherteam.nitrogen.client.event.listeners;

import com.aetherteam.nitrogen.client.event.hooks.GuiHooks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class GuiListener {
    @SubscribeEvent
    public static void onGuiOpen(ScreenEvent.Opening event) {
        Screen screen = event.getScreen();
        GuiHooks.trackFallbackMenu(screen);
    }
}
