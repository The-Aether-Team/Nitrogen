package com.aetherteam.nitrogen.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class NitrogenLanguageProvider extends LanguageProvider {
    public NitrogenLanguageProvider(PackOutput output, String id) {
        super(output, id, "en_us");
    }

    public void addPatreonTier(String key, String name) {
        this.add("nitrogen.patreon.tier." + key, name);
    }

    public void addMenuTitle(String key, String name) {
        this.add("nitrogen.menu_title." + key, name);
    }
}
