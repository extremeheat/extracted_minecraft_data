package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;

public class MutableBoundingBox {
   public int field_78897_a;
   public int field_78895_b;
   public int field_78896_c;
   public int field_78893_d;
   public int field_78894_e;
   public int field_78892_f;

   public MutableBoundingBox() {
      super();
   }

   public MutableBoundingBox(int[] var1) {
      super();
      if (var1.length == 6) {
         this.field_78897_a = var1[0];
         this.field_78895_b = var1[1];
         this.field_78896_c = var1[2];
         this.field_78893_d = var1[3];
         this.field_78894_e = var1[4];
         this.field_78892_f = var1[5];
      }

   }

   public static MutableBoundingBox func_78887_a() {
      return new MutableBoundingBox(2147483647, 2147483647, 2147483647, -2147483648, -2147483648, -2147483648);
   }

   public static MutableBoundingBox func_175897_a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, EnumFacing var9) {
      switch(var9) {
      case NORTH:
         return new MutableBoundingBox(var0 + var3, var1 + var4, var2 - var8 + 1 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var5);
      case SOUTH:
         return new MutableBoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      case WEST:
         return new MutableBoundingBox(var0 - var8 + 1 + var5, var1 + var4, var2 + var3, var0 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
      case EAST:
         return new MutableBoundingBox(var0 + var5, var1 + var4, var2 + var3, var0 + var8 - 1 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
      default:
         return new MutableBoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      }
   }

   public static MutableBoundingBox func_175899_a(int var0, int var1, int var2, int var3, int var4, int var5) {
      return new MutableBoundingBox(Math.min(var0, var3), Math.min(var1, var4), Math.min(var2, var5), Math.max(var0, var3), Math.max(var1, var4), Math.max(var2, var5));
   }

   public MutableBoundingBox(MutableBoundingBox var1) {
      super();
      this.field_78897_a = var1.field_78897_a;
      this.field_78895_b = var1.field_78895_b;
      this.field_78896_c = var1.field_78896_c;
      this.field_78893_d = var1.field_78893_d;
      this.field_78894_e = var1.field_78894_e;
      this.field_78892_f = var1.field_78892_f;
   }

   public MutableBoundingBox(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.field_78897_a = var1;
      this.field_78895_b = var2;
      this.field_78896_c = var3;
      this.field_78893_d = var4;
      this.field_78894_e = var5;
      this.field_78892_f = var6;
   }

   public MutableBoundingBox(Vec3i var1, Vec3i var2) {
      super();
      this.field_78897_a = Math.min(var1.func_177958_n(), var2.func_177958_n());
      this.field_78895_b = Math.min(var1.func_177956_o(), var2.func_177956_o());
      this.field_78896_c = Math.min(var1.func_177952_p(), var2.func_177952_p());
      this.field_78893_d = Math.max(var1.func_177958_n(), var2.func_177958_n());
      this.field_78894_e = Math.max(var1.func_177956_o(), var2.func_177956_o());
      this.field_78892_f = Math.max(var1.func_177952_p(), var2.func_177952_p());
   }

   public MutableBoundingBox(int var1, int var2, int var3, int var4) {
      super();
      this.field_78897_a = var1;
      this.field_78896_c = var2;
      this.field_78893_d = var3;
      this.field_78892_f = var4;
      this.field_78895_b = 1;
      this.field_78894_e = 512;
   }

   public boolean func_78884_a(MutableBoundingBox var1) {
      return this.field_78893_d >= var1.field_78897_a && this.field_78897_a <= var1.field_78893_d && this.field_78892_f >= var1.field_78896_c && this.field_78896_c <= var1.field_78892_f && this.field_78894_e >= var1.field_78895_b && this.field_78895_b <= var1.field_78894_e;
   }

   public boolean func_78885_a(int var1, int var2, int var3, int var4) {
      return this.field_78893_d >= var1 && this.field_78897_a <= var3 && this.field_78892_f >= var2 && this.field_78896_c <= var4;
   }

   public void func_78888_b(MutableBoundingBox var1) {
      this.field_78897_a = Math.min(this.field_78897_a, var1.field_78897_a);
      this.field_78895_b = Math.min(this.field_78895_b, var1.field_78895_b);
      this.field_78896_c = Math.min(this.field_78896_c, var1.field_78896_c);
      this.field_78893_d = Math.max(this.field_78893_d, var1.field_78893_d);
      this.field_78894_e = Math.max(this.field_78894_e, var1.field_78894_e);
      this.field_78892_f = Math.max(this.field_78892_f, var1.field_78892_f);
   }

   public void func_78886_a(int var1, int var2, int var3) {
      this.field_78897_a += var1;
      this.field_78895_b += var2;
      this.field_78896_c += var3;
      this.field_78893_d += var1;
      this.field_78894_e += var2;
      this.field_78892_f += var3;
   }

   public boolean func_175898_b(Vec3i var1) {
      return var1.func_177958_n() >= this.field_78897_a && var1.func_177958_n() <= this.field_78893_d && var1.func_177952_p() >= this.field_78896_c && var1.func_177952_p() <= this.field_78892_f && var1.func_177956_o() >= this.field_78895_b && var1.func_177956_o() <= this.field_78894_e;
   }

   public Vec3i func_175896_b() {
      return new Vec3i(this.field_78893_d - this.field_78897_a, this.field_78894_e - this.field_78895_b, this.field_78892_f - this.field_78896_c);
   }

   public int func_78883_b() {
      return this.field_78893_d - this.field_78897_a + 1;
   }

   public int func_78882_c() {
      return this.field_78894_e - this.field_78895_b + 1;
   }

   public int func_78880_d() {
      return this.field_78892_f - this.field_78896_c + 1;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x0", this.field_78897_a).add("y0", this.field_78895_b).add("z0", this.field_78896_c).add("x1", this.field_78893_d).add("y1", this.field_78894_e).add("z1", this.field_78892_f).toString();
   }

   public NBTTagIntArray func_151535_h() {
      return new NBTTagIntArray(new int[]{this.field_78897_a, this.field_78895_b, this.field_78896_c, this.field_78893_d, this.field_78894_e, this.field_78892_f});
   }
}
