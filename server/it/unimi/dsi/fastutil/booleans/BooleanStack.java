package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Stack;

public interface BooleanStack extends Stack<Boolean> {
   void push(boolean var1);

   boolean popBoolean();

   boolean topBoolean();

   boolean peekBoolean(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Boolean var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean pop() {
      return this.popBoolean();
   }

   /** @deprecated */
   @Deprecated
   default Boolean top() {
      return this.topBoolean();
   }

   /** @deprecated */
   @Deprecated
   default Boolean peek(int var1) {
      return this.peekBoolean(var1);
   }
}
