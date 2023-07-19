package com.aetherteam.nitrogen.data.generators;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.data.providers.NitrogenLanguageProvider;
import net.minecraft.data.PackOutput;

public class NitrogenLanguageData extends NitrogenLanguageProvider {
    public NitrogenLanguageData(PackOutput output) {
        super(output, Nitrogen.MODID);
    }

    @Override
    protected void addTranslations() {
        this.addGuiText("button.menu_list", "Menu List");
        this.addGuiText("button.menu_launch", "Launch Menu");
        this.addGuiText("title.menu_selection", "Choose a Main Menu");

        this.addMenuTitle("minecraft", "Minecraft");

        this.addPatreonTier("human", "Human");
        this.addPatreonTier("ascentan", "Ascentan");
        this.addPatreonTier("valkyrie", "Valkyrie");
        this.addPatreonTier("arkenzus", "Arkenzus");

        this.addClientConfig("menu", "enable_menu_api", "Determines whether the Menu API is enabled or not");
        this.addClientConfig("menu", "active_menu", "Sets the current active menu title screen");
        this.addClientConfig("menu", "enable_menu_list_button", "Adds a button to the top right of the main menu screen to open a menu selection screen");
    }
}
