package com.aetherteam.nitrogen.fabric.events;

public class CancellableCallbackImpl implements CancellableCallback {

    public CancellableCallbackImpl() {}

    public CancellableCallbackImpl(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    private boolean isCancelled = false;

    public boolean isCanceled() {
        return this.isCancelled;
    }

    public void setCanceled(boolean value) {
        this.isCancelled = value;
    }
}
