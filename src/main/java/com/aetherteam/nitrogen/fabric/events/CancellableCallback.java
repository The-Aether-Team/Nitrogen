package com.aetherteam.nitrogen.fabric.events;

public interface CancellableCallback {

    boolean isCanceled();

    void setCanceled(boolean value);
}
