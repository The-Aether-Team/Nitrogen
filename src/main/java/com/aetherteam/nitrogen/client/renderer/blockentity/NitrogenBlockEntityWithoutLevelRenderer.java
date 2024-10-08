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

import java.util.function.Supplier;

/**
 * Used in the registration of block items that have block entity renderers.
 */
public class NitrogenBlockEntityWithoutLevelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer{
    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (stack.getItem() instanceof EntityBlockItem blockItem) {
            Supplier<? extends BlockEntity> blockEntity = blockItem.getBlockEntity()
                .orElseThrow(() -> new IllegalStateException("Unable to get the required BlockEntity from the block item! [Item: " + blockItem + "]"));

            Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(blockEntity.get(), poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
