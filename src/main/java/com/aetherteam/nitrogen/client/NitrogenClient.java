package com.aetherteam.nitrogen.client;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.api.menu.MenuHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Nitrogen.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NitrogenClient {
    public static final MenuHelper MENU_HELPER = new MenuHelper();
}
