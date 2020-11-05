package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ColumnFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DeltaFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

public abstract class Feature<FC extends FeatureConfiguration> {
   public static final Feature<NoneFeatureConfiguration> NO_OP;
   public static final Feature<TreeConfiguration> TREE;
   public static final AbstractFlowerFeature<RandomPatchConfiguration> FLOWER;
   public static final AbstractFlowerFeature<RandomPatchConfiguration> NO_BONEMEAL_FLOWER;
   public static final Feature<RandomPatchConfiguration> RANDOM_PATCH;
   public static final Feature<BlockPileConfiguration> BLOCK_PILE;
   public static final Feature<SpringConfiguration> SPRING;
   public static final Feature<NoneFeatureConfiguration> CHORUS_PLANT;
   public static final Feature<ReplaceBlockConfiguration> EMERALD_ORE;
   public static final Feature<NoneFeatureConfiguration> VOID_START_PLATFORM;
   public static final Feature<NoneFeatureConfiguration> DESERT_WELL;
   public static final Feature<NoneFeatureConfiguration> FOSSIL;
   public static final Feature<HugeMushroomFeatureConfiguration> HUGE_RED_MUSHROOM;
   public static final Feature<HugeMushroomFeatureConfiguration> HUGE_BROWN_MUSHROOM;
   public static final Feature<NoneFeatureConfiguration> ICE_SPIKE;
   public static final Feature<NoneFeatureConfiguration> GLOWSTONE_BLOB;
   public static final Feature<NoneFeatureConfiguration> FREEZE_TOP_LAYER;
   public static final Feature<NoneFeatureConfiguration> VINES;
   public static final Feature<NoneFeatureConfiguration> MONSTER_ROOM;
   public static final Feature<NoneFeatureConfiguration> BLUE_ICE;
   public static final Feature<BlockStateConfiguration> ICEBERG;
   public static final Feature<BlockStateConfiguration> FOREST_ROCK;
   public static final Feature<DiskConfiguration> DISK;
   public static final Feature<DiskConfiguration> ICE_PATCH;
   public static final Feature<BlockStateConfiguration> LAKE;
   public static final Feature<OreConfiguration> ORE;
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
   public static final Feature<BlockPileConfiguration> NETHER_FOREST_VEGETATION;
   public static final Feature<NoneFeatureConfiguration> WEEPING_VINES;
   public static final Feature<NoneFeatureConfiguration> TWISTING_VINES;
   public static final Feature<ColumnFeatureConfiguration> BASALT_COLUMNS;
   public static final Feature<DeltaFeatureConfiguration> DELTA_FEATURE;
   public static final Feature<ReplaceSphereConfiguration> REPLACE_BLOBS;
   public static final Feature<LayerConfiguration> FILL_LAYER;
   public static final BonusChestFeature BONUS_CHEST;
   public static final Feature<NoneFeatureConfiguration> BASALT_PILLAR;
   public static final Feature<OreConfiguration> NO_SURFACE_ORE;
   public static final Feature<RandomFeatureConfiguration> RANDOM_SELECTOR;
   public static final Feature<SimpleRandomFeatureConfiguration> SIMPLE_RANDOM_SELECTOR;
   public static final Feature<RandomBooleanFeatureConfiguration> RANDOM_BOOLEAN_SELECTOR;
   public static final Feature<DecoratedFeatureConfiguration> DECORATED;
   public static final Feature<GeodeConfiguration> GEODE;
   private final Codec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec;

   private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String var0, F var1) {
      return (Feature)Registry.register(Registry.FEATURE, (String)var0, var1);
   }

   public Feature(Codec<FC> var1) {
      super();
      this.configuredCodec = var1.fieldOf("config").xmap((var1x) -> {
         return new ConfiguredFeature(this, var1x);
      }, (var0) -> {
         return var0.config;
      }).codec();
   }

   public Codec<ConfiguredFeature<FC, Feature<FC>>> configuredCodec() {
      return this.configuredCodec;
   }

   public ConfiguredFeature<FC, ?> configured(FC var1) {
      return new ConfiguredFeature(this, var1);
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3) {
      var1.setBlock(var2, var3, 3);
   }

   public abstract boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, FC var5);

   protected static boolean isStone(BlockState var0) {
      return var0.is(Blocks.STONE) || var0.is(Blocks.GRANITE) || var0.is(Blocks.DIORITE) || var0.is(Blocks.ANDESITE);
   }

   public static boolean isDirt(BlockState var0) {
      return var0.is(Blocks.DIRT) || var0.is(Blocks.GRASS_BLOCK) || var0.is(Blocks.PODZOL) || var0.is(Blocks.COARSE_DIRT) || var0.is(Blocks.MYCELIUM);
   }

   public static boolean isGrassOrDirt(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, Feature::isDirt);
   }

   public static boolean isAir(LevelSimulatedReader var0, BlockPos var1) {
      return var0.isStateAtPosition(var1, BlockBehaviour.BlockStateBase::isAir);
   }

   static {
      NO_OP = register("no_op", new NoOpFeature(NoneFeatureConfiguration.CODEC));
      TREE = register("tree", new TreeFeature(TreeConfiguration.CODEC));
      FLOWER = (AbstractFlowerFeature)register("flower", new DefaultFlowerFeature(RandomPatchConfiguration.CODEC));
      NO_BONEMEAL_FLOWER = (AbstractFlowerFeature)register("no_bonemeal_flower", new DefaultFlowerFeature(RandomPatchConfiguration.CODEC));
      RANDOM_PATCH = register("random_patch", new RandomPatchFeature(RandomPatchConfiguration.CODEC));
      BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockPileConfiguration.CODEC));
      SPRING = register("spring_feature", new SpringFeature(SpringConfiguration.CODEC));
      CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration.CODEC));
      EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfiguration.CODEC));
      VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration.CODEC));
      DESERT_WELL = register("desert_well", new DesertWellFeature(NoneFeatureConfiguration.CODEC));
      FOSSIL = register("fossil", new FossilFeature(NoneFeatureConfiguration.CODEC));
      HUGE_RED_MUSHROOM = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
      HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
      ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration.CODEC));
      GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration.CODEC));
      FREEZE_TOP_LAYER = register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration.CODEC));
      VINES = register("vines", new VinesFeature(NoneFeatureConfiguration.CODEC));
      MONSTER_ROOM = register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration.CODEC));
      BLUE_ICE = register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration.CODEC));
      ICEBERG = register("iceberg", new IcebergFeature(BlockStateConfiguration.CODEC));
      FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockStateConfiguration.CODEC));
      DISK = register("disk", new DiskReplaceFeature(DiskConfiguration.CODEC));
      ICE_PATCH = register("ice_patch", new IcePatchFeature(DiskConfiguration.CODEC));
      LAKE = register("lake", new LakeFeature(BlockStateConfiguration.CODEC));
      ORE = register("ore", new OreFeature(OreConfiguration.CODEC));
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
      NETHER_FOREST_VEGETATION = register("nether_forest_vegetation", new NetherForestVegetationFeature(BlockPileConfiguration.CODEC));
      WEEPING_VINES = register("weeping_vines", new WeepingVinesFeature(NoneFeatureConfiguration.CODEC));
      TWISTING_VINES = register("twisting_vines", new TwistingVinesFeature(NoneFeatureConfiguration.CODEC));
      BASALT_COLUMNS = register("basalt_columns", new BasaltColumnsFeature(ColumnFeatureConfiguration.CODEC));
      DELTA_FEATURE = register("delta_feature", new DeltaFeature(DeltaFeatureConfiguration.CODEC));
      REPLACE_BLOBS = register("netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceSphereConfiguration.CODEC));
      FILL_LAYER = register("fill_layer", new FillLayerFeature(LayerConfiguration.CODEC));
      BONUS_CHEST = (BonusChestFeature)register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration.CODEC));
      BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(NoneFeatureConfiguration.CODEC));
      NO_SURFACE_ORE = register("no_surface_ore", new NoSurfaceOreFeature(OreConfiguration.CODEC));
      RANDOM_SELECTOR = register("random_selector", new RandomSelectorFeature(RandomFeatureConfiguration.CODEC));
      SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfiguration.CODEC));
      RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfiguration.CODEC));
      DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfiguration.CODEC));
      GEODE = register("geode", new GeodeFeature(GeodeConfiguration.CODEC));
   }
}
