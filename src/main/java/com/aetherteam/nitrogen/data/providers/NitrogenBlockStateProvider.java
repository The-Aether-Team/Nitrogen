package com.aetherteam.nitrogen.data.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public abstract class NitrogenBlockStateProvider extends FabricModelProvider {
    public NitrogenBlockStateProvider(PackOutput output, String id, ExistingFileHelper helper) {
        super(output, id, helper);
    }

    public String name(Block block) {
        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
        if (location != null) {
            return location.getPath();
        } else {
            throw new IllegalStateException("Unknown block: " + block.toString());
        }
    }

    public ResourceLocation texture(String name) {
        return this.modLoc("block/" + name);
    }

    public ResourceLocation texture(String name, String location) {
        return this.modLoc("block/" + location + name);
    }

    public ResourceLocation texture(String name, String location, String suffix) {
        return this.modLoc("block/" + location + name + suffix);
    }

    public ResourceLocation extend(ResourceLocation location, String suffix) {
        return new ResourceLocation(location.getNamespace(), location.getPath() + suffix);
    }

    public void block(Block block, String location) {
        this.simpleBlock(block, this.cubeAll(block, location));
    }

    public void portal(Block block) {
        ModelFile portal_ew = this.models().withExistingParent(this.name(block) + "_ew", this.mcLoc("block/nether_portal_ew"))
                .texture("particle", this.modLoc("block/miscellaneous/" + this.name(block)))
                .texture("portal", this.modLoc("block/miscellaneous/" + this.name(block)))
                .renderType(new ResourceLocation("translucent"));
        ModelFile portal_ns = this.models().withExistingParent(this.name(block) + "_ns", this.mcLoc("block/nether_portal_ns"))
                .texture("particle", this.modLoc("block/miscellaneous/" + this.name(block)))
                .texture("portal", this.modLoc("block/miscellaneous/" + this.name(block)))
                .renderType(new ResourceLocation("translucent"));
        this.getVariantBuilder(block).forAllStates(state -> {
            Direction.Axis axis = state.getValue(NetherPortalBlock.AXIS);
            return ConfiguredModel.builder()
                    .modelFile(axis == Direction.Axis.Z ? portal_ew : portal_ns)
                    .build();
        });
    }

    public void translucentBlock(Block block, String location) {
        this.simpleBlock(block, this.cubeAllTranslucent(block, location));
    }

    public void log(RotatedPillarBlock block) {
        this.axisBlock(block, this.texture(this.name(block), "natural/"), this.extend(this.texture(this.name(block), "natural/"), "_top"));
    }

    public void wood(RotatedPillarBlock block, RotatedPillarBlock baseBlock) {
        this.axisBlock(block, this.texture(this.name(baseBlock), "natural/"), this.texture(this.name(baseBlock), "natural/"));
    }

    public void pane(IronBarsBlock block, GlassBlock glass, String location) {
        this.paneBlockWithRenderType(block, this.texture(this.name(glass), location), this.extend(this.texture(this.name(block), location), "_top"), ResourceLocation.tryParse("translucent"));
    }

    public void torchBlock(Block block, Block wall) {
        ModelFile torch = this.models().torch(this.name(block), this.texture(this.name(block), "utility/")).renderType(new ResourceLocation("cutout"));
        ModelFile wallTorch = this.models().torchWall(this.name(wall), this.texture(this.name(block), "utility/")).renderType(new ResourceLocation("cutout"));
        this.simpleBlock(block, torch);
        getVariantBuilder(wall).forAllStates(state ->
                ConfiguredModel.builder()
                        .modelFile(wallTorch)
                        .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 90) % 360)
                        .build());
    }

    public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ResourceLocation texture) {
        ModelFile sign = this.models().sign(this.name(signBlock), texture);
        this.signBlock(signBlock, wallSignBlock, sign);
    }

    public void crossBlock(Block block, String location) {
        this.crossBlock(block, models().cross(this.name(block), this.texture(this.name(block), location)).renderType(new ResourceLocation("cutout")));
    }

    public void crossBlock(Block block, ModelFile model) {
        this.getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(model).build());
    }

    public void pottedPlant(Block block, Block flower, String location) {
        ModelFile pot = this.models().withExistingParent(this.name(block), this.mcLoc("block/flower_pot_cross")).texture("plant", this.modLoc("block/" + location + this.name(flower))).renderType(new ResourceLocation("cutout"));
        this.getVariantBuilder(block).partialState().addModels(new ConfiguredModel(pot));
    }

    public void saplingBlock(Block block, String location) {
        ModelFile sapling = models().cross(this.name(block), this.texture(this.name(block), location)).renderType(new ResourceLocation("cutout"));
        this.getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(sapling).build(), SaplingBlock.STAGE);
    }

    public void chest(Block block, ModelFile chest) {
        this.getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder().modelFile(chest).build(),
                ChestBlock.TYPE, ChestBlock.WATERLOGGED);
    }

    public void fence(FenceBlock block, Block baseBlock, String location) {
        this.fenceBlock(block, this.texture(this.name(baseBlock), location));
        this.fenceColumn(block, this.name(baseBlock), location);
    }

    public void fenceColumn(CrossCollisionBlock block, String side, String location) {
        String baseName = this.name(block);
        this.fourWayBlock(block,
                this.models().fencePost(baseName + "_post", this.texture(side, location)),
                this.models().fenceSide(baseName + "_side", this.texture(side, location)));
    }

    public void fenceGateBlock(FenceGateBlock block, Block baseBlock, String location) {
        this.fenceGateBlockInternal(block, this.name(block), this.texture(this.name(baseBlock), location));
    }

    public void fenceGateBlockInternal(FenceGateBlock block, String baseName, ResourceLocation texture) {
        ModelFile gate = this.models().fenceGate(baseName, texture);
        ModelFile gateOpen = this.models().fenceGateOpen(baseName + "_open", texture);
        ModelFile gateWall = this.models().fenceGateWall(baseName + "_wall", texture);
        ModelFile gateWallOpen = this.models().fenceGateWallOpen(baseName + "_wall_open", texture);
        this.fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
    }

    public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
        this.doorBlockWithRenderType(block, bottom, top, "cutout");
    }

    public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
        this.trapdoorBlockWithRenderType(block, texture, orientable, "cutout");
    }

    public void buttonBlock(ButtonBlock block, ResourceLocation texture) {
        ModelFile button = this.models().button(this.name(block), texture);
        ModelFile buttonPressed = this.models().buttonPressed(this.name(block) + "_pressed", texture);
        this.buttonBlock(block, button, buttonPressed);
    }

    public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
        ModelFile pressurePlate = this.models().pressurePlate(this.name(block), texture);
        ModelFile pressurePlateDown = this.models().pressurePlateDown(this.name(block) + "_down", texture);
        this.pressurePlateBlock(block, pressurePlate, pressurePlateDown);
    }

    public void wallBlock(WallBlock block, Block baseBlock, String location) {
        this.wallBlockInternal(block, this.name(block), this.texture(this.name(baseBlock), location));
    }

    public void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
        this.wallBlock(block, this.models().wallPost(baseName + "_post", texture),
                this.models().wallSide(baseName + "_side", texture),
                this.models().wallSideTall(baseName + "_side_tall", texture));
    }

    protected BlockModelBuilder makeWallPostModel(int width, int height, String name) {
        return models().withExistingParent(name, this.mcLoc("block/block"))
                .element().from(8 - width, 0.0F, 8 - width).to(8 + width, height, 8 + width)
                .face(Direction.DOWN).texture("#top").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#top").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#side").end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end().end();
    }

    protected BlockModelBuilder makeWallSideModel(int length, int height, String name, ModelBuilder.FaceRotation faceRotation, int u1, int u2) {
        return models().withExistingParent(name, this.mcLoc("block/block"))
                .element().from(5.0F, 0.0F, 0.0F).to(11.0F, height, length)
                .face(Direction.DOWN).texture("#top").rotation(faceRotation).uvs(u1, 5, u2, 11).cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#top").rotation(faceRotation).uvs(u1, 5, u2, 11).end()
                .face(Direction.NORTH).texture("#side").cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).texture("#side").end()
                .face(Direction.WEST).texture("#side").end()
                .face(Direction.EAST).texture("#side").end().end();
    }

    public void logWallBlock(WallBlock block, Block baseBlock, String location, String modid, boolean postUsesTop, ModelFile postBig, ModelFile postShort, ModelFile postTall, ModelFile side, ModelFile sideAlt, ModelFile sideTall, ModelFile sideTallAlt, ModelFile sideShort, ModelFile sideAltShort, ModelFile sideTallShort, ModelFile sideTallAltShort) {
        this.logWallBlockInternal(block, this.name(block), new ResourceLocation(modid, "block/" + location + this.name(baseBlock)), postUsesTop, postBig, postShort, postTall, side, sideAlt, sideTall, sideTallAlt, sideShort, sideAltShort, sideTallShort, sideTallAltShort);
    }

    private void logWallBlockInternal(WallBlock block, String baseName, ResourceLocation texture, boolean postUsesTop, ModelFile postBig, ModelFile postShort, ModelFile postTall, ModelFile side, ModelFile sideAlt, ModelFile sideTall, ModelFile sideTallAlt, ModelFile sideShort, ModelFile sideAltShort, ModelFile sideTallShort, ModelFile sideTallAltShort) {
        this.logWallBlock(
                this.getMultipartBuilder(block),
                models().getBuilder(baseName + "_post_short").parent(postShort).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_post_tall").parent(postTall).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side").parent(side).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side_alt").parent(sideAlt).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side_tall").parent(sideTall).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side_tall_alt").parent(sideTallAlt).texture("particle", texture).texture("top", texture).texture("side", texture)
        );

        this.logWallBlockWithPost(
                this.getMultipartBuilder(block),
                models().getBuilder(baseName + "_post").parent(postBig).texture("particle", texture).texture("top", postUsesTop ? (texture + "_top") : texture.toString()).texture("side", texture),
                models().getBuilder(baseName + "_side_short").parent(sideShort).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side_alt_short").parent(sideAltShort).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side_tall_short").parent(sideTallShort).texture("particle", texture).texture("top", texture).texture("side", texture),
                models().getBuilder(baseName + "_side_tall_alt_short").parent(sideTallAltShort).texture("particle", texture).texture("top", texture).texture("side", texture)
        );
    }

    public void logWallBlock(MultiPartBlockStateBuilder builder, ModelFile postShort, ModelFile postTall, ModelFile side, ModelFile sideAlt, ModelFile sideTall, ModelFile sideTallAlt) {
        builder
                // Smaller post when West & East are both short while North & South being none
                .part().modelFile(postShort).addModel()
                .nestedGroup().condition(WallBlock.UP, false).condition(WallBlock.EAST_WALL, WallSide.LOW).condition(WallBlock.WEST_WALL, WallSide.LOW).end().end()
                // Taller thinner post when West & East are both tall while North & South being none
                .part().modelFile(postTall).addModel()
                .nestedGroup().condition(WallBlock.UP, false).condition(WallBlock.EAST_WALL, WallSide.TALL).condition(WallBlock.WEST_WALL, WallSide.TALL).end().end()
                // Rotated small post when West & East are both none while North & South are short
                .part().modelFile(postShort).rotationY(90).addModel()
                .nestedGroup().condition(WallBlock.UP, false).condition(WallBlock.EAST_WALL, WallSide.NONE).condition(WallBlock.NORTH_WALL, WallSide.LOW).condition(WallBlock.WEST_WALL, WallSide.NONE).condition(WallBlock.SOUTH_WALL, WallSide.LOW).end().end()
                // Rotated small post when West & East are both none while North & South are tall
                .part().modelFile(postTall).rotationY(90).addModel()
                .nestedGroup().condition(WallBlock.UP, false).condition(WallBlock.EAST_WALL, WallSide.NONE).condition(WallBlock.NORTH_WALL, WallSide.TALL).condition(WallBlock.WEST_WALL, WallSide.NONE).condition(WallBlock.SOUTH_WALL, WallSide.TALL).end().end();
        WALL_PROPS.entrySet().stream()
                .filter(e -> e.getKey().getAxis().isHorizontal())
                .forEach(e -> {
                    this.logWallSidePart(builder, side, sideAlt, e, WallSide.LOW, false);
                    this.logWallSidePart(builder, sideTall, sideTallAlt, e, WallSide.TALL, false);
                });
    }

    public void logWallBlockWithPost(MultiPartBlockStateBuilder builder, ModelFile postBig, ModelFile side, ModelFile sideAlt, ModelFile sideTall, ModelFile sideTallAlt) {
        builder
                // Big post for connections, typically including angled
                .part().modelFile(postBig).addModel()
                .condition(WallBlock.UP, true).end();
        WALL_PROPS.entrySet().stream()
                .filter(e -> e.getKey().getAxis().isHorizontal())
                .forEach(e -> {
                    this.logWallSidePart(builder, side, sideAlt, e, WallSide.LOW, true);
                    this.logWallSidePart(builder, sideTall, sideTallAlt, e, WallSide.TALL, true);
                });
    }

    private void logWallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, ModelFile modelAlt, Map.Entry<Direction, Property<WallSide>> entry, WallSide height, boolean hasPost) {
        int rotation = (((int) entry.getKey().toYRot()) + 180) % 360;
        builder.part()
                .modelFile(rotation < 180 ? model : modelAlt)
                .rotationY(rotation)
                .addModel()
                .condition(entry.getValue(), height).condition(WallBlock.UP, hasPost);
    }

    public void stairs(StairBlock block, Block baseBlock, String location) {
        this.stairsBlock(block, this.texture(this.name(baseBlock), location));
    }

    public void translucentSlab(Block block, Block baseBlock, String location) {
        ResourceLocation texture = this.texture(this.name(baseBlock), location);
        this.translucentSlabBlock(block, models().slab(this.name(block), texture, texture, texture).renderType(new ResourceLocation("translucent")),
                this.models().slabTop(this.name(block) + "_top", texture, texture, texture).renderType(new ResourceLocation("translucent")),
                this.models().getExistingFile(this.texture(this.name(baseBlock))));
    }

    public void translucentSlabBlock(Block block, ModelFile bottom, ModelFile top, ModelFile doubleSlab) {
        this.getVariantBuilder(block)
                .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom))
                .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top))
                .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleSlab));
    }

    public void slab(SlabBlock block, Block baseBlock, String location) {
        this.slabBlock(block, this.texture(this.name(baseBlock)), this.texture(this.name(baseBlock), location));
    }

    public void bookshelf(Block block, Block endBlock) {
        ModelFile bookshelf = this.models().cubeColumn(this.name(block), this.texture(this.name(block), "construction/"), this.texture(this.name(endBlock), "construction/"));
        this.getVariantBuilder(block).partialState().addModels(new ConfiguredModel(bookshelf));
    }

    public ModelFile cubeAll(Block block, String location) {
        return this.models().cubeAll(this.name(block), this.texture(this.name(block), location));
    }

    public ModelFile cubeAllTranslucent(Block block, String location) {
        return this.models().cubeAll(this.name(block), this.texture(this.name(block), location)).renderType(new ResourceLocation("translucent"));
    }

    public ModelFile cubeBottomTop(String block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return this.models().cubeBottomTop(block, side, bottom, top);
    }

    public void craftingTable(Block block, Block baseBlock, String location, String modid) {
        ResourceLocation baseTexture = new ResourceLocation(modid, "block/" + location + this.name(baseBlock));
        ModelFile workbench = this.models().cube(this.name(block),
                        baseTexture,
                        this.extend(this.texture(this.name(block), "utility/"), "_top"),
                        this.extend(this.texture(this.name(block), "utility/"), "_front"),
                        this.extend(this.texture(this.name(block), "utility/"), "_side"),
                        this.extend(this.texture(this.name(block), "utility/"), "_front"),
                        this.extend(this.texture(this.name(block), "utility/"), "_side"))
                .texture("particle", this.extend(this.texture(this.name(block), "utility/"), "_front"));
        this.getVariantBuilder(block).partialState().addModels(new ConfiguredModel(workbench));
    }

    public void furnace(Block block) {
        String blockName = this.name(block);
        ResourceLocation side = this.extend(this.texture(this.name(block), "utility/"), "_side");
        ResourceLocation front_on =  this.extend(this.texture(this.name(block), "utility/"), "_front_on");
        ResourceLocation front =  this.extend(this.texture(this.name(block), "utility/"), "_front");
        ResourceLocation top = this.extend(this.texture(this.name(block), "utility/"), "_top");
        ModelFile normal = this.models().orientable(blockName, side, front, top);
        ModelFile lit = this.models().orientable(blockName + "_on", side, front_on, top);
        this.getVariantBuilder(block).forAllStatesExcept((state) -> {
            Direction direction = state.getValue(AbstractFurnaceBlock.FACING);
            if (state.getValue(AbstractFurnaceBlock.LIT)) {
                return this.rotateModel(direction, lit);
            } else {
                return this.rotateModel(direction, normal);
            }
        });
    }

    public ConfiguredModel[] rotateModel(Direction direction, ModelFile model) {
        switch (direction) {
            case NORTH -> {
                return ConfiguredModel.builder().modelFile(model).build();
            }
            case SOUTH -> {
                return ConfiguredModel.builder().modelFile(model).rotationY(180).build();
            }
            case WEST -> {
                return ConfiguredModel.builder().modelFile(model).rotationY(270).build();
            }
            case EAST -> {
                return ConfiguredModel.builder().modelFile(model).rotationY(90).build();
            }
        }
        return ConfiguredModel.builder().build();
    }

    public void ladder(LadderBlock block) {
        ResourceLocation location = this.texture(this.name(block), "construction/");
        ModelFile ladder = models().withExistingParent(this.name(block), this.mcLoc("block/block")).renderType(new ResourceLocation("cutout")).ao(false)
                .texture("particle", location).texture("texture", location)
                .element().from(0.0F, 0.0F, 15.2F).to(16.0F, 16.0F, 15.2F).shade(false)
                .face(Direction.NORTH).uvs(0.0F, 0.0F, 16.0F, 16.0F).texture("#texture").end()
                .face(Direction.SOUTH).uvs(16.0F, 0.0F, 0.0F, 16.0F).texture("#texture").end()
                .end();
        this.getVariantBuilder(block).forAllStatesExcept((state) -> {
            Direction direction = state.getValue(LadderBlock.FACING);
            return ConfiguredModel.builder().modelFile(ladder).rotationY((int) (direction.toYRot() + 180) % 360).build();
        }, LadderBlock.WATERLOGGED);
    }
}
