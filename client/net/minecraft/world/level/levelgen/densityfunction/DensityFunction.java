package net.minecraft.world.level.levelgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jspecify.annotations.Nullable;

public interface DensityFunction {
   Codec<Holder<DensityFunction>> REFERENCE_CODEC = RegistryCodecs.holder(Registries.DENSITY_FUNCTION);
   Codec<DensityFunction> CODEC = RegistryCodecs.holder(Registries.DENSITY_FUNCTION, DensityFunctions.DIRECT_CODEC).xmap((holder) -> {
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
      //0->net/minecraft/world/level/levelgen/densityfunction/DensityFunctions$HolderHolder
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
   int AXIS_X = 1;
   int AXIS_Y = 2;
   int AXIS_Z = 4;
   @DensityFunction.Axes int NO_AXES = 0;
   @DensityFunction.Axes int ALL_AXES = 7;

   static @DensityFunction.Axes int axesFrom(final Direction.Axis axis) {
      byte var10000;
      switch (axis) {
         case X -> var10000 = 1;
         case Y -> var10000 = 2;
         case Z -> var10000 = 4;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   float compute(final FunctionContext context);

   void fillArray(final float[] output, final ContextProvider contextProvider);

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

   Interval range();

   @DensityFunction.Axes int domainAxes();

   MapCodec<? extends DensityFunction> codec();

   default DensityFunction clamp(final float min, final float max) {
      return DensityFunctions.clamp(this, min, max);
   }

   default DensityFunction abs() {
      return DensityFunctions.abs(this);
   }

   default DensityFunction square() {
      return DensityFunctions.square(this);
   }

   default DensityFunction cube() {
      return DensityFunctions.cube(this);
   }

   default DensityFunction sqrt() {
      return DensityFunctions.sqrt(this);
   }

   default DensityFunction halfNegative() {
      return DensityFunctions.halfNegative(this);
   }

   default DensityFunction quarterNegative() {
      return DensityFunctions.quarterNegative(this);
   }

   default DensityFunction reciprocal() {
      return DensityFunctions.reciprocal(this);
   }

   default DensityFunction negate() {
      return DensityFunctions.negate(this);
   }

   default DensityFunction squeeze() {
      return DensityFunctions.squeeze(this);
   }

   default DensityFunction log() {
      return DensityFunctions.log(this);
   }

   default DensityFunction sign() {
      return DensityFunctions.sign(this);
   }

   default DensityFunction add(final DensityFunction right) {
      return DensityFunctions.add(this, right);
   }

   default DensityFunction add(final float right) {
      return DensityFunctions.add(this, DensityFunctions.constant(right));
   }

   default DensityFunction sub(final DensityFunction right) {
      return DensityFunctions.sub(this, right);
   }

   default DensityFunction sub(final float right) {
      return DensityFunctions.sub(this, DensityFunctions.constant(right));
   }

   default DensityFunction mul(final DensityFunction right) {
      return DensityFunctions.mul(this, right);
   }

   default DensityFunction mul(final float right) {
      return DensityFunctions.mul(this, DensityFunctions.constant(right));
   }

   default DensityFunction div(final DensityFunction right) {
      return DensityFunctions.div(this, right);
   }

   default DensityFunction div(final float right) {
      return DensityFunctions.div(this, DensityFunctions.constant(right));
   }

   default DensityFunction pow(final DensityFunction exponent) {
      return DensityFunctions.pow(this, exponent);
   }

   default DensityFunction pow(final float exponent) {
      if (exponent == 0.5F) {
         return this.sqrt();
      } else if (exponent == 2.0F) {
         return this.square();
      } else {
         return exponent == 3.0F ? this.cube() : DensityFunctions.pow(this, DensityFunctions.constant(exponent));
      }
   }

   public static record NoiseHolder(Holder<NormalNoise> noiseData, @Nullable Noise noise) {
      public static final Codec<NoiseHolder> CODEC;

      public NoiseHolder(final Holder<NormalNoise> noiseData) {
         this(noiseData, (Noise)null);
      }

      public NoiseHolder {
         super();
      }

      public float getValue(final double x, final double y, final double z) {
         return this.noise == null ? 0.0F : this.noise.get(x, y, z);
      }

      public Interval range() {
         return this.noiseData.isBound() ? ((NormalNoise)this.noiseData.value()).range() : Interval.INFINITE;
      }

      public boolean equals(final Object obj) {
         boolean var10000;
         if (obj instanceof NoiseHolder holder) {
            if (this.noiseData.equals(holder.noiseData)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         return this.noiseData.hashCode();
      }

      static {
         CODEC = NormalNoise.CODEC.xmap((data) -> new NoiseHolder(data, (Noise)null), NoiseHolder::noiseData);
      }
   }

   public interface Visitor {
      DensityFunction apply(DensityFunction input);

      default NoiseHolder visitNoise(final NoiseHolder noise) {
         return noise;
      }
   }

   public static record SinglePointContext(int blockX, int blockY, int blockZ) implements FunctionContext {
      public SinglePointContext {
         super();
      }
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Axes {
   }

   public interface ContextProvider {
      FunctionContext forIndex(int index);

      void fillAllDirectly(float[] output, DensityFunction function);
   }

   public interface FunctionContext {
      int blockX();

      int blockY();

      int blockZ();
   }
}
