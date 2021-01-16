package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Stack;

public interface ByteStack extends Stack<Byte> {
   void push(byte var1);

   byte popByte();

   byte topByte();

   byte peekByte(int var1);

   /** @deprecated */
   @Deprecated
   default void push(Byte var1) {
      this.push(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte pop() {
      return this.popByte();
   }

   /** @deprecated */
   @Deprecated
   default Byte top() {
      return this.topByte();
   }

   /** @deprecated */
   @Deprecated
   default Byte peek(int var1) {
      return this.peekByte(var1);
   }
}
