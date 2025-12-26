package com.aetherteam.nitrogen.fabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NitrogenFabricMixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var mixinClassPath = getClassPath(mixinClassName);

        if (mixinClassPath.contains("accessories")) {
            return FabricLoader.getInstance().isModLoaded("accessories");
        }

        return true;
    }

    private static String getClassPath(String fullClassPath) {
        var pathParts = new ArrayList<>(Arrays.asList(fullClassPath.split("\\.")));

        pathParts.removeLast();

        return String.join(".", pathParts);
    }

    @Override public void onLoad(String mixinPackage) {}

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override public String getRefMapperConfig() { return null; }
    @Override public List<String> getMixins() { return null; }

    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
