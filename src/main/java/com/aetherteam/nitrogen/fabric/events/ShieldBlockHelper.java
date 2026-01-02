package com.aetherteam.nitrogen.fabric.events;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;

public class ShieldBlockHelper extends CancellableCallbackImpl {

    private final DamageSource source;
    private float damageAmount;

    private final float originalBlockedDamage;

    private float blockedDamage;

    private final boolean originalBlocked;
    private boolean currentlyBlocked;

    public ShieldBlockHelper(DamageSource source, float damageAmount, float blockedDamage, boolean originalBlocked) {
        super(originalBlocked);
        this.source = source;
        this.damageAmount = damageAmount;

        this.originalBlockedDamage = blockedDamage;
        this.blockedDamage = blockedDamage;

        this.originalBlocked = originalBlocked;
    }

    public DamageSource source() {
        return source;
    }

    public float damageAmount() {
        return damageAmount;
    }

    public float blockedDamage() {
        return blockedDamage;
    }

    public float originalBlockedDamage() {
        return originalBlockedDamage;
    }

    public boolean isOriginalBlocked() {
        return originalBlocked;
    }

    public boolean isCurrentlyBlocked() {
        return currentlyBlocked;
    }

    public ShieldBlockHelper setBlockedDamage(float blockedDamage) {
        this.blockedDamage = Mth.clamp(blockedDamage, 0, damageAmount);
        return this;
    }
}
