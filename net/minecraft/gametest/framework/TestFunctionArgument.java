package net.minecraft.gametest.framework;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;

public class TestFunctionArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("techtests.piston", "techtests");

   public TestFunction parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      Optional var3 = GameTestRegistry.findTestFunction(var2);
      if (var3.isPresent()) {
         return (TestFunction)var3.get();
      } else {
         TextComponent var4 = new TextComponent("No such test: " + var2);
         throw new CommandSyntaxException(new SimpleCommandExceptionType(var4), var4);
      }
   }

   public static TestFunctionArgument testFunctionArgument() {
      return new TestFunctionArgument();
   }

   public static TestFunction getTestFunction(CommandContext var0, String var1) {
      return (TestFunction)var0.getArgument(var1, TestFunction.class);
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      Stream var3 = GameTestRegistry.getAllTestFunctions().stream().map(TestFunction::getTestName);
      return SharedSuggestionProvider.suggest(var3, var2);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
