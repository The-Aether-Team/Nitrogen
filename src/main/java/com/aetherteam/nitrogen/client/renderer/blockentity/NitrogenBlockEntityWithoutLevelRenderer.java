package com.aetherteam.nitrogen.client.renderer.blockentity;

import com.aetherteam.nitrogen.item.block.EntityBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Used in the registration of block items that have block entity renderers.
 */
public class NitrogenBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public NitrogenBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Item item = stack.getItem();
        if (item instanceof EntityBlockItem blockItem && blockItem.getBlockEntity().isPresent()) {
            BlockEntity blockEntity = blockItem.getBlockEntity().orElseThrow(() -> new UnsupportedOperationException("BlockEntity was expected, but not supplied."));
            Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        } else {
            super.renderByItem(stack, context, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
