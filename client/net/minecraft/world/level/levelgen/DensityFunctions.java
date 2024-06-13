package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

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
            (var1, var2) -> DensityFunctions.TwoArgumentSimpleFunction.create(this, var1, var2),
            DensityFunctions.TwoArgumentSimpleFunction::argument1,
            DensityFunctions.TwoArgumentSimpleFunction::argument2
         );
         private final String name;

         private Type(final String nullxx) {
            this.name = nullxx;
         }

         @Override
         public String getSerializedName() {
            return this.name;
         }
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
