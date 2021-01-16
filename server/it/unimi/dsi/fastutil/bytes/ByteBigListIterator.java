package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface ByteBigListIterator extends ByteBidirectionalIterator, BigListIterator<Byte> {
   default void set(byte var1) {
      throw new UnsupportedOperationException();
   }

   default void add(byte var1) {
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

   default long skip(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasNext()) {
         this.nextByte();
      }

      return var1 - var3 - 1L;
   }

   default long back(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasPrevious()) {
         this.previousByte();
      }

      return var1 - var3 - 1L;
   }

   default int skip(int var1) {
      return SafeMath.safeLongToInt(this.skip((long)var1));
   }
}
