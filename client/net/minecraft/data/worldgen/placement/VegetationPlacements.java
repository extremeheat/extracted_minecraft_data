package net.minecraft.data.worldgen.placement;

import com.google.common.collect.ImmutableList;
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
   public static final ResourceKey<PlacedFeature> PALE_GARDEN_VEGETATION = PlacementUtils.createKey("pale_garden_vegetation");
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

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(VegetationFeatures.BAMBOO_NO_PODZOL);
      Holder.Reference var3 = var1.getOrThrow(VegetationFeatures.BAMBOO_SOME_PODZOL);
      Holder.Reference var4 = var1.getOrThrow(VegetationFeatures.VINES);
      Holder.Reference var5 = var1.getOrThrow(VegetationFeatures.PATCH_SUNFLOWER);
      Holder.Reference var6 = var1.getOrThrow(VegetationFeatures.PATCH_PUMPKIN);
      Holder.Reference var7 = var1.getOrThrow(VegetationFeatures.PATCH_GRASS);
      Holder.Reference var8 = var1.getOrThrow(VegetationFeatures.PATCH_TAIGA_GRASS);
      Holder.Reference var9 = var1.getOrThrow(VegetationFeatures.PATCH_GRASS_JUNGLE);
      Holder.Reference var10 = var1.getOrThrow(VegetationFeatures.SINGLE_PIECE_OF_GRASS);
      Holder.Reference var11 = var1.getOrThrow(VegetationFeatures.PATCH_DEAD_BUSH);
      Holder.Reference var12 = var1.getOrThrow(VegetationFeatures.PATCH_MELON);
      Holder.Reference var13 = var1.getOrThrow(VegetationFeatures.PATCH_BERRY_BUSH);
      Holder.Reference var14 = var1.getOrThrow(VegetationFeatures.PATCH_WATERLILY);
      Holder.Reference var15 = var1.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS);
      Holder.Reference var16 = var1.getOrThrow(VegetationFeatures.PATCH_LARGE_FERN);
      Holder.Reference var17 = var1.getOrThrow(VegetationFeatures.PATCH_CACTUS);
      Holder.Reference var18 = var1.getOrThrow(VegetationFeatures.PATCH_SUGAR_CANE);
      Holder.Reference var19 = var1.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM);
      Holder.Reference var20 = var1.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM);
      Holder.Reference var21 = var1.getOrThrow(VegetationFeatures.FLOWER_DEFAULT);
      Holder.Reference var22 = var1.getOrThrow(VegetationFeatures.FLOWER_FLOWER_FOREST);
      Holder.Reference var23 = var1.getOrThrow(VegetationFeatures.FLOWER_SWAMP);
      Holder.Reference var24 = var1.getOrThrow(VegetationFeatures.FLOWER_PLAIN);
      Holder.Reference var25 = var1.getOrThrow(VegetationFeatures.FLOWER_MEADOW);
      Holder.Reference var26 = var1.getOrThrow(VegetationFeatures.FLOWER_CHERRY);
      Holder.Reference var27 = var1.getOrThrow(VegetationFeatures.TREES_PLAINS);
      Holder.Reference var28 = var1.getOrThrow(VegetationFeatures.DARK_FOREST_VEGETATION);
      Holder.Reference var29 = var1.getOrThrow(VegetationFeatures.PALE_GARDEN_VEGETATION);
      Holder.Reference var30 = var1.getOrThrow(VegetationFeatures.FOREST_FLOWERS);
      Holder.Reference var31 = var1.getOrThrow(VegetationFeatures.TREES_FLOWER_FOREST);
      Holder.Reference var32 = var1.getOrThrow(VegetationFeatures.MEADOW_TREES);
      Holder.Reference var33 = var1.getOrThrow(VegetationFeatures.TREES_TAIGA);
      Holder.Reference var34 = var1.getOrThrow(VegetationFeatures.TREES_GROVE);
      Holder.Reference var35 = var1.getOrThrow(TreeFeatures.OAK);
      Holder.Reference var36 = var1.getOrThrow(TreeFeatures.SPRUCE);
      Holder.Reference var37 = var1.getOrThrow(TreeFeatures.CHERRY_BEES_005);
      Holder.Reference var38 = var1.getOrThrow(TreeFeatures.SWAMP_OAK);
      Holder.Reference var39 = var1.getOrThrow(VegetationFeatures.TREES_SAVANNA);
      Holder.Reference var40 = var1.getOrThrow(VegetationFeatures.BIRCH_TALL);
      Holder.Reference var41 = var1.getOrThrow(TreeFeatures.BIRCH_BEES_0002);
      Holder.Reference var42 = var1.getOrThrow(VegetationFeatures.TREES_WINDSWEPT_HILLS);
      Holder.Reference var43 = var1.getOrThrow(VegetationFeatures.TREES_WATER);
      Holder.Reference var44 = var1.getOrThrow(VegetationFeatures.TREES_BIRCH_AND_OAK);
      Holder.Reference var45 = var1.getOrThrow(VegetationFeatures.TREES_SPARSE_JUNGLE);
      Holder.Reference var46 = var1.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA);
      Holder.Reference var47 = var1.getOrThrow(VegetationFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
      Holder.Reference var48 = var1.getOrThrow(VegetationFeatures.TREES_JUNGLE);
      Holder.Reference var49 = var1.getOrThrow(VegetationFeatures.BAMBOO_VEGETATION);
      Holder.Reference var50 = var1.getOrThrow(VegetationFeatures.MUSHROOM_ISLAND_VEGETATION);
      Holder.Reference var51 = var1.getOrThrow(VegetationFeatures.MANGROVE_VEGETATION);
      PlacementUtils.register(var0, BAMBOO_LIGHT, var2, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(4), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, BAMBOO, var3, (PlacementModifier[])(NoiseBasedCountPlacement.of(160, 80.0, 0.3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, VINES, var4, (PlacementModifier[])(CountPlacement.of(127), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(100)), BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_SUNFLOWER, var5, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_PUMPKIN, var6, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(300), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_GRASS_PLAIN, var7, (PlacementModifier[])(NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_GRASS_FOREST, var7, (List)worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(var0, PATCH_GRASS_BADLANDS, var7, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_GRASS_SAVANNA, var7, (List)worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(var0, PATCH_GRASS_NORMAL, var7, (List)worldSurfaceSquaredWithCount(5));
      PlacementUtils.register(var0, PATCH_GRASS_TAIGA_2, var8, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_GRASS_TAIGA, var8, (List)worldSurfaceSquaredWithCount(7));
      PlacementUtils.register(var0, PATCH_GRASS_JUNGLE, var9, (List)worldSurfaceSquaredWithCount(25));
      PlacementUtils.register(var0, GRASS_BONEMEAL, var10, (PlacementModifier[])(PlacementUtils.isEmpty()));
      PlacementUtils.register(var0, PATCH_DEAD_BUSH_2, var11, (List)worldSurfaceSquaredWithCount(2));
      PlacementUtils.register(var0, PATCH_DEAD_BUSH, var11, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_DEAD_BUSH_BADLANDS, var11, (List)worldSurfaceSquaredWithCount(20));
      PlacementUtils.register(var0, PATCH_MELON, var12, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_MELON_SPARSE, var12, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_BERRY_COMMON, var13, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_BERRY_RARE, var13, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(384), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_WATERLILY, var14, (List)worldSurfaceSquaredWithCount(4));
      PlacementUtils.register(var0, PATCH_TALL_GRASS_2, var15, (PlacementModifier[])(NoiseThresholdCountPlacement.of(-0.8, 0, 7), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_TALL_GRASS, var15, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_LARGE_FERN, var16, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_CACTUS_DESERT, var17, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_CACTUS_DECORATED, var17, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(13), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_SUGAR_CANE_SWAMP, var18, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_SUGAR_CANE_DESERT, var18, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_SUGAR_CANE_BADLANDS, var18, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, PATCH_SUGAR_CANE, var18, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(6), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, BROWN_MUSHROOM_NETHER, var19, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, RED_MUSHROOM_NETHER, var20, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome()));
      PlacementUtils.register(var0, BROWN_MUSHROOM_NORMAL, var19, (List)getMushroomPlacement(256, (PlacementModifier)null));
      PlacementUtils.register(var0, RED_MUSHROOM_NORMAL, var20, (List)getMushroomPlacement(512, (PlacementModifier)null));
      PlacementUtils.register(var0, BROWN_MUSHROOM_TAIGA, var19, (List)getMushroomPlacement(4, (PlacementModifier)null));
      PlacementUtils.register(var0, RED_MUSHROOM_TAIGA, var20, (List)getMushroomPlacement(256, (PlacementModifier)null));
      PlacementUtils.register(var0, BROWN_MUSHROOM_OLD_GROWTH, var19, (List)getMushroomPlacement(4, CountPlacement.of(3)));
      PlacementUtils.register(var0, RED_MUSHROOM_OLD_GROWTH, var20, (List)getMushroomPlacement(171, (PlacementModifier)null));
      PlacementUtils.register(var0, BROWN_MUSHROOM_SWAMP, var19, (List)getMushroomPlacement(0, CountPlacement.of(2)));
      PlacementUtils.register(var0, RED_MUSHROOM_SWAMP, var20, (List)getMushroomPlacement(64, (PlacementModifier)null));
      PlacementUtils.register(var0, FLOWER_WARM, var21, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_DEFAULT, var21, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_FLOWER_FOREST, var22, (PlacementModifier[])(CountPlacement.of(3), RarityFilter.onAverageOnceEvery(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_SWAMP, var23, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_PLAINS, var24, (PlacementModifier[])(NoiseThresholdCountPlacement.of(-0.8, 15, 4), RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_CHERRY, var26, (PlacementModifier[])(NoiseThresholdCountPlacement.of(-0.8, 5, 10), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_MEADOW, var25, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      SurfaceWaterDepthFilter var52 = SurfaceWaterDepthFilter.forMaxDepth(0);
      PlacementUtils.register(var0, TREES_PLAINS, var27, (PlacementModifier[])(PlacementUtils.countExtra(0, 0.05F, 1), InSquarePlacement.spread(), var52, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO)), BiomeFilter.biome()));
      PlacementUtils.register(var0, DARK_FOREST_VEGETATION, var28, (PlacementModifier[])(CountPlacement.of(16), InSquarePlacement.spread(), var52, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome()));
      PlacementUtils.register(var0, PALE_GARDEN_VEGETATION, var29, (PlacementModifier[])(CountPlacement.of(16), InSquarePlacement.spread(), var52, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome()));
      PlacementUtils.register(var0, FLOWER_FOREST_FLOWERS, var30, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-1, 3), 0, 3)), BiomeFilter.biome()));
      PlacementUtils.register(var0, FOREST_FLOWERS, var30, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(7), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, CountPlacement.of(ClampedInt.of(UniformInt.of(-3, 1), 0, 1)), BiomeFilter.biome()));
      PlacementUtils.register(var0, TREES_FLOWER_FOREST, var31, (List)treePlacement(PlacementUtils.countExtra(6, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_MEADOW, var32, (List)treePlacement(RarityFilter.onAverageOnceEvery(100)));
      PlacementUtils.register(var0, TREES_CHERRY, var37, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.CHERRY_SAPLING));
      PlacementUtils.register(var0, TREES_TAIGA, var33, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_GROVE, var34, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_BADLANDS, var35, (List)treePlacement(PlacementUtils.countExtra(5, 0.1F, 1), Blocks.OAK_SAPLING));
      PlacementUtils.register(var0, TREES_SNOWY, var36, (List)treePlacement(PlacementUtils.countExtra(0, 0.1F, 1), Blocks.SPRUCE_SAPLING));
      PlacementUtils.register(var0, TREES_SWAMP, var38, (PlacementModifier[])(PlacementUtils.countExtra(2, 0.1F, 1), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(2), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.OAK_SAPLING.defaultBlockState(), BlockPos.ZERO))));
      PlacementUtils.register(var0, TREES_WINDSWEPT_SAVANNA, var39, (List)treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_SAVANNA, var39, (List)treePlacement(PlacementUtils.countExtra(1, 0.1F, 1)));
      PlacementUtils.register(var0, BIRCH_TALL, var40, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_BIRCH, var41, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1), Blocks.BIRCH_SAPLING));
      PlacementUtils.register(var0, TREES_WINDSWEPT_FOREST, var42, (List)treePlacement(PlacementUtils.countExtra(3, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_WINDSWEPT_HILLS, var42, (List)treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_WATER, var43, (List)treePlacement(PlacementUtils.countExtra(0, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_BIRCH_AND_OAK, var44, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_SPARSE_JUNGLE, var45, (List)treePlacement(PlacementUtils.countExtra(2, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_OLD_GROWTH_SPRUCE_TAIGA, var46, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_OLD_GROWTH_PINE_TAIGA, var47, (List)treePlacement(PlacementUtils.countExtra(10, 0.1F, 1)));
      PlacementUtils.register(var0, TREES_JUNGLE, var48, (List)treePlacement(PlacementUtils.countExtra(50, 0.1F, 1)));
      PlacementUtils.register(var0, BAMBOO_VEGETATION, var49, (List)treePlacement(PlacementUtils.countExtra(30, 0.1F, 1)));
      PlacementUtils.register(var0, MUSHROOM_ISLAND_VEGETATION, var50, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, TREES_MANGROVE, var51, (PlacementModifier[])(CountPlacement.of(25), InSquarePlacement.spread(), SurfaceWaterDepthFilter.forMaxDepth(5), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome(), BlockPredicateFilter.forPredicate(BlockPredicate.wouldSurvive(Blocks.MANGROVE_PROPAGULE.defaultBlockState(), BlockPos.ZERO))));
   }
}
