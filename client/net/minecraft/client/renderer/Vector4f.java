package net.minecraft.client.renderer;

import java.util.Arrays;

public class Vector4f {
   private final float[] field_195916_a;

   public Vector4f(Vector4f var1) {
      super();
      this.field_195916_a = Arrays.copyOf(var1.field_195916_a, 4);
   }

   public Vector4f() {
      super();
      this.field_195916_a = new float[4];
   }

   public Vector4f(float var1, float var2, float var3, float var4) {
      super();
      this.field_195916_a = new float[]{var1, var2, var3, var4};
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector4f var2 = (Vector4f)var1;
         return Arrays.equals(this.field_195916_a, var2.field_195916_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_195916_a);
   }

   public float func_195910_a() {
      return this.field_195916_a[0];
   }

   public float func_195913_b() {
      return this.field_195916_a[1];
   }

   public float func_195914_c() {
      return this.field_195916_a[2];
   }

   public float func_195915_d() {
      return this.field_195916_a[3];
   }

   public void func_195909_a(Vector3f var1) {
      float[] var10000 = this.field_195916_a;
      var10000[0] *= var1.func_195899_a();
      var10000 = this.field_195916_a;
      var10000[1] *= var1.func_195900_b();
      var10000 = this.field_195916_a;
      var10000[2] *= var1.func_195902_c();
   }

   public void func_195911_a(float var1, float var2, float var3, float var4) {
      this.field_195916_a[0] = var1;
      this.field_195916_a[1] = var2;
      this.field_195916_a[2] = var3;
      this.field_195916_a[3] = var4;
   }

   public void func_195908_a(Matrix4f var1) {
      float[] var2 = Arrays.copyOf(this.field_195916_a, 4);

      for(int var3 = 0; var3 < 4; ++var3) {
         this.field_195916_a[var3] = 0.0F;

         for(int var4 = 0; var4 < 4; ++var4) {
            float[] var10000 = this.field_195916_a;
            var10000[var3] += var1.func_195885_a(var3, var4) * var2[var4];
         }
      }

   }

   public void func_195912_a(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.func_195890_a(new Quaternion(this.func_195910_a(), this.func_195913_b(), this.func_195914_c(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.func_195892_e();
      var2.func_195890_a(var3);
      this.func_195911_a(var2.func_195889_a(), var2.func_195891_b(), var2.func_195893_c(), this.func_195915_d());
   }
}
