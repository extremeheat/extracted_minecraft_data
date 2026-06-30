package net.minecraft.world.level.levelgen;

import com.google.common.collect.Comparators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.BoundedFloatFunction;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Interval;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

public final class DensityFunctions {
   private static final Codec<DensityFunction> CODEC;
   static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0;
   private static final Codec<Double> NOISE_VALUE_CODEC;
   public static final Codec<DensityFunction> DIRECT_CODEC;

   public static MapCodec<? extends DensityFunction> bootstrap(final Registry<MapCodec<? extends DensityFunction>> registry) {
      register(registry, "blend_alpha", DensityFunctions.BlendAlpha.CODEC);
      register(registry, "blend_offset", DensityFunctions.BlendOffset.CODEC);
      register(registry, "beardifier", DensityFunctions.BeardifierMarker.CODEC);
      register(registry, "old_blended_noise", BlendedNoise.CODEC);

      for(Marker.Type value : DensityFunctions.Marker.Type.values()) {
         register(registry, value.getSerializedName(), value.codec);
      }

      register(registry, "noise", DensityFunctions.Noise.CODEC);
      register(registry, "end_islands", DensityFunctions.EndIslandDensityFunction.CODEC);
      register(registry, "shifted_noise", DensityFunctions.ShiftedNoise.CODEC);
      register(registry, "range_choice", DensityFunctions.RangeChoice.CODEC);
      register(registry, "interval_select", DensityFunctions.IntervalSelect.CODEC);
      register(registry, "shift_a", DensityFunctions.ShiftA.CODEC);
      register(registry, "shift_b", DensityFunctions.ShiftB.CODEC);
      register(registry, "shift", DensityFunctions.Shift.CODEC);
      register(registry, "clamp", DensityFunctions.Clamp.CODEC);

      for(Mapped.Type value : DensityFunctions.Mapped.Type.values()) {
         register(registry, value.getSerializedName(), value.codec);
      }

      for(TwoArgumentSimpleFunction.Type value : DensityFunctions.TwoArgumentSimpleFunction.Type.values()) {
         register(registry, value.getSerializedName(), value.codec);
      }

      register(registry, "spline", DensityFunctions.Spline.CODEC);
      register(registry, "constant", DensityFunctions.Constant.CODEC);
      register(registry, "y_clamped_gradient", DensityFunctions.YClampedGradient.CODEC);
      return register(registry, "find_top_surface", DensityFunctions.FindTopSurface.CODEC);
   }

   private static MapCodec<? extends DensityFunction> register(final Registry<MapCodec<? extends DensityFunction>> registry, final String name, final KeyDispatchDataCodec<? extends DensityFunction> codec) {
      return (MapCodec)Registry.register(registry, (String)name, codec.codec());
   }

   private static <A, O> KeyDispatchDataCodec<O> singleArgumentCodec(final Codec<A> argumentCodec, final Function<A, O> constructor, final Function<O, A> getter) {
      return KeyDispatchDataCodec.<O>of(argumentCodec.fieldOf("argument").xmap(constructor, getter));
   }

   private static <O> KeyDispatchDataCodec<O> singleFunctionArgumentCodec(final Function<DensityFunction, O> constructor, final Function<O, DensityFunction> getter) {
      return singleArgumentCodec(DensityFunction.CODEC, constructor, getter);
   }

   private static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(final BiFunction<DensityFunction, DensityFunction, O> constructor, final Function<O, DensityFunction> firstArgumentGetter, final Function<O, DensityFunction> secondArgumentGetter) {
      return KeyDispatchDataCodec.<O>of(RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("argument1").forGetter(firstArgumentGetter), DensityFunction.CODEC.fieldOf("argument2").forGetter(secondArgumentGetter)).apply(i, constructor)));
   }

   private static <O> KeyDispatchDataCodec<O> makeCodec(final MapCodec<O> dataCodec) {
      return KeyDispatchDataCodec.<O>of(dataCodec);
   }

   private DensityFunctions() {
      super();
   }

   public static DensityFunction interpolated(final DensityFunction function) {
      return new Marker(DensityFunctions.Marker.Type.Interpolated, function);
   }

   public static DensityFunction flatCache(final DensityFunction function) {
      return new Marker(DensityFunctions.Marker.Type.FlatCache, function);
   }

   public static DensityFunction cache2d(final DensityFunction function) {
      return new Marker(DensityFunctions.Marker.Type.Cache2D, function);
   }

   public static DensityFunction cacheOnce(final DensityFunction function) {
      return new Marker(DensityFunctions.Marker.Type.CacheOnce, function);
   }

   public static DensityFunction cacheAllInCell(final DensityFunction function) {
      return new Marker(DensityFunctions.Marker.Type.CacheAllInCell, function);
   }

   public static DensityFunction mappedNoise(final Holder<NormalNoise.NoiseParameters> noiseData, @Deprecated final double xzScale, final double yScale, final double minTarget, final double maxTarget) {
      return mapFromUnitTo(new Noise(new DensityFunction.NoiseHolder(noiseData), xzScale, yScale), minTarget, maxTarget);
   }

   public static DensityFunction mappedNoise(final Holder<NormalNoise.NoiseParameters> noiseData, final double yScale, final double minTarget, final double maxTarget) {
      return mappedNoise(noiseData, 1.0, yScale, minTarget, maxTarget);
   }

   public static DensityFunction mappedNoise(final Holder<NormalNoise.NoiseParameters> noiseData, final double minTarget, final double maxTarget) {
      return mappedNoise(noiseData, 1.0, 1.0, minTarget, maxTarget);
   }

   public static DensityFunction shiftedNoise2d(final DensityFunction shiftX, final DensityFunction shiftZ, final double xzScale, final Holder<NormalNoise.NoiseParameters> noiseData) {
      return new ShiftedNoise(shiftX, zero(), shiftZ, xzScale, 0.0, new DensityFunction.NoiseHolder(noiseData));
   }

   public static DensityFunction noise(final Holder<NormalNoise.NoiseParameters> noiseData) {
      return noise(noiseData, 1.0, 1.0);
   }

   public static DensityFunction noise(final Holder<NormalNoise.NoiseParameters> noiseData, final double xzScale, final double yScale) {
      return new Noise(new DensityFunction.NoiseHolder(noiseData), xzScale, yScale);
   }

   public static DensityFunction noise(final Holder<NormalNoise.NoiseParameters> noiseData, final double yScale) {
      return noise(noiseData, 1.0, yScale);
   }

   public static DensityFunction rangeChoice(final DensityFunction input, final double minInclusive, final double maxExclusive, final DensityFunction whenInRange, final DensityFunction whenOutOfRange) {
      return new RangeChoice(input, minInclusive, maxExclusive, whenInRange, whenOutOfRange);
   }

   public static DensityFunction intervalSelect(final DensityFunction input, final DoubleList thresholds, final List<DensityFunction> functions) {
      return new IntervalSelect(input, thresholds, functions);
   }

   public static DensityFunction shiftA(final Holder<NormalNoise.NoiseParameters> noiseData) {
      return new ShiftA(new DensityFunction.NoiseHolder(noiseData));
   }

   public static DensityFunction shiftB(final Holder<NormalNoise.NoiseParameters> noiseData) {
      return new ShiftB(new DensityFunction.NoiseHolder(noiseData));
   }

   public static DensityFunction shift(final Holder<NormalNoise.NoiseParameters> noiseData) {
      return new Shift(new DensityFunction.NoiseHolder(noiseData));
   }

   public static DensityFunction blendDensity(final DensityFunction input) {
      return new Marker(DensityFunctions.Marker.Type.BlendDensity, input);
   }

   public static DensityFunction endIslands(final long seed) {
      return new EndIslandDensityFunction(seed);
   }

   public static DensityFunction add(final DensityFunction f1, final DensityFunction f2) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.ADD, f1, f2);
   }

   public static DensityFunction mul(final DensityFunction f1, final DensityFunction f2) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MUL, f1, f2);
   }

   public static DensityFunction min(final DensityFunction f1, final DensityFunction f2) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MIN, f1, f2);
   }

   public static DensityFunction max(final DensityFunction f1, final DensityFunction f2) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MAX, f1, f2);
   }

   public static DensityFunction spline(final CubicSpline<Spline.Coordinate> spline) {
      return new Spline(spline);
   }

   public static DensityFunction zero() {
      return DensityFunctions.Constant.ZERO;
   }

   public static DensityFunction constant(final double value) {
      return new Constant(value);
   }

   public static DensityFunction yClampedGradient(final int fromY, final int toY, final double fromValue, final double toValue) {
      return new YClampedGradient(fromY, toY, fromValue, toValue);
   }

   public static DensityFunction map(final DensityFunction function, final Mapped.Type type) {
      return new Mapped(type, function);
   }

   private static DensityFunction mapFromUnitTo(final DensityFunction function, final double min, final double max) {
      double middle = (min + max) * 0.5;
      double factor = (max - min) * 0.5;
      return add(constant(middle), mul(constant(factor), function));
   }

   public static DensityFunction blendAlpha() {
      return DensityFunctions.BlendAlpha.INSTANCE;
   }

   public static DensityFunction blendOffset() {
      return DensityFunctions.BlendOffset.INSTANCE;
   }

   public static DensityFunction lerp(final DensityFunction alpha, final DensityFunction first, final DensityFunction second) {
      if (first instanceof Constant constant) {
         return lerp(alpha, constant.value, second);
      } else {
         DensityFunction alphaCached = cacheOnce(alpha);
         DensityFunction oneMinusAlpha = add(mul(alphaCached, constant(-1.0)), constant(1.0));
         return add(mul(first, oneMinusAlpha), mul(second, alphaCached));
      }
   }

   public static DensityFunction lerp(final DensityFunction factor, final double first, final DensityFunction second) {
      return add(mul(factor, add(second, constant(-first))), constant(first));
   }

   public static DensityFunction findTopSurface(final DensityFunction density, final DensityFunction upperBound, final int lowerBound, final int stepSize) {
      return new FindTopSurface(density, upperBound, lowerBound, stepSize);
   }

   static {
      CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE.byNameCodec().dispatch((function) -> function.codec().codec(), Function.identity());
      NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0, 1000000.0);
      DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap((either) -> (DensityFunction)either.map(DensityFunctions::constant, Function.identity()), (function) -> {
         if (function instanceof Constant constant) {
            return Either.left(constant.value());
         } else {
            return Either.right(function);
         }
      });
   }

   private interface TransformerWithContext extends DensityFunction {
      DensityFunction input();

      default double compute(final DensityFunction.FunctionContext context) {
         return this.transform(context, this.input().compute(context));
      }

      default void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.input().fillArray(output, contextProvider);

         for(int i = 0; i < output.length; ++i) {
            output[i] = this.transform(contextProvider.forIndex(i), output[i]);
         }

      }

      double transform(DensityFunction.FunctionContext contextSupplier, final double input);
   }

   private interface PureTransformer extends DensityFunction {
      DensityFunction input();

      default double compute(final DensityFunction.FunctionContext context) {
         return this.transform(this.input().compute(context));
      }

      default void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.input().fillArray(output, contextProvider);

         for(int i = 0; i < output.length; ++i) {
            output[i] = this.transform(output[i]);
         }

      }

      double transform(final double input);
   }

   static enum BlendAlpha implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.<DensityFunction>of(MapCodec.unit(INSTANCE));

      private BlendAlpha() {
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return 1.0;
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         Arrays.fill(output, 1.0);
      }

      public Interval range() {
         return Interval.of(0.0, 1.0);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      // $FF: synthetic method
      private static BlendAlpha[] $values() {
         return new BlendAlpha[]{INSTANCE};
      }
   }

   static enum BlendOffset implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.<DensityFunction>of(MapCodec.unit(INSTANCE));

      private BlendOffset() {
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return 0.0;
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         Arrays.fill(output, 0.0);
      }

      public Interval range() {
         return Interval.INFINITE;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      // $FF: synthetic method
      private static BlendOffset[] $values() {
         return new BlendOffset[]{INSTANCE};
      }
   }

   public interface BeardifierOrMarker extends DensityFunction.SimpleFunction {
      KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.<DensityFunction>of(MapCodec.unit(DensityFunctions.BeardifierMarker.INSTANCE));

      default Interval range() {
         return Beardifier.RANGE;
      }

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   static enum BeardifierMarker implements BeardifierOrMarker {
      INSTANCE;

      private BeardifierMarker() {
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return 0.0;
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         Arrays.fill(output, 0.0);
      }

      // $FF: synthetic method
      private static BeardifierMarker[] $values() {
         return new BeardifierMarker[]{INSTANCE};
      }
   }

   @VisibleForDebug
   public static record HolderHolder(Holder<DensityFunction> function) implements DensityFunction {
      public HolderHolder {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return ((DensityFunction)this.function.value()).compute(context);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         ((DensityFunction)this.function.value()).fillArray(output, contextProvider);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new HolderHolder(Holder.direct(visitor.apply(this.function.value())));
      }

      public Interval range() {
         return this.function.isBound() ? ((DensityFunction)this.function.value()).range() : Interval.INFINITE;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
      }
   }

   public interface MarkerOrMarked extends DensityFunction {
      Marker.Type type();

      DensityFunction wrapped();

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      default DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new Marker(this.type(), visitor.apply(this.wrapped()));
      }
   }

   static record Marker(Type type, DensityFunction wrapped) implements MarkerOrMarked {
      Marker {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.wrapped.compute(context);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.wrapped.fillArray(output, contextProvider);
      }

      public Interval range() {
         Interval var10000;
         switch (this.type.ordinal()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
               var10000 = this.wrapped.range();
               break;
            case 5:
               var10000 = Interval.INFINITE;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public static enum Type implements StringRepresentable {
         Interpolated("interpolated"),
         FlatCache("flat_cache"),
         Cache2D("cache_2d"),
         CacheOnce("cache_once"),
         CacheAllInCell("cache_all_in_cell"),
         BlendDensity("blend_density");

         private final String name;
         private final KeyDispatchDataCodec<MarkerOrMarked> codec = DensityFunctions.<MarkerOrMarked>singleFunctionArgumentCodec((input) -> new Marker(this, input), MarkerOrMarked::wrapped);

         private Type(final String name) {
            this.name = name;
         }

         public String getSerializedName() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{Interpolated, FlatCache, Cache2D, CacheOnce, CacheAllInCell, BlendDensity};
         }
      }
   }

   protected static record Noise(DensityFunction.NoiseHolder noise, double xzScale, double yScale) implements DensityFunction {
      public static final MapCodec<Noise> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(Noise::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(Noise::yScale)).apply(i, Noise::new));
      public static final KeyDispatchDataCodec<Noise> CODEC;

      protected Noise(DensityFunction.NoiseHolder noise, @Deprecated double xzScale, double yScale) {
         super();
         this.noise = noise;
         this.xzScale = xzScale;
         this.yScale = yScale;
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.noise.getValue((double)context.blockX() * this.xzScale, (double)context.blockY() * this.yScale, (double)context.blockZ() * this.xzScale);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new Noise(visitor.visitNoise(this.noise), this.xzScale, this.yScale);
      }

      public Interval range() {
         return Interval.ofSymmetric(this.noise.maxValue());
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      /** @deprecated */
      @Deprecated
      public double xzScale() {
         return this.xzScale;
      }

      static {
         CODEC = DensityFunctions.<Noise>makeCodec(DATA_CODEC);
      }
   }

   protected static final class EndIslandDensityFunction implements DensityFunction.SimpleFunction {
      public static final KeyDispatchDataCodec<EndIslandDensityFunction> CODEC = KeyDispatchDataCodec.<EndIslandDensityFunction>of(MapCodec.unit(new EndIslandDensityFunction(0L)));
      private static final float ISLAND_THRESHOLD = -0.9F;
      private final SimplexNoise islandNoise;

      public EndIslandDensityFunction(final long seed) {
         super();
         RandomSource islandRandom = new LegacyRandomSource(seed);
         islandRandom.consumeCount(17292);
         this.islandNoise = new SimplexNoise(islandRandom);
      }

      private static float getHeightValue(final SimplexNoise islandNoise, final int sectionX, final int sectionZ) {
         int chunkX = sectionX / 2;
         int chunkZ = sectionZ / 2;
         int subSectionX = sectionX % 2;
         int subSectionZ = sectionZ % 2;
         float doffs = 100.0F - Mth.sqrt((float)(sectionX * sectionX + sectionZ * sectionZ)) * 8.0F;
         doffs = Mth.clamp(doffs, -100.0F, 80.0F);

         for(int xo = -12; xo <= 12; ++xo) {
            for(int zo = -12; zo <= 12; ++zo) {
               long totalChunkX = (long)(chunkX + xo);
               long totalChunkZ = (long)(chunkZ + zo);
               if (totalChunkX * totalChunkX + totalChunkZ * totalChunkZ > 4096L && islandNoise.getValue((double)totalChunkX, (double)totalChunkZ) < -0.8999999761581421) {
                  float islandSize = (Mth.abs((float)totalChunkX) * 3439.0F + Mth.abs((float)totalChunkZ) * 147.0F) % 13.0F + 9.0F;
                  float xd = (float)(subSectionX - xo * 2);
                  float zd = (float)(subSectionZ - zo * 2);
                  float newDoffs = 100.0F - Mth.sqrt(xd * xd + zd * zd) * islandSize;
                  newDoffs = Mth.clamp(newDoffs, -100.0F, 80.0F);
                  doffs = Math.max(doffs, newDoffs);
               }
            }
         }

         return doffs;
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return ((double)getHeightValue(this.islandNoise, context.blockX() / 8, context.blockZ() / 8) - 8.0) / 128.0;
      }

      public Interval range() {
         return Interval.of(-0.84375, 0.5625);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) implements DensityFunction {
      private static final MapCodec<ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), DensityFunction.CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), DensityFunction.CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise)).apply(i, ShiftedNoise::new));
      public static final KeyDispatchDataCodec<ShiftedNoise> CODEC;

      protected ShiftedNoise {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         double x = (double)context.blockX() * this.xzScale + this.shiftX.compute(context);
         double y = (double)context.blockY() * this.yScale + this.shiftY.compute(context);
         double z = (double)context.blockZ() * this.xzScale + this.shiftZ.compute(context);
         return this.noise.getValue(x, y, z);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftedNoise(visitor.apply(this.shiftX), visitor.apply(this.shiftY), visitor.apply(this.shiftZ), this.xzScale, this.yScale, visitor.visitNoise(this.noise));
      }

      public Interval range() {
         return Interval.ofSymmetric(this.noise.maxValue());
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<ShiftedNoise>makeCodec(DATA_CODEC);
      }
   }

   private static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
      public static final MapCodec<RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(RangeChoice::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), DensityFunction.CODEC.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), DensityFunction.CODEC.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply(i, RangeChoice::new));
      public static final KeyDispatchDataCodec<RangeChoice> CODEC;

      private RangeChoice {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         double inputValue = this.input.compute(context);
         return inputValue >= this.minInclusive && inputValue < this.maxExclusive ? this.whenInRange.compute(context) : this.whenOutOfRange.compute(context);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.input.fillArray(output, contextProvider);

         for(int i = 0; i < output.length; ++i) {
            double v = output[i];
            if (v >= this.minInclusive && v < this.maxExclusive) {
               output[i] = this.whenInRange.compute(contextProvider.forIndex(i));
            } else {
               output[i] = this.whenOutOfRange.compute(contextProvider.forIndex(i));
            }
         }

      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new RangeChoice(visitor.apply(this.input), this.minInclusive, this.maxExclusive, visitor.apply(this.whenInRange), visitor.apply(this.whenOutOfRange));
      }

      public Interval range() {
         return Interval.encapsulating(this.whenInRange.range(), this.whenOutOfRange.range());
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<RangeChoice>makeCodec(DATA_CODEC);
      }
   }

   private static record IntervalSelect(DensityFunction input, DoubleList thresholds, List<DensityFunction> functions) implements DensityFunction {
      private static final Codec<DoubleList> THRESHOLDS_CODEC;
      public static final MapCodec<IntervalSelect> DATA_CODEC;
      public static final KeyDispatchDataCodec<IntervalSelect> CODEC;

      private IntervalSelect {
         super();
      }

      private DataResult<IntervalSelect> validate() {
         if (this.thresholds.size() != this.functions.size() - 1) {
            return DataResult.error(() -> {
               int var10000 = this.functions.size() - 1;
               return "Expected " + var10000 + " thresholds for " + this.functions.size() + " functions, but got " + this.thresholds.size();
            });
         } else {
            return !Comparators.isInOrder(this.thresholds, Double::compare) ? DataResult.error(() -> "Threshold values must be ordered from smallest to largest") : DataResult.success(this);
         }
      }

      private double compute(final DensityFunction.FunctionContext context, final double input) {
         for(int i = 0; i < this.thresholds.size(); ++i) {
            if (input < this.thresholds.getDouble(i)) {
               return ((DensityFunction)this.functions.get(i)).compute(context);
            }
         }

         return ((DensityFunction)this.functions.getLast()).compute(context);
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.compute(context, this.input.compute(context));
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.input.fillArray(output, contextProvider);

         for(int i = 0; i < output.length; ++i) {
            output[i] = this.compute(contextProvider.forIndex(i), output[i]);
         }

      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         DensityFunction var10002 = visitor.apply(this.input);
         DoubleList var10003 = this.thresholds;
         List var10004 = this.functions;
         Objects.requireNonNull(visitor);
         return new IntervalSelect(var10002, var10003, List.copyOf(Lists.transform(var10004, visitor::apply)));
      }

      public Interval range() {
         return Interval.encapsulating(Lists.transform(this.functions, DensityFunction::range));
      }

      public KeyDispatchDataCodec<IntervalSelect> codec() {
         return CODEC;
      }

      static {
         THRESHOLDS_CODEC = DensityFunctions.NOISE_VALUE_CODEC.listOf().xmap(DoubleArrayList::new, Function.identity());
         DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(IntervalSelect::input), THRESHOLDS_CODEC.fieldOf("thresholds").forGetter(IntervalSelect::thresholds), DensityFunction.CODEC.listOf(2, 2147483647).fieldOf("functions").forGetter(IntervalSelect::functions)).apply(i, IntervalSelect::new)).validate(IntervalSelect::validate);
         CODEC = DensityFunctions.<IntervalSelect>makeCodec(DATA_CODEC);
      }
   }

   protected interface ShiftNoise extends DensityFunction {
      DensityFunction.NoiseHolder offsetNoise();

      default Interval range() {
         return Interval.ofSymmetric(this.offsetNoise().maxValue() * 4.0);
      }

      default double compute(final double localX, final double localY, final double localZ) {
         return this.offsetNoise().getValue(localX * 0.25, localY * 0.25, localZ * 0.25) * 4.0;
      }

      default void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }
   }

   protected static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
      private static final KeyDispatchDataCodec<ShiftA> CODEC;

      protected ShiftA {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockX(), 0.0, (double)context.blockZ());
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftA(visitor.visitNoise(this.offsetNoise));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, ShiftA::new, ShiftA::offsetNoise);
      }
   }

   protected static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
      private static final KeyDispatchDataCodec<ShiftB> CODEC;

      protected ShiftB {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockZ(), (double)context.blockX(), 0.0);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new ShiftB(visitor.visitNoise(this.offsetNoise));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, ShiftB::new, ShiftB::offsetNoise);
      }
   }

   protected static record Shift(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
      private static final KeyDispatchDataCodec<Shift> CODEC;

      protected Shift {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.compute((double)context.blockX(), (double)context.blockY(), (double)context.blockZ());
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new Shift(visitor.visitNoise(this.offsetNoise));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, Shift::new, Shift::offsetNoise);
      }
   }

   protected static record Clamp(DensityFunction input, double min, double max) implements PureTransformer {
      private static final MapCodec<Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(Clamp::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(Clamp::min), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(Clamp::max)).apply(i, Clamp::new)).validate(Clamp::validate);
      public static final KeyDispatchDataCodec<Clamp> CODEC;

      protected Clamp {
         super();
      }

      private static DataResult<Clamp> validate(final Clamp clamp) {
         return clamp.max < clamp.min ? DataResult.error(() -> "min (" + clamp.min + ") must be less than or equal to max (" + clamp.max + ")") : DataResult.success(clamp);
      }

      public double transform(final double input) {
         return Mth.clamp(input, this.min, this.max);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new Clamp(visitor.apply(this.input), this.min, this.max);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public Interval range() {
         return Interval.clamp(this.input.range(), this.min, this.max);
      }

      static {
         CODEC = DensityFunctions.<Clamp>makeCodec(DATA_CODEC);
      }
   }

   protected static record Mapped(Type type, DensityFunction input) implements PureTransformer {
      protected Mapped {
         super();
      }

      private static double transform(final Type type, final double input) {
         double var10000;
         switch (type.ordinal()) {
            case 0:
               var10000 = Math.abs(input);
               break;
            case 1:
               var10000 = input * input;
               break;
            case 2:
               var10000 = input * input * input;
               break;
            case 3:
               var10000 = input > 0.0 ? input : input * 0.5;
               break;
            case 4:
               var10000 = input > 0.0 ? input : input * 0.25;
               break;
            case 5:
               var10000 = 1.0 / input;
               break;
            case 6:
               double c = Mth.clamp(input, -1.0, 1.0);
               var10000 = c / 2.0 - c * c * c / 24.0;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public double transform(final double input) {
         return transform(this.type, input);
      }

      public Mapped mapChildren(final DensityFunction.Visitor visitor) {
         return new Mapped(this.type, visitor.apply(this.input));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type.codec;
      }

      public Interval range() {
         Interval input = this.input.range();
         Interval var10000;
         switch (this.type.ordinal()) {
            case 0:
               var10000 = Interval.abs(input);
               break;
            case 1:
               var10000 = Interval.square(input);
               break;
            case 2:
            case 3:
            case 4:
            case 6:
               var10000 = Interval.mapMonotonic(input, (value) -> transform(this.type, value));
               break;
            case 5:
               var10000 = Interval.inverse(input);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public static enum Type implements StringRepresentable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         INVERT("invert"),
         SQUEEZE("squeeze");

         private final String name;
         private final KeyDispatchDataCodec<Mapped> codec = DensityFunctions.<Mapped>singleFunctionArgumentCodec((input) -> new Mapped(this, input), Mapped::input);

         private Type(final String name) {
            this.name = name;
         }

         public String getSerializedName() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{ABS, SQUARE, CUBE, HALF_NEGATIVE, QUARTER_NEGATIVE, INVERT, SQUEEZE};
         }
      }
   }

   public interface TwoArgumentSimpleFunction extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static TwoArgumentSimpleFunction create(final Type type, final DensityFunction argument1, final DensityFunction argument2) {
         if (type == DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || type == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
            if (argument1 instanceof Constant) {
               Constant constant = (Constant)argument1;
               return new MulOrAdd(type == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, argument2, constant.value);
            }

            if (argument2 instanceof Constant) {
               Constant constant = (Constant)argument2;
               return new MulOrAdd(type == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, argument1, constant.value);
            }
         }

         return new Ap2(type, argument1, argument2);
      }

      Type type();

      DensityFunction argument1();

      DensityFunction argument2();

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      public static enum Type implements StringRepresentable {
         ADD("add"),
         MUL("mul"),
         MIN("min"),
         MAX("max");

         private final KeyDispatchDataCodec<TwoArgumentSimpleFunction> codec = DensityFunctions.<TwoArgumentSimpleFunction>doubleFunctionArgumentCodec((argument1, argument2) -> DensityFunctions.TwoArgumentSimpleFunction.create(this, argument1, argument2), TwoArgumentSimpleFunction::argument1, TwoArgumentSimpleFunction::argument2);
         private final String name;

         private Type(final String name) {
            this.name = name;
         }

         public String getSerializedName() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{ADD, MUL, MIN, MAX};
         }
      }
   }

   private static record MulOrAdd(Type specificType, DensityFunction input, double argument) implements TwoArgumentSimpleFunction, PureTransformer {
      private MulOrAdd {
         super();
      }

      public TwoArgumentSimpleFunction.Type type() {
         return this.specificType == DensityFunctions.MulOrAdd.Type.MUL ? DensityFunctions.TwoArgumentSimpleFunction.Type.MUL : DensityFunctions.TwoArgumentSimpleFunction.Type.ADD;
      }

      public DensityFunction argument1() {
         return DensityFunctions.constant(this.argument);
      }

      public DensityFunction argument2() {
         return this.input;
      }

      public double transform(final double input) {
         double var10000;
         switch (this.specificType.ordinal()) {
            case 0 -> var10000 = input * this.argument;
            case 1 -> var10000 = input + this.argument;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new MulOrAdd(this.specificType, visitor.apply(this.input), this.argument);
      }

      public Interval range() {
         Interval var10000;
         switch (this.specificType.ordinal()) {
            case 0 -> var10000 = Interval.mul(this.input.range(), Interval.ofExact(this.argument));
            case 1 -> var10000 = Interval.add(this.input.range(), Interval.ofExact(this.argument));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public static enum Type {
         MUL,
         ADD;

         private Type() {
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{MUL, ADD};
         }
      }
   }

   private static record Ap2(TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue2, double maxValue2) implements TwoArgumentSimpleFunction {
      public Ap2(final TwoArgumentSimpleFunction.Type type, final DensityFunction argument1, final DensityFunction argument2) {
         Interval range2 = argument2.range();
         this(type, argument1, argument2, range2.min(), range2.max());
         if ((type == DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || type == DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) && !argument1.range().intersects(range2)) {
            LOGGER.warn("Creating a {} function between two non-overlapping inputs: {} and {}", new Object[]{type, argument1, argument2});
         }

      }

      private Ap2 {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         double v1 = this.argument1.compute(context);
         double var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = v1 + this.argument2.compute(context);
            case 1 -> var10000 = v1 == 0.0 ? 0.0 : v1 * this.argument2.compute(context);
            case 2 -> var10000 = v1 < this.minValue2 ? v1 : Math.min(v1, this.argument2.compute(context));
            case 3 -> var10000 = v1 > this.maxValue2 ? v1 : Math.max(v1, this.argument2.compute(context));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.argument1.fillArray(output, contextProvider);
         switch (this.type.ordinal()) {
            case 0:
               double[] v2 = new double[output.length];
               this.argument2.fillArray(v2, contextProvider);

               for(int i = 0; i < output.length; ++i) {
                  output[i] += v2[i];
               }
               break;
            case 1:
               for(int i = 0; i < output.length; ++i) {
                  double v = output[i];
                  output[i] = v == 0.0 ? 0.0 : v * this.argument2.compute(contextProvider.forIndex(i));
               }
               break;
            case 2:
               for(int i = 0; i < output.length; ++i) {
                  double v = output[i];
                  output[i] = v < this.minValue2 ? v : Math.min(v, this.argument2.compute(contextProvider.forIndex(i)));
               }
               break;
            case 3:
               for(int i = 0; i < output.length; ++i) {
                  double v = output[i];
                  output[i] = v > this.maxValue2 ? v : Math.max(v, this.argument2.compute(contextProvider.forIndex(i)));
               }
         }

      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return DensityFunctions.TwoArgumentSimpleFunction.create(this.type, visitor.apply(this.argument1), visitor.apply(this.argument2));
      }

      public Interval range() {
         Interval range1 = this.argument1.range();
         Interval range2 = this.argument2.range();
         Interval var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = Interval.add(range1, range2);
            case 1 -> var10000 = Interval.mul(range1, range2);
            case 2 -> var10000 = Interval.min(range1, range2);
            case 3 -> var10000 = Interval.max(range1, range2);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   public static final class Spline implements DensityFunction {
      private static final Codec<CubicSpline<Coordinate>> SPLINE_CODEC;
      private static final MapCodec<Spline> DATA_CODEC;
      public static final KeyDispatchDataCodec<Spline> CODEC;
      private final CubicSpline<Coordinate> spline;
      private final BoundedFloatFunction<Point> sampler;

      public Spline(final CubicSpline<Coordinate> spline) {
         super();
         this.spline = spline;
         this.sampler = CubicSpline.asSampler(spline);
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return (double)this.sampler.apply(new Point(context));
      }

      public Interval range() {
         return this.spline.range();
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new Spline(this.spline.mapCoordinates((c) -> c.mapChildren(visitor)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public CubicSpline<Coordinate> spline() {
         return this.spline;
      }

      public boolean equals(final Object obj) {
         if (obj == this) {
            return true;
         } else {
            boolean var10000;
            if (obj instanceof Spline) {
               Spline splineFunction = (Spline)obj;
               if (this.spline.equals(splineFunction.spline)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      public int hashCode() {
         return this.spline.hashCode();
      }

      public String toString() {
         return this.spline.toString();
      }

      static {
         SPLINE_CODEC = CubicSpline.codec(DensityFunctions.Spline.Coordinate.CODEC);
         DATA_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(Spline::new, Spline::spline);
         CODEC = DensityFunctions.<Spline>makeCodec(DATA_CODEC);
      }

      public static record Coordinate(DensityFunction function) implements BoundedFloatFunction<Point> {
         public static final Codec<Coordinate> CODEC;

         public Coordinate {
            super();
         }

         public float apply(final Point point) {
            return (float)this.function.compute(point.context());
         }

         public Interval range() {
            return this.function.range();
         }

         public Coordinate mapChildren(final DensityFunction.Visitor visitor) {
            return new Coordinate(visitor.apply(this.function));
         }

         static {
            CODEC = DensityFunction.CODEC.xmap(Coordinate::new, Coordinate::function);
         }
      }

      public static record Point(DensityFunction.FunctionContext context) {
         public Point {
            super();
         }
      }
   }

   private static record Constant(double value) implements DensityFunction.SimpleFunction {
      private static final KeyDispatchDataCodec<Constant> CODEC;
      private static final Constant ZERO;

      private Constant {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.value;
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         Arrays.fill(output, this.value);
      }

      public Interval range() {
         return Interval.ofExact(this.value);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunctions.NOISE_VALUE_CODEC, Constant::new, Constant::value);
         ZERO = new Constant(0.0);
      }
   }

   private static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
      private static final MapCodec<YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("from_y").forGetter(YClampedGradient::fromY), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("to_y").forGetter(YClampedGradient::toY), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(YClampedGradient::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(YClampedGradient::toValue)).apply(i, YClampedGradient::new));
      public static final KeyDispatchDataCodec<YClampedGradient> CODEC;

      private YClampedGradient {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return Mth.clampedMap((double)context.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
      }

      public Interval range() {
         return Interval.encapsulating(this.fromValue, this.toValue);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<YClampedGradient>makeCodec(DATA_CODEC);
      }
   }

   private static record FindTopSurface(DensityFunction density, DensityFunction upperBound, int lowerBound, int cellHeight) implements DensityFunction {
      private static final MapCodec<FindTopSurface> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("density").forGetter(FindTopSurface::density), DensityFunction.CODEC.fieldOf("upper_bound").forGetter(FindTopSurface::upperBound), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("lower_bound").forGetter(FindTopSurface::lowerBound), ExtraCodecs.POSITIVE_INT.fieldOf("cell_height").forGetter(FindTopSurface::cellHeight)).apply(i, FindTopSurface::new));
      public static final KeyDispatchDataCodec<FindTopSurface> CODEC;

      private FindTopSurface {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         int topY = Mth.floor(this.upperBound.compute(context) / (double)this.cellHeight) * this.cellHeight;
         if (topY <= this.lowerBound) {
            return (double)this.lowerBound;
         } else {
            for(int blockY = topY; blockY >= this.lowerBound; blockY -= this.cellHeight) {
               if (this.density.compute(new DensityFunction.SinglePointContext(context.blockX(), blockY, context.blockZ())) > 0.0) {
                  return (double)blockY;
               }
            }

            return (double)this.lowerBound;
         }
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new FindTopSurface(visitor.apply(this.density), visitor.apply(this.upperBound), this.lowerBound, this.cellHeight);
      }

      public Interval range() {
         return Interval.of((double)this.lowerBound, Math.max((double)this.lowerBound, this.upperBound.range().max()));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<FindTopSurface>makeCodec(DATA_CODEC);
      }
   }
}
