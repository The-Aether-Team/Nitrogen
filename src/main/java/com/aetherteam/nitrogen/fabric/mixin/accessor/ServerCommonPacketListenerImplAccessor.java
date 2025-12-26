package com.aetherteam.nitrogen.fabric.mixin.accessor;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerCommonPacketListenerImpl.class)
public interface ServerCommonPacketListenerImplAccessor {
    @Accessor("connection")
    Connection nitrogen_fabric$connection();
}
