package com.aetherteam.nitrogen.fabric.pond;

import java.util.Map;

public interface LanguageExtension {

    default void nitrogen_fabric$setLanguageData(Map<String, String> data) {
        throwUnimplementedException();
    }

    default Map<String, String> nitrogen_fabric$getLanguageData() {
        return throwUnimplementedException();
    }

    static <T> T throwUnimplementedException() {
        throw new IllegalStateException("Injected Interface method not implement!");
    }
}
