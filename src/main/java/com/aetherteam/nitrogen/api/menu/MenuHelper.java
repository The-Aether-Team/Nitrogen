package com.aetherteam.nitrogen.api.menu;

import com.aetherteam.nitrogen.mixin.mixins.client.accessor.TitleScreenAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.sounds.Music;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class MenuHelper {
    private Menu activeMenu = null;
    private TitleScreen fallbackTitleScreen = null;
    private Menu.Background fallbackBackground = null;
    private String lastSplash = null;
    private boolean shouldFade = true;
    //todo: give fallback an entry in the menu switcher gui?

    public MenuHelper() { }

    public Menu getActiveMenu() {
        return this.activeMenu;
    }

    public void prepareMenu(Menu menu) {
        if (menu.getCondition().getAsBoolean()) {
            this.setActiveMenu(menu);
        }
    }

    public TitleScreen applyMenu(Menu menu) {
        menu.getApply().run();
        TitleScreen screen = this.checkFallbackScreen(menu, menu.getScreen());
        if (this.shouldFade()) {
            TitleScreenAccessor defaultMenuAccessor = (TitleScreenAccessor) screen;
            defaultMenuAccessor.nitrogen$setFading(true);
            defaultMenuAccessor.nitrogen$setFadeInStart(0L);
        }
        Menu.Background background = this.checkFallbackBackground(menu, screen, menu.getBackground());
        this.applyBackgrounds(background);
        if (this.getLastSplash() != null) {
            this.migrateSplash(this.getLastSplash(), screen);
        }
        return screen;
    }

    private TitleScreen checkFallbackScreen(Menu menu, TitleScreen screen) {
        if ((screen.getClass() == TitleScreen.class || menu == Menus.MINECRAFT.get()) && this.getFallbackTitleScreen() != null) {
            screen = this.getFallbackTitleScreen();
        }
        return screen;
    }

    private Menu.Background checkFallbackBackground(Menu menu, TitleScreen screen, Menu.Background background) {
        if ((screen.getClass() == TitleScreen.class || menu == Menus.MINECRAFT.get()) && this.getFallbackBackground() != null) {
            background = this.getFallbackBackground();
        }
        return background;
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

    public TitleScreen getFallbackTitleScreen() {
        return this.fallbackTitleScreen;
    }

    public void setFallbackTitleScreen(TitleScreen fallbackTitleScreen) {
        this.fallbackTitleScreen = fallbackTitleScreen;
    }

    public Menu.Background getFallbackBackground() {
        return this.fallbackBackground;
    }

    public void setFallbackBackground(Menu.Background fallbackBackground) {
        this.fallbackBackground = fallbackBackground;
    }

    public String getLastSplash() {
        return this.lastSplash;
    }

    public void setLastSplash(String lastSplash) {
        this.lastSplash = lastSplash;
    }

    public void migrateSplash(String originalSplash, TitleScreen newScreen) {
        TitleScreenAccessor newScreenAccessor = (TitleScreenAccessor) newScreen;
        newScreenAccessor.nitrogen$setSplash(originalSplash);
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

    public void resetBackgrounds() {
        Menu.Background.reset();
    }

    public boolean doesScreenMatchMenu(TitleScreen titleScreen) {
        boolean matches = false;
        List<Screen> menuScreens = Menus.getMenuScreens();
        for (Screen screen : menuScreens) {
            if (titleScreen.getClass().equals(screen.getClass())) {
                matches = true;
                break;
            }
        }
        return matches;
    }

    public boolean shouldFade() {
        return this.shouldFade;
    }

    public void setShouldFade(boolean shouldFade) {
        this.shouldFade = shouldFade;
    }
}
