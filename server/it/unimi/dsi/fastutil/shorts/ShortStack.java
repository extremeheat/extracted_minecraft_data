package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Stack;

public interface ShortStack extends Stack<Short> {
   void push(short var1);

   short popShort();

   short topShort();

   short peekShort(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Short var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short pop() {
      return this.popShort();
   }

   /** @deprecated */
   @Deprecated
   default Short top() {
      return this.topShort();
   }

   /** @deprecated */
   @Deprecated
   default Short peek(int var1) {
      return this.peekShort(var1);
   }
}
