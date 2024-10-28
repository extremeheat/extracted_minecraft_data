package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer implements ArgumentTypeInfo<StringArgumentType, Template> {
   public StringArgumentSerializer() {
      super();
   }

   public void serializeToNetwork(Template var1, FriendlyByteBuf var2) {
      var2.writeEnum(var1.type);
   }

   public Template deserializeFromNetwork(FriendlyByteBuf var1) {
      StringArgumentType.StringType var2 = (StringArgumentType.StringType)var1.readEnum(StringArgumentType.StringType.class);
      return new Template(var2);
   }

   public void serializeToJson(Template var1, JsonObject var2) {
      String var10002;
      switch (var1.type) {
         case SINGLE_WORD -> var10002 = "word";
         case QUOTABLE_PHRASE -> var10002 = "phrase";
         case GREEDY_PHRASE -> var10002 = "greedy";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      var2.addProperty("type", var10002);
   }

   public Template unpack(StringArgumentType var1) {
      return new Template(var1.getType());
   }

   // $FF: synthetic method
   public ArgumentTypeInfo.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }

   public final class Template implements ArgumentTypeInfo.Template<StringArgumentType> {
      final StringArgumentType.StringType type;

      public Template(StringArgumentType.StringType var2) {
         super();
         this.type = var2;
      }

      public StringArgumentType instantiate(CommandBuildContext var1) {
         StringArgumentType var10000;
         switch (this.type) {
            case SINGLE_WORD -> var10000 = StringArgumentType.word();
            case QUOTABLE_PHRASE -> var10000 = StringArgumentType.string();
            case GREEDY_PHRASE -> var10000 = StringArgumentType.greedyString();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public ArgumentTypeInfo<StringArgumentType, ?> type() {
         return StringArgumentSerializer.this;
      }

      // $FF: synthetic method
      public ArgumentType instantiate(CommandBuildContext var1) {
         return this.instantiate(var1);
      }
   }
}
