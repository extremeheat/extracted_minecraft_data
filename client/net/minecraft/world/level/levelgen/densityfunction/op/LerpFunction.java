package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record LerpFunction(DensityFunction alpha, DensityFunction first, DensityFunction second) implements DensityFunction {
   public static final MapCodec<LerpFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("alpha").forGetter(LerpFunction::alpha), DensityFunction.CODEC.fieldOf("first").forGetter(LerpFunction::first), DensityFunction.CODEC.fieldOf("second").forGetter(LerpFunction::second)).apply(i, LerpFunction::new));

   public LerpFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      float alpha = this.alpha.compute(context);
      if (alpha == 0.0F) {
         return this.first.compute(context);
      } else {
         return alpha == 1.0F ? this.second.compute(context) : Mth.lerp(alpha, this.first.compute(context), this.second.compute(context));
      }
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.alpha.fillArray(output, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         DensityFunction.FunctionContext context = contextProvider.forIndex(i);
         float alpha = output[i];
         if (alpha == 0.0F) {
            output[i] = this.first.compute(context);
         } else if (alpha == 1.0F) {
            output[i] = this.second.compute(context);
         } else {
            output[i] = Mth.lerp(alpha, this.first.compute(context), this.second.compute(context));
         }
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new LerpFunction(visitor.apply(this.alpha), visitor.apply(this.first), visitor.apply(this.second));
   }

   public Interval range() {
      return Interval.lerp(this.alpha.range(), this.first.range(), this.second.range());
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.alpha.domainAxes() | this.first.domainAxes() | this.second.domainAxes();
   }

   public MapCodec<LerpFunction> codec() {
      return CODEC;
   }
}
