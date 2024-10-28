package net.minecraft.data.worldgen.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import net.minecraft.world.level.material.Fluids;

public class MiscOverworldPlacements {
   public static final ResourceKey<PlacedFeature> ICE_SPIKE = PlacementUtils.createKey("ice_spike");
   public static final ResourceKey<PlacedFeature> ICE_PATCH = PlacementUtils.createKey("ice_patch");
   public static final ResourceKey<PlacedFeature> FOREST_ROCK = PlacementUtils.createKey("forest_rock");
   public static final ResourceKey<PlacedFeature> ICEBERG_PACKED = PlacementUtils.createKey("iceberg_packed");
   public static final ResourceKey<PlacedFeature> ICEBERG_BLUE = PlacementUtils.createKey("iceberg_blue");
   public static final ResourceKey<PlacedFeature> BLUE_ICE = PlacementUtils.createKey("blue_ice");
   public static final ResourceKey<PlacedFeature> LAKE_LAVA_UNDERGROUND = PlacementUtils.createKey("lake_lava_underground");
   public static final ResourceKey<PlacedFeature> LAKE_LAVA_SURFACE = PlacementUtils.createKey("lake_lava_surface");
   public static final ResourceKey<PlacedFeature> DISK_CLAY = PlacementUtils.createKey("disk_clay");
   public static final ResourceKey<PlacedFeature> DISK_GRAVEL = PlacementUtils.createKey("disk_gravel");
   public static final ResourceKey<PlacedFeature> DISK_SAND = PlacementUtils.createKey("disk_sand");
   public static final ResourceKey<PlacedFeature> DISK_GRASS = PlacementUtils.createKey("disk_grass");
   public static final ResourceKey<PlacedFeature> FREEZE_TOP_LAYER = PlacementUtils.createKey("freeze_top_layer");
   public static final ResourceKey<PlacedFeature> VOID_START_PLATFORM = PlacementUtils.createKey("void_start_platform");
   public static final ResourceKey<PlacedFeature> DESERT_WELL = PlacementUtils.createKey("desert_well");
   public static final ResourceKey<PlacedFeature> SPRING_LAVA = PlacementUtils.createKey("spring_lava");
   public static final ResourceKey<PlacedFeature> SPRING_LAVA_FROZEN = PlacementUtils.createKey("spring_lava_frozen");
   public static final ResourceKey<PlacedFeature> SPRING_WATER = PlacementUtils.createKey("spring_water");

   public MiscOverworldPlacements() {
      super();
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(MiscOverworldFeatures.ICE_SPIKE);
      Holder.Reference var3 = var1.getOrThrow(MiscOverworldFeatures.ICE_PATCH);
      Holder.Reference var4 = var1.getOrThrow(MiscOverworldFeatures.FOREST_ROCK);
      Holder.Reference var5 = var1.getOrThrow(MiscOverworldFeatures.ICEBERG_PACKED);
      Holder.Reference var6 = var1.getOrThrow(MiscOverworldFeatures.ICEBERG_BLUE);
      Holder.Reference var7 = var1.getOrThrow(MiscOverworldFeatures.BLUE_ICE);
      Holder.Reference var8 = var1.getOrThrow(MiscOverworldFeatures.LAKE_LAVA);
      Holder.Reference var9 = var1.getOrThrow(MiscOverworldFeatures.DISK_CLAY);
      Holder.Reference var10 = var1.getOrThrow(MiscOverworldFeatures.DISK_GRAVEL);
      Holder.Reference var11 = var1.getOrThrow(MiscOverworldFeatures.DISK_SAND);
      Holder.Reference var12 = var1.getOrThrow(MiscOverworldFeatures.DISK_GRASS);
      Holder.Reference var13 = var1.getOrThrow(MiscOverworldFeatures.FREEZE_TOP_LAYER);
      Holder.Reference var14 = var1.getOrThrow(MiscOverworldFeatures.VOID_START_PLATFORM);
      Holder.Reference var15 = var1.getOrThrow(MiscOverworldFeatures.DESERT_WELL);
      Holder.Reference var16 = var1.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_OVERWORLD);
      Holder.Reference var17 = var1.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_FROZEN);
      Holder.Reference var18 = var1.getOrThrow(MiscOverworldFeatures.SPRING_WATER);
      PlacementUtils.register(var0, ICE_SPIKE, var2, (PlacementModifier[])(CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, ICE_PATCH, var3, (PlacementModifier[])(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.SNOW_BLOCK)), BiomeFilter.biome()));
      PlacementUtils.register(var0, FOREST_ROCK, var4, (PlacementModifier[])(CountPlacement.of(2), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, ICEBERG_BLUE, var6, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), BiomeFilter.biome()));
      PlacementUtils.register(var0, ICEBERG_PACKED, var5, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), BiomeFilter.biome()));
      PlacementUtils.register(var0, BLUE_ICE, var7, (PlacementModifier[])(CountPlacement.of(UniformInt.of(0, 19)), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(30), VerticalAnchor.absolute(61)), BiomeFilter.biome()));
      PlacementUtils.register(var0, LAKE_LAVA_UNDERGROUND, var8, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.top())), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, -2147483648, -5), BiomeFilter.biome()));
      PlacementUtils.register(var0, LAKE_LAVA_SURFACE, var8, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
      PlacementUtils.register(var0, DISK_CLAY, var9, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome()));
      PlacementUtils.register(var0, DISK_GRAVEL, var10, (PlacementModifier[])(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome()));
      PlacementUtils.register(var0, DISK_SAND, var11, (PlacementModifier[])(CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(Fluids.WATER)), BiomeFilter.biome()));
      PlacementUtils.register(var0, DISK_GRASS, var12, (PlacementModifier[])(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, RandomOffsetPlacement.vertical(ConstantInt.of(-1)), BlockPredicateFilter.forPredicate(BlockPredicate.matchesBlocks(Blocks.MUD)), BiomeFilter.biome()));
      PlacementUtils.register(var0, FREEZE_TOP_LAYER, var13, (PlacementModifier[])(BiomeFilter.biome()));
      PlacementUtils.register(var0, VOID_START_PLATFORM, var14, (PlacementModifier[])(BiomeFilter.biome()));
      PlacementUtils.register(var0, DESERT_WELL, var15, (PlacementModifier[])(RarityFilter.onAverageOnceEvery(1000), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_LAVA, var16, (PlacementModifier[])(CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_LAVA_FROZEN, var17, (PlacementModifier[])(CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome()));
      PlacementUtils.register(var0, SPRING_WATER, var18, (PlacementModifier[])(CountPlacement.of(25), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(192)), BiomeFilter.biome()));
   }
}
