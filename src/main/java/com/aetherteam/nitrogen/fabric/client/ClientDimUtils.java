package com.aetherteam.nitrogen.fabric.client;

import com.aetherteam.aether.client.gui.screen.menu.AetherReceivingLevelScreen;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class ClientDimUtils {

    @Nullable
    public static ReceivingLevelScreenFactory getScreenFromLevel(@Nullable Level target, @Nullable Level source) {
        if (source == null) { //source level is null on login: transition screen should not appear in this case
            return getScreen(null, null);
        } else if (target == null) { //the target level shouldn't ever be null, but anyone could call Minecraft.setLevel and pass null in
            return getScreen(null, source.dimension());
        }
        return getScreen(target.dimension(), source.dimension());
    }

    @Nullable
    public static ReceivingLevelScreenFactory getScreen(@Nullable ResourceKey<Level> toDimension, @Nullable ResourceKey<Level> fromDimension) {
        if (Objects.equals(toDimension, AetherDimensions.AETHER_LEVEL) || Objects.equals(fromDimension, AetherDimensions.AETHER_LEVEL))  {
            return AetherReceivingLevelScreen::new;
        }
        return null;
    }

    public interface ReceivingLevelScreenFactory {
        ReceivingLevelScreen create(BooleanSupplier supplier, ReceivingLevelScreen.Reason reason);
    }
}
