package net.minecraft.util;

import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class SimpleBitStorage implements BitStorage {
   private static final int[] MAGIC = new int[]{-1, -1, 0, -2147483648, 0, 0, 1431655765, 1431655765, 0, -2147483648, 0, 1, 858993459, 858993459, 0, 715827882, 715827882, 0, 613566756, 613566756, 0, -2147483648, 0, 2, 477218588, 477218588, 0, 429496729, 429496729, 0, 390451572, 390451572, 0, 357913941, 357913941, 0, 330382099, 330382099, 0, 306783378, 306783378, 0, 286331153, 286331153, 0, -2147483648, 0, 3, 252645135, 252645135, 0, 238609294, 238609294, 0, 226050910, 226050910, 0, 214748364, 214748364, 0, 204522252, 204522252, 0, 195225786, 195225786, 0, 186737708, 186737708, 0, 178956970, 178956970, 0, 171798691, 171798691, 0, 165191049, 165191049, 0, 159072862, 159072862, 0, 153391689, 153391689, 0, 148102320, 148102320, 0, 143165576, 143165576, 0, 138547332, 138547332, 0, -2147483648, 0, 4, 130150524, 130150524, 0, 126322567, 126322567, 0, 122713351, 122713351, 0, 119304647, 119304647, 0, 116080197, 116080197, 0, 113025455, 113025455, 0, 110127366, 110127366, 0, 107374182, 107374182, 0, 104755299, 104755299, 0, 102261126, 102261126, 0, 99882960, 99882960, 0, 97612893, 97612893, 0, 95443717, 95443717, 0, 93368854, 93368854, 0, 91382282, 91382282, 0, 89478485, 89478485, 0, 87652393, 87652393, 0, 85899345, 85899345, 0, 84215045, 84215045, 0, 82595524, 82595524, 0, 81037118, 81037118, 0, 79536431, 79536431, 0, 78090314, 78090314, 0, 76695844, 76695844, 0, 75350303, 75350303, 0, 74051160, 74051160, 0, 72796055, 72796055, 0, 71582788, 71582788, 0, 70409299, 70409299, 0, 69273666, 69273666, 0, 68174084, 68174084, 0, -2147483648, 0, 5};
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;
   private final int valuesPerLong;
   private final int divideMul;
   private final int divideAdd;
   private final int divideShift;

   public SimpleBitStorage(int var1, int var2, int[] var3) {
      this(var1, var2);
      int var5 = 0;

      int var4;
      for(var4 = 0; var4 <= var2 - this.valuesPerLong; var4 += this.valuesPerLong) {
         long var6 = 0L;

         for(int var8 = this.valuesPerLong - 1; var8 >= 0; --var8) {
            var6 <<= var1;
            var6 |= (long)var3[var4 + var8] & this.mask;
         }

         this.data[var5++] = var6;
      }

      int var10 = var2 - var4;
      if (var10 > 0) {
         long var7 = 0L;

         for(int var9 = var10 - 1; var9 >= 0; --var9) {
            var7 <<= var1;
            var7 |= (long)var3[var4 + var9] & this.mask;
         }

         this.data[var5] = var7;
      }

   }

   public SimpleBitStorage(int var1, int var2) {
      this(var1, var2, (long[])null);
   }

   public SimpleBitStorage(int var1, int var2, @Nullable long[] var3) {
      super();
      Validate.inclusiveBetween(1L, 32L, (long)var1);
      this.size = var2;
      this.bits = var1;
      this.mask = (1L << var1) - 1L;
      this.valuesPerLong = (char)(64 / var1);
      int var4 = 3 * (this.valuesPerLong - 1);
      this.divideMul = MAGIC[var4 + 0];
      this.divideAdd = MAGIC[var4 + 1];
      this.divideShift = MAGIC[var4 + 2];
      int var5 = (var2 + this.valuesPerLong - 1) / this.valuesPerLong;
      if (var3 != null) {
         if (var3.length != var5) {
            throw new SimpleBitStorage.InitializationException("Invalid length given for storage, got: " + var3.length + " but expected: " + var5);
         }

         this.data = var3;
      } else {
         this.data = new long[var5];
      }

   }

   private int cellIndex(int var1) {
      long var2 = Integer.toUnsignedLong(this.divideMul);
      long var4 = Integer.toUnsignedLong(this.divideAdd);
      return (int)((long)var1 * var2 + var4 >> 32 >> this.divideShift);
   }

   public int getAndSet(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      Validate.inclusiveBetween(0L, this.mask, (long)var2);
      int var3 = this.cellIndex(var1);
      long var4 = this.data[var3];
      int var6 = (var1 - var3 * this.valuesPerLong) * this.bits;
      int var7 = (int)(var4 >> var6 & this.mask);
      this.data[var3] = var4 & ~(this.mask << var6) | ((long)var2 & this.mask) << var6;
      return var7;
   }

   public void set(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      Validate.inclusiveBetween(0L, this.mask, (long)var2);
      int var3 = this.cellIndex(var1);
      long var4 = this.data[var3];
      int var6 = (var1 - var3 * this.valuesPerLong) * this.bits;
      this.data[var3] = var4 & ~(this.mask << var6) | ((long)var2 & this.mask) << var6;
   }

   public int get(int var1) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      int var2 = this.cellIndex(var1);
      long var3 = this.data[var2];
      int var5 = (var1 - var2 * this.valuesPerLong) * this.bits;
      return (int)(var3 >> var5 & this.mask);
   }

   public long[] getRaw() {
      return this.data;
   }

   public int getSize() {
      return this.size;
   }

   public int getBits() {
      return this.bits;
   }

   public void getAll(IntConsumer var1) {
      int var2 = 0;
      long[] var3 = this.data;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         long var6 = var3[var5];

         for(int var8 = 0; var8 < this.valuesPerLong; ++var8) {
            var1.accept((int)(var6 & this.mask));
            var6 >>= this.bits;
            ++var2;
            if (var2 >= this.size) {
               return;
            }
         }
      }

   }

   public void unpack(int[] var1) {
      int var2 = this.data.length;
      int var3 = 0;

      int var4;
      long var5;
      int var7;
      for(var4 = 0; var4 < var2 - 1; ++var4) {
         var5 = this.data[var4];

         for(var7 = 0; var7 < this.valuesPerLong; ++var7) {
            var1[var3 + var7] = (int)(var5 & this.mask);
            var5 >>= this.bits;
         }

         var3 += this.valuesPerLong;
      }

      var4 = this.size - var3;
      if (var4 > 0) {
         var5 = this.data[var2 - 1];

         for(var7 = 0; var7 < var4; ++var7) {
            var1[var3 + var7] = (int)(var5 & this.mask);
            var5 >>= this.bits;
         }
      }

   }

   public BitStorage copy() {
      return new SimpleBitStorage(this.bits, this.size, (long[])this.data.clone());
   }

   public static class InitializationException extends RuntimeException {
      InitializationException(String var1) {
         super(var1);
      }
   }
}
