package net.minecraft.world.effect;

import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;

public final class MobEffectUtil {
   public static String formatDuration(MobEffectInstance var0, float var1) {
      if (var0.isNoCounter()) {
         return "**:**";
      } else {
         int var2 = Mth.floor((float)var0.getDuration() * var1);
         return StringUtil.formatTickDuration(var2);
      }
   }

   public static boolean hasDigSpeed(LivingEntity var0) {
      return var0.hasEffect(MobEffects.DIG_SPEED) || var0.hasEffect(MobEffects.CONDUIT_POWER);
   }

   public static int getDigSpeedAmplification(LivingEntity var0) {
      int var1 = 0;
      int var2 = 0;
      if (var0.hasEffect(MobEffects.DIG_SPEED)) {
         var1 = var0.getEffect(MobEffects.DIG_SPEED).getAmplifier();
      }

      if (var0.hasEffect(MobEffects.CONDUIT_POWER)) {
         var2 = var0.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
      }

      return Math.max(var1, var2);
   }

   public static boolean hasWaterBreathing(LivingEntity var0) {
      return var0.hasEffect(MobEffects.WATER_BREATHING) || var0.hasEffect(MobEffects.CONDUIT_POWER);
   }
}
