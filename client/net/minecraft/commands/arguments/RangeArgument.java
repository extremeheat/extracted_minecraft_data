package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public interface RangeArgument<T extends MinMaxBounds<?>> extends ArgumentType<T> {
   static RangeArgument.Ints intRange() {
      return new RangeArgument.Ints();
   }

   public abstract static class Serializer<T extends RangeArgument<?>> implements ArgumentSerializer<T> {
      public Serializer() {
         super();
      }

      public void serializeToNetwork(T var1, FriendlyByteBuf var2) {
      }

      public void serializeToJson(T var1, JsonObject var2) {
      }
   }

   public static class Floats implements RangeArgument<MinMaxBounds.Floats> {
      private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public Floats() {
         super();
      }

      public MinMaxBounds.Floats parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.Floats.fromReader(var1);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer<RangeArgument.Floats> {
         public Serializer() {
            super();
         }

         public RangeArgument.Floats deserializeFromNetwork(FriendlyByteBuf var1) {
            return new RangeArgument.Floats();
         }

         // $FF: synthetic method
         public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
            return this.deserializeFromNetwork(var1);
         }
      }
   }

   public static class Ints implements RangeArgument<MinMaxBounds.Ints> {
      private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

      public Ints() {
         super();
      }

      public static MinMaxBounds.Ints getRange(CommandContext<CommandSourceStack> var0, String var1) {
         return (MinMaxBounds.Ints)var0.getArgument(var1, MinMaxBounds.Ints.class);
      }

      public MinMaxBounds.Ints parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.Ints.fromReader(var1);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer<RangeArgument.Ints> {
         public Serializer() {
            super();
         }

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
