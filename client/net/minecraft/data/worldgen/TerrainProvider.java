package net.minecraft.data.worldgen;

import net.minecraft.util.CubicSpline;
import net.minecraft.util.Mth;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.level.levelgen.NoiseRouterData;

public class TerrainProvider {
   private static final float DEEP_OCEAN_CONTINENTALNESS = -0.51F;
   private static final float OCEAN_CONTINENTALNESS = -0.4F;
   private static final float PLAINS_CONTINENTALNESS = 0.1F;
   private static final float BEACH_CONTINENTALNESS = -0.15F;
   private static final ToFloatFunction<Float> NO_TRANSFORM;
   private static final ToFloatFunction<Float> AMPLIFIED_OFFSET;
   private static final ToFloatFunction<Float> AMPLIFIED_FACTOR;
   private static final ToFloatFunction<Float> AMPLIFIED_JAGGEDNESS;

   public TerrainProvider() {
      super();
   }

   public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldOffset(I var0, I var1, I var2, boolean var3) {
      ToFloatFunction var4 = var3 ? AMPLIFIED_OFFSET : NO_TRANSFORM;
      CubicSpline var5 = buildErosionOffsetSpline(var1, var2, -0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, var4);
      CubicSpline var6 = buildErosionOffsetSpline(var1, var2, -0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, var4);
      CubicSpline var7 = buildErosionOffsetSpline(var1, var2, -0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, var4);
      CubicSpline var8 = buildErosionOffsetSpline(var1, var2, -0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, var4);
      return CubicSpline.builder(var0, var4).addPoint(-1.1F, 0.044F).addPoint(-1.02F, -0.2222F).addPoint(-0.51F, -0.2222F).addPoint(-0.44F, -0.12F).addPoint(-0.18F, -0.12F).addPoint(-0.16F, var5).addPoint(-0.15F, var5).addPoint(-0.1F, var6).addPoint(0.25F, var7).addPoint(1.0F, var8).build();
   }

   public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldFactor(I var0, I var1, I var2, I var3, boolean var4) {
      ToFloatFunction var5 = var4 ? AMPLIFIED_FACTOR : NO_TRANSFORM;
      return CubicSpline.builder(var0, NO_TRANSFORM).addPoint(-0.19F, 3.95F).addPoint(-0.15F, getErosionFactor(var1, var2, var3, 6.25F, true, NO_TRANSFORM)).addPoint(-0.1F, getErosionFactor(var1, var2, var3, 5.47F, true, var5)).addPoint(0.03F, getErosionFactor(var1, var2, var3, 5.08F, true, var5)).addPoint(0.06F, getErosionFactor(var1, var2, var3, 4.69F, false, var5)).build();
   }

   public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldJaggedness(I var0, I var1, I var2, I var3, boolean var4) {
      ToFloatFunction var5 = var4 ? AMPLIFIED_JAGGEDNESS : NO_TRANSFORM;
      float var6 = 0.65F;
      return CubicSpline.builder(var0, var5).addPoint(-0.11F, 0.0F).addPoint(0.03F, buildErosionJaggednessSpline(var1, var2, var3, 1.0F, 0.5F, 0.0F, 0.0F, var5)).addPoint(0.65F, buildErosionJaggednessSpline(var1, var2, var3, 1.0F, 1.0F, 1.0F, 0.0F, var5)).build();
   }

   private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionJaggednessSpline(I var0, I var1, I var2, float var3, float var4, float var5, float var6, ToFloatFunction<Float> var7) {
      float var8 = -0.5775F;
      CubicSpline var9 = buildRidgeJaggednessSpline(var1, var2, var3, var5, var7);
      CubicSpline var10 = buildRidgeJaggednessSpline(var1, var2, var4, var6, var7);
      return CubicSpline.builder(var0, var7).addPoint(-1.0F, var9).addPoint(-0.78F, var10).addPoint(-0.5775F, var10).addPoint(-0.375F, 0.0F).build();
   }

   private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildRidgeJaggednessSpline(I var0, I var1, float var2, float var3, ToFloatFunction<Float> var4) {
      float var5 = NoiseRouterData.peaksAndValleys(0.4F);
      float var6 = NoiseRouterData.peaksAndValleys(0.56666666F);
      float var7 = (var5 + var6) / 2.0F;
      CubicSpline.Builder var8 = CubicSpline.builder(var1, var4);
      var8.addPoint(var5, 0.0F);
      if (var3 > 0.0F) {
         var8.addPoint(var7, buildWeirdnessJaggednessSpline(var0, var3, var4));
      } else {
         var8.addPoint(var7, 0.0F);
      }

      if (var2 > 0.0F) {
         var8.addPoint(1.0F, buildWeirdnessJaggednessSpline(var0, var2, var4));
      } else {
         var8.addPoint(1.0F, 0.0F);
      }

      return var8.build();
   }

   private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildWeirdnessJaggednessSpline(I var0, float var1, ToFloatFunction<Float> var2) {
      float var3 = 0.63F * var1;
      float var4 = 0.3F * var1;
      return CubicSpline.builder(var0, var2).addPoint(-0.01F, var3).addPoint(0.01F, var4).build();
   }

   private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> getErosionFactor(I var0, I var1, I var2, float var3, boolean var4, ToFloatFunction<Float> var5) {
      CubicSpline var6 = CubicSpline.builder(var1, var5).addPoint(-0.2F, 6.3F).addPoint(0.2F, var3).build();
      CubicSpline.Builder var7 = CubicSpline.builder(var0, var5).addPoint(-0.6F, var6).addPoint(-0.5F, CubicSpline.builder(var1, var5).addPoint(-0.05F, 6.3F).addPoint(0.05F, 2.67F).build()).addPoint(-0.35F, var6).addPoint(-0.25F, var6).addPoint(-0.1F, CubicSpline.builder(var1, var5).addPoint(-0.05F, 2.67F).addPoint(0.05F, 6.3F).build()).addPoint(0.03F, var6);
      CubicSpline var8;
      CubicSpline var9;
      if (var4) {
         var8 = CubicSpline.builder(var1, var5).addPoint(0.0F, var3).addPoint(0.1F, 0.625F).build();
         var9 = CubicSpline.builder(var2, var5).addPoint(-0.9F, var3).addPoint(-0.69F, var8).build();
         var7.addPoint(0.35F, var3).addPoint(0.45F, var9).addPoint(0.55F, var9).addPoint(0.62F, var3);
      } else {
         var8 = CubicSpline.builder(var2, var5).addPoint(-0.7F, var6).addPoint(-0.15F, 1.37F).build();
         var9 = CubicSpline.builder(var2, var5).addPoint(0.45F, var6).addPoint(0.7F, 1.56F).build();
         var7.addPoint(0.05F, var9).addPoint(0.4F, var9).addPoint(0.45F, var8).addPoint(0.55F, var8).addPoint(0.58F, var3);
      }

      return var7.build();
   }

   private static float calculateSlope(float var0, float var1, float var2, float var3) {
      return (var1 - var0) / (var3 - var2);
   }

   private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildMountainRidgeSplineWithPoints(I var0, float var1, boolean var2, ToFloatFunction<Float> var3) {
      CubicSpline.Builder var4 = CubicSpline.builder(var0, var3);
      float var5 = -0.7F;
      float var6 = -1.0F;
      float var7 = mountainContinentalness(-1.0F, var1, -0.7F);
      float var8 = 1.0F;
      float var9 = mountainContinentalness(1.0F, var1, -0.7F);
      float var10 = calculateMountainRidgeZeroContinentalnessPoint(var1);
      float var11 = -0.65F;
      float var12;
      if (-0.65F < var10 && var10 < 1.0F) {
         var12 = mountainContinentalness(-0.65F, var1, -0.7F);
         float var13 = -0.75F;
         float var14 = mountainContinentalness(-0.75F, var1, -0.7F);
         float var15 = calculateSlope(var7, var14, -1.0F, -0.75F);
         var4.addPoint(-1.0F, var7, var15);
         var4.addPoint(-0.75F, var14);
         var4.addPoint(-0.65F, var12);
         float var16 = mountainContinentalness(var10, var1, -0.7F);
         float var17 = calculateSlope(var16, var9, var10, 1.0F);
         float var18 = 0.01F;
         var4.addPoint(var10 - 0.01F, var16);
         var4.addPoint(var10, var16, var17);
         var4.addPoint(1.0F, var9, var17);
      } else {
         var12 = calculateSlope(var7, var9, -1.0F, 1.0F);
         if (var2) {
            var4.addPoint(-1.0F, Math.max(0.2F, var7));
            var4.addPoint(0.0F, Mth.lerp(0.5F, var7, var9), var12);
         } else {
            var4.addPoint(-1.0F, var7, var12);
         }

         var4.addPoint(1.0F, var9, var12);
      }

      return var4.build();
   }

   private static float mountainContinentalness(float var0, float var1, float var2) {
      float var3 = 1.17F;
      float var4 = 0.46082947F;
      float var5 = 1.0F - (1.0F - var1) * 0.5F;
      float var6 = 0.5F * (1.0F - var1);
      float var7 = (var0 + 1.17F) * 0.46082947F;
      float var8 = var7 * var5 - var6;
      return var0 < var2 ? Math.max(var8, -0.2222F) : Math.max(var8, 0.0F);
   }

   private static float calculateMountainRidgeZeroContinentalnessPoint(float var0) {
      float var1 = 1.17F;
      float var2 = 0.46082947F;
      float var3 = 1.0F - (1.0F - var0) * 0.5F;
      float var4 = 0.5F * (1.0F - var0);
      return var4 / (0.46082947F * var3) - 1.17F;
   }

   public static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> buildErosionOffsetSpline(I var0, I var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8, boolean var9, ToFloatFunction<Float> var10) {
      float var11 = 0.6F;
      float var12 = 0.5F;
      float var13 = 0.5F;
      CubicSpline var14 = buildMountainRidgeSplineWithPoints(var1, Mth.lerp(var5, 0.6F, 1.5F), var9, var10);
      CubicSpline var15 = buildMountainRidgeSplineWithPoints(var1, Mth.lerp(var5, 0.6F, 1.0F), var9, var10);
      CubicSpline var16 = buildMountainRidgeSplineWithPoints(var1, var5, var9, var10);
      CubicSpline var17 = ridgeSpline(var1, var2 - 0.15F, 0.5F * var5, Mth.lerp(0.5F, 0.5F, 0.5F) * var5, 0.5F * var5, 0.6F * var5, 0.5F, var10);
      CubicSpline var18 = ridgeSpline(var1, var2, var6 * var5, var3 * var5, 0.5F * var5, 0.6F * var5, 0.5F, var10);
      CubicSpline var19 = ridgeSpline(var1, var2, var6, var6, var3, var4, 0.5F, var10);
      CubicSpline var20 = ridgeSpline(var1, var2, var6, var6, var3, var4, 0.5F, var10);
      CubicSpline var21 = CubicSpline.builder(var1, var10).addPoint(-1.0F, var2).addPoint(-0.4F, var19).addPoint(0.0F, var4 + 0.07F).build();
      CubicSpline var22 = ridgeSpline(var1, -0.02F, var7, var7, var3, var4, 0.0F, var10);
      CubicSpline.Builder var23 = CubicSpline.builder(var0, var10).addPoint(-0.85F, var14).addPoint(-0.7F, var15).addPoint(-0.4F, var16).addPoint(-0.35F, var17).addPoint(-0.1F, var18).addPoint(0.2F, var19);
      if (var8) {
         var23.addPoint(0.4F, var20).addPoint(0.45F, var21).addPoint(0.55F, var21).addPoint(0.58F, var20);
      }

      var23.addPoint(0.7F, var22);
      return var23.build();
   }

   private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> ridgeSpline(I var0, float var1, float var2, float var3, float var4, float var5, float var6, ToFloatFunction<Float> var7) {
      float var8 = Math.max(0.5F * (var2 - var1), var6);
      float var9 = 5.0F * (var3 - var2);
      return CubicSpline.builder(var0, var7).addPoint(-1.0F, var1, var8).addPoint(-0.4F, var2, Math.min(var8, var9)).addPoint(0.0F, var3, var9).addPoint(0.4F, var4, 2.0F * (var4 - var3)).addPoint(1.0F, var5, 0.7F * (var5 - var4)).build();
   }

   static {
      NO_TRANSFORM = ToFloatFunction.IDENTITY;
      AMPLIFIED_OFFSET = ToFloatFunction.createUnlimited((var0) -> {
         return var0 < 0.0F ? var0 : var0 * 2.0F;
      });
      AMPLIFIED_FACTOR = ToFloatFunction.createUnlimited((var0) -> {
         return 1.25F - 6.25F / (var0 + 5.0F);
      });
      AMPLIFIED_JAGGEDNESS = ToFloatFunction.createUnlimited((var0) -> {
         return var0 * 2.0F;
      });
   }
}
