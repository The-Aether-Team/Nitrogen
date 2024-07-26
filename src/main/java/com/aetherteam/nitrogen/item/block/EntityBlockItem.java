package com.aetherteam.nitrogen.item.block;

import com.aetherteam.nitrogen.client.renderer.NitrogenRenderers;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Used for {@link BlockItem}s that have a {@link BlockEntityWithoutLevelRenderer} attached.
 */
public class EntityBlockItem extends BlockItem {
    private final BlockEntity blockEntity;

    public <B extends Block> EntityBlockItem(B block, BlockEntity blockEntity, Properties properties) {
        super(block, properties);
        this.blockEntity = blockEntity;
        EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> BuiltinItemRendererRegistry.INSTANCE.register(this, NitrogenRenderers.blockEntityWithoutLevelRenderer));
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}