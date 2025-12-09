package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
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

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      Entity var7 = var2.entity();
      if (var7 instanceof LivingEntity var6) {
         MobEffectInstance var9 = var6.getEffect(this.getMobEffect());
         if (var9 != null) {
            float var8 = Mth.lerp(var9.getBlendFactor(var6, var5.getGameTimeDeltaPartialTick(false)), var4, 15.0F);
            var1.environmentalStart = var8 * 0.75F;
            var1.environmentalEnd = var8;
            var1.skyEnd = var8;
            var1.cloudEnd = var8;
         }
      }

   }

   public float getModifiedDarkness(LivingEntity var1, float var2, float var3) {
      MobEffectInstance var4 = var1.getEffect(this.getMobEffect());
      return var4 != null ? Math.max(var4.getBlendFactor(var1, var3), var2) : var2;
   }
}
