package net.minecraft.data.worldgen.placement;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
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
import org.jspecify.annotations.Nullable;

public class VegetationPlacements {
   public static final ResourceKey<PlacedFeature> BAMBOO_LIGHT = PlacementUtils.createKey("bamboo_light");
   public static final ResourceKey<PlacedFeature> BAMBOO = PlacementUtils.createKey("bamboo");
   public static final ResourceKey<PlacedFeature> VINES = PlacementUtils.createKey("vines");
   public static final ResourceKey<PlacedFeature> PATCH_SUNFLOWER = PlacementUtils.createKey("patch_sunflower");
   public static final ResourceKey<PlacedFeature> PATCH_PUMPKIN = PlacementUtils.createKey("patch_pumpkin");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_PLAIN = PlacementUtils.createKey("patch_grass_plain");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_MEADOW = PlacementUtils.createKey("patch_grass_meadow");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_FOREST = PlacementUtils.createKey("patch_grass_forest");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_BADLANDS = PlacementUtils.createKey("patch_grass_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_SAVANNA = PlacementUtils.createKey("patch_grass_savanna");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_NORMAL = PlacementUtils.createKey("patch_grass_normal");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA_2 = PlacementUtils.createKey("patch_grass_taiga_2");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA = PlacementUtils.createKey("patch_grass_taiga");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_JUNGLE = PlacementUtils.createKey("patch_grass_jungle");
   public static final ResourceKey<PlacedFeature> GRASS_BONEMEAL = PlacementUtils.createKey("grass_bonemeal");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_2 = PlacementUtils.createKey("patch_dead_bush_2");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH = PlacementUtils.createKey("patch_dead_bush");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_BADLANDS = PlacementUtils.createKey("patch_dead_bush_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_DRY_GRASS_BADLANDS = PlacementUtils.createKey("patch_dry_grass_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_DRY_GRASS_DESERT = PlacementUtils.createKey("patch_dry_grass_desert");
   public static final ResourceKey<PlacedFeature> PATCH_MELON = PlacementUtils.createKey("patch_melon");
   public static final ResourceKey<PlacedFeature> PATCH_MELON_SPARSE = PlacementUtils.createKey("patch_melon_sparse");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_COMMON = PlacementUtils.createKey("patch_berry_common");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_RARE = PlacementUtils.createKey("patch_berry_rare");
   public static final ResourceKey<PlacedFeature> PATCH_WATERLILY = PlacementUtils.createKey("patch_waterlily");
   public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS_2 = PlacementUtils.createKey("patch_tall_grass_2");
   public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS = PlacementUtils.createKey("patch_tall_grass");
   public static final ResourceKey<PlacedFeature> PATCH_LARGE_FERN = PlacementUtils.createKey("patch_large_fern");
   public static final ResourceKey<PlacedFeature> PATCH_BUSH = PlacementUtils.createKey("patch_bush");
   public static final ResourceKey<PlacedFeature> PATCH_LEAF_LITTER = PlacementUtils.createKey("patch_leaf_litter");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DESERT = PlacementUtils.createKey("patch_cactus_desert");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DECORATED = PlacementUtils.createKey("patch_cactus_decorated");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_SWAMP = PlacementUtils.createKey("patch_sugar_cane_swamp");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_DESERT = PlacementUtils.createKey("patch_sugar_cane_desert");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_BADLANDS = PlacementUtils.createKey("patch_sugar_cane_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE = PlacementUtils.createKey("patch_sugar_cane");
   public static final ResourceKey<PlacedFeature> PATCH_FIREFLY_BUSH_SWAMP = PlacementUtils.createKey("patch_firefly_bush_swamp");
   public static final ResourceKey<PlacedFeature> PATCH_FIREFLY_BUSH_NEAR_WATER_SWAMP = PlacementUtils.createKey("patch_firefly_bush_near_water_swamp");
   public static final ResourceKey<PlacedFeature> PATCH_FIREFLY_BUSH_NEAR_WATER = PlacementUtils.createKey("patch_firefly_bush_near_water");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_NETHER = PlacementUtils.createKey("brown_mushroom_nether");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_NETHER = PlacementUtils.createKey("red_mushroom_nether");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_NORMAL = PlacementUtils.createKey("brown_mushroom_normal");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_NORMAL = PlacementUtils.createKey("red_mushroom_normal");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_TAIGA = PlacementUtils.createKey("brown_mushroom_taiga");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_TAIGA = PlacementUtils.createKey("red_mushroom_taiga");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_OLD_GROWTH = PlacementUtils.createKey("brown_mushroom_old_growth");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_OLD_GROWTH = PlacementUtils.createKey("red_mushroom_old_growth");
   public static final ResourceKey<PlacedFeature> BROWN_MUSHROOM_SWAMP = PlacementUtils.createKey("brown_mushroom_swamp");
   public static final ResourceKey<PlacedFeature> RED_MUSHROOM_SWAMP = PlacementUtils.createKey("red_mushroom_swamp");
   public static final ResourceKey<PlacedFeature> FLOWER_WARM = PlacementUtils.createKey("flower_warm");
   public static final ResourceKey<PlacedFeature> FLOWER_DEFAULT = PlacementUtils.createKey("flower_default");
   public static final ResourceKey<PlacedFeature> FLOWER_FLOWER_FOREST = PlacementUtils.createKey("flower_flower_forest");
   public static final ResourceKey<PlacedFeature> FLOWER_SWAMP = PlacementUtils.createKey("flower_swamp");
   public static final ResourceKey<PlacedFeature> FLOWER_PLAINS = PlacementUtils.createKey("flower_plains");
   public static final ResourceKey<PlacedFeature> FLOWER_MEADOW = PlacementUtils.createKey("flower_meadow");
   public static final ResourceKey<PlacedFeature> FLOWER_CHERRY = PlacementUtils.createKey("flower_cherry");
   public static final ResourceKey<PlacedFeature> FLOWER_PALE_GARDEN = PlacementUtils.createKey("flower_pale_garden");
   public static final ResourceKey<PlacedFeature> WILDFLOWERS_BIRCH_FOREST = PlacementUtils.createKey("wildflowers_birch_forest");
   public static final ResourceKey<PlacedFeature> WILDFLOWERS_MEADOW = PlacementUtils.createKey("wildflowers_meadow");
   public static final ResourceKey<PlacedFeature> TREES_PLAINS = PlacementUtils.createKey("trees_plains");
   public static final ResourceKey<PlacedFeature> DARK_FOREST_VEGETATION = PlacementUtils.createKey("dark_forest_vegetation");
   public static final ResourceKey<PlacedFeature> PALE_GARDEN_VEGETATION = PlacementUtils.createKey("pale_garden_vegetation");
   public static final ResourceKey<PlacedFeature> FLOWER_FOREST_FLOWERS = PlacementUtils.createKey("flower_forest_flowers");
   public static final ResourceKey<PlacedFeature> FOREST_FLOWERS = PlacementUtils.createKey("forest_flowers");
   public static final ResourceKey<PlacedFeature> PALE_GARDEN_FLOWERS = PlacementUtils.createKey("pale_garden_flowers");
   public static final ResourceKey<PlacedFeature> PALE_MOSS_PATCH = PlacementUtils.createKey("pale_moss_patch");
   public static final ResourceKey<PlacedFeature> TREES_FLOWER_FOREST = PlacementUtils.createKey("trees_flower_forest");
   public static final ResourceKey<PlacedFeature> TREES_MEADOW = PlacementUtils.createKey("trees_meadow");
   public static final ResourceKey<PlacedFeature> TREES_CHERRY = PlacementUtils.createKey("trees_cherry");
   public static final ResourceKey<PlacedFeature> TREES_TAIGA = PlacementUtils.createKey("trees_taiga");
   public static final ResourceKey<PlacedFeature> TREES_GROVE = PlacementUtils.createKey("trees_grove");
   public static final ResourceKey<PlacedFeature> TREES_BADLANDS = PlacementUtils.createKey("trees_badlands");
   public static final ResourceKey<PlacedFeature> TREES_SNOWY = PlacementUtils.createKey("trees_snowy");
   public static final ResourceKey<PlacedFeature> TREES_SWAMP = PlacementUtils.createKey("trees_swamp");
   public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_SAVANNA = PlacementUtils.createKey("trees_windswept_savanna");
   public static final ResourceKey<PlacedFeature> TREES_SAVANNA = PlacementUtils.createKey("trees_savanna");
   public static final ResourceKey<PlacedFeature> BIRCH_TALL = PlacementUtils.createKey("birch_tall");
   public static final ResourceKey<PlacedFeature> TREES_BIRCH = PlacementUtils.createKey("trees_birch");
   public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_FOREST = PlacementUtils.createKey("trees_windswept_forest");
   public static final ResourceKey<PlacedFeature> TREES_WINDSWEPT_HILLS = PlacementUtils.createKey("trees_windswept_hills");
   public static final ResourceKey<PlacedFeature> TREES_WATER = PlacementUtils.createKey("trees_water");
   public static final ResourceKey<PlacedFeature> TREES_BIRCH_AND_OAK_LEAF_LITTER = PlacementUtils.createKey("trees_birch_and_oak_leaf_litter");
   public static final ResourceKey<PlacedFeature> TREES_SPARSE_JUNGLE = PlacementUtils.createKey("trees_sparse_jungle");
   public static final ResourceKey<PlacedFeature> TREES_OLD_GROWTH_SPRUCE_TAIGA = PlacementUtils.createKey("trees_old_growth_spruce_taiga");
   public static final ResourceKey<PlacedFeature> TREES_OLD_GROWTH_PINE_TAIGA = PlacementUtils.createKey("trees_old_growth_pine_taiga");
   public static final ResourceKey<PlacedFeature> TREES_JUNGLE = PlacementUtils.createKey("trees_jungle");
   public static final ResourceKey<PlacedFeature> BAMBOO_VEGETATION = PlacementUtils.createKey("bamboo_vegetation");
   public static final ResourceKey<PlacedFeature> MUSHROOM_ISLAND_VEGETATION = PlacementUtils.createKey("mushroom_island_vegetation");
   public static final ResourceKey<PlacedFeature> TREES_MANGROVE = PlacementUtils.createKey("trees_mangrove");
   private static final PlacementModifier TREE_THRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);

   public VegetationPlacements() {
      super();
   }

   public static List<PlacementModifier> worldSurfaceSquaredWithCount(final int count) {
      return List.of(CountPlacement.of(count), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
   }

   private static List<PlacementModifier> getMushroomPlacement(final int rarity, final @Nullable PlacementModifier prefix) {
      ImmutableList.Builder<PlacementModifier> builder = ImmutableList.builder();
      if (prefix != null) {
         builder.add(prefix);
      }

      if (rarity != 0) {
         builder.add(RarityFilter.onAverageOnceEvery(rarity));
      }

      builder.add(InSquarePlacement.spread());
      builder.add(PlacementUtils.HEIGHTMAP);
      builder.add(BiomeFilter.biome());
      return builder.build();
   }

   private static ImmutableList.Builder<PlacementModifier> treePlacementBase(final PlacementModifier frequency) {
      return ImmutableList.builder().add(frequency).add(InSquarePlacement.spread()).add(TREE_THRESHOLD).add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR).add(BiomeFilter.biome());
   }

   public static List<PlacementModifier> treePlacement(final PlacementModifier frequency) {
      return treePlacementBase(frequency).build();
   }

   public static List<PlacementModifier> treePlacement(final PlacementModifier frequency, final Block sapling) {
      return treePlacementBase(frequency).add(BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(sapling.defaultBlockState(), BlockPos.ZERO))).build();
   }

   public static void bootstrap(final BootstrapContext<PlacedFeature> context) {
      HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.<ConfiguredFeature<?, ?>>lookup(Registries.CONFIGURED_FEATURE);
      Holder<ConfiguredFeature<?, ?>> bambooNoPodzol = configuredFeatures.getOrThrow(VegetationFeatures.BAMBOO_NO_PODZOL);
      Holder<ConfiguredFeature<?, ?>> bambooSomePodzol = configuredFeatures.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL);
      Holder<ConfiguredFeature<?, ?>> vines = configuredFeatures.getOrThrow(VegetationFeatures.VINES);
      Holder<ConfiguredFeature<?, ?>> patchSunflower = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER);
      Holder<ConfiguredFeature<?, ?>> patchPumpkin = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_PUMPKIN);
      Holder<ConfiguredFeature<?, ?>> patchGrass = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_GRASS);
      Holder<ConfiguredFeature<?, ?>> patchGrassMeadow = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_GRASS_MEADOW);
      Holder<ConfiguredFeature<?, ?>> patchLeafLitter = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_LEAF_LITTER);
      Holder<ConfiguredFeature<?, ?>> patchTaigaGrass = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_TAIGA_GRASS);
      Holder<ConfiguredFeature<?, ?>> patchGrassJungle = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_GRASS_JUNGLE);
      Holder<ConfiguredFeature<?, ?>> singlePieceOfGrass = configuredFeatures.getOrThrow(VegetationFeatures.SINGLE_PIECE_OF_GRASS);
      Holder<ConfiguredFeature<?, ?>> patchDeadBush = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH);
      Holder<ConfiguredFeature<?, ?>> patchDryGrass = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_DRY_GRASS);
      Holder<ConfiguredFeature<?, ?>> patchFireflyBush = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_FIREFLY_BUSH);
      Holder<ConfiguredFeature<?, ?>> patchMelon = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_MELON);
      Holder<ConfiguredFeature<?, ?>> patchBerryBush = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_BERRY_BUSH);
      Holder<ConfiguredFeature<?, ?>> patchWaterlily = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_WATERLILY);
      Holder<ConfiguredFeature<?, ?>> patchTallGrass = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS);
      Holder<ConfiguredFeature<?, ?>> patchLargeFern = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN);
      Holder<ConfiguredFeature<?, ?>> patchBush = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_BUSH);
      Holder<ConfiguredFeature<?, ?>> patchCactus = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_CACTUS);
      Holder<ConfiguredFeature<?, ?>> patchSugarCane = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE);
      Holder<ConfiguredFeature<?, ?>> patchBrownMushroom = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM);
      Holder<ConfiguredFeature<?, ?>> patchRedMushroom = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM);
      Holder<ConfiguredFeature<?, ?>> flowerDefault = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_DEFAULT);
      Holder<ConfiguredFeature<?, ?>> flowerFlowerForest = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_FLOWER_FOREST);
      Holder<ConfiguredFeature<?, ?>> flowerSwamp = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_SWAMP);
      Holder<ConfiguredFeature<?, ?>> flowerPlain = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
      Holder<ConfiguredFeature<?, ?>> flowerMeadow = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_MEADOW);
      Holder<ConfiguredFeature<?, ?>> flowerCherry = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_CHERRY);
      Holder<ConfiguredFeature<?, ?>> flowerPaleGarden = configuredFeatures.getOrThrow(VegetationFeatures.FLOWER_PALE_GARDEN);
      Holder<ConfiguredFeature<?, ?>> wildflowersBirchForest = configuredFeatures.getOrThrow(VegetationFeatures.WILDFLOWERS_BIRCH_FOREST);
      Holder<ConfiguredFeature<?, ?>> wildflowersMeadow = configuredFeatures.getOrThrow(VegetationFeatures.WILDFLOWERS_MEADOW);
      Holder<ConfiguredFeature<?, ?>> treesPlains = configuredFeatures.getOrThrow(VegetationFeatures.TREES_PLAINS);
      Holder<ConfiguredFeature<?, ?>> darkForestVegetation = configuredFeatures.getOrThrow(VegetationFeatures.DARK_FOREST_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> paleGardenVegetation = configuredFeatures.getOrThrow(VegetationFeatures.PALE_GARDEN_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> forestFlowers = configuredFeatures.getOrThrow(VegetationFeatures.FOREST_FLOWERS);
      Holder<ConfiguredFeature<?, ?>> paleForestFlowers = configuredFeatures.getOrThrow(VegetationFeatures.PALE_FOREST_FLOWERS);
      Holder<ConfiguredFeature<?, ?>> paleMossPatch = configuredFeatures.getOrThrow(VegetationFeatures.PALE_MOSS_PATCH);
      Holder<ConfiguredFeature<?, ?>> treesFlowerForest = configuredFeatures.getOrThrow(VegetationFeatures.TREES_FLOWER_FOREST);
      Holder<ConfiguredFeature<?, ?>> meadowTrees = configuredFeatures.getOrThrow(VegetationFeatures.MEADOW_TREES);
      Holder<ConfiguredFeature<?, ?>> treesTaiga = configuredFeatures.getOrThrow(VegetationFeatures.TREES_TAIGA);
      Holder<ConfiguredFeature<?, ?>> treesBadlands = configuredFeatures.getOrThrow(VegetationFeatures.TREES_BADLANDS);
      Holder<ConfiguredFeature<?, ?>> treesGrove = configuredFeatures.getOrThrow(VegetationFeatures.TREES_GROVE);
      Holder<ConfiguredFeature<?, ?>> treesSnowy = configuredFeatures.getOrThrow(VegetationFeatures.TREES_SNOWY);
      Holder<ConfiguredFeature<?, ?>> cherryBees005 = configuredFeatures.getOrThrow(TreeFeatures.CHERRY_BEES_005);
      Holder<ConfiguredFeature<?, ?>> swampOak = configuredFeatures.getOrThrow(TreeFeatures.SWAMP_OAK);
      Holder<ConfiguredFeature<?, ?>> treesSavanna = configuredFeatures.getOrThrow(VegetationFeatures.TREES_SAVANNA);
      Holder<ConfiguredFeature<?, ?>> birchTall = configuredFeatures.getOrThrow(VegetationFeatures.BIRCH_TALL);
      Holder<ConfiguredFeature<?, ?>> treesBirch = configuredFeatures.getOrThrow(VegetationFeatures.TREES_BIRCH);
      Holder<ConfiguredFeature<?, ?>> treesWindsweptHills = configuredFeatures.getOrThrow(VegetationFeatures.TREES_WINDSWEPT_HILLS);
      Holder<ConfiguredFeature<?, ?>> treesWater = configuredFeatures.getOrThrow(VegetationFeatures.TREES_WATER);
      Holder<ConfiguredFeature<?, ?>> treesBirchAndOakLeafLitter = configuredFeatures.getOrThrow(VegetationFeatures.TREES_BIRCH_AND_OAK_LEAF_LITTER);
      Holder<ConfiguredFeature<?, ?>> treesSparseJungle = configuredFeatures.getOrThrow(VegetationFeatures.TREES_SPARSE_JUNGLE);
      Holder<ConfiguredFeature<?, ?>> treesOldGrowthSpruceTaiga = configuredFeatures.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA);
      Holder<ConfiguredFeature<?, ?>> treesOldGrowthPineTaiga = configuredFeatures.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
      Holder<ConfiguredFeature<?, ?>> treesJungle = configuredFeatures.getOrThrow(VegetationFeatures.TREES_JUNGLE);
      Holder<ConfiguredFeature<?, ?>> bambooVegetation = configuredFeatures.getOrThrow(VegetationFeatures.BAMBOO_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> mushroomIslandVegetation = configuredFeatures.getOrThrow(VegetationFeatures.MUSHROOM_ISLAND_VEGETATION);
      Holder<ConfiguredFeature<?, ?>> mangroveVegetation = configuredFeatures.getOrThrow(VegetationFeatures.MANGROVE_VEGETATION);
      PlacementUtils.register(context, BAMBOO_LIGHT, bambooNoPodzol, RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, BAMBOO, bambooSomePodzol, NoiseBasedCountPlacement.of(160, 80.0, 0.3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, VINES, vines, CountPlacement.of(127), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)), BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_SUNFLOWER, patchSunflower, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_PUMPKIN, patchPumpkin, RarityFilter.onAverageOnceEvery(300), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_GRASS_PLAIN, patchGrass, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_GRASS_MEADOW, patchGrassMeadow, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_GRASS_FOREST, patchGrass, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(context, PATCH_LEAF_LITTER, patchLeafLitter, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(context, PATCH_GRASS_BADLANDS, patchGrass, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_GRASS_SAVANNA, patchGrass, worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(context, PATCH_GRASS_NORMAL, patchGrass, worldSurfaceSquaredWithCount(5));
      PlacementUtils.register(context, PATCH_GRASS_TAIGA_2, patchTaigaGrass, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_GRASS_TAIGA, patchTaigaGrass, worldSurfaceSquaredWithCount(7));
      PlacementUtils.register(context, PATCH_GRASS_JUNGLE, patchGrassJungle, worldSurfaceSquaredWithCount(25));
      PlacementUtils.register(context, GRASS_BONEMEAL, singlePieceOfGrass, PlacementUtils.isEmpty());
      PlacementUtils.register(context, PATCH_DEAD_BUSH_2, patchDeadBush, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(context, PATCH_DEAD_BUSH, patchDeadBush, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_DEAD_BUSH_BADLANDS, patchDeadBush, worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(context, PATCH_DRY_GRASS_BADLANDS, patchDryGrass, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_DRY_GRASS_DESERT, patchDryGrass, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_MELON, patchMelon, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_MELON_SPARSE, patchMelon, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_BERRY_COMMON, patchBerryBush, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_BERRY_RARE, patchBerryBush, RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_WATERLILY, patchWaterlily, worldSurfaceSquaredWithCount(4));
      PlacementUtils.register(context, PATCH_TALL_GRASS_2, patchTallGrass, NoiseThresholdCountPlacement.of(-0.8, 0, 7), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_TALL_GRASS, patchTallGrass, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_LARGE_FERN, patchLargeFern, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_BUSH, patchBush, RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_CACTUS_DESERT, patchCactus, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_CACTUS_DECORATED, patchCactus, RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_SUGAR_CANE_SWAMP, patchSugarCane, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_SUGAR_CANE_DESERT, patchSugarCane, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_SUGAR_CANE_BADLANDS, patchSugarCane, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_SUGAR_CANE, patchSugarCane, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, PATCH_FIREFLY_BUSH_NEAR_WATER, patchFireflyBush, CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_NO_LEAVES, BiomeFilter.biome(), VegetationFeatures.nearWaterPredicate(Blocks.FIREFLY_BUSH));
      PlacementUtils.register(context, PATCH_FIREFLY_BUSH_NEAR_WATER_SWAMP, patchFireflyBush, CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome(), VegetationFeatures.nearWaterPredicate(Blocks.FIREFLY_BUSH));
      PlacementUtils.register(context, PATCH_FIREFLY_BUSH_SWAMP, patchFireflyBush, RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, BROWN_MUSHROOM_NETHER, patchBrownMushroom, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
      PlacementUtils.register(context, RED_MUSHROOM_NETHER, patchRedMushroom, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome());
      PlacementUtils.register(context, BROWN_MUSHROOM_NORMAL, patchBrownMushroom, getMushroomPlacement(256, (PlacementModifier)null));
      PlacementUtils.register(context, RED_MUSHROOM_NORMAL, patchRedMushroom, getMushroomPlacement(512, (PlacementModifier)null));
      PlacementUtils.register(context, BROWN_MUSHROOM_TAIGA, patchBrownMushroom, getMushroomPlacement(4, (PlacementModifier)null));
      PlacementUtils.register(context, RED_MUSHROOM_TAIGA, patchRedMushroom, getMushroomPlacement(256, (PlacementModifier)null));
      PlacementUtils.register(context, BROWN_MUSHROOM_OLD_GROWTH, patchBrownMushroom, getMushroomPlacement(4, CountPlacement.of(3)));
      PlacementUtils.register(context, RED_MUSHROOM_OLD_GROWTH, patchRedMushroom, getMushroomPlacement(171, (PlacementModifier)null));
      PlacementUtils.register(context, BROWN_MUSHROOM_SWAMP, patchBrownMushroom, getMushroomPlacement(0, CountPlacement.of(2)));
      PlacementUtils.register(context, RED_MUSHROOM_SWAMP, patchRedMushroom, getMushroomPlacement(64, (PlacementModifier)null));
      PlacementUtils.register(context, FLOWER_WARM, flowerDefault, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_DEFAULT, flowerDefault, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_FLOWER_FOREST, flowerFlowerForest, CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_SWAMP, flowerSwamp, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_PLAINS, flowerPlain, NoiseThresholdCountPlacement.of(-0.8, 15, 4), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_CHERRY, flowerCherry, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_MEADOW, flowerMeadow, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_PALE_GARDEN, flowerPaleGarden, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, WILDFLOWERS_BIRCH_FOREST, wildflowersBirchForest, CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, WILDFLOWERS_MEADOW, wildflowersMeadow, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementModifier treeThreshold = SurfaceWaterDepthFilter.forMaxDepth(0);
      PlacementUtils.register(context, TREES_PLAINS, treesPlains, PlacementUtils.countExtra(0, 0.05F, 1), InSquarePlacement.spread(), treeThreshold, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome());
      PlacementUtils.register(context, DARK_FOREST_VEGETATION, darkForestVegetation, CountPlacement.of(16), InSquarePlacement.spread(), treeThreshold, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
      PlacementUtils.register(context, PALE_GARDEN_VEGETATION, paleGardenVegetation, CountPlacement.of(16), InSquarePlacement.spread(), treeThreshold, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
      PlacementUtils.register(context, FLOWER_FOREST_FLOWERS, forestFlowers, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-1, 3), 0, 3)), BiomeFilter.biome());
      PlacementUtils.register(context, FOREST_FLOWERS, forestFlowers, RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)), BiomeFilter.biome());
      PlacementUtils.register(context, PALE_GARDEN_FLOWERS, paleForestFlowers, RarityFilter.onAverageOnceEvery(8), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_NO_LEAVES, BiomeFilter.biome());
      PlacementUtils.register(context, PALE_MOSS_PATCH, paleMossPatch, CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_NO_LEAVES, BiomeFilter.biome());
      PlacementUtils.register(context, TREES_FLOWER_FOREST, treesFlowerForest, treePlacement(PlacementUtils.countExtra(6, 0.1F, 1)));
      PlacementUtils.register(context, TREES_MEADOW, meadowTrees, treePlacement(RarityFilter.onAverageOnceEvery(100)));
      PlacementUtils.register(context, TREES_CHERRY, cherryBees005, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.CHERRY_SAPLING));
      PlacementUtils.register(context, TREES_TAIGA, treesTaiga, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(context, TREES_GROVE, treesGrove, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(context, TREES_BADLANDS, treesBadlands, treePlacement(PlacementUtils.countExtra(5, 0.1F, 1), Blocks.OAK_SAPLING));
      PlacementUtils.register(context, TREES_SNOWY, treesSnowy, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1), Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(context, TREES_SWAMP, swampOak, PlacementUtils.countExtra(2, 0.1F, 1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)));
      PlacementUtils.register(context, TREES_WINDSWEPT_SAVANNA, treesSavanna, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(context, TREES_SAVANNA, treesSavanna, treePlacement(PlacementUtils.countExtra(1, 0.1F, 1)));
      PlacementUtils.register(context, BIRCH_TALL, birchTall, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(context, TREES_BIRCH, treesBirch, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.BIRCH_SAPLING));
      PlacementUtils.register(context, TREES_WINDSWEPT_FOREST, treesWindsweptHills, treePlacement(PlacementUtils.countExtra(3, 0.1F, 1)));
      PlacementUtils.register(context, TREES_WINDSWEPT_HILLS, treesWindsweptHills, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(context, TREES_WATER, treesWater, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(context, TREES_BIRCH_AND_OAK_LEAF_LITTER, treesBirchAndOakLeafLitter, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(context, TREES_SPARSE_JUNGLE, treesSparseJungle, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(context, TREES_OLD_GROWTH_SPRUCE_TAIGA, treesOldGrowthSpruceTaiga, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(context, TREES_OLD_GROWTH_PINE_TAIGA, treesOldGrowthPineTaiga, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(context, TREES_JUNGLE, treesJungle, treePlacement(PlacementUtils.countExtra(50, 0.1F, 1)));
      PlacementUtils.register(context, BAMBOO_VEGETATION, bambooVegetation, treePlacement(PlacementUtils.countExtra(30, 0.1F, 1)));
      PlacementUtils.register(context, MUSHROOM_ISLAND_VEGETATION, mushroomIslandVegetation, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(context, TREES_MANGROVE, mangroveVegetation, CountPlacement.of(25), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(5), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
   }
}
