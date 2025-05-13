package net.minecraft.client.renderer.fog.environment;

import javax.annotation.Nullable;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;

public class AtmosphericFogEnvironment extends AirBasedFogEnvironment {
   private static final int MIN_RAIN_FOG_SKY_LIGHT = 8;
   private static final float RAIN_FOG_START_OFFSET = -160.0F;
   private static final float RAIN_FOG_END_OFFSET = -256.0F;
   private float rainFogMultiplier;

   public AtmosphericFogEnvironment() {
      super();
   }

   public void setupFog(FogData var1, Entity var2, BlockPos var3, ClientLevel var4, float var5, DeltaTracker var6) {
      Biome var7 = (Biome)var4.getBiome(var3).value();
      float var8 = var6.getGameTimeDeltaTicks();
      boolean var9 = var7.hasPrecipitation();
      float var10 = Mth.clamp(((float)var4.getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(var3) - 8.0F) / 7.0F, 0.0F, 1.0F);
      float var11 = var4.getRainLevel(var6.getGameTimeDeltaPartialTick(false)) * var10 * (var9 ? 1.0F : 0.5F);
      this.rainFogMultiplier += (var11 - this.rainFogMultiplier) * var8 * 0.2F;
      var1.environmentalStart = this.rainFogMultiplier * -160.0F;
      var1.environmentalEnd = 1024.0F + -256.0F * this.rainFogMultiplier;
      var1.skyEnd = var5;
      var1.cloudEnd = (float)((Integer)Minecraft.getInstance().options.cloudRange().get() * 16);
   }

   public boolean isApplicable(@Nullable FogType var1, Entity var2) {
      return var1 == FogType.ATMOSPHERIC;
   }
}
