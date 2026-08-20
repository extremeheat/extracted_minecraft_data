package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record FindTopSurfaceFunction(DensityFunction density, DensityFunction upperBound, int lowerBound, int cellHeight) implements DensityFunction {
   public static final MapCodec<FindTopSurfaceFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("density").forGetter(FindTopSurfaceFunction::density), DensityFunction.CODEC.fieldOf("upper_bound").forGetter(FindTopSurfaceFunction::upperBound), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("lower_bound").forGetter(FindTopSurfaceFunction::lowerBound), ExtraCodecs.POSITIVE_INT.fieldOf("cell_height").forGetter(FindTopSurfaceFunction::cellHeight)).apply(i, FindTopSurfaceFunction::new));

   public FindTopSurfaceFunction {
      super();
   }

   public float compute(final DensityFunction.FunctionContext context) {
      int topY = Mth.floor(this.upperBound.compute(context) / (float)this.cellHeight) * this.cellHeight;
      if (topY <= this.lowerBound) {
         return (float)this.lowerBound;
      } else {
         for(int blockY = topY; blockY >= this.lowerBound; blockY -= this.cellHeight) {
            if (this.density.compute(new DensityFunction.SinglePointContext(context.blockX(), blockY, context.blockZ())) > 0.0F) {
               return (float)blockY;
            }
         }

         return (float)this.lowerBound;
      }
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new FindTopSurfaceFunction(visitor.apply(this.density), visitor.apply(this.upperBound), this.lowerBound, this.cellHeight);
   }

   public Interval range() {
      return Interval.of((float)this.lowerBound, Math.max((float)this.lowerBound, this.upperBound.range().max()));
   }

   public @DensityFunction.Axes int domainAxes() {
      return (this.density.domainAxes() | this.upperBound.domainAxes()) & -3;
   }

   public MapCodec<FindTopSurfaceFunction> codec() {
      return CODEC;
   }
}
