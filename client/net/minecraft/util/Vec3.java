package net.minecraft.util;

public class Vec3 {
   public final double field_72450_a;
   public final double field_72448_b;
   public final double field_72449_c;

   public Vec3(double var1, double var3, double var5) {
      super();
      if (var1 == -0.0D) {
         var1 = 0.0D;
      }

      if (var3 == -0.0D) {
         var3 = 0.0D;
      }

      if (var5 == -0.0D) {
         var5 = 0.0D;
      }

      this.field_72450_a = var1;
      this.field_72448_b = var3;
      this.field_72449_c = var5;
   }

   public Vec3(Vec3i var1) {
      this((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p());
   }

   public Vec3 func_72444_a(Vec3 var1) {
      return new Vec3(var1.field_72450_a - this.field_72450_a, var1.field_72448_b - this.field_72448_b, var1.field_72449_c - this.field_72449_c);
   }

   public Vec3 func_72432_b() {
      double var1 = (double)MathHelper.func_76133_a(this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c);
      return var1 < 1.0E-4D ? new Vec3(0.0D, 0.0D, 0.0D) : new Vec3(this.field_72450_a / var1, this.field_72448_b / var1, this.field_72449_c / var1);
   }

   public double func_72430_b(Vec3 var1) {
      return this.field_72450_a * var1.field_72450_a + this.field_72448_b * var1.field_72448_b + this.field_72449_c * var1.field_72449_c;
   }

   public Vec3 func_72431_c(Vec3 var1) {
      return new Vec3(this.field_72448_b * var1.field_72449_c - this.field_72449_c * var1.field_72448_b, this.field_72449_c * var1.field_72450_a - this.field_72450_a * var1.field_72449_c, this.field_72450_a * var1.field_72448_b - this.field_72448_b * var1.field_72450_a);
   }

   public Vec3 func_178788_d(Vec3 var1) {
      return this.func_178786_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public Vec3 func_178786_a(double var1, double var3, double var5) {
      return this.func_72441_c(-var1, -var3, -var5);
   }

   public Vec3 func_178787_e(Vec3 var1) {
      return this.func_72441_c(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public Vec3 func_72441_c(double var1, double var3, double var5) {
      return new Vec3(this.field_72450_a + var1, this.field_72448_b + var3, this.field_72449_c + var5);
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

   public double func_72433_c() {
      return (double)MathHelper.func_76133_a(this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c);
   }

   public Vec3 func_72429_b(Vec3 var1, double var2) {
      double var4 = var1.field_72450_a - this.field_72450_a;
      double var6 = var1.field_72448_b - this.field_72448_b;
      double var8 = var1.field_72449_c - this.field_72449_c;
      if (var4 * var4 < 1.0000000116860974E-7D) {
         return null;
      } else {
         double var10 = (var2 - this.field_72450_a) / var4;
         return var10 >= 0.0D && var10 <= 1.0D ? new Vec3(this.field_72450_a + var4 * var10, this.field_72448_b + var6 * var10, this.field_72449_c + var8 * var10) : null;
      }
   }

   public Vec3 func_72435_c(Vec3 var1, double var2) {
      double var4 = var1.field_72450_a - this.field_72450_a;
      double var6 = var1.field_72448_b - this.field_72448_b;
      double var8 = var1.field_72449_c - this.field_72449_c;
      if (var6 * var6 < 1.0000000116860974E-7D) {
         return null;
      } else {
         double var10 = (var2 - this.field_72448_b) / var6;
         return var10 >= 0.0D && var10 <= 1.0D ? new Vec3(this.field_72450_a + var4 * var10, this.field_72448_b + var6 * var10, this.field_72449_c + var8 * var10) : null;
      }
   }

   public Vec3 func_72434_d(Vec3 var1, double var2) {
      double var4 = var1.field_72450_a - this.field_72450_a;
      double var6 = var1.field_72448_b - this.field_72448_b;
      double var8 = var1.field_72449_c - this.field_72449_c;
      if (var8 * var8 < 1.0000000116860974E-7D) {
         return null;
      } else {
         double var10 = (var2 - this.field_72449_c) / var8;
         return var10 >= 0.0D && var10 <= 1.0D ? new Vec3(this.field_72450_a + var4 * var10, this.field_72448_b + var6 * var10, this.field_72449_c + var8 * var10) : null;
      }
   }

   public String toString() {
      return "(" + this.field_72450_a + ", " + this.field_72448_b + ", " + this.field_72449_c + ")";
   }

   public Vec3 func_178789_a(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a;
      double var6 = this.field_72448_b * (double)var2 + this.field_72449_c * (double)var3;
      double var8 = this.field_72449_c * (double)var2 - this.field_72448_b * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 func_178785_b(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a * (double)var2 + this.field_72449_c * (double)var3;
      double var6 = this.field_72448_b;
      double var8 = this.field_72449_c * (double)var2 - this.field_72450_a * (double)var3;
      return new Vec3(var4, var6, var8);
   }
}
