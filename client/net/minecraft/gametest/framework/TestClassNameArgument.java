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
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TestClassNameArgument implements ArgumentType<String> {
   private static final Collection<String> EXAMPLES = Arrays.asList("techtests", "mobtests");

   public TestClassNameArgument() {
      super();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.readUnquotedString();
      if (GameTestRegistry.isTestClass(var2)) {
         return var2;
      } else {
         MutableComponent var3 = Component.literal("No such test class: " + var2);
         throw new CommandSyntaxException(new SimpleCommandExceptionType(var3), var3);
      }
   }

   public static TestClassNameArgument testClassName() {
      return new TestClassNameArgument();
   }

   public static String getTestClassName(CommandContext<CommandSourceStack> var0, String var1) {
      return (String)var0.getArgument(var1, String.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(GameTestRegistry.getAllTestClassNames().stream(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
