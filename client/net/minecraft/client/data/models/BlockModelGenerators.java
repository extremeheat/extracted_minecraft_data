package net.minecraft.client.data.models;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quadrant;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.BannerSpecialRenderer;
import net.minecraft.client.renderer.special.BedSpecialRenderer;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.ConduitSpecialRenderer;
import net.minecraft.client.renderer.special.CopperGolemStatueSpecialRenderer;
import net.minecraft.client.renderer.special.DecoratedPotSpecialRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.DriedGhastBlock;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.SnifferEggBlock;
import net.minecraft.world.level.block.TestBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.block.state.properties.SideChainPart;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.TestBlockMode;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.block.state.properties.WallSide;

public class BlockModelGenerators {
   final Consumer<BlockModelDefinitionGenerator> blockStateOutput;
   final ItemModelOutput itemModelOutput;
   final BiConsumer<ResourceLocation, ModelInstance> modelOutput;
   static final List<Block> NON_ORIENTABLE_TRAPDOOR;
   public static final VariantMutator NOP;
   public static final VariantMutator UV_LOCK;
   public static final VariantMutator X_ROT_90;
   public static final VariantMutator X_ROT_180;
   public static final VariantMutator X_ROT_270;
   public static final VariantMutator Y_ROT_90;
   public static final VariantMutator Y_ROT_180;
   public static final VariantMutator Y_ROT_270;
   private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_1_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_2_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_3_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> FLOWER_BED_MODEL_4_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_1_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_2_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_3_SEGMENT_CONDITION;
   private static final Function<ConditionBuilder, ConditionBuilder> LEAF_LITTER_MODEL_4_SEGMENT_CONDITION;
   static final Map<Block, BlockStateGeneratorSupplier> FULL_BLOCK_MODEL_CUSTOM_GENERATORS;
   private static final PropertyDispatch<VariantMutator> ROTATION_FACING;
   private static final PropertyDispatch<VariantMutator> ROTATIONS_COLUMN_WITH_FACING;
   private static final PropertyDispatch<VariantMutator> ROTATION_TORCH;
   private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING_ALT;
   private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING;
   static final Map<Block, TexturedModel> TEXTURED_MODELS;
   static final Map<BlockFamily.Variant, BiConsumer<BlockFamilyProvider, Block>> SHAPE_CONSUMERS;
   private static final Map<Direction, VariantMutator> MULTIFACE_GENERATOR;
   private static final Map<BookSlotModelCacheKey, ResourceLocation> CHISELED_BOOKSHELF_SLOT_MODEL_CACHE;

   static Variant plainModel(ResourceLocation var0) {
      return new Variant(var0);
   }

   static MultiVariant variant(Variant var0) {
      return new MultiVariant(WeightedList.of(var0));
   }

   private static MultiVariant variants(Variant... var0) {
      return new MultiVariant(WeightedList.of(Arrays.stream(var0).map((var0x) -> new Weighted(var0x, 1)).toList()));
   }

   static MultiVariant plainVariant(ResourceLocation var0) {
      return variant(plainModel(var0));
   }

   private static ConditionBuilder condition() {
      return new ConditionBuilder();
   }

   @SafeVarargs
   private static <T extends Enum<T> & StringRepresentable> ConditionBuilder condition(EnumProperty<T> var0, T var1, T... var2) {
      return condition().term(var0, var1, var2);
   }

   private static ConditionBuilder condition(BooleanProperty var0, boolean var1) {
      return condition().term(var0, var1);
   }

   private static Condition or(ConditionBuilder... var0) {
      return new CombinedCondition(CombinedCondition.Operation.OR, Stream.of(var0).map(ConditionBuilder::build).toList());
   }

   private static Condition and(ConditionBuilder... var0) {
      return new CombinedCondition(CombinedCondition.Operation.AND, Stream.of(var0).map(ConditionBuilder::build).toList());
   }

   private static BlockModelDefinitionGenerator createMirroredCubeGenerator(Block var0, Variant var1, TextureMapping var2, BiConsumer<ResourceLocation, ModelInstance> var3) {
      Variant var4 = plainModel(ModelTemplates.CUBE_MIRRORED_ALL.create(var0, var2, var3));
      return MultiVariantGenerator.dispatch(var0, createRotatedVariants(var1, var4));
   }

   private static BlockModelDefinitionGenerator createNorthWestMirroredCubeGenerator(Block var0, Variant var1, TextureMapping var2, BiConsumer<ResourceLocation, ModelInstance> var3) {
      MultiVariant var4 = plainVariant(ModelTemplates.CUBE_NORTH_WEST_MIRRORED_ALL.create(var0, var2, var3));
      return createSimpleBlock(var0, var4);
   }

   private static BlockModelDefinitionGenerator createMirroredColumnGenerator(Block var0, Variant var1, TextureMapping var2, BiConsumer<ResourceLocation, ModelInstance> var3) {
      Variant var4 = plainModel(ModelTemplates.CUBE_COLUMN_MIRRORED.create(var0, var2, var3));
      return MultiVariantGenerator.dispatch(var0, createRotatedVariants(var1, var4)).with(createRotatedPillar());
   }

   public BlockModelGenerators(Consumer<BlockModelDefinitionGenerator> var1, ItemModelOutput var2, BiConsumer<ResourceLocation, ModelInstance> var3) {
      super();
      this.blockStateOutput = var1;
      this.itemModelOutput = var2;
      this.modelOutput = var3;
   }

   private void registerSimpleItemModel(Item var1, ResourceLocation var2) {
      this.itemModelOutput.accept(var1, ItemModelUtils.plainModel(var2));
   }

   void registerSimpleItemModel(Block var1, ResourceLocation var2) {
      this.itemModelOutput.accept(var1.asItem(), ItemModelUtils.plainModel(var2));
   }

   private void registerSimpleTintedItemModel(Block var1, ResourceLocation var2, ItemTintSource var3) {
      this.itemModelOutput.accept(var1.asItem(), ItemModelUtils.tintedModel(var2, var3));
   }

   private ResourceLocation createFlatItemModel(Item var1) {
      return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layer0(var1), this.modelOutput);
   }

   ResourceLocation createFlatItemModelWithBlockTexture(Item var1, Block var2) {
      return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layer0(var2), this.modelOutput);
   }

   private ResourceLocation createFlatItemModelWithBlockTexture(Item var1, Block var2, String var3) {
      return ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layer0(TextureMapping.getBlockTexture(var2, var3)), this.modelOutput);
   }

   ResourceLocation createFlatItemModelWithBlockTextureAndOverlay(Item var1, Block var2, String var3) {
      ResourceLocation var4 = TextureMapping.getBlockTexture(var2);
      ResourceLocation var5 = TextureMapping.getBlockTexture(var2, var3);
      return ModelTemplates.TWO_LAYERED_ITEM.create(ModelLocationUtils.getModelLocation(var1), TextureMapping.layered(var4, var5), this.modelOutput);
   }

   void registerSimpleFlatItemModel(Item var1) {
      this.registerSimpleItemModel(var1, this.createFlatItemModel(var1));
   }

   private void registerSimpleFlatItemModel(Block var1) {
      Item var2 = var1.asItem();
      if (var2 != Items.AIR) {
         this.registerSimpleItemModel(var2, this.createFlatItemModelWithBlockTexture(var2, var1));
      }

   }

   private void registerSimpleFlatItemModel(Block var1, String var2) {
      Item var3 = var1.asItem();
      if (var3 != Items.AIR) {
         this.registerSimpleItemModel(var3, this.createFlatItemModelWithBlockTexture(var3, var1, var2));
      }

   }

   private void registerTwoLayerFlatItemModel(Block var1, String var2) {
      Item var3 = var1.asItem();
      if (var3 != Items.AIR) {
         ResourceLocation var4 = this.createFlatItemModelWithBlockTextureAndOverlay(var3, var1, var2);
         this.registerSimpleItemModel(var3, var4);
      }

   }

   private static MultiVariant createRotatedVariants(Variant var0) {
      return variants(var0, var0.with(Y_ROT_90), var0.with(Y_ROT_180), var0.with(Y_ROT_270));
   }

   private static MultiVariant createRotatedVariants(Variant var0, Variant var1) {
      return variants(var0, var1, var0.with(Y_ROT_180), var1.with(Y_ROT_180));
   }

   private static PropertyDispatch<MultiVariant> createBooleanModelDispatch(BooleanProperty var0, MultiVariant var1, MultiVariant var2) {
      return PropertyDispatch.initial(var0).select(true, var1).select(false, var2);
   }

   private void createRotatedMirroredVariantBlock(Block var1) {
      Variant var2 = plainModel(TexturedModel.CUBE.create(var1, this.modelOutput));
      Variant var3 = plainModel(TexturedModel.CUBE_MIRRORED.create(var1, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, createRotatedVariants(var2, var3)));
   }

   private void createRotatedVariantBlock(Block var1) {
      Variant var2 = plainModel(TexturedModel.CUBE.create(var1, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, createRotatedVariants(var2)));
   }

   private void createBrushableBlock(Block var1) {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.DUSTED).generate((var2) -> {
         String var3 = "_" + var2;
         ResourceLocation var4 = TextureMapping.getBlockTexture(var1, var3);
         ResourceLocation var5 = ModelTemplates.CUBE_ALL.createWithSuffix(var1, var3, (new TextureMapping()).put(TextureSlot.ALL, var4), this.modelOutput);
         return plainVariant(var5);
      })));
      this.registerSimpleItemModel(var1, ModelLocationUtils.getModelLocation(var1, "_0"));
   }

   static BlockModelDefinitionGenerator createButton(Block var0, MultiVariant var1, MultiVariant var2) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.POWERED).select(false, var1).select(true, var2)).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.WALL, Direction.EAST, Y_ROT_90.then(X_ROT_90).then(UV_LOCK)).select(AttachFace.WALL, Direction.WEST, Y_ROT_270.then(X_ROT_90).then(UV_LOCK)).select(AttachFace.WALL, Direction.SOUTH, Y_ROT_180.then(X_ROT_90).then(UV_LOCK)).select(AttachFace.WALL, Direction.NORTH, X_ROT_90.then(UV_LOCK)).select(AttachFace.CEILING, Direction.EAST, Y_ROT_270.then(X_ROT_180)).select(AttachFace.CEILING, Direction.WEST, Y_ROT_90.then(X_ROT_180)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.NORTH, Y_ROT_180.then(X_ROT_180)));
   }

   private static BlockModelDefinitionGenerator createDoor(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3, MultiVariant var4, MultiVariant var5, MultiVariant var6, MultiVariant var7, MultiVariant var8) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, var1).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, var1.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, var1.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, false, var1.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, var3).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, var3.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, var3.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, false, var3.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, var2.with(Y_ROT_90)).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, var2.with(Y_ROT_180)).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, var2.with(Y_ROT_270)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.LEFT, true, var2).select(Direction.EAST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, var4.with(Y_ROT_270)).select(Direction.SOUTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, var4).select(Direction.WEST, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, var4.with(Y_ROT_90)).select(Direction.NORTH, DoubleBlockHalf.LOWER, DoorHingeSide.RIGHT, true, var4.with(Y_ROT_180)).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, var5).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, var5.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, var5.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, false, var5.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, var7).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, var7.with(Y_ROT_90)).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, var7.with(Y_ROT_180)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, false, var7.with(Y_ROT_270)).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, var6.with(Y_ROT_90)).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, var6.with(Y_ROT_180)).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, var6.with(Y_ROT_270)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.LEFT, true, var6).select(Direction.EAST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, var8.with(Y_ROT_270)).select(Direction.SOUTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, var8).select(Direction.WEST, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, var8.with(Y_ROT_90)).select(Direction.NORTH, DoubleBlockHalf.UPPER, DoorHingeSide.RIGHT, true, var8.with(Y_ROT_180)));
   }

   static BlockModelDefinitionGenerator createCustomFence(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3, MultiVariant var4, MultiVariant var5) {
      return MultiPartGenerator.multiPart(var0).with(var1).with(condition().term(BlockStateProperties.NORTH, true), var2).with(condition().term(BlockStateProperties.EAST, true), var3).with(condition().term(BlockStateProperties.SOUTH, true), var4).with(condition().term(BlockStateProperties.WEST, true), var5);
   }

   static BlockModelDefinitionGenerator createFence(Block var0, MultiVariant var1, MultiVariant var2) {
      return MultiPartGenerator.multiPart(var0).with(var1).with(condition().term(BlockStateProperties.NORTH, true), var2.with(UV_LOCK)).with(condition().term(BlockStateProperties.EAST, true), var2.with(Y_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.SOUTH, true), var2.with(Y_ROT_180).with(UV_LOCK)).with(condition().term(BlockStateProperties.WEST, true), var2.with(Y_ROT_270).with(UV_LOCK));
   }

   static BlockModelDefinitionGenerator createWall(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3) {
      return MultiPartGenerator.multiPart(var0).with(condition().term(BlockStateProperties.UP, true), var1).with(condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW), var2.with(UV_LOCK)).with(condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW), var2.with(Y_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW), var2.with(Y_ROT_180).with(UV_LOCK)).with(condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW), var2.with(Y_ROT_270).with(UV_LOCK)).with(condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL), var3.with(UV_LOCK)).with(condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL), var3.with(Y_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL), var3.with(Y_ROT_180).with(UV_LOCK)).with(condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL), var3.with(Y_ROT_270).with(UV_LOCK));
   }

   static BlockModelDefinitionGenerator createFenceGate(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3, MultiVariant var4, boolean var5) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN).select(false, false, var2).select(true, false, var4).select(false, true, var1).select(true, true, var3)).with(var5 ? UV_LOCK : NOP).with(ROTATION_HORIZONTAL_FACING_ALT);
   }

   static BlockModelDefinitionGenerator createStairs(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, var2).select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, var2.with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, var2.with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, var2.with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, var3).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, var3.with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, var3.with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, var3.with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, var3.with(Y_ROT_270).with(UV_LOCK)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, var3.with(Y_ROT_90).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, var3).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, var3.with(Y_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, var1).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, var1.with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, var1.with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, var1.with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, var1.with(Y_ROT_270).with(UV_LOCK)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, var1.with(Y_ROT_90).with(UV_LOCK)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, var1).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, var1.with(Y_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, var2.with(X_ROT_180).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, var2.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, var2.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, var2.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, var3.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, var3.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, var3.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, var3.with(X_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, var3.with(X_ROT_180).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, var3.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, var3.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, var3.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, var1.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, var1.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, var1.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, var1.with(X_ROT_180).with(UV_LOCK)).select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, var1.with(X_ROT_180).with(UV_LOCK)).select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, var1.with(X_ROT_180).with(Y_ROT_180).with(UV_LOCK)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, var1.with(X_ROT_180).with(Y_ROT_90).with(UV_LOCK)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, var1.with(X_ROT_180).with(Y_ROT_270).with(UV_LOCK)));
   }

   private static BlockModelDefinitionGenerator createOrientableTrapdoor(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, var2).select(Direction.SOUTH, Half.BOTTOM, false, var2.with(Y_ROT_180)).select(Direction.EAST, Half.BOTTOM, false, var2.with(Y_ROT_90)).select(Direction.WEST, Half.BOTTOM, false, var2.with(Y_ROT_270)).select(Direction.NORTH, Half.TOP, false, var1).select(Direction.SOUTH, Half.TOP, false, var1.with(Y_ROT_180)).select(Direction.EAST, Half.TOP, false, var1.with(Y_ROT_90)).select(Direction.WEST, Half.TOP, false, var1.with(Y_ROT_270)).select(Direction.NORTH, Half.BOTTOM, true, var3).select(Direction.SOUTH, Half.BOTTOM, true, var3.with(Y_ROT_180)).select(Direction.EAST, Half.BOTTOM, true, var3.with(Y_ROT_90)).select(Direction.WEST, Half.BOTTOM, true, var3.with(Y_ROT_270)).select(Direction.NORTH, Half.TOP, true, var3.with(X_ROT_180).with(Y_ROT_180)).select(Direction.SOUTH, Half.TOP, true, var3.with(X_ROT_180)).select(Direction.EAST, Half.TOP, true, var3.with(X_ROT_180).with(Y_ROT_270)).select(Direction.WEST, Half.TOP, true, var3.with(X_ROT_180).with(Y_ROT_90)));
   }

   private static BlockModelDefinitionGenerator createTrapdoor(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, var2).select(Direction.SOUTH, Half.BOTTOM, false, var2).select(Direction.EAST, Half.BOTTOM, false, var2).select(Direction.WEST, Half.BOTTOM, false, var2).select(Direction.NORTH, Half.TOP, false, var1).select(Direction.SOUTH, Half.TOP, false, var1).select(Direction.EAST, Half.TOP, false, var1).select(Direction.WEST, Half.TOP, false, var1).select(Direction.NORTH, Half.BOTTOM, true, var3).select(Direction.SOUTH, Half.BOTTOM, true, var3.with(Y_ROT_180)).select(Direction.EAST, Half.BOTTOM, true, var3.with(Y_ROT_90)).select(Direction.WEST, Half.BOTTOM, true, var3.with(Y_ROT_270)).select(Direction.NORTH, Half.TOP, true, var3).select(Direction.SOUTH, Half.TOP, true, var3.with(Y_ROT_180)).select(Direction.EAST, Half.TOP, true, var3.with(Y_ROT_90)).select(Direction.WEST, Half.TOP, true, var3.with(Y_ROT_270)));
   }

   static MultiVariantGenerator createSimpleBlock(Block var0, MultiVariant var1) {
      return MultiVariantGenerator.dispatch(var0, var1);
   }

   private static PropertyDispatch<VariantMutator> createRotatedPillar() {
      return PropertyDispatch.modify(BlockStateProperties.AXIS).select(Direction.Axis.Y, NOP).select(Direction.Axis.Z, X_ROT_90).select(Direction.Axis.X, X_ROT_90.then(Y_ROT_90));
   }

   static BlockModelDefinitionGenerator createPillarBlockUVLocked(Block var0, TextureMapping var1, BiConsumer<ResourceLocation, ModelInstance> var2) {
      MultiVariant var3 = plainVariant(ModelTemplates.CUBE_COLUMN_UV_LOCKED_X.create(var0, var1, var2));
      MultiVariant var4 = plainVariant(ModelTemplates.CUBE_COLUMN_UV_LOCKED_Y.create(var0, var1, var2));
      MultiVariant var5 = plainVariant(ModelTemplates.CUBE_COLUMN_UV_LOCKED_Z.create(var0, var1, var2));
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.AXIS).select(Direction.Axis.X, var3).select(Direction.Axis.Y, var4).select(Direction.Axis.Z, var5));
   }

   static BlockModelDefinitionGenerator createAxisAlignedPillarBlock(Block var0, MultiVariant var1) {
      return MultiVariantGenerator.dispatch(var0, var1).with(createRotatedPillar());
   }

   private void createAxisAlignedPillarBlockCustomModel(Block var1, MultiVariant var2) {
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(var1, var2));
   }

   public void createAxisAlignedPillarBlock(Block var1, TexturedModel.Provider var2) {
      MultiVariant var3 = plainVariant(var2.create(var1, this.modelOutput));
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(var1, var3));
   }

   private void createHorizontallyRotatedBlock(Block var1, TexturedModel.Provider var2) {
      MultiVariant var3 = plainVariant(var2.create(var1, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, var3).with(ROTATION_HORIZONTAL_FACING));
   }

   static BlockModelDefinitionGenerator createRotatedPillarWithHorizontalVariant(Block var0, MultiVariant var1, MultiVariant var2) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.AXIS).select(Direction.Axis.Y, var1).select(Direction.Axis.Z, var2.with(X_ROT_90)).select(Direction.Axis.X, var2.with(X_ROT_90).with(Y_ROT_90)));
   }

   private void createRotatedPillarWithHorizontalVariant(Block var1, TexturedModel.Provider var2, TexturedModel.Provider var3) {
      MultiVariant var4 = plainVariant(var2.create(var1, this.modelOutput));
      MultiVariant var5 = plainVariant(var3.create(var1, this.modelOutput));
      this.blockStateOutput.accept(createRotatedPillarWithHorizontalVariant(var1, var4, var5));
   }

   private void createCreakingHeart(Block var1) {
      MultiVariant var2 = plainVariant(TexturedModel.COLUMN_ALT.create(var1, this.modelOutput));
      MultiVariant var3 = plainVariant(TexturedModel.COLUMN_HORIZONTAL_ALT.create(var1, this.modelOutput));
      MultiVariant var4 = plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_ALT, var1, "_awake"));
      MultiVariant var5 = plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_HORIZONTAL_ALT, var1, "_awake"));
      MultiVariant var6 = plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_ALT, var1, "_dormant"));
      MultiVariant var7 = plainVariant(this.createCreakingHeartModel(TexturedModel.COLUMN_HORIZONTAL_ALT, var1, "_dormant"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.AXIS, CreakingHeartBlock.STATE).select(Direction.Axis.Y, CreakingHeartState.UPROOTED, var2).select(Direction.Axis.Z, CreakingHeartState.UPROOTED, var3.with(X_ROT_90)).select(Direction.Axis.X, CreakingHeartState.UPROOTED, var3.with(X_ROT_90).with(Y_ROT_90)).select(Direction.Axis.Y, CreakingHeartState.DORMANT, var6).select(Direction.Axis.Z, CreakingHeartState.DORMANT, var7.with(X_ROT_90)).select(Direction.Axis.X, CreakingHeartState.DORMANT, var7.with(X_ROT_90).with(Y_ROT_90)).select(Direction.Axis.Y, CreakingHeartState.AWAKE, var4).select(Direction.Axis.Z, CreakingHeartState.AWAKE, var5.with(X_ROT_90)).select(Direction.Axis.X, CreakingHeartState.AWAKE, var5.with(X_ROT_90).with(Y_ROT_90))));
   }

   private ResourceLocation createCreakingHeartModel(TexturedModel.Provider var1, Block var2, String var3) {
      return var1.updateTexture((var2x) -> var2x.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(var2, var3)).put(TextureSlot.END, TextureMapping.getBlockTexture(var2, "_top" + var3))).createWithSuffix(var2, var3, this.modelOutput);
   }

   private ResourceLocation createSuffixedVariant(Block var1, String var2, ModelTemplate var3, Function<ResourceLocation, TextureMapping> var4) {
      return var3.createWithSuffix(var1, var2, (TextureMapping)var4.apply(TextureMapping.getBlockTexture(var1, var2)), this.modelOutput);
   }

   static BlockModelDefinitionGenerator createPressurePlate(Block var0, MultiVariant var1, MultiVariant var2) {
      return MultiVariantGenerator.dispatch(var0).with(createBooleanModelDispatch(BlockStateProperties.POWERED, var2, var1));
   }

   static BlockModelDefinitionGenerator createSlab(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.SLAB_TYPE).select(SlabType.BOTTOM, var1).select(SlabType.TOP, var2).select(SlabType.DOUBLE, var3));
   }

   public void createTrivialCube(Block var1) {
      this.createTrivialBlock(var1, TexturedModel.CUBE);
   }

   public void createTrivialBlock(Block var1, TexturedModel.Provider var2) {
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(var2.create(var1, this.modelOutput))));
   }

   public void createTintedLeaves(Block var1, TexturedModel.Provider var2, int var3) {
      ResourceLocation var4 = var2.create(var1, this.modelOutput);
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(var4)));
      this.registerSimpleTintedItemModel(var1, var4, ItemModelUtils.constantTint(var3));
   }

   private void createVine() {
      this.createMultifaceBlockStates(Blocks.VINE);
      ResourceLocation var1 = this.createFlatItemModelWithBlockTexture(Items.VINE, Blocks.VINE);
      this.registerSimpleTintedItemModel(Blocks.VINE, var1, ItemModelUtils.constantTint(-12012264));
   }

   private void createItemWithGrassTint(Block var1) {
      ResourceLocation var2 = this.createFlatItemModelWithBlockTexture(var1.asItem(), var1);
      this.registerSimpleTintedItemModel(var1, var2, new GrassColorSource());
   }

   private BlockFamilyProvider family(Block var1) {
      TexturedModel var2 = (TexturedModel)TEXTURED_MODELS.getOrDefault(var1, TexturedModel.CUBE.get(var1));
      return (new BlockFamilyProvider(var2.getMapping())).fullBlock(var1, var2.getTemplate());
   }

   public void createHangingSign(Block var1, Block var2, Block var3) {
      MultiVariant var4 = this.createParticleOnlyBlockModel(var2, var1);
      this.blockStateOutput.accept(createSimpleBlock(var2, var4));
      this.blockStateOutput.accept(createSimpleBlock(var3, var4));
      this.registerSimpleFlatItemModel(var2.asItem());
   }

   void createDoor(Block var1) {
      TextureMapping var2 = TextureMapping.door(var1);
      MultiVariant var3 = plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT.create(var1, var2, this.modelOutput));
      MultiVariant var4 = plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(var1, var2, this.modelOutput));
      MultiVariant var5 = plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT.create(var1, var2, this.modelOutput));
      MultiVariant var6 = plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(var1, var2, this.modelOutput));
      MultiVariant var7 = plainVariant(ModelTemplates.DOOR_TOP_LEFT.create(var1, var2, this.modelOutput));
      MultiVariant var8 = plainVariant(ModelTemplates.DOOR_TOP_LEFT_OPEN.create(var1, var2, this.modelOutput));
      MultiVariant var9 = plainVariant(ModelTemplates.DOOR_TOP_RIGHT.create(var1, var2, this.modelOutput));
      MultiVariant var10 = plainVariant(ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(var1, var2, this.modelOutput));
      this.registerSimpleFlatItemModel(var1.asItem());
      this.blockStateOutput.accept(createDoor(var1, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   private void copyDoorModel(Block var1, Block var2) {
      MultiVariant var3 = plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT.getDefaultModelLocation(var1));
      MultiVariant var4 = plainVariant(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.getDefaultModelLocation(var1));
      MultiVariant var5 = plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT.getDefaultModelLocation(var1));
      MultiVariant var6 = plainVariant(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.getDefaultModelLocation(var1));
      MultiVariant var7 = plainVariant(ModelTemplates.DOOR_TOP_LEFT.getDefaultModelLocation(var1));
      MultiVariant var8 = plainVariant(ModelTemplates.DOOR_TOP_LEFT_OPEN.getDefaultModelLocation(var1));
      MultiVariant var9 = plainVariant(ModelTemplates.DOOR_TOP_RIGHT.getDefaultModelLocation(var1));
      MultiVariant var10 = plainVariant(ModelTemplates.DOOR_TOP_RIGHT_OPEN.getDefaultModelLocation(var1));
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
      this.blockStateOutput.accept(createDoor(var2, var3, var4, var5, var6, var7, var8, var9, var10));
   }

   void createOrientableTrapdoor(Block var1) {
      TextureMapping var2 = TextureMapping.defaultTexture(var1);
      MultiVariant var3 = plainVariant(ModelTemplates.ORIENTABLE_TRAPDOOR_TOP.create(var1, var2, this.modelOutput));
      ResourceLocation var4 = ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM.create(var1, var2, this.modelOutput);
      MultiVariant var5 = plainVariant(ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN.create(var1, var2, this.modelOutput));
      this.blockStateOutput.accept(createOrientableTrapdoor(var1, var3, plainVariant(var4), var5));
      this.registerSimpleItemModel(var1, var4);
   }

   void createTrapdoor(Block var1) {
      TextureMapping var2 = TextureMapping.defaultTexture(var1);
      MultiVariant var3 = plainVariant(ModelTemplates.TRAPDOOR_TOP.create(var1, var2, this.modelOutput));
      ResourceLocation var4 = ModelTemplates.TRAPDOOR_BOTTOM.create(var1, var2, this.modelOutput);
      MultiVariant var5 = plainVariant(ModelTemplates.TRAPDOOR_OPEN.create(var1, var2, this.modelOutput));
      this.blockStateOutput.accept(createTrapdoor(var1, var3, plainVariant(var4), var5));
      this.registerSimpleItemModel(var1, var4);
   }

   private void copyTrapdoorModel(Block var1, Block var2) {
      MultiVariant var3 = plainVariant(ModelTemplates.TRAPDOOR_TOP.getDefaultModelLocation(var1));
      MultiVariant var4 = plainVariant(ModelTemplates.TRAPDOOR_BOTTOM.getDefaultModelLocation(var1));
      MultiVariant var5 = plainVariant(ModelTemplates.TRAPDOOR_OPEN.getDefaultModelLocation(var1));
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
      this.blockStateOutput.accept(createTrapdoor(var2, var3, var4, var5));
   }

   private void createBigDripLeafBlock() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_partial_tilt"));
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_full_tilt"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.BIG_DRIPLEAF).with(PropertyDispatch.initial(BlockStateProperties.TILT).select(Tilt.NONE, var1).select(Tilt.UNSTABLE, var1).select(Tilt.PARTIAL, var2).select(Tilt.FULL, var3)).with(ROTATION_HORIZONTAL_FACING));
   }

   private WoodProvider woodProvider(Block var1) {
      return new WoodProvider(TextureMapping.logColumn(var1));
   }

   private void createNonTemplateModelBlock(Block var1) {
      this.createNonTemplateModelBlock(var1, var1);
   }

   private void createNonTemplateModelBlock(Block var1, Block var2) {
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(ModelLocationUtils.getModelLocation(var2))));
   }

   private void createCrossBlockWithDefaultItem(Block var1, PlantType var2) {
      this.registerSimpleItemModel(var1.asItem(), var2.createItemModel(this, var1));
      this.createCrossBlock(var1, var2);
   }

   private void createCrossBlockWithDefaultItem(Block var1, PlantType var2, TextureMapping var3) {
      this.registerSimpleFlatItemModel(var1);
      this.createCrossBlock(var1, var2, var3);
   }

   private void createCrossBlock(Block var1, PlantType var2) {
      TextureMapping var3 = var2.getTextureMapping(var1);
      this.createCrossBlock(var1, var2, var3);
   }

   private void createCrossBlock(Block var1, PlantType var2, TextureMapping var3) {
      MultiVariant var4 = plainVariant(var2.getCross().create(var1, var3, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
   }

   private void createCrossBlock(Block var1, PlantType var2, Property<Integer> var3, int... var4) {
      if (var3.getPossibleValues().size() != var4.length) {
         throw new IllegalArgumentException("missing values for property: " + String.valueOf(var3));
      } else {
         this.registerSimpleFlatItemModel(var1.asItem());
         this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(var3).generate((var4x) -> {
            int var10000 = var4[var4x];
            String var5 = "_stage" + var10000;
            TextureMapping var6 = TextureMapping.cross(TextureMapping.getBlockTexture(var1, var5));
            return plainVariant(var2.getCross().createWithSuffix(var1, var5, var6, this.modelOutput));
         })));
      }
   }

   private void createPlantWithDefaultItem(Block var1, Block var2, PlantType var3) {
      this.registerSimpleItemModel(var1.asItem(), var3.createItemModel(this, var1));
      this.createPlant(var1, var2, var3);
   }

   private void createPlant(Block var1, Block var2, PlantType var3) {
      this.createCrossBlock(var1, var3);
      TextureMapping var4 = var3.getPlantTextureMapping(var1);
      MultiVariant var5 = plainVariant(var3.getCrossPot().create(var2, var4, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var2, var5));
   }

   private void createCoralFans(Block var1, Block var2) {
      TexturedModel var3 = TexturedModel.CORAL_FAN.get(var1);
      MultiVariant var4 = plainVariant(var3.create(var1, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
      MultiVariant var5 = plainVariant(ModelTemplates.CORAL_WALL_FAN.create(var2, var3.getMapping(), this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2, var5).with(ROTATION_HORIZONTAL_FACING));
      this.registerSimpleFlatItemModel(var1);
   }

   private void createStems(Block var1, Block var2) {
      this.registerSimpleFlatItemModel(var1.asItem());
      TextureMapping var3 = TextureMapping.stem(var1);
      TextureMapping var4 = TextureMapping.attachedStem(var1, var2);
      MultiVariant var5 = plainVariant(ModelTemplates.ATTACHED_STEM.create(var2, var4, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2, var5).with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.WEST, NOP).select(Direction.SOUTH, Y_ROT_270).select(Direction.NORTH, Y_ROT_90).select(Direction.EAST, Y_ROT_180)));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.AGE_7).generate((var3x) -> plainVariant(ModelTemplates.STEMS[var3x].create(var1, var3, this.modelOutput)))));
   }

   private void createPitcherPlant() {
      Block var1 = Blocks.PITCHER_PLANT;
      this.registerSimpleFlatItemModel(var1.asItem());
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_top"));
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_bottom"));
      this.createDoubleBlock(var1, var2, var3);
   }

   private void createPitcherCrop() {
      Block var1 = Blocks.PITCHER_CROP;
      this.registerSimpleFlatItemModel(var1.asItem());
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(PitcherCropBlock.AGE, BlockStateProperties.DOUBLE_BLOCK_HALF).generate((var1x, var2) -> {
         MultiVariant var10000;
         switch (var2) {
            case UPPER -> var10000 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_top_stage_" + var1x));
            case LOWER -> var10000 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_bottom_stage_" + var1x));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      })));
   }

   private void createCoral(Block var1, Block var2, Block var3, Block var4, Block var5, Block var6, Block var7, Block var8) {
      this.createCrossBlockWithDefaultItem(var1, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createCrossBlockWithDefaultItem(var2, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTrivialCube(var3);
      this.createTrivialCube(var4);
      this.createCoralFans(var5, var7);
      this.createCoralFans(var6, var8);
   }

   private void createDoublePlant(Block var1, PlantType var2) {
      MultiVariant var3 = plainVariant(this.createSuffixedVariant(var1, "_top", var2.getCross(), TextureMapping::cross));
      MultiVariant var4 = plainVariant(this.createSuffixedVariant(var1, "_bottom", var2.getCross(), TextureMapping::cross));
      this.createDoubleBlock(var1, var3, var4);
   }

   private void createDoublePlantWithDefaultItem(Block var1, PlantType var2) {
      this.registerSimpleFlatItemModel(var1, "_top");
      this.createDoublePlant(var1, var2);
   }

   private void createTintedDoublePlant(Block var1) {
      ResourceLocation var2 = this.createFlatItemModelWithBlockTexture(var1.asItem(), var1, "_top");
      this.registerSimpleTintedItemModel(var1, var2, new GrassColorSource());
      this.createDoublePlant(var1, BlockModelGenerators.PlantType.TINTED);
   }

   private void createSunflower() {
      this.registerSimpleFlatItemModel(Blocks.SUNFLOWER, "_front");
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.SUNFLOWER, "_top"));
      MultiVariant var2 = plainVariant(this.createSuffixedVariant(Blocks.SUNFLOWER, "_bottom", BlockModelGenerators.PlantType.NOT_TINTED.getCross(), TextureMapping::cross));
      this.createDoubleBlock(Blocks.SUNFLOWER, var1, var2);
   }

   private void createTallSeagrass() {
      MultiVariant var1 = plainVariant(this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_top", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture));
      MultiVariant var2 = plainVariant(this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_bottom", ModelTemplates.SEAGRASS, TextureMapping::defaultTexture));
      this.createDoubleBlock(Blocks.TALL_SEAGRASS, var1, var2);
   }

   private void createSmallDripleaf() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_top"));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_bottom"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SMALL_DRIPLEAF).with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, var2).select(DoubleBlockHalf.UPPER, var1)).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createDoubleBlock(Block var1, MultiVariant var2, MultiVariant var3) {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, var3).select(DoubleBlockHalf.UPPER, var2)));
   }

   private void createPassiveRail(Block var1) {
      TextureMapping var2 = TextureMapping.rail(var1);
      TextureMapping var3 = TextureMapping.rail(TextureMapping.getBlockTexture(var1, "_corner"));
      MultiVariant var4 = plainVariant(ModelTemplates.RAIL_FLAT.create(var1, var2, this.modelOutput));
      MultiVariant var5 = plainVariant(ModelTemplates.RAIL_CURVED.create(var1, var3, this.modelOutput));
      MultiVariant var6 = plainVariant(ModelTemplates.RAIL_RAISED_NE.create(var1, var2, this.modelOutput));
      MultiVariant var7 = plainVariant(ModelTemplates.RAIL_RAISED_SW.create(var1, var2, this.modelOutput));
      this.registerSimpleFlatItemModel(var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE).select(RailShape.NORTH_SOUTH, var4).select(RailShape.EAST_WEST, var4.with(Y_ROT_90)).select(RailShape.ASCENDING_EAST, var6.with(Y_ROT_90)).select(RailShape.ASCENDING_WEST, var7.with(Y_ROT_90)).select(RailShape.ASCENDING_NORTH, var6).select(RailShape.ASCENDING_SOUTH, var7).select(RailShape.SOUTH_EAST, var5).select(RailShape.SOUTH_WEST, var5.with(Y_ROT_90)).select(RailShape.NORTH_WEST, var5.with(Y_ROT_180)).select(RailShape.NORTH_EAST, var5.with(Y_ROT_270))));
   }

   private void createActiveRail(Block var1) {
      MultiVariant var2 = plainVariant(this.createSuffixedVariant(var1, "", ModelTemplates.RAIL_FLAT, TextureMapping::rail));
      MultiVariant var3 = plainVariant(this.createSuffixedVariant(var1, "", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail));
      MultiVariant var4 = plainVariant(this.createSuffixedVariant(var1, "", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail));
      MultiVariant var5 = plainVariant(this.createSuffixedVariant(var1, "_on", ModelTemplates.RAIL_FLAT, TextureMapping::rail));
      MultiVariant var6 = plainVariant(this.createSuffixedVariant(var1, "_on", ModelTemplates.RAIL_RAISED_NE, TextureMapping::rail));
      MultiVariant var7 = plainVariant(this.createSuffixedVariant(var1, "_on", ModelTemplates.RAIL_RAISED_SW, TextureMapping::rail));
      this.registerSimpleFlatItemModel(var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((var6x, var7x) -> {
         MultiVariant var10000;
         switch (var7x) {
            case NORTH_SOUTH -> var10000 = var6x ? var5 : var2;
            case EAST_WEST -> var10000 = (var6x ? var5 : var2).with(Y_ROT_90);
            case ASCENDING_EAST -> var10000 = (var6x ? var6 : var3).with(Y_ROT_90);
            case ASCENDING_WEST -> var10000 = (var6x ? var7 : var4).with(Y_ROT_90);
            case ASCENDING_NORTH -> var10000 = var6x ? var6 : var3;
            case ASCENDING_SOUTH -> var10000 = var6x ? var7 : var4;
            default -> throw new UnsupportedOperationException("Fix you generator!");
         }

         return var10000;
      })));
   }

   private void createAirLikeBlock(Block var1, Item var2) {
      MultiVariant var3 = plainVariant(ModelTemplates.PARTICLE_ONLY.create(var1, TextureMapping.particleFromItem(var2), this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var1, var3));
   }

   private void createAirLikeBlock(Block var1, ResourceLocation var2) {
      MultiVariant var3 = plainVariant(ModelTemplates.PARTICLE_ONLY.create(var1, TextureMapping.particle(var2), this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var1, var3));
   }

   private MultiVariant createParticleOnlyBlockModel(Block var1, Block var2) {
      return plainVariant(ModelTemplates.PARTICLE_ONLY.create(var1, TextureMapping.particle(var2), this.modelOutput));
   }

   public void createParticleOnlyBlock(Block var1, Block var2) {
      this.blockStateOutput.accept(createSimpleBlock(var1, this.createParticleOnlyBlockModel(var1, var2)));
   }

   private void createParticleOnlyBlock(Block var1) {
      this.createParticleOnlyBlock(var1, var1);
   }

   private void createFullAndCarpetBlocks(Block var1, Block var2) {
      this.createTrivialCube(var1);
      MultiVariant var3 = plainVariant(TexturedModel.CARPET.get(var1).create(var2, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var2, var3));
   }

   private void createLeafLitter(Block var1) {
      MultiVariant var2 = plainVariant(TexturedModel.LEAF_LITTER_1.create(var1, this.modelOutput));
      MultiVariant var3 = plainVariant(TexturedModel.LEAF_LITTER_2.create(var1, this.modelOutput));
      MultiVariant var4 = plainVariant(TexturedModel.LEAF_LITTER_3.create(var1, this.modelOutput));
      MultiVariant var5 = plainVariant(TexturedModel.LEAF_LITTER_4.create(var1, this.modelOutput));
      this.registerSimpleFlatItemModel(var1.asItem());
      this.createSegmentedBlock(var1, var2, LEAF_LITTER_MODEL_1_SEGMENT_CONDITION, var3, LEAF_LITTER_MODEL_2_SEGMENT_CONDITION, var4, LEAF_LITTER_MODEL_3_SEGMENT_CONDITION, var5, LEAF_LITTER_MODEL_4_SEGMENT_CONDITION);
   }

   private void createFlowerBed(Block var1) {
      MultiVariant var2 = plainVariant(TexturedModel.FLOWERBED_1.create(var1, this.modelOutput));
      MultiVariant var3 = plainVariant(TexturedModel.FLOWERBED_2.create(var1, this.modelOutput));
      MultiVariant var4 = plainVariant(TexturedModel.FLOWERBED_3.create(var1, this.modelOutput));
      MultiVariant var5 = plainVariant(TexturedModel.FLOWERBED_4.create(var1, this.modelOutput));
      this.registerSimpleFlatItemModel(var1.asItem());
      this.createSegmentedBlock(var1, var2, FLOWER_BED_MODEL_1_SEGMENT_CONDITION, var3, FLOWER_BED_MODEL_2_SEGMENT_CONDITION, var4, FLOWER_BED_MODEL_3_SEGMENT_CONDITION, var5, FLOWER_BED_MODEL_4_SEGMENT_CONDITION);
   }

   private void createSegmentedBlock(Block var1, MultiVariant var2, Function<ConditionBuilder, ConditionBuilder> var3, MultiVariant var4, Function<ConditionBuilder, ConditionBuilder> var5, MultiVariant var6, Function<ConditionBuilder, ConditionBuilder> var7, MultiVariant var8, Function<ConditionBuilder, ConditionBuilder> var9) {
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(var1).with((ConditionBuilder)var3.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), var2).with((ConditionBuilder)var3.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), var2.with(Y_ROT_90)).with((ConditionBuilder)var3.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), var2.with(Y_ROT_180)).with((ConditionBuilder)var3.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), var2.with(Y_ROT_270)).with((ConditionBuilder)var5.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), var4).with((ConditionBuilder)var5.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), var4.with(Y_ROT_90)).with((ConditionBuilder)var5.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), var4.with(Y_ROT_180)).with((ConditionBuilder)var5.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), var4.with(Y_ROT_270)).with((ConditionBuilder)var7.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), var6).with((ConditionBuilder)var7.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), var6.with(Y_ROT_90)).with((ConditionBuilder)var7.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), var6.with(Y_ROT_180)).with((ConditionBuilder)var7.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), var6.with(Y_ROT_270)).with((ConditionBuilder)var9.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), var8).with((ConditionBuilder)var9.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), var8.with(Y_ROT_90)).with((ConditionBuilder)var9.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), var8.with(Y_ROT_180)).with((ConditionBuilder)var9.apply(condition().term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), var8.with(Y_ROT_270)));
   }

   private void createColoredBlockWithRandomRotations(TexturedModel.Provider var1, Block... var2) {
      for(Block var6 : var2) {
         Variant var7 = plainModel(var1.create(var6, this.modelOutput));
         this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var6, createRotatedVariants(var7)));
      }

   }

   private void createColoredBlockWithStateRotations(TexturedModel.Provider var1, Block... var2) {
      for(Block var6 : var2) {
         MultiVariant var7 = plainVariant(var1.create(var6, this.modelOutput));
         this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var6, var7).with(ROTATION_HORIZONTAL_FACING_ALT));
      }

   }

   private void createGlassBlocks(Block var1, Block var2) {
      this.createTrivialCube(var1);
      TextureMapping var3 = TextureMapping.pane(var1, var2);
      MultiVariant var4 = plainVariant(ModelTemplates.STAINED_GLASS_PANE_POST.create(var2, var3, this.modelOutput));
      MultiVariant var5 = plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE.create(var2, var3, this.modelOutput));
      MultiVariant var6 = plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(var2, var3, this.modelOutput));
      MultiVariant var7 = plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(var2, var3, this.modelOutput));
      MultiVariant var8 = plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(var2, var3, this.modelOutput));
      Item var9 = var2.asItem();
      this.registerSimpleItemModel(var9, this.createFlatItemModelWithBlockTexture(var9, var1));
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(var2).with(var4).with(condition().term(BlockStateProperties.NORTH, true), var5).with(condition().term(BlockStateProperties.EAST, true), var5.with(Y_ROT_90)).with(condition().term(BlockStateProperties.SOUTH, true), var6).with(condition().term(BlockStateProperties.WEST, true), var6.with(Y_ROT_90)).with(condition().term(BlockStateProperties.NORTH, false), var7).with(condition().term(BlockStateProperties.EAST, false), var8).with(condition().term(BlockStateProperties.SOUTH, false), var8.with(Y_ROT_90)).with(condition().term(BlockStateProperties.WEST, false), var7.with(Y_ROT_270)));
   }

   private void createCommandBlock(Block var1) {
      TextureMapping var2 = TextureMapping.commandBlock(var1);
      MultiVariant var3 = plainVariant(ModelTemplates.COMMAND_BLOCK.create(var1, var2, this.modelOutput));
      MultiVariant var4 = plainVariant(this.createSuffixedVariant(var1, "_conditional", ModelTemplates.COMMAND_BLOCK, (var1x) -> var2.copyAndUpdate(TextureSlot.SIDE, var1x)));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createBooleanModelDispatch(BlockStateProperties.CONDITIONAL, var4, var3)).with(ROTATION_FACING));
   }

   private void createAnvil(Block var1) {
      MultiVariant var2 = plainVariant(TexturedModel.ANVIL.create(var1, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var1, var2).with(ROTATION_HORIZONTAL_FACING_ALT));
   }

   private static MultiVariant createBambooModels(int var0) {
      String var1 = "_age" + var0;
      return new MultiVariant(WeightedList.of((List)IntStream.range(1, 5).mapToObj((var1x) -> new Weighted(plainModel(ModelLocationUtils.getModelLocation(Blocks.BAMBOO, var1x + var1)), 1)).collect(Collectors.toList())));
   }

   private void createBamboo() {
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.BAMBOO).with(condition().term(BlockStateProperties.AGE_1, 0), createBambooModels(0)).with(condition().term(BlockStateProperties.AGE_1, 1), createBambooModels(1)).with(condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.SMALL), plainVariant(ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_small_leaves"))).with(condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE), plainVariant(ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_large_leaves"))));
   }

   private void createBarrel() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
      MultiVariant var2 = plainVariant(TexturedModel.CUBE_TOP_BOTTOM.create(Blocks.BARREL, this.modelOutput));
      MultiVariant var3 = plainVariant(TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.BARREL).updateTextures((var1x) -> var1x.put(TextureSlot.TOP, var1)).createWithSuffix(Blocks.BARREL, "_open", this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.BARREL).with(PropertyDispatch.initial(BlockStateProperties.OPEN).select(false, var2).select(true, var3)).with(ROTATIONS_COLUMN_WITH_FACING));
   }

   private static <T extends Comparable<T>> PropertyDispatch<MultiVariant> createEmptyOrFullDispatch(Property<T> var0, T var1, MultiVariant var2, MultiVariant var3) {
      return PropertyDispatch.initial(var0).generate((var3x) -> {
         boolean var4 = var3x.compareTo(var1) >= 0;
         return var4 ? var2 : var3;
      });
   }

   private void createBeeNest(Block var1, Function<Block, TextureMapping> var2) {
      TextureMapping var3 = ((TextureMapping)var2.apply(var1)).copyForced(TextureSlot.SIDE, TextureSlot.PARTICLE);
      TextureMapping var4 = var3.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1, "_front_honey"));
      ResourceLocation var5 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(var1, "_empty", var3, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix(var1, "_honey", var4, this.modelOutput);
      this.itemModelOutput.accept(var1.asItem(), ItemModelUtils.selectBlockItemProperty(BeehiveBlock.HONEY_LEVEL, ItemModelUtils.plainModel(var5), Map.of(5, ItemModelUtils.plainModel(var6))));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createEmptyOrFullDispatch(BeehiveBlock.HONEY_LEVEL, 5, plainVariant(var6), plainVariant(var5))).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createCropBlock(Block var1, Property<Integer> var2, int... var3) {
      this.registerSimpleFlatItemModel(var1.asItem());
      if (var2.getPossibleValues().size() != var3.length) {
         throw new IllegalArgumentException();
      } else {
         Int2ObjectOpenHashMap var4 = new Int2ObjectOpenHashMap();
         this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(var2).generate((var4x) -> {
            int var5 = var3[var4x];
            return plainVariant((ResourceLocation)var4.computeIfAbsent(var5, (var2) -> this.createSuffixedVariant(var1, "_stage" + var2, ModelTemplates.CROP, TextureMapping::crop)));
         })));
      }
   }

   private void createBell() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_floor"));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_ceiling"));
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_wall"));
      MultiVariant var4 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.BELL, "_between_walls"));
      this.registerSimpleFlatItemModel(Items.BELL);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.BELL).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT).select(Direction.NORTH, BellAttachType.FLOOR, var1).select(Direction.SOUTH, BellAttachType.FLOOR, var1.with(Y_ROT_180)).select(Direction.EAST, BellAttachType.FLOOR, var1.with(Y_ROT_90)).select(Direction.WEST, BellAttachType.FLOOR, var1.with(Y_ROT_270)).select(Direction.NORTH, BellAttachType.CEILING, var2).select(Direction.SOUTH, BellAttachType.CEILING, var2.with(Y_ROT_180)).select(Direction.EAST, BellAttachType.CEILING, var2.with(Y_ROT_90)).select(Direction.WEST, BellAttachType.CEILING, var2.with(Y_ROT_270)).select(Direction.NORTH, BellAttachType.SINGLE_WALL, var3.with(Y_ROT_270)).select(Direction.SOUTH, BellAttachType.SINGLE_WALL, var3.with(Y_ROT_90)).select(Direction.EAST, BellAttachType.SINGLE_WALL, var3).select(Direction.WEST, BellAttachType.SINGLE_WALL, var3.with(Y_ROT_180)).select(Direction.SOUTH, BellAttachType.DOUBLE_WALL, var4.with(Y_ROT_90)).select(Direction.NORTH, BellAttachType.DOUBLE_WALL, var4.with(Y_ROT_270)).select(Direction.EAST, BellAttachType.DOUBLE_WALL, var4).select(Direction.WEST, BellAttachType.DOUBLE_WALL, var4.with(Y_ROT_180))));
   }

   private void createGrindstone() {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.GRINDSTONE, plainVariant(ModelLocationUtils.getModelLocation(Blocks.GRINDSTONE))).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.WALL, Direction.NORTH, X_ROT_90).select(AttachFace.WALL, Direction.EAST, X_ROT_90.then(Y_ROT_90)).select(AttachFace.WALL, Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(AttachFace.WALL, Direction.WEST, X_ROT_90.then(Y_ROT_270)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.WEST, X_ROT_180.then(Y_ROT_90)).select(AttachFace.CEILING, Direction.NORTH, X_ROT_180.then(Y_ROT_180)).select(AttachFace.CEILING, Direction.EAST, X_ROT_180.then(Y_ROT_270))));
   }

   private void createFurnace(Block var1, TexturedModel.Provider var2) {
      MultiVariant var3 = plainVariant(var2.create(var1, this.modelOutput));
      ResourceLocation var4 = TextureMapping.getBlockTexture(var1, "_front_on");
      MultiVariant var5 = plainVariant(var2.get(var1).updateTextures((var1x) -> var1x.put(TextureSlot.FRONT, var4)).createWithSuffix(var1, "_on", this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createBooleanModelDispatch(BlockStateProperties.LIT, var5, var3)).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createCampfires(Block... var1) {
      MultiVariant var2 = plainVariant(ModelLocationUtils.decorateBlockModelLocation("campfire_off"));

      for(Block var6 : var1) {
         MultiVariant var7 = plainVariant(ModelTemplates.CAMPFIRE.create(var6, TextureMapping.campfire(var6), this.modelOutput));
         this.registerSimpleFlatItemModel(var6.asItem());
         this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var6).with(createBooleanModelDispatch(BlockStateProperties.LIT, var7, var2)).with(ROTATION_HORIZONTAL_FACING_ALT));
      }

   }

   private void createAzalea(Block var1) {
      MultiVariant var2 = plainVariant(ModelTemplates.AZALEA.create(var1, TextureMapping.cubeTop(var1), this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var1, var2));
   }

   private void createPottedAzalea(Block var1) {
      MultiVariant var2;
      if (var1 == Blocks.POTTED_FLOWERING_AZALEA) {
         var2 = plainVariant(ModelTemplates.POTTED_FLOWERING_AZALEA.create(var1, TextureMapping.pottedAzalea(var1), this.modelOutput));
      } else {
         var2 = plainVariant(ModelTemplates.POTTED_AZALEA.create(var1, TextureMapping.pottedAzalea(var1), this.modelOutput));
      }

      this.blockStateOutput.accept(createSimpleBlock(var1, var2));
   }

   private void createBookshelf() {
      TextureMapping var1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.BOOKSHELF), TextureMapping.getBlockTexture(Blocks.OAK_PLANKS));
      MultiVariant var2 = plainVariant(ModelTemplates.CUBE_COLUMN.create(Blocks.BOOKSHELF, var1, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.BOOKSHELF, var2));
   }

   private void createRedstoneWire() {
      this.registerSimpleFlatItemModel(Items.REDSTONE);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.REDSTONE_WIRE).with(or(condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.NONE), condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP).term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP)), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_dot"))).with(condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side0"))).with(condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt0"))).with(condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt1")).with(Y_ROT_270)).with(condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.SIDE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side1")).with(Y_ROT_270)).with(condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))).with(condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(Y_ROT_90)).with(condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(Y_ROT_180)).with(condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.UP), plainVariant(ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(Y_ROT_270)));
   }

   private void createComparator() {
      this.registerSimpleFlatItemModel(Items.COMPARATOR);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.COMPARATOR).with(PropertyDispatch.initial(BlockStateProperties.MODE_COMPARATOR, BlockStateProperties.POWERED).select(ComparatorMode.COMPARE, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR))).select(ComparatorMode.COMPARE, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on"))).select(ComparatorMode.SUBTRACT, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_subtract"))).select(ComparatorMode.SUBTRACT, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on_subtract")))).with(ROTATION_HORIZONTAL_FACING_ALT));
   }

   private void createSmoothStoneSlab() {
      TextureMapping var1 = TextureMapping.cube(Blocks.SMOOTH_STONE);
      TextureMapping var2 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), var1.get(TextureSlot.TOP));
      MultiVariant var3 = plainVariant(ModelTemplates.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, var2, this.modelOutput));
      MultiVariant var4 = plainVariant(ModelTemplates.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, var2, this.modelOutput));
      MultiVariant var5 = plainVariant(ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", var2, this.modelOutput));
      this.blockStateOutput.accept(createSlab(Blocks.SMOOTH_STONE_SLAB, var3, var4, var5));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMOOTH_STONE, plainVariant(ModelTemplates.CUBE_ALL.create(Blocks.SMOOTH_STONE, var1, this.modelOutput))));
   }

   private void createBrewingStand() {
      this.registerSimpleFlatItemModel(Items.BREWING_STAND);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.BREWING_STAND).with(plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND))).with(condition().term(BlockStateProperties.HAS_BOTTLE_0, true), plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle0"))).with(condition().term(BlockStateProperties.HAS_BOTTLE_1, true), plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle1"))).with(condition().term(BlockStateProperties.HAS_BOTTLE_2, true), plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle2"))).with(condition().term(BlockStateProperties.HAS_BOTTLE_0, false), plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty0"))).with(condition().term(BlockStateProperties.HAS_BOTTLE_1, false), plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty1"))).with(condition().term(BlockStateProperties.HAS_BOTTLE_2, false), plainVariant(TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty2"))));
   }

   private void createMushroomBlock(Block var1) {
      MultiVariant var2 = plainVariant(ModelTemplates.SINGLE_FACE.create(var1, TextureMapping.defaultTexture(var1), this.modelOutput));
      MultiVariant var3 = plainVariant(ModelLocationUtils.decorateBlockModelLocation("mushroom_block_inside"));
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(var1).with(condition().term(BlockStateProperties.NORTH, true), var2).with(condition().term(BlockStateProperties.EAST, true), var2.with(Y_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.SOUTH, true), var2.with(Y_ROT_180).with(UV_LOCK)).with(condition().term(BlockStateProperties.WEST, true), var2.with(Y_ROT_270).with(UV_LOCK)).with(condition().term(BlockStateProperties.UP, true), var2.with(X_ROT_270).with(UV_LOCK)).with(condition().term(BlockStateProperties.DOWN, true), var2.with(X_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.NORTH, false), var3).with(condition().term(BlockStateProperties.EAST, false), var3.with(Y_ROT_90)).with(condition().term(BlockStateProperties.SOUTH, false), var3.with(Y_ROT_180)).with(condition().term(BlockStateProperties.WEST, false), var3.with(Y_ROT_270)).with(condition().term(BlockStateProperties.UP, false), var3.with(X_ROT_270)).with(condition().term(BlockStateProperties.DOWN, false), var3.with(X_ROT_90)));
      this.registerSimpleItemModel(var1, TexturedModel.CUBE.createWithSuffix(var1, "_inventory", this.modelOutput));
   }

   private void createCakeBlock() {
      this.registerSimpleFlatItemModel(Items.CAKE);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CAKE).with(PropertyDispatch.initial(BlockStateProperties.BITES).select(0, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE))).select(1, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice1"))).select(2, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice2"))).select(3, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice3"))).select(4, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice4"))).select(5, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice5"))).select(6, plainVariant(ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice6")))));
   }

   private void createCartographyTable() {
      TextureMapping var1 = (new TextureMapping()).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.DARK_OAK_PLANKS)).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.CARTOGRAPHY_TABLE, plainVariant(ModelTemplates.CUBE.create(Blocks.CARTOGRAPHY_TABLE, var1, this.modelOutput))));
   }

   private void createSmithingTable() {
      TextureMapping var1 = (new TextureMapping()).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_bottom")).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SMITHING_TABLE, plainVariant(ModelTemplates.CUBE.create(Blocks.SMITHING_TABLE, var1, this.modelOutput))));
   }

   private void createCraftingTableLike(Block var1, Block var2, BiFunction<Block, Block, TextureMapping> var3) {
      TextureMapping var4 = (TextureMapping)var3.apply(var1, var2);
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(ModelTemplates.CUBE.create(var1, var4, this.modelOutput))));
   }

   public void createGenericCube(Block var1) {
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(var1, "_particle")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(var1, "_down")).put(TextureSlot.UP, TextureMapping.getBlockTexture(var1, "_up")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(var1, "_north")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(var1, "_south")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(var1, "_east")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(var1, "_west"));
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(ModelTemplates.CUBE.create(var1, var2, this.modelOutput))));
   }

   private void createPumpkins() {
      TextureMapping var1 = TextureMapping.column(Blocks.PUMPKIN);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.PUMPKIN, plainVariant(ModelLocationUtils.getModelLocation(Blocks.PUMPKIN))));
      this.createPumpkinVariant(Blocks.CARVED_PUMPKIN, var1);
      this.createPumpkinVariant(Blocks.JACK_O_LANTERN, var1);
   }

   private void createPumpkinVariant(Block var1, TextureMapping var2) {
      MultiVariant var3 = plainVariant(ModelTemplates.CUBE_ORIENTABLE.create(var1, var2.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1)), this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, var3).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createCauldrons() {
      this.registerSimpleFlatItemModel(Items.CAULDRON);
      this.createNonTemplateModelBlock(Blocks.CAULDRON);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.LAVA_CAULDRON, plainVariant(ModelTemplates.CAULDRON_FULL.create(Blocks.LAVA_CAULDRON, TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.LAVA, "_still")), this.modelOutput))));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.WATER_CAULDRON).with(PropertyDispatch.initial(LayeredCauldronBlock.LEVEL).select(1, plainVariant(ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.WATER_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select(2, plainVariant(ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.WATER_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select(3, plainVariant(ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.WATER_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.POWDER_SNOW_CAULDRON).with(PropertyDispatch.initial(LayeredCauldronBlock.LEVEL).select(1, plainVariant(ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select(2, plainVariant(ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select(3, plainVariant(ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput)))));
   }

   private void createChorusFlower() {
      TextureMapping var1 = TextureMapping.defaultTexture(Blocks.CHORUS_FLOWER);
      MultiVariant var2 = plainVariant(ModelTemplates.CHORUS_FLOWER.create(Blocks.CHORUS_FLOWER, var1, this.modelOutput));
      MultiVariant var3 = plainVariant(this.createSuffixedVariant(Blocks.CHORUS_FLOWER, "_dead", ModelTemplates.CHORUS_FLOWER, (var1x) -> var1.copyAndUpdate(TextureSlot.TEXTURE, var1x)));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CHORUS_FLOWER).with(createEmptyOrFullDispatch(BlockStateProperties.AGE_5, 5, var3, var2)));
   }

   private void createCrafterBlock() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_triggered"));
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_crafting"));
      MultiVariant var4 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.CRAFTER, "_crafting_triggered"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CRAFTER).with(PropertyDispatch.initial(BlockStateProperties.TRIGGERED, CrafterBlock.CRAFTING).select(false, false, var1).select(true, true, var4).select(true, false, var2).select(false, true, var3)).with(PropertyDispatch.modify(BlockStateProperties.ORIENTATION).generate(BlockModelGenerators::applyRotation)));
   }

   private void createDispenserBlock(Block var1) {
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1, "_front"));
      TextureMapping var3 = (new TextureMapping()).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(var1, "_front_vertical"));
      MultiVariant var4 = plainVariant(ModelTemplates.CUBE_ORIENTABLE.create(var1, var2, this.modelOutput));
      MultiVariant var5 = plainVariant(ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create(var1, var3, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.FACING).select(Direction.DOWN, var5.with(X_ROT_180)).select(Direction.UP, var5).select(Direction.NORTH, var4).select(Direction.EAST, var4.with(Y_ROT_90)).select(Direction.SOUTH, var4.with(Y_ROT_180)).select(Direction.WEST, var4.with(Y_ROT_270))));
   }

   private void createEndPortalFrame() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME, "_filled"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.END_PORTAL_FRAME).with(PropertyDispatch.initial(BlockStateProperties.EYE).select(false, var1).select(true, var2)).with(ROTATION_HORIZONTAL_FACING_ALT));
   }

   private void createChorusPlant() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_side"));
      Variant var2 = plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside"));
      Variant var3 = plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside1"));
      Variant var4 = plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside2"));
      Variant var5 = plainModel(ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside3"));
      Variant var6 = var2.with(UV_LOCK);
      Variant var7 = var3.with(UV_LOCK);
      Variant var8 = var4.with(UV_LOCK);
      Variant var9 = var5.with(UV_LOCK);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.CHORUS_PLANT).with(condition().term(BlockStateProperties.NORTH, true), var1).with(condition().term(BlockStateProperties.EAST, true), var1.with(Y_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.SOUTH, true), var1.with(Y_ROT_180).with(UV_LOCK)).with(condition().term(BlockStateProperties.WEST, true), var1.with(Y_ROT_270).with(UV_LOCK)).with(condition().term(BlockStateProperties.UP, true), var1.with(X_ROT_270).with(UV_LOCK)).with(condition().term(BlockStateProperties.DOWN, true), var1.with(X_ROT_90).with(UV_LOCK)).with(condition().term(BlockStateProperties.NORTH, false), new MultiVariant(WeightedList.of(new Weighted(var2, 2), new Weighted(var3, 1), new Weighted(var4, 1), new Weighted(var5, 1)))).with(condition().term(BlockStateProperties.EAST, false), new MultiVariant(WeightedList.of(new Weighted(var7.with(Y_ROT_90), 1), new Weighted(var8.with(Y_ROT_90), 1), new Weighted(var9.with(Y_ROT_90), 1), new Weighted(var6.with(Y_ROT_90), 2)))).with(condition().term(BlockStateProperties.SOUTH, false), new MultiVariant(WeightedList.of(new Weighted(var8.with(Y_ROT_180), 1), new Weighted(var9.with(Y_ROT_180), 1), new Weighted(var6.with(Y_ROT_180), 2), new Weighted(var7.with(Y_ROT_180), 1)))).with(condition().term(BlockStateProperties.WEST, false), new MultiVariant(WeightedList.of(new Weighted(var9.with(Y_ROT_270), 1), new Weighted(var6.with(Y_ROT_270), 2), new Weighted(var7.with(Y_ROT_270), 1), new Weighted(var8.with(Y_ROT_270), 1)))).with(condition().term(BlockStateProperties.UP, false), new MultiVariant(WeightedList.of(new Weighted(var6.with(X_ROT_270), 2), new Weighted(var9.with(X_ROT_270), 1), new Weighted(var7.with(X_ROT_270), 1), new Weighted(var8.with(X_ROT_270), 1)))).with(condition().term(BlockStateProperties.DOWN, false), new MultiVariant(WeightedList.of(new Weighted(var9.with(X_ROT_90), 1), new Weighted(var8.with(X_ROT_90), 1), new Weighted(var7.with(X_ROT_90), 1), new Weighted(var6.with(X_ROT_90), 2)))));
   }

   private void createComposter() {
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.COMPOSTER).with(plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7"))).with(condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), plainVariant(TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
   }

   private void createCopperBulb(Block var1) {
      MultiVariant var2 = plainVariant(ModelTemplates.CUBE_ALL.create(var1, TextureMapping.cube(var1), this.modelOutput));
      MultiVariant var3 = plainVariant(this.createSuffixedVariant(var1, "_powered", ModelTemplates.CUBE_ALL, TextureMapping::cube));
      MultiVariant var4 = plainVariant(this.createSuffixedVariant(var1, "_lit", ModelTemplates.CUBE_ALL, TextureMapping::cube));
      MultiVariant var5 = plainVariant(this.createSuffixedVariant(var1, "_lit_powered", ModelTemplates.CUBE_ALL, TextureMapping::cube));
      this.blockStateOutput.accept(createCopperBulb(var1, var2, var4, var3, var5));
   }

   private static BlockModelDefinitionGenerator createCopperBulb(Block var0, MultiVariant var1, MultiVariant var2, MultiVariant var3, MultiVariant var4) {
      return MultiVariantGenerator.dispatch(var0).with(PropertyDispatch.initial(BlockStateProperties.LIT, BlockStateProperties.POWERED).generate((var4x, var5) -> {
         if (var4x) {
            return var5 ? var4 : var2;
         } else {
            return var5 ? var3 : var1;
         }
      }));
   }

   private void copyCopperBulbModel(Block var1, Block var2) {
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(var1));
      MultiVariant var4 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_powered"));
      MultiVariant var5 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_lit"));
      MultiVariant var6 = plainVariant(ModelLocationUtils.getModelLocation(var1, "_lit_powered"));
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
      this.blockStateOutput.accept(createCopperBulb(var2, var3, var5, var4, var6));
   }

   private void createAmethystCluster(Block var1) {
      MultiVariant var2 = plainVariant(ModelTemplates.CROSS.create(var1, TextureMapping.cross(var1), this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, var2).with(ROTATIONS_COLUMN_WITH_FACING));
   }

   private void createAmethystClusters() {
      this.createAmethystCluster(Blocks.SMALL_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.MEDIUM_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.LARGE_AMETHYST_BUD);
      this.createAmethystCluster(Blocks.AMETHYST_CLUSTER);
   }

   private void createPointedDripstone() {
      PropertyDispatch.C2 var1 = PropertyDispatch.initial(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);

      for(DripstoneThickness var5 : DripstoneThickness.values()) {
         var1.select(Direction.UP, var5, this.createPointedDripstoneVariant(Direction.UP, var5));
      }

      for(DripstoneThickness var9 : DripstoneThickness.values()) {
         var1.select(Direction.DOWN, var9, this.createPointedDripstoneVariant(Direction.DOWN, var9));
      }

      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.POINTED_DRIPSTONE).with(var1));
   }

   private MultiVariant createPointedDripstoneVariant(Direction var1, DripstoneThickness var2) {
      String var10000 = var1.getSerializedName();
      String var3 = "_" + var10000 + "_" + var2.getSerializedName();
      TextureMapping var4 = TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.POINTED_DRIPSTONE, var3));
      return plainVariant(ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(Blocks.POINTED_DRIPSTONE, var3, var4, this.modelOutput));
   }

   private void createNyliumBlock(Block var1) {
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.NETHERRACK)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(var1)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(var1, "_side"));
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.create(var1, var2, this.modelOutput))));
   }

   private void createDaylightDetector() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_side");
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureSlot.SIDE, var1);
      TextureMapping var3 = (new TextureMapping()).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureSlot.SIDE, var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DAYLIGHT_DETECTOR).with(PropertyDispatch.initial(BlockStateProperties.INVERTED).select(false, plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.create(Blocks.DAYLIGHT_DETECTOR, var2, this.modelOutput))).select(true, plainVariant(ModelTemplates.DAYLIGHT_DETECTOR.create(ModelLocationUtils.getModelLocation(Blocks.DAYLIGHT_DETECTOR, "_inverted"), var3, this.modelOutput)))));
   }

   private void createRotatableColumn(Block var1) {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, plainVariant(ModelLocationUtils.getModelLocation(var1))).with(ROTATIONS_COLUMN_WITH_FACING));
   }

   private void createLightningRod(Block var1, Block var2) {
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.LIGHTNING_ROD, "_on"));
      MultiVariant var4 = plainVariant(ModelTemplates.LIGHTNING_ROD.create(var1, TextureMapping.defaultTexture(var1), this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createBooleanModelDispatch(BlockStateProperties.POWERED, var3, var4)).with(ROTATIONS_COLUMN_WITH_FACING));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2).with(createBooleanModelDispatch(BlockStateProperties.POWERED, var3, var4)).with(ROTATIONS_COLUMN_WITH_FACING));
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
   }

   private void createFarmland() {
      TextureMapping var1 = (new TextureMapping()).put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND));
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"));
      MultiVariant var3 = plainVariant(ModelTemplates.FARMLAND.create(Blocks.FARMLAND, var1, this.modelOutput));
      MultiVariant var4 = plainVariant(ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"), var2, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.FARMLAND).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, var4, var3)));
   }

   private MultiVariant createFloorFireModels(Block var1) {
      return variants(plainModel(ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation(var1, "_floor0"), TextureMapping.fire0(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation(var1, "_floor1"), TextureMapping.fire1(var1), this.modelOutput)));
   }

   private MultiVariant createSideFireModels(Block var1) {
      return variants(plainModel(ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation(var1, "_side0"), TextureMapping.fire0(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation(var1, "_side1"), TextureMapping.fire1(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation(var1, "_side_alt0"), TextureMapping.fire0(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation(var1, "_side_alt1"), TextureMapping.fire1(var1), this.modelOutput)));
   }

   private MultiVariant createTopFireModels(Block var1) {
      return variants(plainModel(ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation(var1, "_up0"), TextureMapping.fire0(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation(var1, "_up1"), TextureMapping.fire1(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation(var1, "_up_alt0"), TextureMapping.fire0(var1), this.modelOutput)), plainModel(ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation(var1, "_up_alt1"), TextureMapping.fire1(var1), this.modelOutput)));
   }

   private void createFire() {
      ConditionBuilder var1 = condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false).term(BlockStateProperties.UP, false);
      MultiVariant var2 = this.createFloorFireModels(Blocks.FIRE);
      MultiVariant var3 = this.createSideFireModels(Blocks.FIRE);
      MultiVariant var4 = this.createTopFireModels(Blocks.FIRE);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.FIRE).with(var1, var2).with(or(condition().term(BlockStateProperties.NORTH, true), var1), var3).with(or(condition().term(BlockStateProperties.EAST, true), var1), var3.with(Y_ROT_90)).with(or(condition().term(BlockStateProperties.SOUTH, true), var1), var3.with(Y_ROT_180)).with(or(condition().term(BlockStateProperties.WEST, true), var1), var3.with(Y_ROT_270)).with(condition().term(BlockStateProperties.UP, true), var4));
   }

   private void createSoulFire() {
      MultiVariant var1 = this.createFloorFireModels(Blocks.SOUL_FIRE);
      MultiVariant var2 = this.createSideFireModels(Blocks.SOUL_FIRE);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(Blocks.SOUL_FIRE).with(var1).with(var2).with(var2.with(Y_ROT_90)).with(var2.with(Y_ROT_180)).with(var2.with(Y_ROT_270)));
   }

   private void createLantern(Block var1) {
      MultiVariant var2 = plainVariant(TexturedModel.LANTERN.create(var1, this.modelOutput));
      MultiVariant var3 = plainVariant(TexturedModel.HANGING_LANTERN.create(var1, this.modelOutput));
      this.registerSimpleFlatItemModel(var1.asItem());
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createBooleanModelDispatch(BlockStateProperties.HANGING, var3, var2)));
   }

   private void createCopperLantern(Block var1, Block var2) {
      ResourceLocation var3 = TexturedModel.LANTERN.create(var1, this.modelOutput);
      ResourceLocation var4 = TexturedModel.HANGING_LANTERN.create(var1, this.modelOutput);
      this.registerSimpleFlatItemModel(var1.asItem());
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createBooleanModelDispatch(BlockStateProperties.HANGING, plainVariant(var4), plainVariant(var3))));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2).with(createBooleanModelDispatch(BlockStateProperties.HANGING, plainVariant(var4), plainVariant(var3))));
   }

   private void createCopperChain(Block var1, Block var2) {
      MultiVariant var3 = plainVariant(TexturedModel.CHAIN.create(var1, this.modelOutput));
      this.createAxisAlignedPillarBlockCustomModel(var1, var3);
      this.createAxisAlignedPillarBlockCustomModel(var2, var3);
   }

   private void createMuddyMangroveRoots() {
      TextureMapping var1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_side"), TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_top"));
      MultiVariant var2 = plainVariant(ModelTemplates.CUBE_COLUMN.create(Blocks.MUDDY_MANGROVE_ROOTS, var1, this.modelOutput));
      this.blockStateOutput.accept(createAxisAlignedPillarBlock(Blocks.MUDDY_MANGROVE_ROOTS, var2));
   }

   private void createMangrovePropagule() {
      this.registerSimpleFlatItemModel(Items.MANGROVE_PROPAGULE);
      Block var1 = Blocks.MANGROVE_PROPAGULE;
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(var1));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.MANGROVE_PROPAGULE).with(PropertyDispatch.initial(MangrovePropaguleBlock.HANGING, MangrovePropaguleBlock.AGE).generate((var2x, var3) -> var2x ? plainVariant(ModelLocationUtils.getModelLocation(var1, "_hanging_" + var3)) : var2)));
   }

   private void createFrostedIce() {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.FROSTED_ICE).with(PropertyDispatch.initial(BlockStateProperties.AGE_3).select(0, plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_0", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(1, plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_1", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(2, plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_2", ModelTemplates.CUBE_ALL, TextureMapping::cube))).select(3, plainVariant(this.createSuffixedVariant(Blocks.FROSTED_ICE, "_3", ModelTemplates.CUBE_ALL, TextureMapping::cube)))));
   }

   private void createGrassBlocks() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.DIRT);
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.BOTTOM, var1).copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
      MultiVariant var3 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.GRASS_BLOCK, "_snow", var2, this.modelOutput));
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(Blocks.GRASS_BLOCK);
      this.createGrassLikeBlock(Blocks.GRASS_BLOCK, createRotatedVariants(plainModel(var4)), var3);
      this.registerSimpleTintedItemModel(Blocks.GRASS_BLOCK, var4, new GrassColorSource());
      MultiVariant var5 = createRotatedVariants(plainModel(TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.MYCELIUM).updateTextures((var1x) -> var1x.put(TextureSlot.BOTTOM, var1)).create(Blocks.MYCELIUM, this.modelOutput)));
      this.createGrassLikeBlock(Blocks.MYCELIUM, var5, var3);
      MultiVariant var6 = createRotatedVariants(plainModel(TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.PODZOL).updateTextures((var1x) -> var1x.put(TextureSlot.BOTTOM, var1)).create(Blocks.PODZOL, this.modelOutput)));
      this.createGrassLikeBlock(Blocks.PODZOL, var6, var3);
   }

   private void createGrassLikeBlock(Block var1, MultiVariant var2, MultiVariant var3) {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.SNOWY).select(true, var3).select(false, var2)));
   }

   private void createCocoa() {
      this.registerSimpleFlatItemModel(Items.COCOA_BEANS);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.COCOA).with(PropertyDispatch.initial(BlockStateProperties.AGE_2).select(0, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage0"))).select(1, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage1"))).select(2, plainVariant(ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage2")))).with(ROTATION_HORIZONTAL_FACING_ALT));
   }

   private void createDirtPath() {
      Variant var1 = plainModel(ModelLocationUtils.getModelLocation(Blocks.DIRT_PATH));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DIRT_PATH, createRotatedVariants(var1)));
   }

   private void createWeightedPressurePlate(Block var1, Block var2) {
      TextureMapping var3 = TextureMapping.defaultTexture(var2);
      MultiVariant var4 = plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create(var1, var3, this.modelOutput));
      MultiVariant var5 = plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create(var1, var3, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createEmptyOrFullDispatch(BlockStateProperties.POWER, 1, var5, var4)));
   }

   private void createHopper() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.HOPPER));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.HOPPER, "_side"));
      this.registerSimpleFlatItemModel(Items.HOPPER);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.HOPPER).with(PropertyDispatch.initial(BlockStateProperties.FACING_HOPPER).select(Direction.DOWN, var1).select(Direction.NORTH, var2).select(Direction.EAST, var2.with(Y_ROT_90)).select(Direction.SOUTH, var2.with(Y_ROT_180)).select(Direction.WEST, var2.with(Y_ROT_270))));
   }

   private void copyModel(Block var1, Block var2) {
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(var1));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2, var3));
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
   }

   private void createBarsAndItem(Block var1) {
      TextureMapping var2 = TextureMapping.bars(var1);
      this.createBars(var1, ModelTemplates.BARS_POST_ENDS.create(var1, var2, this.modelOutput), ModelTemplates.BARS_POST.create(var1, var2, this.modelOutput), ModelTemplates.BARS_CAP.create(var1, var2, this.modelOutput), ModelTemplates.BARS_CAP_ALT.create(var1, var2, this.modelOutput), ModelTemplates.BARS_POST_SIDE.create(var1, var2, this.modelOutput), ModelTemplates.BARS_POST_SIDE_ALT.create(var1, var2, this.modelOutput));
      this.registerSimpleFlatItemModel(var1);
   }

   private void createBarsAndItem(Block var1, Block var2) {
      TextureMapping var3 = TextureMapping.bars(var1);
      ResourceLocation var4 = ModelTemplates.BARS_POST_ENDS.create(var1, var3, this.modelOutput);
      ResourceLocation var5 = ModelTemplates.BARS_POST.create(var1, var3, this.modelOutput);
      ResourceLocation var6 = ModelTemplates.BARS_CAP.create(var1, var3, this.modelOutput);
      ResourceLocation var7 = ModelTemplates.BARS_CAP_ALT.create(var1, var3, this.modelOutput);
      ResourceLocation var8 = ModelTemplates.BARS_POST_SIDE.create(var1, var3, this.modelOutput);
      ResourceLocation var9 = ModelTemplates.BARS_POST_SIDE_ALT.create(var1, var3, this.modelOutput);
      this.createBars(var1, var4, var5, var6, var7, var8, var9);
      this.createBars(var2, var4, var5, var6, var7, var8, var9);
      this.registerSimpleFlatItemModel(var1);
      this.itemModelOutput.copy(var1.asItem(), var2.asItem());
   }

   private void createBars(Block var1, ResourceLocation var2, ResourceLocation var3, ResourceLocation var4, ResourceLocation var5, ResourceLocation var6, ResourceLocation var7) {
      MultiVariant var8 = plainVariant(var2);
      MultiVariant var9 = plainVariant(var3);
      MultiVariant var10 = plainVariant(var4);
      MultiVariant var11 = plainVariant(var5);
      MultiVariant var12 = plainVariant(var6);
      MultiVariant var13 = plainVariant(var7);
      this.blockStateOutput.accept(MultiPartGenerator.multiPart(var1).with(var8).with(condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), var9).with(condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), var10).with(condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), var10.with(Y_ROT_90)).with(condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false), var11).with(condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true), var11.with(Y_ROT_90)).with(condition().term(BlockStateProperties.NORTH, true), var12).with(condition().term(BlockStateProperties.EAST, true), var12.with(Y_ROT_90)).with(condition().term(BlockStateProperties.SOUTH, true), var13).with(condition().term(BlockStateProperties.WEST, true), var13.with(Y_ROT_90)));
   }

   private void createNonTemplateHorizontalBlock(Block var1) {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1, plainVariant(ModelLocationUtils.getModelLocation(var1))).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createLever() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.LEVER));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.LEVER, "_on"));
      this.registerSimpleFlatItemModel(Blocks.LEVER);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.LEVER).with(createBooleanModelDispatch(BlockStateProperties.POWERED, var1, var2)).with(PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.CEILING, Direction.NORTH, X_ROT_180.then(Y_ROT_180)).select(AttachFace.CEILING, Direction.EAST, X_ROT_180.then(Y_ROT_270)).select(AttachFace.CEILING, Direction.SOUTH, X_ROT_180).select(AttachFace.CEILING, Direction.WEST, X_ROT_180.then(Y_ROT_90)).select(AttachFace.FLOOR, Direction.NORTH, NOP).select(AttachFace.FLOOR, Direction.EAST, Y_ROT_90).select(AttachFace.FLOOR, Direction.SOUTH, Y_ROT_180).select(AttachFace.FLOOR, Direction.WEST, Y_ROT_270).select(AttachFace.WALL, Direction.NORTH, X_ROT_90).select(AttachFace.WALL, Direction.EAST, X_ROT_90.then(Y_ROT_90)).select(AttachFace.WALL, Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(AttachFace.WALL, Direction.WEST, X_ROT_90.then(Y_ROT_270))));
   }

   private void createLilyPad() {
      ResourceLocation var1 = this.createFlatItemModelWithBlockTexture(Items.LILY_PAD, Blocks.LILY_PAD);
      this.registerSimpleTintedItemModel(Blocks.LILY_PAD, var1, ItemModelUtils.constantTint(-9321636));
      Variant var2 = plainModel(ModelLocationUtils.getModelLocation(Blocks.LILY_PAD));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.LILY_PAD, createRotatedVariants(var2)));
   }

   private void createFrogspawnBlock() {
      this.registerSimpleFlatItemModel(Blocks.FROGSPAWN);
      this.blockStateOutput.accept(createSimpleBlock(Blocks.FROGSPAWN, plainVariant(ModelLocationUtils.getModelLocation(Blocks.FROGSPAWN))));
   }

   private void createNetherPortalBlock() {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.NETHER_PORTAL).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS).select(Direction.Axis.X, plainVariant(ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ns"))).select(Direction.Axis.Z, plainVariant(ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ew")))));
   }

   private void createNetherrack() {
      Variant var1 = plainModel(TexturedModel.CUBE.create(Blocks.NETHERRACK, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.NETHERRACK, variants(var1, var1.with(X_ROT_90), var1.with(X_ROT_180), var1.with(X_ROT_270), var1.with(Y_ROT_90), var1.with(Y_ROT_90.then(X_ROT_90)), var1.with(Y_ROT_90.then(X_ROT_180)), var1.with(Y_ROT_90.then(X_ROT_270)), var1.with(Y_ROT_180), var1.with(Y_ROT_180.then(X_ROT_90)), var1.with(Y_ROT_180.then(X_ROT_180)), var1.with(Y_ROT_180.then(X_ROT_270)), var1.with(Y_ROT_270), var1.with(Y_ROT_270.then(X_ROT_90)), var1.with(Y_ROT_270.then(X_ROT_180)), var1.with(Y_ROT_270.then(X_ROT_270)))));
   }

   private void createObserver() {
      MultiVariant var1 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.OBSERVER));
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.OBSERVER, "_on"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.OBSERVER).with(createBooleanModelDispatch(BlockStateProperties.POWERED, var2, var1)).with(ROTATION_FACING));
   }

   private void createPistons() {
      TextureMapping var1 = (new TextureMapping()).put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.PISTON, "_bottom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      ResourceLocation var2 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky");
      ResourceLocation var3 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top");
      TextureMapping var4 = var1.copyAndUpdate(TextureSlot.PLATFORM, var2);
      TextureMapping var5 = var1.copyAndUpdate(TextureSlot.PLATFORM, var3);
      MultiVariant var6 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.PISTON, "_base"));
      this.createPistonVariant(Blocks.PISTON, var6, var5);
      this.createPistonVariant(Blocks.STICKY_PISTON, var6, var4);
      ResourceLocation var7 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.PISTON, "_inventory", var1.copyAndUpdate(TextureSlot.TOP, var3), this.modelOutput);
      ResourceLocation var8 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.STICKY_PISTON, "_inventory", var1.copyAndUpdate(TextureSlot.TOP, var2), this.modelOutput);
      this.registerSimpleItemModel(Blocks.PISTON, var7);
      this.registerSimpleItemModel(Blocks.STICKY_PISTON, var8);
   }

   private void createPistonVariant(Block var1, MultiVariant var2, TextureMapping var3) {
      MultiVariant var4 = plainVariant(ModelTemplates.PISTON.create(var1, var3, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(createBooleanModelDispatch(BlockStateProperties.EXTENDED, var2, var4)).with(ROTATION_FACING));
   }

   private void createPistonHeads() {
      TextureMapping var1 = (new TextureMapping()).put(TextureSlot.UNSTICKY, TextureMapping.getBlockTexture(Blocks.PISTON, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
      TextureMapping var2 = var1.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky"));
      TextureMapping var3 = var1.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.PISTON_HEAD).with(PropertyDispatch.initial(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE).select(false, PistonType.DEFAULT, plainVariant(ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head", var3, this.modelOutput))).select(false, PistonType.STICKY, plainVariant(ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head_sticky", var2, this.modelOutput))).select(true, PistonType.DEFAULT, plainVariant(ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short", var3, this.modelOutput))).select(true, PistonType.STICKY, plainVariant(ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short_sticky", var2, this.modelOutput)))).with(ROTATION_FACING));
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
      MultiVariant var9 = plainVariant(var8);
      MultiVariant var10 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_active", var3, this.modelOutput));
      MultiVariant var11 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_ejecting_reward", var4, this.modelOutput));
      MultiVariant var12 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_inactive_ominous", var5, this.modelOutput));
      MultiVariant var13 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_active_ominous", var6, this.modelOutput));
      MultiVariant var14 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP_INNER_FACES.createWithSuffix(var1, "_ejecting_reward_ominous", var7, this.modelOutput));
      this.registerSimpleItemModel(var1, var8);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.TRIAL_SPAWNER_STATE, BlockStateProperties.OMINOUS).generate((var6x, var7x) -> {
         MultiVariant var10000;
         switch (var6x) {
            case INACTIVE:
            case COOLDOWN:
               var10000 = var7x ? var12 : var9;
               break;
            case WAITING_FOR_PLAYERS:
            case ACTIVE:
            case WAITING_FOR_REWARD_EJECTION:
               var10000 = var7x ? var13 : var10;
               break;
            case EJECTING_REWARD:
               var10000 = var7x ? var14 : var11;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      })));
   }

   private void createVault() {
      Block var1 = Blocks.VAULT;
      TextureMapping var2 = TextureMapping.vault(var1, "_front_off", "_side_off", "_top", "_bottom");
      TextureMapping var3 = TextureMapping.vault(var1, "_front_on", "_side_on", "_top", "_bottom");
      TextureMapping var4 = TextureMapping.vault(var1, "_front_ejecting", "_side_on", "_top", "_bottom");
      TextureMapping var5 = TextureMapping.vault(var1, "_front_ejecting", "_side_on", "_top_ejecting", "_bottom");
      ResourceLocation var6 = ModelTemplates.VAULT.create(var1, var2, this.modelOutput);
      MultiVariant var7 = plainVariant(var6);
      MultiVariant var8 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_active", var3, this.modelOutput));
      MultiVariant var9 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_unlocking", var4, this.modelOutput));
      MultiVariant var10 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_ejecting_reward", var5, this.modelOutput));
      TextureMapping var11 = TextureMapping.vault(var1, "_front_off_ominous", "_side_off_ominous", "_top_ominous", "_bottom_ominous");
      TextureMapping var12 = TextureMapping.vault(var1, "_front_on_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
      TextureMapping var13 = TextureMapping.vault(var1, "_front_ejecting_ominous", "_side_on_ominous", "_top_ominous", "_bottom_ominous");
      TextureMapping var14 = TextureMapping.vault(var1, "_front_ejecting_ominous", "_side_on_ominous", "_top_ejecting_ominous", "_bottom_ominous");
      MultiVariant var15 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_ominous", var11, this.modelOutput));
      MultiVariant var16 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_active_ominous", var12, this.modelOutput));
      MultiVariant var17 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_unlocking_ominous", var13, this.modelOutput));
      MultiVariant var18 = plainVariant(ModelTemplates.VAULT.createWithSuffix(var1, "_ejecting_reward_ominous", var14, this.modelOutput));
      this.registerSimpleItemModel(var1, var6);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(VaultBlock.STATE, VaultBlock.OMINOUS).generate((var8x, var9x) -> {
         MultiVariant var10000;
         switch (var8x) {
            case INACTIVE -> var10000 = var9x ? var15 : var7;
            case ACTIVE -> var10000 = var9x ? var16 : var8;
            case UNLOCKING -> var10000 = var9x ? var17 : var9;
            case EJECTING -> var10000 = var9x ? var18 : var10;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      })).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createSculkSensor() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_inactive");
      MultiVariant var2 = plainVariant(var1);
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_active"));
      this.registerSimpleItemModel(Blocks.SCULK_SENSOR, var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCULK_SENSOR).with(PropertyDispatch.initial(BlockStateProperties.SCULK_SENSOR_PHASE).generate((var2x) -> var2x != SculkSensorPhase.ACTIVE && var2x != SculkSensorPhase.COOLDOWN ? var2 : var3)));
   }

   private void createCalibratedSculkSensor() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.CALIBRATED_SCULK_SENSOR, "_inactive");
      MultiVariant var2 = plainVariant(var1);
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.CALIBRATED_SCULK_SENSOR, "_active"));
      this.registerSimpleItemModel(Blocks.CALIBRATED_SCULK_SENSOR, var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CALIBRATED_SCULK_SENSOR).with(PropertyDispatch.initial(BlockStateProperties.SCULK_SENSOR_PHASE).generate((var2x) -> var2x != SculkSensorPhase.ACTIVE && var2x != SculkSensorPhase.COOLDOWN ? var2 : var3)).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createSculkShrieker() {
      ResourceLocation var1 = ModelTemplates.SCULK_SHRIEKER.create(Blocks.SCULK_SHRIEKER, TextureMapping.sculkShrieker(false), this.modelOutput);
      MultiVariant var2 = plainVariant(var1);
      MultiVariant var3 = plainVariant(ModelTemplates.SCULK_SHRIEKER.createWithSuffix(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMapping.sculkShrieker(true), this.modelOutput));
      this.registerSimpleItemModel(Blocks.SCULK_SHRIEKER, var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCULK_SHRIEKER).with(createBooleanModelDispatch(BlockStateProperties.CAN_SUMMON, var3, var2)));
   }

   private void createScaffolding() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_stable");
      MultiVariant var2 = plainVariant(var1);
      MultiVariant var3 = plainVariant(ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_unstable"));
      this.registerSimpleItemModel(Blocks.SCAFFOLDING, var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCAFFOLDING).with(createBooleanModelDispatch(BlockStateProperties.BOTTOM, var3, var2)));
   }

   private void createCaveVines() {
      MultiVariant var1 = plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES, "", ModelTemplates.CROSS, TextureMapping::cross));
      MultiVariant var2 = plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES, "_lit", ModelTemplates.CROSS, TextureMapping::cross));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CAVE_VINES).with(createBooleanModelDispatch(BlockStateProperties.BERRIES, var2, var1)));
      MultiVariant var3 = plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "", ModelTemplates.CROSS, TextureMapping::cross));
      MultiVariant var4 = plainVariant(this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "_lit", ModelTemplates.CROSS, TextureMapping::cross));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.CAVE_VINES_PLANT).with(createBooleanModelDispatch(BlockStateProperties.BERRIES, var4, var3)));
   }

   private void createRedstoneLamp() {
      MultiVariant var1 = plainVariant(TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, this.modelOutput));
      MultiVariant var2 = plainVariant(this.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REDSTONE_LAMP).with(createBooleanModelDispatch(BlockStateProperties.LIT, var2, var1)));
   }

   private void createNormalTorch(Block var1, Block var2) {
      TextureMapping var3 = TextureMapping.torch(var1);
      this.blockStateOutput.accept(createSimpleBlock(var1, plainVariant(ModelTemplates.TORCH.create(var1, var3, this.modelOutput))));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2, plainVariant(ModelTemplates.WALL_TORCH.create(var2, var3, this.modelOutput))).with(ROTATION_TORCH));
      this.registerSimpleFlatItemModel(var1);
   }

   private void createRedstoneTorch() {
      TextureMapping var1 = TextureMapping.torch(Blocks.REDSTONE_TORCH);
      TextureMapping var2 = TextureMapping.torch(TextureMapping.getBlockTexture(Blocks.REDSTONE_TORCH, "_off"));
      MultiVariant var3 = plainVariant(ModelTemplates.REDSTONE_TORCH.create(Blocks.REDSTONE_TORCH, var1, this.modelOutput));
      MultiVariant var4 = plainVariant(ModelTemplates.TORCH_UNLIT.createWithSuffix(Blocks.REDSTONE_TORCH, "_off", var2, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REDSTONE_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, var3, var4)));
      MultiVariant var5 = plainVariant(ModelTemplates.REDSTONE_WALL_TORCH.create(Blocks.REDSTONE_WALL_TORCH, var1, this.modelOutput));
      MultiVariant var6 = plainVariant(ModelTemplates.WALL_TORCH_UNLIT.createWithSuffix(Blocks.REDSTONE_WALL_TORCH, "_off", var2, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REDSTONE_WALL_TORCH).with(createBooleanModelDispatch(BlockStateProperties.LIT, var5, var6)).with(ROTATION_TORCH));
      this.registerSimpleFlatItemModel(Blocks.REDSTONE_TORCH);
   }

   private void createRepeater() {
      this.registerSimpleFlatItemModel(Items.REPEATER);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.REPEATER).with(PropertyDispatch.initial(BlockStateProperties.DELAY, BlockStateProperties.LOCKED, BlockStateProperties.POWERED).generate((var0, var1, var2) -> {
         StringBuilder var3 = new StringBuilder();
         var3.append('_').append(var0).append("tick");
         if (var2) {
            var3.append("_on");
         }

         if (var1) {
            var3.append("_locked");
         }

         return plainVariant(TextureMapping.getBlockTexture(Blocks.REPEATER, var3.toString()));
      })).with(ROTATION_HORIZONTAL_FACING_ALT));
   }

   private void createSeaPickle() {
      this.registerSimpleFlatItemModel(Items.SEA_PICKLE);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SEA_PICKLE).with(PropertyDispatch.initial(BlockStateProperties.PICKLES, BlockStateProperties.WATERLOGGED).select(1, false, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("dead_sea_pickle")))).select(2, false, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("two_dead_sea_pickles")))).select(3, false, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("three_dead_sea_pickles")))).select(4, false, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("four_dead_sea_pickles")))).select(1, true, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("sea_pickle")))).select(2, true, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("two_sea_pickles")))).select(3, true, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("three_sea_pickles")))).select(4, true, createRotatedVariants(plainModel(ModelLocationUtils.decorateBlockModelLocation("four_sea_pickles"))))));
   }

   private void createSnowBlocks() {
      TextureMapping var1 = TextureMapping.cube(Blocks.SNOW);
      MultiVariant var2 = plainVariant(ModelTemplates.CUBE_ALL.create(Blocks.SNOW_BLOCK, var1, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SNOW).with(PropertyDispatch.initial(BlockStateProperties.LAYERS).generate((var1x) -> {
         MultiVariant var2x;
         if (var1x < 8) {
            Block var10000 = Blocks.SNOW;
            int var10001 = var1x;
            var2x = plainVariant(ModelLocationUtils.getModelLocation(var10000, "_height" + var10001 * 2));
         } else {
            var2x = var2;
         }

         return var2x;
      })));
      this.registerSimpleItemModel(Blocks.SNOW, ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height2"));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.SNOW_BLOCK, var2));
   }

   private void createStonecutter() {
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.STONECUTTER, plainVariant(ModelLocationUtils.getModelLocation(Blocks.STONECUTTER))).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createStructureBlock() {
      ResourceLocation var1 = TexturedModel.CUBE.create(Blocks.STRUCTURE_BLOCK, this.modelOutput);
      this.registerSimpleItemModel(Blocks.STRUCTURE_BLOCK, var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.STRUCTURE_BLOCK).with(PropertyDispatch.initial(BlockStateProperties.STRUCTUREBLOCK_MODE).generate((var1x) -> plainVariant(this.createSuffixedVariant(Blocks.STRUCTURE_BLOCK, "_" + var1x.getSerializedName(), ModelTemplates.CUBE_ALL, TextureMapping::cube)))));
   }

   private void createTestBlock() {
      HashMap var1 = new HashMap();

      for(TestBlockMode var5 : TestBlockMode.values()) {
         var1.put(var5, this.createSuffixedVariant(Blocks.TEST_BLOCK, "_" + var5.getSerializedName(), ModelTemplates.CUBE_ALL, TextureMapping::cube));
      }

      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TEST_BLOCK).with(PropertyDispatch.initial(BlockStateProperties.TEST_BLOCK_MODE).generate((var1x) -> plainVariant((ResourceLocation)var1.get(var1x)))));
      this.itemModelOutput.accept(Items.TEST_BLOCK, ItemModelUtils.selectBlockItemProperty(TestBlock.MODE, ItemModelUtils.plainModel((ResourceLocation)var1.get(TestBlockMode.START)), Map.of(TestBlockMode.FAIL, ItemModelUtils.plainModel((ResourceLocation)var1.get(TestBlockMode.FAIL)), TestBlockMode.LOG, ItemModelUtils.plainModel((ResourceLocation)var1.get(TestBlockMode.LOG)), TestBlockMode.ACCEPT, ItemModelUtils.plainModel((ResourceLocation)var1.get(TestBlockMode.ACCEPT)))));
   }

   private void createSweetBerryBush() {
      this.registerSimpleFlatItemModel(Items.SWEET_BERRIES);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SWEET_BERRY_BUSH).with(PropertyDispatch.initial(BlockStateProperties.AGE_3).generate((var1) -> plainVariant(this.createSuffixedVariant(Blocks.SWEET_BERRY_BUSH, "_stage" + var1, ModelTemplates.CROSS, TextureMapping::cross)))));
   }

   private void createTripwire() {
      this.registerSimpleFlatItemModel(Items.STRING);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TRIPWIRE).with(PropertyDispatch.initial(BlockStateProperties.ATTACHED, BlockStateProperties.EAST, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST).select(false, false, false, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(Y_ROT_90)).select(false, false, true, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))).select(false, false, false, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(Y_ROT_180)).select(false, false, false, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(Y_ROT_270)).select(false, true, true, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))).select(false, true, false, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(Y_ROT_90)).select(false, false, false, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(Y_ROT_180)).select(false, false, true, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(Y_ROT_270)).select(false, false, true, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select(false, true, false, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns")).with(Y_ROT_90)).select(false, true, true, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))).select(false, true, false, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(Y_ROT_90)).select(false, false, true, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(Y_ROT_180)).select(false, true, true, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(Y_ROT_270)).select(false, true, true, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nsew"))).select(true, false, false, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, false, true, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))).select(true, false, false, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(Y_ROT_180)).select(true, true, false, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(Y_ROT_90)).select(true, false, false, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(Y_ROT_270)).select(true, true, true, false, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))).select(true, true, false, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(Y_ROT_90)).select(true, false, false, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(Y_ROT_180)).select(true, false, true, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(Y_ROT_270)).select(true, false, true, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select(true, true, false, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns")).with(Y_ROT_90)).select(true, true, true, true, false, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))).select(true, true, false, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(Y_ROT_90)).select(true, false, true, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(Y_ROT_180)).select(true, true, true, false, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(Y_ROT_270)).select(true, true, true, true, true, plainVariant(ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nsew")))));
   }

   private void createTripwireHook() {
      this.registerSimpleFlatItemModel(Blocks.TRIPWIRE_HOOK);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TRIPWIRE_HOOK).with(PropertyDispatch.initial(BlockStateProperties.ATTACHED, BlockStateProperties.POWERED).generate((var0, var1) -> {
         Block var10000 = Blocks.TRIPWIRE_HOOK;
         String var10001 = var0 ? "_attached" : "";
         return plainVariant(ModelLocationUtils.getModelLocation(var10000, var10001 + (var1 ? "_on" : "")));
      })).with(ROTATION_HORIZONTAL_FACING));
   }

   private Variant createTurtleEggModel(int var1, String var2, TextureMapping var3) {
      Variant var10000;
      switch (var1) {
         case 1 -> var10000 = plainModel(ModelTemplates.TURTLE_EGG.create(ModelLocationUtils.decorateBlockModelLocation(var2 + "turtle_egg"), var3, this.modelOutput));
         case 2 -> var10000 = plainModel(ModelTemplates.TWO_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("two_" + var2 + "turtle_eggs"), var3, this.modelOutput));
         case 3 -> var10000 = plainModel(ModelTemplates.THREE_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("three_" + var2 + "turtle_eggs"), var3, this.modelOutput));
         case 4 -> var10000 = plainModel(ModelTemplates.FOUR_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("four_" + var2 + "turtle_eggs"), var3, this.modelOutput));
         default -> throw new UnsupportedOperationException();
      }

      return var10000;
   }

   private Variant createTurtleEggModel(int var1, int var2) {
      Variant var10000;
      switch (var2) {
         case 0 -> var10000 = this.createTurtleEggModel(var1, "", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG)));
         case 1 -> var10000 = this.createTurtleEggModel(var1, "slightly_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_slightly_cracked")));
         case 2 -> var10000 = this.createTurtleEggModel(var1, "very_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_very_cracked")));
         default -> throw new UnsupportedOperationException();
      }

      return var10000;
   }

   private void createTurtleEgg() {
      this.registerSimpleFlatItemModel(Items.TURTLE_EGG);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.TURTLE_EGG).with(PropertyDispatch.initial(BlockStateProperties.EGGS, BlockStateProperties.HATCH).generate((var1, var2) -> createRotatedVariants(this.createTurtleEggModel(var1, var2)))));
   }

   private void createDriedGhastBlock() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.DRIED_GHAST, "_hydration_0");
      this.registerSimpleItemModel(Blocks.DRIED_GHAST, var1);
      Function var2 = (var1x) -> {
         String var10000;
         switch (var1x) {
            case 1 -> var10000 = "_hydration_1";
            case 2 -> var10000 = "_hydration_2";
            case 3 -> var10000 = "_hydration_3";
            default -> var10000 = "_hydration_0";
         }

         String var2 = var10000;
         TextureMapping var3 = TextureMapping.driedGhast(var2);
         return ModelTemplates.DRIED_GHAST.createWithSuffix(Blocks.DRIED_GHAST, var2, var3, this.modelOutput);
      };
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DRIED_GHAST).with(PropertyDispatch.initial(DriedGhastBlock.HYDRATION_LEVEL).generate((var1x) -> plainVariant((ResourceLocation)var2.apply(var1x)))).with(ROTATION_HORIZONTAL_FACING));
   }

   private void createSnifferEgg() {
      this.registerSimpleFlatItemModel(Items.SNIFFER_EGG);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SNIFFER_EGG).with(PropertyDispatch.initial(SnifferEggBlock.HATCH).generate((var1) -> {
         String var10000;
         switch (var1) {
            case 1 -> var10000 = "_slightly_cracked";
            case 2 -> var10000 = "_very_cracked";
            default -> var10000 = "_not_cracked";
         }

         String var2 = var10000;
         TextureMapping var3 = TextureMapping.snifferEgg(var2);
         return plainVariant(ModelTemplates.SNIFFER_EGG.createWithSuffix(Blocks.SNIFFER_EGG, var2, var3, this.modelOutput));
      })));
   }

   private void createMultiface(Block var1) {
      this.registerSimpleFlatItemModel(var1);
      this.createMultifaceBlockStates(var1);
   }

   private void createMultiface(Block var1, Item var2) {
      this.registerSimpleFlatItemModel(var2);
      this.createMultifaceBlockStates(var1);
   }

   private static <T extends Property<?>> Map<T, VariantMutator> selectMultifaceProperties(StateHolder<?, ?> var0, Function<Direction, T> var1) {
      ImmutableMap.Builder var2 = ImmutableMap.builderWithExpectedSize(MULTIFACE_GENERATOR.size());
      MULTIFACE_GENERATOR.forEach((var3, var4) -> {
         Property var5 = (Property)var1.apply(var3);
         if (var0.hasProperty(var5)) {
            var2.put(var5, var4);
         }

      });
      return var2.build();
   }

   private void createMultifaceBlockStates(Block var1) {
      Map var2 = selectMultifaceProperties(var1.defaultBlockState(), MultifaceBlock::getFaceProperty);
      ConditionBuilder var3 = condition();
      var2.forEach((var1x, var2x) -> var3.term(var1x, false));
      MultiVariant var4 = plainVariant(ModelLocationUtils.getModelLocation(var1));
      MultiPartGenerator var5 = MultiPartGenerator.multiPart(var1);
      var2.forEach((var3x, var4x) -> {
         var5.with(condition().term(var3x, true), var4.with(var4x));
         var5.with(var3, var4.with(var4x));
      });
      this.blockStateOutput.accept(var5);
   }

   private void createMossyCarpet(Block var1) {
      Map var2 = selectMultifaceProperties(var1.defaultBlockState(), MossyCarpetBlock::getPropertyForFace);
      ConditionBuilder var3 = condition().term(MossyCarpetBlock.BASE, false);
      var2.forEach((var1x, var2x) -> var3.term(var1x, WallSide.NONE));
      MultiVariant var4 = plainVariant(TexturedModel.CARPET.create(var1, this.modelOutput));
      MultiVariant var5 = plainVariant(TexturedModel.MOSSY_CARPET_SIDE.get(var1).updateTextures((var1x) -> var1x.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(var1, "_side_tall"))).createWithSuffix(var1, "_side_tall", this.modelOutput));
      MultiVariant var6 = plainVariant(TexturedModel.MOSSY_CARPET_SIDE.get(var1).updateTextures((var1x) -> var1x.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(var1, "_side_small"))).createWithSuffix(var1, "_side_small", this.modelOutput));
      MultiPartGenerator var7 = MultiPartGenerator.multiPart(var1);
      var7.with(condition().term(MossyCarpetBlock.BASE, true), var4);
      var7.with(var3, var4);
      var2.forEach((var4x, var5x) -> {
         var7.with(condition().term(var4x, WallSide.TALL), var5.with(var5x));
         var7.with(condition().term(var4x, WallSide.LOW), var6.with(var5x));
         var7.with(var3, var5.with(var5x));
      });
      this.blockStateOutput.accept(var7);
   }

   private void createHangingMoss(Block var1) {
      this.registerSimpleFlatItemModel(var1);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(HangingMossBlock.TIP).generate((var2) -> {
         String var3 = var2 ? "_tip" : "";
         TextureMapping var4 = TextureMapping.cross(TextureMapping.getBlockTexture(var1, var3));
         return plainVariant(BlockModelGenerators.PlantType.NOT_TINTED.getCross().createWithSuffix(var1, var3, var4, this.modelOutput));
      })));
   }

   private void createSculkCatalyst() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_bottom");
      TextureMapping var2 = (new TextureMapping()).put(TextureSlot.BOTTOM, var1).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side"));
      TextureMapping var3 = (new TextureMapping()).put(TextureSlot.BOTTOM, var1).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top_bloom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side_bloom"));
      ResourceLocation var4 = ModelTemplates.CUBE_BOTTOM_TOP.create(Blocks.SCULK_CATALYST, var2, this.modelOutput);
      MultiVariant var5 = plainVariant(var4);
      MultiVariant var6 = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.SCULK_CATALYST, "_bloom", var3, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.SCULK_CATALYST).with(PropertyDispatch.initial(BlockStateProperties.BLOOM).generate((var2x) -> var2x ? var6 : var5)));
      this.registerSimpleItemModel(Blocks.SCULK_CATALYST, var4);
   }

   private void createShelf(Block var1, Block var2) {
      TextureMapping var3 = (new TextureMapping()).put(TextureSlot.ALL, TextureMapping.getBlockTexture(var1)).put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(var2));
      MultiPartGenerator var4 = MultiPartGenerator.multiPart(var1);
      this.addShelfPart(var1, var3, var4, ModelTemplates.SHELF_BODY, (Boolean)null, (SideChainPart)null);
      this.addShelfPart(var1, var3, var4, ModelTemplates.SHELF_UNPOWERED, false, (SideChainPart)null);
      this.addShelfPart(var1, var3, var4, ModelTemplates.SHELF_UNCONNECTED, true, SideChainPart.UNCONNECTED);
      this.addShelfPart(var1, var3, var4, ModelTemplates.SHELF_LEFT, true, SideChainPart.LEFT);
      this.addShelfPart(var1, var3, var4, ModelTemplates.SHELF_CENTER, true, SideChainPart.CENTER);
      this.addShelfPart(var1, var3, var4, ModelTemplates.SHELF_RIGHT, true, SideChainPart.RIGHT);
      this.blockStateOutput.accept(var4);
      this.registerSimpleItemModel(var1, ModelTemplates.SHELF_INVENTORY.create(var1, var3, this.modelOutput));
   }

   private void addShelfPart(Block var1, TextureMapping var2, MultiPartGenerator var3, ModelTemplate var4, @Nullable Boolean var5, @Nullable SideChainPart var6) {
      MultiVariant var7 = plainVariant(var4.create(var1, var2, this.modelOutput));
      forEachHorizontalDirection((var4x, var5x) -> var3.with(shelfCondition(var4x, var5, var6), var7.with(var5x)));
   }

   private static void forEachHorizontalDirection(BiConsumer<Direction, VariantMutator> var0) {
      List.of(Pair.of(Direction.NORTH, NOP), Pair.of(Direction.EAST, Y_ROT_90), Pair.of(Direction.SOUTH, Y_ROT_180), Pair.of(Direction.WEST, Y_ROT_270)).forEach((var1) -> {
         Direction var2 = (Direction)var1.getFirst();
         VariantMutator var3 = (VariantMutator)var1.getSecond();
         var0.accept(var2, var3);
      });
   }

   private static Condition shelfCondition(Direction var0, @Nullable Boolean var1, @Nullable SideChainPart var2) {
      ConditionBuilder var3 = condition(BlockStateProperties.HORIZONTAL_FACING, var0);
      if (var1 == null) {
         return var3.build();
      } else {
         ConditionBuilder var4 = condition(BlockStateProperties.POWERED, var1);
         return var2 != null ? and(var3, var4, condition(BlockStateProperties.SIDE_CHAIN_PART, var2)) : and(var3, var4);
      }
   }

   private void createChiseledBookshelf() {
      Block var1 = Blocks.CHISELED_BOOKSHELF;
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(var1));
      MultiPartGenerator var3 = MultiPartGenerator.multiPart(var1);
      forEachHorizontalDirection((var3x, var4) -> {
         Condition var5 = condition().term(BlockStateProperties.HORIZONTAL_FACING, var3x).build();
         var3.with(var5, var2.with(var4).with(UV_LOCK));
         this.addSlotStateAndRotationVariants(var3, var5, var4);
      });
      this.blockStateOutput.accept(var3);
      this.registerSimpleItemModel(var1, ModelLocationUtils.getModelLocation(var1, "_inventory"));
      CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.clear();
   }

   private void addSlotStateAndRotationVariants(MultiPartGenerator var1, Condition var2, VariantMutator var3) {
      List.of(Pair.of(ChiseledBookShelfBlock.SLOT_0_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_LEFT), Pair.of(ChiseledBookShelfBlock.SLOT_1_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_MID), Pair.of(ChiseledBookShelfBlock.SLOT_2_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_RIGHT), Pair.of(ChiseledBookShelfBlock.SLOT_3_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT), Pair.of(ChiseledBookShelfBlock.SLOT_4_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_MID), Pair.of(ChiseledBookShelfBlock.SLOT_5_OCCUPIED, ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT)).forEach((var4) -> {
         BooleanProperty var5 = (BooleanProperty)var4.getFirst();
         ModelTemplate var6 = (ModelTemplate)var4.getSecond();
         this.addBookSlotModel(var1, var2, var3, var5, var6, true);
         this.addBookSlotModel(var1, var2, var3, var5, var6, false);
      });
   }

   private void addBookSlotModel(MultiPartGenerator var1, Condition var2, VariantMutator var3, BooleanProperty var4, ModelTemplate var5, boolean var6) {
      String var7 = var6 ? "_occupied" : "_empty";
      TextureMapping var8 = (new TextureMapping()).put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(Blocks.CHISELED_BOOKSHELF, var7));
      BookSlotModelCacheKey var9 = new BookSlotModelCacheKey(var5, var7);
      MultiVariant var10 = plainVariant((ResourceLocation)CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.computeIfAbsent(var9, (var4x) -> var5.createWithSuffix(Blocks.CHISELED_BOOKSHELF, var7, var8, this.modelOutput)));
      var1.with((Condition)(new CombinedCondition(CombinedCondition.Operation.AND, List.of(var2, condition().term(var4, var6).build()))), var10.with(var3));
   }

   private void createMagmaBlock() {
      MultiVariant var1 = plainVariant(ModelTemplates.CUBE_ALL.create(Blocks.MAGMA_BLOCK, TextureMapping.cube(ModelLocationUtils.decorateBlockModelLocation("magma")), this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(Blocks.MAGMA_BLOCK, var1));
   }

   private void createShulkerBox(Block var1, @Nullable DyeColor var2) {
      this.createParticleOnlyBlock(var1);
      Item var3 = var1.asItem();
      ResourceLocation var4 = ModelTemplates.SHULKER_BOX_INVENTORY.create(var3, TextureMapping.particle(var1), this.modelOutput);
      ItemModel.Unbaked var5 = var2 != null ? ItemModelUtils.specialModel(var4, new ShulkerBoxSpecialRenderer.Unbaked(var2)) : ItemModelUtils.specialModel(var4, new ShulkerBoxSpecialRenderer.Unbaked());
      this.itemModelOutput.accept(var3, var5);
   }

   private void createGrowingPlant(Block var1, Block var2, PlantType var3) {
      this.createCrossBlock(var1, var3);
      this.createCrossBlock(var2, var3);
   }

   private void createInfestedStone() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.STONE);
      Variant var2 = plainModel(var1);
      Variant var3 = plainModel(ModelLocationUtils.getModelLocation(Blocks.STONE, "_mirrored"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INFESTED_STONE, createRotatedVariants(var2, var3)));
      this.registerSimpleItemModel(Blocks.INFESTED_STONE, var1);
   }

   private void createInfestedDeepslate() {
      ResourceLocation var1 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE);
      Variant var2 = plainModel(var1);
      Variant var3 = plainModel(ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE, "_mirrored"));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.INFESTED_DEEPSLATE, createRotatedVariants(var2, var3)).with(createRotatedPillar()));
      this.registerSimpleItemModel(Blocks.INFESTED_DEEPSLATE, var1);
   }

   private void createNetherRoots(Block var1, Block var2) {
      this.createCrossBlockWithDefaultItem(var1, BlockModelGenerators.PlantType.NOT_TINTED);
      TextureMapping var3 = TextureMapping.plant(TextureMapping.getBlockTexture(var1, "_pot"));
      MultiVariant var4 = plainVariant(BlockModelGenerators.PlantType.NOT_TINTED.getCrossPot().create(var2, var3, this.modelOutput));
      this.blockStateOutput.accept(createSimpleBlock(var2, var4));
   }

   private void createRespawnAnchor() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_bottom");
      ResourceLocation var2 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top_off");
      ResourceLocation var3 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top");
      ResourceLocation[] var4 = new ResourceLocation[5];

      for(int var5 = 0; var5 < 5; ++var5) {
         TextureMapping var6 = (new TextureMapping()).put(TextureSlot.BOTTOM, var1).put(TextureSlot.TOP, var5 == 0 ? var2 : var3).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_side" + var5));
         var4[var5] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.RESPAWN_ANCHOR, "_" + var5, var6, this.modelOutput);
      }

      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.RESPAWN_ANCHOR).with(PropertyDispatch.initial(BlockStateProperties.RESPAWN_ANCHOR_CHARGES).generate((var1x) -> plainVariant(var4[var1x]))));
      this.registerSimpleItemModel(Blocks.RESPAWN_ANCHOR, var4[0]);
   }

   private static VariantMutator applyRotation(FrontAndTop var0) {
      VariantMutator var10000;
      switch (var0) {
         case DOWN_NORTH -> var10000 = X_ROT_90;
         case DOWN_SOUTH -> var10000 = X_ROT_90.then(Y_ROT_180);
         case DOWN_WEST -> var10000 = X_ROT_90.then(Y_ROT_270);
         case DOWN_EAST -> var10000 = X_ROT_90.then(Y_ROT_90);
         case UP_NORTH -> var10000 = X_ROT_270.then(Y_ROT_180);
         case UP_SOUTH -> var10000 = X_ROT_270;
         case UP_WEST -> var10000 = X_ROT_270.then(Y_ROT_90);
         case UP_EAST -> var10000 = X_ROT_270.then(Y_ROT_270);
         case NORTH_UP -> var10000 = NOP;
         case SOUTH_UP -> var10000 = Y_ROT_180;
         case WEST_UP -> var10000 = Y_ROT_270;
         case EAST_UP -> var10000 = Y_ROT_90;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void createJigsaw() {
      ResourceLocation var1 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_top");
      ResourceLocation var2 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_bottom");
      ResourceLocation var3 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_side");
      ResourceLocation var4 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_lock");
      TextureMapping var5 = (new TextureMapping()).put(TextureSlot.DOWN, var3).put(TextureSlot.WEST, var3).put(TextureSlot.EAST, var3).put(TextureSlot.PARTICLE, var1).put(TextureSlot.NORTH, var1).put(TextureSlot.SOUTH, var2).put(TextureSlot.UP, var4);
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.JIGSAW, plainVariant(ModelTemplates.CUBE_DIRECTIONAL.create(Blocks.JIGSAW, var5, this.modelOutput))).with(PropertyDispatch.modify(BlockStateProperties.ORIENTATION).generate(BlockModelGenerators::applyRotation)));
   }

   private void createPetrifiedOakSlab() {
      Block var1 = Blocks.OAK_PLANKS;
      MultiVariant var2 = plainVariant(ModelLocationUtils.getModelLocation(var1));
      TextureMapping var3 = TextureMapping.cube(var1);
      Block var4 = Blocks.PETRIFIED_OAK_SLAB;
      MultiVariant var5 = plainVariant(ModelTemplates.SLAB_BOTTOM.create(var4, var3, this.modelOutput));
      MultiVariant var6 = plainVariant(ModelTemplates.SLAB_TOP.create(var4, var3, this.modelOutput));
      this.blockStateOutput.accept(createSlab(var4, var5, var6, var2));
   }

   private void createHead(Block var1, Block var2, SkullBlock.Type var3, ResourceLocation var4) {
      MultiVariant var5 = plainVariant(ModelLocationUtils.decorateBlockModelLocation("skull"));
      this.blockStateOutput.accept(createSimpleBlock(var1, var5));
      this.blockStateOutput.accept(createSimpleBlock(var2, var5));
      if (var3 == SkullBlock.Types.PLAYER) {
         this.itemModelOutput.accept(var1.asItem(), ItemModelUtils.specialModel(var4, new PlayerHeadSpecialRenderer.Unbaked()));
      } else {
         this.itemModelOutput.accept(var1.asItem(), ItemModelUtils.specialModel(var4, new SkullSpecialRenderer.Unbaked(var3)));
      }

   }

   private void createHeads() {
      ResourceLocation var1 = ModelLocationUtils.decorateItemModelLocation("template_skull");
      this.createHead(Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, SkullBlock.Types.CREEPER, var1);
      this.createHead(Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, SkullBlock.Types.PLAYER, var1);
      this.createHead(Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, SkullBlock.Types.ZOMBIE, var1);
      this.createHead(Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, SkullBlock.Types.SKELETON, var1);
      this.createHead(Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, SkullBlock.Types.WITHER_SKELETON, var1);
      this.createHead(Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD, SkullBlock.Types.PIGLIN, var1);
      this.createHead(Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, SkullBlock.Types.DRAGON, ModelLocationUtils.getModelLocation(Items.DRAGON_HEAD));
   }

   private void createCopperGolemStatues() {
      this.createCopperGolemStatue(Blocks.COPPER_GOLEM_STATUE, Blocks.COPPER_BLOCK, WeatheringCopper.WeatherState.UNAFFECTED);
      this.createCopperGolemStatue(Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.EXPOSED_COPPER, WeatheringCopper.WeatherState.EXPOSED);
      this.createCopperGolemStatue(Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.WEATHERED_COPPER, WeatheringCopper.WeatherState.WEATHERED);
      this.createCopperGolemStatue(Blocks.OXIDIZED_COPPER_GOLEM_STATUE, Blocks.OXIDIZED_COPPER, WeatheringCopper.WeatherState.OXIDIZED);
      this.copyModel(Blocks.COPPER_GOLEM_STATUE, Blocks.WAXED_COPPER_GOLEM_STATUE);
      this.copyModel(Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE);
      this.copyModel(Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE);
      this.copyModel(Blocks.OXIDIZED_COPPER_GOLEM_STATUE, Blocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE);
   }

   private void createCopperGolemStatue(Block var1, Block var2, WeatheringCopper.WeatherState var3) {
      MultiVariant var4 = plainVariant(ModelTemplates.PARTICLE_ONLY.create(var1, TextureMapping.particle(TextureMapping.getBlockTexture(var2)), this.modelOutput));
      ResourceLocation var5 = ModelLocationUtils.decorateItemModelLocation("template_copper_golem_statue");
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
      this.itemModelOutput.accept(var1.asItem(), ItemModelUtils.selectBlockItemProperty(CopperGolemStatueBlock.POSE, ItemModelUtils.specialModel(var5, new CopperGolemStatueSpecialRenderer.Unbaked(var3, CopperGolemStatueBlock.Pose.STANDING)), Map.of(CopperGolemStatueBlock.Pose.SITTING, ItemModelUtils.specialModel(var5, new CopperGolemStatueSpecialRenderer.Unbaked(var3, CopperGolemStatueBlock.Pose.SITTING)), CopperGolemStatueBlock.Pose.STAR, ItemModelUtils.specialModel(var5, new CopperGolemStatueSpecialRenderer.Unbaked(var3, CopperGolemStatueBlock.Pose.STAR)), CopperGolemStatueBlock.Pose.RUNNING, ItemModelUtils.specialModel(var5, new CopperGolemStatueSpecialRenderer.Unbaked(var3, CopperGolemStatueBlock.Pose.RUNNING)))));
   }

   private void createBanner(Block var1, Block var2, DyeColor var3) {
      MultiVariant var4 = plainVariant(ModelLocationUtils.decorateBlockModelLocation("banner"));
      ResourceLocation var5 = ModelLocationUtils.decorateItemModelLocation("template_banner");
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
      this.blockStateOutput.accept(createSimpleBlock(var2, var4));
      Item var6 = var1.asItem();
      this.itemModelOutput.accept(var6, ItemModelUtils.specialModel(var5, new BannerSpecialRenderer.Unbaked(var3)));
   }

   private void createBanners() {
      this.createBanner(Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER, DyeColor.WHITE);
      this.createBanner(Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER, DyeColor.ORANGE);
      this.createBanner(Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER, DyeColor.MAGENTA);
      this.createBanner(Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, DyeColor.LIGHT_BLUE);
      this.createBanner(Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER, DyeColor.YELLOW);
      this.createBanner(Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER, DyeColor.LIME);
      this.createBanner(Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER, DyeColor.PINK);
      this.createBanner(Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER, DyeColor.GRAY);
      this.createBanner(Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, DyeColor.LIGHT_GRAY);
      this.createBanner(Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER, DyeColor.CYAN);
      this.createBanner(Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER, DyeColor.PURPLE);
      this.createBanner(Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER, DyeColor.BLUE);
      this.createBanner(Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER, DyeColor.BROWN);
      this.createBanner(Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER, DyeColor.GREEN);
      this.createBanner(Blocks.RED_BANNER, Blocks.RED_WALL_BANNER, DyeColor.RED);
      this.createBanner(Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER, DyeColor.BLACK);
   }

   private void createChest(Block var1, Block var2, ResourceLocation var3, boolean var4) {
      this.createParticleOnlyBlock(var1, var2);
      Item var5 = var1.asItem();
      ResourceLocation var6 = ModelTemplates.CHEST_INVENTORY.create(var5, TextureMapping.particle(var2), this.modelOutput);
      ItemModel.Unbaked var7 = ItemModelUtils.specialModel(var6, new ChestSpecialRenderer.Unbaked(var3));
      if (var4) {
         ItemModel.Unbaked var8 = ItemModelUtils.specialModel(var6, new ChestSpecialRenderer.Unbaked(ChestSpecialRenderer.GIFT_CHEST_TEXTURE));
         this.itemModelOutput.accept(var5, ItemModelUtils.isXmas(var8, var7));
      } else {
         this.itemModelOutput.accept(var5, var7);
      }

   }

   private void createChests() {
      this.createChest(Blocks.CHEST, Blocks.OAK_PLANKS, ChestSpecialRenderer.NORMAL_CHEST_TEXTURE, true);
      this.createChest(Blocks.TRAPPED_CHEST, Blocks.OAK_PLANKS, ChestSpecialRenderer.TRAPPED_CHEST_TEXTURE, true);
      this.createChest(Blocks.ENDER_CHEST, Blocks.OBSIDIAN, ChestSpecialRenderer.ENDER_CHEST_TEXTURE, false);
   }

   private void createCopperChests() {
      this.createChest(Blocks.COPPER_CHEST, Blocks.COPPER_BLOCK, ChestSpecialRenderer.COPPER_CHEST_TEXTURE, false);
      this.createChest(Blocks.EXPOSED_COPPER_CHEST, Blocks.EXPOSED_COPPER, ChestSpecialRenderer.EXPOSED_COPPER_CHEST_TEXTURE, false);
      this.createChest(Blocks.WEATHERED_COPPER_CHEST, Blocks.WEATHERED_COPPER, ChestSpecialRenderer.WEATHERED_COPPER_CHEST_TEXTURE, false);
      this.createChest(Blocks.OXIDIZED_COPPER_CHEST, Blocks.OXIDIZED_COPPER, ChestSpecialRenderer.OXIDIZED_COPPER_CHEST_TEXTURE, false);
      this.copyModel(Blocks.COPPER_CHEST, Blocks.WAXED_COPPER_CHEST);
      this.copyModel(Blocks.EXPOSED_COPPER_CHEST, Blocks.WAXED_EXPOSED_COPPER_CHEST);
      this.copyModel(Blocks.WEATHERED_COPPER_CHEST, Blocks.WAXED_WEATHERED_COPPER_CHEST);
      this.copyModel(Blocks.OXIDIZED_COPPER_CHEST, Blocks.WAXED_OXIDIZED_COPPER_CHEST);
   }

   private void createBed(Block var1, Block var2, DyeColor var3) {
      MultiVariant var4 = plainVariant(ModelLocationUtils.decorateBlockModelLocation("bed"));
      this.blockStateOutput.accept(createSimpleBlock(var1, var4));
      Item var5 = var1.asItem();
      ResourceLocation var6 = ModelTemplates.BED_INVENTORY.create(ModelLocationUtils.getModelLocation(var5), TextureMapping.particle(var2), this.modelOutput);
      this.itemModelOutput.accept(var5, ItemModelUtils.specialModel(var6, new BedSpecialRenderer.Unbaked(var3)));
   }

   private void createBeds() {
      this.createBed(Blocks.WHITE_BED, Blocks.WHITE_WOOL, DyeColor.WHITE);
      this.createBed(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL, DyeColor.ORANGE);
      this.createBed(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL, DyeColor.MAGENTA);
      this.createBed(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE);
      this.createBed(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL, DyeColor.YELLOW);
      this.createBed(Blocks.LIME_BED, Blocks.LIME_WOOL, DyeColor.LIME);
      this.createBed(Blocks.PINK_BED, Blocks.PINK_WOOL, DyeColor.PINK);
      this.createBed(Blocks.GRAY_BED, Blocks.GRAY_WOOL, DyeColor.GRAY);
      this.createBed(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY);
      this.createBed(Blocks.CYAN_BED, Blocks.CYAN_WOOL, DyeColor.CYAN);
      this.createBed(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL, DyeColor.PURPLE);
      this.createBed(Blocks.BLUE_BED, Blocks.BLUE_WOOL, DyeColor.BLUE);
      this.createBed(Blocks.BROWN_BED, Blocks.BROWN_WOOL, DyeColor.BROWN);
      this.createBed(Blocks.GREEN_BED, Blocks.GREEN_WOOL, DyeColor.GREEN);
      this.createBed(Blocks.RED_BED, Blocks.RED_WOOL, DyeColor.RED);
      this.createBed(Blocks.BLACK_BED, Blocks.BLACK_WOOL, DyeColor.BLACK);
   }

   private void generateSimpleSpecialItemModel(Block var1, SpecialModelRenderer.Unbaked var2) {
      Item var3 = var1.asItem();
      ResourceLocation var4 = ModelLocationUtils.getModelLocation(var3);
      this.itemModelOutput.accept(var3, ItemModelUtils.specialModel(var4, var2));
   }

   public void run() {
      BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach((var1) -> this.family(var1.getBaseBlock()).generateFor(var1));
      this.family(Blocks.CUT_COPPER).generateFor(BlockFamilies.CUT_COPPER).donateModelTo(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER).donateModelTo(Blocks.CHISELED_COPPER, Blocks.WAXED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_CUT_COPPER);
      this.family(Blocks.EXPOSED_CUT_COPPER).generateFor(BlockFamilies.EXPOSED_CUT_COPPER).donateModelTo(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER).donateModelTo(Blocks.EXPOSED_CHISELED_COPPER, Blocks.WAXED_EXPOSED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
      this.family(Blocks.WEATHERED_CUT_COPPER).generateFor(BlockFamilies.WEATHERED_CUT_COPPER).donateModelTo(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER).donateModelTo(Blocks.WEATHERED_CHISELED_COPPER, Blocks.WAXED_WEATHERED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
      this.family(Blocks.OXIDIZED_CUT_COPPER).generateFor(BlockFamilies.OXIDIZED_CUT_COPPER).donateModelTo(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER).donateModelTo(Blocks.OXIDIZED_CHISELED_COPPER, Blocks.WAXED_OXIDIZED_CHISELED_COPPER).generateFor(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
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
      this.registerSimpleFlatItemModel(Items.FLOWER_POT);
      this.createNonTemplateModelBlock(Blocks.HONEY_BLOCK);
      this.createNonTemplateModelBlock(Blocks.WATER);
      this.createNonTemplateModelBlock(Blocks.LAVA);
      this.createNonTemplateModelBlock(Blocks.SLIME_BLOCK);
      this.registerSimpleFlatItemModel(Items.IRON_CHAIN);
      Items.COPPER_CHAIN.waxedMapping().forEach(this::createCopperChainItem);
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
      this.createMossyCarpet(Blocks.PALE_MOSS_CARPET);
      this.createHangingMoss(Blocks.PALE_HANGING_MOSS);
      this.createTrivialCube(Blocks.PALE_MOSS_BLOCK);
      this.createFlowerBed(Blocks.PINK_PETALS);
      this.createFlowerBed(Blocks.WILDFLOWERS);
      this.createLeafLitter(Blocks.LEAF_LITTER);
      this.createCrossBlock(Blocks.FIREFLY_BUSH, BlockModelGenerators.PlantType.EMISSIVE_NOT_TINTED);
      this.registerSimpleFlatItemModel(Items.FIREFLY_BUSH);
      this.createAirLikeBlock(Blocks.BARRIER, Items.BARRIER);
      this.registerSimpleFlatItemModel(Items.BARRIER);
      this.createLightBlock();
      this.createAirLikeBlock(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
      this.registerSimpleFlatItemModel(Items.STRUCTURE_VOID);
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
      this.createTrivialCube(Blocks.RESIN_BLOCK);
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
      this.createCreakingHeart(Blocks.CREAKING_HEART);
      this.createTrivialCube(Blocks.SPONGE);
      this.createTrivialBlock(Blocks.SEAGRASS, TexturedModel.SEAGRASS);
      this.registerSimpleFlatItemModel(Items.SEAGRASS);
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
      this.createLightningRod(Blocks.LIGHTNING_ROD, Blocks.WAXED_LIGHTNING_ROD);
      this.createLightningRod(Blocks.EXPOSED_LIGHTNING_ROD, Blocks.WAXED_EXPOSED_LIGHTNING_ROD);
      this.createLightningRod(Blocks.WEATHERED_LIGHTNING_ROD, Blocks.WAXED_WEATHERED_LIGHTNING_ROD);
      this.createLightningRod(Blocks.OXIDIZED_LIGHTNING_ROD, Blocks.WAXED_OXIDIZED_LIGHTNING_ROD);
      this.createWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
      this.createWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
      this.createShelf(Blocks.ACACIA_SHELF, Blocks.STRIPPED_ACACIA_LOG);
      this.createShelf(Blocks.BAMBOO_SHELF, Blocks.STRIPPED_BAMBOO_BLOCK);
      this.createShelf(Blocks.BIRCH_SHELF, Blocks.STRIPPED_BIRCH_LOG);
      this.createShelf(Blocks.CHERRY_SHELF, Blocks.STRIPPED_CHERRY_LOG);
      this.createShelf(Blocks.CRIMSON_SHELF, Blocks.STRIPPED_CRIMSON_STEM);
      this.createShelf(Blocks.DARK_OAK_SHELF, Blocks.STRIPPED_DARK_OAK_LOG);
      this.createShelf(Blocks.JUNGLE_SHELF, Blocks.STRIPPED_JUNGLE_LOG);
      this.createShelf(Blocks.MANGROVE_SHELF, Blocks.STRIPPED_MANGROVE_LOG);
      this.createShelf(Blocks.OAK_SHELF, Blocks.STRIPPED_OAK_LOG);
      this.createShelf(Blocks.PALE_OAK_SHELF, Blocks.STRIPPED_PALE_OAK_LOG);
      this.createShelf(Blocks.SPRUCE_SHELF, Blocks.STRIPPED_SPRUCE_LOG);
      this.createShelf(Blocks.WARPED_SHELF, Blocks.STRIPPED_WARPED_STEM);
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
      this.createFarmland();
      this.createFire();
      this.createSoulFire();
      this.createFrostedIce();
      this.createGrassBlocks();
      this.createCocoa();
      this.createDirtPath();
      this.createGrindstone();
      this.createHopper();
      this.createBarsAndItem(Blocks.IRON_BARS);
      Blocks.COPPER_BARS.waxedMapping().forEach(this::createBarsAndItem);
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
      this.createTestBlock();
      this.createTrivialCube(Blocks.TEST_INSTANCE_BLOCK);
      this.createTripwire();
      this.createTripwireHook();
      this.createTurtleEgg();
      this.createSnifferEgg();
      this.createDriedGhastBlock();
      this.createVine();
      this.createMultiface(Blocks.GLOW_LICHEN);
      this.createMultiface(Blocks.SCULK_VEIN);
      this.createMultiface(Blocks.RESIN_CLUMP, Items.RESIN_CLUMP);
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
      this.registerSimpleFlatItemModel(Blocks.LADDER);
      this.createNonTemplateHorizontalBlock(Blocks.LECTERN);
      this.createBigDripLeafBlock();
      this.createNonTemplateHorizontalBlock(Blocks.BIG_DRIPLEAF_STEM);
      this.createNormalTorch(Blocks.TORCH, Blocks.WALL_TORCH);
      this.createNormalTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
      this.createNormalTorch(Blocks.COPPER_TORCH, Blocks.COPPER_WALL_TORCH);
      this.createCraftingTableLike(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, TextureMapping::craftingTable);
      this.createCraftingTableLike(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, TextureMapping::fletchingTable);
      this.createNyliumBlock(Blocks.CRIMSON_NYLIUM);
      this.createNyliumBlock(Blocks.WARPED_NYLIUM);
      this.createDispenserBlock(Blocks.DISPENSER);
      this.createDispenserBlock(Blocks.DROPPER);
      this.createCrafterBlock();
      this.createLantern(Blocks.LANTERN);
      this.createLantern(Blocks.SOUL_LANTERN);
      Blocks.COPPER_LANTERN.waxedMapping().forEach(this::createCopperLantern);
      this.createAxisAlignedPillarBlockCustomModel(Blocks.IRON_CHAIN, plainVariant(TexturedModel.CHAIN.create(Blocks.IRON_CHAIN, this.modelOutput)));
      Blocks.COPPER_CHAIN.waxedMapping().forEach(this::createCopperChain);
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
      this.createCrossBlock(Blocks.TORCHFLOWER_CROP, BlockModelGenerators.PlantType.NOT_TINTED, BlockStateProperties.AGE_1, 0, 1);
      this.createPitcherCrop();
      this.createPitcherPlant();
      this.createBanners();
      this.createBeds();
      this.createHeads();
      this.createChests();
      this.createCopperChests();
      this.createShulkerBox(Blocks.SHULKER_BOX, (DyeColor)null);
      this.createShulkerBox(Blocks.WHITE_SHULKER_BOX, DyeColor.WHITE);
      this.createShulkerBox(Blocks.ORANGE_SHULKER_BOX, DyeColor.ORANGE);
      this.createShulkerBox(Blocks.MAGENTA_SHULKER_BOX, DyeColor.MAGENTA);
      this.createShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX, DyeColor.LIGHT_BLUE);
      this.createShulkerBox(Blocks.YELLOW_SHULKER_BOX, DyeColor.YELLOW);
      this.createShulkerBox(Blocks.LIME_SHULKER_BOX, DyeColor.LIME);
      this.createShulkerBox(Blocks.PINK_SHULKER_BOX, DyeColor.PINK);
      this.createShulkerBox(Blocks.GRAY_SHULKER_BOX, DyeColor.GRAY);
      this.createShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX, DyeColor.LIGHT_GRAY);
      this.createShulkerBox(Blocks.CYAN_SHULKER_BOX, DyeColor.CYAN);
      this.createShulkerBox(Blocks.PURPLE_SHULKER_BOX, DyeColor.PURPLE);
      this.createShulkerBox(Blocks.BLUE_SHULKER_BOX, DyeColor.BLUE);
      this.createShulkerBox(Blocks.BROWN_SHULKER_BOX, DyeColor.BROWN);
      this.createShulkerBox(Blocks.GREEN_SHULKER_BOX, DyeColor.GREEN);
      this.createShulkerBox(Blocks.RED_SHULKER_BOX, DyeColor.RED);
      this.createShulkerBox(Blocks.BLACK_SHULKER_BOX, DyeColor.BLACK);
      this.createCopperGolemStatues();
      this.createParticleOnlyBlock(Blocks.CONDUIT);
      this.generateSimpleSpecialItemModel(Blocks.CONDUIT, new ConduitSpecialRenderer.Unbaked());
      this.createParticleOnlyBlock(Blocks.DECORATED_POT, Blocks.TERRACOTTA);
      this.generateSimpleSpecialItemModel(Blocks.DECORATED_POT, new DecoratedPotSpecialRenderer.Unbaked());
      this.createParticleOnlyBlock(Blocks.END_PORTAL, Blocks.OBSIDIAN);
      this.createParticleOnlyBlock(Blocks.END_GATEWAY, Blocks.OBSIDIAN);
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
      this.createColoredBlockWithRandomRotations(TexturedModel.CUBE, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
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
      this.createColoredBlockWithStateRotations(TexturedModel.GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
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
      this.createPlant(Blocks.FERN, Blocks.POTTED_FERN, BlockModelGenerators.PlantType.TINTED);
      this.createItemWithGrassTint(Blocks.FERN);
      this.createPlantWithDefaultItem(Blocks.DANDELION, Blocks.POTTED_DANDELION, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.POPPY, Blocks.POTTED_POPPY, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.OPEN_EYEBLOSSOM, Blocks.POTTED_OPEN_EYEBLOSSOM, BlockModelGenerators.PlantType.EMISSIVE_NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.CLOSED_EYEBLOSSOM, Blocks.POTTED_CLOSED_EYEBLOSSOM, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPlantWithDefaultItem(Blocks.TORCHFLOWER, Blocks.POTTED_TORCHFLOWER, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createPointedDripstone();
      this.createMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
      this.createMushroomBlock(Blocks.MUSHROOM_STEM);
      this.createCrossBlock(Blocks.SHORT_GRASS, BlockModelGenerators.PlantType.TINTED);
      this.createItemWithGrassTint(Blocks.SHORT_GRASS);
      this.createCrossBlockWithDefaultItem(Blocks.SHORT_DRY_GRASS, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createCrossBlockWithDefaultItem(Blocks.TALL_DRY_GRASS, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createCrossBlock(Blocks.BUSH, BlockModelGenerators.PlantType.TINTED);
      this.createItemWithGrassTint(Blocks.BUSH);
      this.createCrossBlock(Blocks.SUGAR_CANE, BlockModelGenerators.PlantType.TINTED);
      this.registerSimpleFlatItemModel(Items.SUGAR_CANE);
      this.createGrowingPlant(Blocks.KELP, Blocks.KELP_PLANT, BlockModelGenerators.PlantType.NOT_TINTED);
      this.registerSimpleFlatItemModel(Items.KELP);
      this.createCrossBlock(Blocks.HANGING_ROOTS, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createGrowingPlant(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createGrowingPlant(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, BlockModelGenerators.PlantType.NOT_TINTED);
      this.registerSimpleFlatItemModel(Blocks.WEEPING_VINES, "_plant");
      this.registerSimpleFlatItemModel(Blocks.TWISTING_VINES, "_plant");
      this.createCrossBlockWithDefaultItem(Blocks.BAMBOO_SAPLING, BlockModelGenerators.PlantType.TINTED, TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.BAMBOO, "_stage0")));
      this.createBamboo();
      this.createCrossBlockWithDefaultItem(Blocks.CACTUS_FLOWER, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createCrossBlockWithDefaultItem(Blocks.COBWEB, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createDoublePlantWithDefaultItem(Blocks.LILAC, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createDoublePlantWithDefaultItem(Blocks.ROSE_BUSH, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createDoublePlantWithDefaultItem(Blocks.PEONY, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedDoublePlant(Blocks.TALL_GRASS);
      this.createTintedDoublePlant(Blocks.LARGE_FERN);
      this.createSunflower();
      this.createTallSeagrass();
      this.createSmallDripleaf();
      this.createCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
      this.createCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
      this.createCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
      this.createCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
      this.createCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
      this.createStems(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
      this.createStems(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
      this.woodProvider(Blocks.MANGROVE_LOG).logWithHorizontal(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
      this.woodProvider(Blocks.STRIPPED_MANGROVE_LOG).logWithHorizontal(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
      this.createHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
      this.createTintedLeaves(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES, -7158200);
      this.woodProvider(Blocks.ACACIA_LOG).logWithHorizontal(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
      this.woodProvider(Blocks.STRIPPED_ACACIA_LOG).logWithHorizontal(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
      this.createHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedLeaves(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES, -12012264);
      this.woodProvider(Blocks.CHERRY_LOG).logUVLocked(Blocks.CHERRY_LOG).wood(Blocks.CHERRY_WOOD);
      this.woodProvider(Blocks.STRIPPED_CHERRY_LOG).logUVLocked(Blocks.STRIPPED_CHERRY_LOG).wood(Blocks.STRIPPED_CHERRY_WOOD);
      this.createHangingSign(Blocks.STRIPPED_CHERRY_LOG, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.CHERRY_SAPLING, Blocks.POTTED_CHERRY_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTrivialBlock(Blocks.CHERRY_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.BIRCH_LOG).logWithHorizontal(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
      this.woodProvider(Blocks.STRIPPED_BIRCH_LOG).logWithHorizontal(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
      this.createHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedLeaves(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES, -8345771);
      this.woodProvider(Blocks.OAK_LOG).logWithHorizontal(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
      this.createHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedLeaves(Blocks.OAK_LEAVES, TexturedModel.LEAVES, -12012264);
      this.woodProvider(Blocks.SPRUCE_LOG).logWithHorizontal(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
      this.woodProvider(Blocks.STRIPPED_SPRUCE_LOG).logWithHorizontal(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
      this.createHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedLeaves(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES, -10380959);
      this.woodProvider(Blocks.DARK_OAK_LOG).logWithHorizontal(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_DARK_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.createHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedLeaves(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES, -12012264);
      this.woodProvider(Blocks.PALE_OAK_LOG).logWithHorizontal(Blocks.PALE_OAK_LOG).wood(Blocks.PALE_OAK_WOOD);
      this.woodProvider(Blocks.STRIPPED_PALE_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_PALE_OAK_LOG).wood(Blocks.STRIPPED_PALE_OAK_WOOD);
      this.createHangingSign(Blocks.STRIPPED_PALE_OAK_LOG, Blocks.PALE_OAK_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.PALE_OAK_SAPLING, Blocks.POTTED_PALE_OAK_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTrivialBlock(Blocks.PALE_OAK_LEAVES, TexturedModel.LEAVES);
      this.woodProvider(Blocks.JUNGLE_LOG).logWithHorizontal(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
      this.woodProvider(Blocks.STRIPPED_JUNGLE_LOG).logWithHorizontal(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
      this.createHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createTintedLeaves(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES, -12012264);
      this.woodProvider(Blocks.CRIMSON_STEM).log(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_CRIMSON_STEM).log(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.createHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createNetherRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
      this.woodProvider(Blocks.WARPED_STEM).log(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
      this.woodProvider(Blocks.STRIPPED_WARPED_STEM).log(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
      this.createHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
      this.createPlantWithDefaultItem(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, BlockModelGenerators.PlantType.NOT_TINTED);
      this.createNetherRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
      this.woodProvider(Blocks.BAMBOO_BLOCK).logUVLocked(Blocks.BAMBOO_BLOCK);
      this.woodProvider(Blocks.STRIPPED_BAMBOO_BLOCK).logUVLocked(Blocks.STRIPPED_BAMBOO_BLOCK);
      this.createHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
      this.createCrossBlock(Blocks.NETHER_SPROUTS, BlockModelGenerators.PlantType.NOT_TINTED);
      this.registerSimpleFlatItemModel(Items.NETHER_SPROUTS);
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
   }

   private void createLightBlock() {
      ItemModel.Unbaked var1 = ItemModelUtils.plainModel(this.createFlatItemModel(Items.LIGHT));
      HashMap var2 = new HashMap(16);
      PropertyDispatch.C1 var3 = PropertyDispatch.initial(BlockStateProperties.LEVEL);

      for(int var4 = 0; var4 <= 15; ++var4) {
         String var5 = String.format(Locale.ROOT, "_%02d", var4);
         ResourceLocation var6 = TextureMapping.getItemTexture(Items.LIGHT, var5);
         var3.select(var4, plainVariant(ModelTemplates.PARTICLE_ONLY.createWithSuffix(Blocks.LIGHT, var5, TextureMapping.particle(var6), this.modelOutput)));
         ItemModel.Unbaked var7 = ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(Items.LIGHT, var5), TextureMapping.layer0(var6), this.modelOutput));
         var2.put(var4, var7);
      }

      this.itemModelOutput.accept(Items.LIGHT, ItemModelUtils.selectBlockItemProperty(LightBlock.LEVEL, var1, var2));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.LIGHT).with(var3));
   }

   private void createCopperChainItem(Item var1, Item var2) {
      ResourceLocation var3 = this.createFlatItemModel(var1);
      this.registerSimpleItemModel(var1, var3);
      this.registerSimpleItemModel(var2, var3);
   }

   private void createCandleAndCandleCake(Block var1, Block var2) {
      this.registerSimpleFlatItemModel(var1.asItem());
      TextureMapping var3 = TextureMapping.cube(TextureMapping.getBlockTexture(var1));
      TextureMapping var4 = TextureMapping.cube(TextureMapping.getBlockTexture(var1, "_lit"));
      MultiVariant var5 = plainVariant(ModelTemplates.CANDLE.createWithSuffix(var1, "_one_candle", var3, this.modelOutput));
      MultiVariant var6 = plainVariant(ModelTemplates.TWO_CANDLES.createWithSuffix(var1, "_two_candles", var3, this.modelOutput));
      MultiVariant var7 = plainVariant(ModelTemplates.THREE_CANDLES.createWithSuffix(var1, "_three_candles", var3, this.modelOutput));
      MultiVariant var8 = plainVariant(ModelTemplates.FOUR_CANDLES.createWithSuffix(var1, "_four_candles", var3, this.modelOutput));
      MultiVariant var9 = plainVariant(ModelTemplates.CANDLE.createWithSuffix(var1, "_one_candle_lit", var4, this.modelOutput));
      MultiVariant var10 = plainVariant(ModelTemplates.TWO_CANDLES.createWithSuffix(var1, "_two_candles_lit", var4, this.modelOutput));
      MultiVariant var11 = plainVariant(ModelTemplates.THREE_CANDLES.createWithSuffix(var1, "_three_candles_lit", var4, this.modelOutput));
      MultiVariant var12 = plainVariant(ModelTemplates.FOUR_CANDLES.createWithSuffix(var1, "_four_candles_lit", var4, this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var1).with(PropertyDispatch.initial(BlockStateProperties.CANDLES, BlockStateProperties.LIT).select(1, false, var5).select(2, false, var6).select(3, false, var7).select(4, false, var8).select(1, true, var9).select(2, true, var10).select(3, true, var11).select(4, true, var12)));
      MultiVariant var13 = plainVariant(ModelTemplates.CANDLE_CAKE.create(var2, TextureMapping.candleCake(var1, false), this.modelOutput));
      MultiVariant var14 = plainVariant(ModelTemplates.CANDLE_CAKE.createWithSuffix(var2, "_lit", TextureMapping.candleCake(var1, true), this.modelOutput));
      this.blockStateOutput.accept(MultiVariantGenerator.dispatch(var2).with(createBooleanModelDispatch(BlockStateProperties.LIT, var14, var13)));
   }

   static {
      NON_ORIENTABLE_TRAPDOOR = List.of(Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR);
      NOP = (var0) -> var0;
      UV_LOCK = VariantMutator.UV_LOCK.withValue(true);
      X_ROT_90 = VariantMutator.X_ROT.withValue(Quadrant.R90);
      X_ROT_180 = VariantMutator.X_ROT.withValue(Quadrant.R180);
      X_ROT_270 = VariantMutator.X_ROT.withValue(Quadrant.R270);
      Y_ROT_90 = VariantMutator.Y_ROT.withValue(Quadrant.R90);
      Y_ROT_180 = VariantMutator.Y_ROT.withValue(Quadrant.R180);
      Y_ROT_270 = VariantMutator.Y_ROT.withValue(Quadrant.R270);
      FLOWER_BED_MODEL_1_SEGMENT_CONDITION = (var0) -> var0;
      FLOWER_BED_MODEL_2_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.FLOWER_AMOUNT, 2, 3, 4);
      FLOWER_BED_MODEL_3_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.FLOWER_AMOUNT, 3, 4);
      FLOWER_BED_MODEL_4_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.FLOWER_AMOUNT, 4);
      LEAF_LITTER_MODEL_1_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.SEGMENT_AMOUNT, 1);
      LEAF_LITTER_MODEL_2_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.SEGMENT_AMOUNT, 2, 3);
      LEAF_LITTER_MODEL_3_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.SEGMENT_AMOUNT, 3);
      LEAF_LITTER_MODEL_4_SEGMENT_CONDITION = (var0) -> var0.term(BlockStateProperties.SEGMENT_AMOUNT, 4);
      FULL_BLOCK_MODEL_CUSTOM_GENERATORS = Map.of(Blocks.STONE, BlockModelGenerators::createMirroredCubeGenerator, Blocks.DEEPSLATE, BlockModelGenerators::createMirroredColumnGenerator, Blocks.MUD_BRICKS, BlockModelGenerators::createNorthWestMirroredCubeGenerator);
      ROTATION_FACING = PropertyDispatch.modify(BlockStateProperties.FACING).select(Direction.DOWN, X_ROT_90).select(Direction.UP, X_ROT_270).select(Direction.NORTH, NOP).select(Direction.SOUTH, Y_ROT_180).select(Direction.WEST, Y_ROT_270).select(Direction.EAST, Y_ROT_90);
      ROTATIONS_COLUMN_WITH_FACING = PropertyDispatch.modify(BlockStateProperties.FACING).select(Direction.DOWN, X_ROT_180).select(Direction.UP, NOP).select(Direction.NORTH, X_ROT_90).select(Direction.SOUTH, X_ROT_90.then(Y_ROT_180)).select(Direction.WEST, X_ROT_90.then(Y_ROT_270)).select(Direction.EAST, X_ROT_90.then(Y_ROT_90));
      ROTATION_TORCH = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, NOP).select(Direction.SOUTH, Y_ROT_90).select(Direction.WEST, Y_ROT_180).select(Direction.NORTH, Y_ROT_270);
      ROTATION_HORIZONTAL_FACING_ALT = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, NOP).select(Direction.WEST, Y_ROT_90).select(Direction.NORTH, Y_ROT_180).select(Direction.EAST, Y_ROT_270);
      ROTATION_HORIZONTAL_FACING = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Y_ROT_90).select(Direction.SOUTH, Y_ROT_180).select(Direction.WEST, Y_ROT_270).select(Direction.NORTH, NOP);
      TEXTURED_MODELS = ImmutableMap.builder().put(Blocks.SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.SANDSTONE)).put(Blocks.RED_SANDSTONE, TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.RED_SANDSTONE)).put(Blocks.SMOOTH_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"))).put(Blocks.SMOOTH_RED_SANDSTONE, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"))).put(Blocks.CUT_SANDSTONE, TexturedModel.COLUMN.get(Blocks.SANDSTONE).updateTextures((var0) -> var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_SANDSTONE)))).put(Blocks.CUT_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.RED_SANDSTONE).updateTextures((var0) -> var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_RED_SANDSTONE)))).put(Blocks.QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.QUARTZ_BLOCK)).put(Blocks.SMOOTH_QUARTZ, TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom"))).put(Blocks.BLACKSTONE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.BLACKSTONE)).put(Blocks.DEEPSLATE, TexturedModel.COLUMN_WITH_WALL.get(Blocks.DEEPSLATE)).put(Blocks.CHISELED_QUARTZ_BLOCK, TexturedModel.COLUMN.get(Blocks.CHISELED_QUARTZ_BLOCK).updateTextures((var0) -> var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK)))).put(Blocks.CHISELED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_SANDSTONE).updateTextures((var0) -> {
         var0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"));
         var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_SANDSTONE));
      })).put(Blocks.CHISELED_RED_SANDSTONE, TexturedModel.COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).updateTextures((var0) -> {
         var0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"));
         var0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_RED_SANDSTONE));
      })).put(Blocks.CHISELED_TUFF_BRICKS, TexturedModel.COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF_BRICKS)).put(Blocks.CHISELED_TUFF, TexturedModel.COLUMN_WITH_WALL.get(Blocks.CHISELED_TUFF)).build();
      SHAPE_CONSUMERS = ImmutableMap.builder().put(BlockFamily.Variant.BUTTON, BlockFamilyProvider::button).put(BlockFamily.Variant.DOOR, BlockFamilyProvider::door).put(BlockFamily.Variant.CHISELED, BlockFamilyProvider::fullBlockVariant).put(BlockFamily.Variant.CRACKED, BlockFamilyProvider::fullBlockVariant).put(BlockFamily.Variant.CUSTOM_FENCE, BlockFamilyProvider::customFence).put(BlockFamily.Variant.FENCE, BlockFamilyProvider::fence).put(BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockFamilyProvider::customFenceGate).put(BlockFamily.Variant.FENCE_GATE, BlockFamilyProvider::fenceGate).put(BlockFamily.Variant.SIGN, BlockFamilyProvider::sign).put(BlockFamily.Variant.SLAB, BlockFamilyProvider::slab).put(BlockFamily.Variant.STAIRS, BlockFamilyProvider::stairs).put(BlockFamily.Variant.PRESSURE_PLATE, BlockFamilyProvider::pressurePlate).put(BlockFamily.Variant.TRAPDOOR, BlockFamilyProvider::trapdoor).put(BlockFamily.Variant.WALL, BlockFamilyProvider::wall).build();
      MULTIFACE_GENERATOR = ImmutableMap.of(Direction.NORTH, NOP, Direction.EAST, Y_ROT_90.then(UV_LOCK), Direction.SOUTH, Y_ROT_180.then(UV_LOCK), Direction.WEST, Y_ROT_270.then(UV_LOCK), Direction.UP, X_ROT_270.then(UV_LOCK), Direction.DOWN, X_ROT_90.then(UV_LOCK));
      CHISELED_BOOKSHELF_SLOT_MODEL_CACHE = new HashMap();
   }

   class BlockFamilyProvider {
      private final TextureMapping mapping;
      private final Map<ModelTemplate, ResourceLocation> models = new HashMap();
      @Nullable
      private BlockFamily family;
      @Nullable
      private Variant fullBlock;
      private final Set<Block> skipGeneratingModelsFor = new HashSet();

      public BlockFamilyProvider(final TextureMapping var2) {
         super();
         this.mapping = var2;
      }

      public BlockFamilyProvider fullBlock(Block var1, ModelTemplate var2) {
         this.fullBlock = BlockModelGenerators.plainModel(var2.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         if (BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS.containsKey(var1)) {
            BlockModelGenerators.this.blockStateOutput.accept(((BlockStateGeneratorSupplier)BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS.get(var1)).create(var1, this.fullBlock, this.mapping, BlockModelGenerators.this.modelOutput));
         } else {
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var1, BlockModelGenerators.variant(this.fullBlock)));
         }

         return this;
      }

      public BlockFamilyProvider donateModelTo(Block var1, Block var2) {
         ResourceLocation var3 = ModelLocationUtils.getModelLocation(var1);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var2, BlockModelGenerators.plainVariant(var3)));
         BlockModelGenerators.this.itemModelOutput.copy(var1.asItem(), var2.asItem());
         this.skipGeneratingModelsFor.add(var2);
         return this;
      }

      public BlockFamilyProvider button(Block var1) {
         MultiVariant var2 = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON_PRESSED.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createButton(var1, var2, var3));
         ResourceLocation var4 = ModelTemplates.BUTTON_INVENTORY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.registerSimpleItemModel(var1, var4);
         return this;
      }

      public BlockFamilyProvider wall(Block var1) {
         MultiVariant var2 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_POST.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_LOW_SIDE.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var4 = BlockModelGenerators.plainVariant(ModelTemplates.WALL_TALL_SIDE.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createWall(var1, var2, var3, var4));
         ResourceLocation var5 = ModelTemplates.WALL_INVENTORY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.registerSimpleItemModel(var1, var5);
         return this;
      }

      public BlockFamilyProvider customFence(Block var1) {
         TextureMapping var2 = TextureMapping.customParticle(var1);
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_POST.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var4 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_NORTH.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var5 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_EAST.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var6 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var7 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_SIDE_WEST.create(var1, var2, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createCustomFence(var1, var3, var4, var5, var6, var7));
         ResourceLocation var8 = ModelTemplates.CUSTOM_FENCE_INVENTORY.create(var1, var2, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.registerSimpleItemModel(var1, var8);
         return this;
      }

      public BlockFamilyProvider fence(Block var1) {
         MultiVariant var2 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_POST.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_SIDE.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFence(var1, var2, var3));
         ResourceLocation var4 = ModelTemplates.FENCE_INVENTORY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.registerSimpleItemModel(var1, var4);
         return this;
      }

      public BlockFamilyProvider customFenceGate(Block var1) {
         TextureMapping var2 = TextureMapping.customParticle(var1);
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_OPEN.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var4 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_CLOSED.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var5 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN.create(var1, var2, BlockModelGenerators.this.modelOutput));
         MultiVariant var6 = BlockModelGenerators.plainVariant(ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED.create(var1, var2, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate(var1, var3, var4, var5, var6, false));
         return this;
      }

      public BlockFamilyProvider fenceGate(Block var1) {
         MultiVariant var2 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_OPEN.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_CLOSED.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var4 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_WALL_OPEN.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var5 = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_WALL_CLOSED.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createFenceGate(var1, var2, var3, var4, var5, true));
         return this;
      }

      public BlockFamilyProvider pressurePlate(Block var1) {
         MultiVariant var2 = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(var1, var2, var3));
         return this;
      }

      public BlockFamilyProvider sign(Block var1) {
         if (this.family == null) {
            throw new IllegalStateException("Family not defined");
         } else {
            Block var2 = (Block)this.family.getVariants().get(BlockFamily.Variant.WALL_SIGN);
            MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.PARTICLE_ONLY.create(var1, this.mapping, BlockModelGenerators.this.modelOutput));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var1, var3));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var2, var3));
            BlockModelGenerators.this.registerSimpleFlatItemModel(var1.asItem());
            return this;
         }
      }

      public BlockFamilyProvider slab(Block var1) {
         if (this.fullBlock == null) {
            throw new IllegalStateException("Full block not generated yet");
         } else {
            ResourceLocation var2 = this.getOrCreateModel(ModelTemplates.SLAB_BOTTOM, var1);
            MultiVariant var3 = BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.SLAB_TOP, var1));
            BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSlab(var1, BlockModelGenerators.plainVariant(var2), var3, BlockModelGenerators.variant(this.fullBlock)));
            BlockModelGenerators.this.registerSimpleItemModel(var1, var2);
            return this;
         }
      }

      public BlockFamilyProvider stairs(Block var1) {
         MultiVariant var2 = BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.STAIRS_INNER, var1));
         ResourceLocation var3 = this.getOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, var1);
         MultiVariant var4 = BlockModelGenerators.plainVariant(this.getOrCreateModel(ModelTemplates.STAIRS_OUTER, var1));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createStairs(var1, var2, BlockModelGenerators.plainVariant(var3), var4));
         BlockModelGenerators.this.registerSimpleItemModel(var1, var3);
         return this;
      }

      private BlockFamilyProvider fullBlockVariant(Block var1) {
         TexturedModel var2 = (TexturedModel)BlockModelGenerators.TEXTURED_MODELS.getOrDefault(var1, TexturedModel.CUBE.get(var1));
         MultiVariant var3 = BlockModelGenerators.plainVariant(var2.create(var1, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(var1, var3));
         return this;
      }

      private BlockFamilyProvider door(Block var1) {
         BlockModelGenerators.this.createDoor(var1);
         return this;
      }

      private void trapdoor(Block var1) {
         if (BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR.contains(var1)) {
            BlockModelGenerators.this.createTrapdoor(var1);
         } else {
            BlockModelGenerators.this.createOrientableTrapdoor(var1);
         }

      }

      private ResourceLocation getOrCreateModel(ModelTemplate var1, Block var2) {
         return (ResourceLocation)this.models.computeIfAbsent(var1, (var2x) -> var2x.create(var2, this.mapping, BlockModelGenerators.this.modelOutput));
      }

      public BlockFamilyProvider generateFor(BlockFamily var1) {
         this.family = var1;
         var1.getVariants().forEach((var1x, var2) -> {
            if (!this.skipGeneratingModelsFor.contains(var2)) {
               BiConsumer var3 = (BiConsumer)BlockModelGenerators.SHAPE_CONSUMERS.get(var1x);
               if (var3 != null) {
                  var3.accept(this, var2);
               }

            }
         });
         return this;
      }
   }

   class WoodProvider {
      private final TextureMapping logMapping;

      public WoodProvider(final TextureMapping var2) {
         super();
         this.logMapping = var2;
      }

      public WoodProvider wood(Block var1) {
         TextureMapping var2 = this.logMapping.copyAndUpdate(TextureSlot.END, this.logMapping.get(TextureSlot.SIDE));
         ResourceLocation var3 = ModelTemplates.CUBE_COLUMN.create(var1, var2, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(var1, BlockModelGenerators.plainVariant(var3)));
         BlockModelGenerators.this.registerSimpleItemModel(var1, var3);
         return this;
      }

      public WoodProvider log(Block var1) {
         ResourceLocation var2 = ModelTemplates.CUBE_COLUMN.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput);
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createAxisAlignedPillarBlock(var1, BlockModelGenerators.plainVariant(var2)));
         BlockModelGenerators.this.registerSimpleItemModel(var1, var2);
         return this;
      }

      public WoodProvider logWithHorizontal(Block var1) {
         ResourceLocation var2 = ModelTemplates.CUBE_COLUMN.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput);
         MultiVariant var3 = BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(var1, BlockModelGenerators.plainVariant(var2), var3));
         BlockModelGenerators.this.registerSimpleItemModel(var1, var2);
         return this;
      }

      public WoodProvider logUVLocked(Block var1) {
         BlockModelGenerators.this.blockStateOutput.accept(BlockModelGenerators.createPillarBlockUVLocked(var1, this.logMapping, BlockModelGenerators.this.modelOutput));
         BlockModelGenerators.this.registerSimpleItemModel(var1, ModelTemplates.CUBE_COLUMN.create(var1, this.logMapping, BlockModelGenerators.this.modelOutput));
         return this;
      }
   }

   static enum PlantType {
      TINTED(ModelTemplates.TINTED_CROSS, ModelTemplates.TINTED_FLOWER_POT_CROSS, false),
      NOT_TINTED(ModelTemplates.CROSS, ModelTemplates.FLOWER_POT_CROSS, false),
      EMISSIVE_NOT_TINTED(ModelTemplates.CROSS_EMISSIVE, ModelTemplates.FLOWER_POT_CROSS_EMISSIVE, true);

      private final ModelTemplate blockTemplate;
      private final ModelTemplate flowerPotTemplate;
      private final boolean isEmissive;

      private PlantType(final ModelTemplate var3, final ModelTemplate var4, final boolean var5) {
         this.blockTemplate = var3;
         this.flowerPotTemplate = var4;
         this.isEmissive = var5;
      }

      public ModelTemplate getCross() {
         return this.blockTemplate;
      }

      public ModelTemplate getCrossPot() {
         return this.flowerPotTemplate;
      }

      public ResourceLocation createItemModel(BlockModelGenerators var1, Block var2) {
         Item var3 = var2.asItem();
         return this.isEmissive ? var1.createFlatItemModelWithBlockTextureAndOverlay(var3, var2, "_emissive") : var1.createFlatItemModelWithBlockTexture(var3, var2);
      }

      public TextureMapping getTextureMapping(Block var1) {
         return this.isEmissive ? TextureMapping.crossEmissive(var1) : TextureMapping.cross(var1);
      }

      public TextureMapping getPlantTextureMapping(Block var1) {
         return this.isEmissive ? TextureMapping.plantEmissive(var1) : TextureMapping.plant(var1);
      }

      // $FF: synthetic method
      private static PlantType[] $values() {
         return new PlantType[]{TINTED, NOT_TINTED, EMISSIVE_NOT_TINTED};
      }
   }

   static record BookSlotModelCacheKey(ModelTemplate template, String modelSuffix) {
      BookSlotModelCacheKey(ModelTemplate var1, String var2) {
         super();
         this.template = var1;
         this.modelSuffix = var2;
      }
   }

   @FunctionalInterface
   interface BlockStateGeneratorSupplier {
      BlockModelDefinitionGenerator create(Block var1, Variant var2, TextureMapping var3, BiConsumer<ResourceLocation, ModelInstance> var4);
   }
}
