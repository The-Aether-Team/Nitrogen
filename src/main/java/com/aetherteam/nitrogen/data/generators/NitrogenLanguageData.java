package com.aetherteam.nitrogen.data.generators;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.data.providers.NitrogenLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class NitrogenLanguageData extends NitrogenLanguageProvider {
    public NitrogenLanguageData(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
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
