package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SculkChargeParticleOptions(float roll) implements ParticleOptions {
   public static final MapCodec<SculkChargeParticleOptions> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("roll").forGetter((var0x) -> {
         return var0x.roll;
      })).apply(var0, SculkChargeParticleOptions::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, SculkChargeParticleOptions> STREAM_CODEC;

   public SculkChargeParticleOptions(float var1) {
      super();
      this.roll = var1;
   }

   public ParticleType<SculkChargeParticleOptions> getType() {
      return ParticleTypes.SCULK_CHARGE;
   }

   public float roll() {
      return this.roll;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, (var0) -> {
         return var0.roll;
      }, SculkChargeParticleOptions::new);
   }
}
