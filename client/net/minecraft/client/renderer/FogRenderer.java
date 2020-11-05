package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class FogRenderer {
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public static void setupColor(Camera var0, float var1, ClientLevel var2, int var3, float var4) {
      FluidState var5 = var0.getFluidInCamera();
      int var8;
      float var15;
      float var16;
      float var17;
      float var21;
      float var22;
      if (var5.is(FluidTags.WATER)) {
         long var6 = Util.getMillis();
         var8 = var2.getBiome(new BlockPos(var0.getPosition())).getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = var8;
            previousBiomeFog = var8;
            biomeChangedTime = var6;
         }

         int var9 = targetBiomeFog >> 16 & 255;
         int var10 = targetBiomeFog >> 8 & 255;
         int var11 = targetBiomeFog & 255;
         int var12 = previousBiomeFog >> 16 & 255;
         int var13 = previousBiomeFog >> 8 & 255;
         int var14 = previousBiomeFog & 255;
         var15 = Mth.clamp((float)(var6 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         var16 = Mth.lerp(var15, (float)var12, (float)var9);
         var17 = Mth.lerp(var15, (float)var13, (float)var10);
         float var18 = Mth.lerp(var15, (float)var14, (float)var11);
         fogRed = var16 / 255.0F;
         fogGreen = var17 / 255.0F;
         fogBlue = var18 / 255.0F;
         if (targetBiomeFog != var8) {
            targetBiomeFog = var8;
            previousBiomeFog = Mth.floor(var16) << 16 | Mth.floor(var17) << 8 | Mth.floor(var18);
            biomeChangedTime = var6;
         }
      } else if (var5.is(FluidTags.LAVA)) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeChangedTime = -1L;
      } else {
         float var19 = 0.25F + 0.75F * (float)var3 / 32.0F;
         var19 = 1.0F - (float)Math.pow((double)var19, 0.25D);
         Vec3 var7 = var2.getSkyColor(var0.getBlockPosition(), var1);
         var21 = (float)var7.x;
         var22 = (float)var7.y;
         float var24 = (float)var7.z;
         float var25 = Mth.clamp(Mth.cos(var2.getTimeOfDay(var1) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager var26 = var2.getBiomeManager();
         Vec3 var27 = var0.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
         Vec3 var28 = CubicSampler.gaussianSampleVec3(var27, (var3x, var4x, var5x) -> {
            return var2.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(var26.getNoiseBiomeAtQuart(var3x, var4x, var5x).getFogColor()), var25);
         });
         fogRed = (float)var28.x();
         fogGreen = (float)var28.y();
         fogBlue = (float)var28.z();
         if (var3 >= 4) {
            var15 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var29 = new Vector3f(var15, 0.0F, 0.0F);
            var17 = var0.getLookVector().dot(var29);
            if (var17 < 0.0F) {
               var17 = 0.0F;
            }

            if (var17 > 0.0F) {
               float[] var30 = var2.effects().getSunriseColor(var2.getTimeOfDay(var1), var1);
               if (var30 != null) {
                  var17 *= var30[3];
                  fogRed = fogRed * (1.0F - var17) + var30[0] * var17;
                  fogGreen = fogGreen * (1.0F - var17) + var30[1] * var17;
                  fogBlue = fogBlue * (1.0F - var17) + var30[2] * var17;
               }
            }
         }

         fogRed += (var21 - fogRed) * var19;
         fogGreen += (var22 - fogGreen) * var19;
         fogBlue += (var24 - fogBlue) * var19;
         var15 = var2.getRainLevel(var1);
         if (var15 > 0.0F) {
            var16 = 1.0F - var15 * 0.5F;
            var17 = 1.0F - var15 * 0.4F;
            fogRed *= var16;
            fogGreen *= var16;
            fogBlue *= var17;
         }

         var16 = var2.getThunderLevel(var1);
         if (var16 > 0.0F) {
            var17 = 1.0F - var16 * 0.5F;
            fogRed *= var17;
            fogGreen *= var17;
            fogBlue *= var17;
         }

         biomeChangedTime = -1L;
      }

      double var20 = (var0.getPosition().y - (double)var2.getMinBuildHeight()) * var2.getLevelData().getClearColorScale();
      if (var0.getEntity() instanceof LivingEntity && ((LivingEntity)var0.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         var8 = ((LivingEntity)var0.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if (var8 < 20) {
            var20 *= (double)(1.0F - (float)var8 / 20.0F);
         } else {
            var20 = 0.0D;
         }
      }

      if (var20 < 1.0D && !var5.is(FluidTags.LAVA)) {
         if (var20 < 0.0D) {
            var20 = 0.0D;
         }

         var20 *= var20;
         fogRed = (float)((double)fogRed * var20);
         fogGreen = (float)((double)fogGreen * var20);
         fogBlue = (float)((double)fogBlue * var20);
      }

      if (var4 > 0.0F) {
         fogRed = fogRed * (1.0F - var4) + fogRed * 0.7F * var4;
         fogGreen = fogGreen * (1.0F - var4) + fogGreen * 0.6F * var4;
         fogBlue = fogBlue * (1.0F - var4) + fogBlue * 0.6F * var4;
      }

      if (var5.is(FluidTags.WATER)) {
         var21 = 0.0F;
         if (var0.getEntity() instanceof LocalPlayer) {
            LocalPlayer var23 = (LocalPlayer)var0.getEntity();
            var21 = var23.getWaterVision();
         }

         var22 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - var21) + fogRed * var22 * var21;
         fogGreen = fogGreen * (1.0F - var21) + fogGreen * var22 * var21;
         fogBlue = fogBlue * (1.0F - var21) + fogBlue * var22 * var21;
      } else if (var0.getEntity() instanceof LivingEntity && ((LivingEntity)var0.getEntity()).hasEffect(MobEffects.NIGHT_VISION)) {
         var21 = GameRenderer.getNightVisionScale((LivingEntity)var0.getEntity(), var1);
         var22 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - var21) + fogRed * var22 * var21;
         fogGreen = fogGreen * (1.0F - var21) + fogGreen * var22 * var21;
         fogBlue = fogBlue * (1.0F - var21) + fogBlue * var22 * var21;
      }

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.fogDensity(0.0F);
      RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
   }

   public static void setupFog(Camera var0, FogRenderer.FogMode var1, float var2, boolean var3) {
      FluidState var4 = var0.getFluidInCamera();
      Entity var5 = var0.getEntity();
      float var6;
      if (var4.is(FluidTags.WATER)) {
         var6 = 1.0F;
         var6 = 0.05F;
         if (var5 instanceof LocalPlayer) {
            LocalPlayer var7 = (LocalPlayer)var5;
            var6 -= var7.getWaterVision() * var7.getWaterVision() * 0.03F;
            Biome var8 = var7.level.getBiome(var7.blockPosition());
            if (var8.getBiomeCategory() == Biome.BiomeCategory.SWAMP) {
               var6 += 0.005F;
            }
         }

         RenderSystem.fogDensity(var6);
         RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
      } else {
         float var10;
         if (var4.is(FluidTags.LAVA)) {
            if (var5 instanceof LivingEntity && ((LivingEntity)var5).hasEffect(MobEffects.FIRE_RESISTANCE)) {
               var6 = 0.0F;
               var10 = 3.0F;
            } else {
               var6 = 0.25F;
               var10 = 1.0F;
            }
         } else if (var5 instanceof LivingEntity && ((LivingEntity)var5).hasEffect(MobEffects.BLINDNESS)) {
            int var11 = ((LivingEntity)var5).getEffect(MobEffects.BLINDNESS).getDuration();
            float var9 = Mth.lerp(Math.min(1.0F, (float)var11 / 20.0F), var2, 5.0F);
            if (var1 == FogRenderer.FogMode.FOG_SKY) {
               var6 = 0.0F;
               var10 = var9 * 0.8F;
            } else {
               var6 = var9 * 0.25F;
               var10 = var9;
            }
         } else if (var3) {
            var6 = var2 * 0.05F;
            var10 = Math.min(var2, 192.0F) * 0.5F;
         } else if (var1 == FogRenderer.FogMode.FOG_SKY) {
            var6 = 0.0F;
            var10 = var2;
         } else {
            var6 = var2 * 0.75F;
            var10 = var2;
         }

         RenderSystem.fogStart(var6);
         RenderSystem.fogEnd(var10);
         RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
         RenderSystem.setupNvFogDistance();
      }

   }

   public static void levelFogColor() {
      RenderSystem.fog(2918, fogRed, fogGreen, fogBlue, 1.0F);
   }

   public static enum FogMode {
      FOG_SKY,
      FOG_TERRAIN;

      private FogMode() {
      }
   }
}
