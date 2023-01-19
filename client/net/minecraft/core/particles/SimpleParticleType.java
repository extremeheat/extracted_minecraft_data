package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
   private static final ParticleOptions.Deserializer<SimpleParticleType> DESERIALIZER = new ParticleOptions.Deserializer<SimpleParticleType>() {
      public SimpleParticleType fromCommand(ParticleType<SimpleParticleType> var1, StringReader var2) {
         return (SimpleParticleType)var1;
      }

      public SimpleParticleType fromNetwork(ParticleType<SimpleParticleType> var1, FriendlyByteBuf var2) {
         return (SimpleParticleType)var1;
      }
   };
   private final Codec<SimpleParticleType> codec = Codec.unit(this::getType);

   protected SimpleParticleType(boolean var1) {
      super(var1, DESERIALIZER);
   }

   public SimpleParticleType getType() {
      return this;
   }

   @Override
   public Codec<SimpleParticleType> codec() {
      return this.codec;
   }

   @Override
   public void writeToNetwork(FriendlyByteBuf var1) {
   }

   @Override
   public String writeToString() {
      return Registry.PARTICLE_TYPE.getKey(this).toString();
   }
}
