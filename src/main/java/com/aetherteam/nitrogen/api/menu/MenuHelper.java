package com.aetherteam.nitrogen.api.menu;

import com.aetherteam.nitrogen.NitrogenConfig;
import com.aetherteam.nitrogen.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.sounds.Music;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Predicate;

public class MenuHelper {
    private Menu activeMenu = null;

    public MenuHelper() { }

    public Menu getActiveMenu() {
        return this.activeMenu;
    }

    public TitleScreen applyMenu(Menu menu, TitleScreen originalScreen, boolean shouldFade) {
        if (menu.getCondition().getAsBoolean()) {
            return this.forceMenu(menu, originalScreen, shouldFade);
        }
        return originalScreen;
    }

    public TitleScreen forceMenu(Menu menu, TitleScreen originalScreen, boolean shouldFade) {
        menu.getApply().run();
        this.setActiveMenu(menu);
        this.applyBackgrounds(menu.getBackground());
        TitleScreen screen = menu.getScreen();
        if (shouldFade) {
            TitleScreenAccessor defaultMenuAccessor = (TitleScreenAccessor) screen;
            defaultMenuAccessor.nitrogen$setFading(true);
            defaultMenuAccessor.nitrogen$setFadeInStart(0L);
        }
        this.migrateSplash(originalScreen, screen);
        if (!NitrogenConfig.CLIENT.active_menu.get().equals(menu.toString())) {
            NitrogenConfig.CLIENT.active_menu.set(menu.toString());
        }
        return screen;
    }

    public void setActiveMenu(Menu activeMenu) {
        this.activeMenu = activeMenu;
    }

    public void clearActiveMenu() {
        this.activeMenu = Menus.MINECRAFT.get();
    }

    public TitleScreen getActiveScreen() {
        return this.getActiveMenu().getScreen();
    }

    public Music getActiveMusic() {
        return this.getActiveMenu().getMusic();
    }

    public void migrateSplash(TitleScreen originalScreen, TitleScreen newScreen) {
        TitleScreenAccessor originalScreenAccessor = (TitleScreenAccessor) originalScreen;
        TitleScreenAccessor newScreenAccessor = (TitleScreenAccessor) newScreen;
        newScreenAccessor.nitrogen$setSplash(originalScreenAccessor.nitrogen$getSplash());
    }

    public void setCustomSplash(TitleScreen screen, Predicate<Calendar> condition, String splash) {
        TitleScreenAccessor screenAccessor = (TitleScreenAccessor) screen;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (condition.test(calendar)) {
            screenAccessor.nitrogen$setSplash(splash);
        }
    }

    public void applyBackgrounds(Menu.Background background) {
        Menu.Background.apply(background);
    }
}
