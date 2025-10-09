package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public class DimensionOrBossFogEnvironment extends AirBasedFogEnvironment {
   public DimensionOrBossFogEnvironment() {
      super();
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      var1.environmentalStart = var4 * 0.05F;
      var1.environmentalEnd = Math.min(var4, 192.0F) * 0.5F;
      var1.skyEnd = var1.environmentalEnd;
      var1.cloudEnd = var1.environmentalEnd;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.DIMENSION_OR_BOSS;
   }
}
