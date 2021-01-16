package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class RootCommandNode<S> extends CommandNode<S> {
   public RootCommandNode() {
      super((Command)null, (var0) -> {
         return true;
      }, (CommandNode)null, (var0) -> {
         return Collections.singleton(var0.getSource());
      }, false);
   }

   public String getName() {
      return "";
   }

   public String getUsageText() {
      return "";
   }

   public void parse(StringReader var1, CommandContextBuilder<S> var2) throws CommandSyntaxException {
   }

   public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return Suggestions.empty();
   }

   public boolean isValidInput(String var1) {
      return false;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof RootCommandNode) ? false : super.equals(var1);
      }
   }

   public ArgumentBuilder<S, ?> createBuilder() {
      throw new IllegalStateException("Cannot convert root into a builder");
   }

   protected String getSortedKey() {
      return "";
   }

   public Collection<String> getExamples() {
      return Collections.emptyList();
   }

   public String toString() {
      return "<root>";
   }
}
