package net.minecraft.data.worldgen.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import net.minecraft.world.level.material.Fluids;

public class MiscOverworldPlacements {
   public static final Holder<PlacedFeature> ICE_SPIKE;
   public static final Holder<PlacedFeature> ICE_PATCH;
   public static final Holder<PlacedFeature> FOREST_ROCK;
   public static final Holder<PlacedFeature> ICEBERG_PACKED;
   public static final Holder<PlacedFeature> ICEBERG_BLUE;
   public static final Holder<PlacedFeature> BLUE_ICE;
   public static final Holder<PlacedFeature> LAKE_LAVA_UNDERGROUND;
   public static final Holder<PlacedFeature> LAKE_LAVA_SURFACE;
   public static final Holder<PlacedFeature> DISK_CLAY;
   public static final Holder<PlacedFeature> DISK_GRAVEL;
   public static final Holder<PlacedFeature> DISK_SAND;
   public static final Holder<PlacedFeature> DISK_GRASS;
   public static final Holder<PlacedFeature> FREEZE_TOP_LAYER;
   public static final Holder<PlacedFeature> VOID_START_PLATFORM;
   public static final Holder<PlacedFeature> DESERT_WELL;
   public static final Holder<PlacedFeature> SPRING_LAVA;
   public static final Holder<PlacedFeature> SPRING_LAVA_FROZEN;
   public static final Holder<PlacedFeature> SPRING_WATER;

   public MiscOverworldPlacements() {
      super();
   }

   static {
      ICE_SPIKE = PlacementUtils.register("ice_spike", MiscOverworldFeatures.ICE_SPIKE, CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      ICE_PATCH = PlacementUtils.register("ice_patch", MiscOverworldFeatures.ICE_PATCH, CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.SNOW_BLOCK)), BiomeFilter.biome());
      FOREST_ROCK = PlacementUtils.register("forest_rock", MiscOverworldFeatures.FOREST_ROCK, CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      ICEBERG_PACKED = PlacementUtils.register("iceberg_packed", MiscOverworldFeatures.ICEBERG_PACKED, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), BiomeFilter.biome());
      ICEBERG_BLUE = PlacementUtils.register("iceberg_blue", MiscOverworldFeatures.ICEBERG_BLUE, RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), BiomeFilter.biome());
      BLUE_ICE = PlacementUtils.register("blue_ice", MiscOverworldFeatures.BLUE_ICE, CountPlacement.of(UniformInt.of(0, 19)), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(61)), BiomeFilter.biome());
      LAKE_LAVA_UNDERGROUND = PlacementUtils.register("lake_lava_underground", MiscOverworldFeatures.LAKE_LAVA, RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.top())), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, -2147483648, -5), BiomeFilter.biome());
      LAKE_LAVA_SURFACE = PlacementUtils.register("lake_lava_surface", MiscOverworldFeatures.LAKE_LAVA, RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
      DISK_CLAY = PlacementUtils.register("disk_clay", MiscOverworldFeatures.DISK_CLAY, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome());
      DISK_GRAVEL = PlacementUtils.register("disk_gravel", MiscOverworldFeatures.DISK_GRAVEL, InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome());
      DISK_SAND = PlacementUtils.register("disk_sand", MiscOverworldFeatures.DISK_SAND, CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome());
      DISK_GRASS = PlacementUtils.register("disk_grass", MiscOverworldFeatures.DISK_GRASS, CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.MUD)), BiomeFilter.biome());
      FREEZE_TOP_LAYER = PlacementUtils.register("freeze_top_layer", MiscOverworldFeatures.FREEZE_TOP_LAYER, BiomeFilter.biome());
      VOID_START_PLATFORM = PlacementUtils.register("void_start_platform", MiscOverworldFeatures.VOID_START_PLATFORM, BiomeFilter.biome());
      DESERT_WELL = PlacementUtils.register("desert_well", MiscOverworldFeatures.DESERT_WELL, RarityFilter.onAverageOnceEvery(1000), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
      SPRING_LAVA = PlacementUtils.register("spring_lava", MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome());
      SPRING_LAVA_FROZEN = PlacementUtils.register("spring_lava_frozen", MiscOverworldFeatures.SPRING_LAVA_FROZEN, CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome());
      SPRING_WATER = PlacementUtils.register("spring_water", MiscOverworldFeatures.SPRING_WATER, CountPlacement.of(25), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(192)), BiomeFilter.biome());
   }
}
