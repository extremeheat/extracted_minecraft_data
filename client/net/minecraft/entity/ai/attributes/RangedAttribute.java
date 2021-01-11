package net.minecraft.entity.ai.attributes;

import net.minecraft.util.MathHelper;

public class RangedAttribute extends BaseAttribute {
   private final double field_111120_a;
   private final double field_111118_b;
   private String field_111119_c;

   public RangedAttribute(IAttribute var1, String var2, double var3, double var5, double var7) {
      super(var1, var2, var3);
      this.field_111120_a = var5;
      this.field_111118_b = var7;
      if (var5 > var7) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (var3 < var5) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (var3 > var7) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute func_111117_a(String var1) {
      this.field_111119_c = var1;
      return this;
   }

   public String func_111116_f() {
      return this.field_111119_c;
   }

   public double func_111109_a(double var1) {
      var1 = MathHelper.func_151237_a(var1, this.field_111120_a, this.field_111118_b);
      return var1;
   }
}
