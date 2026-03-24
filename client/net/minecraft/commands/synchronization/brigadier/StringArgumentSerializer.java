package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Objects;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer implements ArgumentTypeInfo<StringArgumentType, Template> {
   public StringArgumentSerializer() {
      super();
   }

   public void serializeToNetwork(final Template template, final FriendlyByteBuf out) {
      out.writeEnum(template.type);
   }

   public Template deserializeFromNetwork(final FriendlyByteBuf in) {
      StringArgumentType.StringType type = (StringArgumentType.StringType)in.readEnum(StringArgumentType.StringType.class);
      return new Template(type);
   }

   public void serializeToJson(final Template template, final JsonObject out) {
      String var10002;
      switch (template.type) {
         case SINGLE_WORD -> var10002 = "word";
         case QUOTABLE_PHRASE -> var10002 = "phrase";
         case GREEDY_PHRASE -> var10002 = "greedy";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      out.addProperty("type", var10002);
   }

   public Template unpack(final StringArgumentType argument) {
      return new Template(argument.getType());
   }

   public final class Template implements ArgumentTypeInfo.Template<StringArgumentType> {
      private final StringArgumentType.StringType type;

      public Template(final StringArgumentType.StringType type) {
         Objects.requireNonNull(StringArgumentSerializer.this);
         super();
         this.type = type;
      }

      public StringArgumentType instantiate(final CommandBuildContext context) {
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
   }
}
