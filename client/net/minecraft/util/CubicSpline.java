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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C> extends ToFloatFunction<C> {
   @VisibleForDebug
   String parityString();

   static <C> Codec<CubicSpline<C>> codec(Codec<ToFloatFunction<C>> var0) {
      MutableObject var1 = new MutableObject();
      Codec var2 = RecordCodecBuilder.create((var1x) -> {
         RecordCodecBuilder var10001 = Codec.FLOAT.fieldOf("location").forGetter(InnerPoint::location);
         Objects.requireNonNull(var1);
         return var1x.group(var10001, ExtraCodecs.lazyInitializedCodec(var1::getValue).fieldOf("value").forGetter(InnerPoint::value), Codec.FLOAT.fieldOf("derivative").forGetter(InnerPoint::derivative)).apply(var1x, (var0, var1xx, var2) -> {
            record InnerPoint<C>(float a, CubicSpline<C> b, float c) {
               private final float location;
               private final CubicSpline<C> value;
               private final float derivative;

               InnerPoint(float var1, CubicSpline<C> var2, float var3) {
                  super();
                  this.location = var1;
                  this.value = var2;
                  this.derivative = var3;
               }

               public float location() {
                  return this.location;
               }

               public CubicSpline<C> value() {
                  return this.value;
               }

               public float derivative() {
                  return this.derivative;
               }
            }

            return new InnerPoint(var0, var1xx, var2);
         });
      });
      Codec var3 = RecordCodecBuilder.create((var2x) -> {
         return var2x.group(var0.fieldOf("coordinate").forGetter(CubicSpline.Multipoint::coordinate), ExtraCodecs.nonEmptyList(var2.listOf()).fieldOf("points").forGetter((var0x) -> {
            return IntStream.range(0, var0x.locations.length).mapToObj((var1) -> {
               return new InnerPoint(var0x.locations()[var1], (CubicSpline)var0x.values().get(var1), var0x.derivatives()[var1]);
            }).toList();
         })).apply(var2x, (var0x, var1) -> {
            float[] var2 = new float[var1.size()];
            com.google.common.collect.ImmutableList.Builder var3 = ImmutableList.builder();
            float[] var4 = new float[var1.size()];

            for(int var5 = 0; var5 < var1.size(); ++var5) {
               InnerPoint var6 = (InnerPoint)var1.get(var5);
               var2[var5] = var6.location();
               var3.add(var6.value());
               var4[var5] = var6.derivative();
            }

            return new CubicSpline.Multipoint(var0x, var2, var3.build(), var4);
         });
      });
      var1.setValue(Codec.either(Codec.FLOAT, var3).xmap((var0x) -> {
         return (CubicSpline)var0x.map(CubicSpline.Constant::new, (var0) -> {
            return var0;
         });
      }, (var0x) -> {
         Either var10000;
         if (var0x instanceof CubicSpline.Constant) {
            CubicSpline.Constant var1 = (CubicSpline.Constant)var0x;
            var10000 = Either.left(var1.value());
         } else {
            var10000 = Either.right((CubicSpline.Multipoint)var0x);
         }

         return var10000;
      }));
      return (Codec)var1.getValue();
   }

   static <C> CubicSpline<C> constant(float var0) {
      return new CubicSpline.Constant(var0);
   }

   static <C> CubicSpline.Builder<C> builder(ToFloatFunction<C> var0) {
      return new CubicSpline.Builder(var0);
   }

   static <C> CubicSpline.Builder<C> builder(ToFloatFunction<C> var0, ToFloatFunction<Float> var1) {
      return new CubicSpline.Builder(var0, var1);
   }

   @VisibleForDebug
   public static record Constant<C>(float a) implements CubicSpline<C> {
      private final float value;

      public Constant(float var1) {
         super();
         this.value = var1;
      }

      public float apply(C var1) {
         return this.value;
      }

      public String parityString() {
         return String.format("k=%.3f", this.value);
      }

      public float value() {
         return this.value;
      }
   }

   public static final class Builder<C> {
      private final ToFloatFunction<C> coordinate;
      private final ToFloatFunction<Float> valueTransformer;
      private final FloatList locations;
      private final List<CubicSpline<C>> values;
      private final FloatList derivatives;

      protected Builder(ToFloatFunction<C> var1) {
         this(var1, (var0) -> {
            return var0;
         });
      }

      protected Builder(ToFloatFunction<C> var1, ToFloatFunction<Float> var2) {
         super();
         this.locations = new FloatArrayList();
         this.values = Lists.newArrayList();
         this.derivatives = new FloatArrayList();
         this.coordinate = var1;
         this.valueTransformer = var2;
      }

      public CubicSpline.Builder<C> addPoint(float var1, float var2, float var3) {
         return this.addPoint(var1, new CubicSpline.Constant(this.valueTransformer.apply(var2)), var3);
      }

      public CubicSpline.Builder<C> addPoint(float var1, CubicSpline<C> var2, float var3) {
         if (!this.locations.isEmpty() && var1 <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add(var1);
            this.values.add(var2);
            this.derivatives.add(var3);
            return this;
         }
      }

      public CubicSpline<C> build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return new CubicSpline.Multipoint(this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
         }
      }
   }

   @VisibleForDebug
   public static record Multipoint<C>(ToFloatFunction<C> a, float[] b, List<CubicSpline<C>> c, float[] d) implements CubicSpline<C> {
      private final ToFloatFunction<C> coordinate;
      final float[] locations;
      private final List<CubicSpline<C>> values;
      private final float[] derivatives;

      public Multipoint(ToFloatFunction<C> var1, float[] var2, List<CubicSpline<C>> var3, float[] var4) {
         super();
         if (var2.length == var3.size() && var2.length == var4.length) {
            this.coordinate = var1;
            this.locations = var2;
            this.values = var3;
            this.derivatives = var4;
         } else {
            throw new IllegalArgumentException("All lengths must be equal, got: " + var2.length + " " + var3.size() + " " + var4.length);
         }
      }

      public float apply(C var1) {
         float var2 = this.coordinate.apply(var1);
         int var3 = Mth.binarySearch(0, this.locations.length, (var2x) -> {
            return var2 < this.locations[var2x];
         }) - 1;
         int var4 = this.locations.length - 1;
         if (var3 < 0) {
            return ((CubicSpline)this.values.get(0)).apply(var1) + this.derivatives[0] * (var2 - this.locations[0]);
         } else if (var3 == var4) {
            return ((CubicSpline)this.values.get(var4)).apply(var1) + this.derivatives[var4] * (var2 - this.locations[var4]);
         } else {
            float var5 = this.locations[var3];
            float var6 = this.locations[var3 + 1];
            float var7 = (var2 - var5) / (var6 - var5);
            ToFloatFunction var8 = (ToFloatFunction)this.values.get(var3);
            ToFloatFunction var9 = (ToFloatFunction)this.values.get(var3 + 1);
            float var10 = this.derivatives[var3];
            float var11 = this.derivatives[var3 + 1];
            float var12 = var8.apply(var1);
            float var13 = var9.apply(var1);
            float var14 = var10 * (var6 - var5) - (var13 - var12);
            float var15 = -var11 * (var6 - var5) + (var13 - var12);
            float var16 = Mth.lerp(var7, var12, var13) + var7 * (1.0F - var7) * Mth.lerp(var7, var14, var15);
            return var16;
         }
      }

      @VisibleForTesting
      public String parityString() {
         ToFloatFunction var10000 = this.coordinate;
         return "Spline{coordinate=" + var10000 + ", locations=" + this.toString(this.locations) + ", derivatives=" + this.toString(this.derivatives) + ", values=" + (String)this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]")) + "}";
      }

      private String toString(float[] var1) {
         Stream var10000 = IntStream.range(0, var1.length).mapToDouble((var1x) -> {
            return (double)var1[var1x];
         }).mapToObj((var0) -> {
            return String.format(Locale.ROOT, "%.3f", var0);
         });
         return "[" + (String)var10000.collect(Collectors.joining(", ")) + "]";
      }

      public ToFloatFunction<C> coordinate() {
         return this.coordinate;
      }

      public float[] locations() {
         return this.locations;
      }

      public List<CubicSpline<C>> values() {
         return this.values;
      }

      public float[] derivatives() {
         return this.derivatives;
      }
   }
}
