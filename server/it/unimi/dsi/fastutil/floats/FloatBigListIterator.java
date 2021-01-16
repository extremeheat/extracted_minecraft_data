package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface FloatBigListIterator extends FloatBidirectionalIterator, BigListIterator<Float> {
   default void set(float var1) {
      throw new UnsupportedOperationException();
   }

   default void add(float var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Float var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Float var1) {
      this.add(var1);
   }

   default long skip(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasNext()) {
         this.nextFloat();
      }

      return var1 - var3 - 1L;
   }

   default long back(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasPrevious()) {
         this.previousFloat();
      }

      return var1 - var3 - 1L;
   }

   default int skip(int var1) {
      return SafeMath.safeLongToInt(this.skip((long)var1));
   }
}
