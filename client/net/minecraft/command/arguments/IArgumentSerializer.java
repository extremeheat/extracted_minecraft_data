package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.PacketBuffer;

public interface IArgumentSerializer<T extends ArgumentType<?>> {
   void func_197072_a(T var1, PacketBuffer var2);

   T func_197071_b(PacketBuffer var1);

   void func_212244_a(T var1, JsonObject var2);
}
