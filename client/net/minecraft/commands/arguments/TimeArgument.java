package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class TimeArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0d", "0s", "0t", "0");
   private static final SimpleCommandExceptionType ERROR_INVALID_UNIT = new SimpleCommandExceptionType(Component.translatable("argument.time.invalid_unit"));
   private static final Dynamic2CommandExceptionType ERROR_TICK_COUNT_TOO_LOW = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatable("argument.time.tick_count_too_low", var1, var0)
   );
   private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap();
   final int minimum;

   private TimeArgument(int var1) {
      super();
      this.minimum = var1;
   }

   public static TimeArgument time() {
      return new TimeArgument(0);
   }

   public static TimeArgument time(int var0) {
      return new TimeArgument(var0);
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      float var2 = var1.readFloat();
      String var3 = var1.readUnquotedString();
      int var4 = UNITS.getOrDefault(var3, 0);
      if (var4 == 0) {
         throw ERROR_INVALID_UNIT.create();
      } else {
         int var5 = Math.round(var2 * (float)var4);
         if (var5 < this.minimum) {
            throw ERROR_TICK_COUNT_TOO_LOW.create(var5, this.minimum);
         } else {
            return var5;
         }
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getRemaining());

      try {
         var3.readFloat();
      } catch (CommandSyntaxException var5) {
         return var2.buildFuture();
      }

      return SharedSuggestionProvider.suggest(UNITS.keySet(), var2.createOffset(var2.getStart() + var3.getCursor()));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      UNITS.put("d", 24000);
      UNITS.put("s", 20);
      UNITS.put("t", 1);
      UNITS.put("", 1);
   }

   public static class Info implements ArgumentTypeInfo<TimeArgument, TimeArgument.Info.Template> {
      public Info() {
         super();
      }

      public void serializeToNetwork(TimeArgument.Info.Template var1, FriendlyByteBuf var2) {
         var2.writeInt(var1.min);
      }

      public TimeArgument.Info.Template deserializeFromNetwork(FriendlyByteBuf var1) {
         int var2 = var1.readInt();
         return new TimeArgument.Info.Template(var2);
      }

      public void serializeToJson(TimeArgument.Info.Template var1, JsonObject var2) {
         var2.addProperty("min", var1.min);
      }

      public TimeArgument.Info.Template unpack(TimeArgument var1) {
         return new TimeArgument.Info.Template(var1.minimum);
      }

      public final class Template implements ArgumentTypeInfo.Template<TimeArgument> {
         final int min;

         Template(int var2) {
            super();
            this.min = var2;
         }

         public TimeArgument instantiate(CommandBuildContext var1) {
            return TimeArgument.time(this.min);
         }

         @Override
         public ArgumentTypeInfo<TimeArgument, ?> type() {
            return Info.this;
         }
      }
   }
}
