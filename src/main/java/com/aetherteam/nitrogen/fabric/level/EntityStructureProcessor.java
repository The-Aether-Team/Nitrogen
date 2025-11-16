package com.aetherteam.nitrogen.fabric.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface EntityStructureProcessor {
    /**
     * Use this method to process entities from a structure in much the same way as
     * blocks, parameters are analogous.
     *
     * @param world
     * @param seedPos
     * @param rawEntityInfo
     * @param entityInfo
     * @param placementSettings
     * @param template
     *
     * @see ExtendedStructureProcessor#process(LevelReader, BlockPos, BlockPos, StructureTemplate.StructureBlockInfo, StructureTemplate.StructureBlockInfo, StructurePlaceSettings, StructureTemplate)
     */
    default StructureTemplate.StructureEntityInfo nitrogen_fabric$processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo, StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings, StructureTemplate template) {
        return entityInfo;
    }
}
