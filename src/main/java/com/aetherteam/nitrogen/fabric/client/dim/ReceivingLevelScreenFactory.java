package com.aetherteam.nitrogen.fabric.client.dim;

import net.minecraft.client.gui.screens.ReceivingLevelScreen;

import java.util.function.BooleanSupplier;

public interface ReceivingLevelScreenFactory {
    ReceivingLevelScreen create(BooleanSupplier supplier, ReceivingLevelScreen.Reason reason);
}
