package net.minecraft.client.util;

public class MouseSmoother {
   private double field_199103_a;
   private double field_199104_b;
   private double field_199105_c;

   public MouseSmoother() {
      super();
   }

   public double func_199102_a(double var1, double var3) {
      this.field_199103_a += var1;
      double var5 = this.field_199103_a - this.field_199104_b;
      double var7 = this.field_199105_c + (var5 - this.field_199105_c) * 0.5D;
      double var9 = Math.signum(var5);
      if (var9 * var5 > var9 * this.field_199105_c) {
         var5 = var7;
      }

      this.field_199105_c = var7;
      this.field_199104_b += var5 * var3;
      return var5 * var3;
   }

   public void func_199101_a() {
      this.field_199103_a = 0.0D;
      this.field_199104_b = 0.0D;
      this.field_199105_c = 0.0D;
   }
}
