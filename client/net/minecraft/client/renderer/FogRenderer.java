package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.shaders.FogShape;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.ARGB;
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
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 96;
   private static final List<MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList(new MobEffectFogFunction[]{new BlindnessFogFunction(), new DarknessFogFunction()});
   public static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;
   private static boolean fogEnabled = true;

   public FogRenderer() {
      super();
   }

   public static Vector4f computeFogColor(Camera var0, float var1, ClientLevel var2, int var3, float var4) {
      FogType var5 = var0.getFluidInCamera();
      Entity var6 = var0.getEntity();
      float var7;
      float var8;
      float var9;
      float var19;
      float var20;
      float var21;
      float var23;
      float var25;
      float var27;
      if (var5 == FogType.WATER) {
         long var10 = Util.getMillis();
         int var12 = ((Biome)var2.getBiome(BlockPos.containing(var0.getPosition())).value()).getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = var12;
            previousBiomeFog = var12;
            biomeChangedTime = var10;
         }

         int var13 = targetBiomeFog >> 16 & 255;
         int var14 = targetBiomeFog >> 8 & 255;
         int var15 = targetBiomeFog & 255;
         int var16 = previousBiomeFog >> 16 & 255;
         int var17 = previousBiomeFog >> 8 & 255;
         int var18 = previousBiomeFog & 255;
         var19 = Mth.clamp((float)(var10 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         var20 = Mth.lerp(var19, (float)var16, (float)var13);
         var21 = Mth.lerp(var19, (float)var17, (float)var14);
         float var22 = Mth.lerp(var19, (float)var18, (float)var15);
         var7 = var20 / 255.0F;
         var8 = var21 / 255.0F;
         var9 = var22 / 255.0F;
         if (targetBiomeFog != var12) {
            targetBiomeFog = var12;
            previousBiomeFog = Mth.floor(var20) << 16 | Mth.floor(var21) << 8 | Mth.floor(var22);
            biomeChangedTime = var10;
         }
      } else if (var5 == FogType.LAVA) {
         var7 = 0.6F;
         var8 = 0.1F;
         var9 = 0.0F;
         biomeChangedTime = -1L;
      } else if (var5 == FogType.POWDER_SNOW) {
         var7 = 0.623F;
         var8 = 0.734F;
         var9 = 0.785F;
         biomeChangedTime = -1L;
      } else {
         var23 = 0.25F + 0.75F * (float)var3 / 32.0F;
         var23 = 1.0F - (float)Math.pow((double)var23, 0.25);
         int var11 = var2.getSkyColor(var0.getPosition(), var1);
         var25 = ARGB.redFloat(var11);
         var27 = ARGB.greenFloat(var11);
         float var29 = ARGB.blueFloat(var11);
         float var30 = Mth.clamp(Mth.cos(var2.getTimeOfDay(var1) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager var31 = var2.getBiomeManager();
         Vec3 var32 = var0.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
         Vec3 var33 = CubicSampler.gaussianSampleVec3(var32, (var3x, var4x, var5x) -> {
            return var2.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(((Biome)var31.getNoiseBiomeAtQuart(var3x, var4x, var5x).value()).getFogColor()), var30);
         });
         var7 = (float)var33.x();
         var8 = (float)var33.y();
         var9 = (float)var33.z();
         if (var3 >= 4) {
            var19 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var34 = new Vector3f(var19, 0.0F, 0.0F);
            var21 = var0.getLookVector().dot(var34);
            if (var21 < 0.0F) {
               var21 = 0.0F;
            }

            if (var21 > 0.0F && var2.effects().isSunriseOrSunset(var2.getTimeOfDay(var1))) {
               int var35 = var2.effects().getSunriseOrSunsetColor(var2.getTimeOfDay(var1));
               var21 *= ARGB.alphaFloat(var35);
               var7 = var7 * (1.0F - var21) + ARGB.redFloat(var35) * var21;
               var8 = var8 * (1.0F - var21) + ARGB.greenFloat(var35) * var21;
               var9 = var9 * (1.0F - var21) + ARGB.blueFloat(var35) * var21;
            }
         }

         var7 += (var25 - var7) * var23;
         var8 += (var27 - var8) * var23;
         var9 += (var29 - var9) * var23;
         var19 = var2.getRainLevel(var1);
         if (var19 > 0.0F) {
            var20 = 1.0F - var19 * 0.5F;
            var21 = 1.0F - var19 * 0.4F;
            var7 *= var20;
            var8 *= var20;
            var9 *= var21;
         }

         var20 = var2.getThunderLevel(var1);
         if (var20 > 0.0F) {
            var21 = 1.0F - var20 * 0.5F;
            var7 *= var21;
            var8 *= var21;
            var9 *= var21;
         }

         biomeChangedTime = -1L;
      }

      var23 = ((float)var0.getPosition().y - (float)var2.getMinY()) * var2.getLevelData().getClearColorScale();
      MobEffectFogFunction var24 = getPriorityFogFunction(var6, var1);
      if (var24 != null) {
         LivingEntity var26 = (LivingEntity)var6;
         var23 = var24.getModifiedVoidDarkness(var26, var26.getEffect(var24.getMobEffect()), var23, var1);
      }

      if (var23 < 1.0F && var5 != FogType.LAVA && var5 != FogType.POWDER_SNOW) {
         if (var23 < 0.0F) {
            var23 = 0.0F;
         }

         var23 *= var23;
         var7 *= var23;
         var8 *= var23;
         var9 *= var23;
      }

      if (var4 > 0.0F) {
         var7 = var7 * (1.0F - var4) + var7 * 0.7F * var4;
         var8 = var8 * (1.0F - var4) + var8 * 0.6F * var4;
         var9 = var9 * (1.0F - var4) + var9 * 0.6F * var4;
      }

      if (var5 == FogType.WATER) {
         if (var6 instanceof LocalPlayer) {
            var25 = ((LocalPlayer)var6).getWaterVision();
         } else {
            var25 = 1.0F;
         }
      } else {
         label86: {
            if (var6 instanceof LivingEntity) {
               LivingEntity var28 = (LivingEntity)var6;
               if (var28.hasEffect(MobEffects.NIGHT_VISION) && !var28.hasEffect(MobEffects.DARKNESS)) {
                  var25 = GameRenderer.getNightVisionScale(var28, var1);
                  break label86;
               }
            }

            var25 = 0.0F;
         }
      }

      if (var7 != 0.0F && var8 != 0.0F && var9 != 0.0F) {
         var27 = Math.min(1.0F / var7, Math.min(1.0F / var8, 1.0F / var9));
         var7 = var7 * (1.0F - var25) + var7 * var27 * var25;
         var8 = var8 * (1.0F - var25) + var8 * var27 * var25;
         var9 = var9 * (1.0F - var25) + var9 * var27 * var25;
      }

      return new Vector4f(var7, var8, var9, 1.0F);
   }

   public static boolean toggleFog() {
      return fogEnabled = !fogEnabled;
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

   public static FogParameters setupFog(Camera var0, FogMode var1, Vector4f var2, float var3, boolean var4, float var5) {
      if (!fogEnabled) {
         return FogParameters.NO_FOG;
      } else {
         FogType var6 = var0.getFluidInCamera();
         Entity var7 = var0.getEntity();
         FogData var8 = new FogData(var1);
         MobEffectFogFunction var9 = getPriorityFogFunction(var7, var5);
         if (var6 == FogType.LAVA) {
            if (var7.isSpectator()) {
               var8.start = -8.0F;
               var8.end = var3 * 0.5F;
            } else if (var7 instanceof LivingEntity && ((LivingEntity)var7).hasEffect(MobEffects.FIRE_RESISTANCE)) {
               var8.start = 0.0F;
               var8.end = 5.0F;
            } else {
               var8.start = 0.25F;
               var8.end = 1.0F;
            }
         } else if (var6 == FogType.POWDER_SNOW) {
            if (var7.isSpectator()) {
               var8.start = -8.0F;
               var8.end = var3 * 0.5F;
            } else {
               var8.start = 0.0F;
               var8.end = 2.0F;
            }
         } else if (var9 != null) {
            LivingEntity var10 = (LivingEntity)var7;
            MobEffectInstance var11 = var10.getEffect(var9.getMobEffect());
            if (var11 != null) {
               var9.setupFog(var8, var10, var11, var3, var5);
            }
         } else if (var6 == FogType.WATER) {
            var8.start = -8.0F;
            var8.end = 96.0F;
            if (var7 instanceof LocalPlayer) {
               LocalPlayer var12 = (LocalPlayer)var7;
               var8.end *= Math.max(0.25F, var12.getWaterVision());
               Holder var14 = var12.level().getBiome(var12.blockPosition());
               if (var14.is(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                  var8.end *= 0.85F;
               }
            }

            if (var8.end > var3) {
               var8.end = var3;
               var8.shape = FogShape.CYLINDER;
            }
         } else if (var4) {
            var8.start = var3 * 0.05F;
            var8.end = Math.min(var3, 192.0F) * 0.5F;
         } else if (var1 == FogRenderer.FogMode.FOG_SKY) {
            var8.start = 0.0F;
            var8.end = var3;
            var8.shape = FogShape.CYLINDER;
         } else if (var1 == FogRenderer.FogMode.FOG_TERRAIN) {
            float var13 = Mth.clamp(var3 / 10.0F, 4.0F, 64.0F);
            var8.start = var3 - var13;
            var8.end = var3;
            var8.shape = FogShape.CYLINDER;
         }

         return new FogParameters(var8.start, var8.end, var8.shape, var2.x, var2.y, var2.z, var2.w);
      }
   }

   private interface MobEffectFogFunction {
      Holder<MobEffect> getMobEffect();

      void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5);

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

      public Holder<MobEffect> getMobEffect() {
         return MobEffects.BLINDNESS;
      }

      public void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5) {
         float var6 = var3.isInfiniteDuration() ? 5.0F : Mth.lerp(Math.min(1.0F, (float)var3.getDuration() / 20.0F), var4, 5.0F);
         if (var1.mode == FogRenderer.FogMode.FOG_SKY) {
            var1.start = 0.0F;
            var1.end = var6 * 0.8F;
         } else if (var1.mode == FogRenderer.FogMode.FOG_TERRAIN) {
            var1.start = var6 * 0.25F;
            var1.end = var6;
         }

      }
   }

   private static class DarknessFogFunction implements MobEffectFogFunction {
      DarknessFogFunction() {
         super();
      }

      public Holder<MobEffect> getMobEffect() {
         return MobEffects.DARKNESS;
      }

      public void setupFog(FogData var1, LivingEntity var2, MobEffectInstance var3, float var4, float var5) {
         float var6 = Mth.lerp(var3.getBlendFactor(var2, var5), var4, 15.0F);
         float var10001;
         switch (var1.mode.ordinal()) {
            case 0 -> var10001 = 0.0F;
            case 1 -> var10001 = var6 * 0.75F;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         var1.start = var10001;
         var1.end = var6;
      }

      public float getModifiedVoidDarkness(LivingEntity var1, MobEffectInstance var2, float var3, float var4) {
         return 1.0F - var2.getBlendFactor(var1, var4);
      }
   }
}
