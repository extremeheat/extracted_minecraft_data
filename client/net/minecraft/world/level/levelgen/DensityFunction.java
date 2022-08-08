package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public interface DensityFunction {
   Codec<DensityFunction> DIRECT_CODEC = DensityFunctions.DIRECT_CODEC;
   Codec<Holder<DensityFunction>> CODEC = RegistryFileCodec.create(Registry.DENSITY_FUNCTION_REGISTRY, DIRECT_CODEC);
   Codec<DensityFunction> HOLDER_HELPER_CODEC = CODEC.xmap(DensityFunctions.HolderHolder::new, (var0) -> {
      if (var0 instanceof DensityFunctions.HolderHolder var1) {
         return var1.function();
      } else {
         return new Holder.Direct(var0);
      }
   });

   double compute(FunctionContext var1);

   void fillArray(double[] var1, ContextProvider var2);

   DensityFunction mapAll(Visitor var1);

   double minValue();

   double maxValue();

   KeyDispatchDataCodec<? extends DensityFunction> codec();

   default DensityFunction clamp(double var1, double var3) {
      return new DensityFunctions.Clamp(this, var1, var3);
   }

   default DensityFunction abs() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.ABS);
   }

   default DensityFunction square() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.SQUARE);
   }

   default DensityFunction cube() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.CUBE);
   }

   default DensityFunction halfNegative() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.HALF_NEGATIVE);
   }

   default DensityFunction quarterNegative() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.QUARTER_NEGATIVE);
   }

   default DensityFunction squeeze() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.SQUEEZE);
   }

   public static record SinglePointContext(int a, int b, int c) implements FunctionContext {
      private final int blockX;
      private final int blockY;
      private final int blockZ;

      public SinglePointContext(int var1, int var2, int var3) {
         super();
         this.blockX = var1;
         this.blockY = var2;
         this.blockZ = var3;
      }

      public int blockX() {
         return this.blockX;
      }

      public int blockY() {
         return this.blockY;
      }

      public int blockZ() {
         return this.blockZ;
      }
   }

   public interface FunctionContext {
      int blockX();

      int blockY();

      int blockZ();

      default Blender getBlender() {
         return Blender.empty();
      }
   }

   public interface SimpleFunction extends DensityFunction {
      default void fillArray(double[] var1, ContextProvider var2) {
         var2.fillAllDirectly(var1, this);
      }

      default DensityFunction mapAll(Visitor var1) {
         return var1.apply(this);
      }
   }

   public interface Visitor {
      DensityFunction apply(DensityFunction var1);

      default NoiseHolder visitNoise(NoiseHolder var1) {
         return var1;
      }
   }

   public static record NoiseHolder(Holder<NormalNoise.NoiseParameters> b, @Nullable NormalNoise c) {
      private final Holder<NormalNoise.NoiseParameters> noiseData;
      @Nullable
      private final NormalNoise noise;
      public static final Codec<NoiseHolder> CODEC;

      public NoiseHolder(Holder<NormalNoise.NoiseParameters> var1) {
         this(var1, (NormalNoise)null);
      }

      public NoiseHolder(Holder<NormalNoise.NoiseParameters> var1, @Nullable NormalNoise var2) {
         super();
         this.noiseData = var1;
         this.noise = var2;
      }

      public double getValue(double var1, double var3, double var5) {
         return this.noise == null ? 0.0 : this.noise.getValue(var1, var3, var5);
      }

      public double maxValue() {
         return this.noise == null ? 2.0 : this.noise.maxValue();
      }

      public Holder<NormalNoise.NoiseParameters> noiseData() {
         return this.noiseData;
      }

      @Nullable
      public NormalNoise noise() {
         return this.noise;
      }

      static {
         CODEC = NormalNoise.NoiseParameters.CODEC.xmap((var0) -> {
            return new NoiseHolder(var0, (NormalNoise)null);
         }, NoiseHolder::noiseData);
      }
   }

   public interface ContextProvider {
      FunctionContext forIndex(int var1);

      void fillAllDirectly(double[] var1, DensityFunction var2);
   }
}
