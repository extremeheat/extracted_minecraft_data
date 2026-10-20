package net.minecraft.world.level.levelgen.densityfunction.op.upscale;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.util.Ease;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.util.monomorphizer.MonomorphizableDelegate;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import org.slf4j.Logger;

public interface Upscaler {
   static void upscale(final Cache cache, final AxisPlan x, final AxisPlan y, final AxisPlan z, final DensityBuffer outputBuffer, final DensityVolume outputVolume, final DensityBuffer cellBuffer, final DensityVolume cellVolume) {
      Upscaler upscaler = cache.getOrCreate(x.mode, y.mode, z.mode, x.cellSize, y.cellSize, z.cellSize);
      upscaler.doUpscale(x, y, z, outputBuffer, outputVolume, cellBuffer, cellVolume);
   }

   void doUpscale(AxisPlan x, AxisPlan y, AxisPlan z, DensityBuffer outputBuffer, DensityVolume outputVolume, DensityBuffer cellBuffer, DensityVolume cellVolume);

   float upscaleValue(int xInCell, int yInCell, int zInCell, DensityBuffer cellBuffer);

   public static final class Delegate extends MonomorphizableDelegate<Upscaler, PolymorphicUpscaler> implements Upscaler {
      protected Delegate(final PolymorphicUpscaler target) {
         super(target);
      }

      public void doUpscale(final AxisPlan x, final AxisPlan y, final AxisPlan z, final DensityBuffer outputBuffer, final DensityVolume outputVolume, final DensityBuffer cellBuffer, final DensityVolume cellVolume) {
         ((PolymorphicUpscaler)this.target).doUpscale(x, y, z, outputBuffer, outputVolume, cellBuffer, cellVolume);
      }

      public float upscaleValue(final int xInCell, final int yInCell, final int zInCell, final DensityBuffer cellBuffer) {
         return ((PolymorphicUpscaler)this.target).upscaleValue(xInCell, yInCell, zInCell, cellBuffer);
      }
   }

   public static record AxisPlan(UpscaleFunction.Mode mode, boolean exactlyAligned, int cellSampleCount, int cellSampleMinBlock, int cellSampleStep, int cellOutputCount, int minCellOutput, int cellSize) {
      public AxisPlan {
         super();
      }

      public static AxisPlan exactlyAligned(final int outputMinBlock, final int outputSize, final int outputStepBlocks) {
         return new AxisPlan(UpscaleFunction.Mode.NEAREST_NEIGHBOR, true, outputSize, outputMinBlock, outputStepBlocks, outputSize, 0, 1);
      }
   }

   public interface Coefficients {
      Coefficients NEAREST_NEIGHBOR = new Nearest();

      static Coefficients[] computeTable(final int cellSize, final UpscaleFunction.Mode mode) {
         Coefficients[] table = new Coefficients[cellSize];

         for(int i = 0; i < cellSize; ++i) {
            table[i] = compute(mode, (float)i / (float)cellSize);
         }

         return table;
      }

      static Interval modifyRange(final Interval input, final int cellSize, final UpscaleFunction.Mode mode) {
         return Interval.encapsulating(Arrays.stream(computeTable(cellSize, mode)).map((coefficients) -> coefficients.modifyRange(input)).toList());
      }

      static Coefficients compute(final UpscaleFunction.Mode mode, final float alpha) {
         Coefficients var10000;
         switch (mode) {
            case NEAREST_NEIGHBOR -> var10000 = NEAREST_NEIGHBOR;
            case INTERPOLATE_LINEAR -> var10000 = interpolateLinear(alpha);
            case INTERPOLATE_COSINE -> var10000 = interpolateCosine(alpha);
            case INTERPOLATE_CUBIC -> var10000 = interpolateCubic(alpha);
            case B_SPLINE_CUBIC -> var10000 = cubicBSpline(alpha);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      static Coefficients interpolateLinear(final float alpha) {
         return new Linear(1.0F - alpha, alpha);
      }

      static Coefficients interpolateCosine(final float alpha) {
         return interpolateLinear(Ease.inOutSine(alpha));
      }

      static Coefficients interpolateCubic(final float alpha) {
         return new Cubic(-0.5F * Mth.cube(alpha) + Mth.square(alpha) - 0.5F * alpha, 1.5F * Mth.cube(alpha) - 2.5F * Mth.square(alpha) + 1.0F, -1.5F * Mth.cube(alpha) + 2.0F * Mth.square(alpha) + 0.5F * alpha, 0.5F * Mth.cube(alpha) - 0.5F * Mth.square(alpha));
      }

      static Coefficients cubicBSpline(final float alpha) {
         return new Cubic(Mth.cube(1.0F - alpha) / 6.0F, (3.0F * Mth.cube(alpha) - 6.0F * Mth.square(alpha) + 4.0F) / 6.0F, (-3.0F * Mth.cube(alpha) + 3.0F * Mth.square(alpha) + 3.0F * alpha + 1.0F) / 6.0F, Mth.cube(alpha) / 6.0F);
      }

      float multiply(float[] samples, int startIndex, int stride);

      float[] asArray();

      default Interval modifyRange(final Interval input) {
         Interval sum = Interval.ofExact(0.0F);

         for(float coefficient : this.asArray()) {
            sum = Interval.add(sum, Interval.mul(Interval.ofExact(coefficient), input));
         }

         return sum;
      }

      public static record Nearest() implements Coefficients {
         private static final float[] ARRAY = new float[]{1.0F};

         public Nearest() {
            super();
         }

         public float multiply(final float[] samples, final int startIndex, final int stride) {
            return samples[startIndex];
         }

         public float[] asArray() {
            return ARRAY;
         }
      }

      public static record Linear(float c1, float c2) implements Coefficients {
         public Linear {
            super();
         }

         public float multiply(final float[] samples, final int startIndex, final int stride) {
            return this.c1 * samples[startIndex] + this.c2 * samples[startIndex + stride];
         }

         public float[] asArray() {
            return new float[]{this.c1, this.c2};
         }
      }

      public static record Cubic(float c1, float c2, float c3, float c4) implements Coefficients {
         public Cubic {
            super();
         }

         public float multiply(final float[] samples, final int startIndex, final int stride) {
            return this.c1 * samples[startIndex] + this.c2 * samples[startIndex + stride] + this.c3 * samples[startIndex + 2 * stride] + this.c4 * samples[startIndex + 3 * stride];
         }

         public float[] asArray() {
            return new float[]{this.c1, this.c2, this.c3, this.c4};
         }
      }
   }

   public static record Parameters(UpscaleFunction.Mode modeX, UpscaleFunction.Mode modeY, UpscaleFunction.Mode modeZ, int cellSizeX, int cellSizeY, int cellSizeZ) {
      public Parameters {
         super();
      }
   }

   public static class Cache {
      private static final Logger LOGGER = LogUtils.getLogger();
      private static final int WARN_THRESHOLD = 64;
      private final ConcurrentMap<Parameters, Upscaler> cache = new ConcurrentHashMap();

      public Cache() {
         super();
      }

      public Upscaler getOrCreate(final UpscaleFunction.Mode modeX, final UpscaleFunction.Mode modeY, final UpscaleFunction.Mode modeZ, final int cellSizeX, final int cellSizeY, final int cellSizeZ) {
         Parameters parameters = new Parameters(modeX, modeY, modeZ, cellSizeX, cellSizeY, cellSizeZ);
         Upscaler existingUpscaler = (Upscaler)this.cache.get(parameters);
         if (existingUpscaler != null) {
            return existingUpscaler;
         } else {
            synchronized(this) {
               int lastSize = this.cache.size();
               Upscaler upscaler = (Upscaler)this.cache.computeIfAbsent(parameters, (p) -> (new PolymorphicUpscaler(p)).makeMonomorphicDelegate());
               if (lastSize < 64 && this.cache.size() >= 64) {
                  LOGGER.warn("Exceeded {} upscalers in cache - this is probably bad!", 64);
               }

               return upscaler;
            }
         }
      }
   }
}
