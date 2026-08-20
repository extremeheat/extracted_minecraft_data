package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record NoiseFunction(DensityFunction.NoiseHolder noise, double xzScale, double yScale) implements DensityFunction {
   public static final MapCodec<NoiseFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(NoiseFunction::noise), Codec.DOUBLE.fieldOf("xz_scale").forGetter(NoiseFunction::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(NoiseFunction::yScale)).apply(i, NoiseFunction::new));

   public NoiseFunction(DensityFunction.NoiseHolder noise, @Deprecated double xzScale, double yScale) {
      super();
      this.noise = noise;
      this.xzScale = xzScale;
      this.yScale = yScale;
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.noise.getValue((double)context.blockX() * this.xzScale, (double)context.blockY() * this.yScale, (double)context.blockZ() * this.xzScale);
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new NoiseFunction(visitor.visitNoise(this.noise), this.xzScale, this.yScale);
   }

   public Interval range() {
      return this.noise.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      int axes = 7;
      if (this.yScale == 0.0) {
         axes &= -3;
      }

      if (this.xzScale == 0.0) {
         axes &= -6;
      }

      return axes;
   }

   public MapCodec<NoiseFunction> codec() {
      return CODEC;
   }

   /** @deprecated */
   @Deprecated
   public double xzScale() {
      return this.xzScale;
   }
}
