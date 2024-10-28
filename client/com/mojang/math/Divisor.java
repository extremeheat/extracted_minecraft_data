package com.mojang.math;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.NoSuchElementException;

public class Divisor implements IntIterator {
   private final int denominator;
   private final int quotient;
   private final int mod;
   private int returnedParts;
   private int remainder;

   public Divisor(int var1, int var2) {
      super();
      this.denominator = var2;
      if (var2 > 0) {
         this.quotient = var1 / var2;
         this.mod = var1 % var2;
      } else {
         this.quotient = 0;
         this.mod = 0;
      }

   }

   public boolean hasNext() {
      return this.returnedParts < this.denominator;
   }

   public int nextInt() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int var1 = this.quotient;
         this.remainder += this.mod;
         if (this.remainder >= this.denominator) {
            this.remainder -= this.denominator;
            ++var1;
         }

         ++this.returnedParts;
         return var1;
      }
   }

   @VisibleForTesting
   public static Iterable<Integer> asIterable(int var0, int var1) {
      return () -> {
         return new Divisor(var0, var1);
      };
   }
}
