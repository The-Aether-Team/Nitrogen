package com.aetherteam.nitrogen.data;

import com.aetherteam.nitrogen.data.generators.NitrogenLanguageData;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.SharedConstants;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class NitrogenDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        // Client Data
        pack.addProvider((output, provider) -> new NitrogenLanguageData(output));

        // pack.mcmeta
        pack.addProvider((output, provider) -> {
            PackMetadataGenerator packMeta = new PackMetadataGenerator(output);
            packMeta.add(PackMetadataSection.TYPE, new PackMetadataSection(Component.translatable("pack.nitrogen_internals.mod.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)));
            return packMeta;
        });
    }
}
