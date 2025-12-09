package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jspecify.annotations.Nullable;

public class WaterFogEnvironment extends FogEnvironment {
   public WaterFogEnvironment() {
      super();
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      float var6 = var5.getGameTimeDeltaPartialTick(false);
      var1.environmentalStart = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_START_DISTANCE, var6);
      var1.environmentalEnd = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_END_DISTANCE, var6);
      Entity var8 = var2.entity();
      if (var8 instanceof LocalPlayer var7) {
         var1.environmentalEnd *= Math.max(0.25F, var7.getWaterVision());
      }

      var1.skyEnd = var1.environmentalEnd;
      var1.cloudEnd = var1.environmentalEnd;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.WATER;
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      return (Integer)var2.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_COLOR, var4);
   }
}
