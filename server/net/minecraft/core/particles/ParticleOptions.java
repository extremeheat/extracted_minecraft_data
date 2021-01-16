package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.FriendlyByteBuf;

public interface ParticleOptions {
   ParticleType<?> getType();

   void writeToNetwork(FriendlyByteBuf var1);

   String writeToString();

   @Deprecated
   public interface Deserializer<T extends ParticleOptions> {
      T fromCommand(ParticleType<T> var1, StringReader var2) throws CommandSyntaxException;

      T fromNetwork(ParticleType<T> var1, FriendlyByteBuf var2);
   }
}
