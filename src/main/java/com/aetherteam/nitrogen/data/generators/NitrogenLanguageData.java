package com.aetherteam.nitrogen.data.generators;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.data.providers.NitrogenLanguageProvider;
import net.minecraft.data.DataGenerator;

public class NitrogenLanguageData extends NitrogenLanguageProvider {
    public NitrogenLanguageData(DataGenerator output) {
        super(output, Nitrogen.MODID);
    }

    @Override
    protected void addTranslations() {
        this.addGuiText("boss.message.far", "You must be in the boss room to interact.");

        this.addPatreonTier("human", "Human");
        this.addPatreonTier("ascentan", "Ascentan");
        this.addPatreonTier("valkyrie", "Valkyrie");
        this.addPatreonTier("arkenzus", "Arkenzus");

        this.addPackDescription("mod", "Nitrogen Resources");
    }
}
