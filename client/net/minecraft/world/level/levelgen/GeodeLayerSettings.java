package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeLayerSettings {
   private static final Codec<Double> LAYER_RANGE = Codec.doubleRange(0.01D, 50.0D);
   public static final Codec<GeodeLayerSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(LAYER_RANGE.fieldOf("filling").orElse(1.7D).forGetter((var0x) -> {
         return var0x.filling;
      }), LAYER_RANGE.fieldOf("inner_layer").orElse(2.2D).forGetter((var0x) -> {
         return var0x.innerLayer;
      }), LAYER_RANGE.fieldOf("middle_layer").orElse(3.2D).forGetter((var0x) -> {
         return var0x.middleLayer;
      }), LAYER_RANGE.fieldOf("outer_layer").orElse(4.2D).forGetter((var0x) -> {
         return var0x.outerLayer;
      })).apply(var0, GeodeLayerSettings::new);
   });
   public final double filling;
   public final double innerLayer;
   public final double middleLayer;
   public final double outerLayer;

   public GeodeLayerSettings(double var1, double var3, double var5, double var7) {
      super();
      this.filling = var1;
      this.innerLayer = var3;
      this.middleLayer = var5;
      this.outerLayer = var7;
   }
}
