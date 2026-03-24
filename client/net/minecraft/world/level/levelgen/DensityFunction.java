package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;

public interface DensityFunction {
   Codec<DensityFunction> DIRECT_CODEC = DensityFunctions.DIRECT_CODEC;
   Codec<Holder<DensityFunction>> CODEC = RegistryFileCodec.<Holder<DensityFunction>>create(Registries.DENSITY_FUNCTION, DIRECT_CODEC);
   Codec<DensityFunction> HOLDER_HELPER_CODEC = CODEC.xmap(DensityFunctions.HolderHolder::new, (value) -> {
      if (value instanceof DensityFunctions.HolderHolder holder) {
         return holder.function();
      } else {
         return Holder.direct(value);
      }
   });

   double compute(final FunctionContext context);

   void fillArray(final double[] output, final ContextProvider contextProvider);

   DensityFunction mapAll(final Visitor visitor);

   double minValue();

   double maxValue();

   KeyDispatchDataCodec<? extends DensityFunction> codec();

   default DensityFunction clamp(final double min, final double max) {
      return new DensityFunctions.Clamp(this, min, max);
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

   default DensityFunction invert() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.INVERT);
   }

   default DensityFunction squeeze() {
      return DensityFunctions.map(this, DensityFunctions.Mapped.Type.SQUEEZE);
   }

   public static record NoiseHolder(Holder<NormalNoise.NoiseParameters> noiseData, @Nullable NormalNoise noise) {
      public static final Codec<NoiseHolder> CODEC;

      public NoiseHolder(final Holder<NormalNoise.NoiseParameters> noiseData) {
         this(noiseData, (NormalNoise)null);
      }

      public NoiseHolder {
         super();
      }

      public double getValue(final double x, final double y, final double z) {
         return this.noise == null ? 0.0 : this.noise.getValue(x, y, z);
      }

      public double maxValue() {
         return this.noise == null ? 2.0 : this.noise.maxValue();
      }

      static {
         CODEC = NormalNoise.NoiseParameters.CODEC.xmap((data) -> new NoiseHolder(data, (NormalNoise)null), NoiseHolder::noiseData);
      }
   }

   public interface Visitor {
      DensityFunction apply(DensityFunction input);

      default NoiseHolder visitNoise(final NoiseHolder noise) {
         return noise;
      }
   }

   public interface SimpleFunction extends DensityFunction {
      default void fillArray(final double[] output, final ContextProvider contextProvider) {
         contextProvider.fillAllDirectly(output, this);
      }

      default DensityFunction mapAll(final Visitor visitor) {
         return visitor.apply(this);
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

   public static record SinglePointContext(int blockX, int blockY, int blockZ) implements FunctionContext {
      public SinglePointContext {
         super();
      }
   }

   public interface ContextProvider {
      FunctionContext forIndex(int index);

      void fillAllDirectly(double[] output, DensityFunction function);
   }
}
