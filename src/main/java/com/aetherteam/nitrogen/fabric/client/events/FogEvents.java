package com.aetherteam.nitrogen.fabric.client.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class FogEvents {

    public static final Event<OnFogRender> ON_FOG_RENDER = EventFactory.createArrayBacked(OnFogRender.class, invokers -> helper -> {
        for (var invoker : invokers) invoker.onRenderer(helper);
    });

    public static final Event<OnFogColoring> ON_FOG_COLORING = EventFactory.createArrayBacked(OnFogColoring.class, invokers -> helper -> {
        for (var invoker : invokers) invoker.onColor(helper);
    });

    public interface OnFogRender {
        void onRenderer(FogAdjustmentHelper helper);
    }

    public interface OnFogColoring {
        void onColor(FogColorHelper helper);
    }
}
