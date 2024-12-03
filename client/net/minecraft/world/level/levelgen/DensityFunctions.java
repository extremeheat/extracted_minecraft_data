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
   private static final Codec<DensityFunction> CODEC;
   protected static final double MAX_REASONABLE_NOISE_VALUE = 1000000.0;
   static final Codec<Double> NOISE_VALUE_CODEC;
   public static final Codec<DensityFunction> DIRECT_CODEC;

   public static MapCodec<? extends DensityFunction> bootstrap(Registry<MapCodec<? extends DensityFunction>> var0) {
      register(var0, "blend_alpha", DensityFunctions.BlendAlpha.CODEC);
      register(var0, "blend_offset", DensityFunctions.BlendOffset.CODEC);
      register(var0, "beardifier", DensityFunctions.BeardifierMarker.CODEC);
      register(var0, "old_blended_noise", BlendedNoise.CODEC);

      for(Marker.Type var4 : DensityFunctions.Marker.Type.values()) {
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

      for(Mapped.Type var11 : DensityFunctions.Mapped.Type.values()) {
         register(var0, var11.getSerializedName(), var11.codec);
      }

      for(TwoArgumentSimpleFunction.Type var12 : DensityFunctions.TwoArgumentSimpleFunction.Type.values()) {
         register(var0, var12.getSerializedName(), var12.codec);
      }

      register(var0, "spline", DensityFunctions.Spline.CODEC);
      register(var0, "constant", DensityFunctions.Constant.CODEC);
      return register(var0, "y_clamped_gradient", DensityFunctions.YClampedGradient.CODEC);
   }

   private static MapCodec<? extends DensityFunction> register(Registry<MapCodec<? extends DensityFunction>> var0, String var1, KeyDispatchDataCodec<? extends DensityFunction> var2) {
      return (MapCodec)Registry.register(var0, (String)var1, var2.codec());
   }

   static <A, O> KeyDispatchDataCodec<O> singleArgumentCodec(Codec<A> var0, Function<A, O> var1, Function<O, A> var2) {
      return KeyDispatchDataCodec.<O>of(var0.fieldOf("argument").xmap(var1, var2));
   }

   static <O> KeyDispatchDataCodec<O> singleFunctionArgumentCodec(Function<DensityFunction, O> var0, Function<O, DensityFunction> var1) {
      return singleArgumentCodec(DensityFunction.HOLDER_HELPER_CODEC, var0, var1);
   }

   static <O> KeyDispatchDataCodec<O> doubleFunctionArgumentCodec(BiFunction<DensityFunction, DensityFunction, O> var0, Function<O, DensityFunction> var1, Function<O, DensityFunction> var2) {
      return KeyDispatchDataCodec.<O>of(RecordCodecBuilder.mapCodec((var3) -> var3.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(var1), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(var2)).apply(var3, var0)));
   }

   static <O> KeyDispatchDataCodec<O> makeCodec(MapCodec<O> var0) {
      return KeyDispatchDataCodec.<O>of(var0);
   }

   private DensityFunctions() {
      super();
   }

   public static DensityFunction interpolated(DensityFunction var0) {
      return new Marker(DensityFunctions.Marker.Type.Interpolated, var0);
   }

   public static DensityFunction flatCache(DensityFunction var0) {
      return new Marker(DensityFunctions.Marker.Type.FlatCache, var0);
   }

   public static DensityFunction cache2d(DensityFunction var0) {
      return new Marker(DensityFunctions.Marker.Type.Cache2D, var0);
   }

   public static DensityFunction cacheOnce(DensityFunction var0) {
      return new Marker(DensityFunctions.Marker.Type.CacheOnce, var0);
   }

   public static DensityFunction cacheAllInCell(DensityFunction var0) {
      return new Marker(DensityFunctions.Marker.Type.CacheAllInCell, var0);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> var0, @Deprecated double var1, double var3, double var5, double var7) {
      return mapFromUnitTo(new Noise(new DensityFunction.NoiseHolder(var0), var1, var3), var5, var7);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> var0, double var1, double var3, double var5) {
      return mappedNoise(var0, 1.0, var1, var3, var5);
   }

   public static DensityFunction mappedNoise(Holder<NormalNoise.NoiseParameters> var0, double var1, double var3) {
      return mappedNoise(var0, 1.0, 1.0, var1, var3);
   }

   public static DensityFunction shiftedNoise2d(DensityFunction var0, DensityFunction var1, double var2, Holder<NormalNoise.NoiseParameters> var4) {
      return new ShiftedNoise(var0, zero(), var1, var2, 0.0, new DensityFunction.NoiseHolder(var4));
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> var0) {
      return noise(var0, 1.0, 1.0);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> var0, double var1, double var3) {
      return new Noise(new DensityFunction.NoiseHolder(var0), var1, var3);
   }

   public static DensityFunction noise(Holder<NormalNoise.NoiseParameters> var0, double var1) {
      return noise(var0, 1.0, var1);
   }

   public static DensityFunction rangeChoice(DensityFunction var0, double var1, double var3, DensityFunction var5, DensityFunction var6) {
      return new RangeChoice(var0, var1, var3, var5, var6);
   }

   public static DensityFunction shiftA(Holder<NormalNoise.NoiseParameters> var0) {
      return new ShiftA(new DensityFunction.NoiseHolder(var0));
   }

   public static DensityFunction shiftB(Holder<NormalNoise.NoiseParameters> var0) {
      return new ShiftB(new DensityFunction.NoiseHolder(var0));
   }

   public static DensityFunction shift(Holder<NormalNoise.NoiseParameters> var0) {
      return new Shift(new DensityFunction.NoiseHolder(var0));
   }

   public static DensityFunction blendDensity(DensityFunction var0) {
      return new BlendDensity(var0);
   }

   public static DensityFunction endIslands(long var0) {
      return new EndIslandDensityFunction(var0);
   }

   public static DensityFunction weirdScaledSampler(DensityFunction var0, Holder<NormalNoise.NoiseParameters> var1, WeirdScaledSampler.RarityValueMapper var2) {
      return new WeirdScaledSampler(var0, new DensityFunction.NoiseHolder(var1), var2);
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

   public static DensityFunction spline(CubicSpline<Spline.Point, Spline.Coordinate> var0) {
      return new Spline(var0);
   }

   public static DensityFunction zero() {
      return DensityFunctions.Constant.ZERO;
   }

   public static DensityFunction constant(double var0) {
      return new Constant(var0);
   }

   public static DensityFunction yClampedGradient(int var0, int var1, double var2, double var4) {
      return new YClampedGradient(var0, var1, var2, var4);
   }

   public static DensityFunction map(DensityFunction var0, Mapped.Type var1) {
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
      if (var1 instanceof Constant var5) {
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

   static {
      CODEC = BuiltInRegistries.DENSITY_FUNCTION_TYPE.byNameCodec().dispatch((var0) -> var0.codec().codec(), Function.identity());
      NOISE_VALUE_CODEC = Codec.doubleRange(-1000000.0, 1000000.0);
      DIRECT_CODEC = Codec.either(NOISE_VALUE_CODEC, CODEC).xmap((var0) -> (DensityFunction)var0.map(DensityFunctions::constant, Function.identity()), (var0) -> {
         if (var0 instanceof Constant var1) {
            return Either.left(var1.value());
         } else {
            return Either.right(var0);
         }
      });
   }

   interface TransformerWithContext extends DensityFunction {
      DensityFunction input();

      default double compute(DensityFunction.FunctionContext var1) {
         return this.transform(var1, this.input().compute(var1));
      }

      default void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.input().fillArray(var1, var2);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = this.transform(var2.forIndex(var3), var1[var3]);
         }

      }

      double transform(DensityFunction.FunctionContext var1, double var2);
   }

   interface PureTransformer extends DensityFunction {
      DensityFunction input();

      default double compute(DensityFunction.FunctionContext var1) {
         return this.transform(this.input().compute(var1));
      }

      default void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.input().fillArray(var1, var2);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            var1[var3] = this.transform(var1[var3]);
         }

      }

      double transform(double var1);
   }

   protected static enum BlendAlpha implements DensityFunction.SimpleFunction {
      INSTANCE;

      public static final KeyDispatchDataCodec<DensityFunction> CODEC = KeyDispatchDataCodec.<DensityFunction>of(MapCodec.unit(INSTANCE));

      private BlendAlpha() {
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return 1.0;
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, 1.0);
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

      public double compute(DensityFunction.FunctionContext var1) {
         return 0.0;
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, 0.0);
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

      public double compute(DensityFunction.FunctionContext var1) {
         return 0.0;
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, 0.0);
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
      public HolderHolder(Holder<DensityFunction> var1) {
         super();
         this.function = var1;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return ((DensityFunction)this.function.value()).compute(var1);
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         ((DensityFunction)this.function.value()).fillArray(var1, var2);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new HolderHolder(new Holder.Direct(((DensityFunction)this.function.value()).mapAll(var1))));
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

      default DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new Marker(this.type(), this.wrapped().mapAll(var1)));
      }
   }

   protected static record Marker(Type type, DensityFunction wrapped) implements MarkerOrMarked {
      protected Marker(Type var1, DensityFunction var2) {
         super();
         this.type = var1;
         this.wrapped = var2;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return this.wrapped.compute(var1);
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.wrapped.fillArray(var1, var2);
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
         final KeyDispatchDataCodec<MarkerOrMarked> codec = DensityFunctions.<MarkerOrMarked>singleFunctionArgumentCodec((var1x) -> new Marker(this, var1x), MarkerOrMarked::wrapped);

         private Type(final String var3) {
            this.name = var3;
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
      public static final MapCodec<Noise> DATA_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(Noise::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(Noise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(Noise::yScale)).apply(var0, Noise::new));
      public static final KeyDispatchDataCodec<Noise> CODEC;

      protected Noise(DensityFunction.NoiseHolder var1, @Deprecated double var2, double var4) {
         super();
         this.noise = var1;
         this.xzScale = var2;
         this.yScale = var4;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return this.noise.getValue((double)var1.blockX() * this.xzScale, (double)var1.blockY() * this.yScale, (double)var1.blockZ() * this.xzScale);
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new Noise(var1.visitNoise(this.noise), this.xzScale, this.yScale));
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

         for(int var8 = -12; var8 <= 12; ++var8) {
            for(int var9 = -12; var9 <= 12; ++var9) {
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

      public double compute(DensityFunction.FunctionContext var1) {
         return ((double)getHeightValue(this.islandNoise, var1.blockX() / 8, var1.blockZ() / 8) - 8.0) / 128.0;
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
      private static final MapCodec<WeirdScaledSampler> DATA_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(WeirdScaledSampler::input), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(WeirdScaledSampler::noise), DensityFunctions.WeirdScaledSampler.RarityValueMapper.CODEC.fieldOf("rarity_value_mapper").forGetter(WeirdScaledSampler::rarityValueMapper)).apply(var0, WeirdScaledSampler::new));
      public static final KeyDispatchDataCodec<WeirdScaledSampler> CODEC;

      protected WeirdScaledSampler(DensityFunction var1, DensityFunction.NoiseHolder var2, RarityValueMapper var3) {
         super();
         this.input = var1;
         this.noise = var2;
         this.rarityValueMapper = var3;
      }

      public double transform(DensityFunction.FunctionContext var1, double var2) {
         double var4 = this.rarityValueMapper.mapper.get(var2);
         return var4 * Math.abs(this.noise.getValue((double)var1.blockX() / var4, (double)var1.blockY() / var4, (double)var1.blockZ() / var4));
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new WeirdScaledSampler(this.input.mapAll(var1), var1.visitNoise(this.noise), this.rarityValueMapper));
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
         final Double2DoubleFunction mapper;
         final double maxRarity;

         private RarityValueMapper(final String var3, final Double2DoubleFunction var4, final double var5) {
            this.name = var3;
            this.mapper = var4;
            this.maxRarity = var5;
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
      private static final MapCodec<ShiftedNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(ShiftedNoise::shiftX), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(ShiftedNoise::shiftY), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(ShiftedNoise::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoise::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoise::yScale), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftedNoise::noise)).apply(var0, ShiftedNoise::new));
      public static final KeyDispatchDataCodec<ShiftedNoise> CODEC;

      protected ShiftedNoise(DensityFunction var1, DensityFunction var2, DensityFunction var3, double var4, double var6, DensityFunction.NoiseHolder var8) {
         super();
         this.shiftX = var1;
         this.shiftY = var2;
         this.shiftZ = var3;
         this.xzScale = var4;
         this.yScale = var6;
         this.noise = var8;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         double var2 = (double)var1.blockX() * this.xzScale + this.shiftX.compute(var1);
         double var4 = (double)var1.blockY() * this.yScale + this.shiftY.compute(var1);
         double var6 = (double)var1.blockZ() * this.xzScale + this.shiftZ.compute(var1);
         return this.noise.getValue(var2, var4, var6);
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new ShiftedNoise(this.shiftX.mapAll(var1), this.shiftY.mapAll(var1), this.shiftZ.mapAll(var1), this.xzScale, this.yScale, var1.visitNoise(this.noise)));
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

   static record RangeChoice(DensityFunction input, double minInclusive, double maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
      public static final MapCodec<RangeChoice> DATA_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(RangeChoice::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoice::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoice::maxExclusive), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_in_range").forGetter(RangeChoice::whenInRange), DensityFunction.HOLDER_HELPER_CODEC.fieldOf("when_out_of_range").forGetter(RangeChoice::whenOutOfRange)).apply(var0, RangeChoice::new));
      public static final KeyDispatchDataCodec<RangeChoice> CODEC;

      RangeChoice(DensityFunction var1, double var2, double var4, DensityFunction var6, DensityFunction var7) {
         super();
         this.input = var1;
         this.minInclusive = var2;
         this.maxExclusive = var4;
         this.whenInRange = var6;
         this.whenOutOfRange = var7;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         double var2 = this.input.compute(var1);
         return var2 >= this.minInclusive && var2 < this.maxExclusive ? this.whenInRange.compute(var1) : this.whenOutOfRange.compute(var1);
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.input.fillArray(var1, var2);

         for(int var3 = 0; var3 < var1.length; ++var3) {
            double var4 = var1[var3];
            if (var4 >= this.minInclusive && var4 < this.maxExclusive) {
               var1[var3] = this.whenInRange.compute(var2.forIndex(var3));
            } else {
               var1[var3] = this.whenOutOfRange.compute(var2.forIndex(var3));
            }
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new RangeChoice(this.input.mapAll(var1), this.minInclusive, this.maxExclusive, this.whenInRange.mapAll(var1), this.whenOutOfRange.mapAll(var1)));
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

      default double compute(double var1, double var3, double var5) {
         return this.offsetNoise().getValue(var1 * 0.25, var3 * 0.25, var5 * 0.25) * 4.0;
      }

      default void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }
   }

   protected static record ShiftA(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
      static final KeyDispatchDataCodec<ShiftA> CODEC;

      protected ShiftA(DensityFunction.NoiseHolder var1) {
         super();
         this.offsetNoise = var1;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return this.compute((double)var1.blockX(), 0.0, (double)var1.blockZ());
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new ShiftA(var1.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, ShiftA::new, ShiftA::offsetNoise);
      }
   }

   protected static record ShiftB(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
      static final KeyDispatchDataCodec<ShiftB> CODEC;

      protected ShiftB(DensityFunction.NoiseHolder var1) {
         super();
         this.offsetNoise = var1;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return this.compute((double)var1.blockZ(), (double)var1.blockX(), 0.0);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new ShiftB(var1.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, ShiftB::new, ShiftB::offsetNoise);
      }
   }

   protected static record Shift(DensityFunction.NoiseHolder offsetNoise) implements ShiftNoise {
      static final KeyDispatchDataCodec<Shift> CODEC;

      protected Shift(DensityFunction.NoiseHolder var1) {
         super();
         this.offsetNoise = var1;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return this.compute((double)var1.blockX(), (double)var1.blockY(), (double)var1.blockZ());
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new Shift(var1.visitNoise(this.offsetNoise)));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.singleArgumentCodec(DensityFunction.NoiseHolder.CODEC, Shift::new, Shift::offsetNoise);
      }
   }

   static record BlendDensity(DensityFunction input) implements TransformerWithContext {
      static final KeyDispatchDataCodec<BlendDensity> CODEC = DensityFunctions.<BlendDensity>singleFunctionArgumentCodec(BlendDensity::new, BlendDensity::input);

      BlendDensity(DensityFunction var1) {
         super();
         this.input = var1;
      }

      public double transform(DensityFunction.FunctionContext var1, double var2) {
         return var1.getBlender().blendDensity(var1, var2);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new BlendDensity(this.input.mapAll(var1)));
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
      private static final MapCodec<Clamp> DATA_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DensityFunction.DIRECT_CODEC.fieldOf("input").forGetter(Clamp::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(Clamp::minValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(Clamp::maxValue)).apply(var0, Clamp::new));
      public static final KeyDispatchDataCodec<Clamp> CODEC;

      protected Clamp(DensityFunction var1, double var2, double var4) {
         super();
         this.input = var1;
         this.minValue = var2;
         this.maxValue = var4;
      }

      public double transform(double var1) {
         return Mth.clamp(var1, this.minValue, this.maxValue);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return new Clamp(this.input.mapAll(var1), this.minValue, this.maxValue);
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         CODEC = DensityFunctions.<Clamp>makeCodec(DATA_CODEC);
      }
   }

   protected static record Mapped(Type type, DensityFunction input, double minValue, double maxValue) implements PureTransformer {
      protected Mapped(Type var1, DensityFunction var2, double var3, double var5) {
         super();
         this.type = var1;
         this.input = var2;
         this.minValue = var3;
         this.maxValue = var5;
      }

      public static Mapped create(Type var0, DensityFunction var1) {
         double var2 = var1.minValue();
         double var4 = transform(var0, var2);
         double var6 = transform(var0, var1.maxValue());
         return var0 != DensityFunctions.Mapped.Type.ABS && var0 != DensityFunctions.Mapped.Type.SQUARE ? new Mapped(var0, var1, var4, var6) : new Mapped(var0, var1, Math.max(0.0, var2), Math.max(var4, var6));
      }

      private static double transform(Type var0, double var1) {
         double var10000;
         switch (var0.ordinal()) {
            case 0:
               var10000 = Math.abs(var1);
               break;
            case 1:
               var10000 = var1 * var1;
               break;
            case 2:
               var10000 = var1 * var1 * var1;
               break;
            case 3:
               var10000 = var1 > 0.0 ? var1 : var1 * 0.5;
               break;
            case 4:
               var10000 = var1 > 0.0 ? var1 : var1 * 0.25;
               break;
            case 5:
               double var3 = Mth.clamp(var1, -1.0, 1.0);
               var10000 = var3 / 2.0 - var3 * var3 * var3 / 24.0;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public double transform(double var1) {
         return transform(this.type, var1);
      }

      public Mapped mapAll(DensityFunction.Visitor var1) {
         return create(this.type, this.input.mapAll(var1));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return this.type.codec;
      }

      // $FF: synthetic method
      public DensityFunction mapAll(final DensityFunction.Visitor var1) {
         return this.mapAll(var1);
      }

      static enum Type implements StringRepresentable {
         ABS("abs"),
         SQUARE("square"),
         CUBE("cube"),
         HALF_NEGATIVE("half_negative"),
         QUARTER_NEGATIVE("quarter_negative"),
         SQUEEZE("squeeze");

         private final String name;
         final KeyDispatchDataCodec<Mapped> codec = DensityFunctions.<Mapped>singleFunctionArgumentCodec((var1x) -> DensityFunctions.Mapped.create(this, var1x), Mapped::input);

         private Type(final String var3) {
            this.name = var3;
         }

         public String getSerializedName() {
            return this.name;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{ABS, SQUARE, CUBE, HALF_NEGATIVE, QUARTER_NEGATIVE, SQUEEZE};
         }
      }
   }

   interface TwoArgumentSimpleFunction extends DensityFunction {
      Logger LOGGER = LogUtils.getLogger();

      static TwoArgumentSimpleFunction create(Type var0, DensityFunction var1, DensityFunction var2) {
         double var3 = var1.minValue();
         double var5 = var2.minValue();
         double var7 = var1.maxValue();
         double var9 = var2.maxValue();
         if (var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.MIN || var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.MAX) {
            boolean var11 = var3 >= var9;
            boolean var12 = var5 >= var7;
            if (var11 || var12) {
               Logger var10000 = LOGGER;
               String var10001 = String.valueOf(var0);
               var10000.warn("Creating a " + var10001 + " function between two non-overlapping inputs: " + String.valueOf(var1) + " and " + String.valueOf(var2));
            }
         }

         double var18;
         switch (var0.ordinal()) {
            case 0 -> var18 = var3 + var5;
            case 1 -> var18 = var3 > 0.0 && var5 > 0.0 ? var3 * var5 : (var7 < 0.0 && var9 < 0.0 ? var7 * var9 : Math.min(var3 * var9, var7 * var5));
            case 2 -> var18 = Math.min(var3, var5);
            case 3 -> var18 = Math.max(var3, var5);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         double var16 = var18;
         switch (var0.ordinal()) {
            case 0 -> var18 = var7 + var9;
            case 1 -> var18 = var3 > 0.0 && var5 > 0.0 ? var7 * var9 : (var7 < 0.0 && var9 < 0.0 ? var3 * var5 : Math.max(var3 * var5, var7 * var9));
            case 2 -> var18 = Math.min(var7, var9);
            case 3 -> var18 = Math.max(var7, var9);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         double var13 = var18;
         if (var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.MUL || var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD) {
            if (var1 instanceof Constant) {
               Constant var17 = (Constant)var1;
               return new MulOrAdd(var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, var2, var16, var13, var17.value);
            }

            if (var2 instanceof Constant) {
               Constant var15 = (Constant)var2;
               return new MulOrAdd(var0 == DensityFunctions.TwoArgumentSimpleFunction.Type.ADD ? DensityFunctions.MulOrAdd.Type.ADD : DensityFunctions.MulOrAdd.Type.MUL, var1, var16, var13, var15.value);
            }
         }

         return new Ap2(var0, var1, var2, var16, var13);
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

         final KeyDispatchDataCodec<TwoArgumentSimpleFunction> codec = DensityFunctions.<TwoArgumentSimpleFunction>doubleFunctionArgumentCodec((var1x, var2x) -> DensityFunctions.TwoArgumentSimpleFunction.create(this, var1x, var2x), TwoArgumentSimpleFunction::argument1, TwoArgumentSimpleFunction::argument2);
         private final String name;

         private Type(final String var3) {
            this.name = var3;
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

   static record MulOrAdd(Type specificType, DensityFunction input, double minValue, double maxValue, double argument) implements PureTransformer, TwoArgumentSimpleFunction {
      MulOrAdd(Type var1, DensityFunction var2, double var3, double var5, double var7) {
         super();
         this.specificType = var1;
         this.input = var2;
         this.minValue = var3;
         this.maxValue = var5;
         this.argument = var7;
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

      public double transform(double var1) {
         double var10000;
         switch (this.specificType.ordinal()) {
            case 0 -> var10000 = var1 * this.argument;
            case 1 -> var10000 = var1 + this.argument;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

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

         return new MulOrAdd(this.specificType, var2, var7, var9, this.argument);
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

   static record Ap2(TwoArgumentSimpleFunction.Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue) implements TwoArgumentSimpleFunction {
      Ap2(TwoArgumentSimpleFunction.Type var1, DensityFunction var2, DensityFunction var3, double var4, double var6) {
         super();
         this.type = var1;
         this.argument1 = var2;
         this.argument2 = var3;
         this.minValue = var4;
         this.maxValue = var6;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         double var2 = this.argument1.compute(var1);
         double var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = var2 + this.argument2.compute(var1);
            case 1 -> var10000 = var2 == 0.0 ? 0.0 : var2 * this.argument2.compute(var1);
            case 2 -> var10000 = var2 < this.argument2.minValue() ? var2 : Math.min(var2, this.argument2.compute(var1));
            case 3 -> var10000 = var2 > this.argument2.maxValue() ? var2 : Math.max(var2, this.argument2.compute(var1));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         this.argument1.fillArray(var1, var2);
         switch (this.type.ordinal()) {
            case 0:
               double[] var10 = new double[var1.length];
               this.argument2.fillArray(var10, var2);

               for(int var11 = 0; var11 < var1.length; ++var11) {
                  var1[var11] += var10[var11];
               }
               break;
            case 1:
               for(int var9 = 0; var9 < var1.length; ++var9) {
                  double var4 = var1[var9];
                  var1[var9] = var4 == 0.0 ? 0.0 : var4 * this.argument2.compute(var2.forIndex(var9));
               }
               break;
            case 2:
               double var8 = this.argument2.minValue();

               for(int var12 = 0; var12 < var1.length; ++var12) {
                  double var13 = var1[var12];
                  var1[var12] = var13 < var8 ? var13 : Math.min(var13, this.argument2.compute(var2.forIndex(var12)));
               }
               break;
            case 3:
               double var3 = this.argument2.maxValue();

               for(int var5 = 0; var5 < var1.length; ++var5) {
                  double var6 = var1[var5];
                  var1[var5] = var6 > var3 ? var6 : Math.max(var6, this.argument2.compute(var2.forIndex(var5)));
               }
         }

      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(DensityFunctions.TwoArgumentSimpleFunction.create(this.type, this.argument1.mapAll(var1), this.argument2.mapAll(var1)));
      }
   }

   public static record Spline(CubicSpline<Point, Coordinate> spline) implements DensityFunction {
      private static final Codec<CubicSpline<Point, Coordinate>> SPLINE_CODEC;
      private static final MapCodec<Spline> DATA_CODEC;
      public static final KeyDispatchDataCodec<Spline> CODEC;

      public Spline(CubicSpline<Point, Coordinate> var1) {
         super();
         this.spline = var1;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return (double)this.spline.apply(new Point(var1));
      }

      public double minValue() {
         return (double)this.spline.minValue();
      }

      public double maxValue() {
         return (double)this.spline.maxValue();
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      public DensityFunction mapAll(DensityFunction.Visitor var1) {
         return var1.apply(new Spline(this.spline.mapAll((var1x) -> var1x.mapAll(var1))));
      }

      public KeyDispatchDataCodec<? extends DensityFunction> codec() {
         return CODEC;
      }

      static {
         SPLINE_CODEC = CubicSpline.codec(DensityFunctions.Spline.Coordinate.CODEC);
         DATA_CODEC = SPLINE_CODEC.fieldOf("spline").xmap(Spline::new, Spline::spline);
         CODEC = DensityFunctions.<Spline>makeCodec(DATA_CODEC);
      }

      public static record Coordinate(Holder<DensityFunction> function) implements ToFloatFunction<Point> {
         public static final Codec<Coordinate> CODEC;

         public Coordinate(Holder<DensityFunction> var1) {
            super();
            this.function = var1;
         }

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

            return "Coordinate[" + String.valueOf(this.function) + "]";
         }

         public float apply(Point var1) {
            return (float)((DensityFunction)this.function.value()).compute(var1.context());
         }

         public float minValue() {
            return this.function.isBound() ? (float)((DensityFunction)this.function.value()).minValue() : -1.0F / 0.0F;
         }

         public float maxValue() {
            return this.function.isBound() ? (float)((DensityFunction)this.function.value()).maxValue() : 1.0F / 0.0F;
         }

         public Coordinate mapAll(DensityFunction.Visitor var1) {
            return new Coordinate(new Holder.Direct(((DensityFunction)this.function.value()).mapAll(var1)));
         }

         static {
            CODEC = DensityFunction.CODEC.xmap(Coordinate::new, Coordinate::function);
         }
      }

      public static record Point(DensityFunction.FunctionContext context) {
         public Point(DensityFunction.FunctionContext var1) {
            super();
            this.context = var1;
         }
      }
   }

   static record Constant(double value) implements DensityFunction.SimpleFunction {
      final double value;
      static final KeyDispatchDataCodec<Constant> CODEC;
      static final Constant ZERO;

      Constant(double var1) {
         super();
         this.value = var1;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return this.value;
      }

      public void fillArray(double[] var1, DensityFunction.ContextProvider var2) {
         Arrays.fill(var1, this.value);
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

   static record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
      private static final MapCodec<YClampedGradient> DATA_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("from_y").forGetter(YClampedGradient::fromY), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("to_y").forGetter(YClampedGradient::toY), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(YClampedGradient::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(YClampedGradient::toValue)).apply(var0, YClampedGradient::new));
      public static final KeyDispatchDataCodec<YClampedGradient> CODEC;

      YClampedGradient(int var1, int var2, double var3, double var5) {
         super();
         this.fromY = var1;
         this.toY = var2;
         this.fromValue = var3;
         this.toValue = var5;
      }

      public double compute(DensityFunction.FunctionContext var1) {
         return Mth.clampedMap((double)var1.blockY(), (double)this.fromY, (double)this.toY, this.fromValue, this.toValue);
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
}
