package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
   private final MapCodec<SimpleParticleType> codec = MapCodec.unit(this::getType);
   private final StreamCodec<RegistryFriendlyByteBuf, SimpleParticleType> streamCodec = StreamCodec.unit(this);

   protected SimpleParticleType(boolean var1) {
      super(var1);
   }

   public SimpleParticleType getType() {
      return this;
   }

   public MapCodec<SimpleParticleType> codec() {
      return this.codec;
   }

   public StreamCodec<RegistryFriendlyByteBuf, SimpleParticleType> streamCodec() {
      return this.streamCodec;
   }

   // $FF: synthetic method
   public ParticleType getType() {
      return this.getType();
   }
}
