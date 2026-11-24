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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Interval;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.densityfunction.op.upscale.Upscaler;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

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
      int index$1 = 0;
      Holder var10000;
      //$FF: index$1->value
      //0->net/minecraft/world/level/levelgen/densityfunction/DensityFunctions$HolderHolder
      switch (value.typeSwitch<invokedynamic>(value, index$1)) {
         case 0:
            DensityFunctions.HolderHolder holder = (DensityFunctions.HolderHolder)value;
            var10000 = holder.function();
            break;
         default:
            var10000 = Holder.direct(value);
      }

      return var10000;
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

   DensitySampler compileSampler(CompileContext context);

   DensityFunction rewriteChildren(DfRewriteRule rule);

   Interval range();

   @DensityFunction.Axes int domainAxes();

   MapCodec<? extends DensityFunction> codec();

   static String idShortString(final Holder<?> inner) {
      return ((ResourceKey)inner.unwrapKey().orElseThrow()).identifier().toShortString();
   }

   @VisibleForDebug
   default String getDebugName() {
      Identifier key = BuiltInRegistries.DENSITY_FUNCTION_TYPE.getKey(this.codec());
      return ((Identifier)Objects.requireNonNull(key)).toShortString();
   }

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

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Axes {
   }

   public interface CompileContext {
      Noise createNoiseSampler(Holder<NormalNoise> parameters);

      RandomSource createRandom(Identifier seed);

      /** @deprecated */
      @Deprecated
      RandomSource createEndIslandRandom();

      Upscaler.Cache upscalerCache();

      VerticalAnchor.Context verticalAnchors();
   }
}
