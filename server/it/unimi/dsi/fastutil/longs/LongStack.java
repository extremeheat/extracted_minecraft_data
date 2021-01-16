package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Stack;

public interface LongStack extends Stack<Long> {
   void push(long var1);

   long popLong();

   long topLong();

   long peekLong(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Long var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long pop() {
      return this.popLong();
   }

   /** @deprecated */
   @Deprecated
   default Long top() {
      return this.topLong();
   }

   /** @deprecated */
   @Deprecated
   default Long peek(int var1) {
      return this.peekLong(var1);
   }
}
