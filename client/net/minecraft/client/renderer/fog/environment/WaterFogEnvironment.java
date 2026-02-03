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

   public void setupFog(final FogData fog, final Camera camera, final ClientLevel level, final float renderDistance, final DeltaTracker deltaTracker) {
      float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
      fog.environmentalStart = (Float)camera.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_START_DISTANCE, partialTicks);
      fog.environmentalEnd = (Float)camera.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_END_DISTANCE, partialTicks);
      Entity var8 = camera.entity();
      if (var8 instanceof LocalPlayer player) {
         fog.environmentalEnd *= Math.max(0.25F, player.getWaterVision());
      }

      fog.skyEnd = fog.environmentalEnd;
      fog.cloudEnd = fog.environmentalEnd;
   }

   public boolean isApplicable(final @Nullable FogType fogType, final Entity entity) {
      return fogType == FogType.WATER;
   }

   public int getBaseColor(final ClientLevel level, final Camera camera, final int renderDistance, final float partialTicks) {
      return (Integer)camera.attributeProbe().getValue(EnvironmentAttributes.WATER_FOG_COLOR, partialTicks);
   }
}
