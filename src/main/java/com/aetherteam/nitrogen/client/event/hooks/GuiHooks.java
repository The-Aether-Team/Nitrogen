package com.aetherteam.nitrogen.client.event.hooks;

import com.aetherteam.nitrogen.api.menu.MenuHelper;
import com.aetherteam.nitrogen.api.menu.Menus;
import com.aetherteam.nitrogen.client.NitrogenClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

public class GuiHooks {
    public static void prepareCustomMenus(MenuHelper menuHelper) {
        menuHelper.prepareMenu(Menus.MINECRAFT.get());
    }

    public static void trackFallbackMenu(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            if (!NitrogenClient.MENU_HELPER.doesScreenMatchMenu(titleScreen)) {
                NitrogenClient.MENU_HELPER.setFallbackTitleScreen(titleScreen);
            }
        }
    }

    public static void trackMinecraftTitleScreen(Screen screen) {
        if (screen.getClass() == TitleScreen.class) {
            TitleScreen titleScreen = (TitleScreen) screen;
            NitrogenClient.MENU_HELPER.setMinecraftTitleScreen(titleScreen);
        }
    }
}
