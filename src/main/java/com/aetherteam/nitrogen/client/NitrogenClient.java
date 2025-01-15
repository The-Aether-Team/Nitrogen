package com.aetherteam.nitrogen.client;

import com.aetherteam.nitrogen.event.listeners.TooltipListeners;
import net.fabricmc.api.ClientModInitializer;

public class NitrogenClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipListeners.init();
    }
}
