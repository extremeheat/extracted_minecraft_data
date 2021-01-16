package com.mojang.math;

import net.minecraft.world.phys.Vec3;

public final class Vector3f {
   public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
   public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
   public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
   public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
   public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);
   public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
   private float x;
   private float y;
   private float z;

   public Vector3f() {
      super();
   }

   public Vector3f(float var1, float var2, float var3) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Vector3f(Vec3 var1) {
      this((float)var1.x, (float)var1.y, (float)var1.z);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector3f var2 = (Vector3f)var1;
         if (Float.compare(var2.x, this.x) != 0) {
            return false;
         } else if (Float.compare(var2.y, this.y) != 0) {
            return false;
         } else {
            return Float.compare(var2.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.x);
      var1 = 31 * var1 + Float.floatToIntBits(this.y);
      var1 = 31 * var1 + Float.floatToIntBits(this.z);
      return var1;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public void set(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.i(), var2.j(), var2.k());
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }
}
