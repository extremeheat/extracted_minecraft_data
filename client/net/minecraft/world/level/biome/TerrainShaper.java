package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;

public final class TerrainShaper {
   private static final Codec<CubicSpline<TerrainShaper.Point>> SPLINE_CODEC;
   public static final Codec<TerrainShaper> CODEC;
   private static final float GLOBAL_OFFSET = -0.50375F;
   private static final ToFloatFunction<Float> NO_TRANSFORM;
   private final CubicSpline<TerrainShaper.Point> offsetSampler;
   private final CubicSpline<TerrainShaper.Point> factorSampler;
   private final CubicSpline<TerrainShaper.Point> jaggednessSampler;

   public TerrainShaper(CubicSpline<TerrainShaper.Point> var1, CubicSpline<TerrainShaper.Point> var2, CubicSpline<TerrainShaper.Point> var3) {
      super();
      this.offsetSampler = var1;
      this.factorSampler = var2;
      this.jaggednessSampler = var3;
   }

   private static float getAmplifiedOffset(float var0) {
      return var0 < 0.0F ? var0 : var0 * 2.0F;
   }

   private static float getAmplifiedFactor(float var0) {
      return 1.25F - 6.25F / (var0 + 5.0F);
   }

   private static float getAmplifiedJaggedness(float var0) {
      return var0 * 2.0F;
   }

   public static TerrainShaper overworld(boolean var0) {
      ToFloatFunction var1 = var0 ? TerrainShaper::getAmplifiedOffset : NO_TRANSFORM;
      ToFloatFunction var2 = var0 ? TerrainShaper::getAmplifiedFactor : NO_TRANSFORM;
      ToFloatFunction var3 = var0 ? TerrainShaper::getAmplifiedJaggedness : NO_TRANSFORM;
      CubicSpline var4 = buildErosionOffsetSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, var1);
      CubicSpline var5 = buildErosionOffsetSpline(-0.1F, 0.03F, 0.1F, 0.1F, 0.01F, -0.03F, false, false, var1);
      CubicSpline var6 = buildErosionOffsetSpline(-0.1F, 0.03F, 0.1F, 0.7F, 0.01F, -0.03F, true, true, var1);
      CubicSpline var7 = buildErosionOffsetSpline(-0.05F, 0.03F, 0.1F, 1.0F, 0.01F, 0.01F, true, true, var1);
      float var8 = -0.51F;
      float var9 = -0.4F;
      float var10 = 0.1F;
      float var11 = -0.15F;
      CubicSpline var12 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, var1).addPoint(-1.1F, 0.044F, 0.0F).addPoint(-1.02F, -0.2222F, 0.0F).addPoint(-0.51F, -0.2222F, 0.0F).addPoint(-0.44F, -0.12F, 0.0F).addPoint(-0.18F, -0.12F, 0.0F).addPoint(-0.16F, var4, 0.0F).addPoint(-0.15F, var4, 0.0F).addPoint(-0.1F, var5, 0.0F).addPoint(0.25F, var6, 0.0F).addPoint(1.0F, var7, 0.0F).build();
      CubicSpline var13 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, NO_TRANSFORM).addPoint(-0.19F, 3.95F, 0.0F).addPoint(-0.15F, getErosionFactor(6.25F, true, NO_TRANSFORM), 0.0F).addPoint(-0.1F, getErosionFactor(5.47F, true, var2), 0.0F).addPoint(0.03F, getErosionFactor(5.08F, true, var2), 0.0F).addPoint(0.06F, getErosionFactor(4.69F, false, var2), 0.0F).build();
      float var14 = 0.65F;
      CubicSpline var15 = CubicSpline.builder(TerrainShaper.Coordinate.CONTINENTS, var3).addPoint(-0.11F, 0.0F, 0.0F).addPoint(0.03F, buildErosionJaggednessSpline(1.0F, 0.5F, 0.0F, 0.0F, var3), 0.0F).addPoint(0.65F, buildErosionJaggednessSpline(1.0F, 1.0F, 1.0F, 0.0F, var3), 0.0F).build();
      return new TerrainShaper(var12, var13, var15);
   }

   private static CubicSpline<TerrainShaper.Point> buildErosionJaggednessSpline(float var0, float var1, float var2, float var3, ToFloatFunction<Float> var4) {
      float var5 = -0.5775F;
      CubicSpline var6 = buildRidgeJaggednessSpline(var0, var2, var4);
      CubicSpline var7 = buildRidgeJaggednessSpline(var1, var3, var4);
      return CubicSpline.builder(TerrainShaper.Coordinate.EROSION, var4).addPoint(-1.0F, var6, 0.0F).addPoint(-0.78F, var7, 0.0F).addPoint(-0.5775F, var7, 0.0F).addPoint(-0.375F, 0.0F, 0.0F).build();
   }

   private static CubicSpline<TerrainShaper.Point> buildRidgeJaggednessSpline(float var0, float var1, ToFloatFunction<Float> var2) {
      float var3 = peaksAndValleys(0.4F);
      float var4 = peaksAndValleys(0.56666666F);
      float var5 = (var3 + var4) / 2.0F;
      CubicSpline.Builder var6 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var2);
      var6.addPoint(var3, 0.0F, 0.0F);
      if (var1 > 0.0F) {
         var6.addPoint(var5, buildWeirdnessJaggednessSpline(var1, var2), 0.0F);
      } else {
         var6.addPoint(var5, 0.0F, 0.0F);
      }

      if (var0 > 0.0F) {
         var6.addPoint(1.0F, buildWeirdnessJaggednessSpline(var0, var2), 0.0F);
      } else {
         var6.addPoint(1.0F, 0.0F, 0.0F);
      }

      return var6.build();
   }

   private static CubicSpline<TerrainShaper.Point> buildWeirdnessJaggednessSpline(float var0, ToFloatFunction<Float> var1) {
      float var2 = 0.63F * var0;
      float var3 = 0.3F * var0;
      return CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, var1).addPoint(-0.01F, var2, 0.0F).addPoint(0.01F, var3, 0.0F).build();
   }

   private static CubicSpline<TerrainShaper.Point> getErosionFactor(float var0, boolean var1, ToFloatFunction<Float> var2) {
      CubicSpline var3 = CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, var2).addPoint(-0.2F, 6.3F, 0.0F).addPoint(0.2F, var0, 0.0F).build();
      CubicSpline.Builder var4 = CubicSpline.builder(TerrainShaper.Coordinate.EROSION, var2).addPoint(-0.6F, var3, 0.0F).addPoint(-0.5F, CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, var2).addPoint(-0.05F, 6.3F, 0.0F).addPoint(0.05F, 2.67F, 0.0F).build(), 0.0F).addPoint(-0.35F, var3, 0.0F).addPoint(-0.25F, var3, 0.0F).addPoint(-0.1F, CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, var2).addPoint(-0.05F, 2.67F, 0.0F).addPoint(0.05F, 6.3F, 0.0F).build(), 0.0F).addPoint(0.03F, var3, 0.0F);
      CubicSpline var5;
      CubicSpline var6;
      if (var1) {
         var5 = CubicSpline.builder(TerrainShaper.Coordinate.WEIRDNESS, var2).addPoint(0.0F, var0, 0.0F).addPoint(0.1F, 0.625F, 0.0F).build();
         var6 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var2).addPoint(-0.9F, var0, 0.0F).addPoint(-0.69F, var5, 0.0F).build();
         var4.addPoint(0.35F, var0, 0.0F).addPoint(0.45F, var6, 0.0F).addPoint(0.55F, var6, 0.0F).addPoint(0.62F, var0, 0.0F);
      } else {
         var5 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var2).addPoint(-0.7F, var3, 0.0F).addPoint(-0.15F, 1.37F, 0.0F).build();
         var6 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var2).addPoint(0.45F, var3, 0.0F).addPoint(0.7F, 1.56F, 0.0F).build();
         var4.addPoint(0.05F, var6, 0.0F).addPoint(0.4F, var6, 0.0F).addPoint(0.45F, var5, 0.0F).addPoint(0.55F, var5, 0.0F).addPoint(0.58F, var0, 0.0F);
      }

      return var4.build();
   }

   private static float calculateSlope(float var0, float var1, float var2, float var3) {
      return (var1 - var0) / (var3 - var2);
   }

   private static CubicSpline<TerrainShaper.Point> buildMountainRidgeSplineWithPoints(float var0, boolean var1, ToFloatFunction<Float> var2) {
      CubicSpline.Builder var3 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var2);
      float var4 = -0.7F;
      float var5 = -1.0F;
      float var6 = mountainContinentalness(-1.0F, var0, -0.7F);
      float var7 = 1.0F;
      float var8 = mountainContinentalness(1.0F, var0, -0.7F);
      float var9 = calculateMountainRidgeZeroContinentalnessPoint(var0);
      float var10 = -0.65F;
      float var11;
      if (-0.65F < var9 && var9 < 1.0F) {
         var11 = mountainContinentalness(-0.65F, var0, -0.7F);
         float var12 = -0.75F;
         float var13 = mountainContinentalness(-0.75F, var0, -0.7F);
         float var14 = calculateSlope(var6, var13, -1.0F, -0.75F);
         var3.addPoint(-1.0F, var6, var14);
         var3.addPoint(-0.75F, var13, 0.0F);
         var3.addPoint(-0.65F, var11, 0.0F);
         float var15 = mountainContinentalness(var9, var0, -0.7F);
         float var16 = calculateSlope(var15, var8, var9, 1.0F);
         float var17 = 0.01F;
         var3.addPoint(var9 - 0.01F, var15, 0.0F);
         var3.addPoint(var9, var15, var16);
         var3.addPoint(1.0F, var8, var16);
      } else {
         var11 = calculateSlope(var6, var8, -1.0F, 1.0F);
         if (var1) {
            var3.addPoint(-1.0F, Math.max(0.2F, var6), 0.0F);
            var3.addPoint(0.0F, Mth.lerp(0.5F, var6, var8), var11);
         } else {
            var3.addPoint(-1.0F, var6, var11);
         }

         var3.addPoint(1.0F, var8, var11);
      }

      return var3.build();
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

   private static CubicSpline<TerrainShaper.Point> buildErosionOffsetSpline(float var0, float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7, ToFloatFunction<Float> var8) {
      float var9 = 0.6F;
      float var10 = 0.5F;
      float var11 = 0.5F;
      CubicSpline var12 = buildMountainRidgeSplineWithPoints(Mth.lerp(var3, 0.6F, 1.5F), var7, var8);
      CubicSpline var13 = buildMountainRidgeSplineWithPoints(Mth.lerp(var3, 0.6F, 1.0F), var7, var8);
      CubicSpline var14 = buildMountainRidgeSplineWithPoints(var3, var7, var8);
      CubicSpline var15 = ridgeSpline(var0 - 0.15F, 0.5F * var3, Mth.lerp(0.5F, 0.5F, 0.5F) * var3, 0.5F * var3, 0.6F * var3, 0.5F, var8);
      CubicSpline var16 = ridgeSpline(var0, var4 * var3, var1 * var3, 0.5F * var3, 0.6F * var3, 0.5F, var8);
      CubicSpline var17 = ridgeSpline(var0, var4, var4, var1, var2, 0.5F, var8);
      CubicSpline var18 = ridgeSpline(var0, var4, var4, var1, var2, 0.5F, var8);
      CubicSpline var19 = CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var8).addPoint(-1.0F, var0, 0.0F).addPoint(-0.4F, var17, 0.0F).addPoint(0.0F, var2 + 0.07F, 0.0F).build();
      CubicSpline var20 = ridgeSpline(-0.02F, var5, var5, var1, var2, 0.0F, var8);
      CubicSpline.Builder var21 = CubicSpline.builder(TerrainShaper.Coordinate.EROSION, var8).addPoint(-0.85F, var12, 0.0F).addPoint(-0.7F, var13, 0.0F).addPoint(-0.4F, var14, 0.0F).addPoint(-0.35F, var15, 0.0F).addPoint(-0.1F, var16, 0.0F).addPoint(0.2F, var17, 0.0F);
      if (var6) {
         var21.addPoint(0.4F, var18, 0.0F).addPoint(0.45F, var19, 0.0F).addPoint(0.55F, var19, 0.0F).addPoint(0.58F, var18, 0.0F);
      }

      var21.addPoint(0.7F, var20, 0.0F);
      return var21.build();
   }

   private static CubicSpline<TerrainShaper.Point> ridgeSpline(float var0, float var1, float var2, float var3, float var4, float var5, ToFloatFunction<Float> var6) {
      float var7 = Math.max(0.5F * (var1 - var0), var5);
      float var8 = 5.0F * (var2 - var1);
      return CubicSpline.builder(TerrainShaper.Coordinate.RIDGES, var6).addPoint(-1.0F, var0, var7).addPoint(-0.4F, var1, Math.min(var7, var8)).addPoint(0.0F, var2, var8).addPoint(0.4F, var3, 2.0F * (var3 - var2)).addPoint(1.0F, var4, 0.7F * (var4 - var3)).build();
   }

   public void addDebugBiomesToVisualizeSplinePoints(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> var1) {
      Climate.Parameter var2 = Climate.Parameter.span(-1.0F, 1.0F);
      var1.accept(Pair.of(Climate.parameters(var2, var2, var2, var2, Climate.Parameter.point(0.0F), var2, 0.01F), Biomes.PLAINS));
      CubicSpline.Multipoint var3 = (CubicSpline.Multipoint)buildErosionOffsetSpline(-0.15F, 0.0F, 0.0F, 0.1F, 0.0F, -0.03F, false, false, NO_TRANSFORM);
      ResourceKey var4 = Biomes.DESERT;
      float[] var5 = var3.locations();
      int var6 = var5.length;

      int var7;
      Float var8;
      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         var1.accept(Pair.of(Climate.parameters(var2, var2, var2, Climate.Parameter.point(var8), Climate.Parameter.point(0.0F), var2, 0.0F), var4));
         var4 = var4 == Biomes.DESERT ? Biomes.BADLANDS : Biomes.DESERT;
      }

      var5 = ((CubicSpline.Multipoint)this.offsetSampler).locations();
      var6 = var5.length;

      for(var7 = 0; var7 < var6; ++var7) {
         var8 = var5[var7];
         var1.accept(Pair.of(Climate.parameters(var2, var2, Climate.Parameter.point(var8), var2, Climate.Parameter.point(0.0F), var2, 0.0F), Biomes.SNOWY_TAIGA));
      }

   }

   @VisibleForDebug
   public CubicSpline<TerrainShaper.Point> offsetSampler() {
      return this.offsetSampler;
   }

   @VisibleForDebug
   public CubicSpline<TerrainShaper.Point> factorSampler() {
      return this.factorSampler;
   }

   @VisibleForDebug
   public CubicSpline<TerrainShaper.Point> jaggednessSampler() {
      return this.jaggednessSampler;
   }

   public float offset(TerrainShaper.Point var1) {
      return this.offsetSampler.apply(var1) + -0.50375F;
   }

   public float factor(TerrainShaper.Point var1) {
      return this.factorSampler.apply(var1);
   }

   public float jaggedness(TerrainShaper.Point var1) {
      return this.jaggednessSampler.apply(var1);
   }

   public TerrainShaper.Point makePoint(float var1, float var2, float var3) {
      return new TerrainShaper.Point(var1, var2, peaksAndValleys(var3), var3);
   }

   public static float peaksAndValleys(float var0) {
      return -(Math.abs(Math.abs(var0) - 0.6666667F) - 0.33333334F) * 3.0F;
   }

   static {
      SPLINE_CODEC = CubicSpline.codec(TerrainShaper.Coordinate.WIDE_CODEC);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(SPLINE_CODEC.fieldOf("offset").forGetter(TerrainShaper::offsetSampler), SPLINE_CODEC.fieldOf("factor").forGetter(TerrainShaper::factorSampler), SPLINE_CODEC.fieldOf("jaggedness").forGetter((var0x) -> {
            return var0x.jaggednessSampler;
         })).apply(var0, TerrainShaper::new);
      });
      NO_TRANSFORM = (var0) -> {
         return var0;
      };
   }

   @VisibleForTesting
   protected static enum Coordinate implements StringRepresentable, ToFloatFunction<TerrainShaper.Point> {
      CONTINENTS(TerrainShaper.Point::continents, "continents"),
      EROSION(TerrainShaper.Point::erosion, "erosion"),
      WEIRDNESS(TerrainShaper.Point::weirdness, "weirdness"),
      /** @deprecated */
      @Deprecated
      RIDGES(TerrainShaper.Point::ridges, "ridges");

      private static final Map<String, TerrainShaper.Coordinate> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(TerrainShaper.Coordinate::getSerializedName, (var0) -> {
         return var0;
      }));
      private static final Codec<TerrainShaper.Coordinate> CODEC;
      static final Codec<ToFloatFunction<TerrainShaper.Point>> WIDE_CODEC;
      private final ToFloatFunction<TerrainShaper.Point> reference;
      private final String name;

      private Coordinate(ToFloatFunction<TerrainShaper.Point> var3, String var4) {
         this.reference = var3;
         this.name = var4;
      }

      public String getSerializedName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public float apply(TerrainShaper.Point var1) {
         return this.reference.apply(var1);
      }

      // $FF: synthetic method
      public float apply(Object var1) {
         return this.apply((TerrainShaper.Point)var1);
      }

      // $FF: synthetic method
      private static TerrainShaper.Coordinate[] $values() {
         return new TerrainShaper.Coordinate[]{CONTINENTS, EROSION, WEIRDNESS, RIDGES};
      }

      static {
         Supplier var10000 = TerrainShaper.Coordinate::values;
         Map var10001 = BY_NAME;
         Objects.requireNonNull(var10001);
         CODEC = StringRepresentable.fromEnum(var10000, var10001::get);
         WIDE_CODEC = CODEC.flatComapMap((var0) -> {
            return var0;
         }, (var0) -> {
            DataResult var10000;
            if (var0 instanceof TerrainShaper.Coordinate) {
               TerrainShaper.Coordinate var1 = (TerrainShaper.Coordinate)var0;
               var10000 = DataResult.success(var1);
            } else {
               var10000 = DataResult.error("Not a coordinate resolver: " + var0);
            }

            return var10000;
         });
      }
   }

   public static record Point(float a, float b, float c, float d) {
      private final float continents;
      private final float erosion;
      private final float ridges;
      private final float weirdness;

      public Point(float var1, float var2, float var3, float var4) {
         super();
         this.continents = var1;
         this.erosion = var2;
         this.ridges = var3;
         this.weirdness = var4;
      }

      public float continents() {
         return this.continents;
      }

      public float erosion() {
         return this.erosion;
      }

      public float ridges() {
         return this.ridges;
      }

      public float weirdness() {
         return this.weirdness;
      }
   }
}
