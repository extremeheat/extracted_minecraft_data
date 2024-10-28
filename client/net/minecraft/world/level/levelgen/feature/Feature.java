package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MultifaceGrowthConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TwistingVinesConfig;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

public abstract class Feature<FC extends FeatureConfiguration> {
   public static final Feature<NoneFeatureConfiguration> NO_OP;
   public static final Feature<TreeConfiguration> TREE;
   public static final Feature<RandomPatchConfiguration> FLOWER;
   public static final Feature<RandomPatchConfiguration> NO_BONEMEAL_FLOWER;
   public static final Feature<RandomPatchConfiguration> RANDOM_PATCH;
   public static final Feature<BlockPileConfiguration> BLOCK_PILE;
   public static final Feature<SpringConfiguration> SPRING;
   public static final Feature<NoneFeatureConfiguration> CHORUS_PLANT;
   public static final Feature<ReplaceBlockConfiguration> REPLACE_SINGLE_BLOCK;
   public static final Feature<NoneFeatureConfiguration> VOID_START_PLATFORM;
   public static final Feature<NoneFeatureConfiguration> DESERT_WELL;
   public static final Feature<FossilFeatureConfiguration> FOSSIL;
   public static final Feature<HugeMushroomFeatureConfiguration> HUGE_RED_MUSHROOM;
   public static final Feature<HugeMushroomFeatureConfiguration> HUGE_BROWN_MUSHROOM;
   public static final Feature<NoneFeatureConfiguration> ICE_SPIKE;
   public static final Feature<NoneFeatureConfiguration> GLOWSTONE_BLOB;
   public static final Feature<NoneFeatureConfiguration> FREEZE_TOP_LAYER;
   public static final Feature<NoneFeatureConfiguration> VINES;
   public static final Feature<BlockColumnConfiguration> BLOCK_COLUMN;
   public static final Feature<VegetationPatchConfiguration> VEGETATION_PATCH;
   public static final Feature<VegetationPatchConfiguration> WATERLOGGED_VEGETATION_PATCH;
   public static final Feature<RootSystemConfiguration> ROOT_SYSTEM;
   public static final Feature<MultifaceGrowthConfiguration> MULTIFACE_GROWTH;
   public static final Feature<UnderwaterMagmaConfiguration> UNDERWATER_MAGMA;
   public static final Feature<NoneFeatureConfiguration> MONSTER_ROOM;
   public static final Feature<NoneFeatureConfiguration> BLUE_ICE;
   public static final Feature<BlockStateConfiguration> ICEBERG;
   public static final Feature<BlockStateConfiguration> FOREST_ROCK;
   public static final Feature<DiskConfiguration> DISK;
   public static final Feature<LakeFeature.Configuration> LAKE;
   public static final Feature<OreConfiguration> ORE;
   public static final Feature<NoneFeatureConfiguration> END_PLATFORM;
   public static final Feature<SpikeConfiguration> END_SPIKE;
   public static final Feature<NoneFeatureConfiguration> END_ISLAND;
   public static final Feature<EndGatewayConfiguration> END_GATEWAY;
   public static final SeagrassFeature SEAGRASS;
   public static final Feature<NoneFeatureConfiguration> KELP;
   public static final Feature<NoneFeatureConfiguration> CORAL_TREE;
   public static final Feature<NoneFeatureConfiguration> CORAL_MUSHROOM;
   public static final Feature<NoneFeatureConfiguration> CORAL_CLAW;
   public static final Feature<CountConfiguration> SEA_PICKLE;
   public static final Feature<SimpleBlockConfiguration> SIMPLE_BLOCK;
   public static final Feature<ProbabilityFeatureConfiguration> BAMBOO;
   public static final Feature<HugeFungusConfiguration> HUGE_FUNGUS;
   public static final Feature<NetherForestVegetationConfig> NETHER_FOREST_VEGETATION;
   public static final Feature<NoneFeatureConfiguration> WEEPING_VINES;
   public static final Feature<TwistingVinesConfig> TWISTING_VINES;
   public static final Feature<ColumnFeatureConfiguration> BASALT_COLUMNS;
   public static final Feature<DeltaFeatureConfiguration> DELTA_FEATURE;
   public static final Feature<ReplaceSphereConfiguration> REPLACE_BLOBS;
   public static final Feature<LayerConfiguration> FILL_LAYER;
   public static final BonusChestFeature BONUS_CHEST;
   public static final Feature<NoneFeatureConfiguration> BASALT_PILLAR;
   public static final Feature<OreConfiguration> SCATTERED_ORE;
   public static final Feature<RandomFeatureConfiguration> RANDOM_SELECTOR;
   public static final Feature<SimpleRandomFeatureConfiguration> SIMPLE_RANDOM_SELECTOR;
   public static final Feature<RandomBooleanFeatureConfiguration> RANDOM_BOOLEAN_SELECTOR;
   public static final Feature<GeodeConfiguration> GEODE;
   public static final Feature<DripstoneClusterConfiguration> DRIPSTONE_CLUSTER;
   public static final Feature<LargeDripstoneConfiguration> LARGE_DRIPSTONE;
   public static final Feature<PointedDripstoneConfiguration> POINTED_DRIPSTONE;
   public static final Feature<SculkPatchConfiguration> SCULK_PATCH;
   private final MapCodec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec;

   private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String var0, F var1) {
      return (Feature)Registry.register(BuiltInRegistries.FEATURE, (String)var0, var1);
   }

   public Feature(Codec<FC> var1) {
      super();
      this.configuredCodec = var1.fieldOf("config").xmap((var1x) -> {
         return new ConfiguredFeature(this, var1x);
      }, ConfiguredFeature::config);
   }

   public MapCodec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec() {
      return this.configuredCodec;
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      var1.setBlock(var2, var3, 3);
   }

   public static Predicate<BlockState> isReplaceable(TagKey<Block> var0) {
      return (var1) -> {
         return !var1.is(var0);
      };
   }

   protected void safeSetBlock(WorldGenLevel var1, BlockPos var2, BlockState var3, Predicate<BlockState> var4) {
      if (var4.test(var1.getBlockState(var2))) {
         var1.setBlock(var2, var3, 2);
      }

   }

   public abstract boolean place(FeaturePlaceContext<FC> var1);

   public boolean place(FC var1, WorldGenLevel var2, ChunkGenerator var3, RandomSource var4, BlockPos var5) {
      return var2.ensureCanWrite(var5) ? this.place(new FeaturePlaceContext(Optional.empty(), var2, var3, var4, var5, var1)) : false;
   }

   protected static boolean isStone(BlockState var0) {
      return var0.is(BlockTags.BASE_STONE_OVERWORLD);
   }

   public static boolean isDirt(BlockState var0) {
      return var0.is(BlockTags.DIRT);
   }

   public static boolean isGrassOrDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, Feature::isDirt);
   }

   public static boolean checkNeighbors(Function<BlockPos, BlockState> var0, BlockPos var1, Predicate<BlockState> var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         var3.setWithOffset(var1, (Direction)var7);
         if (var2.test((BlockState)var0.apply(var3))) {
            return true;
         }
      }

      return false;
   }

   public static boolean isAdjacentToAir(Function<BlockPos, BlockState> var0, BlockPos var1) {
      return checkNeighbors(var0, var1, BlockBehaviour.BlockStateBase::isAir);
   }

   protected void markAboveForPostProcessing(WorldGenLevel var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      for(int var4 = 0; var4 < 2; ++var4) {
         var3.move(Direction.UP);
         if (var1.getBlockState(var3).isAir()) {
            return;
         }

         var1.getChunk(var3).markPosForPostprocessing(var3);
      }

   }

   static {
      NO_OP = register("no_op", new NoOpFeature(NoneFeatureConfiguration.CODEC));
      TREE = register("tree", new TreeFeature(TreeConfiguration.CODEC));
      FLOWER = register("flower", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
      NO_BONEMEAL_FLOWER = register("no_bonemeal_flower", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
      RANDOM_PATCH = register("random_patch", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
      BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockPileConfiguration.CODEC));
      SPRING = register("spring_feature", new SpringFeature(SpringConfiguration.CODEC));
      CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration.CODEC));
      REPLACE_SINGLE_BLOCK = register("replace_single_block", new ReplaceBlockFeature(ReplaceBlockConfiguration.CODEC));
      VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration.CODEC));
      DESERT_WELL = register("desert_well", new DesertWellFeature(NoneFeatureConfiguration.CODEC));
      FOSSIL = register("fossil", new FossilFeature(FossilFeatureConfiguration.CODEC));
      HUGE_RED_MUSHROOM = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
      HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
      ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration.CODEC));
      GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration.CODEC));
      FREEZE_TOP_LAYER = register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration.CODEC));
      VINES = register("vines", new VinesFeature(NoneFeatureConfiguration.CODEC));
      BLOCK_COLUMN = register("block_column", new BlockColumnFeature(BlockColumnConfiguration.CODEC));
      VEGETATION_PATCH = register("vegetation_patch", new VegetationPatchFeature(VegetationPatchConfiguration.CODEC));
      WATERLOGGED_VEGETATION_PATCH = register("waterlogged_vegetation_patch", new WaterloggedVegetationPatchFeature(VegetationPatchConfiguration.CODEC));
      ROOT_SYSTEM = register("root_system", new RootSystemFeature(RootSystemConfiguration.CODEC));
      MULTIFACE_GROWTH = register("multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthConfiguration.CODEC));
      UNDERWATER_MAGMA = register("underwater_magma", new UnderwaterMagmaFeature(UnderwaterMagmaConfiguration.CODEC));
      MONSTER_ROOM = register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration.CODEC));
      BLUE_ICE = register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration.CODEC));
      ICEBERG = register("iceberg", new IcebergFeature(BlockStateConfiguration.CODEC));
      FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockStateConfiguration.CODEC));
      DISK = register("disk", new DiskFeature(DiskConfiguration.CODEC));
      LAKE = register("lake", new LakeFeature(LakeFeature.Configuration.CODEC));
      ORE = register("ore", new OreFeature(OreConfiguration.CODEC));
      END_PLATFORM = register("end_platform", new EndPlatformFeature(NoneFeatureConfiguration.CODEC));
      END_SPIKE = register("end_spike", new SpikeFeature(SpikeConfiguration.CODEC));
      END_ISLAND = register("end_island", new EndIslandFeature(NoneFeatureConfiguration.CODEC));
      END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration.CODEC));
      SEAGRASS = (SeagrassFeature)register("seagrass", new SeagrassFeature(ProbabilityFeatureConfiguration.CODEC));
      KELP = register("kelp", new KelpFeature(NoneFeatureConfiguration.CODEC));
      CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration.CODEC));
      CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration.CODEC));
      CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration.CODEC));
      SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountConfiguration.CODEC));
      SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration.CODEC));
      BAMBOO = register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration.CODEC));
      HUGE_FUNGUS = register("huge_fungus", new HugeFungusFeature(HugeFungusConfiguration.CODEC));
      NETHER_FOREST_VEGETATION = register("nether_forest_vegetation", new NetherForestVegetationFeature(NetherForestVegetationConfig.CODEC));
      WEEPING_VINES = register("weeping_vines", new WeepingVinesFeature(NoneFeatureConfiguration.CODEC));
      TWISTING_VINES = register("twisting_vines", new TwistingVinesFeature(TwistingVinesConfig.CODEC));
      BASALT_COLUMNS = register("basalt_columns", new BasaltColumnsFeature(ColumnFeatureConfiguration.CODEC));
      DELTA_FEATURE = register("delta_feature", new DeltaFeature(DeltaFeatureConfiguration.CODEC));
      REPLACE_BLOBS = register("netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceSphereConfiguration.CODEC));
      FILL_LAYER = register("fill_layer", new FillLayerFeature(LayerConfiguration.CODEC));
      BONUS_CHEST = (BonusChestFeature)register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration.CODEC));
      BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(NoneFeatureConfiguration.CODEC));
      SCATTERED_ORE = register("scattered_ore", new ScatteredOreFeature(OreConfiguration.CODEC));
      RANDOM_SELECTOR = register("random_selector", new RandomSelectorFeature(RandomFeatureConfiguration.CODEC));
      SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfiguration.CODEC));
      RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfiguration.CODEC));
      GEODE = register("geode", new GeodeFeature(GeodeConfiguration.CODEC));
      DRIPSTONE_CLUSTER = register("dripstone_cluster", new DripstoneClusterFeature(DripstoneClusterConfiguration.CODEC));
      LARGE_DRIPSTONE = register("large_dripstone", new LargeDripstoneFeature(LargeDripstoneConfiguration.CODEC));
      POINTED_DRIPSTONE = register("pointed_dripstone", new PointedDripstoneFeature(PointedDripstoneConfiguration.CODEC));
      SCULK_PATCH = register("sculk_patch", new SculkPatchFeature(SculkPatchConfiguration.CODEC));
   }
}
