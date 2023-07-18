package com.aetherteam.nitrogen.client.event.hooks;

import com.aetherteam.nitrogen.api.menu.Menu;
import com.aetherteam.nitrogen.api.menu.MenuHelper;
import com.aetherteam.nitrogen.api.menu.Menus;
import com.aetherteam.nitrogen.client.NitrogenClient;
import com.aetherteam.nitrogen.mixin.mixins.client.accessor.TabButtonAccessor;
import com.aetherteam.nitrogen.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;

public class MenuHooks {
    public static void setLastSplash(Screen screen, MenuHelper menuHelper) {
        menuHelper.setLastSplash(((TitleScreenAccessor) screen).nitrogen$getSplash());
    }

    public static void prepareCustomMenus(MenuHelper menuHelper) {
        menuHelper.prepareMenu(Menus.MINECRAFT.get());
    }

    public static void refreshBackgrounds(Screen screen, MenuHelper menuHelper) {
        if (screen instanceof TitleScreen) {
            menuHelper.resetBackgrounds();
        }
    }

    public static void trackFallbacks(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            if (!NitrogenClient.MENU_HELPER.doesScreenMatchMenu(titleScreen) || screen.getClass() == TitleScreen.class) {
                NitrogenClient.MENU_HELPER.setFallbackTitleScreen(titleScreen);
                NitrogenClient.MENU_HELPER.setFallbackBackground(new Menu.Background().regularBackground(GuiComponent.BACKGROUND_LOCATION).darkBackground(GuiComponent.LIGHT_DIRT_BACKGROUND).headerSeparator(CreateWorldScreen.HEADER_SEPERATOR).footerSeparator(CreateWorldScreen.FOOTER_SEPERATOR).tabButton(TabButtonAccessor.nitrogen$getTextureLocation()));
            }
        }
    }

    public static Screen setupCustomMenu(Screen screen, MenuHelper menuHelper) {
        if (screen instanceof TitleScreen) {
            return menuHelper.applyMenu(menuHelper.getActiveMenu());
        }
        return screen;
    }

    public static void resetFade(MenuHelper menuHelper) {
        menuHelper.setShouldFade(false);
    }
}
