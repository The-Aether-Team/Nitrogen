/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.aetherteam.nitrogen.fabric.network.handling;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface IPayloadContext {

    /**
     * Retrieves the player relevant to this payload. Players are only available in the {@link ConnectionProtocol#PLAY} phase.
     * <p>
     * For server-bound payloads, retrieves the sending {@link ServerPlayer}.
     * <p>
     * For client-bound payloads, retrieves the receiving {@link LocalPlayer}.
     *
     * @throws UnsupportedOperationException when called during the configuration phase.
     */
    Player player();
}
