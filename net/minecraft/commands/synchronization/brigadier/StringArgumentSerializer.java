package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer implements ArgumentSerializer {
   public void serializeToNetwork(StringArgumentType var1, FriendlyByteBuf var2) {
      var2.writeEnum(var1.getType());
   }

   public StringArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      StringType var2 = (StringType)var1.readEnum(StringType.class);
      switch(var2) {
      case SINGLE_WORD:
         return StringArgumentType.word();
      case QUOTABLE_PHRASE:
         return StringArgumentType.string();
      case GREEDY_PHRASE:
      default:
         return StringArgumentType.greedyString();
      }
   }

   public void serializeToJson(StringArgumentType var1, JsonObject var2) {
      switch(var1.getType()) {
      case SINGLE_WORD:
         var2.addProperty("type", "word");
         break;
      case QUOTABLE_PHRASE:
         var2.addProperty("type", "phrase");
         break;
      case GREEDY_PHRASE:
      default:
         var2.addProperty("type", "greedy");
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
