package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public class NoiseSlider {
   public static final Codec<NoiseSlider> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.DOUBLE.fieldOf("target").forGetter((var0x) -> {
         return var0x.target;
      }), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("size").forGetter((var0x) -> {
         return var0x.size;
      }), Codec.INT.fieldOf("offset").forGetter((var0x) -> {
         return var0x.offset;
      })).apply(var0, NoiseSlider::new);
   });
   private final double target;
   private final int size;
   private final int offset;

   public NoiseSlider(double var1, int var3, int var4) {
      super();
      this.target = var1;
      this.size = var3;
      this.offset = var4;
   }

   public double applySlide(double var1, int var3) {
      if (this.size <= 0) {
         return var1;
      } else {
         double var4 = (double)(var3 - this.offset) / (double)this.size;
         return Mth.clampedLerp(this.target, var1, var4);
      }
   }
}
