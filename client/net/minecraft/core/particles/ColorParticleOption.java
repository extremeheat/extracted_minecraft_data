package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;

public class ColorParticleOption implements ParticleOptions {
   private final ParticleType<ColorParticleOption> type;
   private final int color;

   public static MapCodec<ColorParticleOption> codec(ParticleType<ColorParticleOption> var0) {
      return ExtraCodecs.ARGB_COLOR_CODEC.xmap(var1 -> new ColorParticleOption(var0, var1), var0x -> var0x.color).fieldOf("color");
   }

   public static StreamCodec<? super ByteBuf, ColorParticleOption> streamCodec(ParticleType<ColorParticleOption> var0) {
      return ByteBufCodecs.INT.map(var1 -> new ColorParticleOption(var0, var1), var0x -> var0x.color);
   }

   private ColorParticleOption(ParticleType<ColorParticleOption> var1, int var2) {
      super();
      this.type = var1;
      this.color = var2;
   }

   @Override
   public ParticleType<ColorParticleOption> getType() {
      return this.type;
   }

   public float getRed() {
      return (float)ARGB.red(this.color) / 255.0F;
   }

   public float getGreen() {
      return (float)ARGB.green(this.color) / 255.0F;
   }

   public float getBlue() {
      return (float)ARGB.blue(this.color) / 255.0F;
   }

   public float getAlpha() {
      return (float)ARGB.alpha(this.color) / 255.0F;
   }

   public static ColorParticleOption create(ParticleType<ColorParticleOption> var0, int var1) {
      return new ColorParticleOption(var0, var1);
   }

   public static ColorParticleOption create(ParticleType<ColorParticleOption> var0, float var1, float var2, float var3) {
      return create(var0, ARGB.colorFromFloat(1.0F, var1, var2, var3));
   }
}
