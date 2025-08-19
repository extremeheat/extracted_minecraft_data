package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ExplosionParticleInfo(ParticleOptions particle, float scaling, float speed) {
   public static final MapCodec<ExplosionParticleInfo> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ParticleTypes.CODEC.fieldOf("particle").forGetter(ExplosionParticleInfo::particle), Codec.FLOAT.optionalFieldOf("scaling", 1.0F).forGetter(ExplosionParticleInfo::scaling), Codec.FLOAT.optionalFieldOf("speed", 1.0F).forGetter(ExplosionParticleInfo::speed)).apply(var0, ExplosionParticleInfo::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, ExplosionParticleInfo> STREAM_CODEC;

   public ExplosionParticleInfo(ParticleOptions var1, float var2, float var3) {
      super();
      this.particle = var1;
      this.scaling = var2;
      this.speed = var3;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ParticleTypes.STREAM_CODEC, ExplosionParticleInfo::particle, ByteBufCodecs.FLOAT, ExplosionParticleInfo::scaling, ByteBufCodecs.FLOAT, ExplosionParticleInfo::speed, ExplosionParticleInfo::new);
   }
}
