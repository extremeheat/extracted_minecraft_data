package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;

public class EmptyArgumentSerializer<T extends ArgumentType<?>> implements ArgumentSerializer<T> {
   private final Supplier<T> constructor;

   public EmptyArgumentSerializer(Supplier<T> var1) {
      super();
      this.constructor = var1;
   }

   public void serializeToNetwork(T var1, FriendlyByteBuf var2) {
   }

   public T deserializeFromNetwork(FriendlyByteBuf var1) {
      return (ArgumentType)this.constructor.get();
   }

   public void serializeToJson(T var1, JsonObject var2) {
   }
}
