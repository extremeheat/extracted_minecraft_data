package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public class LavaFogEnvironment extends FogEnvironment {
   private static final int COLOR = -6743808;

   public LavaFogEnvironment() {
      super();
   }

   public int getBaseColor(final ClientLevel level, final Camera camera, final int renderDistance, final float partialTicks) {
      return -6743808;
   }

   public void setupFog(final FogData fog, final Camera camera, final ClientLevel level, final float renderDistance, final DeltaTracker deltaTracker) {
      if (camera.entity().isSpectator()) {
         fog.environmentalStart = -8.0F;
         fog.environmentalEnd = renderDistance * 0.5F;
      } else {
         label14: {
            Entity var7 = camera.entity();
            if (var7 instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)var7;
               if (livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                  fog.environmentalStart = 0.0F;
                  fog.environmentalEnd = 5.0F;
                  break label14;
               }
            }

            fog.environmentalStart = 0.25F;
            fog.environmentalEnd = 1.0F;
         }
      }

      fog.skyEnd = fog.environmentalEnd;
      fog.cloudEnd = fog.environmentalEnd;
   }

   public boolean isApplicable(final @Nullable FogType fogType, final Entity entity) {
      return fogType == FogType.LAVA;
   }
}
