package com.aetherteam.nitrogen.client.renderer;

import com.aetherteam.nitrogen.client.renderer.blockentity.NitrogenBlockEntityWithoutLevelRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class NitrogenRenderers {
    public static final BuiltinItemRendererRegistry.DynamicItemRenderer blockEntityWithoutLevelRenderer = new NitrogenBlockEntityWithoutLevelRenderer();

}