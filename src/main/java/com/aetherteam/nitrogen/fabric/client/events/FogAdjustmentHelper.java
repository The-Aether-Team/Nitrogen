package com.aetherteam.nitrogen.fabric.client.events;

import com.aetherteam.nitrogen.fabric.events.CancellableCallbackImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;

public class FogAdjustmentHelper extends CancellableCallbackImpl {

    private final GameRenderer gameRenderer;
    private final Camera camera;
    private final float partialTicks;

    private final FogEnvironment environment;
    private final FogType type;
    private FogData fogData;

    public FogAdjustmentHelper(Camera camera, float partialTicks, FogEnvironment environment, FogType type,  FogData fogData) {
        this.gameRenderer = Minecraft.getInstance().gameRenderer;
        this.camera = camera;
        this.partialTicks = partialTicks;

        this.environment = environment;
        this.type = type;
        this.fogData = fogData;

        setFarPlaneDistance(fogData.environmentalEnd);
        setNearPlaneDistance(fogData.environmentalStart);
    }

    //--

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    //--

    @Nullable
    public FogEnvironment getEnvironment() {
        return environment;
    }

    public FogType getType() {
        return type;
    }

    public float getFarPlaneDistance() {
        return fogData.environmentalEnd;
    }

    public float getNearPlaneDistance() {
        return fogData.environmentalStart;
    }

    public FogData getFogData() {
        return fogData;
    }

    public void setFarPlaneDistance(float distance) {
        fogData.environmentalEnd = distance;
    }

    public void setNearPlaneDistance(float distance) {
        fogData.environmentalStart = distance;
    }

    public void scaleFarPlaneDistance(float factor) {
        fogData.environmentalEnd *= factor;
    }

    public void scaleNearPlaneDistance(float factor) {
        fogData.environmentalStart *= factor;
    }
}
