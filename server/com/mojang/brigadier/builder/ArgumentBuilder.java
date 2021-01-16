package com.mojang.brigadier.builder;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

public abstract class ArgumentBuilder<S, T extends ArgumentBuilder<S, T>> {
   private final RootCommandNode<S> arguments = new RootCommandNode();
   private Command<S> command;
   private Predicate<S> requirement = (var0) -> {
      return true;
   };
   private CommandNode<S> target;
   private RedirectModifier<S> modifier = null;
   private boolean forks;

   public ArgumentBuilder() {
      super();
   }

   protected abstract T getThis();

   public T then(ArgumentBuilder<S, ?> var1) {
      if (this.target != null) {
         throw new IllegalStateException("Cannot add children to a redirected node");
      } else {
         this.arguments.addChild(var1.build());
         return this.getThis();
      }
   }

   public T then(CommandNode<S> var1) {
      if (this.target != null) {
         throw new IllegalStateException("Cannot add children to a redirected node");
      } else {
         this.arguments.addChild(var1);
         return this.getThis();
      }
   }

   public Collection<CommandNode<S>> getArguments() {
      return this.arguments.getChildren();
   }

   public T executes(Command<S> var1) {
      this.command = var1;
      return this.getThis();
   }

   public Command<S> getCommand() {
      return this.command;
   }

   public T requires(Predicate<S> var1) {
      this.requirement = var1;
      return this.getThis();
   }

   public Predicate<S> getRequirement() {
      return this.requirement;
   }

   public T redirect(CommandNode<S> var1) {
      return this.forward(var1, (RedirectModifier)null, false);
   }

   public T redirect(CommandNode<S> var1, SingleRedirectModifier<S> var2) {
      return this.forward(var1, var2 == null ? null : (var1x) -> {
         return Collections.singleton(var2.apply(var1x));
      }, false);
   }

   public T fork(CommandNode<S> var1, RedirectModifier<S> var2) {
      return this.forward(var1, var2, true);
   }

   public T forward(CommandNode<S> var1, RedirectModifier<S> var2, boolean var3) {
      if (!this.arguments.getChildren().isEmpty()) {
         throw new IllegalStateException("Cannot forward a node with children");
      } else {
         this.target = var1;
         this.modifier = var2;
         this.forks = var3;
         return this.getThis();
      }
   }

   public CommandNode<S> getRedirect() {
      return this.target;
   }

   public RedirectModifier<S> getRedirectModifier() {
      return this.modifier;
   }

   public boolean isFork() {
      return this.forks;
   }

   public abstract CommandNode<S> build();
}
