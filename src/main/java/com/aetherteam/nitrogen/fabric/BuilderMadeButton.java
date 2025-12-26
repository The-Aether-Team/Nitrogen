package com.aetherteam.nitrogen.fabric;

import com.aetherteam.nitrogen.fabric.mixin.client.accessor.ButtonBuilderAccessor;
import net.minecraft.client.gui.components.Button;

public class BuilderMadeButton extends Button {

    protected BuilderMadeButton(Button.Builder builder) {
        super(
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$x(),
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$y(),
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$width(),
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$height(),
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$message(),
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$onPress(),
            ((ButtonBuilderAccessor) builder).nitrogen_fabric$createNarration());
    }
}
