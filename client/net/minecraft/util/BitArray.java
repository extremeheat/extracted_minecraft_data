package net.minecraft.util;

import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
   private final long[] field_188145_a;
   private final int field_188146_b;
   private final long field_188147_c;
   private final int field_188148_d;

   public BitArray(int var1, int var2) {
      this(var1, var2, new long[MathHelper.func_154354_b(var2 * var1, 64) / 64]);
   }

   public BitArray(int var1, int var2, long[] var3) {
      super();
      Validate.inclusiveBetween(1L, 32L, (long)var1);
      this.field_188148_d = var2;
      this.field_188146_b = var1;
      this.field_188145_a = var3;
      this.field_188147_c = (1L << var1) - 1L;
      int var4 = MathHelper.func_154354_b(var2 * var1, 64) / 64;
      if (var3.length != var4) {
         throw new RuntimeException("Invalid length given for storage, got: " + var3.length + " but expected: " + var4);
      }
   }

   public void func_188141_a(int var1, int var2) {
      Validate.inclusiveBetween(0L, (long)(this.field_188148_d - 1), (long)var1);
      Validate.inclusiveBetween(0L, this.field_188147_c, (long)var2);
      int var3 = var1 * this.field_188146_b;
      int var4 = var3 / 64;
      int var5 = ((var1 + 1) * this.field_188146_b - 1) / 64;
      int var6 = var3 % 64;
      this.field_188145_a[var4] = this.field_188145_a[var4] & ~(this.field_188147_c << var6) | ((long)var2 & this.field_188147_c) << var6;
      if (var4 != var5) {
         int var7 = 64 - var6;
         int var8 = this.field_188146_b - var7;
         this.field_188145_a[var5] = this.field_188145_a[var5] >>> var8 << var8 | ((long)var2 & this.field_188147_c) >> var7;
      }

   }

   public int func_188142_a(int var1) {
      Validate.inclusiveBetween(0L, (long)(this.field_188148_d - 1), (long)var1);
      int var2 = var1 * this.field_188146_b;
      int var3 = var2 / 64;
      int var4 = ((var1 + 1) * this.field_188146_b - 1) / 64;
      int var5 = var2 % 64;
      if (var3 == var4) {
         return (int)(this.field_188145_a[var3] >>> var5 & this.field_188147_c);
      } else {
         int var6 = 64 - var5;
         return (int)((this.field_188145_a[var3] >>> var5 | this.field_188145_a[var4] << var6) & this.field_188147_c);
      }
   }

   public long[] func_188143_a() {
      return this.field_188145_a;
   }

   public int func_188144_b() {
      return this.field_188148_d;
   }

   public int func_208535_c() {
      return this.field_188146_b;
   }
}
