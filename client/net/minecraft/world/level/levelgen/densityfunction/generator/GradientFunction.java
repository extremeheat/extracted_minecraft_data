package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.TilingMode;

public record GradientFunction(Direction.Axis axis, TilingMode tiling, int fromCoordinate, int toCoordinate, float fromValue, float toValue) implements DensityFunction {
   public static final MapCodec<GradientFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Direction.Axis.CODEC.fieldOf("axis").forGetter(GradientFunction::axis), TilingMode.CODEC.optionalFieldOf("tiling", TilingMode.CLAMP_TO_EDGE).forGetter(GradientFunction::tiling), Codec.INT.fieldOf("from_coordinate").forGetter(GradientFunction::fromCoordinate), Codec.INT.fieldOf("to_coordinate").forGetter(GradientFunction::toCoordinate), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(GradientFunction::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(GradientFunction::toValue)).apply(i, GradientFunction::new)).validate(GradientFunction::validate);

   public GradientFunction {
      super();
   }

   private static DataResult<GradientFunction> validate(final GradientFunction gradient) {
      return gradient.fromCoordinate == gradient.toCoordinate ? DataResult.error(() -> "from_coordinate cannot be equal to to_coordinate") : DataResult.success(gradient);
   }

   public float compute(final DensityFunction.FunctionContext context) {
      int var10000;
      switch (this.axis) {
         case X -> var10000 = context.blockX();
         case Y -> var10000 = context.blockY();
         case Z -> var10000 = context.blockZ();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      int coordinate = var10000;
      int coordinateRange = this.toCoordinate - this.fromCoordinate;
      int relativeCoordinate = coordinate - this.fromCoordinate;
      switch (this.tiling) {
         case CLAMP_TO_EDGE:
            var10000 = relativeCoordinate;
            break;
         case REPEAT:
            var10000 = Math.floorMod(relativeCoordinate, coordinateRange);
            break;
         case MIRRORED_REPEAT:
            int tileIndex = Math.floorDiv(relativeCoordinate, coordinateRange);
            int localCoordinate = relativeCoordinate - tileIndex * coordinateRange;
            var10000 = (tileIndex & 1) == 0 ? localCoordinate : coordinateRange - localCoordinate;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      int factorCoordinate = var10000;
      return Mth.clampedLerp((float)factorCoordinate / (float)coordinateRange, this.fromValue, this.toValue);
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      if (this.axis == Direction.Axis.Y && this.tiling == TilingMode.CLAMP_TO_EDGE) {
         float coordinateRange = (float)(this.toCoordinate - this.fromCoordinate);

         for(int i = 0; i < output.length; ++i) {
            int y = contextProvider.forIndex(i).blockY();
            output[i] = Mth.clampedLerp((float)(y - this.fromCoordinate) / coordinateRange, this.fromValue, this.toValue);
         }

      } else {
         contextProvider.fillAllDirectly(output, this);
      }
   }

   public Interval range() {
      return Interval.encapsulating(this.fromValue, this.toValue);
   }

   public @DensityFunction.Axes int domainAxes() {
      return DensityFunction.axesFrom(this.axis);
   }

   public MapCodec<GradientFunction> codec() {
      return CODEC;
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return this;
   }
}
