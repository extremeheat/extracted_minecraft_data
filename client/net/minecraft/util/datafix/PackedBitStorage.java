package net.minecraft.util.datafix;

import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;

public class PackedBitStorage {
   private static final int BIT_TO_LONG_SHIFT = 6;
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;

   public PackedBitStorage(int var1, int var2) {
      this(var1, var2, new long[Mth.roundToward(var2 * var1, 64) / 64]);
   }

   public PackedBitStorage(int var1, int var2, long[] var3) {
      super();
      Validate.inclusiveBetween(1L, 32L, (long)var1);
      this.size = var2;
      this.bits = var1;
      this.data = var3;
      this.mask = (1L << var1) - 1L;
      int var4 = Mth.roundToward(var2 * var1, 64) / 64;
      if (var3.length != var4) {
         throw new IllegalArgumentException("Invalid length given for storage, got: " + var3.length + " but expected: " + var4);
      }
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

   public int getBits() {
      return this.bits;
   }
}
