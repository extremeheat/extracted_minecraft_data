package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Iterator;

public class LiteralArgumentBuilder<S> extends ArgumentBuilder<S, LiteralArgumentBuilder<S>> {
   private final String literal;

   protected LiteralArgumentBuilder(String var1) {
      super();
      this.literal = var1;
   }

   public static <S> LiteralArgumentBuilder<S> literal(String var0) {
      return new LiteralArgumentBuilder(var0);
   }

   protected LiteralArgumentBuilder<S> getThis() {
      return this;
   }

   public String getLiteral() {
      return this.literal;
   }

   public LiteralCommandNode<S> build() {
      LiteralCommandNode var1 = new LiteralCommandNode(this.getLiteral(), this.getCommand(), this.getRequirement(), this.getRedirect(), this.getRedirectModifier(), this.isFork());
      Iterator var2 = this.getArguments().iterator();

      while(var2.hasNext()) {
         CommandNode var3 = (CommandNode)var2.next();
         var1.addChild(var3);
      }

      return var1;
   }
}
