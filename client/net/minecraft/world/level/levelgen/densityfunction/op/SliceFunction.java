package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record SliceFunction(Direction.Axis axis, int coordinate, DensityFunction input) implements DensityFunction {
   public static final MapCodec<SliceFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Direction.Axis.CODEC.fieldOf("axis").forGetter(SliceFunction::axis), Codec.INT.fieldOf("coordinate").forGetter(SliceFunction::coordinate), DensityFunction.CODEC.fieldOf("input").forGetter(SliceFunction::input)).apply(i, SliceFunction::new));

   public SliceFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      DensityFunction var10000 = this.input;
      DensityFunction.SinglePointContext var10001;
      switch (this.axis) {
         case X -> var10001 = new DensityFunction.SinglePointContext(this.coordinate, context.blockY(), context.blockZ());
         case Y -> var10001 = new DensityFunction.SinglePointContext(context.blockX(), this.coordinate, context.blockZ());
         case Z -> var10001 = new DensityFunction.SinglePointContext(context.blockX(), context.blockY(), this.coordinate);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000.compute(var10001);
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new SliceFunction(this.axis, this.coordinate, visitor.apply(this.input));
   }

   public Interval range() {
      return this.input.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes() & ~DensityFunction.axesFrom(this.axis);
   }

   public MapCodec<SliceFunction> codec() {
      return CODEC;
   }
}
