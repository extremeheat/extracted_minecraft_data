package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ArgumentCommandNode<S, T> extends CommandNode<S> {
   private static final String USAGE_ARGUMENT_OPEN = "<";
   private static final String USAGE_ARGUMENT_CLOSE = ">";
   private final String name;
   private final ArgumentType<T> type;
   private final SuggestionProvider<S> customSuggestions;

   public ArgumentCommandNode(String var1, ArgumentType<T> var2, Command<S> var3, Predicate<S> var4, CommandNode<S> var5, RedirectModifier<S> var6, boolean var7, SuggestionProvider<S> var8) {
      super(var3, var4, var5, var6, var7);
      this.name = var1;
      this.type = var2;
      this.customSuggestions = var8;
   }

   public ArgumentType<T> getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public String getUsageText() {
      return "<" + this.name + ">";
   }

   public SuggestionProvider<S> getCustomSuggestions() {
      return this.customSuggestions;
   }

   public void parse(StringReader var1, CommandContextBuilder<S> var2) throws CommandSyntaxException {
      int var3 = var1.getCursor();
      Object var4 = this.type.parse(var1);
      ParsedArgument var5 = new ParsedArgument(var3, var1.getCursor(), var4);
      var2.withArgument(this.name, var5);
      var2.withNode(this, var5.getRange());
   }

   public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) throws CommandSyntaxException {
      return this.customSuggestions == null ? this.type.listSuggestions(var1, var2) : this.customSuggestions.getSuggestions(var1, var2);
   }

   public RequiredArgumentBuilder<S, T> createBuilder() {
      RequiredArgumentBuilder var1 = RequiredArgumentBuilder.argument(this.name, this.type);
      var1.requires(this.getRequirement());
      var1.forward(this.getRedirect(), this.getRedirectModifier(), this.isFork());
      var1.suggests(this.customSuggestions);
      if (this.getCommand() != null) {
         var1.executes(this.getCommand());
      }

      return var1;
   }

   public boolean isValidInput(String var1) {
      try {
         StringReader var2 = new StringReader(var1);
         this.type.parse(var2);
         return !var2.canRead() || var2.peek() == ' ';
      } catch (CommandSyntaxException var3) {
         return false;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ArgumentCommandNode)) {
         return false;
      } else {
         ArgumentCommandNode var2 = (ArgumentCommandNode)var1;
         if (!this.name.equals(var2.name)) {
            return false;
         } else {
            return !this.type.equals(var2.type) ? false : super.equals(var1);
         }
      }
   }

   public int hashCode() {
      int var1 = this.name.hashCode();
      var1 = 31 * var1 + this.type.hashCode();
      return var1;
   }

   protected String getSortedKey() {
      return this.name;
   }

   public Collection<String> getExamples() {
      return this.type.getExamples();
   }

   public String toString() {
      return "<argument " + this.name + ":" + this.type + ">";
   }
}
