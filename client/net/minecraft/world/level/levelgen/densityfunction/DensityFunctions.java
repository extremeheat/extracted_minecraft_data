package net.minecraft.world.level.levelgen.densityfunction;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.DistanceToPointFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.EndIslandFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.GradientFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.NoiseFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.ShiftNoiseFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.SimpleDensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.BinaryFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.BlendDensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.CacheFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.ClampFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.FindTopSurfaceFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.InterpolatedFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.IntervalSelectFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.LerpFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.PowFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.RangeChoiceFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.RoundFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.SliceFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.SplineFunction;
import net.minecraft.world.level.levelgen.densityfunction.op.UnaryFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class DensityFunctions {
   public static final float MAX_REASONABLE_NOISE_VALUE = 1000000.0F;
   public static final Codec<Float> NOISE_VALUE_CODEC = Codec.floatRange(-1000000.0F, 1000000.0F);
   private static final Codec<DensityFunction> FULL_DIRECT_CODEC;
   public static final Codec<DensityFunction> DIRECT_CODEC;
   private static final ConstantFunction ZERO;

   public static MapCodec<? extends DensityFunction> bootstrap(final Registry<MapCodec<? extends DensityFunction>> registry) {
      MapCodec<? extends DensityFunction> constant = register(registry, "constant", ConstantFunction.CODEC);

      for(SimpleDensityFunction simple : SimpleDensityFunction.values()) {
         register(registry, simple.id(), simple.codec());
      }

      register(registry, "noise", NoiseFunction.CODEC);
      register(registry, "end_outer_islands", EndIslandFunction.CODEC);
      register(registry, "distance_to_point", DistanceToPointFunction.CODEC);
      register(registry, "gradient", GradientFunction.CODEC);
      register(registry, "shift_a", ShiftNoiseFunction.ShiftA.CODEC);
      register(registry, "shift_b", ShiftNoiseFunction.ShiftB.CODEC);
      register(registry, "shift", ShiftNoiseFunction.Shift.CODEC);

      for(UnaryFunction.Type unary : UnaryFunction.Type.values()) {
         register(registry, unary.id, unary.codec);
      }

      for(RoundFunction.Type type : RoundFunction.Type.values()) {
         register(registry, type.id, type.codec);
      }

      for(BinaryFunction.Type binary : BinaryFunction.Type.values()) {
         register(registry, binary.id, binary.codec);
      }

      register(registry, "pow", PowFunction.CODEC);
      register(registry, "spline", SplineFunction.CODEC);
      register(registry, "lerp", LerpFunction.CODEC);
      register(registry, "clamp", ClampFunction.CODEC);
      register(registry, "range_choice", RangeChoiceFunction.CODEC);
      register(registry, "interval_select", IntervalSelectFunction.CODEC);
      register(registry, "cache", CacheFunction.CODEC);
      register(registry, "blend_density", BlendDensityFunction.CODEC);
      register(registry, "interpolated", InterpolatedFunction.CODEC);
      register(registry, "slice", SliceFunction.CODEC);
      register(registry, "find_top_surface", FindTopSurfaceFunction.CODEC);
      register(registry, "old_blended_noise", BlendedNoise.CODEC);
      return constant;
   }

   private static MapCodec<? extends DensityFunction> register(final Registry<MapCodec<? extends DensityFunction>> registry, final String name, final MapCodec<? extends DensityFunction> codec) {
      return (MapCodec)Registry.register(registry, (String)name, codec);
   }

   private DensityFunctions() {
      super();
   }

   public static DensityFunction zero() {
      return ZERO;
   }

   public static DensityFunction constant(final float value) {
      return new ConstantFunction(value);
   }

   public static DensityFunction abs(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.ABS, input);
   }

   public static DensityFunction square(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.SQUARE, input);
   }

   public static DensityFunction cube(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.CUBE, input);
   }

   public static DensityFunction sqrt(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.SQRT, input);
   }

   public static DensityFunction halfNegative(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.HALF_NEGATIVE, input);
   }

   public static DensityFunction quarterNegative(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.QUARTER_NEGATIVE, input);
   }

   public static DensityFunction reciprocal(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.RECIPROCAL, input);
   }

   public static DensityFunction negate(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.NEGATE, input);
   }

   public static DensityFunction squeeze(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.SQUEEZE, input);
   }

   public static DensityFunction log(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.LOG, input);
   }

   public static DensityFunction sign(final DensityFunction input) {
      return new UnaryFunction(UnaryFunction.Type.SIGN, input);
   }

   public static DensityFunction interpolated(final DensityFunction function, final int cellSizeXz, final int cellSizeY) {
      return new InterpolatedFunction(function, cellSizeXz, cellSizeY);
   }

   public static DensityFunction cache(final DensityFunction function) {
      return new CacheFunction(function);
   }

   public static DensityFunction mappedNoise(final Holder<NormalNoise> noiseData, @Deprecated final double xzScale, final double yScale, final float minTarget, final float maxTarget) {
      DensityFunction noise = new NoiseFunction(noiseData, xzScale, yScale, zero(), zero(), zero());
      return remap(noise, -1.0F, 1.0F, minTarget, maxTarget);
   }

   public static DensityFunction mappedNoise(final Holder<NormalNoise> noiseData, final double yScale, final float minTarget, final float maxTarget) {
      return mappedNoise(noiseData, 1.0, yScale, minTarget, maxTarget);
   }

   public static DensityFunction mappedNoise(final Holder<NormalNoise> noiseData, final float minTarget, final float maxTarget) {
      return mappedNoise(noiseData, 1.0, 1.0, minTarget, maxTarget);
   }

   public static DensityFunction shiftedNoise2d(final DensityFunction shiftX, final DensityFunction shiftZ, final double xzScale, final Holder<NormalNoise> noiseData) {
      return new NoiseFunction(noiseData, xzScale, 0.0, shiftX, zero(), shiftZ);
   }

   public static DensityFunction noise(final Holder<NormalNoise> noiseData) {
      return noise(noiseData, 1.0, 1.0);
   }

   public static DensityFunction noise(final Holder<NormalNoise> noiseData, final double xzScale, final double yScale) {
      return new NoiseFunction(noiseData, xzScale, yScale, zero(), zero(), zero());
   }

   public static DensityFunction noise(final Holder<NormalNoise> noiseData, final double yScale) {
      return noise(noiseData, 1.0, yScale);
   }

   public static DensityFunction rangeChoice(final DensityFunction input, final float minInclusive, final float maxExclusive, final DensityFunction whenInRange, final DensityFunction whenOutOfRange) {
      return new RangeChoiceFunction(input, minInclusive, maxExclusive, whenInRange, whenOutOfRange);
   }

   public static DensityFunction intervalSelect(final DensityFunction input, final FloatList thresholds, final List<DensityFunction> functions) {
      return new IntervalSelectFunction(input, thresholds, functions);
   }

   public static DensityFunction shiftA(final Holder<NormalNoise> noiseData) {
      return new ShiftNoiseFunction.ShiftA(noiseData);
   }

   public static DensityFunction shiftB(final Holder<NormalNoise> noiseData) {
      return new ShiftNoiseFunction.ShiftB(noiseData);
   }

   public static DensityFunction shift(final Holder<NormalNoise> noiseData) {
      return new ShiftNoiseFunction.Shift(noiseData);
   }

   public static DensityFunction blendDensity(final DensityFunction input) {
      return new BlendDensityFunction(input);
   }

   public static DensityFunction endOuterIslands() {
      return new EndIslandFunction();
   }

   public static DensityFunction distanceToPoint(final Vec3i point, final DistanceMetric metric) {
      return new DistanceToPointFunction(point, metric);
   }

   public static DensityFunction add(final DensityFunction left, final DensityFunction right) {
      return new BinaryFunction(BinaryFunction.Type.ADD, left, right);
   }

   public static DensityFunction sub(final DensityFunction left, final DensityFunction right) {
      return new BinaryFunction(BinaryFunction.Type.SUB, left, right);
   }

   public static DensityFunction mul(final DensityFunction left, final DensityFunction right) {
      return new BinaryFunction(BinaryFunction.Type.MUL, left, right);
   }

   public static DensityFunction div(final DensityFunction left, final DensityFunction right) {
      return new BinaryFunction(BinaryFunction.Type.DIV, left, right);
   }

   public static DensityFunction pow(final DensityFunction base, final DensityFunction exponent) {
      return new PowFunction(base, exponent);
   }

   public static DensityFunction clamp(final DensityFunction input, final float min, final float max) {
      return new ClampFunction(input, min, max);
   }

   public static DensityFunction min(final DensityFunction left, final DensityFunction right) {
      return new BinaryFunction(BinaryFunction.Type.MIN, left, right);
   }

   public static DensityFunction max(final DensityFunction left, final DensityFunction right) {
      return new BinaryFunction(BinaryFunction.Type.MAX, left, right);
   }

   public static DensityFunction spline(final CubicSpline<SplineFunction.Coordinate> spline) {
      return new SplineFunction(spline);
   }

   public static DensityFunction sliceY(final DensityFunction input, final int y) {
      return slice(Direction.Axis.Y, y, input);
   }

   public static DensityFunction slice(final Direction.Axis axis, final int coordinate, final DensityFunction input) {
      return new SliceFunction(axis, coordinate, input);
   }

   public static DensityFunction yClampedGradient(final int fromY, final int toY, final float fromValue, final float toValue) {
      return gradient(Direction.Axis.Y, TilingMode.CLAMP_TO_EDGE, fromY, toY, fromValue, toValue);
   }

   public static DensityFunction gradient(final Direction.Axis axis, final TilingMode tiling, final int fromCoordinate, final int toCoordinate, final float fromValue, final float toValue) {
      return new GradientFunction(axis, tiling, fromCoordinate, toCoordinate, fromValue, toValue);
   }

   public static DensityFunction blendAlpha() {
      return SimpleDensityFunction.BLEND_ALPHA;
   }

   public static DensityFunction blendOffset() {
      return SimpleDensityFunction.BLEND_OFFSET;
   }

   public static DensityFunction beardifier() {
      return SimpleDensityFunction.BEARDIFIER;
   }

   public static DensityFunction lerp(final DensityFunction alpha, final DensityFunction first, final DensityFunction second) {
      return new LerpFunction(alpha, first, second);
   }

   public static DensityFunction lerp(final DensityFunction factor, final float first, final DensityFunction second) {
      return lerp(factor, constant(first), second);
   }

   public static DensityFunction round(final RoundFunction.Type type, final DensityFunction input, final DensityFunction multiple) {
      return new RoundFunction(type, input, multiple);
   }

   public static DensityFunction round(final RoundFunction.Type type, final DensityFunction input) {
      return round(type, input, constant(1.0F));
   }

   public static DensityFunction floor(final DensityFunction input, final DensityFunction multiple) {
      return round(RoundFunction.Type.FLOOR, input, multiple);
   }

   public static DensityFunction floor(final DensityFunction input) {
      return floor(input, constant(1.0F));
   }

   public static DensityFunction round(final DensityFunction input, final DensityFunction multiple) {
      return round(RoundFunction.Type.ROUND, input, multiple);
   }

   public static DensityFunction round(final DensityFunction input) {
      return round(input, constant(1.0F));
   }

   public static DensityFunction ceil(final DensityFunction input, final DensityFunction multiple) {
      return round(RoundFunction.Type.CEIL, input, multiple);
   }

   public static DensityFunction ceil(final DensityFunction input) {
      return ceil(input, constant(1.0F));
   }

   public static DensityFunction truncate(final DensityFunction input, final DensityFunction multiple) {
      return round(RoundFunction.Type.TRUNCATE, input, multiple);
   }

   public static DensityFunction truncate(final DensityFunction input) {
      return truncate(input, constant(1.0F));
   }

   public static DensityFunction findTopSurface(final DensityFunction density, final DensityFunction upperBound, final int lowerBound, final int stepSize) {
      return new FindTopSurfaceFunction(density, upperBound, lowerBound, stepSize);
   }

   public static DensityFunction remap(final DensityFunction input, final float fromMin, final float fromMax, final float toMin, final float toMax) {
      float factor = (toMax - toMin) / (fromMax - fromMin);
      float offset = toMin - fromMin * factor;
      return offset == 0.0F ? input.mul(factor) : input.mul(factor).add(offset);
   }

   public static DensityFunction clampedMap(final DensityFunction input, final float fromMin, final float fromMax, final float toMin, final float toMax) {
      return remap(input.clamp(fromMin, fromMax), fromMin, fromMax, toMin, toMax);
   }

   static {
      FULL_DIRECT_CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE.byNameCodec().dispatch(DensityFunction::codec, Function.identity());
      DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, FULL_DIRECT_CODEC).xmap((either) -> (DensityFunction)either.map(DensityFunctions::constant, Function.identity()), (function) -> {
         Objects.requireNonNull(function);
         DensityFunction selector1$temp = function;
         int index$2 = 0;

         while(true) {
            Either var9;
            //$FF: index$2->value
            //0->net/minecraft/world/level/levelgen/densityfunction/generator/ConstantFunction
            switch (selector1$temp.typeSwitch<invokedynamic>(selector1$temp, index$2)) {
               case 0:
                  ConstantFunction $b$0 = (ConstantFunction)selector1$temp;
                  ConstantFunction var10000 = $b$0;

                  try {
                     var8 = var10000.value();
                  } catch (Throwable var7) {
                     throw new MatchException(var7.toString(), var7);
                  }

                  float patt3$temp = var8;
                  if (true) {
                     var9 = Either.left(patt3$temp);
                     return var9;
                  }

                  index$2 = 1;
                  break;
               default:
                  var9 = Either.right(function);
                  return var9;
            }
         }
      });
      ZERO = new ConstantFunction(0.0F);
   }

   public static record HolderHolder(Holder<DensityFunction> function) implements DensityFunction {
      public HolderHolder {
         super();
      }

      public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
         return ((DensityFunction)this.function.value()).compileSampler(context);
      }

      public DensityFunction rewriteChildren(final DfRewriteRule rule) {
         DensityFunction newFunction = rule.rewrite(this.function.value());
         return (DensityFunction)(newFunction == this.function.value() ? this : newFunction);
      }

      public Interval range() {
         return ((DensityFunction)this.function.value()).range();
      }

      public @DensityFunction.Axes int domainAxes() {
         return ((DensityFunction)this.function.value()).domainAxes();
      }

      public MapCodec<HolderHolder> codec() {
         throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
      }
   }
}
