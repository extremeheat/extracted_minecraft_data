package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;
import org.slf4j.Logger;

public record BinaryFunction(Type type, DensityFunction left, DensityFunction right, float rightMinValue, float rightMaxValue) implements DensityFunction {
   private static final Logger LOGGER = LogUtils.getLogger();

   public BinaryFunction(final Type type, final DensityFunction left, final DensityFunction right) {
      Interval rightRange = right.range();
      this(type, left, right, rightRange.min(), rightRange.max());
      if ((type == BinaryFunction.Type.MIN || type == BinaryFunction.Type.MAX) && !left.range().intersects(rightRange)) {
         LOGGER.warn("Creating a {} function between two non-overlapping inputs: {} and {}", new Object[]{type, left, right});
      }

   }

   public BinaryFunction {
      super();
   }

   public DensityFunction trySimplify() {
      if (this.type == BinaryFunction.Type.MUL || this.type == BinaryFunction.Type.ADD) {
         DensityFunction var3 = this.left;
         if (var3 instanceof ConstantFunction) {
            ConstantFunction var1 = (ConstantFunction)var3;
            ConstantFunction var10000 = var1;

            try {
               var11 = var10000.value();
            } catch (Throwable var7) {
               throw new MatchException(var7.toString(), var7);
            }

            float leftValue = var11;
            if (true) {
               return new MulOrAdd(this.type == BinaryFunction.Type.ADD ? BinaryFunction.MulOrAdd.Type.ADD : BinaryFunction.MulOrAdd.Type.MUL, this.right, leftValue);
            }
         }

         var3 = this.right;
         if (var3 instanceof ConstantFunction) {
            ConstantFunction var8 = (ConstantFunction)var3;
            ConstantFunction var12 = var8;

            try {
               var13 = var12.value();
            } catch (Throwable var6) {
               throw new MatchException(var6.toString(), var6);
            }

            float leftValue = var13;
            if (true) {
               return new MulOrAdd(this.type == BinaryFunction.Type.ADD ? BinaryFunction.MulOrAdd.Type.ADD : BinaryFunction.MulOrAdd.Type.MUL, this.left, leftValue);
            }
         }
      }

      return this;
   }

   public float compute(final DensityFunction.FunctionContext context) {
      float left = this.left.compute(context);
      float var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = left + this.right.compute(context);
         case 1 -> var10000 = left - this.right.compute(context);
         case 2 -> var10000 = left == 0.0F ? 0.0F : left * this.right.compute(context);
         case 3 -> var10000 = left == 0.0F ? 0.0F : left / this.right.compute(context);
         case 4 -> var10000 = left < this.rightMinValue ? left : Math.min(left, this.right.compute(context));
         case 5 -> var10000 = left > this.rightMaxValue ? left : Math.max(left, this.right.compute(context));
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      this.left.fillArray(output, contextProvider);
      switch (this.type.ordinal()) {
         case 0:
            float[] right = new float[output.length];
            this.right.fillArray(right, contextProvider);

            for(int i = 0; i < output.length; ++i) {
               output[i] += right[i];
            }
            break;
         case 1:
            float[] right = new float[output.length];
            this.right.fillArray(right, contextProvider);

            for(int i = 0; i < output.length; ++i) {
               output[i] -= right[i];
            }
            break;
         case 2:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left == 0.0F ? 0.0F : left * this.right.compute(contextProvider.forIndex(i));
            }
            break;
         case 3:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left == 0.0F ? 0.0F : left / this.right.compute(contextProvider.forIndex(i));
            }
            break;
         case 4:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left < this.rightMinValue ? left : Math.min(left, this.right.compute(contextProvider.forIndex(i)));
            }
            break;
         case 5:
            for(int i = 0; i < output.length; ++i) {
               float left = output[i];
               output[i] = left > this.rightMaxValue ? left : Math.max(left, this.right.compute(contextProvider.forIndex(i)));
            }
      }

   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new BinaryFunction(this.type, visitor.apply(this.left), visitor.apply(this.right));
   }

   public Interval range() {
      Interval left = this.left.range();
      Interval right = this.right.range();
      Interval var10000;
      switch (this.type.ordinal()) {
         case 0 -> var10000 = Interval.add(left, right);
         case 1 -> var10000 = Interval.sub(left, right);
         case 2 -> var10000 = Interval.mul(left, right);
         case 3 -> var10000 = Interval.div(left, right);
         case 4 -> var10000 = Interval.min(left, right);
         case 5 -> var10000 = Interval.max(left, right);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean equals(final Object obj) {
      boolean var10000;
      if (obj instanceof BinaryFunction binary) {
         if (this.type == binary.type && this.left.equals(binary.left) && this.right.equals(binary.right)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.type, this.left, this.right});
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.left.domainAxes() | this.right.domainAxes();
   }

   public MapCodec<BinaryFunction> codec() {
      return this.type().codec;
   }

   public static enum Type {
      ADD("add"),
      SUB("sub"),
      MUL("mul"),
      DIV("div"),
      MIN("min"),
      MAX("max");

      public final String id;
      public final MapCodec<BinaryFunction> codec = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("left").forGetter(BinaryFunction::left), DensityFunction.CODEC.fieldOf("right").forGetter(BinaryFunction::right)).apply(i, (left, right) -> new BinaryFunction(this, left, right)));

      private Type(final String id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{ADD, SUB, MUL, DIV, MIN, MAX};
      }
   }

   public static record MulOrAdd(Type specificType, DensityFunction right, float leftValue) implements DensityFunction {
      public MulOrAdd {
         super();
      }

      public float compute(final DensityFunction.FunctionContext context) {
         float input = this.right.compute(context);
         float var10000;
         switch (this.specificType.ordinal()) {
            case 0 -> var10000 = input * this.leftValue;
            case 1 -> var10000 = input + this.leftValue;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
         this.right.fillArray(output, contextProvider);
         switch (this.specificType.ordinal()) {
            case 0:
               for(int i = 0; i < output.length; ++i) {
                  output[i] *= this.leftValue;
               }
               break;
            case 1:
               for(int i = 0; i < output.length; ++i) {
                  output[i] += this.leftValue;
               }
         }

      }

      public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
         return new MulOrAdd(this.specificType, visitor.apply(this.right), this.leftValue);
      }

      public Interval range() {
         Interval var10000;
         switch (this.specificType.ordinal()) {
            case 0 -> var10000 = Interval.mul(this.right.range(), Interval.ofExact(this.leftValue));
            case 1 -> var10000 = Interval.add(this.right.range(), Interval.ofExact(this.leftValue));
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public @DensityFunction.Axes int domainAxes() {
         return this.right.domainAxes();
      }

      public MapCodec<? extends DensityFunction> codec() {
         throw new UnsupportedOperationException();
      }

      public static enum Type {
         MUL,
         ADD;

         private Type() {
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{MUL, ADD};
         }
      }
   }
}
