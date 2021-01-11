package net.minecraft.util;

import com.google.common.base.Objects;

public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i field_177959_e = new Vec3i(0, 0, 0);
   private final int field_177962_a;
   private final int field_177960_b;
   private final int field_177961_c;

   public Vec3i(int var1, int var2, int var3) {
      super();
      this.field_177962_a = var1;
      this.field_177960_b = var2;
      this.field_177961_c = var3;
   }

   public Vec3i(double var1, double var3, double var5) {
      this(MathHelper.func_76128_c(var1), MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var5));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3i)) {
         return false;
      } else {
         Vec3i var2 = (Vec3i)var1;
         if (this.func_177958_n() != var2.func_177958_n()) {
            return false;
         } else if (this.func_177956_o() != var2.func_177956_o()) {
            return false;
         } else {
            return this.func_177952_p() == var2.func_177952_p();
         }
      }
   }

   public int hashCode() {
      return (this.func_177956_o() + this.func_177952_p() * 31) * 31 + this.func_177958_n();
   }

   public int compareTo(Vec3i var1) {
      if (this.func_177956_o() == var1.func_177956_o()) {
         return this.func_177952_p() == var1.func_177952_p() ? this.func_177958_n() - var1.func_177958_n() : this.func_177952_p() - var1.func_177952_p();
      } else {
         return this.func_177956_o() - var1.func_177956_o();
      }
   }

   public int func_177958_n() {
      return this.field_177962_a;
   }

   public int func_177956_o() {
      return this.field_177960_b;
   }

   public int func_177952_p() {
      return this.field_177961_c;
   }

   public Vec3i func_177955_d(Vec3i var1) {
      return new Vec3i(this.func_177956_o() * var1.func_177952_p() - this.func_177952_p() * var1.func_177956_o(), this.func_177952_p() * var1.func_177958_n() - this.func_177958_n() * var1.func_177952_p(), this.func_177958_n() * var1.func_177956_o() - this.func_177956_o() * var1.func_177958_n());
   }

   public double func_177954_c(double var1, double var3, double var5) {
      double var7 = (double)this.func_177958_n() - var1;
      double var9 = (double)this.func_177956_o() - var3;
      double var11 = (double)this.func_177952_p() - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_177957_d(double var1, double var3, double var5) {
      double var7 = (double)this.func_177958_n() + 0.5D - var1;
      double var9 = (double)this.func_177956_o() + 0.5D - var3;
      double var11 = (double)this.func_177952_p() + 0.5D - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_177951_i(Vec3i var1) {
      return this.func_177954_c((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p());
   }

   public String toString() {
      return Objects.toStringHelper(this).add("x", this.func_177958_n()).add("y", this.func_177956_o()).add("z", this.func_177952_p()).toString();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Vec3i)var1);
   }
}
