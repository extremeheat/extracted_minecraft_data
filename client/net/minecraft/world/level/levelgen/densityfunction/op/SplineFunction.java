package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.BoundedFloatFunction;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SplineFunction implements DensityFunction {
   private static final Codec<CubicSpline<Coordinate>> SPLINE_CODEC;
   public static final MapCodec<SplineFunction> CODEC;
   private final CubicSpline<Coordinate> spline;
   private final BoundedFloatFunction<Point> sampler;

   public SplineFunction(final CubicSpline<Coordinate> spline) {
      super();
      this.spline = spline;
      this.sampler = CubicSpline.asSampler(spline);
   }

   public float compute(final DensityFunction.FunctionContext context) {
      return this.sampler.apply(new Point(context));
   }

   public Interval range() {
      return this.spline.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      MutableInt axes = new MutableInt(0);
      this.spline.forEachCoordinate((coordinate) -> axes.setValue(axes.intValue() | coordinate.function().domainAxes()));
      return axes.intValue();
   }

   public void fillArray(final float[] output, final DensityFunction.ContextProvider contextProvider) {
      contextProvider.fillAllDirectly(output, this);
   }

   public DensityFunction mapChildren(final DensityFunction.Visitor visitor) {
      return new SplineFunction(this.spline.mapCoordinates((c) -> c.mapChildren(visitor)));
   }

   public MapCodec<SplineFunction> codec() {
      return CODEC;
   }

   public CubicSpline<Coordinate> spline() {
      return this.spline;
   }

   public boolean equals(final Object obj) {
      if (obj == this) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof SplineFunction) {
            SplineFunction splineFunction = (SplineFunction)obj;
            if (this.spline.equals(splineFunction.spline)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.spline.hashCode();
   }

   public String toString() {
      return this.spline.toString();
   }

   static {
      SPLINE_CODEC = CubicSpline.codec(SplineFunction.Coordinate.CODEC);
      CODEC = SPLINE_CODEC.fieldOf("spline").xmap(SplineFunction::new, SplineFunction::spline);
   }

   public static record Coordinate(DensityFunction function) implements BoundedFloatFunction<Point> {
      public static final Codec<Coordinate> CODEC;

      public Coordinate {
         super();
      }

      public float apply(final Point point) {
         return this.function.compute(point.context());
      }

      public Interval range() {
         return this.function.range();
      }

      public Coordinate mapChildren(final DensityFunction.Visitor visitor) {
         return new Coordinate(visitor.apply(this.function));
      }

      static {
         CODEC = DensityFunction.CODEC.xmap(Coordinate::new, Coordinate::function);
      }
   }

   public static record Point(DensityFunction.FunctionContext context) {
      public Point {
         super();
      }
   }
}
