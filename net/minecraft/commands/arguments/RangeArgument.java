package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public interface RangeArgument extends ArgumentType {
   static RangeArgument.Ints intRange() {
      return new RangeArgument.Ints();
   }

   public abstract static class Serializer implements ArgumentSerializer {
      public void serializeToNetwork(RangeArgument var1, FriendlyByteBuf var2) {
      }

      public void serializeToJson(RangeArgument var1, JsonObject var2) {
      }
   }

   public static class Floats implements RangeArgument {
      private static final Collection EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public MinMaxBounds.Floats parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.Floats.fromReader(var1);
      }

      public Collection getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer {
         public RangeArgument.Floats deserializeFromNetwork(FriendlyByteBuf var1) {
            return new RangeArgument.Floats();
         }

         // $FF: synthetic method
         public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
            return this.deserializeFromNetwork(var1);
         }
      }
   }

   public static class Ints implements RangeArgument {
      private static final Collection EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

      public static MinMaxBounds.Ints getRange(CommandContext var0, String var1) {
         return (MinMaxBounds.Ints)var0.getArgument(var1, MinMaxBounds.Ints.class);
      }

      public MinMaxBounds.Ints parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.Ints.fromReader(var1);
      }

      public Collection getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer {
         public RangeArgument.Ints deserializeFromNetwork(FriendlyByteBuf var1) {
            return new RangeArgument.Ints();
         }

         // $FF: synthetic method
         public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
            return this.deserializeFromNetwork(var1);
         }
      }
   }
}
