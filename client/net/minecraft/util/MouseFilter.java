package net.minecraft.util;

public class MouseFilter {
   private float field_76336_a;
   private float field_76334_b;
   private float field_76335_c;

   public MouseFilter() {
      super();
   }

   public float func_76333_a(float var1, float var2) {
      this.field_76336_a += var1;
      var1 = (this.field_76336_a - this.field_76334_b) * var2;
      this.field_76335_c += (var1 - this.field_76335_c) * 0.5F;
      if (var1 > 0.0F && var1 > this.field_76335_c || var1 < 0.0F && var1 < this.field_76335_c) {
         var1 = this.field_76335_c;
      }

      this.field_76334_b += var1;
      return var1;
   }
}
