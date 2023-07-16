package com.aetherteam.nitrogen.client;

import com.aetherteam.nitrogen.Nitrogen;
import com.aetherteam.nitrogen.api.menu.MenuHelper;
import com.aetherteam.nitrogen.api.menu.Menus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Nitrogen.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NitrogenClient {
    public static final MenuHelper MENU_HELPER = new MenuHelper();

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            NitrogenClient.MENU_HELPER.setActiveMenu(Menus.MINECRAFT.get());
        });
    }
}
