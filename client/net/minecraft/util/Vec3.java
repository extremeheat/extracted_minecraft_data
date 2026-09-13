package net.minecraft.util;

public class Vec3 {
   public double field_72450_a;
   public double field_72448_b;
   public double field_72449_c;

   public static Vec3 func_72443_a(double var0, double var2, double var4) {
      return new Vec3(var0, var2, var4);
   }

   protected Vec3(double var1, double var3, double var5) {
      super();
      if (var1 == -0.0) {
         var1 = 0.0;
      }

      if (var3 == -0.0) {
         var3 = 0.0;
      }

      if (var5 == -0.0) {
         var5 = 0.0;
      }

      this.field_72450_a = var1;
      this.field_72448_b = var3;
      this.field_72449_c = var5;
   }

   protected Vec3 func_72439_b(double var1, double var3, double var5) {
      this.field_72450_a = var1;
      this.field_72448_b = var3;
      this.field_72449_c = var5;
      return this;
   }

   public Vec3 func_72444_a(Vec3 var1) {
      return func_72443_a(var1.field_72450_a - this.field_72450_a, var1.field_72448_b - this.field_72448_b, var1.field_72449_c - this.field_72449_c);
   }

   public Vec3 func_72432_b() {
      double var1 = (double)MathHelper.func_76133_a(
         this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c
      );
      return var1 < 1.0E-4 ? func_72443_a(0.0, 0.0, 0.0) : func_72443_a(this.field_72450_a / var1, this.field_72448_b / var1, this.field_72449_c / var1);
   }

   public double func_72430_b(Vec3 var1) {
      return this.field_72450_a * var1.field_72450_a + this.field_72448_b * var1.field_72448_b + this.field_72449_c * var1.field_72449_c;
   }

   public Vec3 func_72431_c(Vec3 var1) {
      return func_72443_a(
         this.field_72448_b * var1.field_72449_c - this.field_72449_c * var1.field_72448_b,
         this.field_72449_c * var1.field_72450_a - this.field_72450_a * var1.field_72449_c,
         this.field_72450_a * var1.field_72448_b - this.field_72448_b * var1.field_72450_a
      );
   }

   public Vec3 func_72441_c(double var1, double var3, double var5) {
      return func_72443_a(this.field_72450_a + var1, this.field_72448_b + var3, this.field_72449_c + var5);
   }

   public double func_72438_d(Vec3 var1) {
      double var2 = var1.field_72450_a - this.field_72450_a;
      double var4 = var1.field_72448_b - this.field_72448_b;
      double var6 = var1.field_72449_c - this.field_72449_c;
      return (double)MathHelper.func_76133_a(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double func_72436_e(Vec3 var1) {
      double var2 = var1.field_72450_a - this.field_72450_a;
      double var4 = var1.field_72448_b - this.field_72448_b;
      double var6 = var1.field_72449_c - this.field_72449_c;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double func_72445_d(double var1, double var3, double var5) {
      double var7 = var1 - this.field_72450_a;
      double var9 = var3 - this.field_72448_b;
      double var11 = var5 - this.field_72449_c;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_72433_c() {
      return (double)MathHelper.func_76133_a(
         this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c
      );
   }

   public Vec3 func_72429_b(Vec3 var1, double var2) {
      double var4 = var1.field_72450_a - this.field_72450_a;
      double var6 = var1.field_72448_b - this.field_72448_b;
      double var8 = var1.field_72449_c - this.field_72449_c;
      if (var4 * var4 < 1.0000000116860974E-7) {
         return null;
      } else {
         double var10 = (var2 - this.field_72450_a) / var4;
         return !(var10 < 0.0) && !(var10 > 1.0)
            ? func_72443_a(this.field_72450_a + var4 * var10, this.field_72448_b + var6 * var10, this.field_72449_c + var8 * var10)
            : null;
      }
   }

   public Vec3 func_72435_c(Vec3 var1, double var2) {
      double var4 = var1.field_72450_a - this.field_72450_a;
      double var6 = var1.field_72448_b - this.field_72448_b;
      double var8 = var1.field_72449_c - this.field_72449_c;
      if (var6 * var6 < 1.0000000116860974E-7) {
         return null;
      } else {
         double var10 = (var2 - this.field_72448_b) / var6;
         return !(var10 < 0.0) && !(var10 > 1.0)
            ? func_72443_a(this.field_72450_a + var4 * var10, this.field_72448_b + var6 * var10, this.field_72449_c + var8 * var10)
            : null;
      }
   }

   public Vec3 func_72434_d(Vec3 var1, double var2) {
      double var4 = var1.field_72450_a - this.field_72450_a;
      double var6 = var1.field_72448_b - this.field_72448_b;
      double var8 = var1.field_72449_c - this.field_72449_c;
      if (var8 * var8 < 1.0000000116860974E-7) {
         return null;
      } else {
         double var10 = (var2 - this.field_72449_c) / var8;
         return !(var10 < 0.0) && !(var10 > 1.0)
            ? func_72443_a(this.field_72450_a + var4 * var10, this.field_72448_b + var6 * var10, this.field_72449_c + var8 * var10)
            : null;
      }
   }

   @Override
   public String toString() {
      return "(" + this.field_72450_a + ", " + this.field_72448_b + ", " + this.field_72449_c + ")";
   }

   public void func_72440_a(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a;
      double var6 = this.field_72448_b * (double)var2 + this.field_72449_c * (double)var3;
      double var8 = this.field_72449_c * (double)var2 - this.field_72448_b * (double)var3;
      this.func_72439_b(var4, var6, var8);
   }

   public void func_72442_b(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a * (double)var2 + this.field_72449_c * (double)var3;
      double var6 = this.field_72448_b;
      double var8 = this.field_72449_c * (double)var2 - this.field_72450_a * (double)var3;
      this.func_72439_b(var4, var6, var8);
   }

   public void func_72446_c(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a * (double)var2 + this.field_72448_b * (double)var3;
      double var6 = this.field_72448_b * (double)var2 - this.field_72450_a * (double)var3;
      double var8 = this.field_72449_c;
      this.func_72439_b(var4, var6, var8);
   }
}
