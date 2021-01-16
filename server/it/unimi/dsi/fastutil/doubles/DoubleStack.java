package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Stack;

public interface DoubleStack extends Stack<Double> {
   void push(double var1);

   double popDouble();

   double topDouble();

   double peekDouble(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Double var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double pop() {
      return this.popDouble();
   }

   /** @deprecated */
   @Deprecated
   default Double top() {
      return this.topDouble();
   }

   /** @deprecated */
   @Deprecated
   default Double peek(int var1) {
      return this.peekDouble(var1);
   }
}
