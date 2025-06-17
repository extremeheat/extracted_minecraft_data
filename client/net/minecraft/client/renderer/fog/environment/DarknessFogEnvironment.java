package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DarknessFogEnvironment extends MobEffectFogEnvironment {
   public DarknessFogEnvironment() {
      super();
   }

   public Holder<MobEffect> getMobEffect() {
      return MobEffects.DARKNESS;
   }

   public void setupFog(FogData var1, Entity var2, BlockPos var3, ClientLevel var4, float var5, DeltaTracker var6) {
      if (var2 instanceof LivingEntity var7) {
         MobEffectInstance var8 = var7.getEffect(this.getMobEffect());
         if (var8 != null) {
            float var9 = Mth.lerp(var8.getBlendFactor(var7, var6.getGameTimeDeltaPartialTick(false)), var5, 15.0F);
            var1.environmentalStart = var9 * 0.75F;
            var1.environmentalEnd = var9;
            var1.skyEnd = var9;
            var1.cloudEnd = var9;
         }
      }

   }

   public float getModifiedDarkness(LivingEntity var1, float var2, float var3) {
      MobEffectInstance var4 = var1.getEffect(this.getMobEffect());
      return var4 != null ? Math.max(var4.getBlendFactor(var1, var3), var2) : var2;
   }
}
