package net.minecraft.client.renderer;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;

public final class Quaternion {
   private final float[] field_195895_a;

   public Quaternion() {
      super();
      this.field_195895_a = new float[4];
      this.field_195895_a[4] = 1.0F;
   }

   public Quaternion(float var1, float var2, float var3, float var4) {
      super();
      this.field_195895_a = new float[4];
      this.field_195895_a[0] = var1;
      this.field_195895_a[1] = var2;
      this.field_195895_a[2] = var3;
      this.field_195895_a[3] = var4;
   }

   public Quaternion(Vector3f var1, float var2, boolean var3) {
      super();
      if (var3) {
         var2 *= 0.017453292F;
      }

      float var4 = MathHelper.func_76126_a(var2 / 2.0F);
      this.field_195895_a = new float[4];
      this.field_195895_a[0] = var1.func_195899_a() * var4;
      this.field_195895_a[1] = var1.func_195900_b() * var4;
      this.field_195895_a[2] = var1.func_195902_c() * var4;
      this.field_195895_a[3] = MathHelper.func_76134_b(var2 / 2.0F);
   }

   public Quaternion(float var1, float var2, float var3, boolean var4) {
      super();
      if (var4) {
         var1 *= 0.017453292F;
         var2 *= 0.017453292F;
         var3 *= 0.017453292F;
      }

      float var5 = MathHelper.func_76126_a(0.5F * var1);
      float var6 = MathHelper.func_76134_b(0.5F * var1);
      float var7 = MathHelper.func_76126_a(0.5F * var2);
      float var8 = MathHelper.func_76134_b(0.5F * var2);
      float var9 = MathHelper.func_76126_a(0.5F * var3);
      float var10 = MathHelper.func_76134_b(0.5F * var3);
      this.field_195895_a = new float[4];
      this.field_195895_a[0] = var5 * var8 * var10 + var6 * var7 * var9;
      this.field_195895_a[1] = var6 * var7 * var10 - var5 * var8 * var9;
      this.field_195895_a[2] = var5 * var7 * var10 + var6 * var8 * var9;
      this.field_195895_a[3] = var6 * var8 * var10 - var5 * var7 * var9;
   }

   public Quaternion(Quaternion var1) {
      super();
      this.field_195895_a = Arrays.copyOf(var1.field_195895_a, 4);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Quaternion var2 = (Quaternion)var1;
         return Arrays.equals(this.field_195895_a, var2.field_195895_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_195895_a);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Quaternion[").append(this.func_195894_d()).append(" + ");
      var1.append(this.func_195889_a()).append("i + ");
      var1.append(this.func_195891_b()).append("j + ");
      var1.append(this.func_195893_c()).append("k]");
      return var1.toString();
   }

   public float func_195889_a() {
      return this.field_195895_a[0];
   }

   public float func_195891_b() {
      return this.field_195895_a[1];
   }

   public float func_195893_c() {
      return this.field_195895_a[2];
   }

   public float func_195894_d() {
      return this.field_195895_a[3];
   }

   public void func_195890_a(Quaternion var1) {
      float var2 = this.func_195889_a();
      float var3 = this.func_195891_b();
      float var4 = this.func_195893_c();
      float var5 = this.func_195894_d();
      float var6 = var1.func_195889_a();
      float var7 = var1.func_195891_b();
      float var8 = var1.func_195893_c();
      float var9 = var1.func_195894_d();
      this.field_195895_a[0] = var5 * var6 + var2 * var9 + var3 * var8 - var4 * var7;
      this.field_195895_a[1] = var5 * var7 - var2 * var8 + var3 * var9 + var4 * var6;
      this.field_195895_a[2] = var5 * var8 + var2 * var7 - var3 * var6 + var4 * var9;
      this.field_195895_a[3] = var5 * var9 - var2 * var6 - var3 * var7 - var4 * var8;
   }

   public void func_195892_e() {
      this.field_195895_a[0] = -this.field_195895_a[0];
      this.field_195895_a[1] = -this.field_195895_a[1];
      this.field_195895_a[2] = -this.field_195895_a[2];
   }
}
