package com.aetherteam.nitrogen;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class NitrogenConfig {
    public static class Client {
        public final ForgeConfigSpec.ConfigValue<String> active_menu;

        //todo: config for displaying nitrogen menu selection screen; off by default
        //  will be able to be checked by aether to determine whether to display its menu toggle-related buttons
        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("Menu");
            active_menu = builder
                    .comment("Sets the current active menu title screen")
                    .translation("config.nitrogen.client.menu.active_menu")
                    .define("Active Menu", "nitrogen:minecraft");
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
