package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.ScoreAccess;

public class OperationArgument implements SerializableArgument.ContextFree<Operation> {
   private static final OperationArgument INSTANCE = new OperationArgument();
   public static final SerializableArgument.Type<OperationArgument> TYPE;
   private static final List<String> VALUES;
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION;
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO;

   private OperationArgument() {
      super();
   }

   public SerializableArgument.Type<OperationArgument> type() {
      return TYPE;
   }

   public static ArgumentType<Operation> operation() {
      return INSTANCE.toBrigadier();
   }

   public static Operation getOperation(final CommandContext<?> context, final String name) {
      return (Operation)context.getArgument(name, Operation.class);
   }

   public Operation parse(final StringReader reader) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw ERROR_INVALID_OPERATION.createWithContext(reader);
      } else {
         int start = reader.getCursor();

         while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
         }

         return getOperation(reader.getString().substring(start, reader.getCursor()));
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return SuggestionContext.suggest(VALUES, builder);
   }

   public Collection<String> getExamples() {
      return VALUES;
   }

   private static Operation getOperation(final String op) throws CommandSyntaxException {
      return (Operation)(op.equals("><") ? (a, b) -> {
         int swap = a.get();
         a.set(b.get());
         b.set(swap);
      } : getSimpleOperation(op));
   }

   private static SimpleOperation getSimpleOperation(final String op) throws CommandSyntaxException {
      SimpleOperation var10000;
      switch (op) {
         case "=" -> var10000 = (a, b) -> b;
         case "+=" -> var10000 = Integer::sum;
         case "-=" -> var10000 = (a, b) -> a - b;
         case "*=" -> var10000 = (a, b) -> a * b;
         case "/=" -> var10000 = (a, b) -> {
   if (b == 0) {
      throw ERROR_DIVIDE_BY_ZERO.create();
   } else {
      return Mth.floorDiv(a, b);
   }
};
         case "%=" -> var10000 = (a, b) -> {
   if (b == 0) {
      throw ERROR_DIVIDE_BY_ZERO.create();
   } else {
      return Mth.positiveModulo(a, b);
   }
};
         case "<" -> var10000 = Math::min;
         case ">" -> var10000 = Math::max;
         default -> throw ERROR_INVALID_OPERATION.create();
      }

      return var10000;
   }

   static {
      TYPE = SerializableArgument.<OperationArgument>singletonType(INSTANCE);
      VALUES = List.of("=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><");
      ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(Component.translatable("arguments.operation.invalid"));
      ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(Component.translatable("arguments.operation.div0"));
   }

   @FunctionalInterface
   private interface SimpleOperation extends Operation {
      int apply(int a, int b) throws CommandSyntaxException;

      default void apply(final ScoreAccess a, final ScoreAccess b) throws CommandSyntaxException {
         a.set(this.apply(a.get(), b.get()));
      }
   }

   @FunctionalInterface
   public interface Operation {
      void apply(ScoreAccess a, ScoreAccess b) throws CommandSyntaxException;
   }
}
