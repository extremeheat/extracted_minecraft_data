package net.minecraft.client.renderer.fog.environment;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public abstract class AirBasedFogEnvironment extends FogEnvironment {
   public AirBasedFogEnvironment() {
      super();
   }

   public int getBaseColor(ClientLevel var1, Camera var2, int var3, float var4) {
      float var5 = Mth.clamp(Mth.cos(var1.getTimeOfDay(var4) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
      Vec3 var6 = var1.effects().getBrightnessDependentFogColor(var2.cubicBiomeSampler().sampleVec3((var0) -> Vec3.fromRGB24(((Biome)var0.value()).getFogColor())), var5);
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

      int var21 = var1.getSkyColor(var2, var4);
      float var22 = ARGB.redFloat(var21);
      float var24 = ARGB.greenFloat(var21);
      float var25 = ARGB.blueFloat(var21);
      float var14 = 0.25F + 0.75F * (float)var3 / 32.0F;
      var14 = 1.0F - (float)Math.pow((double)var14, 0.25);
      var7 += (var22 - var7) * var14;
      var8 += (var24 - var8) * var14;
      var9 += (var25 - var9) * var14;
      float var15 = var1.getRainLevel(var4);
      if (var15 > 0.0F) {
         float var16 = 1.0F - var15 * 0.5F;
         float var17 = 1.0F - var15 * 0.4F;
         var7 *= var16;
         var8 *= var16;
         var9 *= var17;
      }

      float var27 = var1.getThunderLevel(var4);
      if (var27 > 0.0F) {
         float var28 = 1.0F - var27 * 0.5F;
         var7 *= var28;
         var8 *= var28;
         var9 *= var28;
      }

      return ARGB.colorFromFloat(1.0F, var7, var8, var9);
   }
}
