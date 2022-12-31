package com.gildedgames.nitrogen.data.generators;

import com.gildedgames.nitrogen.Nitrogen;
import com.gildedgames.nitrogen.data.providers.NitrogenLanguageProvider;
import net.minecraft.data.PackOutput;

public class NitrogenLanguageData extends NitrogenLanguageProvider {
    public NitrogenLanguageData(PackOutput output) {
        super(output, Nitrogen.MODID);
    }

    @Override
    protected void addTranslations() {
        this.addPatreonTier("human", "Human");
        this.addPatreonTier("angel", "Angel");
        this.addPatreonTier("valkyrie", "Valkyrie");
        this.addPatreonTier("arkenzus", "Arkenzus");
    }
}
