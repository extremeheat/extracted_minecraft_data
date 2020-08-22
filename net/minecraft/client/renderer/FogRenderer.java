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
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
         float var15 = Mth.clamp((float)(var6 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float var16 = Mth.lerp(var15, (float)var12, (float)var9);
         float var17 = Mth.lerp(var15, (float)var13, (float)var10);
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
         Vec3 var25 = var2.getFogColor(var1);
         fogRed = (float)var25.x;
         fogGreen = (float)var25.y;
         fogBlue = (float)var25.z;
         float var26;
         float var29;
         if (var3 >= 4) {
            var26 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var27 = new Vector3f(var26, 0.0F, 0.0F);
            var29 = var0.getLookVector().dot(var27);
            if (var29 < 0.0F) {
               var29 = 0.0F;
            }

            if (var29 > 0.0F) {
               float[] var30 = var2.dimension.getSunriseColor(var2.getTimeOfDay(var1), var1);
               if (var30 != null) {
                  var29 *= var30[3];
                  fogRed = fogRed * (1.0F - var29) + var30[0] * var29;
                  fogGreen = fogGreen * (1.0F - var29) + var30[1] * var29;
                  fogBlue = fogBlue * (1.0F - var29) + var30[2] * var29;
               }
            }
         }

         fogRed += (var21 - fogRed) * var19;
         fogGreen += (var22 - fogGreen) * var19;
         fogBlue += (var24 - fogBlue) * var19;
         var26 = var2.getRainLevel(var1);
         float var28;
         if (var26 > 0.0F) {
            var28 = 1.0F - var26 * 0.5F;
            var29 = 1.0F - var26 * 0.4F;
            fogRed *= var28;
            fogGreen *= var28;
            fogBlue *= var29;
         }

         var28 = var2.getThunderLevel(var1);
         if (var28 > 0.0F) {
            var29 = 1.0F - var28 * 0.5F;
            fogRed *= var29;
            fogGreen *= var29;
            fogBlue *= var29;
         }

         biomeChangedTime = -1L;
      }

      double var20 = var0.getPosition().y * var2.dimension.getClearColorScale();
      if (var0.getEntity() instanceof LivingEntity && ((LivingEntity)var0.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         var8 = ((LivingEntity)var0.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if (var8 < 20) {
            var20 *= (double)(1.0F - (float)var8 / 20.0F);
         } else {
            var20 = 0.0D;
         }
      }

      if (var20 < 1.0D) {
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
      boolean var6 = var4.getType() != Fluids.EMPTY;
      float var7;
      if (var6) {
         var7 = 1.0F;
         if (var4.is(FluidTags.WATER)) {
            var7 = 0.05F;
            if (var5 instanceof LocalPlayer) {
               LocalPlayer var8 = (LocalPlayer)var5;
               var7 -= var8.getWaterVision() * var8.getWaterVision() * 0.03F;
               Biome var9 = var8.level.getBiome(new BlockPos(var8));
               if (var9 == Biomes.SWAMP || var9 == Biomes.SWAMP_HILLS) {
                  var7 += 0.005F;
               }
            }
         } else if (var4.is(FluidTags.LAVA)) {
            var7 = 2.0F;
         }

         RenderSystem.fogDensity(var7);
         RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
      } else {
         float var11;
         if (var5 instanceof LivingEntity && ((LivingEntity)var5).hasEffect(MobEffects.BLINDNESS)) {
            int var12 = ((LivingEntity)var5).getEffect(MobEffects.BLINDNESS).getDuration();
            float var10 = Mth.lerp(Math.min(1.0F, (float)var12 / 20.0F), var2, 5.0F);
            if (var1 == FogRenderer.FogMode.FOG_SKY) {
               var7 = 0.0F;
               var11 = var10 * 0.8F;
            } else {
               var7 = var10 * 0.25F;
               var11 = var10;
            }
         } else if (var3) {
            var7 = var2 * 0.05F;
            var11 = Math.min(var2, 192.0F) * 0.5F;
         } else if (var1 == FogRenderer.FogMode.FOG_SKY) {
            var7 = 0.0F;
            var11 = var2;
         } else {
            var7 = var2 * 0.75F;
            var11 = var2;
         }

         RenderSystem.fogStart(var7);
         RenderSystem.fogEnd(var11);
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
   }
}
