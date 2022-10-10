package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public final class Vector3f {
   private final float[] field_195907_a;

   public Vector3f(Vector3f var1) {
      super();
      this.field_195907_a = Arrays.copyOf(var1.field_195907_a, 3);
   }

   public Vector3f() {
      super();
      this.field_195907_a = new float[3];
   }

   public Vector3f(float var1, float var2, float var3) {
      super();
      this.field_195907_a = new float[]{var1, var2, var3};
   }

   public Vector3f(EnumFacing var1) {
      super();
      Vec3i var2 = var1.func_176730_m();
      this.field_195907_a = new float[]{(float)var2.func_177958_n(), (float)var2.func_177956_o(), (float)var2.func_177952_p()};
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector3f var2 = (Vector3f)var1;
         return Arrays.equals(this.field_195907_a, var2.field_195907_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_195907_a);
   }

   public float func_195899_a() {
      return this.field_195907_a[0];
   }

   public float func_195900_b() {
      return this.field_195907_a[1];
   }

   public float func_195902_c() {
      return this.field_195907_a[2];
   }

   public void func_195898_a(float var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         float[] var10000 = this.field_195907_a;
         var10000[var2] *= var1;
      }

   }

   public void func_195901_a(float var1, float var2) {
      this.field_195907_a[0] = MathHelper.func_76131_a(this.field_195907_a[0], var1, var2);
      this.field_195907_a[1] = MathHelper.func_76131_a(this.field_195907_a[1], var1, var2);
      this.field_195907_a[2] = MathHelper.func_76131_a(this.field_195907_a[2], var1, var2);
   }

   public void func_195905_a(float var1, float var2, float var3) {
      this.field_195907_a[0] = var1;
      this.field_195907_a[1] = var2;
      this.field_195907_a[2] = var3;
   }

   public void func_195904_b(float var1, float var2, float var3) {
      float[] var10000 = this.field_195907_a;
      var10000[0] += var1;
      var10000 = this.field_195907_a;
      var10000[1] += var2;
      var10000 = this.field_195907_a;
      var10000[2] += var3;
   }

   public void func_195897_a(Vector3f var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         float[] var10000 = this.field_195907_a;
         var10000[var2] -= var1.field_195907_a[var2];
      }

   }

   public float func_195903_b(Vector3f var1) {
      float var2 = 0.0F;

      for(int var3 = 0; var3 < 3; ++var3) {
         var2 += this.field_195907_a[var3] * var1.field_195907_a[var3];
      }

      return var2;
   }

   public void func_195906_d() {
      float var1 = 0.0F;

      int var2;
      for(var2 = 0; var2 < 3; ++var2) {
         var1 += this.field_195907_a[var2] * this.field_195907_a[var2];
      }

      for(var2 = 0; var2 < 3; ++var2) {
         float[] var10000 = this.field_195907_a;
         var10000[var2] /= var1;
      }

   }

   public void func_195896_c(Vector3f var1) {
      float var2 = this.field_195907_a[0];
      float var3 = this.field_195907_a[1];
      float var4 = this.field_195907_a[2];
      float var5 = var1.func_195899_a();
      float var6 = var1.func_195900_b();
      float var7 = var1.func_195902_c();
      this.field_195907_a[0] = var3 * var7 - var4 * var6;
      this.field_195907_a[1] = var4 * var5 - var2 * var7;
      this.field_195907_a[2] = var2 * var6 - var3 * var5;
   }
}
