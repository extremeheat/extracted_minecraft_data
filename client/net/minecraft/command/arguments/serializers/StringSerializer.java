package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class StringSerializer implements IArgumentSerializer<StringArgumentType> {
   public StringSerializer() {
      super();
   }

   public void func_197072_a(StringArgumentType var1, PacketBuffer var2) {
      var2.func_179249_a(var1.getType());
   }

   public StringArgumentType func_197071_b(PacketBuffer var1) {
      StringType var2 = (StringType)var1.func_179257_a(StringType.class);
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

   public void func_212244_a(StringArgumentType var1, JsonObject var2) {
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
   public ArgumentType func_197071_b(PacketBuffer var1) {
      return this.func_197071_b(var1);
   }
}
