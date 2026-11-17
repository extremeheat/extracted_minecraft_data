package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record WaterCurrentDownParticleOptions(float radius) implements ParticleOptions {
   public static final WaterCurrentDownParticleOptions DEFAULT = new WaterCurrentDownParticleOptions(0.5F);
   public static final MapCodec<WaterCurrentDownParticleOptions> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("radius").forGetter(WaterCurrentDownParticleOptions::radius)).apply(i, WaterCurrentDownParticleOptions::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, WaterCurrentDownParticleOptions> STREAM_CODEC;

   public WaterCurrentDownParticleOptions {
      super();
   }

   public ParticleType<?> getType() {
      return ParticleTypes.CURRENT_DOWN;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, WaterCurrentDownParticleOptions::radius, WaterCurrentDownParticleOptions::new);
   }
}
