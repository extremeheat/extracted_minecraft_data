package net.minecraft.command.arguments;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class OperationArgument implements ArgumentType<OperationArgument.IOperation> {
   private static final Collection<String> field_201319_a = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType field_197185_a = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.operation.invalid", new Object[0]));
   private static final SimpleCommandExceptionType field_197186_b = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.operation.div0", new Object[0]));

   public OperationArgument() {
      super();
   }

   public static OperationArgument func_197184_a() {
      return new OperationArgument();
   }

   public static OperationArgument.IOperation func_197179_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return (OperationArgument.IOperation)var0.getArgument(var1, OperationArgument.IOperation.class);
   }

   public OperationArgument.IOperation parse(StringReader var1) throws CommandSyntaxException {
      if (!var1.canRead()) {
         throw field_197185_a.create();
      } else {
         int var2 = var1.getCursor();

         while(var1.canRead() && var1.peek() != ' ') {
            var1.skip();
         }

         return func_197177_a(var1.getString().substring(var2, var1.getCursor()));
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ISuggestionProvider.func_197008_a(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, var2);
   }

   public Collection<String> getExamples() {
      return field_201319_a;
   }

   private static OperationArgument.IOperation func_197177_a(String var0) throws CommandSyntaxException {
      return (OperationArgument.IOperation)(var0.equals("><") ? (var0x, var1) -> {
         int var2 = var0x.func_96652_c();
         var0x.func_96647_c(var1.func_96652_c());
         var1.func_96647_c(var2);
      } : func_197182_b(var0));
   }

   private static OperationArgument.Operation func_197182_b(String var0) throws CommandSyntaxException {
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
               throw field_197186_b.create();
            } else {
               return MathHelper.func_76137_a(var0x, var1);
            }
         };
      case 5:
         return (var0x, var1) -> {
            if (var1 == 0) {
               throw field_197186_b.create();
            } else {
               return MathHelper.func_180184_b(var0x, var1);
            }
         };
      case 6:
         return Math::min;
      case 7:
         return Math::max;
      default:
         throw field_197185_a.create();
      }
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   @FunctionalInterface
   interface Operation extends OperationArgument.IOperation {
      int apply(int var1, int var2) throws CommandSyntaxException;

      default void apply(Score var1, Score var2) throws CommandSyntaxException {
         var1.func_96647_c(this.apply(var1.func_96652_c(), var2.func_96652_c()));
      }
   }

   @FunctionalInterface
   public interface IOperation {
      void apply(Score var1, Score var2) throws CommandSyntaxException;
   }
}
