package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;

public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 96;
   private static final List<MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList(new MobEffectFogFunction[]{new BlindnessFogFunction(), new DarknessFogFunction()});
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
      float var16;
      float var17;
      float var18;
      float var20;
      float var22;
      float var24;
      if (var5 == FogType.WATER) {
         long var7 = Util.getMillis();
         int var9 = ((Biome)var2.getBiome(new BlockPos(var0.getPosition())).value()).getWaterFogColor();
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
         var20 = 0.25F + 0.75F * (float)var3 / 32.0F;
         var20 = 1.0F - (float)Math.pow((double)var20, 0.25);
         Vec3 var8 = var2.getSkyColor(var0.getPosition(), var1);
         var22 = (float)var8.x;
         var24 = (float)var8.y;
         float var26 = (float)var8.z;
         float var27 = Mth.clamp(Mth.cos(var2.getTimeOfDay(var1) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager var28 = var2.getBiomeManager();
         Vec3 var29 = var0.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
         Vec3 var30 = CubicSampler.gaussianSampleVec3(var29, (var3x, var4x, var5x) -> {
            return var2.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(((Biome)var28.getNoiseBiomeAtQuart(var3x, var4x, var5x).value()).getFogColor()), var27);
         });
         fogRed = (float)var30.x();
         fogGreen = (float)var30.y();
         fogBlue = (float)var30.z();
         if (var3 >= 4) {
            var16 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var31 = new Vector3f(var16, 0.0F, 0.0F);
            var18 = var0.getLookVector().dot(var31);
            if (var18 < 0.0F) {
               var18 = 0.0F;
            }

            if (var18 > 0.0F) {
               float[] var32 = var2.effects().getSunriseColor(var2.getTimeOfDay(var1), var1);
               if (var32 != null) {
                  var18 *= var32[3];
                  fogRed = fogRed * (1.0F - var18) + var32[0] * var18;
                  fogGreen = fogGreen * (1.0F - var18) + var32[1] * var18;
                  fogBlue = fogBlue * (1.0F - var18) + var32[2] * var18;
               }
            }
         }

         fogRed += (var22 - fogRed) * var20;
         fogGreen += (var24 - fogGreen) * var20;
         fogBlue += (var26 - fogBlue) * var20;
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

      var20 = ((float)var0.getPosition().y - (float)var2.getMinBuildHeight()) * var2.getLevelData().getClearColorScale();
      MobEffectFogFunction var21 = getPriorityFogFunction(var6, var1);
      if (var21 != null) {
         LivingEntity var23 = (LivingEntity)var6;
         var20 = var21.getModifiedVoidDarkness(var23, var23.getEffect(var21.getMobEffect()), var20, var1);
      }

      if (var20 < 1.0F && var5 != FogType.LAVA && var5 != FogType.POWDER_SNOW) {
         if (var20 < 0.0F) {
            var20 = 0.0F;
         }

         var20 *= var20;
         fogRed *= var20;
         fogGreen *= var20;
         fogBlue *= var20;
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
      } else {
         label86: {
            if (var6 instanceof LivingEntity) {
               LivingEntity var25 = (LivingEntity)var6;
               if (var25.hasEffect(MobEffects.NIGHT_VISION) && !var25.hasEffect(MobEffects.DARKNESS)) {
                  var22 = GameRenderer.getNightVisionScale(var25, var1);
                  break label86;
               }
            }

            var22 = 0.0F;
         }
      }

      if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
         var24 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - var22) + fogRed * var24 * var22;
         fogGreen = fogGreen * (1.0F - var22) + fogGreen * var24 * var22;
         fogBlue = fogBlue * (1.0F - var22) + fogBlue * var24 * var22;
      }

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.setShaderFogStart(3.4028235E38F);
   }

   @Nullable
   private static MobEffectFogFunction getPriorityFogFunction(Entity var0, float var1) {
      if (var0 instanceof LivingEntity var2) {
         return (MobEffectFogFunction)MOB_EFFECT_FOG.stream().filter((var2x) -> {
            return var2x.isEnabled(var2, var1);
         }).findFirst().orElse((Object)null);
      } else {
         return null;
      }
   }

   public static void setupFog(Camera var0, FogMode var1, float var2, boolean var3, float var4) {
      FogType var5 = var0.getFluidInCamera();
      Entity var6 = var0.getEntity();
      FogData var7 = new FogData(var1);
      MobEffectFogFunction var8 = getPriorityFogFunction(var6, var4);
      if (var5 == FogType.LAVA) {
         if (var6.isSpectator()) {
            var7.start = -8.0F;
            var7.end = var2 * 0.5F;
         } else if (var6 instanceof LivingEntity && ((LivingEntity)var6).hasEffect(MobEffects.FIRE_RESISTANCE)) {
            var7.start = 0.0F;
            var7.end = 3.0F;
         } else {
            var7.start = 0.25F;
            var7.end = 1.0F;
         }
      } else if (var5 == FogType.POWDER_SNOW) {
         if (var6.isSpectator()) {
            var7.start = -8.0F;
            var7.end = var2 * 0.5F;
         } else {
            var7.start = 0.0F;
            var7.end = 2.0F;
         }
      } else if (var8 != null) {
         LivingEntity var9 = (LivingEntity)var6;
         MobEffectInstance var10 = var9.getEffect(var8.getMobEffect());
         if (var10 != null) {
            var8.setupFog(var7, var9, var10, var2, var4);
         }
      } else if (var5 == FogType.WATER) {
         var7.start = -8.0F;
         var7.end = 96.0F;
         if (var6 instanceof LocalPlayer) {
            LocalPlayer var12 = (LocalPlayer)var6;
            var7.end *= Math.max(0.25F, var12.getWaterVision());
            Holder var11 = var12.level.getBiome(var12.blockPosition());
            if (var11.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
               var7.end *= 0.85F;
            }
         }

         if (var7.end > var2) {
            var7.end = var2;
            var7.shape = FogShape.CYLINDER;
         }
      } else if (var3) {
         var7.start = var2 * 0.05F;
         var7.end = Math.min(var2, 192.0F) * 0.5F;
      } else if (var1 == FogRenderer.FogMode.FOG_SKY) {
         var7.start = 0.0F;
         var7.end = var2;
         var7.shape = FogShape.CYLINDER;
      } else {
         float var13 = Mth.clamp(var2 / 10.0F, 4.0F, 64.0F);
         var7.start = var2 - var13;
         var7.end = var2;
         var7.shape = FogShape.CYLINDER;
      }

      RenderSystem.setShaderFogStart(var7.start);
      RenderSystem.setShaderFogEnd(var7.end);
      RenderSystem.setShaderFogShape(var7.shape);
   }

   public static void levelFogColor() {
      RenderSystem.setShaderFogColor(fogRed, fogGreen, fogBlue);
   }

   private interface MobEffectFogFunction {
      MobEffect getMobEffect();

      void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5);

      default boolean isEnabled(LivingEntity var1, float var2) {
         return var1.hasEffect(this.getMobEffect());
      }

      default float getModifiedVoidDarkness(LivingEntity var1, MobEffectInstance var2, float var3, float var4) {
         MobEffectInstance var5 = var1.getEffect(this.getMobEffect());
         if (var5 != null) {
            if (var5.getDuration() < 20) {
               var3 = 1.0F - (float)var5.getDuration() / 20.0F;
            } else {
               var3 = 0.0F;
            }
         }

         return var3;
      }
   }

   private static class FogData {
      public final FogMode mode;
      public float start;
      public float end;
      public FogShape shape;

      public FogData(FogMode var1) {
         super();
         this.shape = FogShape.SPHERE;
         this.mode = var1;
      }
   }

   public static enum FogMode {
      FOG_SKY,
      FOG_TERRAIN;

      private FogMode() {
      }

      // $FF: synthetic method
      private static FogMode[] $values() {
         return new FogMode[]{FOG_SKY, FOG_TERRAIN};
      }
   }

   static class BlindnessFogFunction implements MobEffectFogFunction {
      BlindnessFogFunction() {
         super();
      }

      public MobEffect getMobEffect() {
         return MobEffects.BLINDNESS;
      }

      public void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5) {
         float var6 = Mth.lerp(Math.min(1.0F, (float)var3.getDuration() / 20.0F), var4, 5.0F);
         if (var1.mode == FogRenderer.FogMode.FOG_SKY) {
            var1.start = 0.0F;
            var1.end = var6 * 0.8F;
         } else {
            var1.start = var6 * 0.25F;
            var1.end = var6;
         }

      }
   }

   private static class DarknessFogFunction implements MobEffectFogFunction {
      DarknessFogFunction() {
         super();
      }

      public MobEffect getMobEffect() {
         return MobEffects.DARKNESS;
      }

      public void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5) {
         if (!var3.getFactorData().isEmpty()) {
            float var6 = Mth.lerp(((MobEffectInstance.FactorData)var3.getFactorData().get()).getFactor(var2, var5), var4, 15.0F);
            var1.start = var1.mode == FogRenderer.FogMode.FOG_SKY ? 0.0F : var6 * 0.75F;
            var1.end = var6;
         }
      }

      public float getModifiedVoidDarkness(LivingEntity var1, MobEffectInstance var2, float var3, float var4) {
         return var2.getFactorData().isEmpty() ? 0.0F : 1.0F - ((MobEffectInstance.FactorData)var2.getFactorData().get()).getFactor(var1, var4);
      }
   }
}
