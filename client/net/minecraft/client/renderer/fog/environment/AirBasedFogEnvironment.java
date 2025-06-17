package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AirBasedFogEnvironment extends FogEnvironment {
   public AirBasedFogEnvironment() {
      super();
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      float var5 = Mth.clamp(Mth.cos(var1.getTimeOfDay(var4) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
      BiomeManager var6 = var1.getBiomeManager();
      Vec3 var7 = var2.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
      Vec3 var8 = var1.effects().getBrightnessDependentFogColor(CubicSampler.gaussianSampleVec3(var7, (var1x, var2x, var3x) -> Vec3.fromRGB24(((Biome)var6.getNoiseBiomeAtQuart(var1x, var2x, var3x).value()).getFogColor())), var5);
      float var9 = (float)var8.x();
      float var10 = (float)var8.y();
      float var11 = (float)var8.z();
      if (var3 >= 4) {
         float var12 = Mth.sin(var1.getSunAngle(var4)) > 0.0F ? -1.0F : 1.0F;
         Vector3f var13 = new Vector3f(var12, 0.0F, 0.0F);
         float var14 = var2.getLookVector().dot(var13);
         if (var14 > 0.0F && var1.effects().isSunriseOrSunset(var1.getTimeOfDay(var4))) {
            int var15 = var1.effects().getSunriseOrSunsetColor(var1.getTimeOfDay(var4));
            var14 *= ARGB.alphaFloat(var15);
            var9 = Mth.lerp(var14, var9, ARGB.redFloat(var15));
            var10 = Mth.lerp(var14, var10, ARGB.greenFloat(var15));
            var11 = Mth.lerp(var14, var11, ARGB.blueFloat(var15));
         }
      }

      int var23 = var1.getSkyColor(var2.getPosition(), var4);
      float var24 = ARGB.redFloat(var23);
      float var26 = ARGB.greenFloat(var23);
      float var27 = ARGB.blueFloat(var23);
      float var16 = 0.25F + 0.75F * (float)var3 / 32.0F;
      var16 = 1.0F - (float)Math.pow((double)var16, 0.25);
      var9 += (var24 - var9) * var16;
      var10 += (var26 - var10) * var16;
      var11 += (var27 - var11) * var16;
      float var17 = var1.getRainLevel(var4);
      if (var17 > 0.0F) {
         float var18 = 1.0F - var17 * 0.5F;
         float var19 = 1.0F - var17 * 0.4F;
         var9 *= var18;
         var10 *= var18;
         var11 *= var19;
      }

      float var29 = var1.getThunderLevel(var4);
      if (var29 > 0.0F) {
         float var30 = 1.0F - var29 * 0.5F;
         var9 *= var30;
         var10 *= var30;
         var11 *= var30;
      }

      return ARGB.colorFromFloat(1.0F, var9, var10, var11);
   }
}
