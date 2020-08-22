package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.network.FriendlyByteBuf;

public interface ArgumentSerializer {
   void serializeToNetwork(ArgumentType var1, FriendlyByteBuf var2);

   ArgumentType deserializeFromNetwork(FriendlyByteBuf var1);

   void serializeToJson(ArgumentType var1, JsonObject var2);
}
