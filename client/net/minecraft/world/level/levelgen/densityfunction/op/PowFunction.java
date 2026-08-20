package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record PowFunction(DensityFunction base, DensityFunction exponent) implements DensityFunction {
   public static final MapCodec<PowFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("base").forGetter(PowFunction::base), DensityFunction.CODEC.fieldOf("exponent").forGetter(PowFunction::exponent)).apply(i, PowFunction::new));

   public PowFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return (float)Math.pow((double)this.base.compute(context), (double)this.exponent.compute(context));
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.base.fillArray(output, contextProvider);
      float[] exponentOutput = new float[output.length];
      this.exponent.fillArray(exponentOutput, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         output[i] = (float)Math.pow((double)output[i], (double)exponentOutput[i]);
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new PowFunction(visitor.apply(this.base), visitor.apply(this.exponent));
   }

   public Interval range() {
      return Interval.pow(this.base.range(), this.exponent.range());
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.base.domainAxes() | this.exponent.domainAxes();
   }

   public MapCodec<PowFunction> codec() {
      return CODEC;
   }
}
