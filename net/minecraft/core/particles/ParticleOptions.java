package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.FriendlyByteBuf;

public interface ParticleOptions {
   ParticleType getType();

   void writeToNetwork(FriendlyByteBuf var1);

   String writeToString();

   public interface Deserializer {
      ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException;

      ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2);
   }
}
