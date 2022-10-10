package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.network.PacketBuffer;

public interface RangeArgument<T extends MinMaxBounds<?>> extends ArgumentType<T> {
   static RangeArgument.IntRange func_211371_a() {
      return new RangeArgument.IntRange();
   }

   public abstract static class Serializer<T extends RangeArgument<?>> implements IArgumentSerializer<T> {
      public Serializer() {
         super();
      }

      public void func_197072_a(T var1, PacketBuffer var2) {
      }

      public void func_212244_a(T var1, JsonObject var2) {
      }
   }

   public static class FloatRange implements RangeArgument<MinMaxBounds.FloatBound> {
      private static final Collection<String> field_211374_a = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public FloatRange() {
         super();
      }

      public MinMaxBounds.FloatBound parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.FloatBound.func_211357_a(var1);
      }

      public Collection<String> getExamples() {
         return field_211374_a;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer<RangeArgument.FloatRange> {
         public Serializer() {
            super();
         }

         public RangeArgument.FloatRange func_197071_b(PacketBuffer var1) {
            return new RangeArgument.FloatRange();
         }

         // $FF: synthetic method
         public ArgumentType func_197071_b(PacketBuffer var1) {
            return this.func_197071_b(var1);
         }
      }
   }

   public static class IntRange implements RangeArgument<MinMaxBounds.IntBound> {
      private static final Collection<String> field_201321_a = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

      public IntRange() {
         super();
      }

      public static MinMaxBounds.IntBound func_211372_a(CommandContext<CommandSource> var0, String var1) {
         return (MinMaxBounds.IntBound)var0.getArgument(var1, MinMaxBounds.IntBound.class);
      }

      public MinMaxBounds.IntBound parse(StringReader var1) throws CommandSyntaxException {
         return MinMaxBounds.IntBound.func_211342_a(var1);
      }

      public Collection<String> getExamples() {
         return field_201321_a;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer<RangeArgument.IntRange> {
         public Serializer() {
            super();
         }

         public RangeArgument.IntRange func_197071_b(PacketBuffer var1) {
            return new RangeArgument.IntRange();
         }

         // $FF: synthetic method
         public ArgumentType func_197071_b(PacketBuffer var1) {
            return this.func_197071_b(var1);
         }
      }
   }
}
