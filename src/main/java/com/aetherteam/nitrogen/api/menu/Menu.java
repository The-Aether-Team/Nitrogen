package com.aetherteam.nitrogen.api.menu;

import com.aetherteam.nitrogen.mixin.mixins.client.accessor.CreateWorldScreenAccessor;
import com.aetherteam.nitrogen.mixin.mixins.client.accessor.GuiComponentAccessor;
import com.aetherteam.nitrogen.mixin.mixins.client.accessor.RealmsPlayerScreenAccessor;
import com.aetherteam.nitrogen.mixin.mixins.client.accessor.TabButtonAccessor;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;

import java.util.function.BooleanSupplier;

public class Menu {
    private final ResourceLocation icon;
    private final Component name;
    private final TitleScreen screen;
    private final BooleanSupplier condition;
    private final Runnable apply;
    private final Music music;
    private final Background background;

    public Menu(ResourceLocation icon, Component name, TitleScreen screen, BooleanSupplier condition) {
        this(icon, name, screen, condition, new Properties());
    }

    public Menu(ResourceLocation icon, Component name, TitleScreen screen, BooleanSupplier condition, Properties properties) {
        this(icon, name, screen, condition, properties.apply, properties.music, properties.background);
    }

    public Menu(ResourceLocation icon, Component name, TitleScreen screen, BooleanSupplier condition, Runnable apply, Music music, Background background) {
        this.icon = icon;
        this.name = name;
        this.screen = screen;
        this.condition = condition;
        this.apply = apply;
        this.music = music;
        this.background = background;
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    public Component getName() {
        return this.name;
    }

    public TitleScreen getScreen() {
        return this.screen;
    }

    public BooleanSupplier getCondition() {
        return this.condition;
    }

    public Runnable getApply() {
        return this.apply;
    }

    public Music getMusic() {
        return this.music;
    }

    public Background getBackground() {
        return this.background;
    }

    public ResourceLocation getId() {
        return Menus.MENU_REGISTRY.get().getKey(this);
    }

    public String toString() {
        return this.getId().toString();
    }

    public static class Properties {
        private Runnable apply = () -> {};
        private Music music = Musics.MENU;
        private Background background = Background.MINECRAFT;

        public Properties apply(Runnable apply) {
            this.apply = apply;
            return this;
        }

        public Properties music(Music music) {
            this.music = music;
            return this;
        }

        public Properties background(Background background) {
            this.background = background;
            return this;
        }

        public static Properties propertiesFromType(Menu menu) {
            Properties props = new Properties();
            props.apply = menu.apply;
            props.music = menu.music;
            props.background = menu.background;
            return props;
        }
    }

    public static class Background {
        private static final ResourceLocation DEFAULT_REGULAR_BACKGROUND = GuiComponent.BACKGROUND_LOCATION;
        private static final ResourceLocation DEFAULT_DARK_BACKGROUND = GuiComponent.LIGHT_DIRT_BACKGROUND;
        private static final ResourceLocation DEFAULT_HEADER_SEPARATOR = CreateWorldScreen.HEADER_SEPERATOR;
        private static final ResourceLocation DEFAULT_FOOTER_SEPARATOR = CreateWorldScreen.FOOTER_SEPERATOR;
        private static final ResourceLocation DEFAULT_TAB_BUTTON = TabButtonAccessor.nitrogen$getTextureLocation();

        private ResourceLocation regularBackground = DEFAULT_REGULAR_BACKGROUND;
        private ResourceLocation darkBackground = DEFAULT_DARK_BACKGROUND;
        private ResourceLocation headerSeparator = DEFAULT_HEADER_SEPARATOR;
        private ResourceLocation footerSeparator = DEFAULT_FOOTER_SEPARATOR;
        private ResourceLocation tabButton = DEFAULT_TAB_BUTTON;

        public static final Background MINECRAFT = new Background()
                .regularBackground(DEFAULT_REGULAR_BACKGROUND)
                .darkBackground(DEFAULT_DARK_BACKGROUND)
                .headerSeparator(DEFAULT_HEADER_SEPARATOR)
                .footerSeparator(DEFAULT_FOOTER_SEPARATOR)
                .tabButton(DEFAULT_TAB_BUTTON);

        public static void apply(Background background) {
            GuiComponentAccessor.nitrogen$setBackgroundLocation(background.getRegularBackground());
            RealmsPlayerScreenAccessor.nitrogen$setOptionsBackground(background.getRegularBackground());
            GuiComponentAccessor.nitrogen$setLightDirtBackground(background.getDarkBackground());
            CreateWorldScreenAccessor.nitrogen$setHeaderSeparator(background.getHeaderSeparator());
            CreateWorldScreenAccessor.nitrogen$setFooterSeparator(background.getFooterSeparator());
            TabButtonAccessor.nitrogen$setTextureLocation(background.getTabButton());
        }

        public static void reset() {
            GuiComponentAccessor.nitrogen$setBackgroundLocation(DEFAULT_REGULAR_BACKGROUND);
            RealmsPlayerScreenAccessor.nitrogen$setOptionsBackground(DEFAULT_REGULAR_BACKGROUND);
            GuiComponentAccessor.nitrogen$setLightDirtBackground(DEFAULT_DARK_BACKGROUND);
            CreateWorldScreenAccessor.nitrogen$setHeaderSeparator(DEFAULT_HEADER_SEPARATOR);
            CreateWorldScreenAccessor.nitrogen$setFooterSeparator(DEFAULT_FOOTER_SEPARATOR);
            TabButtonAccessor.nitrogen$setTextureLocation(DEFAULT_TAB_BUTTON);
        }

        public Background regularBackground(ResourceLocation regularBackground) {
            this.regularBackground = regularBackground;
            return this;
        }

        public Background darkBackground(ResourceLocation darkBackground) {
            this.darkBackground = darkBackground;
            return this;
        }

        public Background headerSeparator(ResourceLocation headerSeparator) {
            this.headerSeparator = headerSeparator;
            return this;
        }

        public Background footerSeparator(ResourceLocation footerSeparator) {
            this.footerSeparator = footerSeparator;
            return this;
        }

        public Background tabButton(ResourceLocation tabButton) {
            this.tabButton = tabButton;
            return this;
        }

        public ResourceLocation getRegularBackground() {
            return this.regularBackground;
        }

        public ResourceLocation getDarkBackground() {
            return this.darkBackground;
        }

        public ResourceLocation getHeaderSeparator() {
            return this.headerSeparator;
        }

        public ResourceLocation getFooterSeparator() {
            return this.footerSeparator;
        }

        public ResourceLocation getTabButton() {
            return this.tabButton;
        }
    }
}