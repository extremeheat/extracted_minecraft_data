package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;

public final class PotionUtil {
   public static String func_188410_a(PotionEffect var0, float var1) {
      if (var0.func_100011_g()) {
         return "**:**";
      } else {
         int var2 = MathHelper.func_76141_d((float)var0.func_76459_b() * var1);
         return StringUtils.func_76337_a(var2);
      }
   }

   public static boolean func_205135_a(EntityLivingBase var0) {
      return var0.func_70644_a(MobEffects.field_76422_e) || var0.func_70644_a(MobEffects.field_205136_C);
   }

   public static int func_205134_b(EntityLivingBase var0) {
      int var1 = 0;
      int var2 = 0;
      if (var0.func_70644_a(MobEffects.field_76422_e)) {
         var1 = var0.func_70660_b(MobEffects.field_76422_e).func_76458_c();
      }

      if (var0.func_70644_a(MobEffects.field_205136_C)) {
         var2 = var0.func_70660_b(MobEffects.field_205136_C).func_76458_c();
      }

      return Math.max(var1, var2);
   }

   public static boolean func_205133_c(EntityLivingBase var0) {
      return var0.func_70644_a(MobEffects.field_76427_o) || var0.func_70644_a(MobEffects.field_205136_C);
   }
}
