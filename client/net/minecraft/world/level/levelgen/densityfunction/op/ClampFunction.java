package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

public record ClampFunction(DensityFunction input, float min, float max) implements DensityFunction {
   public static final MapCodec<ClampFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(ClampFunction::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(ClampFunction::min), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(ClampFunction::max)).apply(i, ClampFunction::new)).validate(ClampFunction::validate);

   public ClampFunction {
      super();
   }

   private static DataResult<ClampFunction> validate(final ClampFunction clamp) {
      return clamp.max < clamp.min ? DataResult.error(() -> "min (" + clamp.min + ") must be less than or equal to max (" + clamp.max + ")") : DataResult.success(clamp);
   }

   private float transform(final float input) {
      return Mth.clamp(input, this.min, this.max);
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.transform(this.input.compute(context));
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.input().fillArray(output, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         output[i] = this.transform(output[i]);
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new ClampFunction(visitor.apply(this.input), this.min, this.max);
   }

   public MapCodec<ClampFunction> codec() {
      return CODEC;
   }

   public Interval range() {
      return Interval.clamp(this.input.range(), this.min, this.max);
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }
}
