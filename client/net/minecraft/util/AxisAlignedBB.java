package net.minecraft.util;

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

   public AxisAlignedBB(BlockPos var1, BlockPos var2) {
      super();
      this.field_72340_a = (double)var1.func_177958_n();
      this.field_72338_b = (double)var1.func_177956_o();
      this.field_72339_c = (double)var1.func_177952_p();
      this.field_72336_d = (double)var2.func_177958_n();
      this.field_72337_e = (double)var2.func_177956_o();
      this.field_72334_f = (double)var2.func_177952_p();
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

   public AxisAlignedBB func_111270_a(AxisAlignedBB var1) {
      double var2 = Math.min(this.field_72340_a, var1.field_72340_a);
      double var4 = Math.min(this.field_72338_b, var1.field_72338_b);
      double var6 = Math.min(this.field_72339_c, var1.field_72339_c);
      double var8 = Math.max(this.field_72336_d, var1.field_72336_d);
      double var10 = Math.max(this.field_72337_e, var1.field_72337_e);
      double var12 = Math.max(this.field_72334_f, var1.field_72334_f);
      return new AxisAlignedBB(var2, var4, var6, var8, var10, var12);
   }

   public static AxisAlignedBB func_178781_a(double var0, double var2, double var4, double var6, double var8, double var10) {
      double var12 = Math.min(var0, var6);
      double var14 = Math.min(var2, var8);
      double var16 = Math.min(var4, var10);
      double var18 = Math.max(var0, var6);
      double var20 = Math.max(var2, var8);
      double var22 = Math.max(var4, var10);
      return new AxisAlignedBB(var12, var14, var16, var18, var20, var22);
   }

   public AxisAlignedBB func_72317_d(double var1, double var3, double var5) {
      return new AxisAlignedBB(this.field_72340_a + var1, this.field_72338_b + var3, this.field_72339_c + var5, this.field_72336_d + var1, this.field_72337_e + var3, this.field_72334_f + var5);
   }

   public double func_72316_a(AxisAlignedBB var1, double var2) {
      if (var1.field_72337_e > this.field_72338_b && var1.field_72338_b < this.field_72337_e && var1.field_72334_f > this.field_72339_c && var1.field_72339_c < this.field_72334_f) {
         double var4;
         if (var2 > 0.0D && var1.field_72336_d <= this.field_72340_a) {
            var4 = this.field_72340_a - var1.field_72336_d;
            if (var4 < var2) {
               var2 = var4;
            }
         } else if (var2 < 0.0D && var1.field_72340_a >= this.field_72336_d) {
            var4 = this.field_72336_d - var1.field_72340_a;
            if (var4 > var2) {
               var2 = var4;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public double func_72323_b(AxisAlignedBB var1, double var2) {
      if (var1.field_72336_d > this.field_72340_a && var1.field_72340_a < this.field_72336_d && var1.field_72334_f > this.field_72339_c && var1.field_72339_c < this.field_72334_f) {
         double var4;
         if (var2 > 0.0D && var1.field_72337_e <= this.field_72338_b) {
            var4 = this.field_72338_b - var1.field_72337_e;
            if (var4 < var2) {
               var2 = var4;
            }
         } else if (var2 < 0.0D && var1.field_72338_b >= this.field_72337_e) {
            var4 = this.field_72337_e - var1.field_72338_b;
            if (var4 > var2) {
               var2 = var4;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public double func_72322_c(AxisAlignedBB var1, double var2) {
      if (var1.field_72336_d > this.field_72340_a && var1.field_72340_a < this.field_72336_d && var1.field_72337_e > this.field_72338_b && var1.field_72338_b < this.field_72337_e) {
         double var4;
         if (var2 > 0.0D && var1.field_72334_f <= this.field_72339_c) {
            var4 = this.field_72339_c - var1.field_72334_f;
            if (var4 < var2) {
               var2 = var4;
            }
         } else if (var2 < 0.0D && var1.field_72339_c >= this.field_72334_f) {
            var4 = this.field_72334_f - var1.field_72339_c;
            if (var4 > var2) {
               var2 = var4;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public boolean func_72326_a(AxisAlignedBB var1) {
      if (var1.field_72336_d > this.field_72340_a && var1.field_72340_a < this.field_72336_d) {
         if (var1.field_72337_e > this.field_72338_b && var1.field_72338_b < this.field_72337_e) {
            return var1.field_72334_f > this.field_72339_c && var1.field_72339_c < this.field_72334_f;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean func_72318_a(Vec3 var1) {
      if (var1.field_72450_a > this.field_72340_a && var1.field_72450_a < this.field_72336_d) {
         if (var1.field_72448_b > this.field_72338_b && var1.field_72448_b < this.field_72337_e) {
            return var1.field_72449_c > this.field_72339_c && var1.field_72449_c < this.field_72334_f;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public double func_72320_b() {
      double var1 = this.field_72336_d - this.field_72340_a;
      double var3 = this.field_72337_e - this.field_72338_b;
      double var5 = this.field_72334_f - this.field_72339_c;
      return (var1 + var3 + var5) / 3.0D;
   }

   public AxisAlignedBB func_72331_e(double var1, double var3, double var5) {
      double var7 = this.field_72340_a + var1;
      double var9 = this.field_72338_b + var3;
      double var11 = this.field_72339_c + var5;
      double var13 = this.field_72336_d - var1;
      double var15 = this.field_72337_e - var3;
      double var17 = this.field_72334_f - var5;
      return new AxisAlignedBB(var7, var9, var11, var13, var15, var17);
   }

   public MovingObjectPosition func_72327_a(Vec3 var1, Vec3 var2) {
      Vec3 var3 = var1.func_72429_b(var2, this.field_72340_a);
      Vec3 var4 = var1.func_72429_b(var2, this.field_72336_d);
      Vec3 var5 = var1.func_72435_c(var2, this.field_72338_b);
      Vec3 var6 = var1.func_72435_c(var2, this.field_72337_e);
      Vec3 var7 = var1.func_72434_d(var2, this.field_72339_c);
      Vec3 var8 = var1.func_72434_d(var2, this.field_72334_f);
      if (!this.func_72333_b(var3)) {
         var3 = null;
      }

      if (!this.func_72333_b(var4)) {
         var4 = null;
      }

      if (!this.func_72315_c(var5)) {
         var5 = null;
      }

      if (!this.func_72315_c(var6)) {
         var6 = null;
      }

      if (!this.func_72319_d(var7)) {
         var7 = null;
      }

      if (!this.func_72319_d(var8)) {
         var8 = null;
      }

      Vec3 var9 = null;
      if (var3 != null) {
         var9 = var3;
      }

      if (var4 != null && (var9 == null || var1.func_72436_e(var4) < var1.func_72436_e(var9))) {
         var9 = var4;
      }

      if (var5 != null && (var9 == null || var1.func_72436_e(var5) < var1.func_72436_e(var9))) {
         var9 = var5;
      }

      if (var6 != null && (var9 == null || var1.func_72436_e(var6) < var1.func_72436_e(var9))) {
         var9 = var6;
      }

      if (var7 != null && (var9 == null || var1.func_72436_e(var7) < var1.func_72436_e(var9))) {
         var9 = var7;
      }

      if (var8 != null && (var9 == null || var1.func_72436_e(var8) < var1.func_72436_e(var9))) {
         var9 = var8;
      }

      if (var9 == null) {
         return null;
      } else {
         EnumFacing var10 = null;
         if (var9 == var3) {
            var10 = EnumFacing.WEST;
         } else if (var9 == var4) {
            var10 = EnumFacing.EAST;
         } else if (var9 == var5) {
            var10 = EnumFacing.DOWN;
         } else if (var9 == var6) {
            var10 = EnumFacing.UP;
         } else if (var9 == var7) {
            var10 = EnumFacing.NORTH;
         } else {
            var10 = EnumFacing.SOUTH;
         }

         return new MovingObjectPosition(var9, var10);
      }
   }

   private boolean func_72333_b(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72448_b >= this.field_72338_b && var1.field_72448_b <= this.field_72337_e && var1.field_72449_c >= this.field_72339_c && var1.field_72449_c <= this.field_72334_f;
      }
   }

   private boolean func_72315_c(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_72340_a && var1.field_72450_a <= this.field_72336_d && var1.field_72449_c >= this.field_72339_c && var1.field_72449_c <= this.field_72334_f;
      }
   }

   private boolean func_72319_d(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_72340_a && var1.field_72450_a <= this.field_72336_d && var1.field_72448_b >= this.field_72338_b && var1.field_72448_b <= this.field_72337_e;
      }
   }

   public String toString() {
      return "box[" + this.field_72340_a + ", " + this.field_72338_b + ", " + this.field_72339_c + " -> " + this.field_72336_d + ", " + this.field_72337_e + ", " + this.field_72334_f + "]";
   }

   public boolean func_181656_b() {
      return Double.isNaN(this.field_72340_a) || Double.isNaN(this.field_72338_b) || Double.isNaN(this.field_72339_c) || Double.isNaN(this.field_72336_d) || Double.isNaN(this.field_72337_e) || Double.isNaN(this.field_72334_f);
   }
}
