package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Stack;

public interface IntStack extends Stack<Integer> {
   void push(int var1);

   int popInt();

   int topInt();

   int peekInt(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Integer var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer pop() {
      return this.popInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer top() {
      return this.topInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer peek(int var1) {
      return this.peekInt(var1);
   }
}
