package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

public record ConstantFunction(float value) implements DensityFunction {
   public static final MapCodec<ConstantFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunctions.NOISE_VALUE_CODEC.fieldOf("value").forGetter(ConstantFunction::value)).apply(i, ConstantFunction::new));

   public ConstantFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.value;
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      Arrays.fill(output, this.value);
   }

   public Interval range() {
      return Interval.ofExact(this.value);
   }

   public @DensityFunction.Axes int domainAxes() {
      return 0;
   }

   public MapCodec<ConstantFunction> codec() {
      return CODEC;
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return this;
   }
}
