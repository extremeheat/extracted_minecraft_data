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

public class BlindnessFogEnvironment extends MobEffectFogEnvironment {
   public BlindnessFogEnvironment() {
      super();
   }

   public Holder<MobEffect> getMobEffect() {
      return MobEffects.BLINDNESS;
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      Entity var7 = var2.entity();
      if (var7 instanceof LivingEntity var6) {
         MobEffectInstance var9 = var6.getEffect(this.getMobEffect());
         if (var9 != null) {
            float var8 = var9.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)var9.getDuration() / 20.0F), var4, 5.0F);
            var1.environmentalStart = var8 * 0.25F;
            var1.environmentalEnd = var8;
            var1.skyEnd = var8 * 0.8F;
            var1.cloudEnd = var8 * 0.8F;
         }
      }

   }

   public float getModifiedDarkness(LivingEntity var1, float var2, float var3) {
      MobEffectInstance var4 = var1.getEffect(this.getMobEffect());
      if (var4 != null) {
         if (var4.endsWithin(19)) {
            var2 = Math.max((float)var4.getDuration() / 20.0F, var2);
         } else {
            var2 = 1.0F;
         }
      }

      return var2;
   }
}
