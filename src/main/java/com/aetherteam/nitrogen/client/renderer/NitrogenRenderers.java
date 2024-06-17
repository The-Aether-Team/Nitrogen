package com.aetherteam.nitrogen.client.renderer;

import com.aetherteam.nitrogen.client.renderer.blockentity.NitrogenBlockEntityWithoutLevelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.util.Lazy;

public class NitrogenRenderers {
    public static final Lazy<BlockEntityWithoutLevelRenderer> blockEntityWithoutLevelRenderer = Lazy.of(() ->
        new NitrogenBlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

    public static final IClientItemExtensions entityBlockItemRenderProperties = new IClientItemExtensions() {
        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return NitrogenRenderers.blockEntityWithoutLevelRenderer.get();
        }
    };
}
