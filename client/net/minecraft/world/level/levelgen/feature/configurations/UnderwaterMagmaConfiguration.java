package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UnderwaterMagmaConfiguration implements FeatureConfiguration {
   public static final Codec<UnderwaterMagmaConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.intRange(0, 512).fieldOf("floor_search_range").forGetter((var0x) -> {
         return var0x.floorSearchRange;
      }), Codec.intRange(0, 64).fieldOf("placement_radius_around_floor").forGetter((var0x) -> {
         return var0x.placementRadiusAroundFloor;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("placement_probability_per_valid_position").forGetter((var0x) -> {
         return var0x.placementProbabilityPerValidPosition;
      })).apply(var0, UnderwaterMagmaConfiguration::new);
   });
   public final int floorSearchRange;
   public final int placementRadiusAroundFloor;
   public final float placementProbabilityPerValidPosition;

   public UnderwaterMagmaConfiguration(int var1, int var2, float var3) {
      super();
      this.floorSearchRange = var1;
      this.placementRadiusAroundFloor = var2;
      this.placementProbabilityPerValidPosition = var3;
   }
}
