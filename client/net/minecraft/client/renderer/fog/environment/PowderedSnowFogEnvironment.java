package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public class PowderedSnowFogEnvironment extends FogEnvironment {
   private static final int COLOR = -6308916;

   public PowderedSnowFogEnvironment() {
      super();
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      return -6308916;
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      if (var2.entity().isSpectator()) {
         var1.environmentalStart = -8.0F;
         var1.environmentalEnd = var4 * 0.5F;
      } else {
         var1.environmentalStart = 0.0F;
         var1.environmentalEnd = 2.0F;
      }

      var1.skyEnd = var1.environmentalEnd;
      var1.cloudEnd = var1.environmentalEnd;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.POWDER_SNOW;
   }
}
