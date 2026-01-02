package com.aetherteam.nitrogen.fabric.mixin.client;

import com.aetherteam.nitrogen.fabric.events.AddPackFindersEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;

import java.nio.file.Path;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @WrapOperation(method = "openCreateWorldScreen", at = @At(value = "NEW", target = "([Lnet/minecraft/server/packs/repository/RepositorySource;)Lnet/minecraft/server/packs/repository/PackRepository;"))
    private static PackRepository nitrogen_fabric$addPacks(RepositorySource[] sources, Operation<PackRepository> original) {
        var repo = original.call((Object) sources);

        AddPackFindersEvent.invokeEvent(PackType.SERVER_DATA, repo);

        return repo;
    }

    @WrapOperation(method = "getDataPackSelectionSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/ServerPacksSource;createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;"))
    private static PackRepository nitrogen_fabric$addPacks(Path folder, DirectoryValidator validator, Operation<PackRepository> original) {
        var repo = original.call(folder, validator);

        AddPackFindersEvent.invokeEvent(PackType.SERVER_DATA, repo);

        return repo;
    }
}
