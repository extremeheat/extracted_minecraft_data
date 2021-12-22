package net.minecraft.data.worldgen.placement;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;

public class CavePlacements {
   public static final PlacedFeature MONSTER_ROOM;
   public static final PlacedFeature MONSTER_ROOM_DEEP;
   public static final PlacedFeature FOSSIL_UPPER;
   public static final PlacedFeature FOSSIL_LOWER;
   public static final PlacedFeature DRIPSTONE_CLUSTER;
   public static final PlacedFeature LARGE_DRIPSTONE;
   public static final PlacedFeature POINTED_DRIPSTONE;
   public static final PlacedFeature UNDERWATER_MAGMA;
   public static final PlacedFeature GLOW_LICHEN;
   public static final PlacedFeature ROOTED_AZALEA_TREE;
   public static final PlacedFeature CAVE_VINES;
   public static final PlacedFeature LUSH_CAVES_VEGETATION;
   public static final PlacedFeature LUSH_CAVES_CLAY;
   public static final PlacedFeature LUSH_CAVES_CEILING_VEGETATION;
   public static final PlacedFeature SPORE_BLOSSOM;
   public static final PlacedFeature CLASSIC_VINES;
   public static final PlacedFeature AMETHYST_GEODE;

   public CavePlacements() {
      super();
   }

   static {
      MONSTER_ROOM = PlacementUtils.register("monster_room", CaveFeatures.MONSTER_ROOM.placed(CountPlacement.method_39(10), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome()));
      MONSTER_ROOM_DEEP = PlacementUtils.register("monster_room_deep", CaveFeatures.MONSTER_ROOM.placed(CountPlacement.method_39(4), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(-1)), BiomeFilter.biome()));
      FOSSIL_UPPER = PlacementUtils.register("fossil_upper", CaveFeatures.FOSSIL_COAL.placed(RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.top()), BiomeFilter.biome()));
      FOSSIL_LOWER = PlacementUtils.register("fossil_lower", CaveFeatures.FOSSIL_DIAMONDS.placed(RarityFilter.onAverageOnceEvery(64), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(-8)), BiomeFilter.biome()));
      DRIPSTONE_CLUSTER = PlacementUtils.register("dripstone_cluster", CaveFeatures.DRIPSTONE_CLUSTER.placed(CountPlacement.method_38(UniformInt.method_45(48, 96)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));
      LARGE_DRIPSTONE = PlacementUtils.register("large_dripstone", CaveFeatures.LARGE_DRIPSTONE.placed(CountPlacement.method_38(UniformInt.method_45(10, 48)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));
      POINTED_DRIPSTONE = PlacementUtils.register("pointed_dripstone", CaveFeatures.POINTED_DRIPSTONE.placed(CountPlacement.method_38(UniformInt.method_45(192, 256)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, CountPlacement.method_38(UniformInt.method_45(1, 5)), RandomOffsetPlacement.method_40(ClampedNormalInt.method_48(0.0F, 3.0F, -10, 10), ClampedNormalInt.method_48(0.0F, 0.6F, -2, 2)), BiomeFilter.biome()));
      UNDERWATER_MAGMA = PlacementUtils.register("underwater_magma", CaveFeatures.UNDERWATER_MAGMA.placed(CountPlacement.method_38(UniformInt.method_45(44, 52)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, SurfaceRelativeThresholdFilter.method_41(Heightmap.Types.OCEAN_FLOOR_WG, -2147483648, -2), BiomeFilter.biome()));
      GLOW_LICHEN = PlacementUtils.register("glow_lichen", CaveFeatures.GLOW_LICHEN.placed(CountPlacement.method_38(UniformInt.method_45(104, 157)), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, InSquarePlacement.spread(), SurfaceRelativeThresholdFilter.method_41(Heightmap.Types.OCEAN_FLOOR_WG, -2147483648, -13), BiomeFilter.biome()));
      ROOTED_AZALEA_TREE = PlacementUtils.register("rooted_azalea_tree", CaveFeatures.ROOTED_AZALEA_TREE.placed(CountPlacement.method_38(UniformInt.method_45(1, 2)), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.field_526, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(-1)), BiomeFilter.biome()));
      CAVE_VINES = PlacementUtils.register("cave_vines", CaveFeatures.CAVE_VINE.placed(CountPlacement.method_39(188), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.field_526, BlockPredicate.hasSturdyFace(Direction.DOWN), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(-1)), BiomeFilter.biome()));
      LUSH_CAVES_VEGETATION = PlacementUtils.register("lush_caves_vegetation", CaveFeatures.MOSS_PATCH.placed(CountPlacement.method_39(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(1)), BiomeFilter.biome()));
      LUSH_CAVES_CLAY = PlacementUtils.register("lush_caves_clay", CaveFeatures.LUSH_CAVES_CLAY.placed(CountPlacement.method_39(62), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(1)), BiomeFilter.biome()));
      LUSH_CAVES_CEILING_VEGETATION = PlacementUtils.register("lush_caves_ceiling_vegetation", CaveFeatures.MOSS_PATCH_CEILING.placed(CountPlacement.method_39(125), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.field_526, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(-1)), BiomeFilter.biome()));
      SPORE_BLOSSOM = PlacementUtils.register("spore_blossom", CaveFeatures.SPORE_BLOSSOM.placed(CountPlacement.method_39(25), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, EnvironmentScanPlacement.scanningFor(Direction.field_526, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE, 12), RandomOffsetPlacement.vertical(ConstantInt.method_49(-1)), BiomeFilter.biome()));
      CLASSIC_VINES = PlacementUtils.register("classic_vines_cave_feature", VegetationFeatures.VINES.placed(CountPlacement.method_39(256), InSquarePlacement.spread(), PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, BiomeFilter.biome()));
      AMETHYST_GEODE = PlacementUtils.register("amethyst_geode", CaveFeatures.AMETHYST_GEODE.placed(RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome()));
   }
}
