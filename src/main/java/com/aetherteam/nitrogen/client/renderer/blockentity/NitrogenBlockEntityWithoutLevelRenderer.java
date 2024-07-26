package com.aetherteam.nitrogen.client.renderer.blockentity;

import com.aetherteam.nitrogen.item.block.EntityBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Used in the registration of block items that have block entity renderers.
 */
public class NitrogenBlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (item instanceof EntityBlockItem blockItem) {
            BlockEntity blockEntity = blockItem.getBlockEntity();
            if (blockEntity == null)
                new UnsupportedOperationException("BlockEntity was expected, but not supplied.");
            Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}