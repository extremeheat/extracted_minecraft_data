package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.HolderLookup;

public interface ParticleOptions {
   ParticleType<?> getType();

   String writeToString(HolderLookup.Provider var1);

   /** @deprecated */
   @Deprecated
   public interface Deserializer<T extends ParticleOptions> {
      T fromCommand(ParticleType<T> var1, StringReader var2, HolderLookup.Provider var3) throws CommandSyntaxException;
   }
}
