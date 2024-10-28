package net.minecraft.gametest.framework;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TestFunctionArgument implements ArgumentType<TestFunction> {
   private static final Collection<String> EXAMPLES = Arrays.asList("techtests.piston", "techtests");

   public TestFunctionArgument() {
      super();
   }

   public TestFunction parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      Optional var3 = GameTestRegistry.findTestFunction(var2);
      if (var3.isPresent()) {
         return (TestFunction)var3.get();
      } else {
         MutableComponent var4 = Component.literal("No such test: " + var2);
         throw new CommandSyntaxException(new SimpleCommandExceptionType(var4), var4);
      }
   }

   public static TestFunctionArgument testFunctionArgument() {
      return new TestFunctionArgument();
   }

   public static TestFunction getTestFunction(CommandContext<CommandSourceStack> var0, String var1) {
      return (TestFunction)var0.getArgument(var1, TestFunction.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return suggestTestFunction(var1, var2);
   }

   public static <S> CompletableFuture<Suggestions> suggestTestFunction(CommandContext<S> var0, SuggestionsBuilder var1) {
      Stream var2 = GameTestRegistry.getAllTestFunctions().stream().map(TestFunction::testName);
      return SharedSuggestionProvider.suggest(var2, var1);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
