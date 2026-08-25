package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.BoundedFloatFunction;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Interval;
import net.minecraft.util.Unit;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public record SplineFunction(CubicSpline<Coordinate> spline) implements DensityFunction {
   private static final Codec<CubicSpline<Coordinate>> SPLINE_CODEC;
   public static final MapCodec<SplineFunction> CODEC;

   public SplineFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      return new Sampler(context, this.spline);
   }

   public Interval range() {
      return this.spline.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      MutableInt axes = new MutableInt(0);
      this.spline.forEachCoordinate((coordinate) -> axes.setValue(axes.intValue() | coordinate.function().domainAxes()));
      return axes.intValue();
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      MutableBoolean changed = new MutableBoolean();
      CubicSpline<Coordinate> newSpline = this.spline.<Coordinate>mapCoordinates((coordinate) -> {
         DensityFunction function = coordinate.function();
         DensityFunction newFunction = rule.rewrite(function);
         if (newFunction != function) {
            changed.setTrue();
            return new Coordinate(newFunction);
         } else {
            return coordinate;
         }
      });
      return changed.isFalse() ? this : new SplineFunction(newSpline);
   }

   public MapCodec<SplineFunction> codec() {
      return CODEC;
   }

   public String toString() {
      return this.spline.toString();
   }

   static {
      SPLINE_CODEC = CubicSpline.codec(SplineFunction.Coordinate.CODEC);
      CODEC = SPLINE_CODEC.fieldOf("spline").xmap(SplineFunction::new, SplineFunction::spline);
   }

   public static record Coordinate(DensityFunction function) implements BoundedFloatFunction<Unit> {
      public static final Codec<Coordinate> CODEC;

      public Coordinate {
         super();
      }

      public float apply(final Unit point) {
         return 0.0F;
      }

      public Interval range() {
         return this.function.range();
      }

      static {
         CODEC = DensityFunction.CODEC.xmap(Coordinate::new, Coordinate::function);
      }
   }

   private static class Sampler implements DensitySampler {
      private final BoundedFloatFunction<SplineInput> sampler;
      private final int coordinateCount;

      public Sampler(final DensityFunction.CompileContext context, final CubicSpline<Coordinate> spline) {
         super();
         Map<DensityFunction, SamplerCoordinate> coordinates = new HashMap();
         this.sampler = CubicSpline.asSampler(spline.mapCoordinates((coordinate) -> (SamplerCoordinate)coordinates.computeIfAbsent(coordinate.function(), (function) -> {
               int index = coordinates.size();
               DensitySampler sampler = function.compileSampler(context);
               return new SamplerCoordinate(sampler, index, function.range());
            })));
         this.coordinateCount = coordinates.size();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         try (BufferSplineInput input = new BufferSplineInput(context, volume, this.coordinateCount)) {
            for(int i = 0; i < outputBuffer.size(); ++i) {
               input.index = i;
               outputBuffer.set(i, this.sampler.apply(input));
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.sampler.apply(new PointSplineInput(context, blockX, blockY, blockZ, this.coordinateCount));
      }
   }

   private static record SamplerCoordinate(DensitySampler sampler, int index, Interval range) implements BoundedFloatFunction<SplineInput> {
      private SamplerCoordinate {
         super();
      }

      public float apply(final SplineInput input) {
         return input.sampleCoordinate(this.sampler, this.index);
      }
   }

   private static class BufferSplineInput implements SplineInput, AutoCloseable {
      private final SamplerContext context;
      private final DensityVolume volume;
      private final @Nullable ScopedDensityBuffer[] buffers;
      private int index;

      private BufferSplineInput(final SamplerContext context, final DensityVolume volume, final int coordinateCount) {
         super();
         this.context = context;
         this.volume = volume;
         this.buffers = new ScopedDensityBuffer[coordinateCount];
      }

      public float sampleCoordinate(final DensitySampler coordinateSampler, final int coordinateIndex) {
         ScopedDensityBuffer buffer = this.buffers[coordinateIndex];
         if (buffer == null) {
            buffer = this.context.acquireBuffer(this.volume);
            coordinateSampler.sampleVolume(this.context, buffer, this.volume);
            this.buffers[coordinateIndex] = buffer;
         }

         return buffer.get(this.index);
      }

      public void close() {
         for(ScopedDensityBuffer buffer : this.buffers) {
            if (buffer != null) {
               buffer.close();
            }
         }

      }
   }

   private static class PointSplineInput implements SplineInput {
      private final SamplerContext context;
      private final int blockX;
      private final int blockY;
      private final int blockZ;
      private final float[] cachedValues;

      private PointSplineInput(final SamplerContext context, final int blockX, final int blockY, final int blockZ, final int coordinateCount) {
         super();
         this.context = context;
         this.blockX = blockX;
         this.blockY = blockY;
         this.blockZ = blockZ;
         this.cachedValues = new float[coordinateCount];
         Arrays.fill(this.cachedValues, 0.0F / 0.0F);
      }

      public float sampleCoordinate(final DensitySampler coordinateSampler, final int coordinateIndex) {
         float cachedValue = this.cachedValues[coordinateIndex];
         if (!Float.isNaN(cachedValue)) {
            return cachedValue;
         } else {
            float value = coordinateSampler.sampleValue(this.context, this.blockX, this.blockY, this.blockZ);
            this.cachedValues[coordinateIndex] = value;
            return value;
         }
      }
   }

   @FunctionalInterface
   private interface SplineInput {
      float sampleCoordinate(DensitySampler coordinateSampler, int coordinateIndex);
   }
}
