package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.ScoreAccess;

public class OperationArgument implements ArgumentType<OperationArgument.Operation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(
      Component.translatable("arguments.operation.invalid")
   );
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(Component.translatable("arguments.operation.div0"));

   public OperationArgument() {
      super();
   }

   public static OperationArgument operation() {
      return new OperationArgument();
   }

   public static OperationArgument.Operation getOperation(CommandContext<CommandSourceStack> var0, String var1) {
      return (OperationArgument.Operation)var0.getArgument(var1, OperationArgument.Operation.class);
   }

   public OperationArgument.Operation parse(StringReader var1) throws CommandSyntaxException {
      if (!var1.canRead()) {
         throw ERROR_INVALID_OPERATION.createWithContext(var1);
      } else {
         int var2 = var1.getCursor();

         while(var1.canRead() && var1.peek() != ' ') {
            var1.skip();
         }

         return getOperation(var1.getString().substring(var2, var1.getCursor()));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static OperationArgument.Operation getOperation(String var0) throws CommandSyntaxException {
      return (OperationArgument.Operation)(var0.equals("><") ? (var0x, var1) -> {
         int var2 = var0x.get();
         var0x.set(var1.get());
         var1.set(var2);
      } : getSimpleOperation(var0));
   }

   private static OperationArgument.SimpleOperation getSimpleOperation(String var0) throws CommandSyntaxException {
      return switch(var0) {
         case "=" -> (var0x, var1) -> var1;
         case "+=" -> Integer::sum;
         case "-=" -> (var0x, var1) -> var0x - var1;
         case "*=" -> (var0x, var1) -> var0x * var1;
         case "/=" -> (var0x, var1) -> {
         if (var1 == 0) {
            throw ERROR_DIVIDE_BY_ZERO.create();
         } else {
            return Mth.floorDiv(var0x, var1);
         }
      };
         case "%=" -> (var0x, var1) -> {
         if (var1 == 0) {
            throw ERROR_DIVIDE_BY_ZERO.create();
         } else {
            return Mth.positiveModulo(var0x, var1);
         }
      };
         case "<" -> Math::min;
         case ">" -> Math::max;
         default -> throw ERROR_INVALID_OPERATION.create();
      };
   }

   @FunctionalInterface
   public interface Operation {
      void apply(ScoreAccess var1, ScoreAccess var2) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface SimpleOperation extends OperationArgument.Operation {
      int apply(int var1, int var2) throws CommandSyntaxException;

      @Override
      default void apply(ScoreAccess var1, ScoreAccess var2) throws CommandSyntaxException {
         var1.set(this.apply(var1.get(), var2.get()));
      }
   }
}
