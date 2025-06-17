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

public class BlindnessFogEnvironment extends MobEffectFogEnvironment {
   public BlindnessFogEnvironment() {
      super();
   }

   public Holder<MobEffect> getMobEffect() {
      return MobEffects.BLINDNESS;
   }

   public void setupFog(FogData var1, Entity var2, BlockPos var3, ClientLevel var4, float var5, DeltaTracker var6) {
      if (var2 instanceof LivingEntity var7) {
         MobEffectInstance var8 = var7.getEffect(this.getMobEffect());
         if (var8 != null) {
            float var9 = var8.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)var8.getDuration() / 20.0F), var5, 5.0F);
            var1.environmentalStart = var9 * 0.25F;
            var1.environmentalEnd = var9;
            var1.skyEnd = var9 * 0.8F;
            var1.cloudEnd = var9 * 0.8F;
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
