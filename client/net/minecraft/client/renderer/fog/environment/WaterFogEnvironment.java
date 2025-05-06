package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;

public class WaterFogEnvironment extends FogEnvironment {
   private static final int WATER_FOG_DISTANCE = 96;
   private static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public WaterFogEnvironment() {
      super();
   }

   public void setupFog(FogData var1, Entity var2, BlockPos var3, ClientLevel var4, float var5, DeltaTracker var6) {
      var1.environmentalStart = -8.0F;
      var1.environmentalEnd = 96.0F;
      if (var2 instanceof LocalPlayer var7) {
         var1.environmentalEnd *= Math.max(0.25F, var7.getWaterVision());
         if (var4.getBiome(var3).is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
            var1.environmentalEnd *= 0.85F;
         }
      }

      var1.skyEnd = var1.environmentalEnd;
      var1.cloudEnd = var1.environmentalEnd;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.WATER;
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      long var5 = Util.getMillis();
      int var7 = ((Biome)var1.getBiome(var2.getBlockPosition()).value()).getWaterFogColor();
      if (biomeChangedTime < 0L) {
         targetBiomeFog = var7;
         previousBiomeFog = var7;
         biomeChangedTime = var5;
      }

      float var8 = Mth.clamp((float)(var5 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
      int var9 = ARGB.lerp(var8, previousBiomeFog, targetBiomeFog);
      if (targetBiomeFog != var7) {
         targetBiomeFog = var7;
         previousBiomeFog = var9;
         biomeChangedTime = var5;
      }

      return var9;
   }

   public void onNotApplicable() {
      biomeChangedTime = -1L;
   }
}
