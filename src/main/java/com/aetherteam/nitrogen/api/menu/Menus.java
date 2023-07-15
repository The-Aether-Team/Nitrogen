package com.aetherteam.nitrogen.api.menu;

import com.aetherteam.nitrogen.Nitrogen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Menus {
    public static final DeferredRegister<Menu> MENUS = DeferredRegister.create(Nitrogen.MENU_REGISTRY_KEY, Nitrogen.MODID);
    public static final Supplier<IForgeRegistry<Menu>> MENU_REGISTRY = MENUS.makeRegistry(RegistryBuilder::new);

    public static final ResourceLocation MINECRAFT_ICON = new ResourceLocation("textures/block/grass_block_side");
    public static final Component MINECRAFT_NAME = Component.translatable("nitrogen.menu_title.minecraft");
    public static final BooleanSupplier MINECRAFT_CONDITION = () -> true;

    public static final RegistryObject<Menu> MINECRAFT = MENUS.register("minecraft", () -> new Menu(MINECRAFT_ICON, MINECRAFT_NAME, new TitleScreen(true), MINECRAFT_CONDITION));

    public static Menu get(String id) {
        return MENU_REGISTRY.get().getValue(new ResourceLocation(id));
    }
}
