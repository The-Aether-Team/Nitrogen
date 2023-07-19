package com.aetherteam.nitrogen;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NitrogenConfig {
    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Boolean> enable_menu_api;
        public final ForgeConfigSpec.ConfigValue<String> active_menu;
        public final ForgeConfigSpec.ConfigValue<Boolean> enable_menu_list_button;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("Menu");
            enable_menu_api = builder
                    .comment("Determines whether the Menu API is enabled or not")
                    .translation("config.nitrogen.client.menu.enable_menu_api")
                    .define("Enable Menu API", false);
            active_menu = builder
                    .comment("Sets the current active menu title screen")
                    .translation("config.nitrogen.client.menu.active_menu")
                    .define("Active Menu", "nitrogen:minecraft");
            enable_menu_list_button = builder
                    .comment("Adds a button to the top right of the main menu screen to open a menu selection screen")
                    .translation("config.nitrogen.client.menu.enable_menu_list_button")
                    .define("Enables menu selection button", false);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }
}
