package net.minecraft.entity;

import net.minecraft.util.MathHelper;

public enum Entity$EnumEntitySize {
   SIZE_1,
   SIZE_2,
   SIZE_3,
   SIZE_4,
   SIZE_5,
   SIZE_6;

   private Entity$EnumEntitySize() {
   }

   public int func_75630_a(double var1) {
      double var3 = var1 - ((double)MathHelper.func_76128_c(var1) + 0.5);
      switch(this) {
         case SIZE_1:
            return (var3 < 0.0 ? !(var3 < -0.3125) : !(var3 < 0.3125)) ? MathHelper.func_76128_c(var1 * 32.0) : MathHelper.func_76143_f(var1 * 32.0);
         case SIZE_2:
            if (var3 < 0.0 ? !(var3 < -0.3125) : !(var3 < 0.3125)) {
               return MathHelper.func_76143_f(var1 * 32.0);
            }

            return MathHelper.func_76128_c(var1 * 32.0);
         case SIZE_3:
            if (var3 > 0.0) {
               return MathHelper.func_76128_c(var1 * 32.0);
            }

            return MathHelper.func_76143_f(var1 * 32.0);
         case SIZE_4:
            if (var3 < 0.0 ? !(var3 < -0.1875) : !(var3 < 0.1875)) {
               return MathHelper.func_76128_c(var1 * 32.0);
            }

            return MathHelper.func_76143_f(var1 * 32.0);
         case SIZE_5:
            if (var3 < 0.0 ? !(var3 < -0.1875) : !(var3 < 0.1875)) {
               return MathHelper.func_76143_f(var1 * 32.0);
            }

            return MathHelper.func_76128_c(var1 * 32.0);
         case SIZE_6:
         default:
            return var3 > 0.0 ? MathHelper.func_76143_f(var1 * 32.0) : MathHelper.func_76128_c(var1 * 32.0);
      }
   }
}
