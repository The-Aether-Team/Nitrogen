package com.aetherteam.nitrogen.client;

import com.aetherteam.nitrogen.event.listeners.TooltipListeners;
import com.aetherteam.nitrogen.network.packet.clientbound.UpdateUserInfoPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NitrogenClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TooltipListeners.onTooltipCreationLowPriority();

        ClientPlayNetworking.registerGlobalReceiver(UpdateUserInfoPacket.TYPE, UpdateUserInfoPacket::execute);
    }
}
