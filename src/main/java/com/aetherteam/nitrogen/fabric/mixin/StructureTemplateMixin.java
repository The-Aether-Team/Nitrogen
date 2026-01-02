package com.aetherteam.nitrogen.fabric.mixin;

import com.aetherteam.nitrogen.fabric.level.EntityStructureProcessor;
import com.aetherteam.nitrogen.fabric.level.ExtendedStructureProcessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
    @Shadow
    public static Vec3 transform(Vec3 target, Mirror mirror, Rotation rotation, BlockPos centerOffset) { return null; }

    @Shadow
    public static BlockPos calculateRelativePosition(StructurePlaceSettings decorator, BlockPos pos) { return null; }

    @Unique
    private static final boolean isPortingLibLoaded = FabricLoader.getInstance().isModLoaded("porting_lib_extensions");

    @Unique
    private static final ThreadLocal<@Nullable StructurePlaceSettings> capturedSettings = ThreadLocal.withInitial(() -> null);

    @Unique
    private static final ThreadLocal<StructureTemplate> processBlockInfosTemplateCache = ThreadLocal.withInitial(() -> null);

    @WrapOperation(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;processBlockInfos(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Ljava/util/List;)Ljava/util/List;"))
    private List<StructureTemplate.StructureBlockInfo> nitrogen_fabric$setTemplateCache(ServerLevelAccessor serverLevel, BlockPos offset, BlockPos pos, StructurePlaceSettings settings, List<StructureTemplate.StructureBlockInfo> blockInfos, Operation<List<StructureTemplate.StructureBlockInfo>> original) {
        processBlockInfosTemplateCache.set((StructureTemplate) (Object) this);

        return original.call(serverLevel, offset, pos, settings, blockInfos);
    }

    @WrapOperation(method = "processBlockInfos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureProcessor;processBlock(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;"))
    private static StructureTemplate.StructureBlockInfo nitrogen_fabric$processExtendedProcessor(StructureProcessor instance, LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings, Operation<StructureTemplate.StructureBlockInfo> original) {
        return (instance instanceof ExtendedStructureProcessor extendedStructureProcessor)
            ? extendedStructureProcessor.process(level, offset, pos, blockInfo, relativeBlockInfo, settings, processBlockInfosTemplateCache.get())
            : original.call(instance, level, offset, pos, blockInfo, relativeBlockInfo, settings);
    }

    @WrapOperation(method = "placeInWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;ZLnet/minecraft/util/ProblemReporter;)V"))
    private void nitrogen_fabric$captureSettings(StructureTemplate instance, ServerLevelAccessor level, BlockPos pos, Mirror mirror, Rotation rotation, BlockPos offset, BoundingBox boundingBox, boolean finalize, ProblemReporter problemReporter, Operation<Void> original, @Local(argsOnly = true) StructurePlaceSettings settings) {
        if (!isPortingLibLoaded) capturedSettings.set(settings);

        original.call(instance, level, pos, mirror, rotation, offset, boundingBox, finalize, problemReporter);
    }

    @WrapOperation(method = "placeEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private <E> Iterator<E> nitrogen_fabric$processEntityInfo(List<?> instance, Operation<Iterator<E>> original,
                                                           @Share(value = "entity_process_occurred") LocalBooleanRef entityProcessOccurred,
                                                           @Local(argsOnly = true) ServerLevelAccessor serverLevel,
                                                           @Local(argsOnly = true, ordinal = 0) BlockPos blockPos) {
        List<StructureTemplate.StructureEntityInfo> entityInfos = (List<StructureTemplate.StructureEntityInfo>) instance;
        List<StructureTemplate.StructureEntityInfo> processedEntityInfo = entityInfos;

        entityProcessOccurred.set(false);

        if (!isPortingLibLoaded) {
            StructurePlaceSettings settings = Objects.requireNonNull(capturedSettings.get(), "[NitrogenFabric] Unable to get the StructurePlaceSettings to process the given StructureEntityInfo");

            var entityProcessors = settings.getProcessors().stream()
                .map(structureProcessor -> structureProcessor instanceof EntityStructureProcessor entityStructureProcessor ? entityStructureProcessor : null)
                .filter(Objects::nonNull)
                .toList();

            if (!entityProcessors.isEmpty()) {
                processedEntityInfo = new ArrayList<>();

                for (var entityInfo : entityInfos) {
                    var pos = this.transform(entityInfo.pos, settings.getMirror(), settings.getRotation(), settings.getRotationPivot())
                        .add(Vec3.atLowerCornerOf(blockPos));

                    var blockpos = this.calculateRelativePosition(settings, entityInfo.blockPos)
                        .offset(blockPos);

                    var info = new StructureTemplate.StructureEntityInfo(pos, blockpos, entityInfo.nbt);

                    for (EntityStructureProcessor entityProcessor : entityProcessors) {
                        info = entityProcessor.nitrogen_fabric$processEntity(serverLevel, blockPos, entityInfo, info, settings, (StructureTemplate) (Object) this);
                        if (info == null) break;
                    }

                    if (info != null) processedEntityInfo.add(info);
                }

                entityProcessOccurred.set(true);
            }
        }

        return original.call(processedEntityInfo);
    }

    @WrapOperation(method = "placeEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;"))
    private BlockPos nitrogen_fabric$preventWrongTransforms_BlockPos(BlockPos instance, Vec3i vector, Operation<BlockPos> original,
                                                                  @Share(value = "entity_process_occurred") LocalBooleanRef entityProcessOccurred,
                                                                  @Local() StructureTemplate.StructureEntityInfo structureEntityInfo) {
        return (entityProcessOccurred.get())
            ? structureEntityInfo.blockPos
            : original.call(instance, vector);
    }

    @WrapOperation(method = "placeEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 nitrogen_fabric$preventWrongTransforms_Vec3(Vec3 instance, double x, double y, double z, Operation<Vec3> original,
                                                          @Share(value = "entity_process_occurred") LocalBooleanRef entityProcessOccurred,
                                                          @Local() StructureTemplate.StructureEntityInfo structureEntityInfo) {
        return (entityProcessOccurred.get())
            ? structureEntityInfo.pos
            : original.call(instance, x, y, z);
    }
}
