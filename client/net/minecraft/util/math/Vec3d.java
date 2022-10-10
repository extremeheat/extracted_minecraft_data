package net.minecraft.util.math;

import java.util.EnumSet;
import net.minecraft.util.EnumFacing;

public class Vec3d {
   public static final Vec3d field_186680_a = new Vec3d(0.0D, 0.0D, 0.0D);
   public final double field_72450_a;
   public final double field_72448_b;
   public final double field_72449_c;

   public Vec3d(double var1, double var3, double var5) {
      super();
      this.field_72450_a = var1;
      this.field_72448_b = var3;
      this.field_72449_c = var5;
   }

   public Vec3d(Vec3i var1) {
      this((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p());
   }

   public Vec3d func_72444_a(Vec3d var1) {
      return new Vec3d(var1.field_72450_a - this.field_72450_a, var1.field_72448_b - this.field_72448_b, var1.field_72449_c - this.field_72449_c);
   }

   public Vec3d func_72432_b() {
      double var1 = (double)MathHelper.func_76133_a(this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c);
      return var1 < 1.0E-4D ? field_186680_a : new Vec3d(this.field_72450_a / var1, this.field_72448_b / var1, this.field_72449_c / var1);
   }

   public double func_72430_b(Vec3d var1) {
      return this.field_72450_a * var1.field_72450_a + this.field_72448_b * var1.field_72448_b + this.field_72449_c * var1.field_72449_c;
   }

   public Vec3d func_72431_c(Vec3d var1) {
      return new Vec3d(this.field_72448_b * var1.field_72449_c - this.field_72449_c * var1.field_72448_b, this.field_72449_c * var1.field_72450_a - this.field_72450_a * var1.field_72449_c, this.field_72450_a * var1.field_72448_b - this.field_72448_b * var1.field_72450_a);
   }

   public Vec3d func_178788_d(Vec3d var1) {
      return this.func_178786_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public Vec3d func_178786_a(double var1, double var3, double var5) {
      return this.func_72441_c(-var1, -var3, -var5);
   }

   public Vec3d func_178787_e(Vec3d var1) {
      return this.func_72441_c(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
   }

   public Vec3d func_72441_c(double var1, double var3, double var5) {
      return new Vec3d(this.field_72450_a + var1, this.field_72448_b + var3, this.field_72449_c + var5);
   }

   public double func_72438_d(Vec3d var1) {
      double var2 = var1.field_72450_a - this.field_72450_a;
      double var4 = var1.field_72448_b - this.field_72448_b;
      double var6 = var1.field_72449_c - this.field_72449_c;
      return (double)MathHelper.func_76133_a(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double func_72436_e(Vec3d var1) {
      double var2 = var1.field_72450_a - this.field_72450_a;
      double var4 = var1.field_72448_b - this.field_72448_b;
      double var6 = var1.field_72449_c - this.field_72449_c;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double func_186679_c(double var1, double var3, double var5) {
      double var7 = var1 - this.field_72450_a;
      double var9 = var3 - this.field_72448_b;
      double var11 = var5 - this.field_72449_c;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public Vec3d func_186678_a(double var1) {
      return new Vec3d(this.field_72450_a * var1, this.field_72448_b * var1, this.field_72449_c * var1);
   }

   public double func_72433_c() {
      return (double)MathHelper.func_76133_a(this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c);
   }

   public double func_189985_c() {
      return this.field_72450_a * this.field_72450_a + this.field_72448_b * this.field_72448_b + this.field_72449_c * this.field_72449_c;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3d)) {
         return false;
      } else {
         Vec3d var2 = (Vec3d)var1;
         if (Double.compare(var2.field_72450_a, this.field_72450_a) != 0) {
            return false;
         } else if (Double.compare(var2.field_72448_b, this.field_72448_b) != 0) {
            return false;
         } else {
            return Double.compare(var2.field_72449_c, this.field_72449_c) == 0;
         }
      }
   }

   public int hashCode() {
      long var2 = Double.doubleToLongBits(this.field_72450_a);
      int var1 = (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.field_72448_b);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.field_72449_c);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public String toString() {
      return "(" + this.field_72450_a + ", " + this.field_72448_b + ", " + this.field_72449_c + ")";
   }

   public Vec3d func_178789_a(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a;
      double var6 = this.field_72448_b * (double)var2 + this.field_72449_c * (double)var3;
      double var8 = this.field_72449_c * (double)var2 - this.field_72448_b * (double)var3;
      return new Vec3d(var4, var6, var8);
   }

   public Vec3d func_178785_b(float var1) {
      float var2 = MathHelper.func_76134_b(var1);
      float var3 = MathHelper.func_76126_a(var1);
      double var4 = this.field_72450_a * (double)var2 + this.field_72449_c * (double)var3;
      double var6 = this.field_72448_b;
      double var8 = this.field_72449_c * (double)var2 - this.field_72450_a * (double)var3;
      return new Vec3d(var4, var6, var8);
   }

   public static Vec3d func_189984_a(Vec2f var0) {
      return func_189986_a(var0.field_189982_i, var0.field_189983_j);
   }

   public static Vec3d func_189986_a(float var0, float var1) {
      float var2 = MathHelper.func_76134_b(-var1 * 0.017453292F - 3.1415927F);
      float var3 = MathHelper.func_76126_a(-var1 * 0.017453292F - 3.1415927F);
      float var4 = -MathHelper.func_76134_b(-var0 * 0.017453292F);
      float var5 = MathHelper.func_76126_a(-var0 * 0.017453292F);
      return new Vec3d((double)(var3 * var4), (double)var5, (double)(var2 * var4));
   }

   public Vec3d func_197746_a(EnumSet<EnumFacing.Axis> var1) {
      double var2 = var1.contains(EnumFacing.Axis.X) ? (double)MathHelper.func_76128_c(this.field_72450_a) : this.field_72450_a;
      double var4 = var1.contains(EnumFacing.Axis.Y) ? (double)MathHelper.func_76128_c(this.field_72448_b) : this.field_72448_b;
      double var6 = var1.contains(EnumFacing.Axis.Z) ? (double)MathHelper.func_76128_c(this.field_72449_c) : this.field_72449_c;
      return new Vec3d(var2, var4, var6);
   }
}
