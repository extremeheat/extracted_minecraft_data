package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;

public interface RangeArgument<T extends MinMaxBounds<?>> extends ArgumentType<T> {
   static Ints intRange() {
      return new Ints();
   }

   static Floats floatRange() {
      return new Floats();
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
      public Object parse(final StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }
   }

   public static class Floats implements RangeArgument<MinMaxBounds.Doubles> {
      private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public Floats() {
         super();
      }

      public static MinMaxBounds.Doubles getRange(CommandContext<CommandSourceStack> var0, String var1) {
         return (MinMaxBounds.Doubles)var0.getArgument(var1, MinMaxBounds.Doubles.class);
      }

      public MinMaxBounds.Doubles parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.Doubles.fromReader(var1);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(final StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }
   }
}
