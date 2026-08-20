package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

public record RangeChoiceFunction(DensityFunction input, float minInclusive, float maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
   public static final MapCodec<RangeChoiceFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(RangeChoiceFunction::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoiceFunction::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoiceFunction::maxExclusive), DensityFunction.CODEC.fieldOf("when_in_range").forGetter(RangeChoiceFunction::whenInRange), DensityFunction.CODEC.fieldOf("when_out_of_range").forGetter(RangeChoiceFunction::whenOutOfRange)).apply(i, RangeChoiceFunction::new));

   public RangeChoiceFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      float inputValue = this.input.compute(context);
      return inputValue >= this.minInclusive && inputValue < this.maxExclusive ? this.whenInRange.compute(context) : this.whenOutOfRange.compute(context);
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.input.fillArray(output, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         float input = output[i];
         if (input >= this.minInclusive && input < this.maxExclusive) {
            output[i] = this.whenInRange.compute(contextProvider.forIndex(i));
         } else {
            output[i] = this.whenOutOfRange.compute(contextProvider.forIndex(i));
         }
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new RangeChoiceFunction(visitor.apply(this.input), this.minInclusive, this.maxExclusive, visitor.apply(this.whenInRange), visitor.apply(this.whenOutOfRange));
   }

   public Interval range() {
      return Interval.encapsulating(this.whenInRange.range(), this.whenOutOfRange.range());
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes() | this.whenInRange.domainAxes() | this.whenOutOfRange.domainAxes();
   }

   public MapCodec<RangeChoiceFunction> codec() {
      return CODEC;
   }
}
