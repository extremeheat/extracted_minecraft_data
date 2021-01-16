package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class LiteralCommandNode<S> extends CommandNode<S> {
   private final String literal;

   public LiteralCommandNode(String var1, Command<S> var2, Predicate<S> var3, CommandNode<S> var4, RedirectModifier<S> var5, boolean var6) {
      super(var2, var3, var4, var5, var6);
      this.literal = var1;
   }

   public String getLiteral() {
      return this.literal;
   }

   public String getName() {
      return this.literal;
   }

   public void parse(StringReader var1, CommandContextBuilder<S> var2) throws CommandSyntaxException {
      int var3 = var1.getCursor();
      int var4 = this.parse(var1);
      if (var4 > -1) {
         var2.withNode(this, StringRange.between(var3, var4));
      } else {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(var1, this.literal);
      }
   }

   private int parse(StringReader var1) {
      int var2 = var1.getCursor();
      if (var1.canRead(this.literal.length())) {
         int var3 = var2 + this.literal.length();
         if (var1.getString().substring(var2, var3).equals(this.literal)) {
            var1.setCursor(var3);
            if (!var1.canRead() || var1.peek() == ' ') {
               return var3;
            }

            var1.setCursor(var2);
         }
      }

      return -1;
   }

   public CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return this.literal.toLowerCase().startsWith(var2.getRemaining().toLowerCase()) ? var2.suggest(this.literal).buildFuture() : Suggestions.empty();
   }

   public boolean isValidInput(String var1) {
      return this.parse(new StringReader(var1)) > -1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LiteralCommandNode)) {
         return false;
      } else {
         LiteralCommandNode var2 = (LiteralCommandNode)var1;
         return !this.literal.equals(var2.literal) ? false : super.equals(var1);
      }
   }

   public String getUsageText() {
      return this.literal;
   }

   public int hashCode() {
      int var1 = this.literal.hashCode();
      var1 = 31 * var1 + super.hashCode();
      return var1;
   }

   public LiteralArgumentBuilder<S> createBuilder() {
      LiteralArgumentBuilder var1 = LiteralArgumentBuilder.literal(this.literal);
      var1.requires(this.getRequirement());
      var1.forward(this.getRedirect(), this.getRedirectModifier(), this.isFork());
      if (this.getCommand() != null) {
         var1.executes(this.getCommand());
      }

      return var1;
   }

   protected String getSortedKey() {
      return this.literal;
   }

   public Collection<String> getExamples() {
      return Collections.singleton(this.literal);
   }

   public String toString() {
      return "<literal " + this.literal + ">";
   }
}
