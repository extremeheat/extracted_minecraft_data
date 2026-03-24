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

   public void setupFog(final FogData fog, final Camera camera, final ClientLevel level, final float renderDistance, final DeltaTracker deltaTracker) {
      Entity var7 = camera.entity();
      if (var7 instanceof LivingEntity livingEntity) {
         MobEffectInstance effect = livingEntity.getEffect(this.getMobEffect());
         if (effect != null) {
            float distance = effect.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)effect.getDuration() / 20.0F), renderDistance, 5.0F);
            fog.environmentalStart = distance * 0.25F;
            fog.environmentalEnd = distance;
            fog.skyEnd = distance * 0.8F;
            fog.cloudEnd = distance * 0.8F;
         }
      }

   }

   public float getModifiedDarkness(final LivingEntity entity, float darkness, final float partialTickTime) {
      MobEffectInstance instance = entity.getEffect(this.getMobEffect());
      if (instance != null) {
         if (instance.endsWithin(19)) {
            darkness = Math.max((float)instance.getDuration() / 20.0F, darkness);
         } else {
            darkness = 1.0F;
         }
      }

      return darkness;
   }
}
