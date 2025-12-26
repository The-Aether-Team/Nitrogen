package com.aetherteam.nitrogen.fabric.client.dim;

import com.aetherteam.nitrogen.fabric.client.events.ClientDimensionEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ClientDimensionUtils {

    @Nullable
    public static ReceivingLevelScreenFactory getScreenFromLevel(@Nullable Level target, @Nullable Level source) {
        ResourceKey<Level> toDimension = null;
        ResourceKey<Level> fromDimension = null;

        if (source != null) {
            fromDimension = source.dimension();

            if (target != null) toDimension = target.dimension();
        }

        return ClientDimensionEvents.ON_LEVEL_TRANSITION_SCREEN.invoker().getAlternativeScreen(toDimension, fromDimension);
    }

}
