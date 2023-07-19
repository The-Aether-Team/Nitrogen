package com.aetherteam.nitrogen.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class NitrogenLanguageProvider extends LanguageProvider {
    public NitrogenLanguageProvider(PackOutput output, String id) {
        super(output, id, "en_us");
    }

    public void addGuiText(String key, String name) {
        this.add("gui.nitrogen." + key, name);
    }

    public void addMenuTitle(String key, String name) {
        this.add("nitrogen.menu_title." + key, name);
    }

    public void addPatreonTier(String key, String name) {
        this.add("nitrogen.patreon.tier." + key, name);
    }

    public void addClientConfig(String prefix, String key, String name) {
        this.add("config.nitrogen.client." + prefix + "." + key, name);
    }
}
