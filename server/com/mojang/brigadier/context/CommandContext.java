package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContext<S> {
   private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap();
   private final S source;
   private final String input;
   private final Command<S> command;
   private final Map<String, ParsedArgument<S, ?>> arguments;
   private final CommandNode<S> rootNode;
   private final List<ParsedCommandNode<S>> nodes;
   private final StringRange range;
   private final CommandContext<S> child;
   private final RedirectModifier<S> modifier;
   private final boolean forks;

   public CommandContext(S var1, String var2, Map<String, ParsedArgument<S, ?>> var3, Command<S> var4, CommandNode<S> var5, List<ParsedCommandNode<S>> var6, StringRange var7, CommandContext<S> var8, RedirectModifier<S> var9, boolean var10) {
      super();
      this.source = var1;
      this.input = var2;
      this.arguments = var3;
      this.command = var4;
      this.rootNode = var5;
      this.nodes = var6;
      this.range = var7;
      this.child = var8;
      this.modifier = var9;
      this.forks = var10;
   }

   public CommandContext<S> copyFor(S var1) {
      return this.source == var1 ? this : new CommandContext(var1, this.input, this.arguments, this.command, this.rootNode, this.nodes, this.range, this.child, this.modifier, this.forks);
   }

   public CommandContext<S> getChild() {
      return this.child;
   }

   public CommandContext<S> getLastChild() {
      CommandContext var1;
      for(var1 = this; var1.getChild() != null; var1 = var1.getChild()) {
      }

      return var1;
   }

   public Command<S> getCommand() {
      return this.command;
   }

   public S getSource() {
      return this.source;
   }

   public <V> V getArgument(String var1, Class<V> var2) {
      ParsedArgument var3 = (ParsedArgument)this.arguments.get(var1);
      if (var3 == null) {
         throw new IllegalArgumentException("No such argument '" + var1 + "' exists on this command");
      } else {
         Object var4 = var3.getResult();
         if (((Class)PRIMITIVE_TO_WRAPPER.getOrDefault(var2, var2)).isAssignableFrom(var4.getClass())) {
            return var4;
         } else {
            throw new IllegalArgumentException("Argument '" + var1 + "' is defined as " + var4.getClass().getSimpleName() + ", not " + var2);
         }
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CommandContext)) {
         return false;
      } else {
         CommandContext var2 = (CommandContext)var1;
         if (!this.arguments.equals(var2.arguments)) {
            return false;
         } else if (!this.rootNode.equals(var2.rootNode)) {
            return false;
         } else if (this.nodes.size() == var2.nodes.size() && this.nodes.equals(var2.nodes)) {
            if (this.command != null) {
               if (!this.command.equals(var2.command)) {
                  return false;
               }
            } else if (var2.command != null) {
               return false;
            }

            if (!this.source.equals(var2.source)) {
               return false;
            } else {
               if (this.child != null) {
                  if (!this.child.equals(var2.child)) {
                     return false;
                  }
               } else if (var2.child != null) {
                  return false;
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = this.source.hashCode();
      var1 = 31 * var1 + this.arguments.hashCode();
      var1 = 31 * var1 + (this.command != null ? this.command.hashCode() : 0);
      var1 = 31 * var1 + this.rootNode.hashCode();
      var1 = 31 * var1 + this.nodes.hashCode();
      var1 = 31 * var1 + (this.child != null ? this.child.hashCode() : 0);
      return var1;
   }

   public RedirectModifier<S> getRedirectModifier() {
      return this.modifier;
   }

   public StringRange getRange() {
      return this.range;
   }

   public String getInput() {
      return this.input;
   }

   public CommandNode<S> getRootNode() {
      return this.rootNode;
   }

   public List<ParsedCommandNode<S>> getNodes() {
      return this.nodes;
   }

   public boolean hasNodes() {
      return !this.nodes.isEmpty();
   }

   public boolean isForked() {
      return this.forks;
   }

   static {
      PRIMITIVE_TO_WRAPPER.put(Boolean.TYPE, Boolean.class);
      PRIMITIVE_TO_WRAPPER.put(Byte.TYPE, Byte.class);
      PRIMITIVE_TO_WRAPPER.put(Short.TYPE, Short.class);
      PRIMITIVE_TO_WRAPPER.put(Character.TYPE, Character.class);
      PRIMITIVE_TO_WRAPPER.put(Integer.TYPE, Integer.class);
      PRIMITIVE_TO_WRAPPER.put(Long.TYPE, Long.class);
      PRIMITIVE_TO_WRAPPER.put(Float.TYPE, Float.class);
      PRIMITIVE_TO_WRAPPER.put(Double.TYPE, Double.class);
   }
}
