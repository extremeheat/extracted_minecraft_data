package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Collections;
import java.util.Map;

public class ParseResults<S> {
   private final CommandContextBuilder<S> context;
   private final Map<CommandNode<S>, CommandSyntaxException> exceptions;
   private final ImmutableStringReader reader;

   public ParseResults(CommandContextBuilder<S> var1, ImmutableStringReader var2, Map<CommandNode<S>, CommandSyntaxException> var3) {
      super();
      this.context = var1;
      this.reader = var2;
      this.exceptions = var3;
   }

   public ParseResults(CommandContextBuilder<S> var1) {
      this(var1, new StringReader(""), Collections.emptyMap());
   }

   public CommandContextBuilder<S> getContext() {
      return this.context;
   }

   public ImmutableStringReader getReader() {
      return this.reader;
   }

   public Map<CommandNode<S>, CommandSyntaxException> getExceptions() {
      return this.exceptions;
   }
}
