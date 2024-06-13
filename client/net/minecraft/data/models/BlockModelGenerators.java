package net.minecraft.data.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.SnifferEggBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.block.state.properties.WallSide;

public class BlockModelGenerators {
   final Consumer<BlockStateGenerator> blockStateOutput;
   final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;
   private final Consumer<Item> skippedAutoModelsOutput;
   final List<Block> nonOrientableTrapdoor = ImmutableList.of(Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
   final Map<Block, BlockModelGenerators.BlockStateGeneratorSupplier> fullBlockModelCustomGenerators = ImmutableMap.builder()
      .put(Blocks.STONE, BlockModelGenerators::createMirroredCubeGenerator)
      .put(Blocks.DEEPSLATE, BlockModelGenerators::createMirroredColumnGenerator)
      .put(Blocks.MUD_BRICKS, BlockModelGenerators::createNorthWestMirroredCubeGenerator)
      .build();
   final Map<Block, TexturedModel> texturedModels = ImmutableMap.builder()
      .put(Blocks.SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.SANDSTONE))
      .put(Blocks.RED_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.RED_SANDSTONE))
      .put(Blocks.SMOOTH_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top")))
      .put(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top")))
      .put(
         Blocks.CUT_SANDSTONE,
         TexturedModel.COLUMN.get(Blocks.SANDSTONE).updateTextures(var0 -> var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_SANDSTONE)))
      )
      .put(
         Blocks.CUT_RED_SANDSTONE,
         TexturedModel.COLUMN
            .get(Blocks.RED_SANDSTONE)
            .updateTextures(var0 -> var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_RED_SANDSTONE)))
      )
      .put(Blocks.QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.QUARTZ_BLOCK))
      .put(Blocks.SMOOTH_QUARTZ, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom")))
      .put(Blocks.BLACKSTONE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.BLACKSTONE))
      .put(Blocks.DEEPSLATE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.DEEPSLATE))
      .put(
         Blocks.CHISELED_QUARTZ_BLOCK,
         TexturedModel.COLUMN
            .get(Blocks.CHISELED_QUARTZ_BLOCK)
            .updateTextures(var0 -> var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK)))
      )
      .put(Blocks.CHISELED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_SANDSTONE).updateTextures(var0 -> {
         var0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"));
         var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_SANDSTONE));
      }))
      .put(Blocks.CHISELED_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).updateTextures(var0 -> {
         var0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"));
         var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_RED_SANDSTONE));
      }))
      .put(Blocks.CHISELED_TUFF_BRICKS, TexturedModel.COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF_BRICKS))
      .put(Blocks.CHISELED_TUFF, TexturedModel.COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF))
      .build();
   static final Map<BlockFamily.Variant, BiConsumer<BlockModelGenerators.BlockFamilyProvider, Block>> SHAPE_CONSUMERS = ImmutableMap.builder()
      .put(BlockFamily.Variant.BUTTON, BlockModelGenerators.BlockFamilyProvider::button)
      .put(BlockFamily.Variant.DOOR, BlockModelGenerators.BlockFamilyProvider::door)
      .put(BlockFamily.Variant.CHISELED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant)
      .put(BlockFamily.Variant.CRACKED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant)
      .put(BlockFamily.Variant.CUSTOM_FENCE, BlockModelGenerators.BlockFamilyProvider::customFence)
      .put(BlockFamily.Variant.FENCE, BlockModelGenerators.BlockFamilyProvider::fence)
      .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockModelGenerators.BlockFamilyProvider::customFenceGate)
      .put(BlockFamily.Variant.FENCE_GATE, BlockModelGenerators.BlockFamilyProvider::fenceGate)
      .put(BlockFamily.Variant.SIGN, BlockModelGenerators.BlockFamilyProvider::sign)
      .put(BlockFamily.Variant.SLAB, BlockModelGenerators.BlockFamilyProvider::slab)
      .put(BlockFamily.Variant.STAIRS, BlockModelGenerators.BlockFamilyProvider::stairs)
      .put(BlockFamily.Variant.PRESSURE_PLATE, BlockModelGenerators.BlockFamilyProvider::pressurePlate)
      .put(BlockFamily.Variant.TRAPDOOR, BlockModelGenerators.BlockFamilyProvider::trapdoor)
      .put(BlockFamily.Variant.WALL, BlockModelGenerators.BlockFamilyProvider::wall)
      .build();
   public static final List<Pair<BooleanProperty, Function<ResourceLocation, Variant>>> MULTIFACE_GENERATOR = List.of(
      Pair.of(BlockStateProperties.NORTH, (Function<ResourceLocation, Variant>)var0 -> Variant.variant().with(VariantProperties.MODEL, var0)),
      Pair.of(
         BlockStateProperties.EAST,
         (Function<ResourceLocation, Variant>)var0 -> Variant.variant()
               .with(VariantProperties.MODEL, var0)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               .with(VariantProperties.UV_LOCK, true)
      ),
      Pair.of(
         BlockStateProperties.SOUTH,
         (Function<ResourceLocation, Variant>)var0 -> Variant.variant()
               .with(VariantProperties.MODEL, var0)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               .with(VariantProperties.UV_LOCK, true)
      ),
      Pair.of(
         BlockStateProperties.WEST,
         (Function<ResourceLocation, Variant>)var0 -> Variant.variant()
               .with(VariantProperties.MODEL, var0)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               .with(VariantProperties.UV_LOCK, true)
      ),
      Pair.of(
         BlockStateProperties.UP,
         (Function<ResourceLocation, Variant>)var0 -> Variant.variant()
               .with(VariantProperties.MODEL, var0)
               .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
               .with(VariantProperties.UV_LOCK, true)
      ),
      Pair.of(
         BlockStateProperties.DOWN,
         (Function<ResourceLocation, Variant>)var0 -> Variant.variant()
               .with(VariantProperties.MODEL, var0)
               .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
               .with(VariantProperties.UV_LOCK, true)
      )
   );
   private static final Map<BlockModelGenerators.BookSlotModelCacheKey, ResourceLocation> CHISELED_BOOKSHELF_SLOT_MODEL_CACHE = new HashMap<>();

   private static BlockStateGenerator createMirroredCubeGenerator(
      Block var0, ResourceLocation var1, TextureMapping var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3
   ) {
      ResourceLocation var4 = ModelTemplates.CUBE_MIRRORED_ALL.create(var0, var2, var3);
      return createRotatedVariant(var0, var1, var4);
   }

   private static BlockStateGenerator createNorthWestMirroredCubeGenerator(
      Block var0, ResourceLocation var1, TextureMapping var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3
   ) {
      ResourceLocation var4 = ModelTemplates.CUBE_NORTH_WEST_MIRRORED_ALL.create(var0, var2, var3);
      return createSimpleBlock(var0, var4);
   }

   private static BlockStateGenerator createMirroredColumnGenerator(
      Block var0, ResourceLocation var1, TextureMapping var2, BiConsumer<ResourceLocation, Supplier<JsonElement>> var3
   ) {
      ResourceLocation var4 = ModelTemplates.CUBE_COLUMN_MIRRORED.create(var0, var2, var3);
      return createRotatedVariant(var0, var1, var4).with(createRotatedPillar());
   }

   public BlockModelGenerators(Consumer<BlockStateGenerator> var1, BiConsumer<ResourceLocation, Supplier<JsonElement>> var2, Consumer<Item> var3) {
      super();
      this.blockStateOutput = var1;
      this.modelOutput = var2;
      this.skippedAutoModelsOutput = var3;
   }

   void skipAutoItemBlock(Block var1) {
      this.skippedAutoModelsOutput.accept(var1.asItem());
   }

   void delegateItemModel(Block var1, ResourceLocation var2) {
      this.modelOutput.accept(ModelLocationUtils.getModelLocation(var1.asItem()), new DelegatedModel(var2));
   }

   private void delegateItemModel(Item var1, ResourceLocation var2) {
      this.modelOutput.accept(ModelLocationUtils.getModelLocation(var1), new DelegatedModel(var2));
   }

   void createSimpleFlatItemModel(Item var1) {
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layer0(var1), this.modelOutput);
   }

   private void createSimpleFlatItemModel(Block var1) {
      Item var2 = var1.asItem();
      if (var2 != Items.AIR) {
         ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(var2), TextureMapping.layer0(var1), this.modelOutput);
      }
   }

   private void createSimpleFlatItemModel(Block var1, String var2) {
      Item var3 = var1.asItem();
      ModelTemplates.FLAT_ITEM
         .create(ModelLocationUtils.getModelLocation(var3), TextureMapping.layer0(TextureMapping.getBlockTexture(var1, var2)), this.modelOutput);
   }

   private static PropertyDispatch createHorizontalFacingDispatch() {
      return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
         .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
         .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
         .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
         .select(Direction.NORTH, Variant.variant());
   }

   private static PropertyDispatch createHorizontalFacingDispatchAlt() {
      return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
         .select(Direction.SOUTH, Variant.variant())
         .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
         .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
         .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
   }

   private static PropertyDispatch createTorchHorizontalDispatch() {
      return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
         .select(Direction.EAST, Variant.variant())
         .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
         .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
         .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
   }

   private static PropertyDispatch createFacingDispatch() {
      return PropertyDispatch.property(BlockStateProperties.FACING)
         .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
         .select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
         .select(Direction.NORTH, Variant.variant())
         .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
         .select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
         .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
   }

   private static MultiVariantGenerator createRotatedVariant(Block var0, ResourceLocation var1) {
      return MultiVariantGenerator.multiVariant(var0, createRotatedVariants(var1));
   }

   private static Variant[] createRotatedVariants(ResourceLocation var0) {
      return new Variant[]{
         Variant.variant().with(VariantProperties.MODEL, var0),
         Variant.variant().with(VariantProperties.MODEL, var0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90),
         Variant.variant().with(VariantProperties.MODEL, var0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180),
         Variant.variant().with(VariantProperties.MODEL, var0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
      };
   }

   private static MultiVariantGenerator createRotatedVariant(Block var0, ResourceLocation var1, ResourceLocation var2) {
      return MultiVariantGenerator.multiVariant(
         var0,
         Variant.variant().with(VariantProperties.MODEL, var1),
         Variant.variant().with(VariantProperties.MODEL, var2),
         Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180),
         Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
      );
   }

   private static PropertyDispatch createBooleanModelDispatch(BooleanProperty var0, ResourceLocation var1, ResourceLocation var2) {
      return PropertyDispatch.property(var0)
         .select(true, Variant.variant().with(VariantProperties.MODEL, var1))
         .select(false, Variant.variant().with(VariantProperties.MODEL, var2));
   }

   private void createRotatedMirroredVariantBlock(Block var1) {
      ResourceLocation var2 = TexturedModel.CUBE.create(var1, this.modelOutput);
      ResourceLocation var3 = TexturedModel.CUBE_MIRRORED.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createRotatedVariant(var1, var2, var3));
   }

   private void createRotatedVariantBlock(Block var1) {
      ResourceLocation var2 = TexturedModel.CUBE.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createRotatedVariant(var1, var2));
   }

   private void createBrushableBlock(Block var1) {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(
                  PropertyDispatch.property(BlockStateProperties.DUSTED)
                     .generate(
                        var2 -> {
                           String var3 = "_" + var2;
                           ResourceLocation var4 = TextureMapping.getBlockTexture(var1, var3);
                           return Variant.variant()
                              .with(
                                 VariantProperties.MODEL,
                                 ModelTemplates.CUBE_ALL.createWithSuffix(var1, var3, new TextureMapping().put(TextureSlot.ALL, var4), this.modelOutput)
                              );
                        }
                     )
               )
         );
      this.delegateItemModel(var1, TextureMapping.getBlockTexture(var1, "_0"));
   }

   static BlockStateGenerator createButton(Block var0, ResourceLocation var1, ResourceLocation var2) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            PropertyDispatch.property(BlockStateProperties.POWERED)
               .select(false, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(true, Variant.variant().with(VariantProperties.MODEL, var2))
         )
         .with(
            PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
               .select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
               .select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
               .select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
               .select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
               .select(
                  AttachFace.WALL,
                  Direction.EAST,
                  Variant.variant()
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  AttachFace.WALL,
                  Direction.WEST,
                  Variant.variant()
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  AttachFace.WALL,
                  Direction.SOUTH,
                  Variant.variant()
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  AttachFace.WALL,
                  Direction.NORTH,
                  Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  AttachFace.CEILING,
                  Direction.EAST,
                  Variant.variant()
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  AttachFace.CEILING,
                  Direction.WEST,
                  Variant.variant()
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
               )
               .select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
               .select(
                  AttachFace.CEILING,
                  Direction.NORTH,
                  Variant.variant()
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
               )
         );
   }

   private static PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(
      PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> var0,
      DoubleBlockHalf var1,
      ResourceLocation var2,
      ResourceLocation var3,
      ResourceLocation var4,
      ResourceLocation var5
   ) {
      return var0.select(Direction.EAST, var1, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, var2))
         .select(
            Direction.SOUTH,
            var1,
            DoorHingeSide.LEFT,
            false,
            Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
         )
         .select(
            Direction.WEST,
            var1,
            DoorHingeSide.LEFT,
            false,
            Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
         )
         .select(
            Direction.NORTH,
            var1,
            DoorHingeSide.LEFT,
            false,
            Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
         )
         .select(Direction.EAST, var1, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, var4))
         .select(
            Direction.SOUTH,
            var1,
            DoorHingeSide.RIGHT,
            false,
            Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
         )
         .select(
            Direction.WEST,
            var1,
            DoorHingeSide.RIGHT,
            false,
            Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
         )
         .select(
            Direction.NORTH,
            var1,
            DoorHingeSide.RIGHT,
            false,
            Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
         )
         .select(
            Direction.EAST,
            var1,
            DoorHingeSide.LEFT,
            true,
            Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
         )
         .select(
            Direction.SOUTH,
            var1,
            DoorHingeSide.LEFT,
            true,
            Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
         )
         .select(
            Direction.WEST,
            var1,
            DoorHingeSide.LEFT,
            true,
            Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
         )
         .select(Direction.NORTH, var1, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, var3))
         .select(
            Direction.EAST,
            var1,
            DoorHingeSide.RIGHT,
            true,
            Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
         )
         .select(Direction.SOUTH, var1, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, var5))
         .select(
            Direction.WEST,
            var1,
            DoorHingeSide.RIGHT,
            true,
            Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
         )
         .select(
            Direction.NORTH,
            var1,
            DoorHingeSide.RIGHT,
            true,
            Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
         );
   }

   private static BlockStateGenerator createDoor(
      Block var0,
      ResourceLocation var1,
      ResourceLocation var2,
      ResourceLocation var3,
      ResourceLocation var4,
      ResourceLocation var5,
      ResourceLocation var6,
      ResourceLocation var7,
      ResourceLocation var8
   ) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            configureDoorHalf(
               configureDoorHalf(
                  PropertyDispatch.properties(
                     BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN
                  ),
                  DoubleBlockHalf.LOWER,
                  var1,
                  var2,
                  var3,
                  var4
               ),
               DoubleBlockHalf.UPPER,
               var5,
               var6,
               var7,
               var8
            )
         );
   }

   static BlockStateGenerator createCustomFence(
      Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4, ResourceLocation var5
   ) {
      return MultiPartGenerator.multiPart(var0)
         .with(Variant.variant().with(VariantProperties.MODEL, var1))
         .with(
            Condition.condition().term(BlockStateProperties.NORTH, true),
            Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.UV_LOCK, false)
         )
         .with(
            Condition.condition().term(BlockStateProperties.EAST, true),
            Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.UV_LOCK, false)
         )
         .with(
            Condition.condition().term(BlockStateProperties.SOUTH, true),
            Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.UV_LOCK, false)
         )
         .with(
            Condition.condition().term(BlockStateProperties.WEST, true),
            Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.UV_LOCK, false)
         );
   }

   static BlockStateGenerator createFence(Block var0, ResourceLocation var1, ResourceLocation var2) {
      return MultiPartGenerator.multiPart(var0)
         .with(Variant.variant().with(VariantProperties.MODEL, var1))
         .with(
            Condition.condition().term(BlockStateProperties.NORTH, true),
            Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.EAST, true),
            Variant.variant()
               .with(VariantProperties.MODEL, var2)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.SOUTH, true),
            Variant.variant()
               .with(VariantProperties.MODEL, var2)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.WEST, true),
            Variant.variant()
               .with(VariantProperties.MODEL, var2)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               .with(VariantProperties.UV_LOCK, true)
         );
   }

   static BlockStateGenerator createWall(Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      return MultiPartGenerator.multiPart(var0)
         .with(Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, var1))
         .with(
            Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW),
            Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW),
            Variant.variant()
               .with(VariantProperties.MODEL, var2)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW),
            Variant.variant()
               .with(VariantProperties.MODEL, var2)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW),
            Variant.variant()
               .with(VariantProperties.MODEL, var2)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL),
            Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL),
            Variant.variant()
               .with(VariantProperties.MODEL, var3)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL),
            Variant.variant()
               .with(VariantProperties.MODEL, var3)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               .with(VariantProperties.UV_LOCK, true)
         )
         .with(
            Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL),
            Variant.variant()
               .with(VariantProperties.MODEL, var3)
               .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               .with(VariantProperties.UV_LOCK, true)
         );
   }

   static BlockStateGenerator createFenceGate(
      Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4, boolean var5
   ) {
      return MultiVariantGenerator.multiVariant(var0, Variant.variant().with(VariantProperties.UV_LOCK, var5))
         .with(createHorizontalFacingDispatchAlt())
         .with(
            PropertyDispatch.properties(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN)
               .select(false, false, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(true, false, Variant.variant().with(VariantProperties.MODEL, var4))
               .select(false, true, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(true, true, Variant.variant().with(VariantProperties.MODEL, var3))
         );
   }

   static BlockStateGenerator createStairs(Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE)
               .select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.BOTTOM,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.BOTTOM,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, var3))
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.BOTTOM,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.BOTTOM,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.BOTTOM,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, var3))
               .select(
                  Direction.NORTH,
                  Half.BOTTOM,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.BOTTOM,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.BOTTOM,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.BOTTOM,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(
                  Direction.NORTH,
                  Half.BOTTOM,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.TOP,
                  StairsShape.STRAIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.TOP,
                  StairsShape.OUTER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.TOP,
                  StairsShape.OUTER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.TOP,
                  StairsShape.INNER_RIGHT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .select(
                  Direction.NORTH,
                  Half.TOP,
                  StairsShape.INNER_LEFT,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
         );
   }

   private static BlockStateGenerator createOrientableTrapdoor(Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN)
               .select(Direction.NORTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(
                  Direction.SOUTH,
                  Half.BOTTOM,
                  false,
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  Direction.EAST,
                  Half.BOTTOM,
                  false,
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  false,
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .select(Direction.NORTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  false,
                  Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  false,
                  Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  false,
                  Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .select(Direction.NORTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, var3))
               .select(
                  Direction.SOUTH,
                  Half.BOTTOM,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  Direction.EAST,
                  Half.BOTTOM,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .select(
                  Direction.NORTH,
                  Half.TOP,
                  true,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  true,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  true,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  true,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
         );
   }

   private static BlockStateGenerator createTrapdoor(Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN)
               .select(Direction.NORTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(Direction.EAST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(Direction.WEST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(Direction.NORTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(Direction.SOUTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(Direction.EAST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(Direction.WEST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(Direction.NORTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, var3))
               .select(
                  Direction.SOUTH,
                  Half.BOTTOM,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  Direction.EAST,
                  Half.BOTTOM,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .select(
                  Direction.WEST,
                  Half.BOTTOM,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .select(Direction.NORTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, var3))
               .select(
                  Direction.SOUTH,
                  Half.TOP,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .select(
                  Direction.EAST,
                  Half.TOP,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .select(
                  Direction.WEST,
                  Half.TOP,
                  true,
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
         );
   }

   static MultiVariantGenerator createSimpleBlock(Block var0, ResourceLocation var1) {
      return MultiVariantGenerator.multiVariant(var0, Variant.variant().with(VariantProperties.MODEL, var1));
   }

   private static PropertyDispatch createRotatedPillar() {
      return PropertyDispatch.property(BlockStateProperties.AXIS)
         .select(Direction.Axis.Y, Variant.variant())
         .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
         .select(
            Direction.Axis.X,
            Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
         );
   }

   static BlockStateGenerator createPillarBlockUVLocked(Block var0, TextureMapping var1, BiConsumer<ResourceLocation, Supplier<JsonElement>> var2) {
      ResourceLocation var3 = ModelTemplates.CUBE_COLUMN_UV_LOCKED_X.create(var0, var1, var2);
      ResourceLocation var4 = ModelTemplates.CUBE_COLUMN_UV_LOCKED_Y.create(var0, var1, var2);
      ResourceLocation var5 = ModelTemplates.CUBE_COLUMN_UV_LOCKED_Z.create(var0, var1, var2);
      ResourceLocation var6 = ModelTemplates.CUBE_COLUMN.create(var0, var1, var2);
      return MultiVariantGenerator.multiVariant(var0, Variant.variant().with(VariantProperties.MODEL, var6))
         .with(
            PropertyDispatch.property(BlockStateProperties.AXIS)
               .select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, var3))
               .select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, var4))
               .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, var5))
         );
   }

   static BlockStateGenerator createAxisAlignedPillarBlock(Block var0, ResourceLocation var1) {
      return MultiVariantGenerator.multiVariant(var0, Variant.variant().with(VariantProperties.MODEL, var1)).with(createRotatedPillar());
   }

   private void createAxisAlignedPillarBlockCustomModel(Block var1, ResourceLocation var2) {
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(var1, var2));
   }

   public void createAxisAlignedPillarBlock(Block var1, TexturedModel.Provider var2) {
      ResourceLocation var3 = var2.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(var1, var3));
   }

   private void createHorizontallyRotatedBlock(Block var1, TexturedModel.Provider var2) {
      ResourceLocation var3 = var2.create(var1, this.modelOutput);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(var1, Variant.variant().with(VariantProperties.MODEL, var3)).with(createHorizontalFacingDispatch()));
   }

   static BlockStateGenerator createRotatedPillarWithHorizontalVariant(Block var0, ResourceLocation var1, ResourceLocation var2) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            PropertyDispatch.property(BlockStateProperties.AXIS)
               .select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
               .select(
                  Direction.Axis.X,
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
         );
   }

   private void createRotatedPillarWithHorizontalVariant(Block var1, TexturedModel.Provider var2, TexturedModel.Provider var3) {
      ResourceLocation var4 = var2.create(var1, this.modelOutput);
      ResourceLocation var5 = var3.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createRotatedPillarWithHorizontalVariant(var1, var4, var5));
   }

   private ResourceLocation createSuffixedVariant(Block var1, String var2, ModelTemplate var3, Function<ResourceLocation, TextureMapping> var4) {
      return var3.createWithSuffix(var1, var2, (TextureMapping)var4.apply(TextureMapping.getBlockTexture(var1, var2)), this.modelOutput);
   }

   static BlockStateGenerator createPressurePlate(Block var0, ResourceLocation var1, ResourceLocation var2) {
      return MultiVariantGenerator.multiVariant(var0).with(createBooleanModelDispatch(BlockStateProperties.POWERED, var2, var1));
   }

   static BlockStateGenerator createSlab(Block var0, ResourceLocation var1, ResourceLocation var2, ResourceLocation var3) {
      return MultiVariantGenerator.multiVariant(var0)
         .with(
            PropertyDispatch.property(BlockStateProperties.SLAB_TYPE)
               .select(SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, var1))
               .select(SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, var2))
               .select(SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, var3))
         );
   }

   public void createTrivialCube(Block var1) {
      this.createTrivialBlock(var1, TexturedModel.CUBE);
   }

   public void createTrivialBlock(Block var1, TexturedModel.Provider var2) {
      this.blockStateOutput.accept(createSimpleBlock(var1, var2.create(var1, this.modelOutput)));
   }

   private void createTrivialBlock(Block var1, TextureMapping var2, ModelTemplate var3) {
      ResourceLocation var4 = var3.create(var1, var2, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
   }

   private BlockModelGenerators.BlockFamilyProvider family(Block var1) {
      TexturedModel var2 = this.texturedModels.getOrDefault(var1, TexturedModel.CUBE.get(var1));
      return new BlockModelGenerators.BlockFamilyProvider(var2.getMapping()).fullBlock(var1, var2.getTemplate());
   }

   public void createHangingSign(Block var1, Block var2, Block var3) {
      TextureMapping var4 = TextureMapping.particle(var1);
      ResourceLocation var5 = ModelTemplates.PARTICLE_ONLY.create(var2, var4, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var2, var5));
      this.blockStateOutput.accept(createSimpleBlock(var3, var5));
      this.createSimpleFlatItemModel(var2.asItem());
      this.skipAutoItemBlock(var3);
   }

   void createDoor(Block var1) {
      TextureMapping var2 = TextureMapping.door(var1);
      ResourceLocation var3 = ModelTemplates.DOOR_BOTTOM_LEFT.create(var1, var2, this.modelOutput);
      ResourceLocation var4 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(var1, var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.DOOR_BOTTOM_RIGHT.create(var1, var2, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(var1, var2, this.modelOutput);
      ResourceLocation var7 = ModelTemplates.DOOR_TOP_LEFT.create(var1, var2, this.modelOutput);
      ResourceLocation var8 = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(var1, var2, this.modelOutput);
      ResourceLocation var9 = ModelTemplates.DOOR_TOP_RIGHT.create(var1, var2, this.modelOutput);
      ResourceLocation var10 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(var1, var2, this.modelOutput);
      this.createSimpleFlatItemModel(var1.asItem());
      this.blockStateOutput.accept(createDoor(var1, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   private void copyDoorModel(Block var1, Block var2) {
      ResourceLocation var3 = ModelTemplates.DOOR_BOTTOM_LEFT.getDefaultModelLocation(var1);
      ResourceLocation var4 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.getDefaultModelLocation(var1);
      ResourceLocation var5 = ModelTemplates.DOOR_BOTTOM_RIGHT.getDefaultModelLocation(var1);
      ResourceLocation var6 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.getDefaultModelLocation(var1);
      ResourceLocation var7 = ModelTemplates.DOOR_TOP_LEFT.getDefaultModelLocation(var1);
      ResourceLocation var8 = ModelTemplates.DOOR_TOP_LEFT_OPEN.getDefaultModelLocation(var1);
      ResourceLocation var9 = ModelTemplates.DOOR_TOP_RIGHT.getDefaultModelLocation(var1);
      ResourceLocation var10 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.getDefaultModelLocation(var1);
      this.delegateItemModel(var2, ModelLocationUtils.getModelLocation(var1.asItem()));
      this.blockStateOutput.accept(createDoor(var2, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   void createOrientableTrapdoor(Block var1) {
      TextureMapping var2 = TextureMapping.defaultTexture(var1);
      ResourceLocation var3 = ModelTemplates.ORIENTABLE_TRAPDOOR_TOP.create(var1, var2, this.modelOutput);
      ResourceLocation var4 = ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM.create(var1, var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN.create(var1, var2, this.modelOutput);
      this.blockStateOutput.accept(createOrientableTrapdoor(var1, var3, var4, var5));
      this.delegateItemModel(var1, var4);
   }

   void createTrapdoor(Block var1) {
      TextureMapping var2 = TextureMapping.defaultTexture(var1);
      ResourceLocation var3 = ModelTemplates.TRAPDOOR_TOP.create(var1, var2, this.modelOutput);
      ResourceLocation var4 = ModelTemplates.TRAPDOOR_BOTTOM.create(var1, var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.TRAPDOOR_OPEN.create(var1, var2, this.modelOutput);
      this.blockStateOutput.accept(createTrapdoor(var1, var3, var4, var5));
      this.delegateItemModel(var1, var4);
   }

   private void copyTrapdoorModel(Block var1, Block var2) {
      ResourceLocation var3 = ModelTemplates.TRAPDOOR_TOP.getDefaultModelLocation(var1);
      ResourceLocation var4 = ModelTemplates.TRAPDOOR_BOTTOM.getDefaultModelLocation(var1);
      ResourceLocation var5 = ModelTemplates.TRAPDOOR_OPEN.getDefaultModelLocation(var1);
      this.delegateItemModel(var2, ModelLocationUtils.getModelLocation(var1.asItem()));
      this.blockStateOutput.accept(createTrapdoor(var2, var3, var4, var5));
   }

   private void createBigDripLeafBlock() {
      this.skipAutoItemBlock(Blocks.BIG_DRIPLEAF);
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_partial_tilt");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_full_tilt");
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.BIG_DRIPLEAF)
               .with(createHorizontalFacingDispatch())
               .with(
                  PropertyDispatch.property(BlockStateProperties.TILT)
                     .select(Tilt.NONE, Variant.variant().with(VariantProperties.MODEL, var1))
                     .select(Tilt.UNSTABLE, Variant.variant().with(VariantProperties.MODEL, var1))
                     .select(Tilt.PARTIAL, Variant.variant().with(VariantProperties.MODEL, var2))
                     .select(Tilt.FULL, Variant.variant().with(VariantProperties.MODEL, var3))
               )
         );
   }

   private BlockModelGenerators.WoodProvider woodProvider(Block var1) {
      return new BlockModelGenerators.WoodProvider(TextureMapping.logColumn(var1));
   }

   private void createNonTemplateModelBlock(Block var1) {
      this.createNonTemplateModelBlock(var1, var1);
   }

   private void createNonTemplateModelBlock(Block var1, Block var2) {
      this.blockStateOutput.accept(createSimpleBlock(var1, ModelLocationUtils.getModelLocation(var2)));
   }

   private void createCrossBlockWithDefaultItem(Block var1, BlockModelGenerators.TintState var2) {
      this.createSimpleFlatItemModel(var1);
      this.createCrossBlock(var1, var2);
   }

   private void createCrossBlockWithDefaultItem(Block var1, BlockModelGenerators.TintState var2, TextureMapping var3) {
      this.createSimpleFlatItemModel(var1);
      this.createCrossBlock(var1, var2, var3);
   }

   private void createCrossBlock(Block var1, BlockModelGenerators.TintState var2) {
      TextureMapping var3 = TextureMapping.cross(var1);
      this.createCrossBlock(var1, var2, var3);
   }

   private void createCrossBlock(Block var1, BlockModelGenerators.TintState var2, TextureMapping var3) {
      ResourceLocation var4 = var2.getCross().create(var1, var3, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
   }

   private void createCrossBlock(Block var1, BlockModelGenerators.TintState var2, Property<Integer> var3, int... var4) {
      if (var3.getPossibleValues().size() != var4.length) {
         throw new IllegalArgumentException("missing values for property: " + var3);
      } else {
         PropertyDispatch var5 = PropertyDispatch.<Integer>property(var3).generate(var4x -> {
            String var5x = "_stage" + var4[var4x];
            TextureMapping var6 = TextureMapping.cross(TextureMapping.getBlockTexture(var1, var5x));
            ResourceLocation var7 = var2.getCross().createWithSuffix(var1, var5x, var6, this.modelOutput);
            return Variant.variant().with(VariantProperties.MODEL, var7);
         });
         this.createSimpleFlatItemModel(var1.asItem());
         this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var1).with(var5));
      }
   }

   private void createPlant(Block var1, Block var2, BlockModelGenerators.TintState var3) {
      this.createCrossBlockWithDefaultItem(var1, var3);
      TextureMapping var4 = TextureMapping.plant(var1);
      ResourceLocation var5 = var3.getCrossPot().create(var2, var4, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var2, var5));
   }

   private void createCoralFans(Block var1, Block var2) {
      TexturedModel var3 = TexturedModel.CORAL_FAN.get(var1);
      ResourceLocation var4 = var3.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
      ResourceLocation var5 = ModelTemplates.CORAL_WALL_FAN.create(var2, var3.getMapping(), this.modelOutput);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(var2, Variant.variant().with(VariantProperties.MODEL, var5)).with(createHorizontalFacingDispatch()));
      this.createSimpleFlatItemModel(var1);
   }

   private void createStems(Block var1, Block var2) {
      this.createSimpleFlatItemModel(var1.asItem());
      TextureMapping var3 = TextureMapping.stem(var1);
      TextureMapping var4 = TextureMapping.attachedStem(var1, var2);
      ResourceLocation var5 = ModelTemplates.ATTACHED_STEM.create(var2, var4, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var2, Variant.variant().with(VariantProperties.MODEL, var5))
               .with(
                  PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
                     .select(Direction.WEST, Variant.variant())
                     .select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                     .select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                     .select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
               )
         );
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(
                  PropertyDispatch.property(BlockStateProperties.AGE_7)
                     .generate(var3x -> Variant.variant().with(VariantProperties.MODEL, ModelTemplates.STEMS[var3x].create(var1, var3, this.modelOutput)))
               )
         );
   }

   private void createPitcherPlant() {
      Block var1 = Blocks.PITCHER_PLANT;
      this.createSimpleFlatItemModel(var1.asItem());
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(var1, "_top");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1, "_bottom");
      this.createDoubleBlock(var1, var2, var3);
   }

   private void createPitcherCrop() {
      Block var1 = Blocks.PITCHER_CROP;
      this.createSimpleFlatItemModel(var1.asItem());
      PropertyDispatch var2 = PropertyDispatch.properties(PitcherCropBlock.AGE, BlockStateProperties.DOUBLE_BLOCK_HALF).generate((var1x, var2x) -> {
         return switch (var2x) {
            case UPPER -> Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(var1, "_top_stage_" + var1x));
            case LOWER -> Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(var1, "_bottom_stage_" + var1x));
         };
      });
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var1).with(var2));
   }

   private void createCoral(Block var1, Block var2, Block var3, Block var4, Block var5, Block var6, Block var7, Block var8) {
      this.createCrossBlockWithDefaultItem(var1, BlockModelGenerators.TintState.NOT_TINTED);
      this.createCrossBlockWithDefaultItem(var2, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialCube(var3);
      this.createTrivialCube(var4);
      this.createCoralFans(var5, var7);
      this.createCoralFans(var6, var8);
   }

   private void createDoublePlant(Block var1, BlockModelGenerators.TintState var2) {
      this.createSimpleFlatItemModel(var1, "_top");
      ResourceLocation var3 = this.createSuffixedVariant(var1, "_top", var2.getCross(), TextureMapping::cross);
      ResourceLocation var4 = this.createSuffixedVariant(var1, "_bottom", var2.getCross(), TextureMapping::cross);
      this.createDoubleBlock(var1, var3, var4);
   }

   private void createSunflower() {
      this.createSimpleFlatItemModel(Blocks.SUNFLOWER, "_front");
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.SUNFLOWER, "_top");
      ResourceLocation var2 = this.createSuffixedVariant(
         Blocks.SUNFLOWER, "_bottom", BlockModelGenerators.TintState.NOT_TINTED.getCross(), TextureMapping::cross
      );
      this.createDoubleBlock(Blocks.SUNFLOWER, var1, var2);
   }

   private void createTallSeagrass() {
      ResourceLocation var1 = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_top", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture);
      ResourceLocation var2 = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_bottom", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture);
      this.createDoubleBlock(Blocks.TALL_SEAGRASS, var1, var2);
   }

   private void createSmallDripleaf() {
      this.skipAutoItemBlock(Blocks.SMALL_DRIPLEAF);
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_top");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_bottom");
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SMALL_DRIPLEAF)
               .with(createHorizontalFacingDispatch())
               .with(
                  PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF)
                     .select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, var2))
                     .select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, var1))
               )
         );
   }

   private void createDoubleBlock(Block var1, ResourceLocation var2, ResourceLocation var3) {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(
                  PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF)
                     .select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, var3))
                     .select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, var2))
               )
         );
   }

   private void createPassiveRail(Block var1) {
      TextureMapping var2 = TextureMapping.rail(var1);
      TextureMapping var3 = TextureMapping.rail(TextureMapping.getBlockTexture(var1, "_corner"));
      ResourceLocation var4 = ModelTemplates.RAIL_FLAT.create(var1, var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.RAIL_CURVED.create(var1, var3, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.RAIL_RAISED_NE.create(var1, var2, this.modelOutput);
      ResourceLocation var7 = ModelTemplates.RAIL_RAISED_SW.create(var1, var2, this.modelOutput);
      this.createSimpleFlatItemModel(var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(
                  PropertyDispatch.property(BlockStateProperties.RAIL_SHAPE)
                     .select(RailShape.NORTH_SOUTH, Variant.variant().with(VariantProperties.MODEL, var4))
                     .select(
                        RailShape.EAST_WEST,
                        Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        RailShape.ASCENDING_EAST,
                        Variant.variant().with(VariantProperties.MODEL, var6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        RailShape.ASCENDING_WEST,
                        Variant.variant().with(VariantProperties.MODEL, var7).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(RailShape.ASCENDING_NORTH, Variant.variant().with(VariantProperties.MODEL, var6))
                     .select(RailShape.ASCENDING_SOUTH, Variant.variant().with(VariantProperties.MODEL, var7))
                     .select(RailShape.SOUTH_EAST, Variant.variant().with(VariantProperties.MODEL, var5))
                     .select(
                        RailShape.SOUTH_WEST,
                        Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        RailShape.NORTH_WEST,
                        Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        RailShape.NORTH_EAST,
                        Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
               )
         );
   }

   private void createActiveRail(Block var1) {
      ResourceLocation var2 = this.createSuffixedVariant(var1, "", ModelTemplates.RAIL_FLAT, TextureMapping::rail);
      ResourceLocation var3 = this.createSuffixedVariant(var1, "", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail);
      ResourceLocation var4 = this.createSuffixedVariant(var1, "", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail);
      ResourceLocation var5 = this.createSuffixedVariant(var1, "_on", ModelTemplates.RAIL_FLAT, TextureMapping::rail);
      ResourceLocation var6 = this.createSuffixedVariant(var1, "_on", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail);
      ResourceLocation var7 = this.createSuffixedVariant(var1, "_on", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail);
      PropertyDispatch var8 = PropertyDispatch.properties(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((var6x, var7x) -> {
         switch (var7x) {
            case NORTH_SOUTH:
               return Variant.variant().with(VariantProperties.MODEL, var6x ? var5 : var2);
            case EAST_WEST:
               return Variant.variant().with(VariantProperties.MODEL, var6x ? var5 : var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            case ASCENDING_EAST:
               return Variant.variant().with(VariantProperties.MODEL, var6x ? var6 : var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            case ASCENDING_WEST:
               return Variant.variant().with(VariantProperties.MODEL, var6x ? var7 : var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            case ASCENDING_NORTH:
               return Variant.variant().with(VariantProperties.MODEL, var6x ? var6 : var3);
            case ASCENDING_SOUTH:
               return Variant.variant().with(VariantProperties.MODEL, var6x ? var7 : var4);
            default:
               throw new UnsupportedOperationException("Fix you generator!");
         }
      });
      this.createSimpleFlatItemModel(var1);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var1).with(var8));
   }

   private BlockModelGenerators.BlockEntityModelGenerator blockEntityModels(ResourceLocation var1, Block var2) {
      return new BlockModelGenerators.BlockEntityModelGenerator(var1, var2);
   }

   private BlockModelGenerators.BlockEntityModelGenerator blockEntityModels(Block var1, Block var2) {
      return new BlockModelGenerators.BlockEntityModelGenerator(ModelLocationUtils.getModelLocation(var1), var2);
   }

   private void createAirLikeBlock(Block var1, Item var2) {
      ResourceLocation var3 = ModelTemplates.PARTICLE_ONLY.create(var1, TextureMapping.particleFromItem(var2), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var3));
   }

   private void createAirLikeBlock(Block var1, ResourceLocation var2) {
      ResourceLocation var3 = ModelTemplates.PARTICLE_ONLY.create(var1, TextureMapping.particle(var2), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var3));
   }

   private void createFullAndCarpetBlocks(Block var1, Block var2) {
      this.createTrivialCube(var1);
      ResourceLocation var3 = TexturedModel.CARPET.get(var1).create(var2, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var2, var3));
   }

   private void createFlowerBed(Block var1) {
      this.createSimpleFlatItemModel(var1.asItem());
      ResourceLocation var2 = TexturedModel.FLOWERBED_1.create(var1, this.modelOutput);
      ResourceLocation var3 = TexturedModel.FLOWERBED_2.create(var1, this.modelOutput);
      ResourceLocation var4 = TexturedModel.FLOWERBED_3.create(var1, this.modelOutput);
      ResourceLocation var5 = TexturedModel.FLOWERBED_4.create(var1, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(var1)
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                  Variant.variant().with(VariantProperties.MODEL, var2)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 1, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                  Variant.variant().with(VariantProperties.MODEL, var3)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                  Variant.variant().with(VariantProperties.MODEL, var4)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                  Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                  Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 3, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
                  Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH),
                  Variant.variant().with(VariantProperties.MODEL, var5)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST),
                  Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                  Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.FLOWER_AMOUNT, 4).term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST),
                  Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
         );
   }

   private void createColoredBlockWithRandomRotations(TexturedModel.Provider var1, Block... var2) {
      for (Block var6 : var2) {
         ResourceLocation var7 = var1.create(var6, this.modelOutput);
         this.blockStateOutput.accept(createRotatedVariant(var6, var7));
      }
   }

   private void createColoredBlockWithStateRotations(TexturedModel.Provider var1, Block... var2) {
      for (Block var6 : var2) {
         ResourceLocation var7 = var1.create(var6, this.modelOutput);
         this.blockStateOutput
            .accept(MultiVariantGenerator.multiVariant(var6, Variant.variant().with(VariantProperties.MODEL, var7)).with(createHorizontalFacingDispatchAlt()));
      }
   }

   private void createGlassBlocks(Block var1, Block var2) {
      this.createTrivialCube(var1);
      TextureMapping var3 = TextureMapping.pane(var1, var2);
      ResourceLocation var4 = ModelTemplates.STAINED_GLASS_PANE_POST.create(var2, var3, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(var2, var3, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(var2, var3, this.modelOutput);
      ResourceLocation var7 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(var2, var3, this.modelOutput);
      ResourceLocation var8 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(var2, var3, this.modelOutput);
      Item var9 = var2.asItem();
      ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(var9), TextureMapping.layer0(var1), this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(var2)
               .with(Variant.variant().with(VariantProperties.MODEL, var4))
               .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, var5))
               .with(
                  Condition.condition().term(BlockStateProperties.EAST, true),
                  Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, var6))
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, true),
                  Variant.variant().with(VariantProperties.MODEL, var6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, var7))
               .with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, var8))
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH, false),
                  Variant.variant().with(VariantProperties.MODEL, var8).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, false),
                  Variant.variant().with(VariantProperties.MODEL, var7).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
         );
   }

   private void createCommandBlock(Block var1) {
      TextureMapping var2 = TextureMapping.commandBlock(var1);
      ResourceLocation var3 = ModelTemplates.COMMAND_BLOCK.create(var1, var2, this.modelOutput);
      ResourceLocation var4 = this.createSuffixedVariant(
         var1, "_conditional", ModelTemplates.COMMAND_BLOCK, var1x -> var2.copyAndUpdate(TextureSlot.SIDE, var1x)
      );
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(createBooleanModelDispatch(BlockStateProperties.CONDITIONAL, var4, var3))
               .with(createFacingDispatch())
         );
   }

   private void createAnvil(Block var1) {
      ResourceLocation var2 = TexturedModel.ANVIL.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var2).with(createHorizontalFacingDispatchAlt()));
   }

   private List<Variant> createBambooModels(int var1) {
      String var2 = "_age" + var1;
      return IntStream.range(1, 5)
         .mapToObj(var1x -> Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, var1x + var2)))
         .collect(Collectors.toList());
   }

   private void createBamboo() {
      this.skipAutoItemBlock(Blocks.BAMBOO);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.BAMBOO)
               .with(Condition.condition().term(BlockStateProperties.AGE_1, 0), this.createBambooModels(0))
               .with(Condition.condition().term(BlockStateProperties.AGE_1, 1), this.createBambooModels(1))
               .with(
                  Condition.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.SMALL),
                  Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_small_leaves"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE),
                  Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_large_leaves"))
               )
         );
   }

   private PropertyDispatch createColumnWithFacing() {
      return PropertyDispatch.property(BlockStateProperties.FACING)
         .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
         .select(Direction.UP, Variant.variant())
         .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
         .select(
            Direction.SOUTH,
            Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
         )
         .select(
            Direction.WEST,
            Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
         )
         .select(
            Direction.EAST,
            Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
         );
   }

   private void createBarrel() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.BARREL)
               .with(this.createColumnWithFacing())
               .with(
                  PropertyDispatch.property(BlockStateProperties.OPEN)
                     .select(false, Variant.variant().with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.create(Blocks.BARREL, this.modelOutput)))
                     .select(
                        true,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              TexturedModel.CUBE_TOP_BOTTOM
                                 .get(Blocks.BARREL)
                                 .updateTextures(var1x -> var1x.put(TextureSlot.TOP, var1))
                                 .createWithSuffix(Blocks.BARREL, "_open", this.modelOutput)
                           )
                     )
               )
         );
   }

   private static <T extends Comparable<T>> PropertyDispatch createEmptyOrFullDispatch(Property<T> var0, T var1, ResourceLocation var2, ResourceLocation var3) {
      Variant var4 = Variant.variant().with(VariantProperties.MODEL, var2);
      Variant var5 = Variant.variant().with(VariantProperties.MODEL, var3);
      return PropertyDispatch.property(var0).generate(var3x -> {
         boolean var4x = var3x.compareTo(var1) >= 0;
         return var4x ? var4 : var5;
      });
   }

   private void createBeeNest(Block var1, Function<Block, TextureMapping> var2) {
      TextureMapping var3 = ((TextureMapping)var2.apply(var1)).copyForced(TextureSlot.SIDE, TextureSlot.PARTICLE);
      TextureMapping var4 = var3.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1, "_front_honey"));
      ResourceLocation var5 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create(var1, var3, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(var1, "_honey", var4, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(createHorizontalFacingDispatch())
               .with(createEmptyOrFullDispatch(BlockStateProperties.LEVEL_HONEY, 5, var6, var5))
         );
   }

   private void createCropBlock(Block var1, Property<Integer> var2, int... var3) {
      if (var2.getPossibleValues().size() != var3.length) {
         throw new IllegalArgumentException();
      } else {
         Int2ObjectOpenHashMap var4 = new Int2ObjectOpenHashMap();
         PropertyDispatch var5 = PropertyDispatch.<Integer>property(var2)
            .generate(
               var4x -> {
                  int var5x = var3[var4x];
                  ResourceLocation var6 = (ResourceLocation)var4.computeIfAbsent(
                     var5x, var3xx -> this.createSuffixedVariant(var1, "_stage" + var5x, ModelTemplates.CROP, TextureMapping::crop)
                  );
                  return Variant.variant().with(VariantProperties.MODEL, var6);
               }
            );
         this.createSimpleFlatItemModel(var1.asItem());
         this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var1).with(var5));
      }
   }

   private void createBell() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_floor");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_ceiling");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_wall");
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_between_walls");
      this.createSimpleFlatItemModel(Items.BELL);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.BELL)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT)
                     .select(Direction.NORTH, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, var1))
                     .select(
                        Direction.SOUTH,
                        BellAttachType.FLOOR,
                        Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        Direction.EAST,
                        BellAttachType.FLOOR,
                        Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        Direction.WEST,
                        BellAttachType.FLOOR,
                        Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(Direction.NORTH, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, var2))
                     .select(
                        Direction.SOUTH,
                        BellAttachType.CEILING,
                        Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        Direction.EAST,
                        BellAttachType.CEILING,
                        Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        Direction.WEST,
                        BellAttachType.CEILING,
                        Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        Direction.NORTH,
                        BellAttachType.SINGLE_WALL,
                        Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        Direction.SOUTH,
                        BellAttachType.SINGLE_WALL,
                        Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(Direction.EAST, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, var3))
                     .select(
                        Direction.WEST,
                        BellAttachType.SINGLE_WALL,
                        Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        Direction.SOUTH,
                        BellAttachType.DOUBLE_WALL,
                        Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        Direction.NORTH,
                        BellAttachType.DOUBLE_WALL,
                        Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(Direction.EAST, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, var4))
                     .select(
                        Direction.WEST,
                        BellAttachType.DOUBLE_WALL,
                        Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
               )
         );
   }

   private void createGrindstone() {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(
                  Blocks.GRINDSTONE, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.GRINDSTONE))
               )
               .with(
                  PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                     .select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
                     .select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                     .select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                     .select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                     .select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                     .select(
                        AttachFace.WALL,
                        Direction.EAST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        AttachFace.WALL,
                        Direction.SOUTH,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        AttachFace.WALL,
                        Direction.WEST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                     .select(
                        AttachFace.CEILING,
                        Direction.WEST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        AttachFace.CEILING,
                        Direction.NORTH,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        AttachFace.CEILING,
                        Direction.EAST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
               )
         );
   }

   private void createFurnace(Block var1, TexturedModel.Provider var2) {
      ResourceLocation var3 = var2.create(var1, this.modelOutput);
      ResourceLocation var4 = TextureMapping.getBlockTexture(var1, "_front_on");
      ResourceLocation var5 = var2.get(var1).updateTextures(var1x -> var1x.put(TextureSlot.FRONT, var4)).createWithSuffix(var1, "_on", this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(createBooleanModelDispatch(BlockStateProperties.LIT, var5, var3))
               .with(createHorizontalFacingDispatch())
         );
   }

   private void createCampfires(Block... var1) {
      ResourceLocation var2 = ModelLocationUtils.decorateBlockModelLocation("campfire_off");

      for (Block var6 : var1) {
         ResourceLocation var7 = ModelTemplates.CAMPFIRE.create(var6, TextureMapping.campfire(var6), this.modelOutput);
         this.createSimpleFlatItemModel(var6.asItem());
         this.blockStateOutput
            .accept(
               MultiVariantGenerator.multiVariant(var6)
                  .with(createBooleanModelDispatch(BlockStateProperties.LIT, var7, var2))
                  .with(createHorizontalFacingDispatchAlt())
            );
      }
   }

   private void createAzalea(Block var1) {
      ResourceLocation var2 = ModelTemplates.AZALEA.create(var1, TextureMapping.cubeTop(var1), this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, var2));
   }

   private void createPottedAzalea(Block var1) {
      ResourceLocation var2;
      if (var1 == Blocks.POTTED_FLOWERING_AZALEA) {
         var2 = ModelTemplates.POTTED_FLOWERING_AZALEA.create(var1, TextureMapping.pottedAzalea(var1), this.modelOutput);
      } else {
         var2 = ModelTemplates.POTTED_AZALEA.create(var1, TextureMapping.pottedAzalea(var1), this.modelOutput);
      }

      this.blockStateOutput.accept(createSimpleBlock(var1, var2));
   }

   private void createBookshelf() {
      TextureMapping var1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.BOOKSHELF), TextureMapping.getBlockTexture(Blocks.OAK_PLANKS));
      ResourceLocation var2 = ModelTemplates.CUBE_COLUMN.create(Blocks.BOOKSHELF, var1, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.BOOKSHELF, var2));
   }

   private void createRedstoneWire() {
      this.createSimpleFlatItemModel(Items.REDSTONE);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.REDSTONE_WIRE)
               .with(
                  Condition.or(
                     Condition.condition()
                        .term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.NONE)
                        .term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.NONE)
                        .term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.NONE)
                        .term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.NONE),
                     Condition.condition()
                        .term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)
                        .term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                     Condition.condition()
                        .term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)
                        .term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                     Condition.condition()
                        .term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)
                        .term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                     Condition.condition()
                        .term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)
                        .term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)
                  ),
                  Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_dot"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                  Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side0"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                  Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt0"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                  Variant.variant()
                     .with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt1"))
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP),
                  Variant.variant()
                     .with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side1"))
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.UP),
                  Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.UP),
                  Variant.variant()
                     .with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.UP),
                  Variant.variant()
                     .with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.UP),
                  Variant.variant()
                     .with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
               )
         );
   }

   private void createComparator() {
      this.createSimpleFlatItemModel(Items.COMPARATOR);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.COMPARATOR)
               .with(createHorizontalFacingDispatchAlt())
               .with(
                  PropertyDispatch.properties(BlockStateProperties.MODE_COMPARATOR, BlockStateProperties.POWERED)
                     .select(
                        ComparatorMode.COMPARE, false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR))
                     )
                     .select(
                        ComparatorMode.COMPARE,
                        true,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on"))
                     )
                     .select(
                        ComparatorMode.SUBTRACT,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_subtract"))
                     )
                     .select(
                        ComparatorMode.SUBTRACT,
                        true,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on_subtract"))
                     )
               )
         );
   }

   private void createSmoothStoneSlab() {
      TextureMapping var1 = TextureMapping.cube(Blocks.SMOOTH_STONE);
      TextureMapping var2 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), var1.get(TextureSlot.TOP));
      ResourceLocation var3 = ModelTemplates.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, var2, this.modelOutput);
      ResourceLocation var4 = ModelTemplates.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", var2, this.modelOutput);
      this.blockStateOutput.accept(createSlab(Blocks.SMOOTH_STONE_SLAB, var3, var4, var5));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMOOTH_STONE, ModelTemplates.CUBE_ALL.create(Blocks.SMOOTH_STONE, var1, this.modelOutput)));
   }

   private void createBrewingStand() {
      this.createSimpleFlatItemModel(Items.BREWING_STAND);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.BREWING_STAND)
               .with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND)))
               .with(
                  Condition.condition().term(BlockStateProperties.HAS_BOTTLE_0, true),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle0"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.HAS_BOTTLE_1, true),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle1"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.HAS_BOTTLE_2, true),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle2"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.HAS_BOTTLE_0, false),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty0"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.HAS_BOTTLE_1, false),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty1"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.HAS_BOTTLE_2, false),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty2"))
               )
         );
   }

   private void createMushroomBlock(Block var1) {
      ResourceLocation var2 = ModelTemplates.SINGLE_FACE.create(var1, TextureMapping.defaultTexture(var1), this.modelOutput);
      ResourceLocation var3 = ModelLocationUtils.decorateBlockModelLocation("mushroom_block_inside");
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(var1)
               .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, var2))
               .with(
                  Condition.condition().term(BlockStateProperties.EAST, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.UP, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.DOWN, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, var3))
               .with(
                  Condition.condition().term(BlockStateProperties.EAST, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, false)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, false)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, false)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.UP, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, false)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.DOWN, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, false)
               )
         );
      this.delegateItemModel(var1, TexturedModel.CUBE.createWithSuffix(var1, "_inventory", this.modelOutput));
   }

   private void createCakeBlock() {
      this.createSimpleFlatItemModel(Items.CAKE);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.CAKE)
               .with(
                  PropertyDispatch.property(BlockStateProperties.BITES)
                     .select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE)))
                     .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice1")))
                     .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice2")))
                     .select(3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice3")))
                     .select(4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice4")))
                     .select(5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice5")))
                     .select(6, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice6")))
               )
         );
   }

   private void createCartographyTable() {
      TextureMapping var1 = new TextureMapping()
         .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3"))
         .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.DARK_OAK_PLANKS))
         .put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_top"))
         .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3"))
         .put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3"))
         .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side1"))
         .put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.CARTOGRAPHY_TABLE, ModelTemplates.CUBE.create(Blocks.CARTOGRAPHY_TABLE, var1, this.modelOutput)));
   }

   private void createSmithingTable() {
      TextureMapping var1 = new TextureMapping()
         .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front"))
         .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_bottom"))
         .put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_top"))
         .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front"))
         .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front"))
         .put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side"))
         .put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMITHING_TABLE, ModelTemplates.CUBE.create(Blocks.SMITHING_TABLE, var1, this.modelOutput)));
   }

   private void createCraftingTableLike(Block var1, Block var2, BiFunction<Block, Block, TextureMapping> var3) {
      TextureMapping var4 = (TextureMapping)var3.apply(var1, var2);
      this.blockStateOutput.accept(createSimpleBlock(var1, ModelTemplates.CUBE.create(var1, var4, this.modelOutput)));
   }

   public void createGenericCube(Block var1) {
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(var1, "_particle"))
         .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(var1, "_down"))
         .put(TextureSlot.UP, TextureMapping.getBlockTexture(var1, "_up"))
         .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(var1, "_north"))
         .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(var1, "_south"))
         .put(TextureSlot.EAST, TextureMapping.getBlockTexture(var1, "_east"))
         .put(TextureSlot.WEST, TextureMapping.getBlockTexture(var1, "_west"));
      this.blockStateOutput.accept(createSimpleBlock(var1, ModelTemplates.CUBE.create(var1, var2, this.modelOutput)));
   }

   private void createPumpkins() {
      TextureMapping var1 = TextureMapping.column(Blocks.PUMPKIN);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.PUMPKIN, ModelLocationUtils.getModelLocation(Blocks.PUMPKIN)));
      this.createPumpkinVariant(Blocks.CARVED_PUMPKIN, var1);
      this.createPumpkinVariant(Blocks.JACK_O_LANTERN, var1);
   }

   private void createPumpkinVariant(Block var1, TextureMapping var2) {
      ResourceLocation var3 = ModelTemplates.CUBE_ORIENTABLE
         .create(var1, var2.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1)), this.modelOutput);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(var1, Variant.variant().with(VariantProperties.MODEL, var3)).with(createHorizontalFacingDispatch()));
   }

   private void createCauldrons() {
      this.createSimpleFlatItemModel(Items.CAULDRON);
      this.createNonTemplateModelBlock(Blocks.CAULDRON);
      this.blockStateOutput
         .accept(
            createSimpleBlock(
               Blocks.LAVA_CAULDRON,
               ModelTemplates.CAULDRON_FULL
                  .create(Blocks.LAVA_CAULDRON, TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.LAVA, "_still")), this.modelOutput)
            )
         );
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.WATER_CAULDRON)
               .with(
                  PropertyDispatch.property(LayeredCauldronBlock.LEVEL)
                     .select(
                        1,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.CAULDRON_LEVEL1
                                 .createWithSuffix(
                                    Blocks.WATER_CAULDRON,
                                    "_level1",
                                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")),
                                    this.modelOutput
                                 )
                           )
                     )
                     .select(
                        2,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.CAULDRON_LEVEL2
                                 .createWithSuffix(
                                    Blocks.WATER_CAULDRON,
                                    "_level2",
                                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")),
                                    this.modelOutput
                                 )
                           )
                     )
                     .select(
                        3,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.CAULDRON_FULL
                                 .createWithSuffix(
                                    Blocks.WATER_CAULDRON,
                                    "_full",
                                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")),
                                    this.modelOutput
                                 )
                           )
                     )
               )
         );
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.POWDER_SNOW_CAULDRON)
               .with(
                  PropertyDispatch.property(LayeredCauldronBlock.LEVEL)
                     .select(
                        1,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.CAULDRON_LEVEL1
                                 .createWithSuffix(
                                    Blocks.POWDER_SNOW_CAULDRON,
                                    "_level1",
                                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)),
                                    this.modelOutput
                                 )
                           )
                     )
                     .select(
                        2,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.CAULDRON_LEVEL2
                                 .createWithSuffix(
                                    Blocks.POWDER_SNOW_CAULDRON,
                                    "_level2",
                                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)),
                                    this.modelOutput
                                 )
                           )
                     )
                     .select(
                        3,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.CAULDRON_FULL
                                 .createWithSuffix(
                                    Blocks.POWDER_SNOW_CAULDRON,
                                    "_full",
                                    TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)),
                                    this.modelOutput
                                 )
                           )
                     )
               )
         );
   }

   private void createChorusFlower() {
      TextureMapping var1 = TextureMapping.defaultTexture(Blocks.CHORUS_FLOWER);
      ResourceLocation var2 = ModelTemplates.CHORUS_FLOWER.create(Blocks.CHORUS_FLOWER, var1, this.modelOutput);
      ResourceLocation var3 = this.createSuffixedVariant(
         Blocks.CHORUS_FLOWER, "_dead", ModelTemplates.CHORUS_FLOWER, var1x -> var1.copyAndUpdate(TextureSlot.TEXTURE, var1x)
      );
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.CHORUS_FLOWER).with(createEmptyOrFullDispatch(BlockStateProperties.AGE_5, 5, var3, var2)));
   }

   private void createCrafterBlock() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.CRAFTER);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_triggered");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_crafting");
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_crafting_triggered");
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.CRAFTER)
               .with(PropertyDispatch.property(BlockStateProperties.ORIENTATION).generate(var1x -> this.applyRotation(var1x, Variant.variant())))
               .with(
                  PropertyDispatch.properties(BlockStateProperties.TRIGGERED, CrafterBlock.CRAFTING)
                     .select(false, false, Variant.variant().with(VariantProperties.MODEL, var1))
                     .select(true, true, Variant.variant().with(VariantProperties.MODEL, var4))
                     .select(true, false, Variant.variant().with(VariantProperties.MODEL, var2))
                     .select(false, true, Variant.variant().with(VariantProperties.MODEL, var3))
               )
         );
   }

   private void createDispenserBlock(Block var1) {
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top"))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_side"))
         .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1, "_front"));
      TextureMapping var3 = new TextureMapping()
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top"))
         .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1, "_front_vertical"));
      ResourceLocation var4 = ModelTemplates.CUBE_ORIENTABLE.create(var1, var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(var1, var3, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(
                  PropertyDispatch.property(BlockStateProperties.FACING)
                     .select(
                        Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(Direction.UP, Variant.variant().with(VariantProperties.MODEL, var5))
                     .select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, var4))
                     .select(
                        Direction.EAST, Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        Direction.WEST, Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
               )
         );
   }

   private void createEndPortalFrame() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME, "_filled");
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.END_PORTAL_FRAME)
               .with(
                  PropertyDispatch.property(BlockStateProperties.EYE)
                     .select(false, Variant.variant().with(VariantProperties.MODEL, var1))
                     .select(true, Variant.variant().with(VariantProperties.MODEL, var2))
               )
               .with(createHorizontalFacingDispatchAlt())
         );
   }

   private void createChorusPlant() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_side");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside1");
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside2");
      ResourceLocation var5 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside3");
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.CHORUS_PLANT)
               .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, var1))
               .with(
                  Condition.condition().term(BlockStateProperties.EAST, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.UP, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.DOWN, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var1)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.NORTH, false),
                  Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.WEIGHT, 2),
                  Variant.variant().with(VariantProperties.MODEL, var3),
                  Variant.variant().with(VariantProperties.MODEL, var4),
                  Variant.variant().with(VariantProperties.MODEL, var5)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.EAST, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var4)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var5)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.WEIGHT, 2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.SOUTH, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var4)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var5)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.WEIGHT, 2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var5)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.WEIGHT, 2)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var4)
                     .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.UP, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.WEIGHT, 2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var5)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var4)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
                     .with(VariantProperties.UV_LOCK, true)
               )
               .with(
                  Condition.condition().term(BlockStateProperties.DOWN, false),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var5)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var4)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var3)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true),
                  Variant.variant()
                     .with(VariantProperties.MODEL, var2)
                     .with(VariantProperties.WEIGHT, 2)
                     .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                     .with(VariantProperties.UV_LOCK, true)
               )
         );
   }

   private void createComposter() {
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.COMPOSTER)
               .with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER)))
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7"))
               )
               .with(
                  Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8),
                  Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))
               )
         );
   }

   private void createCopperBulb(Block var1) {
      ResourceLocation var2 = ModelTemplates.CUBE_ALL.create(var1, TextureMapping.cube(var1), this.modelOutput);
      ResourceLocation var3 = this.createSuffixedVariant(var1, "_powered", ModelTemplates.CUBE_ALL, TextureMapping::cube);
      ResourceLocation var4 = this.createSuffixedVariant(var1, "_lit", ModelTemplates.CUBE_ALL, TextureMapping::cube);
      ResourceLocation var5 = this.createSuffixedVariant(var1, "_lit_powered", ModelTemplates.CUBE_ALL, TextureMapping::cube);
      this.blockStateOutput.accept(this.createCopperBulb(var1, var2, var4, var3, var5));
   }

   private BlockStateGenerator createCopperBulb(Block var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4, ResourceLocation var5) {
      return MultiVariantGenerator.multiVariant(var1)
         .with(
            PropertyDispatch.properties(BlockStateProperties.LIT, BlockStateProperties.POWERED)
               .generate(
                  (var4x, var5x) -> var4x
                        ? Variant.variant().with(VariantProperties.MODEL, var5x ? var5 : var3)
                        : Variant.variant().with(VariantProperties.MODEL, var5x ? var4 : var2)
               )
         );
   }

   private void copyCopperBulbModel(Block var1, Block var2) {
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1);
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(var1, "_powered");
      ResourceLocation var5 = ModelLocationUtils.getModelLocation(var1, "_lit");
      ResourceLocation var6 = ModelLocationUtils.getModelLocation(var1, "_lit_powered");
      this.delegateItemModel(var2, ModelLocationUtils.getModelLocation(var1.asItem()));
      this.blockStateOutput.accept(this.createCopperBulb(var2, var3, var5, var4, var6));
   }

   private void createAmethystCluster(Block var1) {
      this.skipAutoItemBlock(var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(
                  var1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CROSS.create(var1, TextureMapping.cross(var1), this.modelOutput))
               )
               .with(this.createColumnWithFacing())
         );
   }

   private void createAmethystClusters() {
      this.createAmethystCluster(Blocks.SMALL_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.MEDIUM_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.LARGE_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.AMETHYST_CLUSTER);
   }

   private void createPointedDripstone() {
      this.skipAutoItemBlock(Blocks.POINTED_DRIPSTONE);
      PropertyDispatch.C2 var1 = PropertyDispatch.properties(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);

      for (DripstoneThickness var5 : DripstoneThickness.values()) {
         var1.select(Direction.UP, var5, this.createPointedDripstoneVariant(Direction.UP, var5));
      }

      for (DripstoneThickness var9 : DripstoneThickness.values()) {
         var1.select(Direction.DOWN, var9, this.createPointedDripstoneVariant(Direction.DOWN, var9));
      }

      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.POINTED_DRIPSTONE).with(var1));
   }

   private Variant createPointedDripstoneVariant(Direction var1, DripstoneThickness var2) {
      String var3 = "_" + var1.getSerializedName() + "_" + var2.getSerializedName();
      TextureMapping var4 = TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.POINTED_DRIPSTONE, var3));
      return Variant.variant()
         .with(VariantProperties.MODEL, ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(Blocks.POINTED_DRIPSTONE, var3, var4, this.modelOutput));
   }

   private void createNyliumBlock(Block var1) {
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.NETHERRACK))
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(var1))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(var1, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(var1, ModelTemplates.CUBE_BOTTOM_TOP.create(var1, var2, this.modelOutput)));
   }

   private void createDaylightDetector() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_side");
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_top"))
         .put(TextureSlot.SIDE, var1);
      TextureMapping var3 = new TextureMapping()
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_inverted_top"))
         .put(TextureSlot.SIDE, var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.DAYLIGHT_DETECTOR)
               .with(
                  PropertyDispatch.property(BlockStateProperties.INVERTED)
                     .select(
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelTemplates.DAYLIGHT_DETECTOR.create(Blocks.DAYLIGHT_DETECTOR, var2, this.modelOutput))
                     )
                     .select(
                        true,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.DAYLIGHT_DETECTOR
                                 .create(ModelLocationUtils.getModelLocation(Blocks.DAYLIGHT_DETECTOR, "_inverted"), var3, this.modelOutput)
                           )
                     )
               )
         );
   }

   private void createRotatableColumn(Block var1) {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(var1)))
               .with(this.createColumnWithFacing())
         );
   }

   private void createLightningRod() {
      Block var1 = Blocks.LIGHTNING_ROD;
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(var1, "_on");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(var1)))
               .with(this.createColumnWithFacing())
               .with(createBooleanModelDispatch(BlockStateProperties.POWERED, var2, var3))
         );
   }

   private void createFarmland() {
      TextureMapping var1 = new TextureMapping()
         .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT))
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND));
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT))
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"));
      ResourceLocation var3 = ModelTemplates.FARMLAND.create(Blocks.FARMLAND, var1, this.modelOutput);
      ResourceLocation var4 = ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"), var2, this.modelOutput);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.FARMLAND).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, var4, var3)));
   }

   private List<ResourceLocation> createFloorFireModels(Block var1) {
      ResourceLocation var2 = ModelTemplates.FIRE_FLOOR
         .create(ModelLocationUtils.getModelLocation(var1, "_floor0"), TextureMapping.fire0(var1), this.modelOutput);
      ResourceLocation var3 = ModelTemplates.FIRE_FLOOR
         .create(ModelLocationUtils.getModelLocation(var1, "_floor1"), TextureMapping.fire1(var1), this.modelOutput);
      return ImmutableList.of(var2, var3);
   }

   private List<ResourceLocation> createSideFireModels(Block var1) {
      ResourceLocation var2 = ModelTemplates.FIRE_SIDE
         .create(ModelLocationUtils.getModelLocation(var1, "_side0"), TextureMapping.fire0(var1), this.modelOutput);
      ResourceLocation var3 = ModelTemplates.FIRE_SIDE
         .create(ModelLocationUtils.getModelLocation(var1, "_side1"), TextureMapping.fire1(var1), this.modelOutput);
      ResourceLocation var4 = ModelTemplates.FIRE_SIDE_ALT
         .create(ModelLocationUtils.getModelLocation(var1, "_side_alt0"), TextureMapping.fire0(var1), this.modelOutput);
      ResourceLocation var5 = ModelTemplates.FIRE_SIDE_ALT
         .create(ModelLocationUtils.getModelLocation(var1, "_side_alt1"), TextureMapping.fire1(var1), this.modelOutput);
      return ImmutableList.of(var2, var3, var4, var5);
   }

   private List<ResourceLocation> createTopFireModels(Block var1) {
      ResourceLocation var2 = ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation(var1, "_up0"), TextureMapping.fire0(var1), this.modelOutput);
      ResourceLocation var3 = ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation(var1, "_up1"), TextureMapping.fire1(var1), this.modelOutput);
      ResourceLocation var4 = ModelTemplates.FIRE_UP_ALT
         .create(ModelLocationUtils.getModelLocation(var1, "_up_alt0"), TextureMapping.fire0(var1), this.modelOutput);
      ResourceLocation var5 = ModelTemplates.FIRE_UP_ALT
         .create(ModelLocationUtils.getModelLocation(var1, "_up_alt1"), TextureMapping.fire1(var1), this.modelOutput);
      return ImmutableList.of(var2, var3, var4, var5);
   }

   private static List<Variant> wrapModels(List<ResourceLocation> var0, UnaryOperator<Variant> var1) {
      return var0.stream().map(var0x -> Variant.variant().with(VariantProperties.MODEL, var0x)).map(var1).collect(Collectors.toList());
   }

   private void createFire() {
      Condition.TerminalCondition var1 = Condition.condition()
         .term(BlockStateProperties.NORTH, false)
         .term(BlockStateProperties.EAST, false)
         .term(BlockStateProperties.SOUTH, false)
         .term(BlockStateProperties.WEST, false)
         .term(BlockStateProperties.UP, false);
      List var2 = this.createFloorFireModels(Blocks.FIRE);
      List var3 = this.createSideFireModels(Blocks.FIRE);
      List var4 = this.createTopFireModels(Blocks.FIRE);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.FIRE)
               .with(var1, wrapModels(var2, var0 -> var0))
               .with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH, true), var1), wrapModels(var3, var0 -> var0))
               .with(
                  Condition.or(Condition.condition().term(BlockStateProperties.EAST, true), var1),
                  wrapModels(var3, var0 -> var0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
               )
               .with(
                  Condition.or(Condition.condition().term(BlockStateProperties.SOUTH, true), var1),
                  wrapModels(var3, var0 -> var0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
               )
               .with(
                  Condition.or(Condition.condition().term(BlockStateProperties.WEST, true), var1),
                  wrapModels(var3, var0 -> var0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
               )
               .with(Condition.condition().term(BlockStateProperties.UP, true), wrapModels(var4, var0 -> var0))
         );
   }

   private void createSoulFire() {
      List var1 = this.createFloorFireModels(Blocks.SOUL_FIRE);
      List var2 = this.createSideFireModels(Blocks.SOUL_FIRE);
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.SOUL_FIRE)
               .with(wrapModels(var1, var0 -> var0))
               .with(wrapModels(var2, var0 -> var0))
               .with(wrapModels(var2, var0 -> var0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))
               .with(wrapModels(var2, var0 -> var0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)))
               .with(wrapModels(var2, var0 -> var0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))
         );
   }

   private void createLantern(Block var1) {
      ResourceLocation var2 = TexturedModel.LANTERN.create(var1, this.modelOutput);
      ResourceLocation var3 = TexturedModel.HANGING_LANTERN.create(var1, this.modelOutput);
      this.createSimpleFlatItemModel(var1.asItem());
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var1).with(createBooleanModelDispatch(BlockStateProperties.HANGING, var3, var2)));
   }

   private void createMuddyMangroveRoots() {
      TextureMapping var1 = TextureMapping.column(
         TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_side"), TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_top")
      );
      ResourceLocation var2 = ModelTemplates.CUBE_COLUMN.create(Blocks.MUDDY_MANGROVE_ROOTS, var1, this.modelOutput);
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(Blocks.MUDDY_MANGROVE_ROOTS, var2));
   }

   private void createMangrovePropagule() {
      this.createSimpleFlatItemModel(Items.MANGROVE_PROPAGULE);
      Block var1 = Blocks.MANGROVE_PROPAGULE;
      PropertyDispatch.C2 var2 = PropertyDispatch.properties(MangrovePropaguleBlock.HANGING, MangrovePropaguleBlock.AGE);
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1);

      for (int var4 = 0; var4 <= 4; var4++) {
         ResourceLocation var5 = ModelLocationUtils.getModelLocation(var1, "_hanging_" + var4);
         var2.select(true, var4, Variant.variant().with(VariantProperties.MODEL, var5));
         var2.select(false, var4, Variant.variant().with(VariantProperties.MODEL, var3));
      }

      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.MANGROVE_PROPAGULE).with(var2));
   }

   private void createFrostedIce() {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.FROSTED_ICE)
               .with(
                  PropertyDispatch.property(BlockStateProperties.AGE_3)
                     .select(
                        0,
                        Variant.variant()
                           .with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_0", ModelTemplates.CUBE_ALL, TextureMapping::cube))
                     )
                     .select(
                        1,
                        Variant.variant()
                           .with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_1", ModelTemplates.CUBE_ALL, TextureMapping::cube))
                     )
                     .select(
                        2,
                        Variant.variant()
                           .with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_2", ModelTemplates.CUBE_ALL, TextureMapping::cube))
                     )
                     .select(
                        3,
                        Variant.variant()
                           .with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_3", ModelTemplates.CUBE_ALL, TextureMapping::cube))
                     )
               )
         );
   }

   private void createGrassBlocks() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.DIRT);
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.BOTTOM, var1)
         .copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_top"))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
      Variant var3 = Variant.variant()
         .with(VariantProperties.MODEL, ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.GRASS_BLOCK, "_snow", var2, this.modelOutput));
      this.createGrassLikeBlock(Blocks.GRASS_BLOCK, ModelLocationUtils.getModelLocation(Blocks.GRASS_BLOCK), var3);
      ResourceLocation var4 = TexturedModel.CUBE_TOP_BOTTOM
         .get(Blocks.MYCELIUM)
         .updateTextures(var1x -> var1x.put(TextureSlot.BOTTOM, var1))
         .create(Blocks.MYCELIUM, this.modelOutput);
      this.createGrassLikeBlock(Blocks.MYCELIUM, var4, var3);
      ResourceLocation var5 = TexturedModel.CUBE_TOP_BOTTOM
         .get(Blocks.PODZOL)
         .updateTextures(var1x -> var1x.put(TextureSlot.BOTTOM, var1))
         .create(Blocks.PODZOL, this.modelOutput);
      this.createGrassLikeBlock(Blocks.PODZOL, var5, var3);
   }

   private void createGrassLikeBlock(Block var1, ResourceLocation var2, Variant var3) {
      List var4 = Arrays.asList(createRotatedVariants(var2));
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(var1).with(PropertyDispatch.property(BlockStateProperties.SNOWY).select(true, var3).select(false, var4)));
   }

   private void createCocoa() {
      this.createSimpleFlatItemModel(Items.COCOA_BEANS);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.COCOA)
               .with(
                  PropertyDispatch.property(BlockStateProperties.AGE_2)
                     .select(0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage0")))
                     .select(1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage1")))
                     .select(2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage2")))
               )
               .with(createHorizontalFacingDispatchAlt())
         );
   }

   private void createDirtPath() {
      this.blockStateOutput.accept(createRotatedVariant(Blocks.DIRT_PATH, ModelLocationUtils.getModelLocation(Blocks.DIRT_PATH)));
   }

   private void createWeightedPressurePlate(Block var1, Block var2) {
      TextureMapping var3 = TextureMapping.defaultTexture(var2);
      ResourceLocation var4 = ModelTemplates.PRESSURE_PLATE_UP.create(var1, var3, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.PRESSURE_PLATE_DOWN.create(var1, var3, this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var1).with(createEmptyOrFullDispatch(BlockStateProperties.POWER, 1, var5, var4)));
   }

   private void createHopper() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.HOPPER);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.HOPPER, "_side");
      this.createSimpleFlatItemModel(Items.HOPPER);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.HOPPER)
               .with(
                  PropertyDispatch.property(BlockStateProperties.FACING_HOPPER)
                     .select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, var1))
                     .select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, var2))
                     .select(
                        Direction.EAST, Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        Direction.WEST, Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
               )
         );
   }

   private void copyModel(Block var1, Block var2) {
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var2, Variant.variant().with(VariantProperties.MODEL, var3)));
      this.delegateItemModel(var2, var3);
   }

   private void createIronBars() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post_ends");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post");
      ResourceLocation var3 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap");
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap_alt");
      ResourceLocation var5 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side");
      ResourceLocation var6 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side_alt");
      this.blockStateOutput
         .accept(
            MultiPartGenerator.multiPart(Blocks.IRON_BARS)
               .with(Variant.variant().with(VariantProperties.MODEL, var1))
               .with(
                  Condition.condition()
                     .term(BlockStateProperties.NORTH, false)
                     .term(BlockStateProperties.EAST, false)
                     .term(BlockStateProperties.SOUTH, false)
                     .term(BlockStateProperties.WEST, false),
                  Variant.variant().with(VariantProperties.MODEL, var2)
               )
               .with(
                  Condition.condition()
                     .term(BlockStateProperties.NORTH, true)
                     .term(BlockStateProperties.EAST, false)
                     .term(BlockStateProperties.SOUTH, false)
                     .term(BlockStateProperties.WEST, false),
                  Variant.variant().with(VariantProperties.MODEL, var3)
               )
               .with(
                  Condition.condition()
                     .term(BlockStateProperties.NORTH, false)
                     .term(BlockStateProperties.EAST, true)
                     .term(BlockStateProperties.SOUTH, false)
                     .term(BlockStateProperties.WEST, false),
                  Variant.variant().with(VariantProperties.MODEL, var3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(
                  Condition.condition()
                     .term(BlockStateProperties.NORTH, false)
                     .term(BlockStateProperties.EAST, false)
                     .term(BlockStateProperties.SOUTH, true)
                     .term(BlockStateProperties.WEST, false),
                  Variant.variant().with(VariantProperties.MODEL, var4)
               )
               .with(
                  Condition.condition()
                     .term(BlockStateProperties.NORTH, false)
                     .term(BlockStateProperties.EAST, false)
                     .term(BlockStateProperties.SOUTH, false)
                     .term(BlockStateProperties.WEST, true),
                  Variant.variant().with(VariantProperties.MODEL, var4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, var5))
               .with(
                  Condition.condition().term(BlockStateProperties.EAST, true),
                  Variant.variant().with(VariantProperties.MODEL, var5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
               .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, var6))
               .with(
                  Condition.condition().term(BlockStateProperties.WEST, true),
                  Variant.variant().with(VariantProperties.MODEL, var6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
               )
         );
      this.createSimpleFlatItemModel(Blocks.IRON_BARS);
   }

   private void createNonTemplateHorizontalBlock(Block var1) {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(var1)))
               .with(createHorizontalFacingDispatch())
         );
   }

   private void createLever() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.LEVER);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.LEVER, "_on");
      this.createSimpleFlatItemModel(Blocks.LEVER);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.LEVER)
               .with(createBooleanModelDispatch(BlockStateProperties.POWERED, var1, var2))
               .with(
                  PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                     .select(
                        AttachFace.CEILING,
                        Direction.NORTH,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        AttachFace.CEILING,
                        Direction.EAST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                     .select(
                        AttachFace.CEILING,
                        Direction.WEST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
                     .select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                     .select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                     .select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                     .select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                     .select(
                        AttachFace.WALL,
                        Direction.EAST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        AttachFace.WALL,
                        Direction.SOUTH,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        AttachFace.WALL,
                        Direction.WEST,
                        Variant.variant()
                           .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
               )
         );
   }

   private void createLilyPad() {
      this.createSimpleFlatItemModel(Blocks.LILY_PAD);
      this.blockStateOutput.accept(createRotatedVariant(Blocks.LILY_PAD, ModelLocationUtils.getModelLocation(Blocks.LILY_PAD)));
   }

   private void createFrogspawnBlock() {
      this.createSimpleFlatItemModel(Blocks.FROGSPAWN);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.FROGSPAWN, ModelLocationUtils.getModelLocation(Blocks.FROGSPAWN)));
   }

   private void createNetherPortalBlock() {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.NETHER_PORTAL)
               .with(
                  PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
                     .select(
                        Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ns"))
                     )
                     .select(
                        Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ew"))
                     )
               )
         );
   }

   private void createNetherrack() {
      ResourceLocation var1 = TexturedModel.CUBE.create(Blocks.NETHERRACK, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(
               Blocks.NETHERRACK,
               Variant.variant().with(VariantProperties.MODEL, var1),
               Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90),
               Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180),
               Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270),
               Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270),
               Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270),
               Variant.variant().with(VariantProperties.MODEL, var1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180),
               Variant.variant()
                  .with(VariantProperties.MODEL, var1)
                  .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                  .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)
            )
         );
   }

   private void createObserver() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.OBSERVER);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.OBSERVER, "_on");
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.OBSERVER)
               .with(createBooleanModelDispatch(BlockStateProperties.POWERED, var2, var1))
               .with(createFacingDispatch())
         );
   }

   private void createPistons() {
      TextureMapping var1 = new TextureMapping()
         .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.PISTON, "_bottom"))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      ResourceLocation var2 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky");
      ResourceLocation var3 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top");
      TextureMapping var4 = var1.copyAndUpdate(TextureSlot.PLATFORM, var2);
      TextureMapping var5 = var1.copyAndUpdate(TextureSlot.PLATFORM, var3);
      ResourceLocation var6 = ModelLocationUtils.getModelLocation(Blocks.PISTON, "_base");
      this.createPistonVariant(Blocks.PISTON, var6, var5);
      this.createPistonVariant(Blocks.STICKY_PISTON, var6, var4);
      ResourceLocation var7 = ModelTemplates.CUBE_BOTTOM_TOP
         .createWithSuffix(Blocks.PISTON, "_inventory", var1.copyAndUpdate(TextureSlot.TOP, var3), this.modelOutput);
      ResourceLocation var8 = ModelTemplates.CUBE_BOTTOM_TOP
         .createWithSuffix(Blocks.STICKY_PISTON, "_inventory", var1.copyAndUpdate(TextureSlot.TOP, var2), this.modelOutput);
      this.delegateItemModel(Blocks.PISTON, var7);
      this.delegateItemModel(Blocks.STICKY_PISTON, var8);
   }

   private void createPistonVariant(Block var1, ResourceLocation var2, TextureMapping var3) {
      ResourceLocation var4 = ModelTemplates.PISTON.create(var1, var3, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1).with(createBooleanModelDispatch(BlockStateProperties.EXTENDED, var2, var4)).with(createFacingDispatch())
         );
   }

   private void createPistonHeads() {
      TextureMapping var1 = new TextureMapping()
         .put(TextureSlot.UNSTICKY, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      TextureMapping var2 = var1.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky"));
      TextureMapping var3 = var1.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"));
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.PISTON_HEAD)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE)
                     .select(
                        false,
                        PistonType.DEFAULT,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head", var3, this.modelOutput))
                     )
                     .select(
                        false,
                        PistonType.STICKY,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head_sticky", var2, this.modelOutput))
                     )
                     .select(
                        true,
                        PistonType.DEFAULT,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short", var3, this.modelOutput)
                           )
                     )
                     .select(
                        true,
                        PistonType.STICKY,
                        Variant.variant()
                           .with(
                              VariantProperties.MODEL,
                              ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short_sticky", var2, this.modelOutput)
                           )
                     )
               )
               .with(createFacingDispatch())
         );
   }

   private void createTrialSpawner() {
      Block var1 = Blocks.TRIAL_SPAWNER;
      TextureMapping var2 = TextureMapping.trialSpawner(var1, "_side_inactive", "_top_inactive");
      TextureMapping var3 = TextureMapping.trialSpawner(var1, "_side_active", "_top_active");
      TextureMapping var4 = TextureMapping.trialSpawner(var1, "_side_active", "_top_ejecting_reward");
      TextureMapping var5 = TextureMapping.trialSpawner(var1, "_side_inactive_ominous", "_top_inactive_ominous");
      TextureMapping var6 = TextureMapping.trialSpawner(var1, "_side_active_ominous", "_top_active_ominous");
      TextureMapping var7 = TextureMapping.trialSpawner(var1, "_side_active_ominous", "_top_ejecting_reward_ominous");
      ResourceLocation var8 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.create(var1, var2, this.modelOutput);
      ResourceLocation var9 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_active", var3, this.modelOutput);
      ResourceLocation var10 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_ejecting_reward", var4, this.modelOutput);
      ResourceLocation var11 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_inactive_ominous", var5, this.modelOutput);
      ResourceLocation var12 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_active_ominous", var6, this.modelOutput);
      ResourceLocation var13 = ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_ejecting_reward_ominous", var7, this.modelOutput);
      this.delegateItemModel(var1, var8);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(PropertyDispatch.properties(BlockStateProperties.TRIAL_SPAWNER_STATE, BlockStateProperties.OMINOUS).generate((var6x, var7x) -> {
                  return switch (var6x) {
                     case INACTIVE, COOLDOWN -> Variant.variant().with(VariantProperties.MODEL, var7x ? var11 : var8);
                     case WAITING_FOR_PLAYERS, ACTIVE, WAITING_FOR_REWARD_EJECTION -> Variant.variant().with(VariantProperties.MODEL, var7x ? var12 : var9);
                     case EJECTING_REWARD -> Variant.variant().with(VariantProperties.MODEL, var7x ? var13 : var10);
                  };
               }))
         );
   }

   private void createVault() {
      Block var1 = Blocks.VAULT;
      TextureMapping var2 = TextureMapping.vault(var1, "_front_off", "_side_off", "_top", "_bottom");
      TextureMapping var3 = TextureMapping.vault(var1, "_front_on", "_side_on", "_top", "_bottom");
      TextureMapping var4 = TextureMapping.vault(var1, "_front_ejecting", "_side_on", "_top", "_bottom");
      TextureMapping var5 = TextureMapping.vault(var1, "_front_ejecting", "_side_on", "_top_ejecting", "_bottom");
      ResourceLocation var6 = ModelTemplates.VAULT.create(var1, var2, this.modelOutput);
      ResourceLocation var7 = ModelTemplates.VAULT.createWithSuffix(var1, "_active", var3, this.modelOutput);
      ResourceLocation var8 = ModelTemplates.VAULT.createWithSuffix(var1, "_unlocking", var4, this.modelOutput);
      ResourceLocation var9 = ModelTemplates.VAULT.createWithSuffix(var1, "_ejecting_reward", var5, this.modelOutput);
      TextureMapping var10 = TextureMapping.vault(var1, "_front_off_ominous", "_side_off_ominous", "_top_ominous", "_bottom_ominous");
      TextureMapping var11 = TextureMapping.vault(var1, "_front_on_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
      TextureMapping var12 = TextureMapping.vault(var1, "_front_ejecting_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
      TextureMapping var13 = TextureMapping.vault(var1, "_front_ejecting_ominous", "_side_on_ominous", "_top_ejecting_ominous", "_bottom_ominous");
      ResourceLocation var14 = ModelTemplates.VAULT.createWithSuffix(var1, "_ominous", var10, this.modelOutput);
      ResourceLocation var15 = ModelTemplates.VAULT.createWithSuffix(var1, "_active_ominous", var11, this.modelOutput);
      ResourceLocation var16 = ModelTemplates.VAULT.createWithSuffix(var1, "_unlocking_ominous", var12, this.modelOutput);
      ResourceLocation var17 = ModelTemplates.VAULT.createWithSuffix(var1, "_ejecting_reward_ominous", var13, this.modelOutput);
      this.delegateItemModel(var1, var6);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(createHorizontalFacingDispatch())
               .with(PropertyDispatch.properties(VaultBlock.STATE, VaultBlock.OMINOUS).generate((var8x, var9x) -> {
                  return switch (var8x) {
                     case INACTIVE -> Variant.variant().with(VariantProperties.MODEL, var9x ? var14 : var6);
                     case ACTIVE -> Variant.variant().with(VariantProperties.MODEL, var9x ? var15 : var7);
                     case UNLOCKING -> Variant.variant().with(VariantProperties.MODEL, var9x ? var16 : var8);
                     case EJECTING -> Variant.variant().with(VariantProperties.MODEL, var9x ? var17 : var9);
                  };
               }))
         );
   }

   private void createSculkSensor() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_inactive");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_active");
      this.delegateItemModel(Blocks.SCULK_SENSOR, var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SCULK_SENSOR)
               .with(
                  PropertyDispatch.property(BlockStateProperties.SCULK_SENSOR_PHASE)
                     .generate(
                        var2x -> Variant.variant()
                              .with(VariantProperties.MODEL, var2x != SculkSensorPhase.ACTIVE && var2x != SculkSensorPhase.COOLDOWN ? var1 : var2)
                     )
               )
         );
   }

   private void createCalibratedSculkSensor() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.CALIBRATED_SCULK_SENSOR, "_inactive");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.CALIBRATED_SCULK_SENSOR, "_active");
      this.delegateItemModel(Blocks.CALIBRATED_SCULK_SENSOR, var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.CALIBRATED_SCULK_SENSOR)
               .with(
                  PropertyDispatch.property(BlockStateProperties.SCULK_SENSOR_PHASE)
                     .generate(
                        var2x -> Variant.variant()
                              .with(VariantProperties.MODEL, var2x != SculkSensorPhase.ACTIVE && var2x != SculkSensorPhase.COOLDOWN ? var1 : var2)
                     )
               )
               .with(createHorizontalFacingDispatch())
         );
   }

   private void createSculkShrieker() {
      ResourceLocation var1 = ModelTemplates.SCULK_SHRIEKER.create(Blocks.SCULK_SHRIEKER, TextureMapping.sculkShrieker(false), this.modelOutput);
      ResourceLocation var2 = ModelTemplates.SCULK_SHRIEKER
         .createWithSuffix(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMapping.sculkShrieker(true), this.modelOutput);
      this.delegateItemModel(Blocks.SCULK_SHRIEKER, var1);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.SCULK_SHRIEKER).with(createBooleanModelDispatch(BlockStateProperties.CAN_SUMMON, var2, var1)));
   }

   private void createScaffolding() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_stable");
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_unstable");
      this.delegateItemModel(Blocks.SCAFFOLDING, var1);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.SCAFFOLDING).with(createBooleanModelDispatch(BlockStateProperties.BOTTOM, var2, var1)));
   }

   private void createCaveVines() {
      ResourceLocation var1 = this.createSuffixedVariant(Blocks.CAVE_VINES, "", ModelTemplates.CROSS, TextureMapping::cross);
      ResourceLocation var2 = this.createSuffixedVariant(Blocks.CAVE_VINES, "_lit", ModelTemplates.CROSS, TextureMapping::cross);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.CAVE_VINES).with(createBooleanModelDispatch(BlockStateProperties.BERRIES, var2, var1)));
      ResourceLocation var3 = this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "", ModelTemplates.CROSS, TextureMapping::cross);
      ResourceLocation var4 = this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "_lit", ModelTemplates.CROSS, TextureMapping::cross);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.CAVE_VINES_PLANT).with(createBooleanModelDispatch(BlockStateProperties.BERRIES, var4, var3)));
   }

   private void createRedstoneLamp() {
      ResourceLocation var1 = TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, this.modelOutput);
      ResourceLocation var2 = this.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.REDSTONE_LAMP).with(createBooleanModelDispatch(BlockStateProperties.LIT, var2, var1)));
   }

   private void createNormalTorch(Block var1, Block var2) {
      TextureMapping var3 = TextureMapping.torch(var1);
      this.blockStateOutput.accept(createSimpleBlock(var1, ModelTemplates.TORCH.create(var1, var3, this.modelOutput)));
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(
                  var2, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.WALL_TORCH.create(var2, var3, this.modelOutput))
               )
               .with(createTorchHorizontalDispatch())
         );
      this.createSimpleFlatItemModel(var1);
      this.skipAutoItemBlock(var2);
   }

   private void createRedstoneTorch() {
      TextureMapping var1 = TextureMapping.torch(Blocks.REDSTONE_TORCH);
      TextureMapping var2 = TextureMapping.torch(TextureMapping.getBlockTexture(Blocks.REDSTONE_TORCH, "_off"));
      ResourceLocation var3 = ModelTemplates.TORCH.create(Blocks.REDSTONE_TORCH, var1, this.modelOutput);
      ResourceLocation var4 = ModelTemplates.TORCH.createWithSuffix(Blocks.REDSTONE_TORCH, "_off", var2, this.modelOutput);
      this.blockStateOutput
         .accept(MultiVariantGenerator.multiVariant(Blocks.REDSTONE_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, var3, var4)));
      ResourceLocation var5 = ModelTemplates.WALL_TORCH.create(Blocks.REDSTONE_WALL_TORCH, var1, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.WALL_TORCH.createWithSuffix(Blocks.REDSTONE_WALL_TORCH, "_off", var2, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.REDSTONE_WALL_TORCH)
               .with(createBooleanModelDispatch(BlockStateProperties.LIT, var5, var6))
               .with(createTorchHorizontalDispatch())
         );
      this.createSimpleFlatItemModel(Blocks.REDSTONE_TORCH);
      this.skipAutoItemBlock(Blocks.REDSTONE_WALL_TORCH);
   }

   private void createRepeater() {
      this.createSimpleFlatItemModel(Items.REPEATER);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.REPEATER)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.DELAY, BlockStateProperties.LOCKED, BlockStateProperties.POWERED)
                     .generate((var0, var1, var2) -> {
                        StringBuilder var3 = new StringBuilder();
                        var3.append('_').append(var0).append("tick");
                        if (var2) {
                           var3.append("_on");
                        }

                        if (var1) {
                           var3.append("_locked");
                        }

                        return Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.REPEATER, var3.toString()));
                     })
               )
               .with(createHorizontalFacingDispatchAlt())
         );
   }

   private void createSeaPickle() {
      this.createSimpleFlatItemModel(Items.SEA_PICKLE);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SEA_PICKLE)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.PICKLES, BlockStateProperties.WATERLOGGED)
                     .select(1, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("dead_sea_pickle"))))
                     .select(2, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("two_dead_sea_pickles"))))
                     .select(3, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("three_dead_sea_pickles"))))
                     .select(4, false, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("four_dead_sea_pickles"))))
                     .select(1, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("sea_pickle"))))
                     .select(2, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("two_sea_pickles"))))
                     .select(3, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("three_sea_pickles"))))
                     .select(4, true, Arrays.asList(createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("four_sea_pickles"))))
               )
         );
   }

   private void createSnowBlocks() {
      TextureMapping var1 = TextureMapping.cube(Blocks.SNOW);
      ResourceLocation var2 = ModelTemplates.CUBE_ALL.create(Blocks.SNOW_BLOCK, var1, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SNOW)
               .with(
                  PropertyDispatch.property(BlockStateProperties.LAYERS)
                     .generate(
                        var1x -> Variant.variant()
                              .with(VariantProperties.MODEL, var1x < 8 ? ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height" + var1x * 2) : var2)
                     )
               )
         );
      this.delegateItemModel(Blocks.SNOW, ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SNOW_BLOCK, var2));
   }

   private void createStonecutter() {
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(
                  Blocks.STONECUTTER, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.STONECUTTER))
               )
               .with(createHorizontalFacingDispatch())
         );
   }

   private void createStructureBlock() {
      ResourceLocation var1 = TexturedModel.CUBE.create(Blocks.STRUCTURE_BLOCK, this.modelOutput);
      this.delegateItemModel(Blocks.STRUCTURE_BLOCK, var1);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.STRUCTURE_BLOCK)
               .with(
                  PropertyDispatch.property(BlockStateProperties.STRUCTUREBLOCK_MODE)
                     .generate(
                        var1x -> Variant.variant()
                              .with(
                                 VariantProperties.MODEL,
                                 this.createSuffixedVariant(
                                    Blocks.STRUCTURE_BLOCK, "_" + var1x.getSerializedName(), ModelTemplates.CUBE_ALL, TextureMapping::cube
                                 )
                              )
                     )
               )
         );
   }

   private void createSweetBerryBush() {
      this.createSimpleFlatItemModel(Items.SWEET_BERRIES);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SWEET_BERRY_BUSH)
               .with(
                  PropertyDispatch.property(BlockStateProperties.AGE_3)
                     .generate(
                        var1 -> Variant.variant()
                              .with(
                                 VariantProperties.MODEL,
                                 this.createSuffixedVariant(Blocks.SWEET_BERRY_BUSH, "_stage" + var1, ModelTemplates.CROSS, TextureMapping::cross)
                              )
                     )
               )
         );
   }

   private void createTripwire() {
      this.createSimpleFlatItemModel(Items.STRING);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.TRIPWIRE)
               .with(
                  PropertyDispatch.properties(
                        BlockStateProperties.ATTACHED,
                        BlockStateProperties.EAST,
                        BlockStateProperties.NORTH,
                        BlockStateProperties.SOUTH,
                        BlockStateProperties.WEST
                     )
                     .select(
                        false,
                        false,
                        false,
                        false,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))
                     )
                     .select(
                        false,
                        true,
                        false,
                        false,
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        false,
                        false,
                        true,
                        false,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))
                     )
                     .select(
                        false,
                        false,
                        false,
                        true,
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        false,
                        false,
                        false,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        false,
                        true,
                        true,
                        false,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))
                     )
                     .select(
                        false,
                        true,
                        false,
                        true,
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        false,
                        false,
                        false,
                        true,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        false,
                        false,
                        true,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        false,
                        false,
                        true,
                        true,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))
                     )
                     .select(
                        false,
                        true,
                        false,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        false,
                        true,
                        true,
                        true,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))
                     )
                     .select(
                        false,
                        true,
                        false,
                        true,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        false,
                        false,
                        true,
                        true,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        false,
                        true,
                        true,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        false,
                        true,
                        true,
                        true,
                        true,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nsew"))
                     )
                     .select(
                        true,
                        false,
                        false,
                        false,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))
                     )
                     .select(
                        true,
                        false,
                        true,
                        false,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))
                     )
                     .select(
                        true,
                        false,
                        false,
                        true,
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        true,
                        true,
                        false,
                        false,
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        true,
                        false,
                        false,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        true,
                        true,
                        true,
                        false,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))
                     )
                     .select(
                        true,
                        true,
                        false,
                        true,
                        false,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        true,
                        false,
                        false,
                        true,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        true,
                        false,
                        true,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        true,
                        false,
                        true,
                        true,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))
                     )
                     .select(
                        true,
                        true,
                        false,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        true,
                        true,
                        true,
                        true,
                        false,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))
                     )
                     .select(
                        true,
                        true,
                        false,
                        true,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                     )
                     .select(
                        true,
                        false,
                        true,
                        true,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                     )
                     .select(
                        true,
                        true,
                        true,
                        false,
                        true,
                        Variant.variant()
                           .with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))
                           .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                     )
                     .select(
                        true,
                        true,
                        true,
                        true,
                        true,
                        Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nsew"))
                     )
               )
         );
   }

   private void createTripwireHook() {
      this.createSimpleFlatItemModel(Blocks.TRIPWIRE_HOOK);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.TRIPWIRE_HOOK)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.ATTACHED, BlockStateProperties.POWERED)
                     .generate(
                        (var0, var1) -> Variant.variant()
                              .with(
                                 VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.TRIPWIRE_HOOK, (var0 ? "_attached" : "") + (var1 ? "_on" : ""))
                              )
                     )
               )
               .with(createHorizontalFacingDispatch())
         );
   }

   private ResourceLocation createTurtleEggModel(int var1, String var2, TextureMapping var3) {
      switch (var1) {
         case 1:
            return ModelTemplates.TURTLE_EGG.create(ModelLocationUtils.decorateBlockModelLocation(var2 + "turtle_egg"), var3, this.modelOutput);
         case 2:
            return ModelTemplates.TWO_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("two_" + var2 + "turtle_eggs"), var3, this.modelOutput);
         case 3:
            return ModelTemplates.THREE_TURTLE_EGGS
               .create(ModelLocationUtils.decorateBlockModelLocation("three_" + var2 + "turtle_eggs"), var3, this.modelOutput);
         case 4:
            return ModelTemplates.FOUR_TURTLE_EGGS
               .create(ModelLocationUtils.decorateBlockModelLocation("four_" + var2 + "turtle_eggs"), var3, this.modelOutput);
         default:
            throw new UnsupportedOperationException();
      }
   }

   private ResourceLocation createTurtleEggModel(Integer var1, Integer var2) {
      switch (var2) {
         case 0:
            return this.createTurtleEggModel(var1, "", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG)));
         case 1:
            return this.createTurtleEggModel(
               var1, "slightly_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_slightly_cracked"))
            );
         case 2:
            return this.createTurtleEggModel(var1, "very_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_very_cracked")));
         default:
            throw new UnsupportedOperationException();
      }
   }

   private void createTurtleEgg() {
      this.createSimpleFlatItemModel(Items.TURTLE_EGG);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.TURTLE_EGG)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.EGGS, BlockStateProperties.HATCH)
                     .generateList((var1, var2) -> Arrays.asList(createRotatedVariants(this.createTurtleEggModel(var1, var2))))
               )
         );
   }

   private void createSnifferEgg() {
      this.createSimpleFlatItemModel(Items.SNIFFER_EGG);
      Function var1 = var1x -> {
         String var2 = switch (var1x) {
            case 1 -> "_slightly_cracked";
            case 2 -> "_very_cracked";
            default -> "_not_cracked";
         };
         TextureMapping var3 = TextureMapping.snifferEgg(var2);
         return ModelTemplates.SNIFFER_EGG.createWithSuffix(Blocks.SNIFFER_EGG, var2, var3, this.modelOutput);
      };
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SNIFFER_EGG)
               .with(
                  PropertyDispatch.property(SnifferEggBlock.HATCH)
                     .generate(var1x -> Variant.variant().with(VariantProperties.MODEL, (ResourceLocation)var1.apply(var1x)))
               )
         );
   }

   private void createMultiface(Block var1) {
      this.createSimpleFlatItemModel(var1);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(var1);
      MultiPartGenerator var3 = MultiPartGenerator.multiPart(var1);
      Condition.TerminalCondition var4 = Util.make(Condition.condition(), var1x -> MULTIFACE_GENERATOR.stream().map(Pair::getFirst).forEach(var2x -> {
            if (var1.defaultBlockState().hasProperty(var2x)) {
               var1x.term(var2x, false);
            }
         }));

      for (Pair var6 : MULTIFACE_GENERATOR) {
         BooleanProperty var7 = (BooleanProperty)var6.getFirst();
         Function var8 = (Function)var6.getSecond();
         if (var1.defaultBlockState().hasProperty(var7)) {
            var3.with(Condition.condition().term(var7, true), (Variant)var8.apply(var2));
            var3.with(var4, (Variant)var8.apply(var2));
         }
      }

      this.blockStateOutput.accept(var3);
   }

   private void createSculkCatalyst() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_bottom");
      TextureMapping var2 = new TextureMapping()
         .put(TextureSlot.BOTTOM, var1)
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top"))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side"));
      TextureMapping var3 = new TextureMapping()
         .put(TextureSlot.BOTTOM, var1)
         .put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top_bloom"))
         .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side_bloom"));
      ResourceLocation var4 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.SCULK_CATALYST, "", var2, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.SCULK_CATALYST, "_bloom", var3, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.SCULK_CATALYST)
               .with(
                  PropertyDispatch.property(BlockStateProperties.BLOOM).generate(var2x -> Variant.variant().with(VariantProperties.MODEL, var2x ? var5 : var4))
               )
         );
      this.delegateItemModel(Items.SCULK_CATALYST, var4);
   }

   private void createChiseledBookshelf() {
      Block var1 = Blocks.CHISELED_BOOKSHELF;
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(var1);
      MultiPartGenerator var3 = MultiPartGenerator.multiPart(var1);
      List.of(
            Pair.of(Direction.NORTH, VariantProperties.Rotation.R0),
            Pair.of(Direction.EAST, VariantProperties.Rotation.R90),
            Pair.of(Direction.SOUTH, VariantProperties.Rotation.R180),
            Pair.of(Direction.WEST, VariantProperties.Rotation.R270)
         )
         .forEach(var3x -> {
            Direction var4 = (Direction)var3x.getFirst();
            VariantProperties.Rotation var5 = (VariantProperties.Rotation)var3x.getSecond();
            Condition.TerminalCondition var6 = Condition.condition().term(BlockStateProperties.HORIZONTAL_FACING, var4);
            var3.with(var6, Variant.variant().with(VariantProperties.MODEL, var2).with(VariantProperties.Y_ROT, var5).with(VariantProperties.UV_LOCK, true));
            this.addSlotStateAndRotationVariants(var3, var6, var5);
         });
      this.blockStateOutput.accept(var3);
      this.delegateItemModel(var1, ModelLocationUtils.getModelLocation(var1, "_inventory"));
      CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.clear();
   }

   private void addSlotStateAndRotationVariants(MultiPartGenerator var1, Condition.TerminalCondition var2, VariantProperties.Rotation var3) {
      List.of(
            Pair.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_LEFT),
            Pair.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_MID),
            Pair.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_RIGHT),
            Pair.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT),
            Pair.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_MID),
            Pair.of(BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT)
         )
         .forEach(var4 -> {
            BooleanProperty var5 = (BooleanProperty)var4.getFirst();
            ModelTemplate var6 = (ModelTemplate)var4.getSecond();
            this.addBookSlotModel(var1, var2, var3, var5, var6, true);
            this.addBookSlotModel(var1, var2, var3, var5, var6, false);
         });
   }

   private void addBookSlotModel(
      MultiPartGenerator var1, Condition.TerminalCondition var2, VariantProperties.Rotation var3, BooleanProperty var4, ModelTemplate var5, boolean var6
   ) {
      String var7 = var6 ? "_occupied" : "_empty";
      TextureMapping var8 = new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(Blocks.CHISELED_BOOKSHELF, var7));
      BlockModelGenerators.BookSlotModelCacheKey var9 = new BlockModelGenerators.BookSlotModelCacheKey(var5, var7);
      ResourceLocation var10 = CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.computeIfAbsent(
         var9, var4x -> var5.createWithSuffix(Blocks.CHISELED_BOOKSHELF, var7, var8, this.modelOutput)
      );
      var1.with(
         Condition.and(var2, Condition.condition().term(var4, var6)),
         Variant.variant().with(VariantProperties.MODEL, var10).with(VariantProperties.Y_ROT, var3)
      );
   }

   private void createMagmaBlock() {
      this.blockStateOutput
         .accept(
            createSimpleBlock(
               Blocks.MAGMA_BLOCK,
               ModelTemplates.CUBE_ALL
                  .create(Blocks.MAGMA_BLOCK, TextureMapping.cube(ModelLocationUtils.decorateBlockModelLocation("magma")), this.modelOutput)
            )
         );
   }

   private void createShulkerBox(Block var1) {
      this.createTrivialBlock(var1, TexturedModel.PARTICLE_ONLY);
      ModelTemplates.SHULKER_BOX_INVENTORY.create(ModelLocationUtils.getModelLocation(var1.asItem()), TextureMapping.particle(var1), this.modelOutput);
   }

   private void createGrowingPlant(Block var1, Block var2, BlockModelGenerators.TintState var3) {
      this.createCrossBlock(var1, var3);
      this.createCrossBlock(var2, var3);
   }

   private void createBedItem(Block var1, Block var2) {
      ModelTemplates.BED_INVENTORY.create(ModelLocationUtils.getModelLocation(var1.asItem()), TextureMapping.particle(var2), this.modelOutput);
   }

   private void createInfestedStone() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.STONE);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.STONE, "_mirrored");
      this.blockStateOutput.accept(createRotatedVariant(Blocks.INFESTED_STONE, var1, var2));
      this.delegateItemModel(Blocks.INFESTED_STONE, var1);
   }

   private void createInfestedDeepslate() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE);
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE, "_mirrored");
      this.blockStateOutput.accept(createRotatedVariant(Blocks.INFESTED_DEEPSLATE, var1, var2).with(createRotatedPillar()));
      this.delegateItemModel(Blocks.INFESTED_DEEPSLATE, var1);
   }

   private void createNetherRoots(Block var1, Block var2) {
      this.createCrossBlockWithDefaultItem(var1, BlockModelGenerators.TintState.NOT_TINTED);
      TextureMapping var3 = TextureMapping.plant(TextureMapping.getBlockTexture(var1, "_pot"));
      ResourceLocation var4 = BlockModelGenerators.TintState.NOT_TINTED.getCrossPot().create(var2, var3, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var2, var4));
   }

   private void createRespawnAnchor() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_bottom");
      ResourceLocation var2 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top_off");
      ResourceLocation var3 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top");
      ResourceLocation[] var4 = new ResourceLocation[5];

      for (int var5 = 0; var5 < 5; var5++) {
         TextureMapping var6 = new TextureMapping()
            .put(TextureSlot.BOTTOM, var1)
            .put(TextureSlot.TOP, var5 == 0 ? var2 : var3)
            .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_side" + var5));
         var4[var5] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.RESPAWN_ANCHOR, "_" + var5, var6, this.modelOutput);
      }

      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.RESPAWN_ANCHOR)
               .with(
                  PropertyDispatch.property(BlockStateProperties.RESPAWN_ANCHOR_CHARGES)
                     .generate(var1x -> Variant.variant().with(VariantProperties.MODEL, var4[var1x]))
               )
         );
      this.delegateItemModel(Items.RESPAWN_ANCHOR, var4[0]);
   }

   private Variant applyRotation(FrontAndTop var1, Variant var2) {
      switch (var1) {
         case DOWN_NORTH:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90);
         case DOWN_SOUTH:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
         case DOWN_WEST:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
         case DOWN_EAST:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
         case UP_NORTH:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
         case UP_SOUTH:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270);
         case UP_WEST:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
         case UP_EAST:
            return var2.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
         case NORTH_UP:
            return var2;
         case SOUTH_UP:
            return var2.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
         case WEST_UP:
            return var2.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
         case EAST_UP:
            return var2.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
         default:
            throw new UnsupportedOperationException("Rotation " + var1 + " can't be expressed with existing x and y values");
      }
   }

   private void createJigsaw() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_top");
      ResourceLocation var2 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_bottom");
      ResourceLocation var3 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_side");
      ResourceLocation var4 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_lock");
      TextureMapping var5 = new TextureMapping()
         .put(TextureSlot.DOWN, var3)
         .put(TextureSlot.WEST, var3)
         .put(TextureSlot.EAST, var3)
         .put(TextureSlot.PARTICLE, var1)
         .put(TextureSlot.NORTH, var1)
         .put(TextureSlot.SOUTH, var2)
         .put(TextureSlot.UP, var4);
      ResourceLocation var6 = ModelTemplates.CUBE_DIRECTIONAL.create(Blocks.JIGSAW, var5, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(Blocks.JIGSAW, Variant.variant().with(VariantProperties.MODEL, var6))
               .with(PropertyDispatch.property(BlockStateProperties.ORIENTATION).generate(var1x -> this.applyRotation(var1x, Variant.variant())))
         );
   }

   private void createPetrifiedOakSlab() {
      Block var1 = Blocks.OAK_PLANKS;
      ResourceLocation var2 = ModelLocationUtils.getModelLocation(var1);
      TexturedModel var3 = TexturedModel.CUBE.get(var1);
      Block var4 = Blocks.PETRIFIED_OAK_SLAB;
      ResourceLocation var5 = ModelTemplates.SLAB_BOTTOM.create(var4, var3.getMapping(), this.modelOutput);
      ResourceLocation var6 = ModelTemplates.SLAB_TOP.create(var4, var3.getMapping(), this.modelOutput);
      this.blockStateOutput.accept(createSlab(var4, var5, var6, var2));
   }

   public void run() {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach(var1 -> this.family(var1.getBaseBlock()).generateFor(var1));
      this.family(Blocks.CUT_COPPER)
         .generateFor(BlockFamilies.CUT_COPPER)
         .donateModelTo(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER)
         .donateModelTo(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER)
         .generateFor(BlockFamilies.WAXED_CUT_COPPER);
      this.family(Blocks.EXPOSED_CUT_COPPER)
         .generateFor(BlockFamilies.EXPOSED_CUT_COPPER)
         .donateModelTo(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
         .donateModelTo(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER)
         .generateFor(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
      this.family(Blocks.WEATHERED_CUT_COPPER)
         .generateFor(BlockFamilies.WEATHERED_CUT_COPPER)
         .donateModelTo(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
         .donateModelTo(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER)
         .generateFor(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
      this.family(Blocks.OXIDIZED_CUT_COPPER)
         .generateFor(BlockFamilies.OXIDIZED_CUT_COPPER)
         .donateModelTo(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER)
         .donateModelTo(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER)
         .generateFor(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
      this.createCopperBulb(Blocks.COPPER_BULB);
      this.createCopperBulb(Blocks.EXPOSED_COPPER_BULB);
      this.createCopperBulb(Blocks.WEATHERED_COPPER_BULB);
      this.createCopperBulb(Blocks.OXIDIZED_COPPER_BULB);
      this.copyCopperBulbModel(Blocks.COPPER_BULB, Blocks.WAXED_COPPER_BULB);
      this.copyCopperBulbModel(Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB);
      this.copyCopperBulbModel(Blocks.WEATHERED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB);
      this.copyCopperBulbModel(Blocks.OXIDIZED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB);
      this.createNonTemplateModelBlock(Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.CAVE_AIR, Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.VOID_AIR, Blocks.AIR);
      this.createNonTemplateModelBlock(Blocks.BEACON);
      this.createNonTemplateModelBlock(Blocks.CACTUS);
      this.createNonTemplateModelBlock(Blocks.BUBBLE_COLUMN, Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.DRAGON_EGG);
      this.createNonTemplateModelBlock(Blocks.DRIED_KELP_BLOCK);
      this.createNonTemplateModelBlock(Blocks.ENCHANTING_TABLE);
      this.createNonTemplateModelBlock(Blocks.FLOWER_POT);
      this.createSimpleFlatItemModel(Items.FLOWER_POT);
      this.createNonTemplateModelBlock(Blocks.HONEY_BLOCK);
      this.createNonTemplateModelBlock(Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.LAVA);
      this.createNonTemplateModelBlock(Blocks.SLIME_BLOCK);
      this.createSimpleFlatItemModel(Items.CHAIN);
      this.createCandleAndCandleCake(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
      this.createCandleAndCandleCake(Blocks.CANDLE, Blocks.CANDLE_CAKE);
      this.createNonTemplateModelBlock(Blocks.POTTED_BAMBOO);
      this.createNonTemplateModelBlock(Blocks.POTTED_CACTUS);
      this.createNonTemplateModelBlock(Blocks.POWDER_SNOW);
      this.createNonTemplateModelBlock(Blocks.SPORE_BLOSSOM);
      this.createAzalea(Blocks.AZALEA);
      this.createAzalea(Blocks.FLOWERING_AZALEA);
      this.createPottedAzalea(Blocks.POTTED_AZALEA);
      this.createPottedAzalea(Blocks.POTTED_FLOWERING_AZALEA);
      this.createCaveVines();
      this.createFullAndCarpetBlocks(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
      this.createFlowerBed(Blocks.PINK_PETALS);
      this.createAirLikeBlock(Blocks.BARRIER, Items.BARRIER);
      this.createSimpleFlatItemModel(Items.BARRIER);
      this.createLightBlock();
      this.createAirLikeBlock(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
      this.createSimpleFlatItemModel(Items.STRUCTURE_VOID);
      this.createAirLikeBlock(Blocks.MOVING_PISTON, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      this.createTrivialCube(Blocks.COAL_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_COAL_ORE);
      this.createTrivialCube(Blocks.COAL_BLOCK);
      this.createTrivialCube(Blocks.DIAMOND_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_DIAMOND_ORE);
      this.createTrivialCube(Blocks.DIAMOND_BLOCK);
      this.createTrivialCube(Blocks.EMERALD_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_EMERALD_ORE);
      this.createTrivialCube(Blocks.EMERALD_BLOCK);
      this.createTrivialCube(Blocks.GOLD_ORE);
      this.createTrivialCube(Blocks.NETHER_GOLD_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_GOLD_ORE);
      this.createTrivialCube(Blocks.GOLD_BLOCK);
      this.createTrivialCube(Blocks.IRON_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_IRON_ORE);
      this.createTrivialCube(Blocks.IRON_BLOCK);
      this.createTrivialBlock(Blocks.ANCIENT_DEBRIS, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.NETHERITE_BLOCK);
      this.createTrivialCube(Blocks.LAPIS_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_LAPIS_ORE);
      this.createTrivialCube(Blocks.LAPIS_BLOCK);
      this.createTrivialCube(Blocks.NETHER_QUARTZ_ORE);
      this.createTrivialCube(Blocks.REDSTONE_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_REDSTONE_ORE);
      this.createTrivialCube(Blocks.REDSTONE_BLOCK);
      this.createTrivialCube(Blocks.GILDED_BLACKSTONE);
      this.createTrivialCube(Blocks.BLUE_ICE);
      this.createTrivialCube(Blocks.CLAY);
      this.createTrivialCube(Blocks.COARSE_DIRT);
      this.createTrivialCube(Blocks.CRYING_OBSIDIAN);
      this.createTrivialCube(Blocks.END_STONE);
      this.createTrivialCube(Blocks.GLOWSTONE);
      this.createTrivialCube(Blocks.GRAVEL);
      this.createTrivialCube(Blocks.HONEYCOMB_BLOCK);
      this.createTrivialCube(Blocks.ICE);
      this.createTrivialBlock(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
      this.createTrivialBlock(Blocks.LODESTONE, TexturedModel.COLUMN);
      this.createTrivialBlock(Blocks.MELON, TexturedModel.COLUMN);
      this.createNonTemplateModelBlock(Blocks.MANGROVE_ROOTS);
      this.createNonTemplateModelBlock(Blocks.POTTED_MANGROVE_PROPAGULE);
      this.createTrivialCube(Blocks.NETHER_WART_BLOCK);
      this.createTrivialCube(Blocks.NOTE_BLOCK);
      this.createTrivialCube(Blocks.PACKED_ICE);
      this.createTrivialCube(Blocks.OBSIDIAN);
      this.createTrivialCube(Blocks.QUARTZ_BRICKS);
      this.createTrivialCube(Blocks.SEA_LANTERN);
      this.createTrivialCube(Blocks.SHROOMLIGHT);
      this.createTrivialCube(Blocks.SOUL_SAND);
      this.createTrivialCube(Blocks.SOUL_SOIL);
      this.createTrivialBlock(Blocks.SPAWNER, TexturedModel.CUBE_INNER_FACES);
      this.createTrivialCube(Blocks.SPONGE);
      this.createTrivialBlock(Blocks.SEAGRASS, TexturedModel.SEAGRASS);
      this.createSimpleFlatItemModel(Items.SEAGRASS);
      this.createTrivialBlock(Blocks.TNT, TexturedModel.CUBE_TOP_BOTTOM);
      this.createTrivialBlock(Blocks.TARGET, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.WARPED_WART_BLOCK);
      this.createTrivialCube(Blocks.WET_SPONGE);
      this.createTrivialCube(Blocks.AMETHYST_BLOCK);
      this.createTrivialCube(Blocks.BUDDING_AMETHYST);
      this.createTrivialCube(Blocks.CALCITE);
      this.createTrivialCube(Blocks.DRIPSTONE_BLOCK);
      this.createTrivialCube(Blocks.RAW_IRON_BLOCK);
      this.createTrivialCube(Blocks.RAW_COPPER_BLOCK);
      this.createTrivialCube(Blocks.RAW_GOLD_BLOCK);
      this.createRotatedMirroredVariantBlock(Blocks.SCULK);
      this.createNonTemplateModelBlock(Blocks.HEAVY_CORE);
      this.createPetrifiedOakSlab();
      this.createTrivialCube(Blocks.COPPER_ORE);
      this.createTrivialCube(Blocks.DEEPSLATE_COPPER_ORE);
      this.createTrivialCube(Blocks.COPPER_BLOCK);
      this.createTrivialCube(Blocks.EXPOSED_COPPER);
      this.createTrivialCube(Blocks.WEATHERED_COPPER);
      this.createTrivialCube(Blocks.OXIDIZED_COPPER);
      this.copyModel(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
      this.copyModel(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
      this.copyModel(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
      this.copyModel(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
      this.createDoor(Blocks.COPPER_DOOR);
      this.createDoor(Blocks.EXPOSED_COPPER_DOOR);
      this.createDoor(Blocks.WEATHERED_COPPER_DOOR);
      this.createDoor(Blocks.OXIDIZED_COPPER_DOOR);
      this.copyDoorModel(Blocks.COPPER_DOOR, Blocks.WAXED_COPPER_DOOR);
      this.copyDoorModel(Blocks.EXPOSED_COPPER_DOOR, Blocks.WAXED_EXPOSED_COPPER_DOOR);
      this.copyDoorModel(Blocks.WEATHERED_COPPER_DOOR, Blocks.WAXED_WEATHERED_COPPER_DOOR);
      this.copyDoorModel(Blocks.OXIDIZED_COPPER_DOOR, Blocks.WAXED_OXIDIZED_COPPER_DOOR);
      this.createTrapdoor(Blocks.COPPER_TRAPDOOR);
      this.createTrapdoor(Blocks.EXPOSED_COPPER_TRAPDOOR);
      this.createTrapdoor(Blocks.WEATHERED_COPPER_TRAPDOOR);
      this.createTrapdoor(Blocks.OXIDIZED_COPPER_TRAPDOOR);
      this.copyTrapdoorModel(Blocks.COPPER_TRAPDOOR, Blocks.WAXED_COPPER_TRAPDOOR);
      this.copyTrapdoorModel(Blocks.EXPOSED_COPPER_TRAPDOOR, Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
      this.copyTrapdoorModel(Blocks.WEATHERED_COPPER_TRAPDOOR, Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
      this.copyTrapdoorModel(Blocks.OXIDIZED_COPPER_TRAPDOOR, Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
      this.createTrivialCube(Blocks.COPPER_GRATE);
      this.createTrivialCube(Blocks.EXPOSED_COPPER_GRATE);
      this.createTrivialCube(Blocks.WEATHERED_COPPER_GRATE);
      this.createTrivialCube(Blocks.OXIDIZED_COPPER_GRATE);
      this.copyModel(Blocks.COPPER_GRATE, Blocks.WAXED_COPPER_GRATE);
      this.copyModel(Blocks.EXPOSED_COPPER_GRATE, Blocks.WAXED_EXPOSED_COPPER_GRATE);
      this.copyModel(Blocks.WEATHERED_COPPER_GRATE, Blocks.WAXED_WEATHERED_COPPER_GRATE);
      this.copyModel(Blocks.OXIDIZED_COPPER_GRATE, Blocks.WAXED_OXIDIZED_COPPER_GRATE);
      this.createWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
      this.createWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
      this.createAmethystClusters();
      this.createBookshelf();
      this.createChiseledBookshelf();
      this.createBrewingStand();
      this.createCakeBlock();
      this.createCampfires(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
      this.createCartographyTable();
      this.createCauldrons();
      this.createChorusFlower();
      this.createChorusPlant();
      this.createComposter();
      this.createDaylightDetector();
      this.createEndPortalFrame();
      this.createRotatableColumn(Blocks.END_ROD);
      this.createLightningRod();
      this.createFarmland();
      this.createFire();
      this.createSoulFire();
      this.createFrostedIce();
      this.createGrassBlocks();
      this.createCocoa();
      this.createDirtPath();
      this.createGrindstone();
      this.createHopper();
      this.createIronBars();
      this.createLever();
      this.createLilyPad();
      this.createNetherPortalBlock();
      this.createNetherrack();
      this.createObserver();
      this.createPistons();
      this.createPistonHeads();
      this.createScaffolding();
      this.createRedstoneTorch();
      this.createRedstoneLamp();
      this.createRepeater();
      this.createSeaPickle();
      this.createSmithingTable();
      this.createSnowBlocks();
      this.createStonecutter();
      this.createStructureBlock();
      this.createSweetBerryBush();
      this.createTripwire();
      this.createTripwireHook();
      this.createTurtleEgg();
      this.createSnifferEgg();
      this.createMultiface(Blocks.VINE);
      this.createMultiface(Blocks.GLOW_LICHEN);
      this.createMultiface(Blocks.SCULK_VEIN);
      this.createMagmaBlock();
      this.createJigsaw();
      this.createSculkSensor();
      this.createCalibratedSculkSensor();
      this.createSculkShrieker();
      this.createFrogspawnBlock();
      this.createMangrovePropagule();
      this.createMuddyMangroveRoots();
      this.createTrialSpawner();
      this.createVault();
      this.createNonTemplateHorizontalBlock(Blocks.LADDER);
      this.createSimpleFlatItemModel(Blocks.LADDER);
      this.createNonTemplateHorizontalBlock(Blocks.LECTERN);
      this.createBigDripLeafBlock();
      this.createNonTemplateHorizontalBlock(Blocks.BIG_DRIPLEAF_STEM);
      this.createNormalTorch(Blocks.TORCH, Blocks.WALL_TORCH);
      this.createNormalTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
      this.createCraftingTableLike(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMapping::craftingTable);
      this.createCraftingTableLike(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMapping::fletchingTable);
      this.createNyliumBlock(Blocks.CRIMSON_NYLIUM);
      this.createNyliumBlock(Blocks.WARPED_NYLIUM);
      this.createDispenserBlock(Blocks.DISPENSER);
      this.createDispenserBlock(Blocks.DROPPER);
      this.createCrafterBlock();
      this.createLantern(Blocks.LANTERN);
      this.createLantern(Blocks.SOUL_LANTERN);
      this.createAxisAlignedPillarBlockCustomModel(Blocks.CHAIN, ModelLocationUtils.getModelLocation(Blocks.CHAIN));
      this.createAxisAlignedPillarBlock(Blocks.BASALT, TexturedModel.COLUMN);
      this.createAxisAlignedPillarBlock(Blocks.POLISHED_BASALT, TexturedModel.COLUMN);
      this.createTrivialCube(Blocks.SMOOTH_BASALT);
      this.createAxisAlignedPillarBlock(Blocks.BONE_BLOCK, TexturedModel.COLUMN);
      this.createRotatedVariantBlock(Blocks.DIRT);
      this.createRotatedVariantBlock(Blocks.ROOTED_DIRT);
      this.createRotatedVariantBlock(Blocks.SAND);
      this.createBrushableBlock(Blocks.SUSPICIOUS_SAND);
      this.createBrushableBlock(Blocks.SUSPICIOUS_GRAVEL);
      this.createRotatedVariantBlock(Blocks.RED_SAND);
      this.createRotatedMirroredVariantBlock(Blocks.BEDROCK);
      this.createTrivialBlock(Blocks.REINFORCED_DEEPSLATE, TexturedModel.CUBE_TOP_BOTTOM);
      this.createRotatedPillarWithHorizontalVariant(Blocks.HAY_BLOCK, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
      this.createRotatedPillarWithHorizontalVariant(Blocks.PURPUR_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
      this.createRotatedPillarWithHorizontalVariant(Blocks.QUARTZ_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
      this.createRotatedPillarWithHorizontalVariant(Blocks.OCHRE_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
      this.createRotatedPillarWithHorizontalVariant(Blocks.VERDANT_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
      this.createRotatedPillarWithHorizontalVariant(Blocks.PEARLESCENT_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
      this.createHorizontallyRotatedBlock(Blocks.LOOM, TexturedModel.ORIENTABLE);
      this.createPumpkins();
      this.createBeeNest(Blocks.BEE_NEST, TextureMapping::orientableCube);
      this.createBeeNest(Blocks.BEEHIVE, TextureMapping::orientableCubeSameEnds);
      this.createCropBlock(Blocks.BEETROOTS, BlockStateProperties.AGE_3, 0, 1, 2, 3);
      this.createCropBlock(Blocks.CARROTS, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
      this.createCropBlock(Blocks.NETHER_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
      this.createCropBlock(Blocks.POTATOES, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
      this.createCropBlock(Blocks.WHEAT, BlockStateProperties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
      this.createCrossBlock(Blocks.TORCHFLOWER_CROP, BlockModelGenerators.TintState.NOT_TINTED, BlockStateProperties.AGE_1, 0, 1);
      this.createPitcherCrop();
      this.createPitcherPlant();
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("decorated_pot"), Blocks.TERRACOTTA).createWithoutBlockItem(Blocks.DECORATED_POT);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("banner"), Blocks.OAK_PLANKS)
         .createWithCustomBlockItemModel(
            ModelTemplates.BANNER_INVENTORY,
            Blocks.WHITE_BANNER,
            Blocks.ORANGE_BANNER,
            Blocks.MAGENTA_BANNER,
            Blocks.LIGHT_BLUE_BANNER,
            Blocks.YELLOW_BANNER,
            Blocks.LIME_BANNER,
            Blocks.PINK_BANNER,
            Blocks.GRAY_BANNER,
            Blocks.LIGHT_GRAY_BANNER,
            Blocks.CYAN_BANNER,
            Blocks.PURPLE_BANNER,
            Blocks.BLUE_BANNER,
            Blocks.BROWN_BANNER,
            Blocks.GREEN_BANNER,
            Blocks.RED_BANNER,
            Blocks.BLACK_BANNER
         )
         .createWithoutBlockItem(
            Blocks.WHITE_WALL_BANNER,
            Blocks.ORANGE_WALL_BANNER,
            Blocks.MAGENTA_WALL_BANNER,
            Blocks.LIGHT_BLUE_WALL_BANNER,
            Blocks.YELLOW_WALL_BANNER,
            Blocks.LIME_WALL_BANNER,
            Blocks.PINK_WALL_BANNER,
            Blocks.GRAY_WALL_BANNER,
            Blocks.LIGHT_GRAY_WALL_BANNER,
            Blocks.CYAN_WALL_BANNER,
            Blocks.PURPLE_WALL_BANNER,
            Blocks.BLUE_WALL_BANNER,
            Blocks.BROWN_WALL_BANNER,
            Blocks.GREEN_WALL_BANNER,
            Blocks.RED_WALL_BANNER,
            Blocks.BLACK_WALL_BANNER
         );
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("bed"), Blocks.OAK_PLANKS)
         .createWithoutBlockItem(
            Blocks.WHITE_BED,
            Blocks.ORANGE_BED,
            Blocks.MAGENTA_BED,
            Blocks.LIGHT_BLUE_BED,
            Blocks.YELLOW_BED,
            Blocks.LIME_BED,
            Blocks.PINK_BED,
            Blocks.GRAY_BED,
            Blocks.LIGHT_GRAY_BED,
            Blocks.CYAN_BED,
            Blocks.PURPLE_BED,
            Blocks.BLUE_BED,
            Blocks.BROWN_BED,
            Blocks.GREEN_BED,
            Blocks.RED_BED,
            Blocks.BLACK_BED
         );
      this.createBedItem(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
      this.createBedItem(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
      this.createBedItem(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
      this.createBedItem(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
      this.createBedItem(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
      this.createBedItem(Blocks.LIME_BED, Blocks.LIME_WOOL);
      this.createBedItem(Blocks.PINK_BED, Blocks.PINK_WOOL);
      this.createBedItem(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
      this.createBedItem(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
      this.createBedItem(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
      this.createBedItem(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
      this.createBedItem(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
      this.createBedItem(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
      this.createBedItem(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
      this.createBedItem(Blocks.RED_BED, Blocks.RED_WOOL);
      this.createBedItem(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("skull"), Blocks.SOUL_SAND)
         .createWithCustomBlockItemModel(
            ModelTemplates.SKULL_INVENTORY,
            Blocks.CREEPER_HEAD,
            Blocks.PLAYER_HEAD,
            Blocks.ZOMBIE_HEAD,
            Blocks.SKELETON_SKULL,
            Blocks.WITHER_SKELETON_SKULL,
            Blocks.PIGLIN_HEAD
         )
         .create(Blocks.DRAGON_HEAD)
         .createWithoutBlockItem(
            Blocks.CREEPER_WALL_HEAD,
            Blocks.DRAGON_WALL_HEAD,
            Blocks.PLAYER_WALL_HEAD,
            Blocks.ZOMBIE_WALL_HEAD,
            Blocks.SKELETON_WALL_SKULL,
            Blocks.WITHER_SKELETON_WALL_SKULL,
            Blocks.PIGLIN_WALL_HEAD
         );
      this.createShulkerBox(Blocks.SHULKER_BOX);
      this.createShulkerBox(Blocks.WHITE_SHULKER_BOX);
      this.createShulkerBox(Blocks.ORANGE_SHULKER_BOX);
      this.createShulkerBox(Blocks.MAGENTA_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX);
      this.createShulkerBox(Blocks.YELLOW_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIME_SHULKER_BOX);
      this.createShulkerBox(Blocks.PINK_SHULKER_BOX);
      this.createShulkerBox(Blocks.GRAY_SHULKER_BOX);
      this.createShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX);
      this.createShulkerBox(Blocks.CYAN_SHULKER_BOX);
      this.createShulkerBox(Blocks.PURPLE_SHULKER_BOX);
      this.createShulkerBox(Blocks.BLUE_SHULKER_BOX);
      this.createShulkerBox(Blocks.BROWN_SHULKER_BOX);
      this.createShulkerBox(Blocks.GREEN_SHULKER_BOX);
      this.createShulkerBox(Blocks.RED_SHULKER_BOX);
      this.createShulkerBox(Blocks.BLACK_SHULKER_BOX);
      this.createTrivialBlock(Blocks.CONDUIT, TexturedModel.PARTICLE_ONLY);
      this.skipAutoItemBlock(Blocks.CONDUIT);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("chest"), Blocks.OAK_PLANKS)
         .createWithoutBlockItem(Blocks.CHEST, Blocks.TRAPPED_CHEST);
      this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("ender_chest"), Blocks.OBSIDIAN).createWithoutBlockItem(Blocks.ENDER_CHEST);
      this.blockEntityModels(Blocks.END_PORTAL, Blocks.OBSIDIAN).create(Blocks.END_PORTAL, Blocks.END_GATEWAY);
      this.createTrivialCube(Blocks.AZALEA_LEAVES);
      this.createTrivialCube(Blocks.FLOWERING_AZALEA_LEAVES);
      this.createTrivialCube(Blocks.WHITE_CONCRETE);
      this.createTrivialCube(Blocks.ORANGE_CONCRETE);
      this.createTrivialCube(Blocks.MAGENTA_CONCRETE);
      this.createTrivialCube(Blocks.LIGHT_BLUE_CONCRETE);
      this.createTrivialCube(Blocks.YELLOW_CONCRETE);
      this.createTrivialCube(Blocks.LIME_CONCRETE);
      this.createTrivialCube(Blocks.PINK_CONCRETE);
      this.createTrivialCube(Blocks.GRAY_CONCRETE);
      this.createTrivialCube(Blocks.LIGHT_GRAY_CONCRETE);
      this.createTrivialCube(Blocks.CYAN_CONCRETE);
      this.createTrivialCube(Blocks.PURPLE_CONCRETE);
      this.createTrivialCube(Blocks.BLUE_CONCRETE);
      this.createTrivialCube(Blocks.BROWN_CONCRETE);
      this.createTrivialCube(Blocks.GREEN_CONCRETE);
      this.createTrivialCube(Blocks.RED_CONCRETE);
      this.createTrivialCube(Blocks.BLACK_CONCRETE);
      this.createColoredBlockWithRandomRotations(
         TexturedModel.CUBE,
         Blocks.WHITE_CONCRETE_POWDER,
         Blocks.ORANGE_CONCRETE_POWDER,
         Blocks.MAGENTA_CONCRETE_POWDER,
         Blocks.LIGHT_BLUE_CONCRETE_POWDER,
         Blocks.YELLOW_CONCRETE_POWDER,
         Blocks.LIME_CONCRETE_POWDER,
         Blocks.PINK_CONCRETE_POWDER,
         Blocks.GRAY_CONCRETE_POWDER,
         Blocks.LIGHT_GRAY_CONCRETE_POWDER,
         Blocks.CYAN_CONCRETE_POWDER,
         Blocks.PURPLE_CONCRETE_POWDER,
         Blocks.BLUE_CONCRETE_POWDER,
         Blocks.BROWN_CONCRETE_POWDER,
         Blocks.GREEN_CONCRETE_POWDER,
         Blocks.RED_CONCRETE_POWDER,
         Blocks.BLACK_CONCRETE_POWDER
      );
      this.createTrivialCube(Blocks.TERRACOTTA);
      this.createTrivialCube(Blocks.WHITE_TERRACOTTA);
      this.createTrivialCube(Blocks.ORANGE_TERRACOTTA);
      this.createTrivialCube(Blocks.MAGENTA_TERRACOTTA);
      this.createTrivialCube(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.createTrivialCube(Blocks.YELLOW_TERRACOTTA);
      this.createTrivialCube(Blocks.LIME_TERRACOTTA);
      this.createTrivialCube(Blocks.PINK_TERRACOTTA);
      this.createTrivialCube(Blocks.GRAY_TERRACOTTA);
      this.createTrivialCube(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.createTrivialCube(Blocks.CYAN_TERRACOTTA);
      this.createTrivialCube(Blocks.PURPLE_TERRACOTTA);
      this.createTrivialCube(Blocks.BLUE_TERRACOTTA);
      this.createTrivialCube(Blocks.BROWN_TERRACOTTA);
      this.createTrivialCube(Blocks.GREEN_TERRACOTTA);
      this.createTrivialCube(Blocks.RED_TERRACOTTA);
      this.createTrivialCube(Blocks.BLACK_TERRACOTTA);
      this.createTrivialCube(Blocks.TINTED_GLASS);
      this.createGlassBlocks(Blocks.GLASS, Blocks.GLASS_PANE);
      this.createGlassBlocks(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
      this.createGlassBlocks(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
      this.createColoredBlockWithStateRotations(
         TexturedModel.GLAZED_TERRACOTTA,
         Blocks.WHITE_GLAZED_TERRACOTTA,
         Blocks.ORANGE_GLAZED_TERRACOTTA,
         Blocks.MAGENTA_GLAZED_TERRACOTTA,
         Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
         Blocks.YELLOW_GLAZED_TERRACOTTA,
         Blocks.LIME_GLAZED_TERRACOTTA,
         Blocks.PINK_GLAZED_TERRACOTTA,
         Blocks.GRAY_GLAZED_TERRACOTTA,
         Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
         Blocks.CYAN_GLAZED_TERRACOTTA,
         Blocks.PURPLE_GLAZED_TERRACOTTA,
         Blocks.BLUE_GLAZED_TERRACOTTA,
         Blocks.BROWN_GLAZED_TERRACOTTA,
         Blocks.GREEN_GLAZED_TERRACOTTA,
         Blocks.RED_GLAZED_TERRACOTTA,
         Blocks.BLACK_GLAZED_TERRACOTTA
      );
      this.createFullAndCarpetBlocks(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
      this.createFullAndCarpetBlocks(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
      this.createFullAndCarpetBlocks(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
      this.createFullAndCarpetBlocks(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
      this.createFullAndCarpetBlocks(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
      this.createFullAndCarpetBlocks(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
      this.createFullAndCarpetBlocks(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
      this.createFullAndCarpetBlocks(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
      this.createFullAndCarpetBlocks(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
      this.createFullAndCarpetBlocks(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
      this.createFullAndCarpetBlocks(Blocks.RED_WOOL, Blocks.RED_CARPET);
      this.createFullAndCarpetBlocks(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
      this.createTrivialCube(Blocks.MUD);
      this.createTrivialCube(Blocks.PACKED_MUD);
      this.createPlant(Blocks.FERN, Blocks.POTTED_FERN, BlockModelGenerators.TintState.TINTED);
      this.createPlant(Blocks.DANDELION, Blocks.POTTED_DANDELION, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.POPPY, Blocks.POTTED_POPPY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPlant(Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER, BlockModelGenerators.TintState.NOT_TINTED);
      this.createPointedDripstone();
      this.createMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.MUSHROOM_STEM);
      this.createCrossBlockWithDefaultItem(Blocks.SHORT_GRASS, BlockModelGenerators.TintState.TINTED);
      this.createCrossBlock(Blocks.SUGAR_CANE, BlockModelGenerators.TintState.TINTED);
      this.createSimpleFlatItemModel(Items.SUGAR_CANE);
      this.createGrowingPlant(Blocks.KELP, Blocks.KELP_PLANT, BlockModelGenerators.TintState.NOT_TINTED);
      this.createSimpleFlatItemModel(Items.KELP);
      this.skipAutoItemBlock(Blocks.KELP_PLANT);
      this.createCrossBlock(Blocks.HANGING_ROOTS, BlockModelGenerators.TintState.NOT_TINTED);
      this.skipAutoItemBlock(Blocks.HANGING_ROOTS);
      this.skipAutoItemBlock(Blocks.CAVE_VINES_PLANT);
      this.createGrowingPlant(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, BlockModelGenerators.TintState.NOT_TINTED);
      this.createGrowingPlant(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, BlockModelGenerators.TintState.NOT_TINTED);
      this.createSimpleFlatItemModel(Blocks.WEEPING_VINES, "_plant");
      this.skipAutoItemBlock(Blocks.WEEPING_VINES_PLANT);
      this.createSimpleFlatItemModel(Blocks.TWISTING_VINES, "_plant");
      this.skipAutoItemBlock(Blocks.TWISTING_VINES_PLANT);
      this.createCrossBlockWithDefaultItem(
         Blocks.BAMBOO_SAPLING, BlockModelGenerators.TintState.TINTED, TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.BAMBOO, "_stage0"))
      );
      this.createBamboo();
      this.createCrossBlockWithDefaultItem(Blocks.COBWEB, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.LILAC, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.ROSE_BUSH, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.PEONY, BlockModelGenerators.TintState.NOT_TINTED);
      this.createDoublePlant(Blocks.TALL_GRASS, BlockModelGenerators.TintState.TINTED);
      this.createDoublePlant(Blocks.LARGE_FERN, BlockModelGenerators.TintState.TINTED);
      this.createSunflower();
      this.createTallSeagrass();
      this.createSmallDripleaf();
      this.createCoral(
         Blocks.TUBE_CORAL,
         Blocks.DEAD_TUBE_CORAL,
         Blocks.TUBE_CORAL_BLOCK,
         Blocks.DEAD_TUBE_CORAL_BLOCK,
         Blocks.TUBE_CORAL_FAN,
         Blocks.DEAD_TUBE_CORAL_FAN,
         Blocks.TUBE_CORAL_WALL_FAN,
         Blocks.DEAD_TUBE_CORAL_WALL_FAN
      );
      this.createCoral(
         Blocks.BRAIN_CORAL,
         Blocks.DEAD_BRAIN_CORAL,
         Blocks.BRAIN_CORAL_BLOCK,
         Blocks.DEAD_BRAIN_CORAL_BLOCK,
         Blocks.BRAIN_CORAL_FAN,
         Blocks.DEAD_BRAIN_CORAL_FAN,
         Blocks.BRAIN_CORAL_WALL_FAN,
         Blocks.DEAD_BRAIN_CORAL_WALL_FAN
      );
      this.createCoral(
         Blocks.BUBBLE_CORAL,
         Blocks.DEAD_BUBBLE_CORAL,
         Blocks.BUBBLE_CORAL_BLOCK,
         Blocks.DEAD_BUBBLE_CORAL_BLOCK,
         Blocks.BUBBLE_CORAL_FAN,
         Blocks.DEAD_BUBBLE_CORAL_FAN,
         Blocks.BUBBLE_CORAL_WALL_FAN,
         Blocks.DEAD_BUBBLE_CORAL_WALL_FAN
      );
      this.createCoral(
         Blocks.FIRE_CORAL,
         Blocks.DEAD_FIRE_CORAL,
         Blocks.FIRE_CORAL_BLOCK,
         Blocks.DEAD_FIRE_CORAL_BLOCK,
         Blocks.FIRE_CORAL_FAN,
         Blocks.DEAD_FIRE_CORAL_FAN,
         Blocks.FIRE_CORAL_WALL_FAN,
         Blocks.DEAD_FIRE_CORAL_WALL_FAN
      );
      this.createCoral(
         Blocks.HORN_CORAL,
         Blocks.DEAD_HORN_CORAL,
         Blocks.HORN_CORAL_BLOCK,
         Blocks.DEAD_HORN_CORAL_BLOCK,
         Blocks.HORN_CORAL_FAN,
         Blocks.DEAD_HORN_CORAL_FAN,
         Blocks.HORN_CORAL_WALL_FAN,
         Blocks.DEAD_HORN_CORAL_WALL_FAN
      );
      this.createStems(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
      this.createStems(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      this.woodProvider(Blocks.MANGROVE_LOG).logWithHorizontal(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
      this.woodProvider(Blocks.STRIPPED_MANGROVE_LOG).logWithHorizontal(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
      this.createHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
      this.createTrivialBlock(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.ACACIA_LOG).logWithHorizontal(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
      this.woodProvider(Blocks.STRIPPED_ACACIA_LOG).logWithHorizontal(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
      this.createHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
      this.createPlant(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.CHERRY_LOG).logUVLocked(Blocks.CHERRY_LOG).wood(Blocks.CHERRY_WOOD);
      this.woodProvider(Blocks.STRIPPED_CHERRY_LOG).logUVLocked(Blocks.STRIPPED_CHERRY_LOG).wood(Blocks.STRIPPED_CHERRY_WOOD);
      this.createHangingSign(Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
      this.createPlant(Blocks.CHERRY_SAPLING, Blocks.POTTED_CHERRY_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.CHERRY_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.BIRCH_LOG).logWithHorizontal(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
      this.woodProvider(Blocks.STRIPPED_BIRCH_LOG).logWithHorizontal(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
      this.createHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
      this.createPlant(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.OAK_LOG).logWithHorizontal(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
      this.createHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
      this.createPlant(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.SPRUCE_LOG).logWithHorizontal(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
      this.woodProvider(Blocks.STRIPPED_SPRUCE_LOG).logWithHorizontal(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
      this.createHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
      this.createPlant(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.DARK_OAK_LOG).logWithHorizontal(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_DARK_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.createHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
      this.createPlant(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.JUNGLE_LOG).logWithHorizontal(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
      this.woodProvider(Blocks.STRIPPED_JUNGLE_LOG).logWithHorizontal(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
      this.createHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
      this.createPlant(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, BlockModelGenerators.TintState.NOT_TINTED);
      this.createTrivialBlock(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.CRIMSON_STEM).log(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_CRIMSON_STEM).log(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.createHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
      this.createPlant(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, BlockModelGenerators.TintState.NOT_TINTED);
      this.createNetherRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
      this.woodProvider(Blocks.WARPED_STEM).log(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_WARPED_STEM).log(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
      this.createHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
      this.createPlant(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, BlockModelGenerators.TintState.NOT_TINTED);
      this.createNetherRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
      this.woodProvider(Blocks.BAMBOO_BLOCK).logUVLocked(Blocks.BAMBOO_BLOCK);
      this.woodProvider(Blocks.STRIPPED_BAMBOO_BLOCK).logUVLocked(Blocks.STRIPPED_BAMBOO_BLOCK);
      this.createHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
      this.createCrossBlock(Blocks.NETHER_SPROUTS, BlockModelGenerators.TintState.NOT_TINTED);
      this.createSimpleFlatItemModel(Items.NETHER_SPROUTS);
      this.createDoor(Blocks.IRON_DOOR);
      this.createTrapdoor(Blocks.IRON_TRAPDOOR);
      this.createSmoothStoneSlab();
      this.createPassiveRail(Blocks.RAIL);
      this.createActiveRail(Blocks.POWERED_RAIL);
      this.createActiveRail(Blocks.DETECTOR_RAIL);
      this.createActiveRail(Blocks.ACTIVATOR_RAIL);
      this.createComparator();
      this.createCommandBlock(Blocks.COMMAND_BLOCK);
      this.createCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
      this.createCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
      this.createAnvil(Blocks.ANVIL);
      this.createAnvil(Blocks.CHIPPED_ANVIL);
      this.createAnvil(Blocks.DAMAGED_ANVIL);
      this.createBarrel();
      this.createBell();
      this.createFurnace(Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
      this.createFurnace(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
      this.createFurnace(Blocks.SMOKER, TexturedModel.ORIENTABLE);
      this.createRedstoneWire();
      this.createRespawnAnchor();
      this.createSculkCatalyst();
      this.copyModel(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
      this.copyModel(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
      this.copyModel(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
      this.copyModel(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
      this.createInfestedStone();
      this.copyModel(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
      this.createInfestedDeepslate();
      SpawnEggItem.eggs().forEach(var1 -> this.delegateItemModel(var1, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg")));
   }

   private void createLightBlock() {
      this.skipAutoItemBlock(Blocks.LIGHT);
      PropertyDispatch.C1 var1 = PropertyDispatch.property(BlockStateProperties.LEVEL);

      for (int var2 = 0; var2 < 16; var2++) {
         String var3 = String.format(Locale.ROOT, "_%02d", var2);
         ResourceLocation var4 = TextureMapping.getItemTexture(Items.LIGHT, var3);
         var1.select(
            var2,
            Variant.variant()
               .with(
                  VariantProperties.MODEL, ModelTemplates.PARTICLE_ONLY.createWithSuffix(Blocks.LIGHT, var3, TextureMapping.particle(var4), this.modelOutput)
               )
         );
         ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(Items.LIGHT, var3), TextureMapping.layer0(var4), this.modelOutput);
      }

      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(Blocks.LIGHT).with(var1));
   }

   private void createCandleAndCandleCake(Block var1, Block var2) {
      this.createSimpleFlatItemModel(var1.asItem());
      TextureMapping var3 = TextureMapping.cube(TextureMapping.getBlockTexture(var1));
      TextureMapping var4 = TextureMapping.cube(TextureMapping.getBlockTexture(var1, "_lit"));
      ResourceLocation var5 = ModelTemplates.CANDLE.createWithSuffix(var1, "_one_candle", var3, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.TWO_CANDLES.createWithSuffix(var1, "_two_candles", var3, this.modelOutput);
      ResourceLocation var7 = ModelTemplates.THREE_CANDLES.createWithSuffix(var1, "_three_candles", var3, this.modelOutput);
      ResourceLocation var8 = ModelTemplates.FOUR_CANDLES.createWithSuffix(var1, "_four_candles", var3, this.modelOutput);
      ResourceLocation var9 = ModelTemplates.CANDLE.createWithSuffix(var1, "_one_candle_lit", var4, this.modelOutput);
      ResourceLocation var10 = ModelTemplates.TWO_CANDLES.createWithSuffix(var1, "_two_candles_lit", var4, this.modelOutput);
      ResourceLocation var11 = ModelTemplates.THREE_CANDLES.createWithSuffix(var1, "_three_candles_lit", var4, this.modelOutput);
      ResourceLocation var12 = ModelTemplates.FOUR_CANDLES.createWithSuffix(var1, "_four_candles_lit", var4, this.modelOutput);
      this.blockStateOutput
         .accept(
            MultiVariantGenerator.multiVariant(var1)
               .with(
                  PropertyDispatch.properties(BlockStateProperties.CANDLES, BlockStateProperties.LIT)
                     .select(1, false, Variant.variant().with(VariantProperties.MODEL, var5))
                     .select(2, false, Variant.variant().with(VariantProperties.MODEL, var6))
                     .select(3, false, Variant.variant().with(VariantProperties.MODEL, var7))
                     .select(4, false, Variant.variant().with(VariantProperties.MODEL, var8))
                     .select(1, true, Variant.variant().with(VariantProperties.MODEL, var9))
                     .select(2, true, Variant.variant().with(VariantProperties.MODEL, var10))
                     .select(3, true, Variant.variant().with(VariantProperties.MODEL, var11))
                     .select(4, true, Variant.variant().with(VariantProperties.MODEL, var12))
               )
         );
      ResourceLocation var13 = ModelTemplates.CANDLE_CAKE.create(var2, TextureMapping.candleCake(var1, false), this.modelOutput);
      ResourceLocation var14 = ModelTemplates.CANDLE_CAKE.createWithSuffix(var2, "_lit", TextureMapping.candleCake(var1, true), this.modelOutput);
      this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(var2).with(createBooleanModelDispatch(BlockStateProperties.LIT, var14, var13)));
   }

   class BlockEntityModelGenerator {
      private final ResourceLocation baseModel;

      public BlockEntityModelGenerator(ResourceLocation var2, Block var3) {
         super();
         this.baseModel = ModelTemplates.PARTICLE_ONLY.create(var2, TextureMapping.particle(var3), BlockModelGenerators.this.modelOutput);
      }

      public BlockModelGenerators.BlockEntityModelGenerator create(Block... var1) {
         for (Block var5 : var1) {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var5, this.baseModel));
         }

         return this;
      }

      public BlockModelGenerators.BlockEntityModelGenerator createWithoutBlockItem(Block... var1) {
         for (Block var5 : var1) {
            BlockModelGenerators.this.skipAutoItemBlock(var5);
         }

         return this.create(var1);
      }

      public BlockModelGenerators.BlockEntityModelGenerator createWithCustomBlockItemModel(ModelTemplate var1, Block... var2) {
         for (Block var6 : var2) {
            var1.create(ModelLocationUtils.getModelLocation(var6.asItem()), TextureMapping.particle(var6), BlockModelGenerators.this.modelOutput);
         }

         return this.create(var2);
      }
   }

   class BlockFamilyProvider {
      private final TextureMapping mapping;
      private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();
      @Nullable
      private BlockFamily family;
      @Nullable
      private ResourceLocation fullBlock;
      private final Set<Block> skipGeneratingModelsFor = new HashSet<>();

      public BlockFamilyProvider(TextureMapping var2) {
         super();
         this.mapping = var2;
      }

      public BlockModelGenerators.BlockFamilyProvider fullBlock(Block var1, ModelTemplate var2) {
         this.fullBlock = var2.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         if (BlockModelGenerators.this.fullBlockModelCustomGenerators.containsKey(var1)) {
            BlockModelGenerators.this.blockStateOutput
               .accept(
                  BlockModelGenerators.this.fullBlockModelCustomGenerators
                     .get(var1)
                     .create(var1, this.fullBlock, this.mapping, BlockModelGenerators.this.modelOutput)
               );
         } else {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var1, this.fullBlock));
         }

         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider donateModelTo(Block var1, Block var2) {
         ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var2, var3));
         BlockModelGenerators.this.delegateItemModel(var2, var3);
         this.skipGeneratingModelsFor.add(var2);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider button(Block var1) {
         ResourceLocation var2 = ModelTemplates.BUTTON.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var3 = ModelTemplates.BUTTON_PRESSED.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createButton(var1, var2, var3));
         ResourceLocation var4 = ModelTemplates.BUTTON_INVENTORY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(var1, var4);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider wall(Block var1) {
         ResourceLocation var2 = ModelTemplates.WALL_POST.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var3 = ModelTemplates.WALL_LOW_SIDE.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var4 = ModelTemplates.WALL_TALL_SIDE.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createWall(var1, var2, var3, var4));
         ResourceLocation var5 = ModelTemplates.WALL_INVENTORY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(var1, var5);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider customFence(Block var1) {
         TextureMapping var2 = TextureMapping.customParticle(var1);
         ResourceLocation var3 = ModelTemplates.CUSTOM_FENCE_POST.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var4 = ModelTemplates.CUSTOM_FENCE_SIDE_NORTH.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var5 = ModelTemplates.CUSTOM_FENCE_SIDE_EAST.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var6 = ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var7 = ModelTemplates.CUSTOM_FENCE_SIDE_WEST.create(var1, var2, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createCustomFence(var1, var3, var4, var5, var6, var7));
         ResourceLocation var8 = ModelTemplates.CUSTOM_FENCE_INVENTORY.create(var1, var2, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(var1, var8);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider fence(Block var1) {
         ResourceLocation var2 = ModelTemplates.FENCE_POST.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var3 = ModelTemplates.FENCE_SIDE.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFence(var1, var2, var3));
         ResourceLocation var4 = ModelTemplates.FENCE_INVENTORY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.delegateItemModel(var1, var4);
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider customFenceGate(Block var1) {
         TextureMapping var2 = TextureMapping.customParticle(var1);
         ResourceLocation var3 = ModelTemplates.CUSTOM_FENCE_GATE_OPEN.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var4 = ModelTemplates.CUSTOM_FENCE_GATE_CLOSED.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var5 = ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN.create(var1, var2, BlockModelGenerators.this.modelOutput);
         ResourceLocation var6 = ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED.create(var1, var2, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate(var1, var3, var4, var5, var6, false));
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider fenceGate(Block var1) {
         ResourceLocation var2 = ModelTemplates.FENCE_GATE_OPEN.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var3 = ModelTemplates.FENCE_GATE_CLOSED.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var4 = ModelTemplates.FENCE_GATE_WALL_OPEN.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var5 = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate(var1, var2, var3, var4, var5, true));
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider pressurePlate(Block var1) {
         ResourceLocation var2 = ModelTemplates.PRESSURE_PLATE_UP.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var3 = ModelTemplates.PRESSURE_PLATE_DOWN.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(var1, var2, var3));
         return this;
      }

      public BlockModelGenerators.BlockFamilyProvider sign(Block var1) {
         if (this.family == null) {
            throw new IllegalStateException("Family not defined");
         } else {
            Block var2 = this.family.getVariants().get(BlockFamily.Variant.WALL_SIGN);
            ResourceLocation var3 = ModelTemplates.PARTICLE_ONLY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var1, var3));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var2, var3));
            BlockModelGenerators.this.createSimpleFlatItemModel(var1.asItem());
            BlockModelGenerators.this.skipAutoItemBlock(var2);
            return this;
         }
      }

      public BlockModelGenerators.BlockFamilyProvider slab(Block var1) {
         if (this.fullBlock == null) {
            throw new IllegalStateException("Full block not generated yet");
         } else {
            ResourceLocation var2 = this.getOrCreateModel(ModelTemplates.SLAB_BOTTOM, var1);
            ResourceLocation var3 = this.getOrCreateModel(ModelTemplates.SLAB_TOP, var1);
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSlab(var1, var2, var3, this.fullBlock));
            BlockModelGenerators.this.delegateItemModel(var1, var2);
            return this;
         }
      }

      public BlockModelGenerators.BlockFamilyProvider stairs(Block var1) {
         ResourceLocation var2 = this.getOrCreateModel(ModelTemplates.STAIRS_INNER, var1);
         ResourceLocation var3 = this.getOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, var1);
         ResourceLocation var4 = this.getOrCreateModel(ModelTemplates.STAIRS_OUTER, var1);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createStairs(var1, var2, var3, var4));
         BlockModelGenerators.this.delegateItemModel(var1, var3);
         return this;
      }

      private BlockModelGenerators.BlockFamilyProvider fullBlockVariant(Block var1) {
         TexturedModel var2 = BlockModelGenerators.this.texturedModels.getOrDefault(var1, TexturedModel.CUBE.get(var1));
         ResourceLocation var3 = var2.create(var1, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var1, var3));
         return this;
      }

      private BlockModelGenerators.BlockFamilyProvider door(Block var1) {
         BlockModelGenerators.this.createDoor(var1);
         return this;
      }

      private void trapdoor(Block var1) {
         if (BlockModelGenerators.this.nonOrientableTrapdoor.contains(var1)) {
            BlockModelGenerators.this.createTrapdoor(var1);
         } else {
            BlockModelGenerators.this.createOrientableTrapdoor(var1);
         }
      }

      private ResourceLocation getOrCreateModel(ModelTemplate var1, Block var2) {
         return this.models.computeIfAbsent(var1, var2x -> var2x.create(var2, this.mapping, BlockModelGenerators.this.modelOutput));
      }

      public BlockModelGenerators.BlockFamilyProvider generateFor(BlockFamily var1) {
         this.family = var1;
         var1.getVariants().forEach((var1x, var2) -> {
            if (!this.skipGeneratingModelsFor.contains(var2)) {
               BiConsumer var3 = BlockModelGenerators.SHAPE_CONSUMERS.get(var1x);
               if (var3 != null) {
                  var3.accept(this, var2);
               }
            }
         });
         return this;
      }
   }

   @FunctionalInterface
   interface BlockStateGeneratorSupplier {
      BlockStateGenerator create(Block var1, ResourceLocation var2, TextureMapping var3, BiConsumer<ResourceLocation, Supplier<JsonElement>> var4);
   }

   static record BookSlotModelCacheKey(ModelTemplate template, String modelSuffix) {
      BookSlotModelCacheKey(ModelTemplate template, String modelSuffix) {
         super();
         this.template = template;
         this.modelSuffix = modelSuffix;
      }
   }

   static enum TintState {
      TINTED,
      NOT_TINTED;

      private TintState() {
      }

      public ModelTemplate getCross() {
         return this == TINTED ? ModelTemplates.TINTED_CROSS : ModelTemplates.CROSS;
      }

      public ModelTemplate getCrossPot() {
         return this == TINTED ? ModelTemplates.TINTED_FLOWER_POT_CROSS : ModelTemplates.FLOWER_POT_CROSS;
      }
   }

   class WoodProvider {
      private final TextureMapping logMapping;

      public WoodProvider(TextureMapping var2) {
         super();
         this.logMapping = var2;
      }

      public BlockModelGenerators.WoodProvider wood(Block var1) {
         TextureMapping var2 = this.logMapping.copyAndUpdate(TextureSlot.END, this.logMapping.get(TextureSlot.SIDE));
         ResourceLocation var3 = ModelTemplates.CUBE_COLUMN.create(var1, var2, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(var1, var3));
         return this;
      }

      public BlockModelGenerators.WoodProvider log(Block var1) {
         ResourceLocation var2 = ModelTemplates.CUBE_COLUMN.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(var1, var2));
         return this;
      }

      public BlockModelGenerators.WoodProvider logWithHorizontal(Block var1) {
         ResourceLocation var2 = ModelTemplates.CUBE_COLUMN.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput);
         ResourceLocation var3 = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(var1, var2, var3));
         return this;
      }

      public BlockModelGenerators.WoodProvider logUVLocked(Block var1) {
         BlockModelGenerators.this.blockStateOutput
            .accept(BlockModelGenerators.createPillarBlockUVLocked(var1, this.logMapping, BlockModelGenerators.this.modelOutput));
         return this;
      }
   }
}
