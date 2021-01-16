package it.unimi.dsi.fastutil.bytes;

import java.util.ListIterator;

public interface ByteListIterator extends ByteBidirectionalIterator, ListIterator<Byte> {
   default void set(byte var1) {
      throw new UnsupportedOperationException();
   }

   default void add(byte var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Byte var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Byte var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte next() {
      return ByteBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Byte previous() {
      return ByteBidirectionalIterator.super.previous();
   }
}
