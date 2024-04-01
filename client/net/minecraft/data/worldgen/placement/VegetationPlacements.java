package net.minecraft.data.worldgen.placement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement;
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
   public static final ResourceKey<PlacedFeature> BAMBOO_LIGHT = PlacementUtils.createKey("bamboo_light");
   public static final ResourceKey<PlacedFeature> BAMBOO = PlacementUtils.createKey("bamboo");
   public static final ResourceKey<PlacedFeature> VINES = PlacementUtils.createKey("vines");
   public static final ResourceKey<PlacedFeature> PATCH_SUNFLOWER = PlacementUtils.createKey("patch_sunflower");
   public static final ResourceKey<PlacedFeature> PATCH_PUMPKIN = PlacementUtils.createKey("patch_pumpkin");
   public static final ResourceKey<PlacedFeature> PATCH_POTATO = PlacementUtils.createKey("patch_potato");
   public static final ResourceKey<PlacedFeature> PATCH_POTATO_SPARSE = PlacementUtils.createKey("patch_potato_sparse");
   public static final ResourceKey<PlacedFeature> POTATO_FIELD = PlacementUtils.createKey("potato_field");
   public static final ResourceKey<PlacedFeature> PARK_LANE = PlacementUtils.createKey("park_lane");
   public static final ResourceKey<PlacedFeature> PARK_LANE_SURFACE = PlacementUtils.createKey("park_lane_surface");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_PLAIN = PlacementUtils.createKey("patch_grass_plain");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_FOREST = PlacementUtils.createKey("patch_grass_forest");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_BADLANDS = PlacementUtils.createKey("patch_grass_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_SAVANNA = PlacementUtils.createKey("patch_grass_savanna");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_NORMAL = PlacementUtils.createKey("patch_grass_normal");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA_2 = PlacementUtils.createKey("patch_grass_taiga_2");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_TAIGA = PlacementUtils.createKey("patch_grass_taiga");
   public static final ResourceKey<PlacedFeature> PATCH_GRASS_JUNGLE = PlacementUtils.createKey("patch_grass_jungle");
   public static final ResourceKey<PlacedFeature> GRASS_BONEMEAL = PlacementUtils.createKey("grass_bonemeal");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_2 = PlacementUtils.createKey("patch_dead_bush_2");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_2_ALL_LEVELS = PlacementUtils.createKey("patch_dead_bush_2_all_levels");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH = PlacementUtils.createKey("patch_dead_bush");
   public static final ResourceKey<PlacedFeature> PATCH_DEAD_BUSH_BADLANDS = PlacementUtils.createKey("patch_dead_bush_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_MELON = PlacementUtils.createKey("patch_melon");
   public static final ResourceKey<PlacedFeature> PATCH_MELON_SPARSE = PlacementUtils.createKey("patch_melon_sparse");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_COMMON = PlacementUtils.createKey("patch_berry_common");
   public static final ResourceKey<PlacedFeature> PATCH_BERRY_RARE = PlacementUtils.createKey("patch_berry_rare");
   public static final ResourceKey<PlacedFeature> PATCH_WATERLILY = PlacementUtils.createKey("patch_waterlily");
   public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS_2 = PlacementUtils.createKey("patch_tall_grass_2");
   public static final ResourceKey<PlacedFeature> PATCH_TALL_GRASS = PlacementUtils.createKey("patch_tall_grass");
   public static final ResourceKey<PlacedFeature> PATCH_LARGE_FERN = PlacementUtils.createKey("patch_large_fern");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DESERT = PlacementUtils.createKey("patch_cactus_desert");
   public static final ResourceKey<PlacedFeature> LEAF_PILE_HASH = PlacementUtils.createKey("leaf_pile_hash");
   public static final ResourceKey<PlacedFeature> VENOMOUS_COLUMN_HASH = PlacementUtils.createKey("venomous_column_hash");
   public static final ResourceKey<PlacedFeature> PATCH_CACTUS_DECORATED = PlacementUtils.createKey("patch_cactus_decorated");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_SWAMP = PlacementUtils.createKey("patch_sugar_cane_swamp");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_DESERT = PlacementUtils.createKey("patch_sugar_cane_desert");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE_BADLANDS = PlacementUtils.createKey("patch_sugar_cane_badlands");
   public static final ResourceKey<PlacedFeature> PATCH_SUGAR_CANE = PlacementUtils.createKey("patch_sugar_cane");
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
   public static final ResourceKey<PlacedFeature> TREES_PLAINS = PlacementUtils.createKey("trees_plains");
   public static final ResourceKey<PlacedFeature> DARK_FOREST_VEGETATION = PlacementUtils.createKey("dark_forest_vegetation");
   public static final ResourceKey<PlacedFeature> ARBORETUM_TREES = PlacementUtils.createKey("arboretum_trees");
   public static final ResourceKey<PlacedFeature> FLOWER_FOREST_FLOWERS = PlacementUtils.createKey("flower_forest_flowers");
   public static final ResourceKey<PlacedFeature> FOREST_FLOWERS = PlacementUtils.createKey("forest_flowers");
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
   public static final ResourceKey<PlacedFeature> TREES_BIRCH_AND_OAK = PlacementUtils.createKey("trees_birch_and_oak");
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

   public static List<PlacementModifier> worldSurfaceSquaredWithCount(int var0) {
      return List.of(CountPlacement.of(var0), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
   }

   private static List<PlacementModifier> getMushroomPlacement(int var0, @Nullable PlacementModifier var1) {
      Builder var2 = ImmutableList.builder();
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

   private static Builder<PlacementModifier> treePlacementBase(PlacementModifier var0) {
      return ImmutableList.builder()
         .add(var0)
         .add(InSquarePlacement.spread())
         .add(TREE_THRESHOLD)
         .add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR)
         .add(BiomeFilter.biome());
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier var0) {
      return treePlacementBase(var0).build();
   }

   public static List<PlacementModifier> treePlacement(PlacementModifier var0, Block var1) {
      return treePlacementBase(var0).add(BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(var1.defaultBlockState(), BlockPos.ZERO))).build();
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VegetationFeatures.BAMBOO_NO_PODZOL);
      Holder.Reference var3 = var1.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL);
      Holder.Reference var4 = var1.getOrThrow(VegetationFeatures.VINES);
      Holder.Reference var5 = var1.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER);
      Holder.Reference var6 = var1.getOrThrow(VegetationFeatures.PATCH_PUMPKIN);
      Holder.Reference var7 = var1.getOrThrow(VegetationFeatures.PATCH_POTATO);
      Holder.Reference var8 = var1.getOrThrow(VegetationFeatures.POTATO_FIELD);
      Holder.Reference var9 = var1.getOrThrow(VegetationFeatures.PARK_LANE);
      Holder.Reference var10 = var1.getOrThrow(VegetationFeatures.PARK_LANE_SURFACE);
      Holder.Reference var11 = var1.getOrThrow(VegetationFeatures.PATCH_GRASS);
      Holder.Reference var12 = var1.getOrThrow(VegetationFeatures.PATCH_TAIGA_GRASS);
      Holder.Reference var13 = var1.getOrThrow(VegetationFeatures.PATCH_GRASS_JUNGLE);
      Holder.Reference var14 = var1.getOrThrow(VegetationFeatures.SINGLE_PIECE_OF_GRASS);
      Holder.Reference var15 = var1.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH);
      Holder.Reference var16 = var1.getOrThrow(VegetationFeatures.PATCH_MELON);
      Holder.Reference var17 = var1.getOrThrow(VegetationFeatures.PATCH_BERRY_BUSH);
      Holder.Reference var18 = var1.getOrThrow(VegetationFeatures.PATCH_WATERLILY);
      Holder.Reference var19 = var1.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS);
      Holder.Reference var20 = var1.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN);
      Holder.Reference var21 = var1.getOrThrow(VegetationFeatures.PATCH_CACTUS);
      Holder.Reference var22 = var1.getOrThrow(VegetationFeatures.LEAF_PILE);
      Holder.Reference var23 = var1.getOrThrow(VegetationFeatures.VENOMOUS_COLUMN);
      Holder.Reference var24 = var1.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE);
      Holder.Reference var25 = var1.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM);
      Holder.Reference var26 = var1.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM);
      Holder.Reference var27 = var1.getOrThrow(VegetationFeatures.FLOWER_DEFAULT);
      Holder.Reference var28 = var1.getOrThrow(VegetationFeatures.FLOWER_FLOWER_FOREST);
      Holder.Reference var29 = var1.getOrThrow(VegetationFeatures.FLOWER_SWAMP);
      Holder.Reference var30 = var1.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
      Holder.Reference var31 = var1.getOrThrow(VegetationFeatures.FLOWER_MEADOW);
      Holder.Reference var32 = var1.getOrThrow(VegetationFeatures.FLOWER_CHERRY);
      Holder.Reference var33 = var1.getOrThrow(VegetationFeatures.TREES_PLAINS);
      Holder.Reference var34 = var1.getOrThrow(VegetationFeatures.DARK_FOREST_VEGETATION);
      Holder.Reference var35 = var1.getOrThrow(VegetationFeatures.ARBORETUM_TREES);
      Holder.Reference var36 = var1.getOrThrow(VegetationFeatures.FOREST_FLOWERS);
      Holder.Reference var37 = var1.getOrThrow(VegetationFeatures.TREES_FLOWER_FOREST);
      Holder.Reference var38 = var1.getOrThrow(VegetationFeatures.MEADOW_TREES);
      Holder.Reference var39 = var1.getOrThrow(VegetationFeatures.TREES_TAIGA);
      Holder.Reference var40 = var1.getOrThrow(VegetationFeatures.TREES_GROVE);
      Holder.Reference var41 = var1.getOrThrow(TreeFeatures.OAK);
      Holder.Reference var42 = var1.getOrThrow(TreeFeatures.SPRUCE);
      Holder.Reference var43 = var1.getOrThrow(TreeFeatures.CHERRY_BEES_005);
      Holder.Reference var44 = var1.getOrThrow(TreeFeatures.SWAMP_OAK);
      Holder.Reference var45 = var1.getOrThrow(VegetationFeatures.TREES_SAVANNA);
      Holder.Reference var46 = var1.getOrThrow(VegetationFeatures.BIRCH_TALL);
      Holder.Reference var47 = var1.getOrThrow(TreeFeatures.BIRCH_BEES_0002);
      Holder.Reference var48 = var1.getOrThrow(VegetationFeatures.TREES_WINDSWEPT_HILLS);
      Holder.Reference var49 = var1.getOrThrow(VegetationFeatures.TREES_WATER);
      Holder.Reference var50 = var1.getOrThrow(VegetationFeatures.TREES_BIRCH_AND_OAK);
      Holder.Reference var51 = var1.getOrThrow(VegetationFeatures.TREES_SPARSE_JUNGLE);
      Holder.Reference var52 = var1.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA);
      Holder.Reference var53 = var1.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
      Holder.Reference var54 = var1.getOrThrow(VegetationFeatures.TREES_JUNGLE);
      Holder.Reference var55 = var1.getOrThrow(VegetationFeatures.BAMBOO_VEGETATION);
      Holder.Reference var56 = var1.getOrThrow(VegetationFeatures.MUSHROOM_ISLAND_VEGETATION);
      Holder.Reference var57 = var1.getOrThrow(VegetationFeatures.MANGROVE_VEGETATION);
      PlacementUtils.register(
         var0, BAMBOO_LIGHT, var2, RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         BAMBOO,
         var3,
         NoiseBasedCountPlacement.of(160, 80.0, 0.3),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         VINES,
         var4,
         CountPlacement.of(127),
         InSquarePlacement.spread(),
         HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)),
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_SUNFLOWER, var5, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_PUMPKIN, var6, RarityFilter.onAverageOnceEvery(300), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(var0, PATCH_POTATO, var7, CountOnEveryLayerPlacement.of(1, 2), BiomeFilter.biome());
      PlacementUtils.register(var0, PATCH_POTATO_SPARSE, var7, RarityFilter.onAverageOnceEvery(4), CountOnEveryLayerPlacement.of(1, 2), BiomeFilter.biome());
      PlacementUtils.register(var0, POTATO_FIELD, var8, CountOnEveryLayerPlacement.of(8, 2), BiomeFilter.biome());
      PlacementUtils.register(var0, PARK_LANE, var9, CountOnEveryLayerPlacement.of(2, 2), BiomeFilter.biome());
      PlacementUtils.register(var0, PARK_LANE_SURFACE, var10, BiomeFilter.biome());
      PlacementUtils.register(
         var0,
         PATCH_GRASS_PLAIN,
         var11,
         NoiseThresholdCountPlacement.of(-0.8, 5, 10),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
         BiomeFilter.biome()
      );
      PlacementUtils.register(var0, PATCH_GRASS_FOREST, var11, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(var0, PATCH_GRASS_BADLANDS, var11, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(var0, PATCH_GRASS_SAVANNA, var11, worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(var0, PATCH_GRASS_NORMAL, var11, worldSurfaceSquaredWithCount(5));
      PlacementUtils.register(var0, PATCH_GRASS_TAIGA_2, var12, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(var0, PATCH_GRASS_TAIGA, var12, worldSurfaceSquaredWithCount(7));
      PlacementUtils.register(var0, PATCH_GRASS_JUNGLE, var13, worldSurfaceSquaredWithCount(25));
      PlacementUtils.register(var0, GRASS_BONEMEAL, var14, PlacementUtils.isEmpty());
      PlacementUtils.register(var0, PATCH_DEAD_BUSH_2, var15, worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(var0, PATCH_DEAD_BUSH_2_ALL_LEVELS, var15, CountOnEveryLayerPlacement.of(2, 2), BiomeFilter.biome());
      PlacementUtils.register(var0, PATCH_DEAD_BUSH, var15, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      PlacementUtils.register(var0, PATCH_DEAD_BUSH_BADLANDS, var15, worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(
         var0, PATCH_MELON, var16, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_MELON_SPARSE, var16, RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         PATCH_BERRY_COMMON,
         var17,
         RarityFilter.onAverageOnceEvery(32),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         PATCH_BERRY_RARE,
         var17,
         RarityFilter.onAverageOnceEvery(384),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
         BiomeFilter.biome()
      );
      PlacementUtils.register(var0, PATCH_WATERLILY, var18, worldSurfaceSquaredWithCount(4));
      PlacementUtils.register(
         var0,
         PATCH_TALL_GRASS_2,
         var19,
         NoiseThresholdCountPlacement.of(-0.8, 0, 7),
         RarityFilter.onAverageOnceEvery(32),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP,
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_TALL_GRASS, var19, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_LARGE_FERN, var20, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_CACTUS_DESERT, var21, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(var0, LEAF_PILE_HASH, var22, CountOnEveryLayerPlacement.of(1, 2), RarityFilter.onAverageOnceEvery(4), BiomeFilter.biome());
      PlacementUtils.register(var0, VENOMOUS_COLUMN_HASH, var23, CountOnEveryLayerPlacement.of(1, 2), RarityFilter.onAverageOnceEvery(16), BiomeFilter.biome());
      PlacementUtils.register(
         var0, PATCH_CACTUS_DECORATED, var21, RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_SUGAR_CANE_SWAMP, var24, RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(var0, PATCH_SUGAR_CANE_DESERT, var24, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(
         var0, PATCH_SUGAR_CANE_BADLANDS, var24, RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, PATCH_SUGAR_CANE, var24, RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, BROWN_MUSHROOM_NETHER, var25, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, RED_MUSHROOM_NETHER, var26, RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()
      );
      PlacementUtils.register(var0, BROWN_MUSHROOM_NORMAL, var25, getMushroomPlacement(256, null));
      PlacementUtils.register(var0, RED_MUSHROOM_NORMAL, var26, getMushroomPlacement(512, null));
      PlacementUtils.register(var0, BROWN_MUSHROOM_TAIGA, var25, getMushroomPlacement(4, null));
      PlacementUtils.register(var0, RED_MUSHROOM_TAIGA, var26, getMushroomPlacement(256, null));
      PlacementUtils.register(var0, BROWN_MUSHROOM_OLD_GROWTH, var25, getMushroomPlacement(4, CountPlacement.of(3)));
      PlacementUtils.register(var0, RED_MUSHROOM_OLD_GROWTH, var26, getMushroomPlacement(171, null));
      PlacementUtils.register(var0, BROWN_MUSHROOM_SWAMP, var25, getMushroomPlacement(0, CountPlacement.of(2)));
      PlacementUtils.register(var0, RED_MUSHROOM_SWAMP, var26, getMushroomPlacement(64, null));
      PlacementUtils.register(
         var0, FLOWER_WARM, var27, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, FLOWER_DEFAULT, var27, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         FLOWER_FLOWER_FOREST,
         var28,
         CountPlacement.of(3),
         RarityFilter.onAverageOnceEvery(2),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP,
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, FLOWER_SWAMP, var29, RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         FLOWER_PLAINS,
         var30,
         NoiseThresholdCountPlacement.of(-0.8, 15, 4),
         RarityFilter.onAverageOnceEvery(32),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP,
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, FLOWER_CHERRY, var32, NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()
      );
      PlacementUtils.register(var0, FLOWER_MEADOW, var31, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      SurfaceWaterDepthFilter var58 = SurfaceWaterDepthFilter.forMaxDepth(0);
      PlacementUtils.register(
         var0,
         TREES_PLAINS,
         var33,
         PlacementUtils.countExtra(0, 0.05F, 1),
         InSquarePlacement.spread(),
         var58,
         PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
         BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)),
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         DARK_FOREST_VEGETATION,
         var34,
         CountPlacement.of(16),
         InSquarePlacement.spread(),
         var58,
         PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
         BiomeFilter.biome()
      );
      PlacementUtils.register(var0, ARBORETUM_TREES, var35, CountOnEveryLayerPlacement.of(12, 2), var58, BiomeFilter.biome());
      PlacementUtils.register(
         var0,
         FLOWER_FOREST_FLOWERS,
         var36,
         RarityFilter.onAverageOnceEvery(7),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP,
         CountPlacement.of(ClampedInt.of(UniformInt.of(-1, 3), 0, 3)),
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         FOREST_FLOWERS,
         var36,
         RarityFilter.onAverageOnceEvery(7),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP,
         CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)),
         BiomeFilter.biome()
      );
      PlacementUtils.register(var0, TREES_FLOWER_FOREST, var37, treePlacement(PlacementUtils.countExtra(6, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_MEADOW, var38, treePlacement(RarityFilter.onAverageOnceEvery(100)));
      PlacementUtils.register(var0, TREES_CHERRY, var43, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.CHERRY_SAPLING));
      PlacementUtils.register(var0, TREES_TAIGA, var39, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_GROVE, var40, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_BADLANDS, var41, treePlacement(PlacementUtils.countExtra(5, 0.1F, 1), Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, TREES_SNOWY, var42, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1), Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(
         var0,
         TREES_SWAMP,
         var44,
         PlacementUtils.countExtra(2, 0.1F, 1),
         InSquarePlacement.spread(),
         SurfaceWaterDepthFilter.forMaxDepth(2),
         PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
         BiomeFilter.biome(),
         BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO))
      );
      PlacementUtils.register(var0, TREES_WINDSWEPT_SAVANNA, var45, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_SAVANNA, var45, treePlacement(PlacementUtils.countExtra(1, 0.1F, 1)));
      PlacementUtils.register(var0, BIRCH_TALL, var46, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_BIRCH, var47, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, TREES_WINDSWEPT_FOREST, var48, treePlacement(PlacementUtils.countExtra(3, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_WINDSWEPT_HILLS, var48, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_WATER, var49, treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_BIRCH_AND_OAK, var50, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_SPARSE_JUNGLE, var51, treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_OLD_GROWTH_SPRUCE_TAIGA, var52, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_OLD_GROWTH_PINE_TAIGA, var53, treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_JUNGLE, var54, treePlacement(PlacementUtils.countExtra(50, 0.1F, 1)));
      PlacementUtils.register(var0, BAMBOO_VEGETATION, var55, treePlacement(PlacementUtils.countExtra(30, 0.1F, 1)));
      PlacementUtils.register(var0, MUSHROOM_ISLAND_VEGETATION, var56, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      PlacementUtils.register(
         var0,
         TREES_MANGROVE,
         var57,
         CountPlacement.of(25),
         InSquarePlacement.spread(),
         SurfaceWaterDepthFilter.forMaxDepth(5),
         PlacementUtils.HEIGHTMAP_OCEAN_FLOOR,
         BiomeFilter.biome(),
         BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), BlockPos.ZERO))
      );
   }
}
