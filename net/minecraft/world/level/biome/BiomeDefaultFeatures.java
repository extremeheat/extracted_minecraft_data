package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.blockplacers.ColumnPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.DoublePlantPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.configurations.BlockBlobConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BuriedTreasureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MegaTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoiseDependantDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SeagrassFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SmallTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VillageConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.ForestFlowerProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.PlainFlowerProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.placement.CarvingMaskDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.DepthAverageConfigation;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.FrequencyChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FrequencyDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FrequencyWithExtraChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.NoiseCountFactorDecoratorConfiguration;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.material.Fluids;

public class BiomeDefaultFeatures {
   private static final BlockState GRASS;
   private static final BlockState FERN;
   private static final BlockState PODZOL;
   private static final BlockState OAK_LOG;
   private static final BlockState OAK_LEAVES;
   private static final BlockState JUNGLE_LOG;
   private static final BlockState JUNGLE_LEAVES;
   private static final BlockState SPRUCE_LOG;
   private static final BlockState SPRUCE_LEAVES;
   private static final BlockState ACACIA_LOG;
   private static final BlockState ACACIA_LEAVES;
   private static final BlockState BIRCH_LOG;
   private static final BlockState BIRCH_LEAVES;
   private static final BlockState DARK_OAK_LOG;
   private static final BlockState DARK_OAK_LEAVES;
   private static final BlockState WATER;
   private static final BlockState LAVA;
   private static final BlockState DIRT;
   private static final BlockState GRAVEL;
   private static final BlockState GRANITE;
   private static final BlockState DIORITE;
   private static final BlockState ANDESITE;
   private static final BlockState COAL_ORE;
   private static final BlockState IRON_ORE;
   private static final BlockState GOLD_ORE;
   private static final BlockState REDSTONE_ORE;
   private static final BlockState DIAMOND_ORE;
   private static final BlockState LAPIS_ORE;
   private static final BlockState STONE;
   private static final BlockState EMERALD_ORE;
   private static final BlockState INFESTED_STONE;
   private static final BlockState SAND;
   private static final BlockState CLAY;
   private static final BlockState GRASS_BLOCK;
   private static final BlockState MOSSY_COBBLESTONE;
   private static final BlockState LARGE_FERN;
   private static final BlockState TALL_GRASS;
   private static final BlockState LILAC;
   private static final BlockState ROSE_BUSH;
   private static final BlockState PEONY;
   private static final BlockState BROWN_MUSHROOM;
   private static final BlockState RED_MUSHROOM;
   private static final BlockState SEAGRASS;
   private static final BlockState PACKED_ICE;
   private static final BlockState BLUE_ICE;
   private static final BlockState LILY_OF_THE_VALLEY;
   private static final BlockState BLUE_ORCHID;
   private static final BlockState POPPY;
   private static final BlockState DANDELION;
   private static final BlockState DEAD_BUSH;
   private static final BlockState MELON;
   private static final BlockState PUMPKIN;
   private static final BlockState SWEET_BERRY_BUSH;
   private static final BlockState FIRE;
   private static final BlockState NETHERRACK;
   private static final BlockState LILY_PAD;
   private static final BlockState SNOW;
   private static final BlockState JACK_O_LANTERN;
   private static final BlockState SUNFLOWER;
   private static final BlockState CACTUS;
   private static final BlockState SUGAR_CANE;
   private static final BlockState HUGE_RED_MUSHROOM;
   private static final BlockState HUGE_BROWN_MUSHROOM;
   private static final BlockState HUGE_MUSHROOM_STEM;
   public static final SmallTreeConfiguration NORMAL_TREE_CONFIG;
   public static final SmallTreeConfiguration JUNGLE_TREE_CONFIG;
   public static final SmallTreeConfiguration JUNGLE_TREE_NOVINE_CONFIG;
   public static final SmallTreeConfiguration PINE_TREE_CONFIG;
   public static final SmallTreeConfiguration SPRUCE_TREE_CONFIG;
   public static final SmallTreeConfiguration ACACIA_TREE_CONFIG;
   public static final SmallTreeConfiguration BIRCH_TREE_CONFIG;
   public static final SmallTreeConfiguration SUPER_BIRCH_TREE_CONFIG;
   public static final SmallTreeConfiguration SWAMP_TREE_CONFIG;
   public static final SmallTreeConfiguration FANCY_TREE_CONFIG;
   public static final SmallTreeConfiguration NORMAL_TREE_WITH_BEES_005_CONFIG;
   public static final SmallTreeConfiguration FANCY_TREE_WITH_BEES_005_CONFIG;
   public static final SmallTreeConfiguration NORMAL_TREE_WITH_BEES_001_CONFIG;
   public static final SmallTreeConfiguration FANCY_TREE_WITH_BEES_001_CONFIG;
   public static final SmallTreeConfiguration BIRCH_TREE_WITH_BEES_001_CONFIG;
   public static final TreeConfiguration JUNGLE_BUSH_CONFIG;
   public static final MegaTreeConfiguration DARK_OAK_TREE_CONFIG;
   public static final MegaTreeConfiguration MEGA_SPRUCE_TREE_CONFIG;
   public static final MegaTreeConfiguration MEGA_PINE_TREE_CONFIG;
   public static final MegaTreeConfiguration MEGA_JUNGLE_TREE_CONFIG;
   public static final RandomPatchConfiguration DEFAULT_GRASS_CONFIG;
   public static final RandomPatchConfiguration TAIGA_GRASS_CONFIG;
   public static final RandomPatchConfiguration JUNGLE_GRASS_CONFIG;
   public static final RandomPatchConfiguration GENERAL_FOREST_FLOWER_CONFIG;
   public static final RandomPatchConfiguration SwAMP_FLOWER_CONFIG;
   public static final RandomPatchConfiguration DEFAULT_FLOWER_CONFIG;
   public static final RandomPatchConfiguration PLAIN_FLOWER_CONFIG;
   public static final RandomPatchConfiguration FOREST_FLOWER_CONFIG;
   public static final RandomPatchConfiguration DEAD_BUSH_CONFIG;
   public static final RandomPatchConfiguration MELON_CONFIG;
   public static final RandomPatchConfiguration PUMPKIN_CONFIG;
   public static final RandomPatchConfiguration SWEET_BERRY_BUSH_CONFIG;
   public static final RandomPatchConfiguration HELL_FIRE_CONFIG;
   public static final RandomPatchConfiguration WATERLILLY_CONFIG;
   public static final RandomPatchConfiguration RED_MUSHROOM_CONFIG;
   public static final RandomPatchConfiguration BROWN_MUSHROOM_CONFIG;
   public static final RandomPatchConfiguration DOUBLE_LILAC_CONFIG;
   public static final RandomPatchConfiguration DOUBLE_ROSE_BUSH_CONFIG;
   public static final RandomPatchConfiguration DOUBLE_PEONY_CONFIG;
   public static final RandomPatchConfiguration SUNFLOWER_CONFIG;
   public static final RandomPatchConfiguration TALL_GRASS_CONFIG;
   public static final RandomPatchConfiguration LARGE_FERN_CONFIG;
   public static final RandomPatchConfiguration CACTUS_CONFIG;
   public static final RandomPatchConfiguration SUGAR_CANE_CONFIG;
   public static final BlockPileConfiguration HAY_PILE_CONFIG;
   public static final BlockPileConfiguration SNOW_PILE_CONFIG;
   public static final BlockPileConfiguration MELON_PILE_CONFIG;
   public static final BlockPileConfiguration PUMPKIN_PILE_CONFIG;
   public static final BlockPileConfiguration ICE_PILE_CONFIG;
   public static final SpringConfiguration WATER_SPRING_CONFIG;
   public static final SpringConfiguration LAVA_SPRING_CONFIG;
   public static final SpringConfiguration OPEN_NETHER_SPRING_CONFIG;
   public static final SpringConfiguration CLOSED_NETHER_SPRING_CONFIG;
   public static final HugeMushroomFeatureConfiguration HUGE_RED_MUSHROOM_CONFIG;
   public static final HugeMushroomFeatureConfiguration HUGE_BROWN_MUSHROOM_CONFIG;

   public static void addDefaultCarvers(Biome var0) {
      var0.addCarver(GenerationStep.Carving.AIR, Biome.makeCarver(WorldCarver.CAVE, new ProbabilityFeatureConfiguration(0.14285715F)));
      var0.addCarver(GenerationStep.Carving.AIR, Biome.makeCarver(WorldCarver.CANYON, new ProbabilityFeatureConfiguration(0.02F)));
   }

   public static void addOceanCarvers(Biome var0) {
      var0.addCarver(GenerationStep.Carving.AIR, Biome.makeCarver(WorldCarver.CAVE, new ProbabilityFeatureConfiguration(0.06666667F)));
      var0.addCarver(GenerationStep.Carving.AIR, Biome.makeCarver(WorldCarver.CANYON, new ProbabilityFeatureConfiguration(0.02F)));
      var0.addCarver(GenerationStep.Carving.LIQUID, Biome.makeCarver(WorldCarver.UNDERWATER_CANYON, new ProbabilityFeatureConfiguration(0.02F)));
      var0.addCarver(GenerationStep.Carving.LIQUID, Biome.makeCarver(WorldCarver.UNDERWATER_CAVE, new ProbabilityFeatureConfiguration(0.06666667F)));
   }

   public static void addStructureFeaturePlacement(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Feature.MINESHAFT.configured(new MineshaftConfiguration(0.004000000189989805D, MineshaftFeature.Type.NORMAL)).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.PILLAGER_OUTPOST.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Feature.STRONGHOLD.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.SWAMP_HUT.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.DESERT_PYRAMID.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.JUNGLE_TEMPLE.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.IGLOO.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.SHIPWRECK.configured(new ShipwreckConfiguration(false)).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.OCEAN_MONUMENT.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.WOODLAND_MANSION.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.OCEAN_RUIN.configured(new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.9F)).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Feature.BURIED_TREASURE.configured(new BuriedTreasureConfiguration(0.01F)).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.VILLAGE.configured(new VillageConfiguration("village/plains/town_centers", 6)).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
   }

   public static void addDefaultLakes(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.configured(new BlockStateConfiguration(WATER)).decorated(FeatureDecorator.WATER_LAKE.configured(new ChanceDecoratorConfiguration(4))));
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.configured(new BlockStateConfiguration(LAVA)).decorated(FeatureDecorator.LAVA_LAKE.configured(new ChanceDecoratorConfiguration(80))));
   }

   public static void addDesertLakes(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Feature.LAKE.configured(new BlockStateConfiguration(LAVA)).decorated(FeatureDecorator.LAVA_LAKE.configured(new ChanceDecoratorConfiguration(80))));
   }

   public static void addDefaultMonsterRoom(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, Feature.MONSTER_ROOM.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.DUNGEONS.configured(new ChanceDecoratorConfiguration(8))));
   }

   public static void addDefaultUndergroundVariety(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, DIRT, 33)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(10, 0, 0, 256))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, GRAVEL, 33)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(8, 0, 0, 256))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, GRANITE, 33)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(10, 0, 0, 80))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, DIORITE, 33)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(10, 0, 0, 80))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, ANDESITE, 33)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(10, 0, 0, 80))));
   }

   public static void addDefaultOres(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, COAL_ORE, 17)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(20, 0, 0, 128))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, IRON_ORE, 9)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(20, 0, 0, 64))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, GOLD_ORE, 9)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(2, 0, 0, 32))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, REDSTONE_ORE, 8)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(8, 0, 0, 16))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, DIAMOND_ORE, 8)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(1, 0, 0, 16))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, LAPIS_ORE, 7)).decorated(FeatureDecorator.COUNT_DEPTH_AVERAGE.configured(new DepthAverageConfigation(1, 16, 16))));
   }

   public static void addExtraGold(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, GOLD_ORE, 9)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(20, 32, 32, 80))));
   }

   public static void addExtraEmeralds(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.EMERALD_ORE.configured(new ReplaceBlockConfiguration(STONE, EMERALD_ORE)).decorated(FeatureDecorator.EMERALD_ORE.configured(DecoratorConfiguration.NONE)));
   }

   public static void addInfestedStone(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, INFESTED_STONE, 9)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(7, 0, 0, 64))));
   }

   public static void addDefaultSoftDisks(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new DiskConfiguration(SAND, 7, 2, Lists.newArrayList(new BlockState[]{DIRT, GRASS_BLOCK}))).decorated(FeatureDecorator.COUNT_TOP_SOLID.configured(new FrequencyDecoratorConfiguration(3))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new DiskConfiguration(CLAY, 4, 1, Lists.newArrayList(new BlockState[]{DIRT, CLAY}))).decorated(FeatureDecorator.COUNT_TOP_SOLID.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new DiskConfiguration(GRAVEL, 6, 2, Lists.newArrayList(new BlockState[]{DIRT, GRASS_BLOCK}))).decorated(FeatureDecorator.COUNT_TOP_SOLID.configured(new FrequencyDecoratorConfiguration(1))));
   }

   public static void addSwampClayDisk(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new DiskConfiguration(CLAY, 4, 1, Lists.newArrayList(new BlockState[]{DIRT, CLAY}))).decorated(FeatureDecorator.COUNT_TOP_SOLID.configured(new FrequencyDecoratorConfiguration(1))));
   }

   public static void addMossyStoneBlock(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Feature.FOREST_ROCK.configured(new BlockBlobConfiguration(MOSSY_COBBLESTONE, 0)).decorated(FeatureDecorator.FOREST_ROCK.configured(new FrequencyDecoratorConfiguration(3))));
   }

   public static void addFerns(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(LARGE_FERN_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_32.configured(new FrequencyDecoratorConfiguration(7))));
   }

   public static void addBerryBushes(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SWEET_BERRY_BUSH_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(12))));
   }

   public static void addSparseBerryBushes(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SWEET_BERRY_BUSH_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
   }

   public static void addLightBambooVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.BAMBOO.configured(new ProbabilityFeatureConfiguration(0.0F)).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(16))));
   }

   public static void addBambooVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.BAMBOO.configured(new ProbabilityFeatureConfiguration(0.2F)).decorated(FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configured(new NoiseCountFactorDecoratorConfiguration(160, 80.0D, 0.3D, Heightmap.Types.WORLD_SURFACE_WG))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.05F), Feature.JUNGLE_GROUND_BUSH.configured(JUNGLE_BUSH_CONFIG).weighted(0.15F), Feature.MEGA_JUNGLE_TREE.configured(MEGA_JUNGLE_TREE_CONFIG).weighted(0.7F)), Feature.RANDOM_PATCH.configured(JUNGLE_GRASS_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(30, 0.1F, 1))));
   }

   public static void addTaigaTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.NORMAL_TREE.configured(PINE_TREE_CONFIG).weighted(0.33333334F)), Feature.NORMAL_TREE.configured(SPRUCE_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));
   }

   public static void addWaterTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.1F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0.1F, 1))));
   }

   public static void addBirchTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.configured(BIRCH_TREE_CONFIG).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));
   }

   public static void addOtherBirchTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.NORMAL_TREE.configured(BIRCH_TREE_CONFIG).weighted(0.2F), Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.1F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));
   }

   public static void addTallBirchTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.NORMAL_TREE.configured(SUPER_BIRCH_TREE_CONFIG).weighted(0.5F)), Feature.NORMAL_TREE.configured(BIRCH_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));
   }

   public static void addSavannaTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.ACACIA_TREE.configured(ACACIA_TREE_CONFIG).weighted(0.8F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(1, 0.1F, 1))));
   }

   public static void addShatteredSavannaTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.ACACIA_TREE.configured(ACACIA_TREE_CONFIG).weighted(0.8F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(2, 0.1F, 1))));
   }

   public static void addMountainTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.NORMAL_TREE.configured(SPRUCE_TREE_CONFIG).weighted(0.666F), Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.1F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0.1F, 1))));
   }

   public static void addMountainEdgeTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.NORMAL_TREE.configured(SPRUCE_TREE_CONFIG).weighted(0.666F), Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.1F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(3, 0.1F, 1))));
   }

   public static void addJungleTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.1F), Feature.JUNGLE_GROUND_BUSH.configured(JUNGLE_BUSH_CONFIG).weighted(0.5F), Feature.MEGA_JUNGLE_TREE.configured(MEGA_JUNGLE_TREE_CONFIG).weighted(0.33333334F)), Feature.NORMAL_TREE.configured(JUNGLE_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(50, 0.1F, 1))));
   }

   public static void addJungleEdgeTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.FANCY_TREE.configured(FANCY_TREE_CONFIG).weighted(0.1F), Feature.JUNGLE_GROUND_BUSH.configured(JUNGLE_BUSH_CONFIG).weighted(0.5F)), Feature.NORMAL_TREE.configured(JUNGLE_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(2, 0.1F, 1))));
   }

   public static void addBadlandsTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.configured(NORMAL_TREE_CONFIG).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(5, 0.1F, 1))));
   }

   public static void addSnowyTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.configured(SPRUCE_TREE_CONFIG).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0.1F, 1))));
   }

   public static void addGiantSpruceTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.MEGA_SPRUCE_TREE.configured(MEGA_SPRUCE_TREE_CONFIG).weighted(0.33333334F), Feature.NORMAL_TREE.configured(PINE_TREE_CONFIG).weighted(0.33333334F)), Feature.NORMAL_TREE.configured(SPRUCE_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));
   }

   public static void addGiantTrees(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.MEGA_SPRUCE_TREE.configured(MEGA_SPRUCE_TREE_CONFIG).weighted(0.025641026F), Feature.MEGA_SPRUCE_TREE.configured(MEGA_PINE_TREE_CONFIG).weighted(0.30769232F), Feature.NORMAL_TREE.configured(PINE_TREE_CONFIG).weighted(0.33333334F)), Feature.NORMAL_TREE.configured(SPRUCE_TREE_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(10, 0.1F, 1))));
   }

   public static void addJungleGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(JUNGLE_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(25))));
   }

   public static void addSavannaGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(TALL_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_32.configured(new FrequencyDecoratorConfiguration(7))));
   }

   public static void addShatteredSavannaGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(5))));
   }

   public static void addSavannaExtraGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(20))));
   }

   public static void addBadlandGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEAD_BUSH_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(20))));
   }

   public static void addForestFlowers(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_RANDOM_SELECTOR.configured(new RandomRandomFeatureConfiguration(ImmutableList.of(Feature.RANDOM_PATCH.configured(DOUBLE_LILAC_CONFIG), Feature.RANDOM_PATCH.configured(DOUBLE_ROSE_BUSH_CONFIG), Feature.RANDOM_PATCH.configured(DOUBLE_PEONY_CONFIG), Feature.FLOWER.configured(GENERAL_FOREST_FLOWER_CONFIG)), 0)).decorated(FeatureDecorator.COUNT_HEIGHTMAP_32.configured(new FrequencyDecoratorConfiguration(5))));
   }

   public static void addForestGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(2))));
   }

   public static void addSwampVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.NORMAL_TREE.configured(SWAMP_TREE_CONFIG).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(2, 0.1F, 1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(SwAMP_FLOWER_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_32.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(5))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEAD_BUSH_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(WATERLILLY_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(4))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP.configured(new FrequencyChanceDecoratorConfiguration(8, 0.25F))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configured(new FrequencyChanceDecoratorConfiguration(8, 0.125F))));
   }

   public static void addMushroomFieldVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_BOOLEAN_SELECTOR.configured(new RandomBooleanFeatureConfiguration(Feature.HUGE_RED_MUSHROOM.configured(HUGE_RED_MUSHROOM_CONFIG), Feature.HUGE_BROWN_MUSHROOM.configured(HUGE_BROWN_MUSHROOM_CONFIG))).decorated(FeatureDecorator.COUNT_HEIGHTMAP.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP.configured(new FrequencyChanceDecoratorConfiguration(1, 0.25F))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configured(new FrequencyChanceDecoratorConfiguration(1, 0.125F))));
   }

   public static void addPlainVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.configured(new RandomFeatureConfiguration(ImmutableList.of(Feature.FANCY_TREE.configured(FANCY_TREE_WITH_BEES_005_CONFIG).weighted(0.33333334F)), Feature.NORMAL_TREE.configured(NORMAL_TREE_WITH_BEES_005_CONFIG))).decorated(FeatureDecorator.COUNT_EXTRA_HEIGHTMAP.configured(new FrequencyWithExtraChanceDecoratorConfiguration(0, 0.05F, 1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(PLAIN_FLOWER_CONFIG).decorated(FeatureDecorator.NOISE_HEIGHTMAP_32.configured(new NoiseDependantDecoratorConfiguration(-0.8D, 15, 4))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.NOISE_HEIGHTMAP_DOUBLE.configured(new NoiseDependantDecoratorConfiguration(-0.8D, 5, 10))));
   }

   public static void addDesertVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEAD_BUSH_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(2))));
   }

   public static void addGiantTaigaVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(TAIGA_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(7))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEAD_BUSH_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP.configured(new FrequencyChanceDecoratorConfiguration(3, 0.25F))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configured(new FrequencyChanceDecoratorConfiguration(3, 0.125F))));
   }

   public static void addDefaultFlowers(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(DEFAULT_FLOWER_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_32.configured(new FrequencyDecoratorConfiguration(2))));
   }

   public static void addWarmFlowers(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(DEFAULT_FLOWER_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_32.configured(new FrequencyDecoratorConfiguration(4))));
   }

   public static void addDefaultGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEFAULT_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
   }

   public static void addTaigaGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(TAIGA_GRASS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP.configured(new FrequencyChanceDecoratorConfiguration(1, 0.25F))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(FeatureDecorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE.configured(new FrequencyChanceDecoratorConfiguration(1, 0.125F))));
   }

   public static void addPlainGrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(TALL_GRASS_CONFIG).decorated(FeatureDecorator.NOISE_HEIGHTMAP_32.configured(new NoiseDependantDecoratorConfiguration(-0.8D, 0, 7))));
   }

   public static void addDefaultMushrooms(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(4))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(8))));
   }

   public static void addDefaultExtraVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SUGAR_CANE_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(10))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(PUMPKIN_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(32))));
   }

   public static void addBadlandExtraVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SUGAR_CANE_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(13))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(PUMPKIN_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(32))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(CACTUS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(5))));
   }

   public static void addJungleExtraVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(MELON_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(1))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.VINES.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.COUNT_HEIGHT_64.configured(new FrequencyDecoratorConfiguration(50))));
   }

   public static void addDesertExtraVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SUGAR_CANE_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(60))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(PUMPKIN_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(32))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(CACTUS_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(10))));
   }

   public static void addSwampExtraVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SUGAR_CANE_CONFIG).decorated(FeatureDecorator.COUNT_HEIGHTMAP_DOUBLE.configured(new FrequencyDecoratorConfiguration(20))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(PUMPKIN_CONFIG).decorated(FeatureDecorator.CHANCE_HEIGHTMAP_DOUBLE.configured(new ChanceDecoratorConfiguration(32))));
   }

   public static void addDesertExtraDecoration(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.DESERT_WELL.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.CHANCE_HEIGHTMAP.configured(new ChanceDecoratorConfiguration(1000))));
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Feature.FOSSIL.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.CHANCE_PASSTHROUGH.configured(new ChanceDecoratorConfiguration(64))));
   }

   public static void addSwampExtraDecoration(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, Feature.FOSSIL.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.CHANCE_PASSTHROUGH.configured(new ChanceDecoratorConfiguration(64))));
   }

   public static void addColdOceanExtraVegetation(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.KELP.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configured(new NoiseCountFactorDecoratorConfiguration(120, 80.0D, 0.0D, Heightmap.Types.OCEAN_FLOOR_WG))));
   }

   public static void addDefaultSeagrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configured(new SimpleBlockConfiguration(SEAGRASS, new BlockState[]{STONE}, new BlockState[]{WATER}, new BlockState[]{WATER})).decorated(FeatureDecorator.CARVING_MASK.configured(new CarvingMaskDecoratorConfiguration(GenerationStep.Carving.LIQUID, 0.1F))));
   }

   public static void addWarmSeagrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.configured(new SeagrassFeatureConfiguration(80, 0.3D)).decorated(FeatureDecorator.TOP_SOLID_HEIGHTMAP.configured(DecoratorConfiguration.NONE)));
   }

   public static void addDeepWarmSeagrass(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.SEAGRASS.configured(new SeagrassFeatureConfiguration(80, 0.8D)).decorated(FeatureDecorator.TOP_SOLID_HEIGHTMAP.configured(DecoratorConfiguration.NONE)));
   }

   public static void addLukeWarmKelp(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.KELP.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED.configured(new NoiseCountFactorDecoratorConfiguration(80, 80.0D, 0.0D, Heightmap.Types.OCEAN_FLOOR_WG))));
   }

   public static void addDefaultSprings(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.SPRING.configured(WATER_SPRING_CONFIG).decorated(FeatureDecorator.COUNT_BIASED_RANGE.configured(new CountRangeDecoratorConfiguration(50, 8, 8, 256))));
      var0.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Feature.SPRING.configured(LAVA_SPRING_CONFIG).decorated(FeatureDecorator.COUNT_VERY_BIASED_RANGE.configured(new CountRangeDecoratorConfiguration(20, 8, 16, 256))));
   }

   public static void addIcebergs(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Feature.ICEBERG.configured(new BlockStateConfiguration(PACKED_ICE)).decorated(FeatureDecorator.ICEBERG.configured(new ChanceDecoratorConfiguration(16))));
      var0.addFeature(GenerationStep.Decoration.LOCAL_MODIFICATIONS, Feature.ICEBERG.configured(new BlockStateConfiguration(BLUE_ICE)).decorated(FeatureDecorator.ICEBERG.configured(new ChanceDecoratorConfiguration(200))));
   }

   public static void addBlueIce(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.BLUE_ICE.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.RANDOM_COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(20, 30, 32, 64))));
   }

   public static void addSurfaceFreezing(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, Feature.FREEZE_TOP_LAYER.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
   }

   public static void addEndCity(Biome var0) {
      var0.addFeature(GenerationStep.Decoration.SURFACE_STRUCTURES, Feature.END_CITY.configured(FeatureConfiguration.NONE).decorated(FeatureDecorator.NOPE.configured(DecoratorConfiguration.NONE)));
   }

   static {
      GRASS = Blocks.GRASS.defaultBlockState();
      FERN = Blocks.FERN.defaultBlockState();
      PODZOL = Blocks.PODZOL.defaultBlockState();
      OAK_LOG = Blocks.OAK_LOG.defaultBlockState();
      OAK_LEAVES = Blocks.OAK_LEAVES.defaultBlockState();
      JUNGLE_LOG = Blocks.JUNGLE_LOG.defaultBlockState();
      JUNGLE_LEAVES = Blocks.JUNGLE_LEAVES.defaultBlockState();
      SPRUCE_LOG = Blocks.SPRUCE_LOG.defaultBlockState();
      SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.defaultBlockState();
      ACACIA_LOG = Blocks.ACACIA_LOG.defaultBlockState();
      ACACIA_LEAVES = Blocks.ACACIA_LEAVES.defaultBlockState();
      BIRCH_LOG = Blocks.BIRCH_LOG.defaultBlockState();
      BIRCH_LEAVES = Blocks.BIRCH_LEAVES.defaultBlockState();
      DARK_OAK_LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
      DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.defaultBlockState();
      WATER = Blocks.WATER.defaultBlockState();
      LAVA = Blocks.LAVA.defaultBlockState();
      DIRT = Blocks.DIRT.defaultBlockState();
      GRAVEL = Blocks.GRAVEL.defaultBlockState();
      GRANITE = Blocks.GRANITE.defaultBlockState();
      DIORITE = Blocks.DIORITE.defaultBlockState();
      ANDESITE = Blocks.ANDESITE.defaultBlockState();
      COAL_ORE = Blocks.COAL_ORE.defaultBlockState();
      IRON_ORE = Blocks.IRON_ORE.defaultBlockState();
      GOLD_ORE = Blocks.GOLD_ORE.defaultBlockState();
      REDSTONE_ORE = Blocks.REDSTONE_ORE.defaultBlockState();
      DIAMOND_ORE = Blocks.DIAMOND_ORE.defaultBlockState();
      LAPIS_ORE = Blocks.LAPIS_ORE.defaultBlockState();
      STONE = Blocks.STONE.defaultBlockState();
      EMERALD_ORE = Blocks.EMERALD_ORE.defaultBlockState();
      INFESTED_STONE = Blocks.INFESTED_STONE.defaultBlockState();
      SAND = Blocks.SAND.defaultBlockState();
      CLAY = Blocks.CLAY.defaultBlockState();
      GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
      MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
      LARGE_FERN = Blocks.LARGE_FERN.defaultBlockState();
      TALL_GRASS = Blocks.TALL_GRASS.defaultBlockState();
      LILAC = Blocks.LILAC.defaultBlockState();
      ROSE_BUSH = Blocks.ROSE_BUSH.defaultBlockState();
      PEONY = Blocks.PEONY.defaultBlockState();
      BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.defaultBlockState();
      RED_MUSHROOM = Blocks.RED_MUSHROOM.defaultBlockState();
      SEAGRASS = Blocks.SEAGRASS.defaultBlockState();
      PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
      BLUE_ICE = Blocks.BLUE_ICE.defaultBlockState();
      LILY_OF_THE_VALLEY = Blocks.LILY_OF_THE_VALLEY.defaultBlockState();
      BLUE_ORCHID = Blocks.BLUE_ORCHID.defaultBlockState();
      POPPY = Blocks.POPPY.defaultBlockState();
      DANDELION = Blocks.DANDELION.defaultBlockState();
      DEAD_BUSH = Blocks.DEAD_BUSH.defaultBlockState();
      MELON = Blocks.MELON.defaultBlockState();
      PUMPKIN = Blocks.PUMPKIN.defaultBlockState();
      SWEET_BERRY_BUSH = (BlockState)Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3);
      FIRE = Blocks.FIRE.defaultBlockState();
      NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
      LILY_PAD = Blocks.LILY_PAD.defaultBlockState();
      SNOW = Blocks.SNOW.defaultBlockState();
      JACK_O_LANTERN = Blocks.JACK_O_LANTERN.defaultBlockState();
      SUNFLOWER = Blocks.SUNFLOWER.defaultBlockState();
      CACTUS = Blocks.CACTUS.defaultBlockState();
      SUGAR_CANE = Blocks.SUGAR_CANE.defaultBlockState();
      HUGE_RED_MUSHROOM = (BlockState)Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, false);
      HUGE_BROWN_MUSHROOM = (BlockState)((BlockState)Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.UP, true)).setValue(HugeMushroomBlock.DOWN, false);
      HUGE_MUSHROOM_STEM = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, false)).setValue(HugeMushroomBlock.DOWN, false);
      NORMAL_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines().build();
      JUNGLE_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(4).heightRandA(8).foliageHeight(3).decorators(ImmutableList.of(new CocoaDecorator(0.2F), new TrunkVineDecorator(), new LeaveVineDecorator())).ignoreVines().build();
      JUNGLE_TREE_NOVINE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(4).heightRandA(8).foliageHeight(3).ignoreVines().build();
      PINE_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES), new PineFoliagePlacer(1, 0))).baseHeight(7).heightRandA(4).trunkTopOffset(1).foliageHeight(3).foliageHeightRandom(1).ignoreVines().build();
      SPRUCE_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES), new SpruceFoliagePlacer(2, 1))).baseHeight(6).heightRandA(3).trunkHeight(1).trunkHeightRandom(1).trunkTopOffsetRandom(2).ignoreVines().build();
      ACACIA_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(ACACIA_LOG), new SimpleStateProvider(ACACIA_LEAVES), new AcaciaFoliagePlacer(2, 0))).baseHeight(5).heightRandA(2).heightRandB(2).trunkHeight(0).ignoreVines().build();
      BIRCH_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(5).heightRandA(2).foliageHeight(3).ignoreVines().build();
      SUPER_BIRCH_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(5).heightRandA(2).heightRandB(6).foliageHeight(3).ignoreVines().build();
      SWAMP_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(3, 0))).baseHeight(5).heightRandA(3).foliageHeight(3).maxWaterDepth(1).decorators(ImmutableList.of(new LeaveVineDecorator())).build();
      FANCY_TREE_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0))).build();
      NORMAL_TREE_WITH_BEES_005_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines().decorators(ImmutableList.of(new BeehiveDecorator(0.05F))).build();
      FANCY_TREE_WITH_BEES_005_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0))).decorators(ImmutableList.of(new BeehiveDecorator(0.05F))).build();
      NORMAL_TREE_WITH_BEES_001_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines().decorators(ImmutableList.of(new BeehiveDecorator(0.01F))).build();
      FANCY_TREE_WITH_BEES_001_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(OAK_LOG), new SimpleStateProvider(OAK_LEAVES), new BlobFoliagePlacer(0, 0))).decorators(ImmutableList.of(new BeehiveDecorator(0.01F))).build();
      BIRCH_TREE_WITH_BEES_001_CONFIG = (new SmallTreeConfiguration.SmallTreeConfigurationBuilder(new SimpleStateProvider(BIRCH_LOG), new SimpleStateProvider(BIRCH_LEAVES), new BlobFoliagePlacer(2, 0))).baseHeight(5).heightRandA(2).foliageHeight(3).ignoreVines().decorators(ImmutableList.of(new BeehiveDecorator(0.01F))).build();
      JUNGLE_BUSH_CONFIG = (new TreeConfiguration.TreeConfigurationBuilder(new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(OAK_LEAVES))).baseHeight(4).build();
      DARK_OAK_TREE_CONFIG = (new MegaTreeConfiguration.MegaTreeConfigurationBuilder(new SimpleStateProvider(DARK_OAK_LOG), new SimpleStateProvider(DARK_OAK_LEAVES))).baseHeight(6).build();
      MEGA_SPRUCE_TREE_CONFIG = (new MegaTreeConfiguration.MegaTreeConfigurationBuilder(new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES))).baseHeight(13).heightInterval(15).crownHeight(13).decorators(ImmutableList.of(new AlterGroundDecorator(new SimpleStateProvider(PODZOL)))).build();
      MEGA_PINE_TREE_CONFIG = (new MegaTreeConfiguration.MegaTreeConfigurationBuilder(new SimpleStateProvider(SPRUCE_LOG), new SimpleStateProvider(SPRUCE_LEAVES))).baseHeight(13).heightInterval(15).crownHeight(3).decorators(ImmutableList.of(new AlterGroundDecorator(new SimpleStateProvider(PODZOL)))).build();
      MEGA_JUNGLE_TREE_CONFIG = (new MegaTreeConfiguration.MegaTreeConfigurationBuilder(new SimpleStateProvider(JUNGLE_LOG), new SimpleStateProvider(JUNGLE_LEAVES))).baseHeight(10).heightInterval(20).decorators(ImmutableList.of(new TrunkVineDecorator(), new LeaveVineDecorator())).build();
      DEFAULT_GRASS_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(GRASS), new SimpleBlockPlacer())).tries(32).build();
      TAIGA_GRASS_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder((new WeightedStateProvider()).add(GRASS, 1).add(FERN, 4), new SimpleBlockPlacer())).tries(32).build();
      JUNGLE_GRASS_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder((new WeightedStateProvider()).add(GRASS, 3).add(FERN, 1), new SimpleBlockPlacer())).blacklist(ImmutableSet.of(PODZOL)).tries(32).build();
      GENERAL_FOREST_FLOWER_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(LILY_OF_THE_VALLEY), new SimpleBlockPlacer())).tries(64).build();
      SwAMP_FLOWER_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(BLUE_ORCHID), new SimpleBlockPlacer())).tries(64).build();
      DEFAULT_FLOWER_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder((new WeightedStateProvider()).add(POPPY, 2).add(DANDELION, 1), new SimpleBlockPlacer())).tries(64).build();
      PLAIN_FLOWER_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new PlainFlowerProvider(), new SimpleBlockPlacer())).tries(64).build();
      FOREST_FLOWER_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new ForestFlowerProvider(), new SimpleBlockPlacer())).tries(64).build();
      DEAD_BUSH_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(DEAD_BUSH), new SimpleBlockPlacer())).tries(4).build();
      MELON_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(MELON), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock())).canReplace().noProjection().build();
      PUMPKIN_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(PUMPKIN), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock())).noProjection().build();
      SWEET_BERRY_BUSH_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(SWEET_BERRY_BUSH), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock())).noProjection().build();
      HELL_FIRE_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(FIRE), new SimpleBlockPlacer())).tries(64).whitelist(ImmutableSet.of(NETHERRACK.getBlock())).noProjection().build();
      WATERLILLY_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(LILY_PAD), new SimpleBlockPlacer())).tries(10).build();
      RED_MUSHROOM_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(RED_MUSHROOM), new SimpleBlockPlacer())).tries(64).noProjection().build();
      BROWN_MUSHROOM_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(BROWN_MUSHROOM), new SimpleBlockPlacer())).tries(64).noProjection().build();
      DOUBLE_LILAC_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(LILAC), new DoublePlantPlacer())).tries(64).noProjection().build();
      DOUBLE_ROSE_BUSH_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(ROSE_BUSH), new DoublePlantPlacer())).tries(64).noProjection().build();
      DOUBLE_PEONY_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(PEONY), new DoublePlantPlacer())).tries(64).noProjection().build();
      SUNFLOWER_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(SUNFLOWER), new DoublePlantPlacer())).tries(64).noProjection().build();
      TALL_GRASS_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(TALL_GRASS), new DoublePlantPlacer())).tries(64).noProjection().build();
      LARGE_FERN_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(LARGE_FERN), new DoublePlantPlacer())).tries(64).noProjection().build();
      CACTUS_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(CACTUS), new ColumnPlacer(1, 2))).tries(10).noProjection().build();
      SUGAR_CANE_CONFIG = (new RandomPatchConfiguration.GrassConfigurationBuilder(new SimpleStateProvider(SUGAR_CANE), new ColumnPlacer(2, 2))).tries(20).xspread(4).yspread(0).zspread(4).noProjection().needWater().build();
      HAY_PILE_CONFIG = new BlockPileConfiguration(new RotatedBlockProvider(Blocks.HAY_BLOCK));
      SNOW_PILE_CONFIG = new BlockPileConfiguration(new SimpleStateProvider(SNOW));
      MELON_PILE_CONFIG = new BlockPileConfiguration(new SimpleStateProvider(MELON));
      PUMPKIN_PILE_CONFIG = new BlockPileConfiguration((new WeightedStateProvider()).add(PUMPKIN, 19).add(JACK_O_LANTERN, 1));
      ICE_PILE_CONFIG = new BlockPileConfiguration((new WeightedStateProvider()).add(BLUE_ICE, 1).add(PACKED_ICE, 5));
      WATER_SPRING_CONFIG = new SpringConfiguration(Fluids.WATER.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
      LAVA_SPRING_CONFIG = new SpringConfiguration(Fluids.LAVA.defaultFluidState(), true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE));
      OPEN_NETHER_SPRING_CONFIG = new SpringConfiguration(Fluids.LAVA.defaultFluidState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK));
      CLOSED_NETHER_SPRING_CONFIG = new SpringConfiguration(Fluids.LAVA.defaultFluidState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK));
      HUGE_RED_MUSHROOM_CONFIG = new HugeMushroomFeatureConfiguration(new SimpleStateProvider(HUGE_RED_MUSHROOM), new SimpleStateProvider(HUGE_MUSHROOM_STEM), 2);
      HUGE_BROWN_MUSHROOM_CONFIG = new HugeMushroomFeatureConfiguration(new SimpleStateProvider(HUGE_BROWN_MUSHROOM), new SimpleStateProvider(HUGE_MUSHROOM_STEM), 3);
   }
}
