package com.aetherteam.nitrogen.fabric.events;

public class FallHelper extends CancellableCallbackImpl {

    private float distance;
    private float damageMultiplier;

    public FallHelper(float distance, float damageMultiplier) {
        this.setDistance(distance);
        this.setDamageMultiplier(damageMultiplier);
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

}
