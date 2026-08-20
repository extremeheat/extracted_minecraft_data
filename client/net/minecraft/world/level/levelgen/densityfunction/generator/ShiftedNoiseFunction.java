package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record ShiftedNoiseFunction(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise) implements DensityFunction {
   public static final MapCodec<ShiftedNoiseFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("shift_x").forGetter(ShiftedNoiseFunction::shiftX), DensityFunction.CODEC.fieldOf("shift_y").forGetter(ShiftedNoiseFunction::shiftY), DensityFunction.CODEC.fieldOf("shift_z").forGetter(ShiftedNoiseFunction::shiftZ), Codec.DOUBLE.fieldOf("xz_scale").forGetter(ShiftedNoiseFunction::xzScale), Codec.DOUBLE.fieldOf("y_scale").forGetter(ShiftedNoiseFunction::yScale), DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(ShiftedNoiseFunction::noise)).apply(i, ShiftedNoiseFunction::new));

   public ShiftedNoiseFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      double x = (double)context.blockX() * this.xzScale + (double)this.shiftX.compute(context);
      double y = (double)context.blockY() * this.yScale + (double)this.shiftY.compute(context);
      double z = (double)context.blockZ() * this.xzScale + (double)this.shiftZ.compute(context);
      return this.noise.getValue(x, y, z);
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new ShiftedNoiseFunction(visitor.apply(this.shiftX), visitor.apply(this.shiftY), visitor.apply(this.shiftZ), this.xzScale, this.yScale, visitor.visitNoise(this.noise));
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

      return axes | this.shiftX.domainAxes() | this.shiftY.domainAxes() | this.shiftZ.domainAxes();
   }

   public MapCodec<ShiftedNoiseFunction> codec() {
      return CODEC;
   }
}
