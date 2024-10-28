package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

public class DripstoneClusterConfiguration implements FeatureConfiguration {
   public static final Codec<DripstoneClusterConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").forGetter((var0x) -> {
         return var0x.floorToCeilingSearchRange;
      }), IntProvider.codec(1, 128).fieldOf("height").forGetter((var0x) -> {
         return var0x.height;
      }), IntProvider.codec(1, 128).fieldOf("radius").forGetter((var0x) -> {
         return var0x.radius;
      }), Codec.intRange(0, 64).fieldOf("max_stalagmite_stalactite_height_diff").forGetter((var0x) -> {
         return var0x.maxStalagmiteStalactiteHeightDiff;
      }), Codec.intRange(1, 64).fieldOf("height_deviation").forGetter((var0x) -> {
         return var0x.heightDeviation;
      }), IntProvider.codec(0, 128).fieldOf("dripstone_block_layer_thickness").forGetter((var0x) -> {
         return var0x.dripstoneBlockLayerThickness;
      }), FloatProvider.codec(0.0F, 2.0F).fieldOf("density").forGetter((var0x) -> {
         return var0x.density;
      }), FloatProvider.codec(0.0F, 2.0F).fieldOf("wetness").forGetter((var0x) -> {
         return var0x.wetness;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_dripstone_column_at_max_distance_from_center").forGetter((var0x) -> {
         return var0x.chanceOfDripstoneColumnAtMaxDistanceFromCenter;
      }), Codec.intRange(1, 64).fieldOf("max_distance_from_edge_affecting_chance_of_dripstone_column").forGetter((var0x) -> {
         return var0x.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn;
      }), Codec.intRange(1, 64).fieldOf("max_distance_from_center_affecting_height_bias").forGetter((var0x) -> {
         return var0x.maxDistanceFromCenterAffectingHeightBias;
      })).apply(var0, DripstoneClusterConfiguration::new);
   });
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

   public DripstoneClusterConfiguration(int var1, IntProvider var2, IntProvider var3, int var4, int var5, IntProvider var6, FloatProvider var7, FloatProvider var8, float var9, int var10, int var11) {
      super();
      this.floorToCeilingSearchRange = var1;
      this.height = var2;
      this.radius = var3;
      this.maxStalagmiteStalactiteHeightDiff = var4;
      this.heightDeviation = var5;
      this.dripstoneBlockLayerThickness = var6;
      this.density = var7;
      this.wetness = var8;
      this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = var9;
      this.maxDistanceFromEdgeAffectingChanceOfDripstoneColumn = var10;
      this.maxDistanceFromCenterAffectingHeightBias = var11;
   }
}
