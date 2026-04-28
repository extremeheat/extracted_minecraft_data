package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;

public interface DensityFunction {
   Codec<DensityFunction> CODEC = RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunctions.DIRECT_CODEC).xmap((holder) -> {
      Objects.requireNonNull(holder);
      int index$1 = 0;
      Object var10000;
      //$FF: index$1->value
      //0->net/minecraft/core/Holder$Direct
      //1->net/minecraft/core/Holder$Reference
      switch (holder.typeSwitch<invokedynamic>(holder, index$1)) {
         case 0:
            Holder.Direct<DensityFunction> direct = (Holder.Direct)holder;
            var10000 = direct.value();
            break;
         case 1:
            Holder.Reference<DensityFunction> reference = (Holder.Reference)holder;
            var10000 = new DensityFunctions.HolderHolder(reference);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (DensityFunction)var10000;
   }, (value) -> {
      Objects.requireNonNull(value);
      int index$2 = 0;
      Holder var8;
      //$FF: index$2->value
      //0->net/minecraft/world/level/levelgen/DensityFunctions$HolderHolder
      switch (value.typeSwitch<invokedynamic>(value, index$2)) {
         case 0:
            DensityFunctions.HolderHolder $b$0 = (DensityFunctions.HolderHolder)value;
            DensityFunctions.HolderHolder var10000 = $b$0;

            try {
               var7 = var10000.function();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            Holder patt3$temp = var7;
            var8 = patt3$temp;
            break;
         default:
            var8 = Holder.direct(value);
      }

      return var8;
   });

   double compute(final FunctionContext context);

   void fillArray(final double[] output, final ContextProvider contextProvider);

   DensityFunction mapChildren(final Visitor visitor);

   default DensityFunction mapAll(final Visitor visitor) {
      class RecursiveVisitor implements Visitor {
         RecursiveVisitor() {
            Objects.requireNonNull(DensityFunction.this);
            super();
         }

         public DensityFunction apply(final DensityFunction input) {
            return visitor.apply(input.mapChildren(this));
         }

         public NoiseHolder visitNoise(final NoiseHolder noise) {
            return visitor.visitNoise(noise);
         }
      }

      return (new RecursiveVisitor()).apply(this);
   }

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

      default DensityFunction mapChildren(final Visitor visitor) {
         return this;
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

   public interface FunctionContext {
      int blockX();

      int blockY();

      int blockZ();
   }
}
