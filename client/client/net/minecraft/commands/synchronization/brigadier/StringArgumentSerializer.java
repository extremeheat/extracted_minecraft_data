package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer implements ArgumentTypeInfo<StringArgumentType, StringArgumentSerializer.Template> {
   public StringArgumentSerializer() {
      super();
   }

   public void serializeToNetwork(StringArgumentSerializer.Template var1, FriendlyByteBuf var2) {
      var2.writeEnum(var1.type);
   }

   public StringArgumentSerializer.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      StringType var2 = var1.readEnum(StringType.class);
      return new StringArgumentSerializer.Template(var2);
   }

   public void serializeToJson(StringArgumentSerializer.Template var1, JsonObject var2) {
      var2.addProperty("type", switch (var1.type) {
         case SINGLE_WORD -> "word";
         case QUOTABLE_PHRASE -> "phrase";
         case GREEDY_PHRASE -> "greedy";
         default -> throw new MatchException(null, null);
      });
   }

   public StringArgumentSerializer.Template unpack(StringArgumentType var1) {
      return new StringArgumentSerializer.Template(var1.getType());
   }

   public final class Template implements ArgumentTypeInfo.Template<StringArgumentType> {
      final StringType type;

      public Template(StringType var2) {
         super();
         this.type = var2;
      }

      public StringArgumentType instantiate(CommandBuildContext var1) {
         return switch (this.type) {
            case SINGLE_WORD -> StringArgumentType.word();
            case QUOTABLE_PHRASE -> StringArgumentType.string();
            case GREEDY_PHRASE -> StringArgumentType.greedyString();
            default -> throw new MatchException(null, null);
         };
      }

      @Override
      public ArgumentTypeInfo<StringArgumentType, ?> type() {
         return StringArgumentSerializer.this;
      }
   }
}
