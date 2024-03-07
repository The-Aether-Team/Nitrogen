package com.aetherteam.nitrogen.data;

import com.aetherteam.nitrogen.data.generators.NitrogenLanguageData;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class NitrogenDataGenerators {
    public static void onInitializeDataGenerator(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        // Client Data
        generator.addProvider(event.includeClient(), new NitrogenLanguageData(generator));
    }
}
