package com.aetherteam.nitrogen.fabric.events;

public class FallHelper extends CancellableCallbackImpl {

    private double distance;
    private float damageMultiplier;

    public FallHelper(double distance, float damageMultiplier) {
        this.setDistance(distance);
        this.setDamageMultiplier(damageMultiplier);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

}
