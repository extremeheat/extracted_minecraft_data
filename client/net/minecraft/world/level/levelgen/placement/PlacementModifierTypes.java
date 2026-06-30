package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public interface PlacementModifierTypes {
   static MapCodec<? extends PlacementModifier> bootstrap(final Registry<MapCodec<? extends PlacementModifier>> registry) {
      Registry.register(registry, (String)"block_predicate_filter", BlockPredicateFilter.CODEC);
      Registry.register(registry, (String)"rarity_filter", RarityFilter.CODEC);
      Registry.register(registry, (String)"surface_relative_threshold_filter", SurfaceRelativeThresholdFilter.CODEC);
      Registry.register(registry, (String)"surface_water_depth_filter", SurfaceWaterDepthFilter.CODEC);
      Registry.register(registry, (String)"biome", BiomeFilter.CODEC);
      Registry.register(registry, (String)"count", CountPlacement.CODEC);
      Registry.register(registry, (String)"noise_based_count", NoiseBasedCountPlacement.CODEC);
      Registry.register(registry, (String)"noise_threshold_count", NoiseThresholdCountPlacement.CODEC);
      Registry.register(registry, (String)"count_on_every_layer", CountOnEveryLayerPlacement.CODEC);
      Registry.register(registry, (String)"environment_scan", EnvironmentScanPlacement.CODEC);
      Registry.register(registry, (String)"heightmap", HeightmapPlacement.CODEC);
      Registry.register(registry, (String)"height_range", HeightRangePlacement.CODEC);
      Registry.register(registry, (String)"in_square", InSquarePlacement.CODEC);
      Registry.register(registry, (String)"offset", OffsetPlacement.CODEC);
      return (MapCodec)Registry.register(registry, (String)"fixed_placement", FixedPlacement.CODEC);
   }
}
