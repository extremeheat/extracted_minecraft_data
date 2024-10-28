package net.minecraft.core.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class ParticleType<T extends ParticleOptions> {
   private final boolean overrideLimiter;
   private final ParticleOptions.Deserializer<T> deserializer;

   protected ParticleType(boolean var1, ParticleOptions.Deserializer<T> var2) {
      super();
      this.overrideLimiter = var1;
      this.deserializer = var2;
   }

   public boolean getOverrideLimiter() {
      return this.overrideLimiter;
   }

   public ParticleOptions.Deserializer<T> getDeserializer() {
      return this.deserializer;
   }

   public abstract MapCodec<T> codec();

   public abstract StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();
}
