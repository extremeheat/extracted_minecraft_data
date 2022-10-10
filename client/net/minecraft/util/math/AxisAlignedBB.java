package net.minecraft.util.math;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;

public class AxisAlignedBB {
   public final double field_72340_a;
   public final double field_72338_b;
   public final double field_72339_c;
   public final double field_72336_d;
   public final double field_72337_e;
   public final double field_72334_f;

   public AxisAlignedBB(double var1, double var3, double var5, double var7, double var9, double var11) {
      super();
      this.field_72340_a = Math.min(var1, var7);
      this.field_72338_b = Math.min(var3, var9);
      this.field_72339_c = Math.min(var5, var11);
      this.field_72336_d = Math.max(var1, var7);
      this.field_72337_e = Math.max(var3, var9);
      this.field_72334_f = Math.max(var5, var11);
   }

   public AxisAlignedBB(BlockPos var1) {
      this((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p(), (double)(var1.func_177958_n() + 1), (double)(var1.func_177956_o() + 1), (double)(var1.func_177952_p() + 1));
   }

   public AxisAlignedBB(BlockPos var1, BlockPos var2) {
      this((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p(), (double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p());
   }

   public AxisAlignedBB(Vec3d var1, Vec3d var2) {
      this(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, var2.field_72450_a, var2.field_72448_b, var2.field_72449_c);
   }

   public double func_197745_a(EnumFacing.Axis var1) {
      return var1.func_196051_a(this.field_72340_a, this.field_72338_b, this.field_72339_c);
   }

   public double func_197742_b(EnumFacing.Axis var1) {
      return var1.func_196051_a(this.field_72336_d, this.field_72337_e, this.field_72334_f);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AxisAlignedBB)) {
         return false;
      } else {
         AxisAlignedBB var2 = (AxisAlignedBB)var1;
         if (Double.compare(var2.field_72340_a, this.field_72340_a) != 0) {
            return false;
         } else if (Double.compare(var2.field_72338_b, this.field_72338_b) != 0) {
            return false;
         } else if (Double.compare(var2.field_72339_c, this.field_72339_c) != 0) {
            return false;
         } else if (Double.compare(var2.field_72336_d, this.field_72336_d) != 0) {
            return false;
         } else if (Double.compare(var2.field_72337_e, this.field_72337_e) != 0) {
            return false;
         } else {
            return Double.compare(var2.field_72334_f, this.field_72334_f) == 0;
         }
      }
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.field_72340_a);
      int var3 = (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.field_72338_b);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.field_72339_c);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.field_72336_d);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.field_72337_e);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.field_72334_f);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      return var3;
   }

   public AxisAlignedBB func_191195_a(double var1, double var3, double var5) {
      double var7 = this.field_72340_a;
      double var9 = this.field_72338_b;
      double var11 = this.field_72339_c;
      double var13 = this.field_72336_d;
      double var15 = this.field_72337_e;
      double var17 = this.field_72334_f;
      if (var1 < 0.0D) {
         var7 -= var1;
      } else if (var1 > 0.0D) {
         var13 -= var1;
      }

      if (var3 < 0.0D) {
         var9 -= var3;
      } else if (var3 > 0.0D) {
         var15 -= var3;
      }

      if (var5 < 0.0D) {
         var11 -= var5;
      } else if (var5 > 0.0D) {
         var17 -= var5;
      }

      return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_72321_a(double var1, double var3, double var5) {
      double var7 = this.field_72340_a;
      double var9 = this.field_72338_b;
      double var11 = this.field_72339_c;
      double var13 = this.field_72336_d;
      double var15 = this.field_72337_e;
      double var17 = this.field_72334_f;
      if (var1 < 0.0D) {
         var7 += var1;
      } else if (var1 > 0.0D) {
         var13 += var1;
      }

      if (var3 < 0.0D) {
         var9 += var3;
      } else if (var3 > 0.0D) {
         var15 += var3;
      }

      if (var5 < 0.0D) {
         var11 += var5;
      } else if (var5 > 0.0D) {
         var17 += var5;
      }

      return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_72314_b(double var1, double var3, double var5) {
      double var7 = this.field_72340_a - var1;
      double var9 = this.field_72338_b - var3;
      double var11 = this.field_72339_c - var5;
      double var13 = this.field_72336_d + var1;
      double var15 = this.field_72337_e + var3;
      double var17 = this.field_72334_f + var5;
      return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_186662_g(double var1) {
      return this.func_72314_b(var1, var1, var1);
   }

   public AxisAlignedBB func_191500_a(AxisAlignedBB var1) {
      double var2 = Math.max(this.field_72340_a, var1.field_72340_a);
      double var4 = Math.max(this.field_72338_b, var1.field_72338_b);
      double var6 = Math.max(this.field_72339_c, var1.field_72339_c);
      double var8 = Math.min(this.field_72336_d, var1.field_72336_d);
      double var10 = Math.min(this.field_72337_e, var1.field_72337_e);
      double var12 = Math.min(this.field_72334_f, var1.field_72334_f);
      return new AxisAlignedBB(var2, var4, var6, var8, var10, var12);
   }

   public AxisAlignedBB func_111270_a(AxisAlignedBB var1) {
      double var2 = Math.min(this.field_72340_a, var1.field_72340_a);
      double var4 = Math.min(this.field_72338_b, var1.field_72338_b);
      double var6 = Math.min(this.field_72339_c, var1.field_72339_c);
      double var8 = Math.max(this.field_72336_d, var1.field_72336_d);
      double var10 = Math.max(this.field_72337_e, var1.field_72337_e);
      double var12 = Math.max(this.field_72334_f, var1.field_72334_f);
      return new AxisAlignedBB(var2, var4, var6, var8, var10, var12);
   }

   public AxisAlignedBB func_72317_d(double var1, double var3, double var5) {
      return new AxisAlignedBB(this.field_72340_a + var1, this.field_72338_b + var3, this.field_72339_c + var5, this.field_72336_d + var1, this.field_72337_e + var3, this.field_72334_f + var5);
   }

   public AxisAlignedBB func_186670_a(BlockPos var1) {
      return new AxisAlignedBB(this.field_72340_a + (double)var1.func_177958_n(), this.field_72338_b + (double)var1.func_177956_o(), this.field_72339_c + (double)var1.func_177952_p(), this.field_72336_d + (double)var1.func_177958_n(), this.field_72337_e + (double)var1.func_177956_o(), this.field_72334_f + (double)var1.func_177952_p());
   }

   public AxisAlignedBB func_191194_a(Vec3d var1) {
      return this.func_72317_d(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public boolean func_72326_a(AxisAlignedBB var1) {
      return this.func_186668_a(var1.field_72340_a, var1.field_72338_b, var1.field_72339_c, var1.field_72336_d, var1.field_72337_e, var1.field_72334_f);
   }

   public boolean func_186668_a(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.field_72340_a < var7 && this.field_72336_d > var1 && this.field_72338_b < var9 && this.field_72337_e > var3 && this.field_72339_c < var11 && this.field_72334_f > var5;
   }

   public boolean func_189973_a(Vec3d var1, Vec3d var2) {
      return this.func_186668_a(Math.min(var1.field_72450_a, var2.field_72450_a), Math.min(var1.field_72448_b, var2.field_72448_b), Math.min(var1.field_72449_c, var2.field_72449_c), Math.max(var1.field_72450_a, var2.field_72450_a), Math.max(var1.field_72448_b, var2.field_72448_b), Math.max(var1.field_72449_c, var2.field_72449_c));
   }

   public boolean func_72318_a(Vec3d var1) {
      return this.func_197744_e(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public boolean func_197744_e(double var1, double var3, double var5) {
      return var1 >= this.field_72340_a && var1 < this.field_72336_d && var3 >= this.field_72338_b && var3 < this.field_72337_e && var5 >= this.field_72339_c && var5 < this.field_72334_f;
   }

   public double func_72320_b() {
      double var1 = this.field_72336_d - this.field_72340_a;
      double var3 = this.field_72337_e - this.field_72338_b;
      double var5 = this.field_72334_f - this.field_72339_c;
      return (var1 + var3 + var5) / 3.0D;
   }

   public AxisAlignedBB func_211539_f(double var1, double var3, double var5) {
      return this.func_72314_b(-var1, -var3, -var5);
   }

   public AxisAlignedBB func_186664_h(double var1) {
      return this.func_186662_g(-var1);
   }

   @Nullable
   public RayTraceResult func_72327_a(Vec3d var1, Vec3d var2) {
      return this.func_197739_a(var1, var2, (BlockPos)null);
   }

   @Nullable
   public RayTraceResult func_197739_a(Vec3d var1, Vec3d var2, @Nullable BlockPos var3) {
      double[] var4 = new double[]{1.0D};
      EnumFacing var5 = null;
      double var6 = var2.field_72450_a - var1.field_72450_a;
      double var8 = var2.field_72448_b - var1.field_72448_b;
      double var10 = var2.field_72449_c - var1.field_72449_c;
      var5 = func_197741_a(var3 == null ? this : this.func_186670_a(var3), var1, var4, var5, var6, var8, var10);
      if (var5 == null) {
         return null;
      } else {
         double var12 = var4[0];
         return new RayTraceResult(var1.func_72441_c(var12 * var6, var12 * var8, var12 * var10), var5, var3 == null ? BlockPos.field_177992_a : var3);
      }
   }

   @Nullable
   public static RayTraceResult func_197743_a(Iterable<AxisAlignedBB> var0, Vec3d var1, Vec3d var2, BlockPos var3) {
      double[] var4 = new double[]{1.0D};
      EnumFacing var5 = null;
      double var6 = var2.field_72450_a - var1.field_72450_a;
      double var8 = var2.field_72448_b - var1.field_72448_b;
      double var10 = var2.field_72449_c - var1.field_72449_c;

      AxisAlignedBB var13;
      for(Iterator var12 = var0.iterator(); var12.hasNext(); var5 = func_197741_a(var13.func_186670_a(var3), var1, var4, var5, var6, var8, var10)) {
         var13 = (AxisAlignedBB)var12.next();
      }

      if (var5 == null) {
         return null;
      } else {
         double var14 = var4[0];
         return new RayTraceResult(var1.func_72441_c(var14 * var6, var14 * var8, var14 * var10), var5, var3);
      }
   }

   @Nullable
   private static EnumFacing func_197741_a(AxisAlignedBB var0, Vec3d var1, double[] var2, @Nullable EnumFacing var3, double var4, double var6, double var8) {
      if (var4 > 1.0E-7D) {
         var3 = func_197740_a(var2, var3, var4, var6, var8, var0.field_72340_a, var0.field_72338_b, var0.field_72337_e, var0.field_72339_c, var0.field_72334_f, EnumFacing.WEST, var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
      } else if (var4 < -1.0E-7D) {
         var3 = func_197740_a(var2, var3, var4, var6, var8, var0.field_72336_d, var0.field_72338_b, var0.field_72337_e, var0.field_72339_c, var0.field_72334_f, EnumFacing.EAST, var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
      }

      if (var6 > 1.0E-7D) {
         var3 = func_197740_a(var2, var3, var6, var8, var4, var0.field_72338_b, var0.field_72339_c, var0.field_72334_f, var0.field_72340_a, var0.field_72336_d, EnumFacing.DOWN, var1.field_72448_b, var1.field_72449_c, var1.field_72450_a);
      } else if (var6 < -1.0E-7D) {
         var3 = func_197740_a(var2, var3, var6, var8, var4, var0.field_72337_e, var0.field_72339_c, var0.field_72334_f, var0.field_72340_a, var0.field_72336_d, EnumFacing.UP, var1.field_72448_b, var1.field_72449_c, var1.field_72450_a);
      }

      if (var8 > 1.0E-7D) {
         var3 = func_197740_a(var2, var3, var8, var4, var6, var0.field_72339_c, var0.field_72340_a, var0.field_72336_d, var0.field_72338_b, var0.field_72337_e, EnumFacing.NORTH, var1.field_72449_c, var1.field_72450_a, var1.field_72448_b);
      } else if (var8 < -1.0E-7D) {
         var3 = func_197740_a(var2, var3, var8, var4, var6, var0.field_72334_f, var0.field_72340_a, var0.field_72336_d, var0.field_72338_b, var0.field_72337_e, EnumFacing.SOUTH, var1.field_72449_c, var1.field_72450_a, var1.field_72448_b);
      }

      return var3;
   }

   @Nullable
   private static EnumFacing func_197740_a(double[] var0, @Nullable EnumFacing var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, EnumFacing var18, double var19, double var21, double var23) {
      double var25 = (var8 - var19) / var2;
      double var27 = var21 + var25 * var4;
      double var29 = var23 + var25 * var6;
      if (0.0D < var25 && var25 < var0[0] && var10 - 1.0E-7D < var27 && var27 < var12 + 1.0E-7D && var14 - 1.0E-7D < var29 && var29 < var16 + 1.0E-7D) {
         var0[0] = var25;
         return var18;
      } else {
         return var1;
      }
   }

   public String toString() {
      return "box[" + this.field_72340_a + ", " + this.field_72338_b + ", " + this.field_72339_c + " -> " + this.field_72336_d + ", " + this.field_72337_e + ", " + this.field_72334_f + "]";
   }

   public boolean func_181656_b() {
      return Double.isNaN(this.field_72340_a) || Double.isNaN(this.field_72338_b) || Double.isNaN(this.field_72339_c) || Double.isNaN(this.field_72336_d) || Double.isNaN(this.field_72337_e) || Double.isNaN(this.field_72334_f);
   }

   public Vec3d func_189972_c() {
      return new Vec3d(this.field_72340_a + (this.field_72336_d - this.field_72340_a) * 0.5D, this.field_72338_b + (this.field_72337_e - this.field_72338_b) * 0.5D, this.field_72339_c + (this.field_72334_f - this.field_72339_c) * 0.5D);
   }
}
