package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Iterator;

public class RequiredArgumentBuilder<S, T> extends ArgumentBuilder<S, RequiredArgumentBuilder<S, T>> {
   private final String name;
   private final ArgumentType<T> type;
   private SuggestionProvider<S> suggestionsProvider = null;

   private RequiredArgumentBuilder(String var1, ArgumentType<T> var2) {
      super();
      this.name = var1;
      this.type = var2;
   }

   public static <S, T> RequiredArgumentBuilder<S, T> argument(String var0, ArgumentType<T> var1) {
      return new RequiredArgumentBuilder(var0, var1);
   }

   public RequiredArgumentBuilder<S, T> suggests(SuggestionProvider<S> var1) {
      this.suggestionsProvider = var1;
      return this.getThis();
   }

   public SuggestionProvider<S> getSuggestionsProvider() {
      return this.suggestionsProvider;
   }

   protected RequiredArgumentBuilder<S, T> getThis() {
      return this;
   }

   public ArgumentType<T> getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public ArgumentCommandNode<S, T> build() {
      ArgumentCommandNode var1 = new ArgumentCommandNode(this.getName(), this.getType(), this.getCommand(), this.getRequirement(), this.getRedirect(), this.getRedirectModifier(), this.isFork(), this.getSuggestionsProvider());
      Iterator var2 = this.getArguments().iterator();

      while(var2.hasNext()) {
         CommandNode var3 = (CommandNode)var2.next();
         var1.addChild(var3);
      }

      return var1;
   }
}
