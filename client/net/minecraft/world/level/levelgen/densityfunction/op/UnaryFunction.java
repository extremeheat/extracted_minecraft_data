package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record UnaryFunction(Type type, DensityFunction input) implements DensityFunction {
   public UnaryFunction {
      super();
   }

   private static float transform(final Type type, final float input) {
      float var10000;
      switch (type.ordinal()) {
         case 0:
            var10000 = Math.abs(input);
            break;
         case 1:
            var10000 = input * input;
            break;
         case 2:
            var10000 = input * input * input;
            break;
         case 3:
            var10000 = Mth.sqrt(input);
            break;
         case 4:
            var10000 = input > 0.0F ? input : input * 0.5F;
            break;
         case 5:
            var10000 = input > 0.0F ? input : input * 0.25F;
            break;
         case 6:
            var10000 = 1.0F / input;
            break;
         case 7:
            var10000 = -input;
            break;
         case 8:
            float c = Mth.clamp(input, -1.0F, 1.0F);
            var10000 = c / 2.0F - c * c * c / 24.0F;
            break;
         case 9:
            var10000 = (float)Math.log((double)input);
            break;
         case 10:
            var10000 = Math.signum(input);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return transform(this.type, this.input.compute(context));
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.input().fillArray(output, contextProvider);

      for(int i = 0; i < output.length; ++i) {
         output[i] = transform(this.type, output[i]);
      }

   }

   public UnaryFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new UnaryFunction(this.type, visitor.apply(this.input));
   }

   public MapCodec<UnaryFunction> codec() {
      return this.type.codec;
   }

   public Interval range() {
      Interval input = this.input.range();
      Interval var10000;
      switch (this.type.ordinal()) {
         case 0:
            var10000 = Interval.abs(input);
            break;
         case 1:
            var10000 = Interval.square(input);
            break;
         case 2:
         case 4:
         case 5:
         case 8:
            var10000 = Interval.mapMonotonic(input, (value) -> transform(this.type, value));
            break;
         case 3:
            var10000 = Interval.pow(input, Interval.ofExact(0.5F));
            break;
         case 6:
            var10000 = Interval.reciprocal(input);
            break;
         case 7:
            var10000 = Interval.sub(Interval.ofExact(0.0F), input);
            break;
         case 9:
            var10000 = Interval.log(input);
            break;
         case 10:
            var10000 = Interval.sign(input);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }

   public static enum Type {
      ABS("abs"),
      SQUARE("square"),
      CUBE("cube"),
      SQRT("sqrt"),
      HALF_NEGATIVE("half_negative"),
      QUARTER_NEGATIVE("quarter_negative"),
      RECIPROCAL("reciprocal"),
      NEGATE("negate"),
      SQUEEZE("squeeze"),
      LOG("log"),
      SIGN("sign");

      public final String id;
      public final MapCodec<UnaryFunction> codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(UnaryFunction::input)).apply(i, (input) -> new UnaryFunction(this, input)));

      private Type(final String id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{ABS, SQUARE, CUBE, SQRT, HALF_NEGATIVE, QUARTER_NEGATIVE, RECIPROCAL, NEGATE, SQUEEZE, LOG, SIGN};
      }
   }
}
