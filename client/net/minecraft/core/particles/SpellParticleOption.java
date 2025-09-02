package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;

public class SpellParticleOption implements ParticleOptions {
   private final ParticleType<SpellParticleOption> type;
   private final int color;
   private final float power;

   public static MapCodec<SpellParticleOption> codec(ParticleType<SpellParticleOption> var0) {
      return RecordCodecBuilder.mapCodec((var1) -> var1.group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color", -1).forGetter((var0x) -> var0x.color), Codec.FLOAT.optionalFieldOf("power", 1.0F).forGetter((var0x) -> var0x.power)).apply(var1, (var1x, var2) -> new SpellParticleOption(var0, var1x, var2)));
   }

   public static StreamCodec<? super ByteBuf, SpellParticleOption> streamCodec(ParticleType<SpellParticleOption> var0) {
      return StreamCodec.composite(ByteBufCodecs.INT, (var0x) -> var0x.color, ByteBufCodecs.FLOAT, (var0x) -> var0x.power, (var1, var2) -> new SpellParticleOption(var0, var1, var2));
   }

   private SpellParticleOption(ParticleType<SpellParticleOption> var1, int var2, float var3) {
      super();
      this.type = var1;
      this.color = var2;
      this.power = var3;
   }

   public ParticleType<SpellParticleOption> getType() {
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

   public float getPower() {
      return this.power;
   }

   public static SpellParticleOption create(ParticleType<SpellParticleOption> var0, int var1, float var2) {
      return new SpellParticleOption(var0, var1, var2);
   }

   public static SpellParticleOption create(ParticleType<SpellParticleOption> var0, float var1, float var2, float var3, float var4) {
      return create(var0, ARGB.colorFromFloat(1.0F, var1, var2, var3), var4);
   }
}
