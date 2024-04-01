package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
   private static final ParticleOptions.Deserializer<SimpleParticleType> DESERIALIZER = new ParticleOptions.Deserializer<SimpleParticleType>() {
      public SimpleParticleType fromCommand(ParticleType<SimpleParticleType> var1, StringReader var2, HolderLookup.Provider var3) {
         return (SimpleParticleType)var1;
      }
   };
   private final Codec<SimpleParticleType> codec = Codec.unit(this::getType);
   private final StreamCodec<RegistryFriendlyByteBuf, SimpleParticleType> streamCodec = StreamCodec.unit(this);

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
   public StreamCodec<RegistryFriendlyByteBuf, SimpleParticleType> streamCodec() {
      return this.streamCodec;
   }

   @Override
   public String writeToString(HolderLookup.Provider var1) {
      return BuiltInRegistries.PARTICLE_TYPE.getKey(this).toString();
   }
}
