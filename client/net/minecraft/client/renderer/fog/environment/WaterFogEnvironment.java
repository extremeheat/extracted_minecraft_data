package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;

public class WaterFogEnvironment extends FogEnvironment {
   private static final int WATER_FOG_DISTANCE = 96;

   public WaterFogEnvironment() {
      super();
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      var1.environmentalStart = -8.0F;
      var1.environmentalEnd = 96.0F;
      Entity var7 = var2.entity();
      if (var7 instanceof LocalPlayer var6) {
         var1.environmentalEnd *= Math.max(0.25F, var6.getWaterVision());
         float var8 = (float)var2.cubicBiomeSampler().sampleVec3((var0) -> var0.is(BiomeTags.HAS_CLOSER_WATER_FOG) ? new Vec3(0.85, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0)).x;
         var1.environmentalEnd *= var8;
      }

      var1.skyEnd = var1.environmentalEnd;
      var1.cloudEnd = var1.environmentalEnd;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.WATER;
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      Vec3 var5 = var2.cubicBiomeSampler().sampleVec3((var0) -> Vec3.fromRGB24(((Biome)var0.value()).getWaterFogColor()));
      return ARGB.colorFromFloat(1.0F, (float)var5.x, (float)var5.y, (float)var5.z);
   }
}
