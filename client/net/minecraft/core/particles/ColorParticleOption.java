package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class ColorParticleOption implements ParticleOptions {
   public static final ParticleOptions.Deserializer<ColorParticleOption> DESERIALIZER = (var0, var1, var2) -> new ColorParticleOption(var0, var1.readInt());
   private final ParticleType<? extends ColorParticleOption> type;
   private final int color;

   public static Codec<ColorParticleOption> codec(ParticleType<ColorParticleOption> var0) {
      return Codec.INT.xmap(var1 -> new ColorParticleOption(var0, var1), var0x -> var0x.color);
   }

   public static StreamCodec<? super ByteBuf, ColorParticleOption> streamCodec(ParticleType<ColorParticleOption> var0) {
      return ByteBufCodecs.INT.map(var1 -> new ColorParticleOption(var0, var1), var0x -> var0x.color);
   }

   private ColorParticleOption(ParticleType<? extends ColorParticleOption> var1, int var2) {
      super();
      this.type = var1;
      this.color = var2;
   }

   @Override
   public ParticleType<?> getType() {
      return this.type;
   }

   @Override
   public String writeToString(HolderLookup.Provider var1) {
      return String.format(Locale.ROOT, "%s 0x%x", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color);
   }

   public float getRed() {
      return (float)FastColor.ARGB32.red(this.color) / 255.0F;
   }

   public float getGreen() {
      return (float)FastColor.ARGB32.green(this.color) / 255.0F;
   }

   public float getBlue() {
      return (float)FastColor.ARGB32.blue(this.color) / 255.0F;
   }

   public float getAlpha() {
      return (float)FastColor.ARGB32.alpha(this.color) / 255.0F;
   }

   public static ColorParticleOption create(ParticleType<? extends ColorParticleOption> var0, int var1) {
      return new ColorParticleOption(var0, var1);
   }

   public static ColorParticleOption create(ParticleType<? extends ColorParticleOption> var0, float var1, float var2, float var3) {
      return create(var0, FastColor.ARGB32.color(as32BitChannel(var1), as32BitChannel(var2), as32BitChannel(var3)));
   }

   private static int as32BitChannel(float var0) {
      return Mth.floor(var0 * 255.0F);
   }
}
