package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public class DripstoneClusterConfiguration implements FeatureConfiguration {
   public static final Codec<DripstoneClusterConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter((c) -> c.floorToCeilingSearchRange), IntProviders.codec(1, 128).fieldOf("height").forGetter((c) -> c.height), IntProviders.codec(1, 128).fieldOf("radius").forGetter((c) -> c.radius), Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter((c) -> c.maxStalagmiteStalactiteHeightDiff), Codec.intRange(1, 64).fieldOf("height_deviation").forGetter((c) -> c.heightDeviation), IntProviders.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter((c) -> c.dripstoneBlockLayerThickness), FloatProviders.codec(0.0F, 2.0F).fieldOf("density").forGetter((c) -> c.density), FloatProviders.codec(0.0F, 2.0F).fieldOf("wetness").forGetter((c) -> c.wetness), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter((c) -> c.chanceOfDripstoneColumnAtMaxDistanceFromCenter), Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter((c) -> c.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn), Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter((c) -> c.maxDistanceFromCenterAffectingHeightBias)).apply(i, DripstoneClusterConfiguration::new));
   public final int floorToCeilingSearchRange;
   public final IntProvider height;
   public final IntProvider radius;
   public final int maxStalagmiteStalactiteHeightDiff;
   public final int heightDeviation;
   public final IntProvider dripstoneBlockLayerThickness;
   public final FloatProvider density;
   public final FloatProvider wetness;
   public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
   public final int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
   public final int maxDistanceFromCenterAffectingHeightBias;

   public DripstoneClusterConfiguration(final int floorToCeilingSearchRange, final IntProvider height, final IntProvider radius, final int maxStalagmiteStalactiteHeightDiff, final int heightDeviation, final IntProvider dripstoneBlockLayerThickness, final FloatProvider density, final FloatProvider wetness, final float chanceOfDripstoneColumnAtMaxDistanceFromCenter, final int maxDistanceFromEdgeAffectingChanceOfDripstoneColumn, final int maxDistanceFromCenterAffectingHeightBias) {
      super();
      this.floorToCeilingSearchRange = floorToCeilingSearchRange;
      this.height = height;
      this.radius = radius;
      this.maxStalagmiteStalactiteHeightDiff = maxStalagmiteStalactiteHeightDiff;
      this.heightDeviation = heightDeviation;
      this.dripstoneBlockLayerThickness = dripstoneBlockLayerThickness;
      this.density = density;
      this.wetness = wetness;
      this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = chanceOfDripstoneColumnAtMaxDistanceFromCenter;
      this.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn = maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
      this.maxDistanceFromCenterAffectingHeightBias = maxDistanceFromCenterAffectingHeightBias;
   }
}
