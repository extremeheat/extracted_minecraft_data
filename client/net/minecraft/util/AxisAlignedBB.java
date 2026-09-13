package net.minecraft.util;

public class AxisAlignedBB {
   public double field_72340_a;
   public double field_72338_b;
   public double field_72339_c;
   public double field_72336_d;
   public double field_72337_e;
   public double field_72334_f;

   public static AxisAlignedBB func_72330_a(double var0, double var2, double var4, double var6, double var8, double var10) {
      return new AxisAlignedBB(var0, var2, var4, var6, var8, var10);
   }

   protected AxisAlignedBB(double var1, double var3, double var5, double var7, double var9, double var11) {
      super();
      this.field_72340_a = var1;
      this.field_72338_b = var3;
      this.field_72339_c = var5;
      this.field_72336_d = var7;
      this.field_72337_e = var9;
      this.field_72334_f = var11;
   }

   public AxisAlignedBB func_72324_b(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.field_72340_a = var1;
      this.field_72338_b = var3;
      this.field_72339_c = var5;
      this.field_72336_d = var7;
      this.field_72337_e = var9;
      this.field_72334_f = var11;
      return this;
   }

   public AxisAlignedBB func_72321_a(double var1, double var3, double var5) {
      double var7 = this.field_72340_a;
      double var9 = this.field_72338_b;
      double var11 = this.field_72339_c;
      double var13 = this.field_72336_d;
      double var15 = this.field_72337_e;
      double var17 = this.field_72334_f;
      if (var1 < 0.0) {
         var7 += var1;
      }

      if (var1 > 0.0) {
         var13 += var1;
      }

      if (var3 < 0.0) {
         var9 += var3;
      }

      if (var3 > 0.0) {
         var15 += var3;
      }

      if (var5 < 0.0) {
         var11 += var5;
      }

      if (var5 > 0.0) {
         var17 += var5;
      }

      return func_72330_a(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_72314_b(double var1, double var3, double var5) {
      double var7 = this.field_72340_a - var1;
      double var9 = this.field_72338_b - var3;
      double var11 = this.field_72339_c - var5;
      double var13 = this.field_72336_d + var1;
      double var15 = this.field_72337_e + var3;
      double var17 = this.field_72334_f + var5;
      return func_72330_a(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_111270_a(AxisAlignedBB var1) {
      double var2 = Math.min(this.field_72340_a, var1.field_72340_a);
      double var4 = Math.min(this.field_72338_b, var1.field_72338_b);
      double var6 = Math.min(this.field_72339_c, var1.field_72339_c);
      double var8 = Math.max(this.field_72336_d, var1.field_72336_d);
      double var10 = Math.max(this.field_72337_e, var1.field_72337_e);
      double var12 = Math.max(this.field_72334_f, var1.field_72334_f);
      return func_72330_a(var2, var4, var6, var8, var10, var12);
   }

   public AxisAlignedBB func_72325_c(double var1, double var3, double var5) {
      return func_72330_a(
         this.field_72340_a + var1,
         this.field_72338_b + var3,
         this.field_72339_c + var5,
         this.field_72336_d + var1,
         this.field_72337_e + var3,
         this.field_72334_f + var5
      );
   }

   public double func_72316_a(AxisAlignedBB var1, double var2) {
      if (var1.field_72337_e <= this.field_72338_b || var1.field_72338_b >= this.field_72337_e) {
         return var2;
      } else if (!(var1.field_72334_f <= this.field_72339_c) && !(var1.field_72339_c >= this.field_72334_f)) {
         if (var2 > 0.0 && var1.field_72336_d <= this.field_72340_a) {
            double var4 = this.field_72340_a - var1.field_72336_d;
            if (var4 < var2) {
               var2 = var4;
            }
         }

         if (var2 < 0.0 && var1.field_72340_a >= this.field_72336_d) {
            double var6 = this.field_72336_d - var1.field_72340_a;
            if (var6 > var2) {
               var2 = var6;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public double func_72323_b(AxisAlignedBB var1, double var2) {
      if (var1.field_72336_d <= this.field_72340_a || var1.field_72340_a >= this.field_72336_d) {
         return var2;
      } else if (!(var1.field_72334_f <= this.field_72339_c) && !(var1.field_72339_c >= this.field_72334_f)) {
         if (var2 > 0.0 && var1.field_72337_e <= this.field_72338_b) {
            double var4 = this.field_72338_b - var1.field_72337_e;
            if (var4 < var2) {
               var2 = var4;
            }
         }

         if (var2 < 0.0 && var1.field_72338_b >= this.field_72337_e) {
            double var6 = this.field_72337_e - var1.field_72338_b;
            if (var6 > var2) {
               var2 = var6;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public double func_72322_c(AxisAlignedBB var1, double var2) {
      if (var1.field_72336_d <= this.field_72340_a || var1.field_72340_a >= this.field_72336_d) {
         return var2;
      } else if (!(var1.field_72337_e <= this.field_72338_b) && !(var1.field_72338_b >= this.field_72337_e)) {
         if (var2 > 0.0 && var1.field_72334_f <= this.field_72339_c) {
            double var4 = this.field_72339_c - var1.field_72334_f;
            if (var4 < var2) {
               var2 = var4;
            }
         }

         if (var2 < 0.0 && var1.field_72339_c >= this.field_72334_f) {
            double var6 = this.field_72334_f - var1.field_72339_c;
            if (var6 > var2) {
               var2 = var6;
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public boolean func_72326_a(AxisAlignedBB var1) {
      if (var1.field_72336_d <= this.field_72340_a || var1.field_72340_a >= this.field_72336_d) {
         return false;
      } else if (var1.field_72337_e <= this.field_72338_b || var1.field_72338_b >= this.field_72337_e) {
         return false;
      } else {
         return !(var1.field_72334_f <= this.field_72339_c) && !(var1.field_72339_c >= this.field_72334_f);
      }
   }

   public AxisAlignedBB func_72317_d(double var1, double var3, double var5) {
      this.field_72340_a += var1;
      this.field_72338_b += var3;
      this.field_72339_c += var5;
      this.field_72336_d += var1;
      this.field_72337_e += var3;
      this.field_72334_f += var5;
      return this;
   }

   public boolean func_72318_a(Vec3 var1) {
      if (var1.field_72450_a <= this.field_72340_a || var1.field_72450_a >= this.field_72336_d) {
         return false;
      } else if (var1.field_72448_b <= this.field_72338_b || var1.field_72448_b >= this.field_72337_e) {
         return false;
      } else {
         return !(var1.field_72449_c <= this.field_72339_c) && !(var1.field_72449_c >= this.field_72334_f);
      }
   }

   public double func_72320_b() {
      double var1 = this.field_72336_d - this.field_72340_a;
      double var3 = this.field_72337_e - this.field_72338_b;
      double var5 = this.field_72334_f - this.field_72339_c;
      return (var1 + var3 + var5) / 3.0;
   }

   public AxisAlignedBB func_72331_e(double var1, double var3, double var5) {
      double var7 = this.field_72340_a + var1;
      double var9 = this.field_72338_b + var3;
      double var11 = this.field_72339_c + var5;
      double var13 = this.field_72336_d - var1;
      double var15 = this.field_72337_e - var3;
      double var17 = this.field_72334_f - var5;
      return func_72330_a(var7, var9, var11, var13, var15, var17);
   }

   public AxisAlignedBB func_72329_c() {
      return func_72330_a(this.field_72340_a, this.field_72338_b, this.field_72339_c, this.field_72336_d, this.field_72337_e, this.field_72334_f);
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
      if (var3 != null && (var9 == null || var1.func_72436_e(var3) < var1.func_72436_e(var9))) {
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
         byte var10 = -1;
         if (var9 == var3) {
            var10 = 4;
         }

         if (var9 == var4) {
            var10 = 5;
         }

         if (var9 == var5) {
            var10 = 0;
         }

         if (var9 == var6) {
            var10 = 1;
         }

         if (var9 == var7) {
            var10 = 2;
         }

         if (var9 == var8) {
            var10 = 3;
         }

         return new MovingObjectPosition(0, 0, 0, var10, var9);
      }
   }

   private boolean func_72333_b(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72448_b >= this.field_72338_b
            && var1.field_72448_b <= this.field_72337_e
            && var1.field_72449_c >= this.field_72339_c
            && var1.field_72449_c <= this.field_72334_f;
      }
   }

   private boolean func_72315_c(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_72340_a
            && var1.field_72450_a <= this.field_72336_d
            && var1.field_72449_c >= this.field_72339_c
            && var1.field_72449_c <= this.field_72334_f;
      }
   }

   private boolean func_72319_d(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_72340_a
            && var1.field_72450_a <= this.field_72336_d
            && var1.field_72448_b >= this.field_72338_b
            && var1.field_72448_b <= this.field_72337_e;
      }
   }

   public void func_72328_c(AxisAlignedBB var1) {
      this.field_72340_a = var1.field_72340_a;
      this.field_72338_b = var1.field_72338_b;
      this.field_72339_c = var1.field_72339_c;
      this.field_72336_d = var1.field_72336_d;
      this.field_72337_e = var1.field_72337_e;
      this.field_72334_f = var1.field_72334_f;
   }

   @Override
   public String toString() {
      return "box["
         + this.field_72340_a
         + ", "
         + this.field_72338_b
         + ", "
         + this.field_72339_c
         + " -> "
         + this.field_72336_d
         + ", "
         + this.field_72337_e
         + ", "
         + this.field_72334_f
         + "]";
   }
}
