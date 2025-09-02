package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class PowerParticleOption implements ParticleOptions {
   private final ParticleType<PowerParticleOption> type;
   private final float power;

   public static MapCodec<PowerParticleOption> codec(ParticleType<PowerParticleOption> var0) {
      return Codec.FLOAT.xmap((var1) -> new PowerParticleOption(var0, var1), (var0x) -> var0x.power).optionalFieldOf("power", create(var0, 1.0F));
   }

   public static StreamCodec<? super ByteBuf, PowerParticleOption> streamCodec(ParticleType<PowerParticleOption> var0) {
      return ByteBufCodecs.FLOAT.map((var1) -> new PowerParticleOption(var0, var1), (var0x) -> var0x.power);
   }

   private PowerParticleOption(ParticleType<PowerParticleOption> var1, float var2) {
      super();
      this.type = var1;
      this.power = var2;
   }

   public ParticleType<PowerParticleOption> getType() {
      return this.type;
   }

   public float getPower() {
      return this.power;
   }

   public static PowerParticleOption create(ParticleType<PowerParticleOption> var0, float var1) {
      return new PowerParticleOption(var0, var1);
   }
}
