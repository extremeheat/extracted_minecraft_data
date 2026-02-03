package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C, I extends BoundedFloatFunction<C>> extends BoundedFloatFunction<C> {
   @VisibleForDebug
   String parityString();

   CubicSpline<C, I> mapAll(final CoordinateVisitor<I> visitor);

   static <C, I extends BoundedFloatFunction<C>> Codec<CubicSpline<C, I>> codec(final Codec<I> coordinateCodec) {
      MutableObject<Codec<CubicSpline<C, I>>> result = new MutableObject();
      Codec<Point<C, I>> pointCodec = RecordCodecBuilder.create((i) -> i.group(Codec.FLOAT.fieldOf("location").forGetter(Point::location), Codec.lazyInitialized(result).fieldOf("value").forGetter(Point::value), Codec.FLOAT.fieldOf("derivative").forGetter(Point::derivative)).apply(i, (x$0, x$1, x$2) -> {
            record Point<C, I extends BoundedFloatFunction<C>>(float location, CubicSpline<C, I> value, float derivative) {
               Point {
                  super();
               }
            }

            return new Point(x$0, x$1, x$2);
         }));
      Codec<Multipoint<C, I>> multipointCodec = RecordCodecBuilder.create((i) -> i.group(coordinateCodec.fieldOf("coordinate").forGetter(Multipoint::coordinate), ExtraCodecs.nonEmptyList(pointCodec.listOf()).fieldOf("points").forGetter((m) -> IntStream.range(0, m.locations.length).mapToObj((p) -> new Point(m.locations()[p], (CubicSpline)m.values().get(p), m.derivatives()[p])).toList())).apply(i, (coordinate, points) -> {
            float[] locations = new float[points.size()];
            ImmutableList.Builder<CubicSpline<C, I>> values = ImmutableList.builder();
            float[] derivatives = new float[points.size()];

            for(int p = 0; p < points.size(); ++p) {
               Point<C, I> point = (Point)points.get(p);
               locations[p] = point.location();
               values.add(point.value());
               derivatives[p] = point.derivative();
            }

            return CubicSpline.Multipoint.create(coordinate, locations, values.build(), derivatives);
         }));
      result.setValue(Codec.either(Codec.FLOAT, multipointCodec).xmap((e) -> (CubicSpline)e.map(Constant::new, (m) -> m), (s) -> {
         Either var10000;
         if (s instanceof Constant<C, I> c) {
            var10000 = Either.left(c.value());
         } else {
            var10000 = Either.right((Multipoint)s);
         }

         return var10000;
      }));
      return (Codec)result.get();
   }

   static <C, I extends BoundedFloatFunction<C>> CubicSpline<C, I> constant(final float value) {
      return new Constant<C, I>(value);
   }

   static <C, I extends BoundedFloatFunction<C>> Builder<C, I> builder(final I coordinate) {
      return new Builder<C, I>(coordinate);
   }

   static <C, I extends BoundedFloatFunction<C>> Builder<C, I> builder(final I coordinate, final BoundedFloatFunction<Float> valueTransformer) {
      return new Builder<C, I>(coordinate, valueTransformer);
   }

   @VisibleForDebug
   public static record Multipoint<C, I extends BoundedFloatFunction<C>>(I coordinate, float[] locations, List<CubicSpline<C, I>> values, float[] derivatives, float minValue, float maxValue) implements CubicSpline<C, I> {
      public Multipoint {
         super();
         validateSizes(locations, values, derivatives);
      }

      private static <C, I extends BoundedFloatFunction<C>> Multipoint<C, I> create(final I coordinate, final float[] locations, final List<CubicSpline<C, I>> values, final float[] derivatives) {
         validateSizes(locations, values, derivatives);
         int lastIndex = locations.length - 1;
         float minValue = 1.0F / 0.0F;
         float maxValue = -1.0F / 0.0F;
         float minInput = coordinate.minValue();
         float maxInput = coordinate.maxValue();
         if (minInput < locations[0]) {
            float edge1 = linearExtend(minInput, locations, ((CubicSpline)values.get(0)).minValue(), derivatives, 0);
            float edge2 = linearExtend(minInput, locations, ((CubicSpline)values.get(0)).maxValue(), derivatives, 0);
            minValue = Math.min(minValue, Math.min(edge1, edge2));
            maxValue = Math.max(maxValue, Math.max(edge1, edge2));
         }

         if (maxInput > locations[lastIndex]) {
            float edge1 = linearExtend(maxInput, locations, ((CubicSpline)values.get(lastIndex)).minValue(), derivatives, lastIndex);
            float edge2 = linearExtend(maxInput, locations, ((CubicSpline)values.get(lastIndex)).maxValue(), derivatives, lastIndex);
            minValue = Math.min(minValue, Math.min(edge1, edge2));
            maxValue = Math.max(maxValue, Math.max(edge1, edge2));
         }

         for(CubicSpline<C, I> value : values) {
            minValue = Math.min(minValue, value.minValue());
            maxValue = Math.max(maxValue, value.maxValue());
         }

         for(int i = 0; i < lastIndex; ++i) {
            float x1 = locations[i];
            float x2 = locations[i + 1];
            float xDiff = x2 - x1;
            CubicSpline<C, I> v1 = (CubicSpline)values.get(i);
            CubicSpline<C, I> v2 = (CubicSpline)values.get(i + 1);
            float min1 = v1.minValue();
            float max1 = v1.maxValue();
            float min2 = v2.minValue();
            float max2 = v2.maxValue();
            float d1 = derivatives[i];
            float d2 = derivatives[i + 1];
            if (d1 != 0.0F || d2 != 0.0F) {
               float p1 = d1 * xDiff;
               float p2 = d2 * xDiff;
               float minLerp1 = Math.min(min1, min2);
               float maxLerp1 = Math.max(max1, max2);
               float minA = p1 - max2 + min1;
               float maxA = p1 - min2 + max1;
               float minB = -p2 + min2 - max1;
               float maxB = -p2 + max2 - min1;
               float minLerp2 = Math.min(minA, minB);
               float maxLerp2 = Math.max(maxA, maxB);
               minValue = Math.min(minValue, minLerp1 + 0.25F * minLerp2);
               maxValue = Math.max(maxValue, maxLerp1 + 0.25F * maxLerp2);
            }
         }

         return new Multipoint<C, I>(coordinate, locations, values, derivatives, minValue, maxValue);
      }

      private static float linearExtend(final float input, final float[] locations, final float value, final float[] derivatives, final int index) {
         float derivative = derivatives[index];
         return derivative == 0.0F ? value : value + derivative * (input - locations[index]);
      }

      private static <C, I extends BoundedFloatFunction<C>> void validateSizes(final float[] locations, final List<CubicSpline<C, I>> values, final float[] derivatives) {
         if (locations.length == values.size() && locations.length == derivatives.length) {
            if (locations.length == 0) {
               throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
            }
         } else {
            throw new IllegalArgumentException("All lengths must be equal, got: " + locations.length + " " + values.size() + " " + derivatives.length);
         }
      }

      public float apply(final C c) {
         float input = this.coordinate.apply(c);
         int start = findIntervalStart(this.locations, input);
         int lastIndex = this.locations.length - 1;
         if (start < 0) {
            return linearExtend(input, this.locations, ((CubicSpline)this.values.get(0)).apply(c), this.derivatives, 0);
         } else if (start == lastIndex) {
            return linearExtend(input, this.locations, ((CubicSpline)this.values.get(lastIndex)).apply(c), this.derivatives, lastIndex);
         } else {
            float x1 = this.locations[start];
            float x2 = this.locations[start + 1];
            float t = (input - x1) / (x2 - x1);
            BoundedFloatFunction<C> f1 = (BoundedFloatFunction)this.values.get(start);
            BoundedFloatFunction<C> f2 = (BoundedFloatFunction)this.values.get(start + 1);
            float d1 = this.derivatives[start];
            float d2 = this.derivatives[start + 1];
            float y1 = f1.apply(c);
            float y2 = f2.apply(c);
            float a = d1 * (x2 - x1) - (y2 - y1);
            float b = -d2 * (x2 - x1) + (y2 - y1);
            float offset = Mth.lerp(t, y1, y2) + t * (1.0F - t) * Mth.lerp(t, a, b);
            return offset;
         }
      }

      private static int findIntervalStart(final float[] locations, final float input) {
         return Mth.binarySearch(0, locations.length, (i) -> input < locations[i]) - 1;
      }

      @VisibleForTesting
      public String parityString() {
         String var10000 = String.valueOf(this.coordinate);
         return "Spline{coordinate=" + var10000 + ", locations=" + this.toString(this.locations) + ", derivatives=" + this.toString(this.derivatives) + ", values=" + (String)this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]")) + "}";
      }

      private String toString(final float[] arr) {
         Stream var10000 = IntStream.range(0, arr.length).mapToDouble((i) -> (double)arr[i]).mapToObj((f) -> String.format(Locale.ROOT, "%.3f", f));
         return "[" + (String)var10000.collect(Collectors.joining(", ")) + "]";
      }

      public CubicSpline<C, I> mapAll(final CoordinateVisitor<I> visitor) {
         return create(visitor.visit(this.coordinate), this.locations, this.values().stream().map((v) -> v.mapAll(visitor)).toList(), this.derivatives);
      }
   }

   @VisibleForDebug
   public static record Constant<C, I extends BoundedFloatFunction<C>>(float value) implements CubicSpline<C, I> {
      public Constant {
         super();
      }

      public float apply(final C c) {
         return this.value;
      }

      public String parityString() {
         return String.format(Locale.ROOT, "k=%.3f", this.value);
      }

      public float minValue() {
         return this.value;
      }

      public float maxValue() {
         return this.value;
      }

      public CubicSpline<C, I> mapAll(final CoordinateVisitor<I> visitor) {
         return this;
      }
   }

   public static final class Builder<C, I extends BoundedFloatFunction<C>> {
      private final I coordinate;
      private final BoundedFloatFunction<Float> valueTransformer;
      private final FloatList locations;
      private final List<CubicSpline<C, I>> values;
      private final FloatList derivatives;

      protected Builder(final I coordinate) {
         this(coordinate, BoundedFloatFunction.IDENTITY);
      }

      protected Builder(final I coordinate, final BoundedFloatFunction<Float> valueTransformer) {
         super();
         this.locations = new FloatArrayList();
         this.values = Lists.newArrayList();
         this.derivatives = new FloatArrayList();
         this.coordinate = coordinate;
         this.valueTransformer = valueTransformer;
      }

      public Builder<C, I> addPoint(final float location, final float value) {
         return this.addPoint(location, new Constant(this.valueTransformer.apply(value)), 0.0F);
      }

      public Builder<C, I> addPoint(final float location, final float value, final float derivative) {
         return this.addPoint(location, new Constant(this.valueTransformer.apply(value)), derivative);
      }

      public Builder<C, I> addPoint(final float location, final CubicSpline<C, I> sampler) {
         return this.addPoint(location, sampler, 0.0F);
      }

      private Builder<C, I> addPoint(final float location, final CubicSpline<C, I> sampler, final float derivative) {
         if (!this.locations.isEmpty() && location <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add(location);
            this.values.add(sampler);
            this.derivatives.add(derivative);
            return this;
         }
      }

      public CubicSpline<C, I> build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return CubicSpline.Multipoint.<C, I>create(this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
         }
      }
   }

   public interface CoordinateVisitor<I> {
      I visit(final I input);
   }
}
