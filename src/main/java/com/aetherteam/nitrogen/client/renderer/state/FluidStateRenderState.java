package com.aetherteam.nitrogen.client.renderer.state;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;

public record FluidStateRenderState(FluidState fluidState, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {

    public FluidStateRenderState(FluidState fluidState, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea) {
        this(fluidState, x0, y0, x1, y1, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }

    @Override
    public float scale() {
        return 16.0F;
    }
}
