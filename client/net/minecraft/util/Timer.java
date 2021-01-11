package net.minecraft.util;

import net.minecraft.client.Minecraft;

public class Timer {
   float field_74282_a;
   private double field_74276_f;
   public int field_74280_b;
   public float field_74281_c;
   public float field_74278_d = 1.0F;
   public float field_74279_e;
   private long field_74277_g;
   private long field_74284_h;
   private long field_74285_i;
   private double field_74283_j = 1.0D;

   public Timer(float var1) {
      super();
      this.field_74282_a = var1;
      this.field_74277_g = Minecraft.func_71386_F();
      this.field_74284_h = System.nanoTime() / 1000000L;
   }

   public void func_74275_a() {
      long var1 = Minecraft.func_71386_F();
      long var3 = var1 - this.field_74277_g;
      long var5 = System.nanoTime() / 1000000L;
      double var7 = (double)var5 / 1000.0D;
      if (var3 <= 1000L && var3 >= 0L) {
         this.field_74285_i += var3;
         if (this.field_74285_i > 1000L) {
            long var9 = var5 - this.field_74284_h;
            double var11 = (double)this.field_74285_i / (double)var9;
            this.field_74283_j += (var11 - this.field_74283_j) * 0.20000000298023224D;
            this.field_74284_h = var5;
            this.field_74285_i = 0L;
         }

         if (this.field_74285_i < 0L) {
            this.field_74284_h = var5;
         }
      } else {
         this.field_74276_f = var7;
      }

      this.field_74277_g = var1;
      double var13 = (var7 - this.field_74276_f) * this.field_74283_j;
      this.field_74276_f = var7;
      var13 = MathHelper.func_151237_a(var13, 0.0D, 1.0D);
      this.field_74279_e = (float)((double)this.field_74279_e + var13 * (double)this.field_74278_d * (double)this.field_74282_a);
      this.field_74280_b = (int)this.field_74279_e;
      this.field_74279_e -= (float)this.field_74280_b;
      if (this.field_74280_b > 10) {
         this.field_74280_b = 10;
      }

      this.field_74281_c = this.field_74279_e;
   }
}
