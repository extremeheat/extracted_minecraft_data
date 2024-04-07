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
import net.minecraft.util.CubicSpline;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ToFloatFunction;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.slf4j.Logger;

public final class DensityFunctions {
   private static final Codec<DensityFunction> CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE
      .byNameCodec()
      .dispatch(var0 -> var0.codec().codec(), Function.identity());
   protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0;
   static final Codec<Double> NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0, 1000000.0);
   public static final Codec<DensityFunction> DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC)
      .xmap(
         var0 -> (DensityFunction)var0.map(DensityFunctions::constant, Function.identity()),
         var0 -> var0 instanceof DensityFunctions.Constant var1 ? Either.left(var1.value()) : Either.right(var0)
      );

   public static MapCodec<? extends DensityFunction> bootstrap(Registry<MapCodec<? extends DensityFunction>> var0) {
      register(var0, "blend_alpha", DensityFunctions.BlendAlpha.CODEC);
      register(var0, "blend_offset", DensityFunctions.BlendOffset.CODEC);
      register(var0, "beardifier", DensityFunctions.BeardifierMarker.CODEC);
      register(var0, "old_blended_noise", BlendedNoise.CODEC);

      for (DensityFunctions.Marker.Type var4 : DensityFunctions.Marker.Type.values()) {
         register(var0, var4.getSerializedName(), var4.codec);
      }

      register(var0, "noise", DensityFunctions.Noise.CODEC);
      register(var0, "end_islands", DensityFunctions.EndIslandDensityFunction.CODEC);
      register(var0, "weird_scaled_sampler", DensityFunctions.WeirdScaledSampler.CODEC);
      register(var0, "shifted_noise", DensityFunctions.ShiftedNoise.CODEC);
      register(var0, "range_choice", DensityFunctions.RangeChoice.CODEC);
      register(var0, "shift_a", DensityFunctions.ShiftA.CODEC);
      register(var0, "shift_b", DensityFunctions.ShiftB.CODEC);
      register(var0, "shift", DensityFunctions.Shift.CODEC);
      register(var0, "blend_density", DensityFunctions.BlendDensity.CODEC);
      register(var0, "clamp", DensityFunctions.Clamp.CODEC);

      for (DensityFunctions.Mapped.Type var11 : DensityFunctions.Mapped.Type.values()) {
         register(var0, var11.getSerializedName(), var11.codec);
      }

      for (DensityFunctions.TwoArgumentSimpleFunction.Type var12 : DensityFunctions.TwoArgumentSimpleFunction.Type.values()) {
         register(var0, var12.getSerializedName(), var12.codec);
      }

      register(var0, "spline", DensityFunctions.Spline.CODEC);
      register(var0, "constant", DensityFunctions.Constant.CODEC);
      return register(var0, "y_clamped_gradient", DensityFunctions.YClampedGradient.CODEC);
   }

   private static MapCodec<? extends DensityFunction> register(
      Registry<MapCodec<? extends DensityFunction>> var0, String var1, KeyDispatchDataCodec<? extends DensityFunction> var2
   ) {
      return Registry.register(var0, var1, var2.codec());
   }

   static <A, O> KeyDispatchDataCodec<O> singleArgumentCodec(Codec<A> var0, Function<A, O> var1, Function<O, A> var2) {
      return KeyDispatchDataCodec.of(var0.fieldOf("argument").xmap(var1, var2));
   }

   static <O> KeyDispatchDataCodec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> var0, Function<O, DensityFunction> var1) {
      return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, var0, var1);
   }

   static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(
      BiFunction<DensityFunction, DensityFunction, O> var0, Function<O, DensityFunction> var1, Function<O, DensityFunction> var2
   ) {
      return KeyDispatchDataCodec.of(
         RecordCodecBuilder.mapCodec(
            var3 -> var3.group(
                     DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(var1),
                     DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(var2)
                  )
                  .apply(var3, var0)
         )
      );
   }

   static <O> KeyDispatchDataCodec<O> makeCodec(MapCodec<O> var0) {
      return KeyDispatchDataCodec.of(var0);
   }

   private DensityFunctions() {
      super();
   }

   public static DensityFunction interpolated(DensityFunction var0) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.Interpolated, var0);
   }

   public static DensityFunction flatCache(DensityFunction var0) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.FlatCache, var0);
   }

   public static DensityFunction cache2d(DensityFunction var0) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.Cache2D, var0);
   }

   public static DensityFunction cacheOnce(DensityFunction var0) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.CacheOnce, var0);
   }

   public static DensityFunction cacheAllInCell(DensityFunction var0) {
      return new DensityFunctions.Marker(DensityFunctions.Marker.Type.CacheAllInCell, var0);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> var0, @Deprecated double var1, double var3, double var5, double var7) {
      return mapFromUnitTo(new DensityFunctions.Noise(new DensityFunction.NoiseHolder(var0), var1, var3), var5, var7);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> var0, double var1, double var3, double var5) {
      return mappedNoise(var0, 1.0, var1, var3, var5);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> var0, double var1, double var3) {
      return mappedNoise(var0, 1.0, 1.0, var1, var3);
   }

   public static DensityFunction shiftedNoise2d(DensityFunction var0, DensityFunction var1, double var2, Holder<NormalNoise.NoiseParameters> var4) {
      return new DensityFunctions.ShiftedNoise(var0, zero(), var1, var2, 0.0, new DensityFunction.NoiseHolder(var4));
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> var0) {
      return noise(var0, 1.0, 1.0);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> var0, double var1, double var3) {
      return new DensityFunctions.Noise(new DensityFunction.NoiseHolder(var0), var1, var3);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> var0, double var1) {
      return noise(var0, 1.0, var1);
   }

   public static DensityFunction rangeChoice(DensityFunction var0, double var1, double var3, DensityFunction var5, DensityFunction var6) {
      return new DensityFunctions.RangeChoice(var0, var1, var3, var5, var6);
   }

   public static DensityFunction shiftA(Holder<NormalNoise.NoiseParameters> var0) {
      return new DensityFunctions.ShiftA(new DensityFunction.NoiseHolder(var0));
   }

   public static DensityFunction shiftB(Holder<NormalNoise.NoiseParameters> var0) {
      return new DensityFunctions.ShiftB(new DensityFunction.NoiseHolder(var0));
   }

   public static DensityFunction shift(Holder<NormalNoise.NoiseParameters> var0) {
      return new DensityFunctions.Shift(new DensityFunction.NoiseHolder(var0));
   }

   public static DensityFunction blendDensity(DensityFunction var0) {
      return new DensityFunctions.BlendDensity(var0);
   }

   public static DensityFunction endIslands(long var0) {
      return new DensityFunctions.EndIslandDensityFunction(var0);
   }

   public static DensityFunction weirdScaledSampler(
      DensityFunction var0, Holder<NormalNoise.NoiseParameters> var1, DensityFunctions.WeirdScaledSampler.RarityValueMapper var2
   ) {
      return new DensityFunctions.WeirdScaledSampler(var0, new DensityFunction.NoiseHolder(var1), var2);
   }

   public static DensityFunction add(DensityFunction var0, DensityFunction var1) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.ADD, var0, var1);
   }

   public static DensityFunction mul(DensityFunction var0, DensityFunction var1) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MUL, var0, var1);
   }

   public static DensityFunction min(DensityFunction var0, DensityFunction var1) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MIN, var0, var1);
   }

   public static DensityFunction max(DensityFunction var0, DensityFunction var1) {
      return DensityFunctions.TwoArgumentSimpleFunction.create(DensityFunctions.TwoArgumentSimpleFunction.Type.MAX, var0, var1);
   }

   public static DensityFunction spline(CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> var0) {
      return new DensityFunctions.Spline(var0);
   }

   public static DensityFunction zero() {
      return DensityFunctions.Constant.ZERO;
   }

   public static DensityFunction constant(double var0) {
      return new DensityFunctions.Constant(var0);
   }

   public static DensityFunction yClampedGradient(int var0, int var1, double var2, double var4) {
      return new DensityFunctions.YClampedGradient(var0, var1, var2, var4);
   }

   public static DensityFunction map(DensityFunction var0, DensityFunctions.Mapped.Type var1) {
      return DensityFunctions.Mapped.create(var1, var0);
   }

   private static DensityFunction mapFromUnitTo(DensityFunction var0, double var1, double var3) {
      double var5 = (var1 + var3) * 0.5;
      double var7 = (var3 - var1) * 0.5;
      return add(constant(var5), mul(constant(var7), var0));
   }

   public static DensityFunction blendAlpha() {
      return DensityFunctions.BlendAlpha.INSTANCE;
   }

   public static DensityFunction blendOffset() {
      return DensityFunctions.BlendOffset.INSTANCE;
   }

   public static DensityFunction lerp(DensityFunction var0, DensityFunction var1, DensityFunction var2) {
      if (var1 instanceof DensityFunctions.Constant var5) {
         return lerp(var0, var5.value, var2);
      } else {
         DensityFunction var3 = cacheOnce(var0);
         DensityFunction var4 = add(mul(var3, constant(-1.0)), constant(1.0));
         return add(mul(var1, var4), mul(var2, var3));
      }
   }

   public static DensityFunction lerp(DensityFunction var0, double var1, DensityFunction var3) {
      return add(mul(var0, add(var3, constant(-var1))), constant(var1));
   }

   static record Ap2(
      DensityFunctions.TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue
   ) implements DensityFunctions.TwoArgumentSimpleFunction {
      Ap2(DensityFunctions.TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) {
         super();
         this.type = type;
         this.argument1 = argument1;
         this.argument2 = argument2;
         this.minValue = minValue;
         this.maxValue = maxValue;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         double var2 = this.argument1.compute(var1);

         return switch (this.type) {
            case ADD -> var2 + this.argument2.compute(var1);
            case MUL -> var2 == 0.0 ? 0.0 : var2 * this.argument2.compute(var1);
            case MIN -> var2 < this.argument2.minValue() ? var2 : Math.min(var2, this.argument2.compute(var1));
            case MAX -> var2 > this.argument2.maxValue() ? var2 : Math.max(var2, this.argument2.compute(var1));
         };
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.argument1.fillArray(var1, var2);
         switch (this.type) {
            case ADD:
               double[] var10 = new double[var1.length];
               this.argument2.fillArray(var10, var2);

               for (int var11 = 0; var11 < var1.length; var11++) {
                  var1[var11] += var10[var11];
               }
               break;
            case MUL:
               for (int var9 = 0; var9 < var1.length; var9++) {
                  double var4 = var1[var9];
                  var1[var9] = var4 == 0.0 ? 0.0 : var4 * this.argument2.compute(var2.forIndex(var9));
               }
               break;
            case MIN:
               double var8 = this.argument2.minValue();

               for (int var12 = 0; var12 < var1.length; var12++) {
                  double var13 = var1[var12];
                  var1[var12] = var13 < var8 ? var13 : Math.min(var13, this.argument2.compute(var2.forIndex(var12)));
               }
               break;
            case MAX:
               double var3 = this.argument2.maxValue();

               for (int var5 = 0; var5 < var1.length; var5++) {
                  double var6 = var1[var5];
                  var1[var5] = var6 > var3 ? var6 : Math.max(var6, this.argument2.compute(var2.forIndex(var5)));
               }
         }
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(DensityFunctions.TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll(var1), this.argument2.mapAll(var1)));
      }
   }

   protected static enum BeardifierMarker implements DensityFunctions.BeardifierOrMarker {
      INSTANCE;

      private BeardifierMarker() {
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return 0.0;
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, 0.0);
      }

      @Override
      public double minValue() {
         return 0.0;
      }

      @Override
      public double maxValue() {
         return 0.0;
      }
   }

   public interface BeardifierOrMarker extends DensityFunction.SimpleFunction {
      KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(DensityFunctions.BeardifierMarker.INSTANCE));

      @Override
      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static enum BlendAlpha implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private BlendAlpha() {
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return 1.0;
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, 1.0);
      }

      @Override
      public double minValue() {
         return 1.0;
      }

      @Override
      public double maxValue() {
         return 1.0;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   static record BlendDensity(DensityFunction input) implements DensityFunctions.TransformerWithContext {
      static final KeyDispatchDataCodec<DensityFunctions.BlendDensity> CODEC = DensityFunctions.singleFunctionArgumentCodec(
         DensityFunctions.BlendDensity::new, DensityFunctions.BlendDensity::input
      );

      BlendDensity(DensityFunction input) {
         super();
         this.input = input;
      }

      @Override
      public double transform(DensityFunction.FunctionContext var1, double var2) {
         return var1.getBlender().blendDensity(var1, var2);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.BlendDensity(this.input.mapAll(var1)));
      }

      @Override
      public double minValue() {
         return -1.0 / 0.0;
      }

      @Override
      public double maxValue() {
         return 1.0 / 0.0;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static enum BlendOffset implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.of(MapCodec.unit(INSTANCE));

      private BlendOffset() {
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return 0.0;
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, 0.0);
      }

      @Override
      public double minValue() {
         return 0.0;
      }

      @Override
      public double maxValue() {
         return 0.0;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Clamp(DensityFunction input, double minValue, double maxValue) implements DensityFunctions.PureTransformer {
      private static final MapCodec<DensityFunctions.Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(DensityFunctions.Clamp::input),
                  DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(DensityFunctions.Clamp::minValue),
                  DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(DensityFunctions.Clamp::maxValue)
               )
               .apply(var0, DensityFunctions.Clamp::new)
      );
      public static final KeyDispatchDataCodec<DensityFunctions.Clamp> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      protected Clamp(DensityFunction input, double minValue, double maxValue) {
         super();
         this.input = input;
         this.minValue = minValue;
         this.maxValue = maxValue;
      }

      @Override
      public double transform(double var1) {
         return Mth.clamp(var1, this.minValue, this.maxValue);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return new DensityFunctions.Clamp(this.input.mapAll(var1), this.minValue, this.maxValue);
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   static record Constant(double value) implements DensityFunction.SimpleFunction {
      static final KeyDispatchDataCodec<DensityFunctions.Constant> CODEC = DensityFunctions.singleArgumentCodec(
         DensityFunctions.NOISE_VALUE_CODEC, DensityFunctions.Constant::new, DensityFunctions.Constant::value
      );
      static final DensityFunctions.Constant ZERO = new DensityFunctions.Constant(0.0);

      Constant(double value) {
         super();
         this.value = value;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.value;
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, this.value);
      }

      @Override
      public double minValue() {
         return this.value;
      }

      @Override
      public double maxValue() {
         return this.value;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static final class EndIslandDensityFunction implements DensityFunction.SimpleFunction {
      public static final KeyDispatchDataCodec<DensityFunctions.EndIslandDensityFunction> CODEC = KeyDispatchDataCodec.of(
         MapCodec.unit(new DensityFunctions.EndIslandDensityFunction(0L))
      );
      private static final float ISLAND_THRESHOLD = -0.9F;
      private final SimplexNoise islandNoise;

      public EndIslandDensityFunction(long var1) {
         super();
         LegacyRandomSource var3 = new LegacyRandomSource(var1);
         var3.consumeCount(17292);
         this.islandNoise = new SimplexNoise(var3);
      }

      private static float getHeightValue(SimplexNoise var0, int var1, int var2) {
         int var3 = var1 / 2;
         int var4 = var2 / 2;
         int var5 = var1 % 2;
         int var6 = var2 % 2;
         float var7 = 100.0F - Mth.sqrt((float)(var1 * var1 + var2 * var2)) * 8.0F;
         var7 = Mth.clamp(var7, -100.0F, 80.0F);

         for (int var8 = -12; var8 <= 12; var8++) {
            for (int var9 = -12; var9 <= 12; var9++) {
               long var10 = (long)(var3 + var8);
               long var12 = (long)(var4 + var9);
               if (var10 * var10 + var12 * var12 > 4096L && var0.getValue((double)var10, (double)var12) < -0.8999999761581421) {
                  float var14 = (Mth.abs((float)var10) * 3439.0F + Mth.abs((float)var12) * 147.0F) % 13.0F + 9.0F;
                  float var15 = (float)(var5 - var8 * 2);
                  float var16 = (float)(var6 - var9 * 2);
                  float var17 = 100.0F - Mth.sqrt(var15 * var15 + var16 * var16) * var14;
                  var17 = Mth.clamp(var17, -100.0F, 80.0F);
                  var7 = Math.max(var7, var17);
               }
            }
         }

         return var7;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return ((double)getHeightValue(this.islandNoise, var1.blockX() / 8, var1.blockZ() / 8) - 8.0) / 128.0;
      }

      @Override
      public double minValue() {
         return -0.84375;
      }

      @Override
      public double maxValue() {
         return 0.5625;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   @VisibleForDebug
   public static record HolderHolder(Holder<DensityFunction> function) implements DensityFunction {
      public HolderHolder(Holder<DensityFunction> function) {
         super();
         this.function = function;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.function.value().compute(var1);
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.function.value().fillArray(var1, var2);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.HolderHolder(new Holder.Direct<>(this.function.value().mapAll(var1))));
      }

      @Override
      public double minValue() {
         return this.function.isBound() ? this.function.value().minValue() : -1.0 / 0.0;
      }

      @Override
      public double maxValue() {
         return this.function.isBound() ? this.function.value().maxValue() : 1.0 / 0.0;
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         throw new UnsupportedOperationException("Calling .codec() on HolderHolder");
      }
   }

   protected static record Mapped(DensityFunctions.Mapped.Type type, DensityFunction input, double minValue, double maxValue)
      implements DensityFunctions.PureTransformer {
      protected Mapped(DensityFunctions.Mapped.Type type, DensityFunction input, double minValue, double maxValue) {
         super();
         this.type = type;
         this.input = input;
         this.minValue = minValue;
         this.maxValue = maxValue;
      }

      public static DensityFunctions.Mapped create(DensityFunctions.Mapped.Type var0, DensityFunction var1) {
         double var2 = var1.minValue();
         double var4 = transform(var0, var2);
         double var6 = transform(var0, var1.maxValue());
         return var0 != DensityFunctions.Mapped.Type.ABS && var0 != DensityFunctions.Mapped.Type.SQUARE
            ? new DensityFunctions.Mapped(var0, var1, var4, var6)
            : new DensityFunctions.Mapped(var0, var1, Math.max(0.0, var2), Math.max(var4, var6));
      }

      private static double transform(DensityFunctions.Mapped.Type var0, double var1) {
         return switch (var0) {
            case ABS -> Math.abs(var1);
            case SQUARE -> var1 * var1;
            case CUBE -> var1 * var1 * var1;
            case HALF_NEGATIVE -> var1 > 0.0 ? var1 : var1 * 0.5;
            case QUARTER_NEGATIVE -> var1 > 0.0 ? var1 : var1 * 0.25;
            case SQUEEZE -> {
               double var3 = Mth.clamp(var1, -1.0, 1.0);
               yield var3 / 2.0 - var3 * var3 * var3 / 24.0;
            }
         };
      }

      @Override
      public double transform(double var1) {
         return transform(this.type, var1);
      }

      public DensityFunctions.Mapped mapAll(DensityFunction.Visitor var1) {
         return create(this.type, this.input.mapAll(var1));
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type.codec;
      }

      static enum Type implements StringRepresentable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         SQUEEZE("squeeze");

         private final String name;
         final KeyDispatchDataCodec<DensityFunctions.Mapped> codec = DensityFunctions.singleFunctionArgumentCodec(
            var1x -> DensityFunctions.Mapped.create(this, var1x), DensityFunctions.Mapped::input
         );

         private Type(String var3) {
            this.name = var3;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }
   }

   protected static record Marker(DensityFunctions.Marker.Type type, DensityFunction wrapped) implements DensityFunctions.MarkerOrMarked {
      protected Marker(DensityFunctions.Marker.Type type, DensityFunction wrapped) {
         super();
         this.type = type;
         this.wrapped = wrapped;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.wrapped.compute(var1);
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.wrapped.fillArray(var1, var2);
      }

      @Override
      public double minValue() {
         return this.wrapped.minValue();
      }

      @Override
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
         final KeyDispatchDataCodec<DensityFunctions.MarkerOrMarked> codec = DensityFunctions.singleFunctionArgumentCodec(
            var1x -> new DensityFunctions.Marker(this, var1x), DensityFunctions.MarkerOrMarked::wrapped
         );

         private Type(String var3) {
            this.name = var3;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }
   }

   public interface MarkerOrMarked extends DensityFunction {
      DensityFunctions.Marker.Type type();

      DensityFunction wrapped();

      @Override
      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      @Override
      default DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.Marker(this.type(), this.wrapped().mapAll(var1)));
      }
   }

   static record MulOrAdd(DensityFunctions.MulOrAdd.Type specificType, DensityFunction input, double minValue, double maxValue, double argument)
      implements DensityFunctions.PureTransformer,
      DensityFunctions.TwoArgumentSimpleFunction {
      MulOrAdd(DensityFunctions.MulOrAdd.Type specificType, DensityFunction input, double minValue, double maxValue, double argument) {
         super();
         this.specificType = specificType;
         this.input = input;
         this.minValue = minValue;
         this.maxValue = maxValue;
         this.argument = argument;
      }

      @Override
      public DensityFunctions.TwoArgumentSimpleFunction.Type type() {
         return this.specificType == DensityFunctions.MulOrAdd.Type.MUL
            ? DensityFunctions.TwoArgumentSimpleFunction.Type.MUL
            : DensityFunctions.TwoArgumentSimpleFunction.Type.ADD;
      }

      @Override
      public DensityFunction argument1() {
         return DensityFunctions.constant(this.argument);
      }

      @Override
      public DensityFunction argument2() {
         return this.input;
      }

      @Override
      public double transform(double var1) {
         return switch (this.specificType) {
            case MUL -> var1 * this.argument;
            case ADD -> var1 + this.argument;
         };
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         DensityFunction var2 = this.input.mapAll(var1);
         double var3 = var2.minValue();
         double var5 = var2.maxValue();
         double var7;
         double var9;
         if (this.specificType == DensityFunctions.MulOrAdd.Type.ADD) {
            var7 = var3 + this.argument;
            var9 = var5 + this.argument;
         } else if (this.argument >= 0.0) {
            var7 = var3 * this.argument;
            var9 = var5 * this.argument;
         } else {
            var7 = var5 * this.argument;
            var9 = var3 * this.argument;
         }

         return new DensityFunctions.MulOrAdd(this.specificType, var2, var7, var9, this.argument);
      }

      static enum Type {
         MUL,
         ADD;

         private Type() {
         }
      }
   }

   protected static record Noise(DensityFunction.NoiseHolder noise, @Deprecated double xzScale, double yScale) implements DensityFunction {
      public static final MapCodec<DensityFunctions.Noise> DATA_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.Noise::noise),
                  Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.Noise::xzScale),
                  Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.Noise::yScale)
               )
               .apply(var0, DensityFunctions.Noise::new)
      );
      public static final KeyDispatchDataCodec<DensityFunctions.Noise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      protected Noise(DensityFunction.NoiseHolder noise, @Deprecated double xzScale, double yScale) {
         super();
         this.noise = noise;
         this.xzScale = xzScale;
         this.yScale = yScale;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.noise.getValue((double)var1.blockX() * this.xzScale, (double)var1.blockY() * this.yScale, (double)var1.blockZ() * this.xzScale);
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.Noise(var1.visitNoise(this.noise), this.xzScale, this.yScale));
      }

      @Override
      public double minValue() {
         return -this.maxValue();
      }

      @Override
      public double maxValue() {
         return this.noise.maxValue();
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   interface PureTransformer extends DensityFunction {
      DensityFunction input();

      @Override
      default double compute(DensityFunction.FunctionContext var1) {
         return this.transform(this.input().compute(var1));
      }

      @Override
      default void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.input().fillArray(var1, var2);

         for (int var3 = 0; var3 < var1.length; var3++) {
            var1[var3] = this.transform(var1[var3]);
         }
      }

      double transform(double var1);
   }

   static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange)
      implements DensityFunction {
      public static final MapCodec<DensityFunctions.RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.RangeChoice::input),
                  DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(DensityFunctions.RangeChoice::minInclusive),
                  DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(DensityFunctions.RangeChoice::maxExclusive),
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(DensityFunctions.RangeChoice::whenInRange),
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(DensityFunctions.RangeChoice::whenOutOfRange)
               )
               .apply(var0, DensityFunctions.RangeChoice::new)
      );
      public static final KeyDispatchDataCodec<DensityFunctions.RangeChoice> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) {
         super();
         this.input = input;
         this.minInclusive = minInclusive;
         this.maxExclusive = maxExclusive;
         this.whenInRange = whenInRange;
         this.whenOutOfRange = whenOutOfRange;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         double var2 = this.input.compute(var1);
         return var2 >= this.minInclusive && var2 < this.maxExclusive ? this.whenInRange.compute(var1) : this.whenOutOfRange.compute(var1);
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.input.fillArray(var1, var2);

         for (int var3 = 0; var3 < var1.length; var3++) {
            double var4 = var1[var3];
            if (var4 >= this.minInclusive && var4 < this.maxExclusive) {
               var1[var3] = this.whenInRange.compute(var2.forIndex(var3));
            } else {
               var1[var3] = this.whenOutOfRange.compute(var2.forIndex(var3));
            }
         }
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(
            new DensityFunctions.RangeChoice(
               this.input.mapAll(var1), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(var1), this.whenOutOfRange.mapAll(var1)
            )
         );
      }

      @Override
      public double minValue() {
         return Math.min(this.whenInRange.minValue(), this.whenOutOfRange.minValue());
      }

      @Override
      public double maxValue() {
         return Math.max(this.whenInRange.maxValue(), this.whenOutOfRange.maxValue());
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record Shift(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
      static final KeyDispatchDataCodec<DensityFunctions.Shift> CODEC = DensityFunctions.singleArgumentCodec(
         DensityFunction.NoiseHolder.CODEC, DensityFunctions.Shift::new, DensityFunctions.Shift::offsetNoise
      );

      protected Shift(DensityFunction.NoiseHolder offsetNoise) {
         super();
         this.offsetNoise = offsetNoise;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.compute((double)var1.blockX(), (double)var1.blockY(), (double)var1.blockZ());
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.Shift(var1.visitNoise(this.offsetNoise)));
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
      static final KeyDispatchDataCodec<DensityFunctions.ShiftA> CODEC = DensityFunctions.singleArgumentCodec(
         DensityFunction.NoiseHolder.CODEC, DensityFunctions.ShiftA::new, DensityFunctions.ShiftA::offsetNoise
      );

      protected ShiftA(DensityFunction.NoiseHolder offsetNoise) {
         super();
         this.offsetNoise = offsetNoise;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.compute((double)var1.blockX(), 0.0, (double)var1.blockZ());
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.ShiftA(var1.visitNoise(this.offsetNoise)));
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   protected static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements DensityFunctions.ShiftNoise {
      static final KeyDispatchDataCodec<DensityFunctions.ShiftB> CODEC = DensityFunctions.singleArgumentCodec(
         DensityFunction.NoiseHolder.CODEC, DensityFunctions.ShiftB::new, DensityFunctions.ShiftB::offsetNoise
      );

      protected ShiftB(DensityFunction.NoiseHolder offsetNoise) {
         super();
         this.offsetNoise = offsetNoise;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return this.compute((double)var1.blockZ(), (double)var1.blockX(), 0.0);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.ShiftB(var1.visitNoise(this.offsetNoise)));
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   interface ShiftNoise extends DensityFunction {
      DensityFunction.NoiseHolder offsetNoise();

      @Override
      default double minValue() {
         return -this.maxValue();
      }

      @Override
      default double maxValue() {
         return this.offsetNoise().maxValue() * 4.0;
      }

      default double compute(double var1, double var3, double var5) {
         return this.offsetNoise().getValue(var1 * 0.25, var3 * 0.25, var5 * 0.25) * 4.0;
      }

      @Override
      default void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }
   }

   protected static record ShiftedNoise(
      DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise
   ) implements DensityFunction {
      private static final MapCodec<DensityFunctions.ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(DensityFunctions.ShiftedNoise::shiftX),
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(DensityFunctions.ShiftedNoise::shiftY),
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(DensityFunctions.ShiftedNoise::shiftZ),
                  Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.ShiftedNoise::xzScale),
                  Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.ShiftedNoise::yScale),
                  DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.ShiftedNoise::noise)
               )
               .apply(var0, DensityFunctions.ShiftedNoise::new)
      );
      public static final KeyDispatchDataCodec<DensityFunctions.ShiftedNoise> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      protected ShiftedNoise(
         DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise
      ) {
         super();
         this.shiftX = shiftX;
         this.shiftY = shiftY;
         this.shiftZ = shiftZ;
         this.xzScale = xzScale;
         this.yScale = yScale;
         this.noise = noise;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         double var2 = (double)var1.blockX() * this.xzScale + this.shiftX.compute(var1);
         double var4 = (double)var1.blockY() * this.yScale + this.shiftY.compute(var1);
         double var6 = (double)var1.blockZ() * this.xzScale + this.shiftZ.compute(var1);
         return this.noise.getValue(var2, var4, var6);
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(
            new DensityFunctions.ShiftedNoise(
               this.shiftX.mapAll(var1), this.shiftY.mapAll(var1), this.shiftZ.mapAll(var1), this.xzScale, this.yScale, var1.visitNoise(this.noise)
            )
         );
      }

      @Override
      public double minValue() {
         return -this.maxValue();
      }

      @Override
      public double maxValue() {
         return this.noise.maxValue();
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }

   public static record Spline(CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> spline) implements DensityFunction {
      private static final Codec<CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate>> SPLINE_CODEC = CubicSpline.codec(
         DensityFunctions.Spline.Coordinate.CODEC
      );
      private static final MapCodec<DensityFunctions.Spline> DATA_CODEC = SPLINE_CODEC.fieldOf("spline")
         .xmap(DensityFunctions.Spline::new, DensityFunctions.Spline::spline);
      public static final KeyDispatchDataCodec<DensityFunctions.Spline> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      public Spline(CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> spline) {
         super();
         this.spline = spline;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return (double)this.spline.apply(new DensityFunctions.Spline.Point(var1));
      }

      @Override
      public double minValue() {
         return (double)this.spline.minValue();
      }

      @Override
      public double maxValue() {
         return (double)this.spline.maxValue();
      }

      @Override
      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.Spline(this.spline.mapAll(var1x -> var1x.mapAll(var1))));
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public static record Coordinate(Holder<DensityFunction> function) implements ToFloatFunction<DensityFunctions.Spline.Point> {
         public static final Codec<DensityFunctions.Spline.Coordinate> CODEC = DensityFunction.CODEC
            .xmap(DensityFunctions.Spline.Coordinate::new, DensityFunctions.Spline.Coordinate::function);

         public Coordinate(Holder<DensityFunction> function) {
            super();
            this.function = function;
         }

         @Override
         public String toString() {
            Optional var1 = this.function.unwrapKey();
            if (var1.isPresent()) {
               ResourceKey var2 = (ResourceKey)var1.get();
               if (var2 == NoiseRouterData.CONTINENTS) {
                  return "continents";
               }

               if (var2 == NoiseRouterData.EROSION) {
                  return "erosion";
               }

               if (var2 == NoiseRouterData.RIDGES) {
                  return "weirdness";
               }

               if (var2 == NoiseRouterData.RIDGES_FOLDED) {
                  return "ridges";
               }
            }

            return "Coordinate[" + this.function + "]";
         }

         public float apply(DensityFunctions.Spline.Point var1) {
            return (float)this.function.value().compute(var1.context());
         }

         @Override
         public float minValue() {
            return this.function.isBound() ? (float)this.function.value().minValue() : -1.0F / 0.0F;
         }

         @Override
         public float maxValue() {
            return this.function.isBound() ? (float)this.function.value().maxValue() : 1.0F / 0.0F;
         }

         public DensityFunctions.Spline.Coordinate mapAll(DensityFunction.Visitor var1) {
            return new DensityFunctions.Spline.Coordinate(new Holder.Direct<>(this.function.value().mapAll(var1)));
         }
      }

      public static record Point(DensityFunction.FunctionContext context) {
         public Point(DensityFunction.FunctionContext context) {
            super();
            this.context = context;
         }
      }
   }

   interface TransformerWithContext extends DensityFunction {
      DensityFunction input();

      @Override
      default double compute(DensityFunction.FunctionContext var1) {
         return this.transform(var1, this.input().compute(var1));
      }

      @Override
      default void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.input().fillArray(var1, var2);

         for (int var3 = 0; var3 < var1.length; var3++) {
            var1[var3] = this.transform(var2.forIndex(var3), var1[var3]);
         }
      }

      double transform(DensityFunction.FunctionContext var1, double var2);
   }

   interface TwoArgumentSimpleFunction extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static DensityFunctions.TwoArgumentSimpleFunction create(DensityFunctions.TwoArgumentSimpleFunction.Type var0, DensityFunction var1, DensityFunction var2) {
         double var3 = var1.minValue();
         double var5 = var2.minValue();
         double var7 = var1.maxValue();
         double var9 = var2.maxValue();
         if (var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) {
            boolean var11 = var3 >= var9;
            boolean var12 = var5 >= var7;
            if (var11 || var12) {
               LOGGER.warn("Creating a " + var0 + " function between two non-overlapping inputs: " + var1 + " and " + var2);
            }
         }
         double var16 = switch (var0) {
            case ADD -> var3 + var5;
            case MUL -> var3 > 0.0 && var5 > 0.0 ? var3 * var5 : (var7 < 0.0 && var9 < 0.0 ? var7 * var9 : Math.min(var3 * var9, var7 * var5));
            case MIN -> Math.min(var3, var5);
            case MAX -> Math.max(var3, var5);
         };

         double var13 = switch (var0) {
            case ADD -> var7 + var9;
            case MUL -> var3 > 0.0 && var5 > 0.0 ? var7 * var9 : (var7 < 0.0 && var9 < 0.0 ? var3 * var5 : Math.max(var3 * var5, var7 * var9));
            case MIN -> Math.min(var7, var9);
            case MAX -> Math.max(var7, var9);
         };
         if (var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
            if (var1 instanceof DensityFunctions.Constant var17) {
               return new DensityFunctions.MulOrAdd(
                  var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL,
                  var2,
                  var16,
                  var13,
                  var17.value
               );
            }

            if (var2 instanceof DensityFunctions.Constant var15) {
               return new DensityFunctions.MulOrAdd(
                  var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL,
                  var1,
                  var16,
                  var13,
                  var15.value
               );
            }
         }

         return new DensityFunctions.Ap2(var0, var1, var2, var16, var13);
      }

      DensityFunctions.TwoArgumentSimpleFunction.Type type();

      DensityFunction argument1();

      DensityFunction argument2();

      @Override
      default KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type().codec;
      }

      public static enum Type implements StringRepresentable {
         ADD("add"),
         MUL("mul"),
         MIN("min"),
         MAX("max");

         final KeyDispatchDataCodec<DensityFunctions.TwoArgumentSimpleFunction> codec = DensityFunctions.doubleFunctionArgumentCodec(
            (var1x, var2x) -> DensityFunctions.TwoArgumentSimpleFunction.create(this, var1x, var2x),
            DensityFunctions.TwoArgumentSimpleFunction::argument1,
            DensityFunctions.TwoArgumentSimpleFunction::argument2
         );
         private final String name;

         private Type(String var3) {
            this.name = var3;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }
   }

   protected static record WeirdScaledSampler(
      DensityFunction input, DensityFunction.NoiseHolder noise, DensityFunctions.WeirdScaledSampler.RarityValueMapper rarityValueMapper
   ) implements DensityFunctions.TransformerWithContext {
      private static final MapCodec<DensityFunctions.WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(DensityFunctions.WeirdScaledSampler::input),
                  DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.WeirdScaledSampler::noise),
                  DensityFunctions.WeirdScaledSampler.RarityValueMapper.CODEC
                     .fieldOf("rarity_value_mapper")
                     .forGetter(DensityFunctions.WeirdScaledSampler::rarityValueMapper)
               )
               .apply(var0, DensityFunctions.WeirdScaledSampler::new)
      );
      public static final KeyDispatchDataCodec<DensityFunctions.WeirdScaledSampler> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      protected WeirdScaledSampler(
         DensityFunction input, DensityFunction.NoiseHolder noise, DensityFunctions.WeirdScaledSampler.RarityValueMapper rarityValueMapper
      ) {
         super();
         this.input = input;
         this.noise = noise;
         this.rarityValueMapper = rarityValueMapper;
      }

      @Override
      public double transform(DensityFunction.FunctionContext var1, double var2) {
         double var4 = this.rarityValueMapper.mapper.get(var2);
         return var4 * Math.abs(this.noise.getValue((double)var1.blockX() / var4, (double)var1.blockY() / var4, (double)var1.blockZ() / var4));
      }

      @Override
      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new DensityFunctions.WeirdScaledSampler(this.input.mapAll(var1), var1.visitNoise(this.noise), this.rarityValueMapper));
      }

      @Override
      public double minValue() {
         return 0.0;
      }

      @Override
      public double maxValue() {
         return this.rarityValueMapper.maxRarity * this.noise.maxValue();
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      public static enum RarityValueMapper implements StringRepresentable {
         TYPE1("type_1", NoiseRouterData.QuantizedSpaghettiRarity::getSpaghettiRarity3D, 2.0),
         TYPE2("type_2", NoiseRouterData.QuantizedSpaghettiRarity::getSphaghettiRarity2D, 3.0);

         public static final Codec<DensityFunctions.WeirdScaledSampler.RarityValueMapper> CODEC = StringRepresentable.fromEnum(
            DensityFunctions.WeirdScaledSampler.RarityValueMapper::values
         );
         private final String name;
         final Double2DoubleFunction mapper;
         final double maxRarity;

         private RarityValueMapper(String var3, Double2DoubleFunction var4, double var5) {
            this.name = var3;
            this.mapper = var4;
            this.maxRarity = var5;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }
   }

   static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
      private static final MapCodec<DensityFunctions.YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(
                  Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("from_y").forGetter(DensityFunctions.YClampedGradient::fromY),
                  Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("to_y").forGetter(DensityFunctions.YClampedGradient::toY),
                  DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(DensityFunctions.YClampedGradient::fromValue),
                  DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(DensityFunctions.YClampedGradient::toValue)
               )
               .apply(var0, DensityFunctions.YClampedGradient::new)
      );
      public static final KeyDispatchDataCodec<DensityFunctions.YClampedGradient> CODEC = DensityFunctions.makeCodec(DATA_CODEC);

      YClampedGradient(int fromY, int toY, double fromValue, double toValue) {
         super();
         this.fromY = fromY;
         this.toY = toY;
         this.fromValue = fromValue;
         this.toValue = toValue;
      }

      @Override
      public double compute(DensityFunction.FunctionContext var1) {
         return Mth.clampedMap((double)var1.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
      }

      @Override
      public double minValue() {
         return Math.min(this.fromValue, this.toValue);
      }

      @Override
      public double maxValue() {
         return Math.max(this.fromValue, this.toValue);
      }

      @Override
      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }
   }
}
