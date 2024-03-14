package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 96;
   private static final List<FogRenderer.MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList(
      new FogRenderer.MobEffectFogFunction[]{new FogRenderer.BlindnessFogFunction(), new FogRenderer.DarknessFogFunction()}
   );
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
      if (var5 == FogType.WATER) {
         long var7 = Util.getMillis();
         int var9 = var2.getBiome(BlockPos.containing(var0.getPosition())).value().getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = var9;
            previousBiomeFog = var9;
            biomeChangedTime = var7;
         }

         int var10 = targetBiomeFog >> 16 & 0xFF;
         int var11 = targetBiomeFog >> 8 & 0xFF;
         int var12 = targetBiomeFog & 0xFF;
         int var13 = previousBiomeFog >> 16 & 0xFF;
         int var14 = previousBiomeFog >> 8 & 0xFF;
         int var15 = previousBiomeFog & 0xFF;
         float var16 = Mth.clamp((float)(var7 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float var17 = Mth.lerp(var16, (float)var13, (float)var10);
         float var18 = Mth.lerp(var16, (float)var14, (float)var11);
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
         var20 = 1.0F - (float)Math.pow((double)var20, 0.25);
         Vec3 var8 = var2.getSkyColor(var0.getPosition(), var1);
         float var25 = (float)var8.x;
         float var28 = (float)var8.y;
         float var31 = (float)var8.z;
         float var32 = Mth.clamp(Mth.cos(var2.getTimeOfDay(var1) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager var33 = var2.getBiomeManager();
         Vec3 var34 = var0.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
         Vec3 var35 = CubicSampler.gaussianSampleVec3(
            var34,
            (var3x, var4x, var5x) -> var2.effects()
                  .getBrightnessDependentFogColor(Vec3.fromRGB24(var33.getNoiseBiomeAtQuart(var3x, var4x, var5x).value().getFogColor()), var32)
         );
         fogRed = (float)var35.x();
         fogGreen = (float)var35.y();
         fogBlue = (float)var35.z();
         if (var3 >= 4) {
            float var36 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var38 = new Vector3f(var36, 0.0F, 0.0F);
            float var41 = var0.getLookVector().dot(var38);
            if (var41 < 0.0F) {
               var41 = 0.0F;
            }

            if (var41 > 0.0F) {
               float[] var45 = var2.effects().getSunriseColor(var2.getTimeOfDay(var1), var1);
               if (var45 != null) {
                  var41 *= var45[3];
                  fogRed = fogRed * (1.0F - var41) + var45[0] * var41;
                  fogGreen = fogGreen * (1.0F - var41) + var45[1] * var41;
                  fogBlue = fogBlue * (1.0F - var41) + var45[2] * var41;
               }
            }
         }

         fogRed += (var25 - fogRed) * var20;
         fogGreen += (var28 - fogGreen) * var20;
         fogBlue += (var31 - fogBlue) * var20;
         float var37 = var2.getRainLevel(var1);
         if (var37 > 0.0F) {
            float var39 = 1.0F - var37 * 0.5F;
            float var43 = 1.0F - var37 * 0.4F;
            fogRed *= var39;
            fogGreen *= var39;
            fogBlue *= var43;
         }

         float var40 = var2.getThunderLevel(var1);
         if (var40 > 0.0F) {
            float var44 = 1.0F - var40 * 0.5F;
            fogRed *= var44;
            fogGreen *= var44;
            fogBlue *= var44;
         }

         biomeChangedTime = -1L;
      }

      float var22 = ((float)var0.getPosition().y - (float)var2.getMinBuildHeight()) * var2.getLevelData().getClearColorScale();
      FogRenderer.MobEffectFogFunction var24 = getPriorityFogFunction(var6, var1);
      if (var24 != null) {
         LivingEntity var26 = (LivingEntity)var6;
         var22 = var24.getModifiedVoidDarkness(var26, var26.getEffect(var24.getMobEffect()), var22, var1);
      }

      if (var22 < 1.0F && var5 != FogType.LAVA && var5 != FogType.POWDER_SNOW) {
         if (var22 < 0.0F) {
            var22 = 0.0F;
         }

         var22 *= var22;
         fogRed *= var22;
         fogGreen *= var22;
         fogBlue *= var22;
      }

      if (var4 > 0.0F) {
         fogRed = fogRed * (1.0F - var4) + fogRed * 0.7F * var4;
         fogGreen = fogGreen * (1.0F - var4) + fogGreen * 0.6F * var4;
         fogBlue = fogBlue * (1.0F - var4) + fogBlue * 0.6F * var4;
      }

      float var27;
      if (var5 == FogType.WATER) {
         if (var6 instanceof LocalPlayer) {
            var27 = ((LocalPlayer)var6).getWaterVision();
         } else {
            var27 = 1.0F;
         }
      } else {
         label86: {
            if (var6 instanceof LivingEntity var29
               && ((LivingEntity)var29).hasEffect(MobEffects.NIGHT_VISION)
               && !((LivingEntity)var29).hasEffect(MobEffects.DARKNESS)) {
               var27 = GameRenderer.getNightVisionScale((LivingEntity)var29, var1);
               break label86;
            }

            var27 = 0.0F;
         }
      }

      if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
         float var30 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - var27) + fogRed * var30 * var27;
         fogGreen = fogGreen * (1.0F - var27) + fogGreen * var30 * var27;
         fogBlue = fogBlue * (1.0F - var27) + fogBlue * var30 * var27;
      }

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.setShaderFogStart(3.4028235E38F);
   }

   @Nullable
   private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity var0, float var1) {
      return var0 instanceof LivingEntity var2 ? MOB_EFFECT_FOG.stream().filter(var2x -> var2x.isEnabled(var2, var1)).findFirst().orElse(null) : null;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void setupFog(Camera var0, FogRenderer.FogMode var1, float var2, boolean var3, float var4) {
      FogType var5 = var0.getFluidInCamera();
      Entity var6 = var0.getEntity();
      FogRenderer.FogData var7 = new FogRenderer.FogData(var1);
      FogRenderer.MobEffectFogFunction var8 = getPriorityFogFunction(var6, var4);
      if (var5 == FogType.LAVA) {
         if (var6.isSpectator()) {
            var7.start = -8.0F;
            var7.end = var2 * 0.5F;
         } else if (var6 instanceof LivingEntity && ((LivingEntity)var6).hasEffect(MobEffects.FIRE_RESISTANCE)) {
            var7.start = 0.0F;
            var7.end = 5.0F;
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
         if (var6 instanceof LocalPlayer var11) {
            var7.end *= Math.max(0.25F, var11.getWaterVision());
            Holder var13 = var11.level().getBiome(var11.blockPosition());
            if (var13.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
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
         float var12 = Mth.clamp(var2 / 10.0F, 4.0F, 64.0F);
         var7.start = var2 - var12;
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

   static class BlindnessFogFunction implements FogRenderer.MobEffectFogFunction {
      BlindnessFogFunction() {
         super();
      }

      @Override
      public Holder<MobEffect> getMobEffect() {
         return MobEffects.BLINDNESS;
      }

      @Override
      public void setupFog(FogRenderer.FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5) {
         float var6 = var3.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)var3.getDuration() / 20.0F), var4, 5.0F);
         if (var1.mode == FogRenderer.FogMode.FOG_SKY) {
            var1.start = 0.0F;
            var1.end = var6 * 0.8F;
         } else {
            var1.start = var6 * 0.25F;
            var1.end = var6;
         }
      }
   }

   static class DarknessFogFunction implements FogRenderer.MobEffectFogFunction {
      DarknessFogFunction() {
         super();
      }

      @Override
      public Holder<MobEffect> getMobEffect() {
         return MobEffects.DARKNESS;
      }

      @Override
      public void setupFog(FogRenderer.FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5) {
         float var6 = Mth.lerp(var3.getBlendFactor(var2, var5), var4, 15.0F);
         var1.start = var1.mode == FogRenderer.FogMode.FOG_SKY ? 0.0F : var6 * 0.75F;
         var1.end = var6;
      }

      @Override
      public float getModifiedVoidDarkness(LivingEntity var1, MobEffectInstance var2, float var3, float var4) {
         return 1.0F - var2.getBlendFactor(var1, var4);
      }
   }

   static class FogData {
      public final FogRenderer.FogMode mode;
      public float start;
      public float end;
      public FogShape shape = FogShape.SPHERE;

      public FogData(FogRenderer.FogMode var1) {
         super();
         this.mode = var1;
      }
   }

   public static enum FogMode {
      FOG_SKY,
      FOG_TERRAIN;

      private FogMode() {
      }
   }

   interface MobEffectFogFunction {
      Holder<MobEffect> getMobEffect();

      void setupFog(FogRenderer.FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5);

      default boolean isEnabled(LivingEntity var1, float var2) {
         return var1.hasEffect(this.getMobEffect());
      }

      default float getModifiedVoidDarkness(LivingEntity var1, MobEffectInstance var2, float var3, float var4) {
         MobEffectInstance var5 = var1.getEffect(this.getMobEffect());
         if (var5 != null) {
            if (var5.endsWithin(19)) {
               var3 = 1.0F - (float)var5.getDuration() / 20.0F;
            } else {
               var3 = 0.0F;
            }
         }

         return var3;
      }
   }
}
