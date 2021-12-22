package com.mojang.math;

public class Vector3d {
   // $FF: renamed from: x double
   public double field_286;
   // $FF: renamed from: y double
   public double field_287;
   // $FF: renamed from: z double
   public double field_288;

   public Vector3d(double var1, double var3, double var5) {
      super();
      this.field_286 = var1;
      this.field_287 = var3;
      this.field_288 = var5;
   }

   public void set(Vector3d var1) {
      this.field_286 = var1.field_286;
      this.field_287 = var1.field_287;
      this.field_288 = var1.field_288;
   }

   public void set(double var1, double var3, double var5) {
      this.field_286 = var1;
      this.field_287 = var3;
      this.field_288 = var5;
   }

   public void scale(double var1) {
      this.field_286 *= var1;
      this.field_287 *= var1;
      this.field_288 *= var1;
   }

   public void add(Vector3d var1) {
      this.field_286 += var1.field_286;
      this.field_287 += var1.field_287;
      this.field_288 += var1.field_288;
   }
}
