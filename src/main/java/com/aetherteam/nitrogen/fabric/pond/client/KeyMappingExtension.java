package com.aetherteam.nitrogen.fabric.pond.client;

import com.mojang.blaze3d.platform.InputConstants;

public interface KeyMappingExtension {

    default InputConstants.Key nitrogen_fabric$getKey() {
        return throwUnimplementedException();
    }

    /**
     * {@return true if the key conflict context and modifier are active and the keyCode matches this binding, false otherwise}
     */
    default boolean nitrogen_fabric$isActiveAndMatches(InputConstants.Key keyCode) {
        return keyCode != InputConstants.UNKNOWN && keyCode.equals(nitrogen_fabric$getKey());
    }

    static <T> T throwUnimplementedException() {
        throw new IllegalStateException("Injected Interface method not implement!");
    }
}
