package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class ScaledResolution {
   private final double field_78332_c;
   private final double field_78329_d;
   private int field_78333_a;
   private int field_78331_b;
   private int field_78330_e;

   public ScaledResolution(Minecraft var1) {
      super();
      this.field_78333_a = var1.field_71443_c;
      this.field_78331_b = var1.field_71440_d;
      this.field_78330_e = 1;
      boolean var2 = var1.func_152349_b();
      int var3 = var1.field_71474_y.field_74335_Z;
      if (var3 == 0) {
         var3 = 1000;
      }

      while(this.field_78330_e < var3 && this.field_78333_a / (this.field_78330_e + 1) >= 320 && this.field_78331_b / (this.field_78330_e + 1) >= 240) {
         ++this.field_78330_e;
      }

      if (var2 && this.field_78330_e % 2 != 0 && this.field_78330_e != 1) {
         --this.field_78330_e;
      }

      this.field_78332_c = (double)this.field_78333_a / (double)this.field_78330_e;
      this.field_78329_d = (double)this.field_78331_b / (double)this.field_78330_e;
      this.field_78333_a = MathHelper.func_76143_f(this.field_78332_c);
      this.field_78331_b = MathHelper.func_76143_f(this.field_78329_d);
   }

   public int func_78326_a() {
      return this.field_78333_a;
   }

   public int func_78328_b() {
      return this.field_78331_b;
   }

   public double func_78327_c() {
      return this.field_78332_c;
   }

   public double func_78324_d() {
      return this.field_78329_d;
   }

   public int func_78325_e() {
      return this.field_78330_e;
   }
}
