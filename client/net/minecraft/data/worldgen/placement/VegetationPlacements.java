package net.minecraft.data.worldgen.placement;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;

public class VegetationPlacements {
   public static final Holder<PlacedFeature> BAMBOO_LIGHT;
   public static final Holder<PlacedFeature> BAMBOO;
   public static final Holder<PlacedFeature> VINES;
   public static final Holder<PlacedFeature> PATCH_SUNFLOWER;
   public static final Holder<PlacedFeature> PATCH_PUMPKIN;
   public static final Holder<PlacedFeature> PATCH_GRASS_PLAIN;
   public static final Holder<PlacedFeature> PATCH_GRASS_FOREST;
   public static final Holder<PlacedFeature> PATCH_GRASS_BADLANDS;
   public static final Holder<PlacedFeature> PATCH_GRASS_SAVANNA;
   public static final Holder<PlacedFeature> PATCH_GRASS_NORMAL;
   public static final Holder<PlacedFeature> PATCH_GRASS_TAIGA_2;
   public static final Holder<PlacedFeature> PATCH_GRASS_TAIGA;
   public static final Holder<PlacedFeature> PATCH_GRASS_JUNGLE;
   public static final Holder<PlacedFeature> GRASS_BONEMEAL;
   public static final Holder<PlacedFeature> PATCH_DEAD_BUSH_2;
   public static final Holder<PlacedFeature> PATCH_DEAD_BUSH;
   public static final Holder<PlacedFeature> PATCH_DEAD_BUSH_BADLANDS;
   public static final Holder<PlacedFeature> PATCH_MELON;
   public static final Holder<PlacedFeature> PATCH_MELON_SPARSE;
   public static final Holder<PlacedFeature> PATCH_BERRY_COMMON;
   public static final Holder<PlacedFeature> PATCH_BERRY_RARE;
   public static final Holder<PlacedFeature> PATCH_WATERLILY;
   public static final Holder<PlacedFeature> PATCH_TALL_GRASS_2;
   public static final Holder<PlacedFeature> PATCH_TALL_GRASS;
   public static final Holder<PlacedFeature> PATCH_LARGE_FERN;
   public static final Holder<PlacedFeature> PATCH_CACTUS_DESERT;
   public static final Holder<PlacedFeature> PATCH_CACTUS_DECORATED;
   public static final Holder<PlacedFeature> PATCH_SUGAR_CANE_SWAMP;
   public static final Holder<PlacedFeature> PATCH_SUGAR_CANE_DESERT;
   public static final Holder<PlacedFeature> PATCH_SUGAR_CANE_BADLANDS;
   public static final Holder<PlacedFeature> PATCH_SUGAR_CANE;
   public static final Holder<PlacedFeature> BROWN_MUSHROOM_NETHER;
   public static final Holder<PlacedFeature> RED_MUSHROOM_NETHER;
   public static final Holder<PlacedFeature> BROWN_MUSHROOM_NORMAL;
   public static final Holder<PlacedFeature> RED_MUSHROOM_NORMAL;
   public static final Holder<PlacedFeature> BROWN_MUSHROOM_TAIGA;
   public static final Holder<PlacedFeature> RED_MUSHROOM_TAIGA;
   public static final Holder<PlacedFeature> BROWN_MUSHROOM_OLD_GROWTH;
   public static final Holder<PlacedFeature> RED_MUSHROOM_OLD_GROWTH;
   public static final Holder<PlacedFeature> BROWN_MUSHROOM_SWAMP;
   public static final Holder<PlacedFeature> RED_MUSHROOM_SWAMP;
   public static final Holder<PlacedFeature> FLOWER_WARM;
   public static final Holder<PlacedFeature> FLOWER_DEFAULT;
   public static final Holder<PlacedFeature> FLOWER_FLOWER_FOREST;
   public static final Holder<PlacedFeature> FLOWER_SWAMP;
   public static final Holder<PlacedFeature> FLOWER_PLAINS;
   public static final Holder<PlacedFeature> FLOWER_MEADOW;
   public static final PlacementModifier TREE_THRESHOLD;
   public static final Holder<PlacedFeature> TREES_PLAINS;
   public static final Holder<PlacedFeature> DARK_FOREST_VEGETATION;
   public static final Holder<PlacedFeature> FLOWER_FOREST_FLOWERS;
   public static final Holder<PlacedFeature> FOREST_FLOWERS;
   public static final Holder<PlacedFeature> TREES_FLOWER_FOREST;
   public static final Holder<PlacedFeature> TREES_MEADOW;
   public static final Holder<PlacedFeature> TREES_TAIGA;
   public static final Holder<PlacedFeature> TREES_GROVE;
   public static final Holder<PlacedFeature> TREES_BADLANDS;
   public static final Holder<PlacedFeature> TREES_SNOWY;
   public static final Holder<PlacedFeature> TREES_SWAMP;
   public static final Holder<PlacedFeature> TREES_WINDSWEPT_SAVANNA;
   public static final Holder<PlacedFeature> TREES_SAVANNA;
   public static final Holder<PlacedFeature> BIRCH_TALL;
   public static final Holder<PlacedFeature> TREES_BIRCH;
   public static final Holder<PlacedFeature> TREES_WINDSWEPT_FOREST;
   public static final Holder<PlacedFeature> TREES_WINDSWEPT_HILLS;
   public static final Holder<PlacedFeature> TREES_WATER;
   public static final Holder<PlacedFeature> TREES_BIRCH_AND_OAK;
   public static final Holder<PlacedFeature> TREES_SPARSE_JUNGLE;
   public static final Holder<PlacedFeature> TREES_OLD_GROWTH_SPRUCE_TAIGA;
   public static final Holder<PlacedFeature> TREES_OLD_GROWTH_PINE_TAIGA;
   public static final Holder<PlacedFeature> TREES_JUNGLE;
   public static final Holder<PlacedFeature> BAMBOO_VEGETATION;
   public static final Holder<PlacedFeature> MUSHROOM_ISLAND_VEGETATION;
   public static final Holder<PlacedFeature> TREES_MANGROVE;

   public VegetationPlacements() {
      super();
   }

   public static List<PlacementModifier> worldSurfaceSquaredWithCount(int var0) {
      return List.of(CountPlacement.of(var0), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
   }

   private static List<PlacementModifier> getMushroomPlacement(int var0, @Nullable PlacementModifier var1) {
      ImmutableList.Builder var2 = ImmutableList.builder();
      if (var1 != null) {
         var2.add(var1);
      }

      if (var0 != 0) {
         var2.add(RarityFilter.onAverageOnceEvery(var0));
      }

      var2.add(InSquarePlacement.spread());
      var2.add(PlacementUtils.HEIGHTMAP);
      var2.add(BiomeFilter.biome());
      return var2.build();
   }

   private static ImmutableList.Builder<PlacementModifier> treePlacementBase(PlacementModifier var0) {
      return ImmutableList.builder().add(var0).add(InSquarePlacement.spread()).add(TREE_THRESHOLD).add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR).add(BiomeFilter.biome());
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier var0) {
      return treePlacementBase(var0).build();
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier var0, Block var1) {
      return treePlacementBase(var0).add(BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(var1.defaultBlockState(), BlockPos.ZERO))).build();
   }

   static {
      BAMBOO_LIGHT = PlacementUtils.register("bamboo_light", VegetationFeatures.BAMBOO_NO_PODZOL, RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      BAMBOO = PlacementUtils.register("bamboo", VegetationFeatures.BAMBOO_SOME_PODZOL, NoiseBasedCountPlacement.of(160, 80.0, 0.3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      VINES = PlacementUtils.register("vines", VegetationFeatures.VINES, CountPlacement.of(127), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)), BiomeFilter.biome());
      PATCH_SUNFLOWER = PlacementUtils.register("patch_sunflower", VegetationFeatures.PATCH_SUNFLOWER, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_PUMPKIN = PlacementUtils.register("patch_pumpkin", VegetationFeatures.PATCH_PUMPKIN, RarityFilter.onAverageOnceEvery(300), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_GRASS_PLAIN = PlacementUtils.register("patch_grass_plain", VegetationFeatures.PATCH_GRASS, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PATCH_GRASS_FOREST = PlacementUtils.register("patch_grass_forest", VegetationFeatures.PATCH_GRASS, worldSurfaceSquaredWithCount(2));
      PATCH_GRASS_BADLANDS = PlacementUtils.register("patch_grass_badlands", VegetationFeatures.PATCH_GRASS, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PATCH_GRASS_SAVANNA = PlacementUtils.register("patch_grass_savanna", VegetationFeatures.PATCH_GRASS, worldSurfaceSquaredWithCount(20));
      PATCH_GRASS_NORMAL = PlacementUtils.register("patch_grass_normal", VegetationFeatures.PATCH_GRASS, worldSurfaceSquaredWithCount(5));
      PATCH_GRASS_TAIGA_2 = PlacementUtils.register("patch_grass_taiga_2", VegetationFeatures.PATCH_TAIGA_GRASS, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PATCH_GRASS_TAIGA = PlacementUtils.register("patch_grass_taiga", VegetationFeatures.PATCH_TAIGA_GRASS, worldSurfaceSquaredWithCount(7));
      PATCH_GRASS_JUNGLE = PlacementUtils.register("patch_grass_jungle", VegetationFeatures.PATCH_GRASS_JUNGLE, worldSurfaceSquaredWithCount(25));
      GRASS_BONEMEAL = PlacementUtils.register("grass_bonemeal", VegetationFeatures.SINGLE_PIECE_OF_GRASS, PlacementUtils.isEmpty());
      PATCH_DEAD_BUSH_2 = PlacementUtils.register("patch_dead_bush_2", VegetationFeatures.PATCH_DEAD_BUSH, worldSurfaceSquaredWithCount(2));
      PATCH_DEAD_BUSH = PlacementUtils.register("patch_dead_bush", VegetationFeatures.PATCH_DEAD_BUSH, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PATCH_DEAD_BUSH_BADLANDS = PlacementUtils.register("patch_dead_bush_badlands", VegetationFeatures.PATCH_DEAD_BUSH, worldSurfaceSquaredWithCount(20));
      PATCH_MELON = PlacementUtils.register("patch_melon", VegetationFeatures.PATCH_MELON, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_MELON_SPARSE = PlacementUtils.register("patch_melon_sparse", VegetationFeatures.PATCH_MELON, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_BERRY_COMMON = PlacementUtils.register("patch_berry_common", VegetationFeatures.PATCH_BERRY_BUSH, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PATCH_BERRY_RARE = PlacementUtils.register("patch_berry_rare", VegetationFeatures.PATCH_BERRY_BUSH, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PATCH_WATERLILY = PlacementUtils.register("patch_waterlily", VegetationFeatures.PATCH_WATERLILY, worldSurfaceSquaredWithCount(4));
      PATCH_TALL_GRASS_2 = PlacementUtils.register("patch_tall_grass_2", VegetationFeatures.PATCH_TALL_GRASS, NoiseThresholdCountPlacement.of(-0.8, 0, 7), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_TALL_GRASS = PlacementUtils.register("patch_tall_grass", VegetationFeatures.PATCH_TALL_GRASS, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_LARGE_FERN = PlacementUtils.register("patch_large_fern", VegetationFeatures.PATCH_LARGE_FERN, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_CACTUS_DESERT = PlacementUtils.register("patch_cactus_desert", VegetationFeatures.PATCH_CACTUS, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_CACTUS_DECORATED = PlacementUtils.register("patch_cactus_decorated", VegetationFeatures.PATCH_CACTUS, RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_SUGAR_CANE_SWAMP = PlacementUtils.register("patch_sugar_cane_swamp", VegetationFeatures.PATCH_SUGAR_CANE, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_SUGAR_CANE_DESERT = PlacementUtils.register("patch_sugar_cane_desert", VegetationFeatures.PATCH_SUGAR_CANE, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_SUGAR_CANE_BADLANDS = PlacementUtils.register("patch_sugar_cane_badlands", VegetationFeatures.PATCH_SUGAR_CANE, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PATCH_SUGAR_CANE = PlacementUtils.register("patch_sugar_cane", VegetationFeatures.PATCH_SUGAR_CANE, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      BROWN_MUSHROOM_NETHER = PlacementUtils.register("brown_mushroom_nether", VegetationFeatures.PATCH_BROWN_MUSHROOM, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
      RED_MUSHROOM_NETHER = PlacementUtils.register("red_mushroom_nether", VegetationFeatures.PATCH_RED_MUSHROOM, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
      BROWN_MUSHROOM_NORMAL = PlacementUtils.register("brown_mushroom_normal", VegetationFeatures.PATCH_BROWN_MUSHROOM, getMushroomPlacement(256, (PlacementModifier)null));
      RED_MUSHROOM_NORMAL = PlacementUtils.register("red_mushroom_normal", VegetationFeatures.PATCH_RED_MUSHROOM, getMushroomPlacement(512, (PlacementModifier)null));
      BROWN_MUSHROOM_TAIGA = PlacementUtils.register("brown_mushroom_taiga", VegetationFeatures.PATCH_BROWN_MUSHROOM, getMushroomPlacement(4, (PlacementModifier)null));
      RED_MUSHROOM_TAIGA = PlacementUtils.register("red_mushroom_taiga", VegetationFeatures.PATCH_RED_MUSHROOM, getMushroomPlacement(256, (PlacementModifier)null));
      BROWN_MUSHROOM_OLD_GROWTH = PlacementUtils.register("brown_mushroom_old_growth", VegetationFeatures.PATCH_BROWN_MUSHROOM, getMushroomPlacement(4, CountPlacement.of(3)));
      RED_MUSHROOM_OLD_GROWTH = PlacementUtils.register("red_mushroom_old_growth", VegetationFeatures.PATCH_RED_MUSHROOM, getMushroomPlacement(171, (PlacementModifier)null));
      BROWN_MUSHROOM_SWAMP = PlacementUtils.register("brown_mushroom_swamp", VegetationFeatures.PATCH_BROWN_MUSHROOM, getMushroomPlacement(0, CountPlacement.of(2)));
      RED_MUSHROOM_SWAMP = PlacementUtils.register("red_mushroom_swamp", VegetationFeatures.PATCH_RED_MUSHROOM, getMushroomPlacement(64, (PlacementModifier)null));
      FLOWER_WARM = PlacementUtils.register("flower_warm", VegetationFeatures.FLOWER_DEFAULT, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      FLOWER_DEFAULT = PlacementUtils.register("flower_default", VegetationFeatures.FLOWER_DEFAULT, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      FLOWER_FLOWER_FOREST = PlacementUtils.register("flower_flower_forest", VegetationFeatures.FLOWER_FLOWER_FOREST, CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      FLOWER_SWAMP = PlacementUtils.register("flower_swamp", VegetationFeatures.FLOWER_SWAMP, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      FLOWER_PLAINS = PlacementUtils.register("flower_plains", VegetationFeatures.FLOWER_PLAIN, NoiseThresholdCountPlacement.of(-0.8, 15, 4), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      FLOWER_MEADOW = PlacementUtils.register("flower_meadow", VegetationFeatures.FLOWER_MEADOW, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      TREE_THRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);
      TREES_PLAINS = PlacementUtils.register("trees_plains", VegetationFeatures.TREES_PLAINS, PlacementUtils.countExtra(0, 0.05F, 1), InSquarePlacement.spread(), TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome());
      DARK_FOREST_VEGETATION = PlacementUtils.register("dark_forest_vegetation", VegetationFeatures.DARK_FOREST_VEGETATION, CountPlacement.of(16), InSquarePlacement.spread(), TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
      FLOWER_FOREST_FLOWERS = PlacementUtils.register("flower_forest_flowers", VegetationFeatures.FOREST_FLOWERS, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-1, 3), 0, 3)), BiomeFilter.biome());
      FOREST_FLOWERS = PlacementUtils.register("forest_flowers", VegetationFeatures.FOREST_FLOWERS, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)), BiomeFilter.biome());
      TREES_FLOWER_FOREST = PlacementUtils.register("trees_flower_forest", VegetationFeatures.TREES_FLOWER_FOREST, treePlacement(PlacementUtils.countExtra(6, 0.1F, 1)));
      TREES_MEADOW = PlacementUtils.register("trees_meadow", VegetationFeatures.MEADOW_TREES, treePlacement(RarityFilter.onAverageOnceEvery(100)));
      TREES_TAIGA = PlacementUtils.register("trees_taiga", VegetationFeatures.TREES_TAIGA, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      TREES_GROVE = PlacementUtils.register("trees_grove", VegetationFeatures.TREES_GROVE, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      TREES_BADLANDS = PlacementUtils.register("trees_badlands", TreeFeatures.OAK, treePlacement(PlacementUtils.countExtra(5, 0.1F, 1), Blocks.OAK_SAPLING));
      TREES_SNOWY = PlacementUtils.register("trees_snowy", TreeFeatures.SPRUCE, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1), Blocks.SPRUCE_SAPLING));
      TREES_SWAMP = PlacementUtils.register("trees_swamp", TreeFeatures.SWAMP_OAK, PlacementUtils.countExtra(2, 0.1F, 1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)));
      TREES_WINDSWEPT_SAVANNA = PlacementUtils.register("trees_windswept_savanna", VegetationFeatures.TREES_SAVANNA, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      TREES_SAVANNA = PlacementUtils.register("trees_savanna", VegetationFeatures.TREES_SAVANNA, treePlacement(PlacementUtils.countExtra(1, 0.1F, 1)));
      BIRCH_TALL = PlacementUtils.register("birch_tall", VegetationFeatures.BIRCH_TALL, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      TREES_BIRCH = PlacementUtils.register("trees_birch", TreeFeatures.BIRCH_BEES_0002, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.BIRCH_SAPLING));
      TREES_WINDSWEPT_FOREST = PlacementUtils.register("trees_windswept_forest", VegetationFeatures.TREES_WINDSWEPT_HILLS, treePlacement(PlacementUtils.countExtra(3, 0.1F, 1)));
      TREES_WINDSWEPT_HILLS = PlacementUtils.register("trees_windswept_hills", VegetationFeatures.TREES_WINDSWEPT_HILLS, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      TREES_WATER = PlacementUtils.register("trees_water", VegetationFeatures.TREES_WATER, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      TREES_BIRCH_AND_OAK = PlacementUtils.register("trees_birch_and_oak", VegetationFeatures.TREES_BIRCH_AND_OAK, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      TREES_SPARSE_JUNGLE = PlacementUtils.register("trees_sparse_jungle", VegetationFeatures.TREES_SPARSE_JUNGLE, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      TREES_OLD_GROWTH_SPRUCE_TAIGA = PlacementUtils.register("trees_old_growth_spruce_taiga", VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      TREES_OLD_GROWTH_PINE_TAIGA = PlacementUtils.register("trees_old_growth_pine_taiga", VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      TREES_JUNGLE = PlacementUtils.register("trees_jungle", VegetationFeatures.TREES_JUNGLE, treePlacement(PlacementUtils.countExtra(50, 0.1F, 1)));
      BAMBOO_VEGETATION = PlacementUtils.register("bamboo_vegetation", VegetationFeatures.BAMBOO_VEGETATION, treePlacement(PlacementUtils.countExtra(30, 0.1F, 1)));
      MUSHROOM_ISLAND_VEGETATION = PlacementUtils.register("mushroom_island_vegetation", VegetationFeatures.MUSHROOM_ISLAND_VEGETATION, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      TREES_MANGROVE = PlacementUtils.register("trees_mangrove", VegetationFeatures.MANGROVE_VEGETATION, CountPlacement.of(25), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(5), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), BlockPos.ZERO)));
   }
}
