package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Stack;

public interface FloatStack extends Stack<Float> {
   void push(float var1);

   float popFloat();

   float topFloat();

   float peekFloat(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Float var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float pop() {
      return this.popFloat();
   }

   /** @deprecated */
   @Deprecated
   default Float top() {
      return this.topFloat();
   }

   /** @deprecated */
   @Deprecated
   default Float peek(int var1) {
      return this.peekFloat(var1);
   }
}
