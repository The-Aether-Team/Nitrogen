package com.aetherteam.nitrogen.fabric.client.events;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public class FogColorHelper {

    private final GameRenderer gameRenderer;
    private final Camera camera;
    private final float partialTicks;

    private float red;
    private float green;
    private float blue;

    public FogColorHelper(Camera camera, float partialTicks, float red, float green, float blue) {
        this.gameRenderer = Minecraft.getInstance().gameRenderer;
        this.camera = camera;
        this.partialTicks = partialTicks;

        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }

    /**
     * {@return the red color value of the fog}
     */
    public float getRed() {
        return red;
    }

    /**
     * Sets the new red color value of the fog.
     *
     * @param red the new red color value
     */
    public void setRed(float red) {
        this.red = red;
    }

    /**
     * {@return the green color value of the fog}
     */
    public float getGreen() {
        return green;
    }

    /**
     * Sets the new green color value of the fog.
     *
     * @param green the new blue color value
     */
    public void setGreen(float green) {
        this.green = green;
    }

    /**
     * {@return the blue color value of the fog}
     */
    public float getBlue() {
        return blue;
    }

    /**
     * Sets the new blue color value of the fog.
     *
     * @param blue the new blue color value
     */
    public void setBlue(float blue) {
        this.blue = blue;
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
