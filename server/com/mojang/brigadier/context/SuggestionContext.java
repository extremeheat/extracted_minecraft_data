package com.mojang.brigadier.context;

import com.mojang.brigadier.tree.CommandNode;

public class SuggestionContext<S> {
   public final CommandNode<S> parent;
   public final int startPos;

   public SuggestionContext(CommandNode<S> var1, int var2) {
      super();
      this.parent = var1;
      this.startPos = var2;
   }
}
