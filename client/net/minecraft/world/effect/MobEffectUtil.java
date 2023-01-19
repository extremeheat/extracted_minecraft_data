package net.minecraft.world.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class MobEffectUtil {
   public MobEffectUtil() {
      super();
   }

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

   public static List<ServerPlayer> addEffectToPlayersAround(ServerLevel var0, @Nullable Entity var1, Vec3 var2, double var3, MobEffectInstance var5, int var6) {
      MobEffect var7 = var5.getEffect();
      List var8 = var0.getPlayers(
         var7x -> var7x.gameMode.isSurvival()
               && (var1 == null || !var1.isAlliedTo(var7x))
               && var2.closerThan(var7x.position(), var3)
               && (!var7x.hasEffect(var7) || var7x.getEffect(var7).getAmplifier() < var5.getAmplifier() || var7x.getEffect(var7).getDuration() < var6)
      );
      var8.forEach(var2x -> var2x.addEffect(new MobEffectInstance(var5), var1));
      return var8;
   }
}
