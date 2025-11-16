package com.aetherteam.nitrogen.fabric.client.events;

import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.material.FogType;

public class FogAdjustmentHelper extends CancellableCallbackImpl {

    private final GameRenderer gameRenderer;
    private final Camera camera;
    private final float partialTicks;

    private final FogRenderer.FogMode mode;
    private final FogType type;
    private float farPlaneDistance;
    private float nearPlaneDistance;
    private FogShape fogShape;

    public FogAdjustmentHelper(Camera camera, float partialTicks, FogRenderer.FogMode mode, FogType type, float farPlaneDistance, float nearPlaneDistance, FogShape fogShape) {
        this.gameRenderer = Minecraft.getInstance().gameRenderer;
        this.camera = camera;
        this.partialTicks = partialTicks;

        this.mode = mode;
        this.type = type;
        setFarPlaneDistance(farPlaneDistance);
        setNearPlaneDistance(nearPlaneDistance);
        setFogShape(fogShape);
    }

    /**
     * {@return the mode of fog being rendered}
     */
    public FogRenderer.FogMode getMode() {
        return mode;
    }

    /**
     * {@return the type of fog being rendered}
     */
    public FogType getType() {
        return type;
    }

    /**
     * {@return the distance to the far plane where the fog ends}
     */
    public float getFarPlaneDistance() {
        return farPlaneDistance;
    }

    /**
     * {@return the distance to the near plane where the fog starts}
     */
    public float getNearPlaneDistance() {
        return nearPlaneDistance;
    }

    /**
     * {@return the shape of the fog being rendered}
     */
    public FogShape getFogShape() {
        return fogShape;
    }

    /**
     * Sets the distance to the far plane of the fog.
     *
     * @param distance the new distance to the far place
     * @see #scaleFarPlaneDistance(float)
     */
    public void setFarPlaneDistance(float distance) {
        farPlaneDistance = distance;
    }

    /**
     * Sets the distance to the near plane of the fog.
     *
     * @param distance the new distance to the near plane
     * @see #scaleNearPlaneDistance(float)
     */
    public void setNearPlaneDistance(float distance) {
        nearPlaneDistance = distance;
    }

    /**
     * Sets the new shape of the fog being rendered. The new shape will only take effect if the event is cancelled.
     *
     * @param shape the new shape of the fog
     */
    public void setFogShape(FogShape shape) {
        fogShape = shape;
    }

    /**
     * Scales the distance to the far plane of the fog by a given factor.
     *
     * @param factor the factor to scale the far plane distance by
     */
    public void scaleFarPlaneDistance(float factor) {
        farPlaneDistance *= factor;
    }

    /**
     * Scales the distance to the near plane of the fog by a given factor.
     *
     * @param factor the factor to scale the near plane distance by
     */
    public void scaleNearPlaneDistance(float factor) {
        nearPlaneDistance *= factor;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
