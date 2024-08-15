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
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 96;
   private static final List<FogRenderer.MobEffectFogFunction> MOB_EFFECT_FOG = Lists.newArrayList(
      new FogRenderer.MobEffectFogFunction[]{new FogRenderer.BlindnessFogFunction(), new FogRenderer.DarknessFogFunction()}
   );
   public static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public FogRenderer() {
      super();
   }

   public static Vector4f computeFogColor(Camera var0, float var1, ClientLevel var2, int var3, float var4) {
      FogType var5 = var0.getFluidInCamera();
      Entity var6 = var0.getEntity();
      float var7;
      float var8;
      float var9;
      if (var5 == FogType.WATER) {
         long var10 = Util.getMillis();
         int var12 = var2.getBiome(BlockPos.containing(var0.getPosition())).value().getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = var12;
            previousBiomeFog = var12;
            biomeChangedTime = var10;
         }

         int var13 = targetBiomeFog >> 16 & 0xFF;
         int var14 = targetBiomeFog >> 8 & 0xFF;
         int var15 = targetBiomeFog & 0xFF;
         int var16 = previousBiomeFog >> 16 & 0xFF;
         int var17 = previousBiomeFog >> 8 & 0xFF;
         int var18 = previousBiomeFog & 0xFF;
         float var19 = Mth.clamp((float)(var10 - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float var20 = Mth.lerp(var19, (float)var16, (float)var13);
         float var21 = Mth.lerp(var19, (float)var17, (float)var14);
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
         float var26 = 0.25F + 0.75F * (float)var3 / 32.0F;
         var26 = 1.0F - (float)Math.pow((double)var26, 0.25);
         int var11 = var2.getSkyColor(var0.getPosition(), var1);
         float var31 = ARGB.from8BitChannel(ARGB.red(var11));
         float var34 = ARGB.from8BitChannel(ARGB.green(var11));
         float var37 = ARGB.from8BitChannel(ARGB.blue(var11));
         float var38 = Mth.clamp(Mth.cos(var2.getTimeOfDay(var1) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager var39 = var2.getBiomeManager();
         Vec3 var40 = var0.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
         Vec3 var41 = CubicSampler.gaussianSampleVec3(
            var40,
            (var3x, var4x, var5x) -> var2.effects()
                  .getBrightnessDependentFogColor(Vec3.fromRGB24(var39.getNoiseBiomeAtQuart(var3x, var4x, var5x).value().getFogColor()), var38)
         );
         var7 = (float)var41.x();
         var8 = (float)var41.y();
         var9 = (float)var41.z();
         if (var3 >= 4) {
            float var42 = Mth.sin(var2.getSunAngle(var1)) > 0.0F ? -1.0F : 1.0F;
            Vector3f var44 = new Vector3f(var42, 0.0F, 0.0F);
            float var47 = var0.getLookVector().dot(var44);
            if (var47 < 0.0F) {
               var47 = 0.0F;
            }

            if (var47 > 0.0F && var2.effects().isSunriseOrSunset(var2.getTimeOfDay(var1))) {
               int var51 = var2.effects().getSunriseOrSunsetColor(var2.getTimeOfDay(var1));
               var47 *= ARGB.from8BitChannel(ARGB.alpha(var51));
               var7 = var7 * (1.0F - var47) + ARGB.from8BitChannel(ARGB.red(var51)) * var47;
               var8 = var8 * (1.0F - var47) + ARGB.from8BitChannel(ARGB.green(var51)) * var47;
               var9 = var9 * (1.0F - var47) + ARGB.from8BitChannel(ARGB.blue(var51)) * var47;
            }
         }

         var7 += (var31 - var7) * var26;
         var8 += (var34 - var8) * var26;
         var9 += (var37 - var9) * var26;
         float var43 = var2.getRainLevel(var1);
         if (var43 > 0.0F) {
            float var45 = 1.0F - var43 * 0.5F;
            float var49 = 1.0F - var43 * 0.4F;
            var7 *= var45;
            var8 *= var45;
            var9 *= var49;
         }

         float var46 = var2.getThunderLevel(var1);
         if (var46 > 0.0F) {
            float var50 = 1.0F - var46 * 0.5F;
            var7 *= var50;
            var8 *= var50;
            var9 *= var50;
         }

         biomeChangedTime = -1L;
      }

      float var28 = ((float)var0.getPosition().y - (float)var2.getMinBuildHeight()) * var2.getLevelData().getClearColorScale();
      FogRenderer.MobEffectFogFunction var30 = getPriorityFogFunction(var6, var1);
      if (var30 != null) {
         LivingEntity var32 = (LivingEntity)var6;
         var28 = var30.getModifiedVoidDarkness(var32, var32.getEffect(var30.getMobEffect()), var28, var1);
      }

      if (var28 < 1.0F && var5 != FogType.LAVA && var5 != FogType.POWDER_SNOW) {
         if (var28 < 0.0F) {
            var28 = 0.0F;
         }

         var28 *= var28;
         var7 *= var28;
         var8 *= var28;
         var9 *= var28;
      }

      if (var4 > 0.0F) {
         var7 = var7 * (1.0F - var4) + var7 * 0.7F * var4;
         var8 = var8 * (1.0F - var4) + var8 * 0.6F * var4;
         var9 = var9 * (1.0F - var4) + var9 * 0.6F * var4;
      }

      float var33;
      if (var5 == FogType.WATER) {
         if (var6 instanceof LocalPlayer) {
            var33 = ((LocalPlayer)var6).getWaterVision();
         } else {
            var33 = 1.0F;
         }
      } else {
         label86: {
            if (var6 instanceof LivingEntity var35 && var35.hasEffect(MobEffects.NIGHT_VISION) && !var35.hasEffect(MobEffects.DARKNESS)) {
               var33 = GameRenderer.getNightVisionScale(var35, var1);
               break label86;
            }

            var33 = 0.0F;
         }
      }

      if (var7 != 0.0F && var8 != 0.0F && var9 != 0.0F) {
         float var36 = Math.min(1.0F / var7, Math.min(1.0F / var8, 1.0F / var9));
         var7 = var7 * (1.0F - var33) + var7 * var36 * var33;
         var8 = var8 * (1.0F - var33) + var8 * var36 * var33;
         var9 = var9 * (1.0F - var33) + var9 * var36 * var33;
      }

      return new Vector4f(var7, var8, var9, 1.0F);
   }

   @Nullable
   private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity var0, float var1) {
      return var0 instanceof LivingEntity var2 ? MOB_EFFECT_FOG.stream().filter(var2x -> var2x.isEnabled(var2, var1)).findFirst().orElse(null) : null;
   }

   public static FogParameters setupFog(Camera var0, FogRenderer.FogMode var1, Vector4f var2, float var3, boolean var4, float var5) {
      FogType var6 = var0.getFluidInCamera();
      Entity var7 = var0.getEntity();
      FogRenderer.FogData var8 = new FogRenderer.FogData(var1);
      FogRenderer.MobEffectFogFunction var9 = getPriorityFogFunction(var7, var5);
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
         if (var7 instanceof LocalPlayer var12) {
            var8.end = var8.end * Math.max(0.25F, var12.getWaterVision());
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
         } else if (var1.mode == FogRenderer.FogMode.FOG_TERRAIN) {
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

         var1.start = switch (var1.mode) {
            case FOG_SKY -> 0.0F;
            case FOG_TERRAIN -> var6 * 0.75F;
         };
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
