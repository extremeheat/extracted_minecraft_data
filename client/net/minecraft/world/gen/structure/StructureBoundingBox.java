package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagIntArray;

public class StructureBoundingBox {
   public int field_78897_a;
   public int field_78895_b;
   public int field_78896_c;
   public int field_78893_d;
   public int field_78894_e;
   public int field_78892_f;

   public StructureBoundingBox() {
      super();
   }

   public StructureBoundingBox(int[] var1) {
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

   public static StructureBoundingBox func_78887_a() {
      return new StructureBoundingBox(2147483647, 2147483647, 2147483647, -2147483648, -2147483648, -2147483648);
   }

   public static StructureBoundingBox func_78889_a(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      switch(var9) {
         case 0:
            return new StructureBoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
         case 1:
            return new StructureBoundingBox(var0 - var8 + 1 + var5, var1 + var4, var2 + var3, var0 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
         case 2:
            return new StructureBoundingBox(var0 + var3, var1 + var4, var2 - var8 + 1 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var5);
         case 3:
            return new StructureBoundingBox(var0 + var5, var1 + var4, var2 + var3, var0 + var8 - 1 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
         default:
            return new StructureBoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      }
   }

   public StructureBoundingBox(StructureBoundingBox var1) {
      super();
      this.field_78897_a = var1.field_78897_a;
      this.field_78895_b = var1.field_78895_b;
      this.field_78896_c = var1.field_78896_c;
      this.field_78893_d = var1.field_78893_d;
      this.field_78894_e = var1.field_78894_e;
      this.field_78892_f = var1.field_78892_f;
   }

   public StructureBoundingBox(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.field_78897_a = var1;
      this.field_78895_b = var2;
      this.field_78896_c = var3;
      this.field_78893_d = var4;
      this.field_78894_e = var5;
      this.field_78892_f = var6;
   }

   public StructureBoundingBox(int var1, int var2, int var3, int var4) {
      super();
      this.field_78897_a = var1;
      this.field_78896_c = var2;
      this.field_78893_d = var3;
      this.field_78892_f = var4;
      this.field_78895_b = 1;
      this.field_78894_e = 512;
   }

   public boolean func_78884_a(StructureBoundingBox var1) {
      return this.field_78893_d >= var1.field_78897_a
         && this.field_78897_a <= var1.field_78893_d
         && this.field_78892_f >= var1.field_78896_c
         && this.field_78896_c <= var1.field_78892_f
         && this.field_78894_e >= var1.field_78895_b
         && this.field_78895_b <= var1.field_78894_e;
   }

   public boolean func_78885_a(int var1, int var2, int var3, int var4) {
      return this.field_78893_d >= var1 && this.field_78897_a <= var3 && this.field_78892_f >= var2 && this.field_78896_c <= var4;
   }

   public void func_78888_b(StructureBoundingBox var1) {
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

   public boolean func_78890_b(int var1, int var2, int var3) {
      return var1 >= this.field_78897_a
         && var1 <= this.field_78893_d
         && var3 >= this.field_78896_c
         && var3 <= this.field_78892_f
         && var2 >= this.field_78895_b
         && var2 <= this.field_78894_e;
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

   public int func_78881_e() {
      return this.field_78897_a + (this.field_78893_d - this.field_78897_a + 1) / 2;
   }

   public int func_78879_f() {
      return this.field_78895_b + (this.field_78894_e - this.field_78895_b + 1) / 2;
   }

   public int func_78891_g() {
      return this.field_78896_c + (this.field_78892_f - this.field_78896_c + 1) / 2;
   }

   @Override
   public String toString() {
      return "("
         + this.field_78897_a
         + ", "
         + this.field_78895_b
         + ", "
         + this.field_78896_c
         + "; "
         + this.field_78893_d
         + ", "
         + this.field_78894_e
         + ", "
         + this.field_78892_f
         + ")";
   }

   public NBTTagIntArray func_151535_h() {
      return new NBTTagIntArray(
         new int[]{this.field_78897_a, this.field_78895_b, this.field_78896_c, this.field_78893_d, this.field_78894_e, this.field_78892_f}
      );
   }
}
