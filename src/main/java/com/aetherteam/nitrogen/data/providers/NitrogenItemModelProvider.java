package com.aetherteam.nitrogen.data.providers;

//import com.aetherteam.nitrogen.Nitrogen;
//import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
//import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
//import net.minecraft.core.Direction;
//import net.minecraft.data.PackOutput;
//import net.minecraft.resources.ResourceKey;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemDisplayContext;
//import net.minecraft.world.item.armortrim.TrimMaterial;
//import net.minecraft.world.item.armortrim.TrimMaterials;
//import net.minecraft.world.level.block.Block;
//
//import java.util.List;
//
//public abstract class NitrogenItemModelProvider extends FabricModelProvider {
//    protected static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
//
//    public NitrogenItemModelProvider(PackOutput output, String id, ExistingFileHelper helper) {
//        super(output, id, helper);
//    }
//
//    public String blockName(Block block) {
//        ResourceLocation location = ForgeRegistries.BLOCKS.getKey(block);
//        if (location != null) {
//            return location.getPath();
//        } else {
//            throw new IllegalStateException("Unknown block: " + block.toString());
//        }
//    }
//
//    public String itemName(Item item) {
//        ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
//        if (location != null) {
//            return location.getPath();
//        } else {
//            throw new IllegalStateException("Unknown item: " + item.toString());
//        }
//    }
//
//    protected ResourceLocation texture(String name) {
//        return this.modLoc("block/" + name);
//    }
//
//    protected ResourceLocation texture(String name, String location) {
//        return this.modLoc("block/" + location + name);
//    }
//
//    public void item(Item item, String location) {
//        this.withExistingParent(this.itemName(item), mcLoc("item/generated"))
//                .texture("layer0", modLoc("item/" + location + this.itemName(item)));
//    }
//
//    public void handheldItem(Item item, String location) {
//        this.withExistingParent(this.itemName(item), this.mcLoc("item/handheld"))
//                .texture("layer0", this.modLoc("item/" + location + this.itemName(item)));
//    }
//
//    public void bowItem(Item item, String location) {
//        this.withExistingParent(this.itemName(item) + "_pulling_0", this.mcLoc("item/bow")).texture("layer0", this.modLoc("item/" + location + this.itemName(item) + "_pulling_0"));
//        this.withExistingParent(this.itemName(item) + "_pulling_1", this.mcLoc("item/bow")).texture("layer0", this.modLoc("item/" + location + this.itemName(item) + "_pulling_1"));
//        this.withExistingParent(this.itemName(item) + "_pulling_2", this.mcLoc("item/bow")).texture("layer0", this.modLoc("item/" + location + this.itemName(item) + "_pulling_2"));
//        this.withExistingParent(this.itemName(item), this.mcLoc("item/bow"))
//                .texture("layer0", this.modLoc("item/" + location + this.itemName(item)))
//                .override().predicate(new ResourceLocation("pulling"), 1).model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_0"))).end()
//                .override().predicate(new ResourceLocation("pulling"), 1).predicate(new ResourceLocation("pull"), 0.65F).model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_1"))).end()
//                .override().predicate(new ResourceLocation("pulling"), 1).predicate(new ResourceLocation("pull"), 0.9F).model(this.getExistingFile(this.modLoc("item/" + this.itemName(item) + "_pulling_2"))).end();
//    }
//
//    public void helmetItem(Item item, String location) {
//        this.armorItem(item, location, "helmet");
//    }
//
//    public void chestplateItem(Item item, String location) {
//        this.armorItem(item, location, "chestplate");
//    }
//
//    public void leggingsItem(Item item, String location) {
//        this.armorItem(item, location, "leggings");
//    }
//
//    public void bootsItem(Item item, String location) {
//        this.armorItem(item, location, "boots");
//    }
//
//    public void armorItem(Item item, String location, String type) {
//        ItemModelBuilder builder = this.withExistingParent(this.itemName(item), this.mcLoc("item/generated")).texture("layer0", this.modLoc("item/" + location + this.itemName(item)));
//        double index = 0.1;
//        for (ResourceKey<TrimMaterial> trimMaterial : VANILLA_TRIM_MATERIALS) {
//            String material = trimMaterial.location().getPath();
//            String name = this.itemName(item) + "_" + material + "_trim";
//            this.withExistingParent(name, this.mcLoc("item/generated"))
//                    .texture("layer0", this.modLoc("item/" + location + this.itemName(item)))
//                    .texture("layer1", this.mcLoc("trims/items/" + type + "_trim_" + material));
//            builder.override().predicate(new ResourceLocation("trim_type"), (float) index).model(this.getExistingFile(this.modLoc("item/" + name))).end();
//            index += 0.1;
//        }
//    }
//
//    public void dyedItem(Item item, String location) {
//        this.withExistingParent(this.itemName(item), this.mcLoc("item/generated"))
//                .texture("layer0", this.modLoc("item/" + location + this.itemName(item)))
//                .texture("layer1", this.modLoc("item/" + location + this.itemName(item) + "_overlay"));
//    }
//
//    public void eggItem(Item item) {
//        this.withExistingParent(this.itemName(item), this.mcLoc("item/template_spawn_egg"));
//    }
//
//    public void itemBlock(Block block) {
//        this.withExistingParent(this.blockName(block), this.texture(this.blockName(block)));
//    }
//
//    public void itemBlock(Block block, String suffix) {
//        this.withExistingParent(this.blockName(block), this.texture(this.blockName(block) + suffix));
//    }
//
//    public void pane(Block block, Block glass, String location) {
//        this.withExistingParent(this.blockName(block), this.mcLoc("item/generated"))
//                .texture("layer0", this.texture(this.blockName(glass), location))
//                .renderType(new ResourceLocation("translucent"));
//    }
//
//    public void itemBlockFlat(Block block, String location) {
//        this.withExistingParent(this.blockName(block), this.mcLoc("item/generated"))
//                .texture("layer0", this.texture(this.blockName(block), location));
//    }
//
//    public void lookalikeBlock(Block block, ResourceLocation lookalike) {
//        this.withExistingParent(this.blockName(block), lookalike);
//    }
//
//    public void itemFence(Block block, Block baseBlock, String location) {
//        this.withExistingParent(this.blockName(block), this.mcLoc("block/fence_inventory"))
//                .texture("texture", this.texture(this.blockName(baseBlock), location));
//    }
//
//    public void itemButton(Block block, Block baseBlock, String location) {
//        this.withExistingParent(this.blockName(block), this.mcLoc("block/button_inventory"))
//                .texture("texture", this.texture(this.blockName(baseBlock), location));
//    }
//
//    public void itemWallBlock(Block block, Block baseBlock, String location) {
//        this.wallInventory(this.blockName(block), this.texture(this.blockName(baseBlock), location));
//    }
//
//    public void translucentItemWallBlock(Block block, Block baseBlock, String location) {
//        this.singleTexture(this.blockName(block), new ResourceLocation(Nitrogen.MODID, BLOCK_FOLDER + "/template_translucent_wall_inventory"), "wall", this.texture(this.blockName(baseBlock), location));
//    }
//
//    public void itemLogWallBlock(Block block, Block baseBlock, String location, String modid) {
//        ResourceLocation baseTexture = new ResourceLocation(modid, "block/" + location + this.blockName(baseBlock));
//        this.withExistingParent(this.blockName(block), this.mcLoc("block/block"))
//                .transforms()
//                .transform(ItemDisplayContext.GUI).rotation(30.0F, 135.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.625F, 0.625F, 0.625F).end()
//                .transform(ItemDisplayContext.FIXED).rotation(0.0F, 90.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.5F, 0.5F, 0.5F).end()
//                .end()
//                .texture("top", baseTexture + "_top").texture("side", baseTexture)
//                .element().from(4.0F, 0.0F, 4.0F).to(12.0F, 16.0F, 12.0F)
//                .face(Direction.DOWN).uvs(4.0F, 4.0F, 12.0F, 12.0F).texture("#top").cullface(Direction.DOWN).end()
//                .face(Direction.UP).uvs(4.0F, 4.0F, 12.0F, 12.0F).texture("#top").end()
//                .face(Direction.NORTH).uvs(4.0F, 0.0F, 12.0F, 16.0F).texture("#side").end()
//                .face(Direction.SOUTH).uvs(4.0F, 0.0F, 12.0F, 16.0F).texture("#side").end()
//                .face(Direction.WEST).uvs(4.0F, 0.0F, 12.0F, 16.0F).texture("#side").end()
//                .face(Direction.EAST).uvs(4.0F, 0.0F, 12.0F, 16.0F).texture("#side").end().end()
//                .element().from(5.0F, 0.0F, 0.0F).to(11.0F, 13.0F, 16.0F)
//                .face(Direction.DOWN).uvs(5.0F, 0.0F, 11.0F, 16.0F).texture("#top").cullface(Direction.DOWN).end()
//                .face(Direction.UP).uvs(5.0F, 0.0F, 11.0F, 16.0F).texture("#top").end()
//                .face(Direction.NORTH).uvs(5.0F, 3.0F, 11.0F, 16.0F).texture("#side").cullface(Direction.NORTH).end()
//                .face(Direction.SOUTH).uvs(5.0F, 3.0F, 11.0F, 16.0F).texture("#side").cullface(Direction.SOUTH).end()
//                .face(Direction.WEST).uvs(0.0F, 3.0F, 16.0F, 16.0F).texture("#side").end()
//                .face(Direction.EAST).uvs(0.0F, 3.0F, 16.0F, 16.0F).texture("#side").end().end();
//    }
//
//    public void itemWoodWallBlock(Block block, Block baseBlock, String location, String modid) {
//        this.wallInventory(this.blockName(block), new ResourceLocation(modid, "block/" + location + this.blockName(baseBlock)));
//    }
//}
