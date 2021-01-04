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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Score;

public class OperationArgument implements ArgumentType<OperationArgument.Operation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(new TranslatableComponent("arguments.operation.invalid", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(new TranslatableComponent("arguments.operation.div0", new Object[0]));

   public OperationArgument() {
      super();
   }

   public static OperationArgument operation() {
      return new OperationArgument();
   }

   public static OperationArgument.Operation getOperation(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return (OperationArgument.Operation)var0.getArgument(var1, OperationArgument.Operation.class);
   }

   public OperationArgument.Operation parse(StringReader var1) throws CommandSyntaxException {
      if (!var1.canRead()) {
         throw ERROR_INVALID_OPERATION.create();
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
         int var2 = var0x.getScore();
         var0x.setScore(var1.getScore());
         var1.setScore(var2);
      } : getSimpleOperation(var0));
   }

   private static OperationArgument.SimpleOperation getSimpleOperation(String var0) throws CommandSyntaxException {
      byte var2 = -1;
      switch(var0.hashCode()) {
      case 60:
         if (var0.equals("<")) {
            var2 = 6;
         }
         break;
      case 61:
         if (var0.equals("=")) {
            var2 = 0;
         }
         break;
      case 62:
         if (var0.equals(">")) {
            var2 = 7;
         }
         break;
      case 1208:
         if (var0.equals("%=")) {
            var2 = 5;
         }
         break;
      case 1363:
         if (var0.equals("*=")) {
            var2 = 3;
         }
         break;
      case 1394:
         if (var0.equals("+=")) {
            var2 = 1;
         }
         break;
      case 1456:
         if (var0.equals("-=")) {
            var2 = 2;
         }
         break;
      case 1518:
         if (var0.equals("/=")) {
            var2 = 4;
         }
      }

      switch(var2) {
      case 0:
         return (var0x, var1) -> {
            return var1;
         };
      case 1:
         return (var0x, var1) -> {
            return var0x + var1;
         };
      case 2:
         return (var0x, var1) -> {
            return var0x - var1;
         };
      case 3:
         return (var0x, var1) -> {
            return var0x * var1;
         };
      case 4:
         return (var0x, var1) -> {
            if (var1 == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return Mth.intFloorDiv(var0x, var1);
            }
         };
      case 5:
         return (var0x, var1) -> {
            if (var1 == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return Mth.positiveModulo(var0x, var1);
            }
         };
      case 6:
         return Math::min;
      case 7:
         return Math::max;
      default:
         throw ERROR_INVALID_OPERATION.create();
      }
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   @FunctionalInterface
   interface SimpleOperation extends OperationArgument.Operation {
      int apply(int var1, int var2) throws CommandSyntaxException;

      default void apply(Score var1, Score var2) throws CommandSyntaxException {
         var1.setScore(this.apply(var1.getScore(), var2.getScore()));
      }
   }

   @FunctionalInterface
   public interface Operation {
      void apply(Score var1, Score var2) throws CommandSyntaxException;
   }
}
