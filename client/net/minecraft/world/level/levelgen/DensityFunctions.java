package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.BoundedFloatFunction;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ExtraCodecs;
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
   protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0;
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
      register(registry, "weird_scaled_sampler", DensityFunctions.WeirdScaledSampler.CODEC);
      register(registry, "shifted_noise", DensityFunctions.ShiftedNoise.CODEC);
      register(registry, "range_choice", DensityFunctions.RangeChoice.CODEC);
      register(registry, "shift_a", DensityFunctions.ShiftA.CODEC);
      register(registry, "shift_b", DensityFunctions.ShiftB.CODEC);
      register(registry, "shift", DensityFunctions.Shift.CODEC);
      register(registry, "blend_density", DensityFunctions.BlendDensity.CODEC);
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
      return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, constructor, getter);
   }

   private static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(final BiFunction<DensityFunction, DensityFunction, O> constructor, final Function<O, DensityFunction> firstArgumentGetter, final Function<O, DensityFunction> secondArgumentGetter) {
      return KeyDispatchDataCodec.<O>of(RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(firstArgumentGetter), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(secondArgumentGetter)).apply(i, constructor)));
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
      return new BlendDensity(input);
   }

   public static DensityFunction endIslands(final long seed) {
      return new EndIslandDensityFunction(seed);
   }

   public static DensityFunction weirdScaledSampler(final DensityFunction input, final Holder<NormalNoise.NoiseParameters> noiseData, final WeirdScaledSampler.RarityValueMapper rarityValueMapper) {
      return new WeirdScaledSampler(input, new DensityFunction.NoiseHolder(noiseData), rarityValueMapper);
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

   public static DensityFunction spline(final CubicSpline<Spline.Point, Spline.Coordinate> spline) {
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
      return DensityFunctions.Mapped.create(type, function);
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

   protected static enum BlendAlpha implements DensityFunction.SimpleFunction {
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

      public double minValue() {
         return 1.0;
      }

      public double maxValue() {
         return 1.0;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      // $FF: synthetic method
      private static BlendAlpha[] $values() {
         return new BlendAlpha[]{INSTANCE};
      }
   }

   protected static enum BlendOffset implements DensityFunction.SimpleFunction {
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

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return 0.0;
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

      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static enum BeardifierMarker implements BeardifierOrMarker {
      INSTANCE;

      private BeardifierMarker() {
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return 0.0;
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         Arrays.fill(output, 0.0);
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return 0.0;
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new HolderHolder(Holder.direct(((DensityFunction)this.function.value()).mapAll(visitor))));
      }

      public double minValue() {
         return this.function.isBound() ? ((DensityFunction)this.function.value()).minValue() : -1.0 / 0.0;
      }

      public double maxValue() {
         return this.function.isBound() ? ((DensityFunction)this.function.value()).maxValue() : 1.0 / 0.0;
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

      default DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new Marker(this.type(), this.wrapped().mapAll(visitor)));
      }
   }

   protected static record Marker(Type type, DensityFunction wrapped) implements MarkerOrMarked {
      protected Marker {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return this.wrapped.compute(context);
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         this.wrapped.fillArray(output, contextProvider);
      }

      public double minValue() {
         return this.wrapped.minValue();
      }

      public double maxValue() {
         return this.wrapped.maxValue();
      }

      static enum Type implements StringRepresentable {
         Interpolated("interpolated"),
         FlatCache("flat_cache"),
         Cache2D("cache_2d"),
         CacheOnce("cache_once"),
         CacheAllInCell("cache_all_in_cell");

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
            return new Type[]{Interpolated, FlatCache, Cache2D, CacheOnce, CacheAllInCell};
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new Noise(visitor.visitNoise(this.noise), this.xzScale, this.yScale));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise.maxValue();
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

      public double minValue() {
         return -0.84375;
      }

      public double maxValue() {
         return 0.5625;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record WeirdScaledSampler(DensityFunction input, DensityFunction.NoiseHolder noise, RarityValueMapper rarityValueMapper) implements TransformerWithContext {
      private static final MapCodec<WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(WeirdScaledSampler::input), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(WeirdScaledSampler::noise), DensityFunctions.WeirdScaledSampler.RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(WeirdScaledSampler::rarityValueMapper)).apply(i, WeirdScaledSampler::new));
      public static final KeyDispatchDataCodec<WeirdScaledSampler> CODEC;

      protected WeirdScaledSampler {
         super();
      }

      public double transform(final DensityFunction.FunctionContext context, final double input) {
         double rarity = this.rarityValueMapper.mapper.get(input);
         return rarity * Math.abs(this.noise.getValue((double)context.blockX() / rarity, (double)context.blockY() / rarity, (double)context.blockZ() / rarity));
      }

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new WeirdScaledSampler(this.input.mapAll(visitor), visitor.visitNoise(this.noise), this.rarityValueMapper));
      }

      public double minValue() {
         return 0.0;
      }

      public double maxValue() {
         return this.rarityValueMapper.maxRarity * this.noise.maxValue();
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<WeirdScaledSampler>makeCodec(DATA_CODEC);
      }

      public static enum RarityValueMapper implements StringRepresentable {
         TYPE1("type_1", NoiseRouterData.QuantizedSpaghettiRarity::getSpaghettiRarity3D, 2.0),
         TYPE2("type_2", NoiseRouterData.QuantizedSpaghettiRarity::getSphaghettiRarity2D, 3.0);

         public static final Codec<RarityValueMapper> CODEC = StringRepresentable.<RarityValueMapper>fromEnum(RarityValueMapper::values);
         private final String name;
         private final Double2DoubleFunction mapper;
         private final double maxRarity;

         private RarityValueMapper(final String name, final Double2DoubleFunction mapper, final double maxRarity) {
            this.name = name;
            this.mapper = mapper;
            this.maxRarity = maxRarity;
         }

         public String getSerializedName() {
            return this.name;
         }

         // $FF: synthetic method
         private static RarityValueMapper[] $values() {
            return new RarityValueMapper[]{TYPE1, TYPE2};
         }
      }
   }

   protected static record ShiftedNoise(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) implements DensityFunction {
      private static final MapCodec<ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise)).apply(i, ShiftedNoise::new));
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new ShiftedNoise(this.shiftX.mapAll(visitor), this.shiftY.mapAll(visitor), this.shiftZ.mapAll(visitor), this.xzScale, this.yScale, visitor.visitNoise(this.noise)));
      }

      public double minValue() {
         return -this.maxValue();
      }

      public double maxValue() {
         return this.noise.maxValue();
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<ShiftedNoise>makeCodec(DATA_CODEC);
      }
   }

   private static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
      public static final MapCodec<RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(RangeChoice::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply(i, RangeChoice::new));
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new RangeChoice(this.input.mapAll(visitor), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(visitor), this.whenOutOfRange.mapAll(visitor)));
      }

      public double minValue() {
         return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
      }

      public double maxValue() {
         return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<RangeChoice>makeCodec(DATA_CODEC);
      }
   }

   interface ShiftNoise extends DensityFunction {
      DensityFunction.NoiseHolder offsetNoise();

      default double minValue() {
         return -this.maxValue();
      }

      default double maxValue() {
         return this.offsetNoise().maxValue() * 4.0;
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new ShiftA(visitor.visitNoise(this.offsetNoise)));
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new ShiftB(visitor.visitNoise(this.offsetNoise)));
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new Shift(visitor.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, Shift::new, Shift::offsetNoise);
      }
   }

   private static record BlendDensity(DensityFunction input) implements TransformerWithContext {
      private static final KeyDispatchDataCodec<BlendDensity> CODEC = DensityFunctions.<BlendDensity>singleFunctionArgumentCodec(BlendDensity::new, BlendDensity::input);

      private BlendDensity {
         super();
      }

      public double transform(final DensityFunction.FunctionContext context, final double input) {
         return context.getBlender().blendDensity(context, input);
      }

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new BlendDensity(this.input.mapAll(visitor)));
      }

      public double minValue() {
         return -1.0 / 0.0;
      }

      public double maxValue() {
         return 1.0 / 0.0;
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements PureTransformer {
      private static final MapCodec<Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(Clamp::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(Clamp::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(Clamp::maxValue)).apply(i, Clamp::new));
      public static final KeyDispatchDataCodec<Clamp> CODEC;

      protected Clamp {
         super();
      }

      public double transform(final double input) {
         return Mth.clamp(input, this.minValue, this.maxValue);
      }

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return new Clamp(this.input.mapAll(visitor), this.minValue, this.maxValue);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<Clamp>makeCodec(DATA_CODEC);
      }
   }

   protected static record Mapped(Type type, DensityFunction input, double minValue, double maxValue) implements PureTransformer {
      protected Mapped {
         super();
      }

      public static Mapped create(final Type type, final DensityFunction input) {
         double minValue = input.minValue();
         double maxValue = input.maxValue();
         double minImage = transform(type, minValue);
         double maxImage = transform(type, maxValue);
         if (type == DensityFunctions.Mapped.Type.INVERT) {
            return minValue < 0.0 && maxValue > 0.0 ? new Mapped(type, input, -1.0 / 0.0, 1.0 / 0.0) : new Mapped(type, input, maxImage, minImage);
         } else {
            return type != DensityFunctions.Mapped.Type.ABS && type != DensityFunctions.Mapped.Type.SQUARE ? new Mapped(type, input, minImage, maxImage) : new Mapped(type, input, Math.max(0.0, minValue), Math.max(minImage, maxImage));
         }
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

      public Mapped mapAll(final DensityFunction.Visitor visitor) {
         return create(this.type, this.input.mapAll(visitor));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type.codec;
      }

      static enum Type implements StringRepresentable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         INVERT("invert"),
         SQUEEZE("squeeze");

         private final String name;
         private final KeyDispatchDataCodec<Mapped> codec = DensityFunctions.<Mapped>singleFunctionArgumentCodec((input) -> DensityFunctions.Mapped.create(this, input), Mapped::input);

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

   interface TwoArgumentSimpleFunction extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static TwoArgumentSimpleFunction create(final Type type, final DensityFunction argument1, final DensityFunction argument2) {
         double min1 = argument1.minValue();
         double min2 = argument2.minValue();
         double max1 = argument1.maxValue();
         double max2 = argument2.maxValue();
         if (type == DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || type == DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) {
            boolean firstAlwaysBiggerThanSecond = min1 >= max2;
            boolean secondAlwaysBiggerThanFirst = min2 >= max1;
            if (firstAlwaysBiggerThanSecond || secondAlwaysBiggerThanFirst) {
               LOGGER.warn("Creating a {} function between two non-overlapping inputs: {} and {}", new Object[]{type, argument1, argument2});
            }
         }

         double var10000;
         switch (type.ordinal()) {
            case 0 -> var10000 = min1 + min2;
            case 1 -> var10000 = min1 > 0.0 && min2 > 0.0 ? min1 * min2 : (max1 < 0.0 && max2 < 0.0 ? max1 * max2 : Math.min(min1 * max2, max1 * min2));
            case 2 -> var10000 = Math.min(min1, min2);
            case 3 -> var10000 = Math.max(min1, min2);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         double minValue = var10000;
         switch (type.ordinal()) {
            case 0 -> var10000 = max1 + max2;
            case 1 -> var10000 = min1 > 0.0 && min2 > 0.0 ? max1 * max2 : (max1 < 0.0 && max2 < 0.0 ? min1 * min2 : Math.max(min1 * min2, max1 * max2));
            case 2 -> var10000 = Math.min(max1, max2);
            case 3 -> var10000 = Math.max(max1, max2);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         double maxValue = var10000;
         if (type == DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || type == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
            if (argument1 instanceof Constant) {
               Constant constant = (Constant)argument1;
               return new MulOrAdd(type == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, argument2, minValue, maxValue, constant.value);
            }

            if (argument2 instanceof Constant) {
               Constant constant = (Constant)argument2;
               return new MulOrAdd(type == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, argument1, minValue, maxValue, constant.value);
            }
         }

         return new Ap2(type, argument1, argument2, minValue, maxValue);
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

   private static record MulOrAdd(Type specificType, DensityFunction input, double minValue, double maxValue, double argument) implements TwoArgumentSimpleFunction, PureTransformer {
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         DensityFunction function = this.input.mapAll(visitor);
         double min = function.minValue();
         double max = function.maxValue();
         double minValue;
         double maxValue;
         if (this.specificType == DensityFunctions.MulOrAdd.Type.ADD) {
            minValue = min + this.argument;
            maxValue = max + this.argument;
         } else if (this.argument >= 0.0) {
            minValue = min * this.argument;
            maxValue = max * this.argument;
         } else {
            minValue = max * this.argument;
            maxValue = min * this.argument;
         }

         return new MulOrAdd(this.specificType, function, minValue, maxValue, this.argument);
      }

      static enum Type {
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

   private static record Ap2(TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements TwoArgumentSimpleFunction {
      private Ap2 {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         double v1 = this.argument1.compute(context);
         double var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = v1 + this.argument2.compute(context);
            case 1 -> var10000 = v1 == 0.0 ? 0.0 : v1 * this.argument2.compute(context);
            case 2 -> var10000 = v1 < this.argument2.minValue() ? v1 : Math.min(v1, this.argument2.compute(context));
            case 3 -> var10000 = v1 > this.argument2.maxValue() ? v1 : Math.max(v1, this.argument2.compute(context));
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
               double min = this.argument2.minValue();

               for(int i = 0; i < output.length; ++i) {
                  double v = output[i];
                  output[i] = v < min ? v : Math.min(v, this.argument2.compute(contextProvider.forIndex(i)));
               }
               break;
            case 3:
               double max = this.argument2.maxValue();

               for(int i = 0; i < output.length; ++i) {
                  double v = output[i];
                  output[i] = v > max ? v : Math.max(v, this.argument2.compute(contextProvider.forIndex(i)));
               }
         }

      }

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(DensityFunctions.TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll(visitor), this.argument2.mapAll(visitor)));
      }
   }

   public static record Spline(CubicSpline<Point, Coordinate> spline) implements DensityFunction {
      private static final Codec<CubicSpline<Point, Coordinate>> SPLINE_CODEC;
      private static final MapCodec<Spline> DATA_CODEC;
      public static final KeyDispatchDataCodec<Spline> CODEC;

      public Spline {
         super();
      }

      public double compute(final DensityFunction.FunctionContext context) {
         return (double)this.spline.apply(new Point(context));
      }

      public double minValue() {
         return (double)this.spline.minValue();
      }

      public double maxValue() {
         return (double)this.spline.maxValue();
      }

      public void fillArray(final double[] output, final DensityFunction.ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new Spline(this.spline.mapAll((c) -> c.mapAll(visitor))));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         SPLINE_CODEC = CubicSpline.codec(DensityFunctions.Spline.Coordinate.CODEC);
         DATA_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(Spline::new, Spline::spline);
         CODEC = DensityFunctions.<Spline>makeCodec(DATA_CODEC);
      }

      public static record Coordinate(Holder<DensityFunction> function) implements BoundedFloatFunction<Point> {
         public static final Codec<Coordinate> CODEC;

         public Coordinate {
            super();
         }

         public String toString() {
            Optional<ResourceKey<DensityFunction>> key = this.function.unwrapKey();
            if (key.isPresent()) {
               ResourceKey<DensityFunction> name = (ResourceKey)key.get();
               if (name == NoiseRouterData.CONTINENTS) {
                  return "continents";
               }

               if (name == NoiseRouterData.EROSION) {
                  return "erosion";
               }

               if (name == NoiseRouterData.RIDGES) {
                  return "weirdness";
               }

               if (name == NoiseRouterData.RIDGES_FOLDED) {
                  return "ridges";
               }
            }

            return "Coordinate[" + String.valueOf(this.function) + "]";
         }

         public float apply(final Point point) {
            return (float)((DensityFunction)this.function.value()).compute(point.context());
         }

         public float minValue() {
            return this.function.isBound() ? (float)((DensityFunction)this.function.value()).minValue() : -1.0F / 0.0F;
         }

         public float maxValue() {
            return this.function.isBound() ? (float)((DensityFunction)this.function.value()).maxValue() : 1.0F / 0.0F;
         }

         public Coordinate mapAll(final DensityFunction.Visitor visitor) {
            return new Coordinate(Holder.direct(((DensityFunction)this.function.value()).mapAll(visitor)));
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

      public double minValue() {
         return this.value;
      }

      public double maxValue() {
         return this.value;
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

      public double minValue() {
         return Math.min(this.fromValue, this.toValue);
      }

      public double maxValue() {
         return Math.max(this.fromValue, this.toValue);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<YClampedGradient>makeCodec(DATA_CODEC);
      }
   }

   private static record FindTopSurface(DensityFunction density, DensityFunction upperBound, int lowerBound, int cellHeight) implements DensityFunction {
      private static final MapCodec<FindTopSurface> DATA_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("density").forGetter(FindTopSurface::density), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("upper_bound").forGetter(FindTopSurface::upperBound), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("lower_bound").forGetter(FindTopSurface::lowerBound), ExtraCodecs.POSITIVE_INT.fieldOf("cell_height").forGetter(FindTopSurface::cellHeight)).apply(i, FindTopSurface::new));
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

      public DensityFunction mapAll(final DensityFunction.Visitor visitor) {
         return visitor.apply(new FindTopSurface(this.density.mapAll(visitor), this.upperBound.mapAll(visitor), this.lowerBound, this.cellHeight));
      }

      public double minValue() {
         return (double)this.lowerBound;
      }

      public double maxValue() {
         return Math.max((double)this.lowerBound, this.upperBound.maxValue());
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<FindTopSurface>makeCodec(DATA_CODEC);
      }
   }
}
