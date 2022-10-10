package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;

public interface IParticleData {
   ParticleType<?> func_197554_b();

   void func_197553_a(PacketBuffer var1);

   String func_197555_a();

   public interface IDeserializer<T extends IParticleData> {
      T func_197544_b(ParticleType<T> var1, StringReader var2) throws CommandSyntaxException;

      T func_197543_b(ParticleType<T> var1, PacketBuffer var2);
   }
}
