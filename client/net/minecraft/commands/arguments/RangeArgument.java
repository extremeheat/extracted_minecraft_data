package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.predicates.MinMaxBounds;

public interface RangeArgument<T extends MinMaxBounds<?>> extends SerializableArgument.ContextFree<T> {
   static ArgumentType<MinMaxBounds.Ints> intRange() {
      return RangeArgument.Ints.INSTANCE.toBrigadier();
   }

   static ArgumentType<MinMaxBounds.Doubles> floatRange() {
      return RangeArgument.Floats.INSTANCE.toBrigadier();
   }

   SerializableArgument.Type<? extends RangeArgument<T>> type();

   public static class Ints implements RangeArgument<MinMaxBounds.Ints> {
      private static final Ints INSTANCE = new Ints();
      public static final SerializableArgument.Type<Ints> TYPE;
      private static final Collection<String> EXAMPLES;

      private Ints() {
         super();
      }

      public SerializableArgument.Type<Ints> type() {
         return TYPE;
      }

      public static MinMaxBounds.Ints getRange(final CommandContext<?> context, final String name) {
         return (MinMaxBounds.Ints)context.getArgument(name, MinMaxBounds.Ints.class);
      }

      public MinMaxBounds.Ints parse(final StringReader reader) throws CommandSyntaxException {
         return MinMaxBounds.Ints.fromReader(reader);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      static {
         TYPE = SerializableArgument.<Ints>singletonType(INSTANCE);
         EXAMPLES = List.of("0..5", "0", "-5", "-100..", "..100");
      }
   }

   public static class Floats implements RangeArgument<MinMaxBounds.Doubles> {
      private static final Floats INSTANCE = new Floats();
      public static final SerializableArgument.Type<Floats> TYPE;
      private static final Collection<String> EXAMPLES;

      private Floats() {
         super();
      }

      public SerializableArgument.Type<Floats> type() {
         return TYPE;
      }

      public static MinMaxBounds.Doubles getRange(final CommandContext<?> context, final String name) {
         return (MinMaxBounds.Doubles)context.getArgument(name, MinMaxBounds.Doubles.class);
      }

      public MinMaxBounds.Doubles parse(final StringReader reader) throws CommandSyntaxException {
         return MinMaxBounds.Doubles.fromReader(reader);
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      static {
         TYPE = SerializableArgument.<Floats>singletonType(INSTANCE);
         EXAMPLES = List.of("0..5.2", "0", "-5.4", "-100.76..", "..100");
      }
   }
}
