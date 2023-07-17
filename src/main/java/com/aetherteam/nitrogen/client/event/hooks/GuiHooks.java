package com.aetherteam.nitrogen.client.event.hooks;

import com.aetherteam.nitrogen.client.NitrogenClient;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

public class GuiHooks {
    public static void trackFallbackMenu(Screen screen) {
        if (screen instanceof TitleScreen titleScreen) {
            if (!NitrogenClient.MENU_HELPER.doesScreenMatchMenu(titleScreen)) {
                NitrogenClient.MENU_HELPER.setFallbackTitleScreen(titleScreen);
            }
        }
    }
}
