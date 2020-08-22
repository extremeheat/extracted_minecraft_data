package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;

public class EmptyArgumentSerializer implements ArgumentSerializer {
   private final Supplier constructor;

   public EmptyArgumentSerializer(Supplier var1) {
      this.constructor = var1;
   }

   public void serializeToNetwork(ArgumentType var1, FriendlyByteBuf var2) {
   }

   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return (ArgumentType)this.constructor.get();
   }

   public void serializeToJson(ArgumentType var1, JsonObject var2) {
   }
}
