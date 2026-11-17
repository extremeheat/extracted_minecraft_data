package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

public abstract class StringArgument extends BrigadierWrapperArgument<String, StringArgumentType> {
   public abstract SerializableArgument.Type<? extends StringArgument> type();

   private StringArgument(final StringArgumentType type) {
      super(type);
   }

   public static ArgumentType<String> word() {
      return StringArgument.Word.INSTANCE.toBrigadier();
   }

   public static ArgumentType<String> string() {
      return StringArgument.Quoted.INSTANCE.toBrigadier();
   }

   public static ArgumentType<String> greedyString() {
      return StringArgument.Greedy.INSTANCE.toBrigadier();
   }

   public static String getString(final CommandContext<?> context, final String name) {
      return (String)context.getArgument(name, String.class);
   }

   public static class Word extends StringArgument {
      public static final Word INSTANCE = new Word();
      public static final SerializableArgument.Type<Word> TYPE;

      private Word() {
         super(StringArgumentType.word());
      }

      public SerializableArgument.Type<Word> type() {
         return TYPE;
      }

      static {
         TYPE = SerializableArgument.<Word>singletonType(INSTANCE);
      }
   }

   public static class Quoted extends StringArgument {
      public static final Quoted INSTANCE = new Quoted();
      public static final SerializableArgument.Type<Quoted> TYPE;

      private Quoted() {
         super(StringArgumentType.string());
      }

      public SerializableArgument.Type<Quoted> type() {
         return TYPE;
      }

      static {
         TYPE = SerializableArgument.<Quoted>singletonType(INSTANCE);
      }
   }

   public static class Greedy extends StringArgument {
      public static final Greedy INSTANCE = new Greedy();
      public static final SerializableArgument.Type<Greedy> TYPE;

      private Greedy() {
         super(StringArgumentType.greedyString());
      }

      public SerializableArgument.Type<Greedy> type() {
         return TYPE;
      }

      static {
         TYPE = SerializableArgument.<Greedy>singletonType(INSTANCE);
      }
   }
}
