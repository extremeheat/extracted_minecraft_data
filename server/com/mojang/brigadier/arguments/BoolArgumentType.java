package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BoolArgumentType implements ArgumentType<Boolean> {
   private static final Collection<String> EXAMPLES = Arrays.asList("true", "false");

   private BoolArgumentType() {
      super();
   }

   public static BoolArgumentType bool() {
      return new BoolArgumentType();
   }

   public static boolean getBool(CommandContext<?> var0, String var1) {
      return (Boolean)var0.getArgument(var1, Boolean.class);
   }

   public Boolean parse(StringReader var1) throws CommandSyntaxException {
      return var1.readBoolean();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if ("true".startsWith(var2.getRemaining().toLowerCase())) {
         var2.suggest("true");
      }

      if ("false".startsWith(var2.getRemaining().toLowerCase())) {
         var2.suggest("false");
      }

      return var2.buildFuture();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
