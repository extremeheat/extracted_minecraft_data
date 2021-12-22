package net.minecraft.data.worldgen.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;

public class MiscOverworldPlacements {
   public static final PlacedFeature ICE_SPIKE;
   public static final PlacedFeature ICE_PATCH;
   public static final PlacedFeature FOREST_ROCK;
   public static final PlacedFeature ICEBERG_PACKED;
   public static final PlacedFeature ICEBERG_BLUE;
   public static final PlacedFeature BLUE_ICE;
   public static final PlacedFeature LAKE_LAVA_UNDERGROUND;
   public static final PlacedFeature LAKE_LAVA_SURFACE;
   public static final PlacedFeature DISK_CLAY;
   public static final PlacedFeature DISK_GRAVEL;
   public static final PlacedFeature DISK_SAND;
   public static final PlacedFeature FREEZE_TOP_LAYER;
   public static final PlacedFeature VOID_START_PLATFORM;
   public static final PlacedFeature DESERT_WELL;
   public static final PlacedFeature SPRING_LAVA;
   public static final PlacedFeature SPRING_LAVA_FROZEN;
   public static final PlacedFeature SPRING_WATER;

   public MiscOverworldPlacements() {
      super();
   }

   static {
      ICE_SPIKE = PlacementUtils.register("ice_spike", MiscOverworldFeatures.ICE_SPIKE.placed(CountPlacement.method_39(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      ICE_PATCH = PlacementUtils.register("ice_patch", MiscOverworldFeatures.ICE_PATCH.placed(CountPlacement.method_39(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      FOREST_ROCK = PlacementUtils.register("forest_rock", MiscOverworldFeatures.FOREST_ROCK.placed(CountPlacement.method_39(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      ICEBERG_PACKED = PlacementUtils.register("iceberg_packed", MiscOverworldFeatures.ICEBERG_PACKED.placed(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), BiomeFilter.biome()));
      ICEBERG_BLUE = PlacementUtils.register("iceberg_blue", MiscOverworldFeatures.ICEBERG_BLUE.placed(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), BiomeFilter.biome()));
      BLUE_ICE = PlacementUtils.register("blue_ice", MiscOverworldFeatures.BLUE_ICE.placed(CountPlacement.method_38(UniformInt.method_45(0, 19)), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(61)), BiomeFilter.biome()));
      LAKE_LAVA_UNDERGROUND = PlacementUtils.register("lake_lava_underground", MiscOverworldFeatures.LAKE_LAVA.placed(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightRangePlacement.method_35(UniformHeight.method_24(VerticalAnchor.absolute(0), VerticalAnchor.top())), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.method_41(Heightmap.Types.OCEAN_FLOOR_WG, -2147483648, -5), BiomeFilter.biome()));
      LAKE_LAVA_SURFACE = PlacementUtils.register("lake_lava_surface", MiscOverworldFeatures.LAKE_LAVA.placed(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      DISK_CLAY = PlacementUtils.register("disk_clay", MiscOverworldFeatures.DISK_CLAY.placed(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
      DISK_GRAVEL = PlacementUtils.register("disk_gravel", MiscOverworldFeatures.DISK_GRAVEL.placed(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
      DISK_SAND = PlacementUtils.register("disk_sand", MiscOverworldFeatures.DISK_SAND.placed(CountPlacement.method_39(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()));
      FREEZE_TOP_LAYER = PlacementUtils.register("freeze_top_layer", MiscOverworldFeatures.FREEZE_TOP_LAYER.placed(BiomeFilter.biome()));
      VOID_START_PLATFORM = PlacementUtils.register("void_start_platform", MiscOverworldFeatures.VOID_START_PLATFORM.placed(BiomeFilter.biome()));
      DESERT_WELL = PlacementUtils.register("desert_well", MiscOverworldFeatures.DESERT_WELL.placed(RarityFilter.onAverageOnceEvery(1000), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      SPRING_LAVA = PlacementUtils.register("spring_lava", MiscOverworldFeatures.SPRING_LAVA_OVERWORLD.placed(CountPlacement.method_39(20), InSquarePlacement.spread(), HeightRangePlacement.method_35(VeryBiasedToBottomHeight.method_27(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome()));
      SPRING_LAVA_FROZEN = PlacementUtils.register("spring_lava_frozen", MiscOverworldFeatures.SPRING_LAVA_FROZEN.placed(CountPlacement.method_39(20), InSquarePlacement.spread(), HeightRangePlacement.method_35(VeryBiasedToBottomHeight.method_27(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome()));
      SPRING_WATER = PlacementUtils.register("spring_water", MiscOverworldFeatures.SPRING_WATER.placed(CountPlacement.method_39(25), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(192)), BiomeFilter.biome()));
   }
}
