package com.mojang.math;

public class Vector3d {
   public double x;
   public double y;
   public double z;

   public Vector3d(double var1, double var3, double var5) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
   }

   public void set(Vector3d var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
   }

   public void set(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
   }

   public void scale(double var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
   }

   public void add(Vector3d var1) {
      this.x += var1.x;
      this.y += var1.y;
      this.z += var1.z;
   }
}
