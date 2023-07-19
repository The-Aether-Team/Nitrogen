package com.aetherteam.nitrogen.client.event.listeners;

import com.aetherteam.nitrogen.client.NitrogenClient;
import com.aetherteam.nitrogen.client.event.hooks.MenuHooks;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class MenuListener {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpenHighest(ScreenEvent.Opening event) {
        Screen newScreen = event.getNewScreen();
        MenuHooks.prepareCustomMenus(NitrogenClient.MENU_HELPER);
        MenuHooks.refreshBackgrounds(newScreen, NitrogenClient.MENU_HELPER);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onGuiOpenLow(ScreenEvent.Opening event) {
        Screen screen = event.getScreen();
        Screen newScreen = event.getNewScreen();
        MenuHooks.setLastSplash(screen, NitrogenClient.MENU_HELPER);
        MenuHooks.trackFallbacks(newScreen);
        Screen titleScreen = MenuHooks.setupCustomMenu(screen, NitrogenClient.MENU_HELPER);
        if (titleScreen != null) {
            event.setNewScreen(titleScreen);
        }
    }

    @SubscribeEvent
    public static void onGuiDraw(ScreenEvent.Render event) {
        MenuHooks.resetFade(NitrogenClient.MENU_HELPER);
    }

    @SubscribeEvent
    public static void onGuiInitialize(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof TitleScreen) {
            Button menuSwitchButton = MenuHooks.setupMenuScreenButton(screen);
            if (menuSwitchButton != null) {
                event.addListener(menuSwitchButton);
            }
        }
    }
}
