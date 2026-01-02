package com.aetherteam.nitrogen.fabric.mixin.fabric;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.fabric.world.biome.BiomeModificationDataRegistries;
import com.aetherteam.nitrogen.fabric.world.biome.NoneBiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeModificationImpl.class)
public abstract class BiomeModificationImplMixin {
    @Inject(method = "finalizeWorldGen", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/impl/biome/modification/BiomeModificationMarker;fabric_markModified()V", shift = At.Shift.AFTER))
    private void loadDataDrivenBiomeModifications(RegistryAccess impl, CallbackInfo ci) {
        impl.lookupOrThrow(BiomeModificationDataRegistries.BIOME_MODIFIERS_KEY).listElements().forEach(ref -> {
            var modificationData = ref.value();
            var phase = modificationData.phase();

            if (phase == null) {
                if (!(modificationData instanceof NoneBiomeModification)) {
                    Nitrogen.LOGGER.warn("Unable to add Biome Modification as it was found to have a null phase! [Key: {}, Modification: {}]", modificationData);
                }

                return;
            }

            BiomeModifications.create(ref.nitrogen_fabric$getKey().location())
                    .add(modificationData.phase(), modificationData.selector(), modificationData::modify);
        });
    }
}
