package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import java.util.stream.IntStream;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.RandomSource;

public class BlendedNoise implements NoiseChunk.NoiseFiller {
   private final PerlinNoise minLimitNoise;
   private final PerlinNoise maxLimitNoise;
   private final PerlinNoise mainNoise;
   private final double xzScale;
   private final double yScale;
   private final double xzMainScale;
   private final double yMainScale;
   private final int cellWidth;
   private final int cellHeight;

   private BlendedNoise(PerlinNoise var1, PerlinNoise var2, PerlinNoise var3, NoiseSamplingSettings var4, int var5, int var6) {
      super();
      this.minLimitNoise = var1;
      this.maxLimitNoise = var2;
      this.mainNoise = var3;
      this.xzScale = 684.412D * var4.xzScale();
      this.yScale = 684.412D * var4.yScale();
      this.xzMainScale = this.xzScale / var4.xzFactor();
      this.yMainScale = this.yScale / var4.yFactor();
      this.cellWidth = var5;
      this.cellHeight = var6;
   }

   public BlendedNoise(RandomSource var1, NoiseSamplingSettings var2, int var3, int var4) {
      this(PerlinNoise.createLegacyForBlendedNoise(var1, IntStream.rangeClosed(-15, 0)), PerlinNoise.createLegacyForBlendedNoise(var1, IntStream.rangeClosed(-15, 0)), PerlinNoise.createLegacyForBlendedNoise(var1, IntStream.rangeClosed(-7, 0)), var2, var3, var4);
   }

   public double calculateNoise(int var1, int var2, int var3) {
      int var4 = Math.floorDiv(var1, this.cellWidth);
      int var5 = Math.floorDiv(var2, this.cellHeight);
      int var6 = Math.floorDiv(var3, this.cellWidth);
      double var7 = 0.0D;
      double var9 = 0.0D;
      double var11 = 0.0D;
      boolean var13 = true;
      double var14 = 1.0D;

      for(int var16 = 0; var16 < 8; ++var16) {
         ImprovedNoise var17 = this.mainNoise.getOctaveNoise(var16);
         if (var17 != null) {
            var11 += var17.noise(PerlinNoise.wrap((double)var4 * this.xzMainScale * var14), PerlinNoise.wrap((double)var5 * this.yMainScale * var14), PerlinNoise.wrap((double)var6 * this.xzMainScale * var14), this.yMainScale * var14, (double)var5 * this.yMainScale * var14) / var14;
         }

         var14 /= 2.0D;
      }

      double var30 = (var11 / 10.0D + 1.0D) / 2.0D;
      boolean var18 = var30 >= 1.0D;
      boolean var19 = var30 <= 0.0D;
      var14 = 1.0D;

      for(int var20 = 0; var20 < 16; ++var20) {
         double var21 = PerlinNoise.wrap((double)var4 * this.xzScale * var14);
         double var23 = PerlinNoise.wrap((double)var5 * this.yScale * var14);
         double var25 = PerlinNoise.wrap((double)var6 * this.xzScale * var14);
         double var27 = this.yScale * var14;
         ImprovedNoise var29;
         if (!var18) {
            var29 = this.minLimitNoise.getOctaveNoise(var20);
            if (var29 != null) {
               var7 += var29.noise(var21, var23, var25, var27, (double)var5 * var27) / var14;
            }
         }

         if (!var19) {
            var29 = this.maxLimitNoise.getOctaveNoise(var20);
            if (var29 != null) {
               var9 += var29.noise(var21, var23, var25, var27, (double)var5 * var27) / var14;
            }
         }

         var14 /= 2.0D;
      }

      return Mth.clampedLerp(var7 / 512.0D, var9 / 512.0D, var30) / 128.0D;
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder var1) {
      var1.append("BlendedNoise{minLimitNoise=");
      this.minLimitNoise.parityConfigString(var1);
      var1.append(", maxLimitNoise=");
      this.maxLimitNoise.parityConfigString(var1);
      var1.append(", mainNoise=");
      this.mainNoise.parityConfigString(var1);
      var1.append(String.format(", xzScale=%.3f, yScale=%.3f, xzMainScale=%.3f, yMainScale=%.3f, cellWidth=%d, cellHeight=%d", this.xzScale, this.yScale, this.xzMainScale, this.yMainScale, this.cellWidth, this.cellHeight)).append('}');
   }
}
