package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;

public class LargeDripstoneConfiguration implements FeatureConfiguration {
   public static final Codec<LargeDripstoneConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(1, 512).fieldOf("floor_to_ceiling_search_range").orElse(30).forGetter((var0x) -> {
         return var0x.floorToCeilingSearchRange;
      }), IntProvider.codec(1, 60).fieldOf("column_radius").forGetter((var0x) -> {
         return var0x.columnRadius;
      }), FloatProvider.codec(0.0F, 20.0F).fieldOf("height_scale").forGetter((var0x) -> {
         return var0x.heightScale;
      }), Codec.floatRange(0.1F, 1.0F).fieldOf("max_column_radius_to_cave_height_ratio").forGetter((var0x) -> {
         return var0x.maxColumnRadiusToCaveHeightRatio;
      }), FloatProvider.codec(0.1F, 10.0F).fieldOf("stalactite_bluntness").forGetter((var0x) -> {
         return var0x.stalactiteBluntness;
      }), FloatProvider.codec(0.1F, 10.0F).fieldOf("stalagmite_bluntness").forGetter((var0x) -> {
         return var0x.stalagmiteBluntness;
      }), FloatProvider.codec(0.0F, 2.0F).fieldOf("wind_speed").forGetter((var0x) -> {
         return var0x.windSpeed;
      }), Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter((var0x) -> {
         return var0x.minRadiusForWind;
      }), Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter((var0x) -> {
         return var0x.minBluntnessForWind;
      })).apply(var0, LargeDripstoneConfiguration::new);
   });
   public final int floorToCeilingSearchRange;
   public final IntProvider columnRadius;
   public final FloatProvider heightScale;
   public final float maxColumnRadiusToCaveHeightRatio;
   public final FloatProvider stalactiteBluntness;
   public final FloatProvider stalagmiteBluntness;
   public final FloatProvider windSpeed;
   public final int minRadiusForWind;
   public final float minBluntnessForWind;

   public LargeDripstoneConfiguration(int var1, IntProvider var2, FloatProvider var3, float var4, FloatProvider var5, FloatProvider var6, FloatProvider var7, int var8, float var9) {
      super();
      this.floorToCeilingSearchRange = var1;
      this.columnRadius = var2;
      this.heightScale = var3;
      this.maxColumnRadiusToCaveHeightRatio = var4;
      this.stalactiteBluntness = var5;
      this.stalagmiteBluntness = var6;
      this.windSpeed = var7;
      this.minRadiusForWind = var8;
      this.minBluntnessForWind = var9;
   }
}
