package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;

public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 192;
   public static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public FogRenderer() {
      super();
   }

   public static void setupColor(Camera var0, float var1, ClientLevel var2, int var3, float var4) {
      FogType var5 = var0.getFluidInCamera();
      Entity var6 = var0.getEntity();
      int var9;
      float var16;
      float var17;
      float var18;
      float var22;
      float var23;
      if (var5 == FogType.WATER) {
         long var7 = Util.getMillis();
         var9 = var2.getBiome(new BlockPos(var0.getPosition())).getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = var9;
            previousBiomeFog = var9;
            biomeChangedTime = var7;
         }

         int var10 = targetBiomeFog >> 16 & 255;
         int var11 = targetBiomeFog >> 8 & 255;
         int var12 = targetBiomeFog & 255;
         int var13 = previousBiomeFog >> 16 & 255;
         int var14 = previousBiomeFog >> 8 & 255;
         int var15 = previousBiomeFog & 255;
         var16 = Mth.clamp((float)(var7 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         var17 = Mth.lerp(var16, (float)var13, (float)var10);
         var18 = Mth.lerp(var16, (float)var14, (float)var11);
         float var19 = Mth.lerp(var16, (float)var15, (float)var12);
         fogRed = var17 / 255.0F;
         fogGreen = var18 / 255.0F;
         fogBlue = var19 / 255.0F;
         if (targetBiomeFog != var9) {
            targetBiomeFog = var9;
            previousBiomeFog = Mth.floor(var17) << 16 | Mth.floor(var18) << 8 | Mth.floor(var19);
            biomeChangedTime = var7;
         }
      } else if (var5 == FogType.LAVA) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeChangedTime = -1L;
      } else if (var5 == FogType.POWDER_SNOW) {
         fogRed = 0.623F;
         fogGreen = 0.734F;
         fogBlue = 0.785F;
         biomeChangedTime = -1L;
         RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
      } else {
         float var20 = 0.25F + 0.75F * (float)var3 / 32.0F;
         var20 = 1.0F - (float)Math.pow((double)var20, 0.25D);
         Vec3 var8 = var2.getSkyColor(var0.getPosition(), var1);
         var22 = (float)var8.field_414;
         var23 = (float)var8.field_415;
         float var24 = (float)var8.field_416;
         float var25 = Mth.clamp(Mth.cos(var2.getTimeOfDay(var1) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager var26 = var2.getBiomeManager();
         Vec3 var27 = var0.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
         Vec3 var28 = CubicSampler.gaussianSampleVec3(var27, (var3x, var4x, var5x) -> {
            return var2.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(var26.getNoiseBiomeAtQuart(var3x, var4x, var5x).getFogColor()), var25);
         });
         fogRed = (float)var28.method_2();
         fogGreen = (float)var28.method_3();
         fogBlue = (float)var28.method_4();
         if (var3 >= 4) {
            var16 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var29 = new Vector3f(var16, 0.0F, 0.0F);
            var18 = var0.getLookVector().dot(var29);
            if (var18 < 0.0F) {
               var18 = 0.0F;
            }

            if (var18 > 0.0F) {
               float[] var30 = var2.effects().getSunriseColor(var2.getTimeOfDay(var1), var1);
               if (var30 != null) {
                  var18 *= var30[3];
                  fogRed = fogRed * (1.0F - var18) + var30[0] * var18;
                  fogGreen = fogGreen * (1.0F - var18) + var30[1] * var18;
                  fogBlue = fogBlue * (1.0F - var18) + var30[2] * var18;
               }
            }
         }

         fogRed += (var22 - fogRed) * var20;
         fogGreen += (var23 - fogGreen) * var20;
         fogBlue += (var24 - fogBlue) * var20;
         var16 = var2.getRainLevel(var1);
         if (var16 > 0.0F) {
            var17 = 1.0F - var16 * 0.5F;
            var18 = 1.0F - var16 * 0.4F;
            fogRed *= var17;
            fogGreen *= var17;
            fogBlue *= var18;
         }

         var17 = var2.getThunderLevel(var1);
         if (var17 > 0.0F) {
            var18 = 1.0F - var17 * 0.5F;
            fogRed *= var18;
            fogGreen *= var18;
            fogBlue *= var18;
         }

         biomeChangedTime = -1L;
      }

      double var21 = (var0.getPosition().field_415 - (double)var2.getMinBuildHeight()) * var2.getLevelData().getClearColorScale();
      if (var0.getEntity() instanceof LivingEntity && ((LivingEntity)var0.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         var9 = ((LivingEntity)var0.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if (var9 < 20) {
            var21 *= (double)(1.0F - (float)var9 / 20.0F);
         } else {
            var21 = 0.0D;
         }
      }

      if (var21 < 1.0D && var5 != FogType.LAVA) {
         if (var21 < 0.0D) {
            var21 = 0.0D;
         }

         var21 *= var21;
         fogRed = (float)((double)fogRed * var21);
         fogGreen = (float)((double)fogGreen * var21);
         fogBlue = (float)((double)fogBlue * var21);
      }

      if (var4 > 0.0F) {
         fogRed = fogRed * (1.0F - var4) + fogRed * 0.7F * var4;
         fogGreen = fogGreen * (1.0F - var4) + fogGreen * 0.6F * var4;
         fogBlue = fogBlue * (1.0F - var4) + fogBlue * 0.6F * var4;
      }

      if (var5 == FogType.WATER) {
         if (var6 instanceof LocalPlayer) {
            var22 = ((LocalPlayer)var6).getWaterVision();
         } else {
            var22 = 1.0F;
         }
      } else if (var6 instanceof LivingEntity && ((LivingEntity)var6).hasEffect(MobEffects.NIGHT_VISION)) {
         var22 = GameRenderer.getNightVisionScale((LivingEntity)var6, var1);
      } else {
         var22 = 0.0F;
      }

      if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
         var23 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - var22) + fogRed * var23 * var22;
         fogGreen = fogGreen * (1.0F - var22) + fogGreen * var23 * var22;
         fogBlue = fogBlue * (1.0F - var22) + fogBlue * var23 * var22;
      }

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.setShaderFogStart(3.4028235E38F);
   }

   public static void setupFog(Camera var0, FogRenderer.FogMode var1, float var2, boolean var3) {
      FogType var4 = var0.getFluidInCamera();
      Entity var5 = var0.getEntity();
      float var6;
      if (var4 == FogType.WATER) {
         var6 = 192.0F;
         if (var5 instanceof LocalPlayer) {
            LocalPlayer var7 = (LocalPlayer)var5;
            var6 *= Math.max(0.25F, var7.getWaterVision());
            Biome var8 = var7.level.getBiome(var7.blockPosition());
            if (var8.getBiomeCategory() == Biome.BiomeCategory.SWAMP) {
               var6 *= 0.85F;
            }
         }

         RenderSystem.setShaderFogStart(-8.0F);
         RenderSystem.setShaderFogEnd(var6 * 0.5F);
      } else {
         float var10;
         if (var4 == FogType.LAVA) {
            if (var5.isSpectator()) {
               var6 = -8.0F;
               var10 = var2 * 0.5F;
            } else if (var5 instanceof LivingEntity && ((LivingEntity)var5).hasEffect(MobEffects.FIRE_RESISTANCE)) {
               var6 = 0.0F;
               var10 = 3.0F;
            } else {
               var6 = 0.25F;
               var10 = 1.0F;
            }
         } else if (var5 instanceof LivingEntity && ((LivingEntity)var5).hasEffect(MobEffects.BLINDNESS)) {
            int var12 = ((LivingEntity)var5).getEffect(MobEffects.BLINDNESS).getDuration();
            float var9 = Mth.lerp(Math.min(1.0F, (float)var12 / 20.0F), var2, 5.0F);
            if (var1 == FogRenderer.FogMode.FOG_SKY) {
               var6 = 0.0F;
               var10 = var9 * 0.8F;
            } else {
               var6 = var9 * 0.25F;
               var10 = var9;
            }
         } else if (var4 == FogType.POWDER_SNOW) {
            if (var5.isSpectator()) {
               var6 = -8.0F;
               var10 = var2 * 0.5F;
            } else {
               var6 = 0.0F;
               var10 = 2.0F;
            }
         } else if (var3) {
            var6 = var2 * 0.05F;
            var10 = Math.min(var2, 192.0F) * 0.5F;
         } else if (var1 == FogRenderer.FogMode.FOG_SKY) {
            var6 = 0.0F;
            var10 = var2;
         } else {
            float var11 = Mth.clamp(var2 / 10.0F, 4.0F, 64.0F);
            var6 = var2 - var11;
            var10 = var2;
         }

         RenderSystem.setShaderFogStart(var6);
         RenderSystem.setShaderFogEnd(var10);
      }

   }

   public static void levelFogColor() {
      RenderSystem.setShaderFogColor(fogRed, fogGreen, fogBlue);
   }

   public static enum FogMode {
      FOG_SKY,
      FOG_TERRAIN;

      private FogMode() {
      }

      // $FF: synthetic method
      private static FogRenderer.FogMode[] $values() {
         return new FogRenderer.FogMode[]{FOG_SKY, FOG_TERRAIN};
      }
   }
}
