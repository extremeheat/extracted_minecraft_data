package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.PanoramicScreenshotParameters;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class AtmosphericFogEnvironment extends FogEnvironment {
   private static final int MIN_RAIN_FOG_SKY_LIGHT = 8;
   private static final float RAIN_FOG_START_OFFSET = -160.0F;
   private static final float RAIN_FOG_END_OFFSET = -256.0F;
   private float rainFogMultiplier;

   public AtmosphericFogEnvironment() {
      super();
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      int var5 = (Integer)var2.attributeProbe().getValue(EnvironmentAttributes.FOG_COLOR, var4);
      if (var3 >= 4) {
         float var6 = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.SUN_ANGLE, var4) * 0.017453292F;
         float var7 = Mth.sin((double)var6) > 0.0F ? -1.0F : 1.0F;
         PanoramicScreenshotParameters var8 = Minecraft.getInstance().gameRenderer.getPanoramicScreenshotParameters();
         Vector3fc var9 = var8 != null ? var8.forwardVector() : var2.forwardVector();
         float var10 = var9.dot(var7, 0.0F, 0.0F);
         if (var10 > 0.0F) {
            int var11 = (Integer)var2.attributeProbe().getValue(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, var4);
            float var12 = ARGB.alphaFloat(var11);
            if (var12 > 0.0F) {
               var5 = ARGB.srgbLerp(var10 * var12, var5, ARGB.opaque(var11));
            }
         }
      }

      int var14 = (Integer)var2.attributeProbe().getValue(EnvironmentAttributes.SKY_COLOR, var4);
      var14 = applyWeatherDarken(var14, var1.getRainLevel(var4), var1.getThunderLevel(var4));
      float var16 = Math.min((Float)var2.attributeProbe().getValue(EnvironmentAttributes.SKY_FOG_END_DISTANCE, var4) / 16.0F, (float)var3);
      float var17 = Mth.clampedLerp(var16 / 32.0F, 0.25F, 1.0F);
      var17 = 1.0F - (float)Math.pow((double)var17, 0.25);
      var5 = ARGB.srgbLerp(var17, var5, var14);
      return var5;
   }

   private static int applyWeatherDarken(int var0, float var1, float var2) {
      if (var1 > 0.0F) {
         float var3 = 1.0F - var1 * 0.5F;
         float var4 = 1.0F - var1 * 0.4F;
         var0 = ARGB.scaleRGB(var0, var3, var3, var4);
      }

      if (var2 > 0.0F) {
         var0 = ARGB.scaleRGB(var0, 1.0F - var2 * 0.5F);
      }

      return var0;
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      this.updateRainFogState(var2, var3, var5);
      float var6 = var5.getGameTimeDeltaPartialTick(false);
      var1.environmentalStart = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.FOG_START_DISTANCE, var6);
      var1.environmentalEnd = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.FOG_END_DISTANCE, var6);
      var1.environmentalStart += -160.0F * this.rainFogMultiplier;
      float var7 = Math.min(96.0F, var1.environmentalEnd);
      var1.environmentalEnd = Math.max(var7, var1.environmentalEnd + -256.0F * this.rainFogMultiplier);
      var1.skyEnd = Math.min(var4, (Float)var2.attributeProbe().getValue(EnvironmentAttributes.SKY_FOG_END_DISTANCE, var6));
      var1.cloudEnd = Math.min((float)((Integer)Minecraft.getInstance().options.cloudRange().get() * 16), (Float)var2.attributeProbe().getValue(EnvironmentAttributes.CLOUD_FOG_END_DISTANCE, var6));
      if (Minecraft.getInstance().gui.getBossOverlay().shouldCreateWorldFog()) {
         var1.environmentalStart = Math.min(var1.environmentalStart, 10.0F);
         var1.environmentalEnd = Math.min(var1.environmentalEnd, 96.0F);
         var1.skyEnd = var1.environmentalEnd;
         var1.cloudEnd = var1.environmentalEnd;
      }

   }

   private void updateRainFogState(Camera var1, ClientLevel var2, DeltaTracker var3) {
      BlockPos var4 = var1.blockPosition();
      Biome var5 = (Biome)var2.getBiome(var4).value();
      float var6 = var3.getGameTimeDeltaTicks();
      float var7 = var3.getGameTimeDeltaPartialTick(false);
      boolean var8 = var5.hasPrecipitation();
      float var9 = Mth.clamp(((float)var2.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(var4) - 8.0F) / 7.0F, 0.0F, 1.0F);
      float var10 = var2.getRainLevel(var7) * var9 * (var8 ? 1.0F : 0.5F);
      this.rainFogMultiplier += (var10 - this.rainFogMultiplier) * var6 * 0.2F;
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.ATMOSPHERIC;
   }
}
