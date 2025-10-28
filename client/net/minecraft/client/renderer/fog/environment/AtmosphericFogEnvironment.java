package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
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
      float var5 = Mth.clamp(Mth.cos(var1.getTimeOfDay(var4) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
      Vec3 var6 = var1.effects().getBrightnessDependentFogColor(new Vec3(ARGB.vector3fFromRGB24((Integer)var2.attributeProbe().getValue(EnvironmentAttributes.FOG_COLOR, var4))), var5);
      float var7 = (float)var6.x();
      float var8 = (float)var6.y();
      float var9 = (float)var6.z();
      if (var3 >= 4) {
         float var10 = Mth.sin(var1.getSunAngle(var4)) > 0.0F ? -1.0F : 1.0F;
         Vector3f var11 = new Vector3f(var10, 0.0F, 0.0F);
         float var12 = var2.forwardVector().dot(var11);
         if (var12 > 0.0F && var1.effects().isSunriseOrSunset(var1.getTimeOfDay(var4))) {
            int var13 = var1.effects().getSunriseOrSunsetColor(var1.getTimeOfDay(var4));
            var12 *= ARGB.alphaFloat(var13);
            var7 = Mth.lerp(var12, var7, ARGB.redFloat(var13));
            var8 = Mth.lerp(var12, var8, ARGB.greenFloat(var13));
            var9 = Mth.lerp(var12, var9, ARGB.blueFloat(var13));
         }
      }

      int var22 = var1.getSkyColor(var2, var4);
      float var23 = ARGB.redFloat(var22);
      float var25 = ARGB.greenFloat(var22);
      float var26 = ARGB.blueFloat(var22);
      float var14 = Math.min((Float)var2.attributeProbe().getValue(EnvironmentAttributes.SKY_FOG_END_DISTANCE, var4) / 16.0F, (float)var3);
      float var15 = Mth.clampedLerp(0.25F, 1.0F, var14 / 32.0F);
      var15 = 1.0F - (float)Math.pow((double)var15, 0.25);
      var7 += (var23 - var7) * var15;
      var8 += (var25 - var8) * var15;
      var9 += (var26 - var9) * var15;
      float var16 = var1.getRainLevel(var4);
      if (var16 > 0.0F) {
         float var17 = 1.0F - var16 * 0.5F;
         float var18 = 1.0F - var16 * 0.4F;
         var7 *= var17;
         var8 *= var17;
         var9 *= var18;
      }

      float var28 = var1.getThunderLevel(var4);
      if (var28 > 0.0F) {
         float var29 = 1.0F - var28 * 0.5F;
         var7 *= var29;
         var8 *= var29;
         var9 *= var29;
      }

      return ARGB.colorFromFloat(1.0F, var7, var8, var9);
   }

   public void setupFog(FogData var1, Camera var2, ClientLevel var3, float var4, DeltaTracker var5) {
      this.updateRainFogState(var2, var3, var5);
      float var6 = var5.getGameTimeDeltaPartialTick(false);
      var1.environmentalStart = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.FOG_START_DISTANCE, var6);
      var1.environmentalEnd = (Float)var2.attributeProbe().getValue(EnvironmentAttributes.FOG_END_DISTANCE, var6);
      var1.environmentalStart += -160.0F * this.rainFogMultiplier;
      var1.environmentalEnd += -256.0F * this.rainFogMultiplier;
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
