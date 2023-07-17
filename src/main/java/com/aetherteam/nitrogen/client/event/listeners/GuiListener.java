package com.aetherteam.nitrogen.client.event.listeners;

import com.aetherteam.nitrogen.client.NitrogenClient;
import com.aetherteam.nitrogen.client.event.hooks.GuiHooks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class GuiListener {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiPreInitialize(ScreenEvent.Init.Pre event) {
        GuiHooks.prepareCustomMenus(NitrogenClient.MENU_HELPER);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(ScreenEvent.Opening event) {
        Screen screen = event.getScreen();
        GuiHooks.trackFallbackMenu(screen);
        GuiHooks.trackMinecraftTitleScreen(screen);
    }
}
