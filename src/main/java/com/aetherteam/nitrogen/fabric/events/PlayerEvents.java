package com.aetherteam.nitrogen.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableFloat;

public class PlayerEvents {

    public static final Event<OnBlockDestroy> ON_BLOCK_DESTROY = EventFactory.createArrayBacked(OnBlockDestroy.class, invokers -> (player, blockState, speed, callback) -> {
        for (var invoker : invokers) {
            invoker.onDestroy(player, blockState, speed, callback);
        }
    });

    public interface OnBlockDestroy {
        void onDestroy(Player player, BlockState blockState, MutableFloat speed, CancellableCallback callback);
    }
}
