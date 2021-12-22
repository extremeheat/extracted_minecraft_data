package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PointedDripstoneConfiguration implements FeatureConfiguration {
   public static final Codec<PointedDripstoneConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_taller_dripstone").orElse(0.2F).forGetter((var0x) -> {
         return var0x.chanceOfTallerDripstone;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_directional_spread").orElse(0.7F).forGetter((var0x) -> {
         return var0x.chanceOfDirectionalSpread;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius2").orElse(0.5F).forGetter((var0x) -> {
         return var0x.chanceOfSpreadRadius2;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spread_radius3").orElse(0.5F).forGetter((var0x) -> {
         return var0x.chanceOfSpreadRadius3;
      })).apply(var0, PointedDripstoneConfiguration::new);
   });
   public final float chanceOfTallerDripstone;
   public final float chanceOfDirectionalSpread;
   public final float chanceOfSpreadRadius2;
   public final float chanceOfSpreadRadius3;

   public PointedDripstoneConfiguration(float var1, float var2, float var3, float var4) {
      super();
      this.chanceOfTallerDripstone = var1;
      this.chanceOfDirectionalSpread = var2;
      this.chanceOfSpreadRadius2 = var3;
      this.chanceOfSpreadRadius3 = var4;
   }
}
