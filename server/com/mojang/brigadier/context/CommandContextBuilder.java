package com.mojang.brigadier.context;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandContextBuilder<S> {
   private final Map<String, ParsedArgument<S, ?>> arguments = new LinkedHashMap();
   private final CommandNode<S> rootNode;
   private final List<ParsedCommandNode<S>> nodes = new ArrayList();
   private final CommandDispatcher<S> dispatcher;
   private S source;
   private Command<S> command;
   private CommandContextBuilder<S> child;
   private StringRange range;
   private RedirectModifier<S> modifier = null;
   private boolean forks;

   public CommandContextBuilder(CommandDispatcher<S> var1, S var2, CommandNode<S> var3, int var4) {
      super();
      this.rootNode = var3;
      this.dispatcher = var1;
      this.source = var2;
      this.range = StringRange.at(var4);
   }

   public CommandContextBuilder<S> withSource(S var1) {
      this.source = var1;
      return this;
   }

   public S getSource() {
      return this.source;
   }

   public CommandNode<S> getRootNode() {
      return this.rootNode;
   }

   public CommandContextBuilder<S> withArgument(String var1, ParsedArgument<S, ?> var2) {
      this.arguments.put(var1, var2);
      return this;
   }

   public Map<String, ParsedArgument<S, ?>> getArguments() {
      return this.arguments;
   }

   public CommandContextBuilder<S> withCommand(Command<S> var1) {
      this.command = var1;
      return this;
   }

   public CommandContextBuilder<S> withNode(CommandNode<S> var1, StringRange var2) {
      this.nodes.add(new ParsedCommandNode(var1, var2));
      this.range = StringRange.encompassing(this.range, var2);
      this.modifier = var1.getRedirectModifier();
      this.forks = var1.isFork();
      return this;
   }

   public CommandContextBuilder<S> copy() {
      CommandContextBuilder var1 = new CommandContextBuilder(this.dispatcher, this.source, this.rootNode, this.range.getStart());
      var1.command = this.command;
      var1.arguments.putAll(this.arguments);
      var1.nodes.addAll(this.nodes);
      var1.child = this.child;
      var1.range = this.range;
      var1.forks = this.forks;
      return var1;
   }

   public CommandContextBuilder<S> withChild(CommandContextBuilder<S> var1) {
      this.child = var1;
      return this;
   }

   public CommandContextBuilder<S> getChild() {
      return this.child;
   }

   public CommandContextBuilder<S> getLastChild() {
      CommandContextBuilder var1;
      for(var1 = this; var1.getChild() != null; var1 = var1.getChild()) {
      }

      return var1;
   }

   public Command<S> getCommand() {
      return this.command;
   }

   public List<ParsedCommandNode<S>> getNodes() {
      return this.nodes;
   }

   public CommandContext<S> build(String var1) {
      return new CommandContext(this.source, var1, this.arguments, this.command, this.rootNode, this.nodes, this.range, this.child == null ? null : this.child.build(var1), this.modifier, this.forks);
   }

   public CommandDispatcher<S> getDispatcher() {
      return this.dispatcher;
   }

   public StringRange getRange() {
      return this.range;
   }

   public SuggestionContext<S> findSuggestionContext(int var1) {
      if (this.range.getStart() <= var1) {
         if (this.range.getEnd() < var1) {
            if (this.child != null) {
               return this.child.findSuggestionContext(var1);
            } else if (!this.nodes.isEmpty()) {
               ParsedCommandNode var6 = (ParsedCommandNode)this.nodes.get(this.nodes.size() - 1);
               return new SuggestionContext(var6.getNode(), var6.getRange().getEnd() + 1);
            } else {
               return new SuggestionContext(this.rootNode, this.range.getStart());
            }
         } else {
            CommandNode var2 = this.rootNode;

            ParsedCommandNode var4;
            for(Iterator var3 = this.nodes.iterator(); var3.hasNext(); var2 = var4.getNode()) {
               var4 = (ParsedCommandNode)var3.next();
               StringRange var5 = var4.getRange();
               if (var5.getStart() <= var1 && var1 <= var5.getEnd()) {
                  return new SuggestionContext(var2, var5.getStart());
               }
            }

            if (var2 == null) {
               throw new IllegalStateException("Can't find node before cursor");
            } else {
               return new SuggestionContext(var2, this.range.getStart());
            }
         }
      } else {
         throw new IllegalStateException("Can't find node before cursor");
      }
   }
}
