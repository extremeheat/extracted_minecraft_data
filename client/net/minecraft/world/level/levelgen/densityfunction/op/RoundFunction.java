package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;

public record RoundFunction(Type type, DensityFunction input, DensityFunction multiple) implements DensityFunction {
   public RoundFunction {
      super();
   }

   private float roundToInteger(final float input) {
      float var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = (float)Math.floor((double)input);
         case 1 -> var10000 = (float)Math.round(input);
         case 2 -> var10000 = (float)Math.ceil((double)input);
         case 3 -> var10000 = input > 0.0F ? (float)Math.floor((double)input) : (float)Math.ceil((double)input);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float compute(final DensityFunction.FunctionContext context) {
      float input = this.input.compute(context);
      float multiple = this.multiple.compute(context);
      return multiple == 0.0F ? input : this.roundToInteger(input / multiple) * multiple;
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.input.fillArray(output, contextProvider);
      float[] multiples = new float[output.length];
      this.multiple.fillArray(multiples, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         float multiple = multiples[i];
         if (multiple != 0.0F) {
            output[i] = this.roundToInteger(output[i] / multiple) * multiple;
         }
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new RoundFunction(this.type, visitor.apply(this.input), visitor.apply(this.multiple));
   }

   public Interval range() {
      Interval multipleRange = this.multiple.range();
      return Interval.mul(Interval.mapMonotonic(Interval.div(this.input.range(), multipleRange), (value) -> this.roundToInteger(value)), multipleRange);
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes() | this.multiple.domainAxes();
   }

   public MapCodec<RoundFunction> codec() {
      return this.type.codec;
   }

   public static enum Type {
      FLOOR("floor"),
      ROUND("round"),
      CEIL("ceil"),
      TRUNCATE("truncate");

      public final String id;
      public final MapCodec<RoundFunction> codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(RoundFunction::input), DensityFunction.CODEC.optionalFieldOf("multiple", DensityFunctions.constant(1.0F)).forGetter(RoundFunction::multiple)).apply(i, (input, multiple) -> new RoundFunction(this, input, multiple)));

      private Type(final String id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{FLOOR, ROUND, CEIL, TRUNCATE};
      }
   }
}
