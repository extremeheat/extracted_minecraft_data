package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Stack;

public interface CharStack extends Stack<Character> {
   void push(char var1);

   char popChar();

   char topChar();

   char peekChar(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Character var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character pop() {
      return this.popChar();
   }

   /** @deprecated */
   @Deprecated
   default Character top() {
      return this.topChar();
   }

   /** @deprecated */
   @Deprecated
   default Character peek(int var1) {
      return this.peekChar(var1);
   }
}
