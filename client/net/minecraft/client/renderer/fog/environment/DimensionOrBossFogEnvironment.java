package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

public class DimensionOrBossFogEnvironment extends AirBasedFogEnvironment {
   public DimensionOrBossFogEnvironment() {
      super();
   }

   public void setupFog(FogData var1, Entity var2, BlockPos var3, ClientLevel var4, float var5, DeltaTracker var6) {
      var1.environmentalStart = var5 * 0.05F;
      var1.environmentalEnd = Math.min(var5, 192.0F) * 0.5F;
      var1.skyEnd = var1.environmentalEnd;
      var1.cloudEnd = var1.environmentalEnd;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.DIMENSION_OR_BOSS;
   }
}
