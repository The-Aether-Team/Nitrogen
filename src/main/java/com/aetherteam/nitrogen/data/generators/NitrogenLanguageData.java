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
        this.addPatreonTier("human", "Human");
        this.addPatreonTier("ascentan", "Ascentan");
        this.addPatreonTier("valkyrie", "Valkyrie");
        this.addPatreonTier("arkenzus", "Arkenzus");

        this.addPackDescription("mod", "Nitrogen Resources");
    }
}
