package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentSerializer<T extends ArgumentType<?>> {
   void serializeToNetwork(T var1, FriendlyByteBuf var2);

   T deserializeFromNetwork(FriendlyByteBuf var1);

   void serializeToJson(T var1, JsonObject var2);
}
