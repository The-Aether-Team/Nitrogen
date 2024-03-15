package com.aetherteam.nitrogen.data.providers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.function.Supplier;

public abstract class NitrogenLanguageProvider extends LanguageProvider {
    protected final String id;

    public NitrogenLanguageProvider(PackOutput output, String id) {
        super(output, id, "en_us");
        this.id = id;
    }

    public void addDiscDesc(Supplier<? extends Item> key, String name) {
        this.add(key.get().getDescriptionId() + ".desc", name);
    }

    public void addTrim(String key, String name) {
        this.add("trim_material." + this.id + "." + key, name + " Material");
    }

    public void addEffectDesc(Supplier<? extends MobEffect> key, String name) {
        this.add(key.get().getDescriptionId() + ".description", name);
    }

    public void addDimension(ResourceKey<Level> dimension, String name) {
        this.add("dimension." + this.id + "." + dimension.location().getPath(), name);
    }

    public void addBiome(ResourceKey<Biome> biome, String name) {
        this.add("biome." + this.id + "." + biome.location().getPath(), name);
    }

    public void addStructure(ResourceKey<Structure> structure, String name) {
        this.add("structure." + this.id + "." + structure.location().getPath(), name);
    }

    public void addContainerType(Supplier<? extends MenuType<?>> key, String name) {
        ResourceLocation location = BuiltInRegistries.MENU.getKey(key.get());
        if (location != null) {
            this.add("menu." + location.toString().replace(":", "."), name);
        }
    }

    public void addContainerType(String key, String name) {
        this.add("menu." + this.id + "." + key, name);
    }

    public void addCreativeTab(CreativeModeTab tab, String name) {
        this.add(tab.getDisplayName().getString(), name);
    }

    public void addAdvancement(String key, String name) {
        this.add("advancement." + this.id + "." + key, name);
    }

    public void addAdvancementDesc(String key, String name) {
        this.add("advancement." + this.id + "." + key + ".desc", name);
    }

    public void addSubtitle(String category, String key, String name) {
        this.add("subtitles." + this.id + "." + category + "." + key, name);
    }

    public void addDeath(String key, String name) {
        this.add("death.attack." + this.id + "." + key, name);
    }

    public void addMenuText(String key, String name) {
        this.addGuiText("menu." + key, name);
    }

    public void addGuiText(String key, String name) {
        this.add("gui." + this.id + "." + key, name);
    }

    public void addGeneric(String key, String name) {
        this.add(this.id + "." + key, name);
    }

    public void addCommand(String key, String name) {
        this.add("commands." + this.id + "." + key, name);
    }

    public void addKeyInfo(String key, String name) {
        this.add("key." + this.id + "." + key, name);
    }

    public void addCuriosIdentifier(String key, String name) {
        this.add("curios.identifier." + key, name);
    }

    public void addCuriosModifier(String key, String name) {
        this.add("curios.modifiers." + key, name);
    }

    public void addServerConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".server." + prefix + "." + key, name);
    }

    public void addCommonConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".common." + prefix + "." + key, name);
    }

    public void addClientConfig(String prefix, String key, String name) {
        this.add("config." + this.id + ".client." + prefix + "." + key, name);
    }

    public void addPackTitle(String packName, String description) {
        this.add("pack." + this.id + "." + packName + ".title", description);
    }

    public void addPackDescription(String packName, String description) {
        this.add("pack." + this.id + "." + packName + ".description", description);
    }

    public void addPatreonTier(String key, String name) {
        this.add(this.id + ".patreon.tier." + key, name);
    }
}
