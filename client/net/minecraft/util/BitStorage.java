package net.minecraft.util;

import java.util.function.IntConsumer;
import org.apache.commons.lang3.Validate;

public class BitStorage {
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;

   public BitStorage(int var1, int var2) {
      this(var1, var2, new long[Mth.roundUp(var2 * var1, 64) / 64]);
   }

   public BitStorage(int var1, int var2, long[] var3) {
      super();
      Validate.inclusiveBetween(1L, 32L, (long)var1);
      this.size = var2;
      this.bits = var1;
      this.data = var3;
      this.mask = (1L << var1) - 1L;
      int var4 = Mth.roundUp(var2 * var1, 64) / 64;
      if (var3.length != var4) {
         throw new RuntimeException("Invalid length given for storage, got: " + var3.length + " but expected: " + var4);
      }
   }

   public int getAndSet(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      Validate.inclusiveBetween(0L, this.mask, (long)var2);
      int var3 = var1 * this.bits;
      int var4 = var3 >> 6;
      int var5 = (var1 + 1) * this.bits - 1 >> 6;
      int var6 = var3 ^ var4 << 6;
      byte var7 = 0;
      int var10 = var7 | (int)(this.data[var4] >>> var6 & this.mask);
      this.data[var4] = this.data[var4] & ~(this.mask << var6) | ((long)var2 & this.mask) << var6;
      if (var4 != var5) {
         int var8 = 64 - var6;
         int var9 = this.bits - var8;
         var10 |= (int)(this.data[var5] << var8 & this.mask);
         this.data[var5] = this.data[var5] >>> var9 << var9 | ((long)var2 & this.mask) >> var8;
      }

      return var10;
   }

   public void set(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      Validate.inclusiveBetween(0L, this.mask, (long)var2);
      int var3 = var1 * this.bits;
      int var4 = var3 >> 6;
      int var5 = (var1 + 1) * this.bits - 1 >> 6;
      int var6 = var3 ^ var4 << 6;
      this.data[var4] = this.data[var4] & ~(this.mask << var6) | ((long)var2 & this.mask) << var6;
      if (var4 != var5) {
         int var7 = 64 - var6;
         int var8 = this.bits - var7;
         this.data[var5] = this.data[var5] >>> var8 << var8 | ((long)var2 & this.mask) >> var7;
      }

   }

   public int get(int var1) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)var1);
      int var2 = var1 * this.bits;
      int var3 = var2 >> 6;
      int var4 = (var1 + 1) * this.bits - 1 >> 6;
      int var5 = var2 ^ var3 << 6;
      if (var3 == var4) {
         return (int)(this.data[var3] >>> var5 & this.mask);
      } else {
         int var6 = 64 - var5;
         return (int)((this.data[var3] >>> var5 | this.data[var4] << var6) & this.mask);
      }
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
      int var2 = this.data.length;
      if (var2 != 0) {
         int var3 = 0;
         long var4 = this.data[0];
         long var6 = var2 > 1 ? this.data[1] : 0L;

         for(int var8 = 0; var8 < this.size; ++var8) {
            int var9 = var8 * this.bits;
            int var10 = var9 >> 6;
            int var11 = (var8 + 1) * this.bits - 1 >> 6;
            int var12 = var9 ^ var10 << 6;
            if (var10 != var3) {
               var4 = var6;
               var6 = var10 + 1 < var2 ? this.data[var10 + 1] : 0L;
               var3 = var10;
            }

            if (var10 == var11) {
               var1.accept((int)(var4 >>> var12 & this.mask));
            } else {
               int var13 = 64 - var12;
               var1.accept((int)((var4 >>> var12 | var6 << var13) & this.mask));
            }
         }

      }
   }
}
