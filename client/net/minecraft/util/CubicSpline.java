package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C, I extends ToFloatFunction<C>> extends ToFloatFunction<C> {
   @VisibleForDebug
   String parityString();

   CubicSpline<C, I> mapAll(CoordinateVisitor<I> var1);

   static <C, I extends ToFloatFunction<C>> Codec<CubicSpline<C, I>> codec(Codec<I> var0) {
      MutableObject var1 = new MutableObject();
      Codec var2 = RecordCodecBuilder.create((var1x) -> {
         RecordCodecBuilder var10001 = Codec.FLOAT.fieldOf("location").forGetter(1Point::location);
         Objects.requireNonNull(var1);
         return var1x.group(var10001, Codec.lazyInitialized(var1::getValue).fieldOf("value").forGetter(1Point::value), Codec.FLOAT.fieldOf("derivative").forGetter(1Point::derivative)).apply(var1x, (var0, var1xx, var2) -> {
            record 1Point<C, I extends ToFloatFunction<C>>(float location, CubicSpline<C, I> value, float derivative) {
               _Point/* $FF was: 1Point*/(float var1, CubicSpline<C, I> var2, float var3) {
                  super();
                  this.location = var1;
                  this.value = var2;
                  this.derivative = var3;
               }

               public float location() {
                  return this.location;
               }

               public CubicSpline<C, I> value() {
                  return this.value;
               }

               public float derivative() {
                  return this.derivative;
               }
            }

            return new 1Point(var0, var1xx, var2);
         });
      });
      Codec var3 = RecordCodecBuilder.create((var2x) -> {
         return var2x.group(var0.fieldOf("coordinate").forGetter(Multipoint::coordinate), ExtraCodecs.nonEmptyList(var2.listOf()).fieldOf("points").forGetter((var0x) -> {
            return IntStream.range(0, var0x.locations.length).mapToObj((var1) -> {
               return new 1Point(var0x.locations()[var1], (CubicSpline)var0x.values().get(var1), var0x.derivatives()[var1]);
            }).toList();
         })).apply(var2x, (var0x, var1) -> {
            float[] var2 = new float[var1.size()];
            ImmutableList.Builder var3 = ImmutableList.builder();
            float[] var4 = new float[var1.size()];

            for(int var5 = 0; var5 < var1.size(); ++var5) {
               1Point var6 = (1Point)var1.get(var5);
               var2[var5] = var6.location();
               var3.add(var6.value());
               var4[var5] = var6.derivative();
            }

            return CubicSpline.Multipoint.create(var0x, var2, var3.build(), var4);
         });
      });
      var1.setValue(Codec.either(Codec.FLOAT, var3).xmap((var0x) -> {
         return (CubicSpline)var0x.map(Constant::new, (var0) -> {
            return var0;
         });
      }, (var0x) -> {
         Either var10000;
         if (var0x instanceof Constant var1) {
            var10000 = Either.left(var1.value());
         } else {
            var10000 = Either.right((Multipoint)var0x);
         }

         return var10000;
      }));
      return (Codec)var1.getValue();
   }

   static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> constant(float var0) {
      return new Constant(var0);
   }

   static <C, I extends ToFloatFunction<C>> Builder<C, I> builder(I var0) {
      return new Builder(var0);
   }

   static <C, I extends ToFloatFunction<C>> Builder<C, I> builder(I var0, ToFloatFunction<Float> var1) {
      return new Builder(var0, var1);
   }

   @VisibleForDebug
   public static record Constant<C, I extends ToFloatFunction<C>>(float value) implements CubicSpline<C, I> {
      public Constant(float var1) {
         super();
         this.value = var1;
      }

      public float apply(C var1) {
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

      public CubicSpline<C, I> mapAll(CoordinateVisitor<I> var1) {
         return this;
      }

      public float value() {
         return this.value;
      }
   }

   public static final class Builder<C, I extends ToFloatFunction<C>> {
      private final I coordinate;
      private final ToFloatFunction<Float> valueTransformer;
      private final FloatList locations;
      private final List<CubicSpline<C, I>> values;
      private final FloatList derivatives;

      protected Builder(I var1) {
         this(var1, ToFloatFunction.IDENTITY);
      }

      protected Builder(I var1, ToFloatFunction<Float> var2) {
         super();
         this.locations = new FloatArrayList();
         this.values = Lists.newArrayList();
         this.derivatives = new FloatArrayList();
         this.coordinate = var1;
         this.valueTransformer = var2;
      }

      public Builder<C, I> addPoint(float var1, float var2) {
         return this.addPoint(var1, new Constant(this.valueTransformer.apply(var2)), 0.0F);
      }

      public Builder<C, I> addPoint(float var1, float var2, float var3) {
         return this.addPoint(var1, new Constant(this.valueTransformer.apply(var2)), var3);
      }

      public Builder<C, I> addPoint(float var1, CubicSpline<C, I> var2) {
         return this.addPoint(var1, var2, 0.0F);
      }

      private Builder<C, I> addPoint(float var1, CubicSpline<C, I> var2, float var3) {
         if (!this.locations.isEmpty() && var1 <= this.locations.getFloat(this.locations.size() - 1)) {
            throw new IllegalArgumentException("Please register points in ascending order");
         } else {
            this.locations.add(var1);
            this.values.add(var2);
            this.derivatives.add(var3);
            return this;
         }
      }

      public CubicSpline<C, I> build() {
         if (this.locations.isEmpty()) {
            throw new IllegalStateException("No elements added");
         } else {
            return CubicSpline.Multipoint.create(this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray());
         }
      }
   }

   @VisibleForDebug
   public static record Multipoint<C, I extends ToFloatFunction<C>>(I coordinate, float[] locations, List<CubicSpline<C, I>> values, float[] derivatives, float minValue, float maxValue) implements CubicSpline<C, I> {
      final float[] locations;

      public Multipoint(I var1, float[] var2, List<CubicSpline<C, I>> var3, float[] var4, float var5, float var6) {
         super();
         validateSizes(var2, var3, var4);
         this.coordinate = var1;
         this.locations = var2;
         this.values = var3;
         this.derivatives = var4;
         this.minValue = var5;
         this.maxValue = var6;
      }

      static <C, I extends ToFloatFunction<C>> Multipoint<C, I> create(I var0, float[] var1, List<CubicSpline<C, I>> var2, float[] var3) {
         validateSizes(var1, var2, var3);
         int var4 = var1.length - 1;
         float var5 = 1.0F / 0.0F;
         float var6 = -1.0F / 0.0F;
         float var7 = var0.minValue();
         float var8 = var0.maxValue();
         float var9;
         float var10;
         if (var7 < var1[0]) {
            var9 = linearExtend(var7, var1, ((CubicSpline)var2.get(0)).minValue(), var3, 0);
            var10 = linearExtend(var7, var1, ((CubicSpline)var2.get(0)).maxValue(), var3, 0);
            var5 = Math.min(var5, Math.min(var9, var10));
            var6 = Math.max(var6, Math.max(var9, var10));
         }

         if (var8 > var1[var4]) {
            var9 = linearExtend(var8, var1, ((CubicSpline)var2.get(var4)).minValue(), var3, var4);
            var10 = linearExtend(var8, var1, ((CubicSpline)var2.get(var4)).maxValue(), var3, var4);
            var5 = Math.min(var5, Math.min(var9, var10));
            var6 = Math.max(var6, Math.max(var9, var10));
         }

         CubicSpline var33;
         for(Iterator var31 = var2.iterator(); var31.hasNext(); var6 = Math.max(var6, var33.maxValue())) {
            var33 = (CubicSpline)var31.next();
            var5 = Math.min(var5, var33.minValue());
         }

         for(int var32 = 0; var32 < var4; ++var32) {
            var10 = var1[var32];
            float var11 = var1[var32 + 1];
            float var12 = var11 - var10;
            CubicSpline var13 = (CubicSpline)var2.get(var32);
            CubicSpline var14 = (CubicSpline)var2.get(var32 + 1);
            float var15 = var13.minValue();
            float var16 = var13.maxValue();
            float var17 = var14.minValue();
            float var18 = var14.maxValue();
            float var19 = var3[var32];
            float var20 = var3[var32 + 1];
            if (var19 != 0.0F || var20 != 0.0F) {
               float var21 = var19 * var12;
               float var22 = var20 * var12;
               float var23 = Math.min(var15, var17);
               float var24 = Math.max(var16, var18);
               float var25 = var21 - var18 + var15;
               float var26 = var21 - var17 + var16;
               float var27 = -var22 + var17 - var16;
               float var28 = -var22 + var18 - var15;
               float var29 = Math.min(var25, var27);
               float var30 = Math.max(var26, var28);
               var5 = Math.min(var5, var23 + 0.25F * var29);
               var6 = Math.max(var6, var24 + 0.25F * var30);
            }
         }

         return new Multipoint(var0, var1, var2, var3, var5, var6);
      }

      private static float linearExtend(float var0, float[] var1, float var2, float[] var3, int var4) {
         float var5 = var3[var4];
         return var5 == 0.0F ? var2 : var2 + var5 * (var0 - var1[var4]);
      }

      private static <C, I extends ToFloatFunction<C>> void validateSizes(float[] var0, List<CubicSpline<C, I>> var1, float[] var2) {
         if (var0.length == var1.size() && var0.length == var2.length) {
            if (var0.length == 0) {
               throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
            }
         } else {
            throw new IllegalArgumentException("All lengths must be equal, got: " + var0.length + " " + var1.size() + " " + var2.length);
         }
      }

      public float apply(C var1) {
         float var2 = this.coordinate.apply(var1);
         int var3 = findIntervalStart(this.locations, var2);
         int var4 = this.locations.length - 1;
         if (var3 < 0) {
            return linearExtend(var2, this.locations, ((CubicSpline)this.values.get(0)).apply(var1), this.derivatives, 0);
         } else if (var3 == var4) {
            return linearExtend(var2, this.locations, ((CubicSpline)this.values.get(var4)).apply(var1), this.derivatives, var4);
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

      private static int findIntervalStart(float[] var0, float var1) {
         return Mth.binarySearch(0, var0.length, (var2) -> {
            return var1 < var0[var2];
         }) - 1;
      }

      @VisibleForTesting
      public String parityString() {
         String var10000 = String.valueOf(this.coordinate);
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

      public CubicSpline<C, I> mapAll(CoordinateVisitor<I> var1) {
         return create((ToFloatFunction)var1.visit(this.coordinate), this.locations, this.values().stream().map((var1x) -> {
            return var1x.mapAll(var1);
         }).toList(), this.derivatives);
      }

      public I coordinate() {
         return this.coordinate;
      }

      public float[] locations() {
         return this.locations;
      }

      public List<CubicSpline<C, I>> values() {
         return this.values;
      }

      public float[] derivatives() {
         return this.derivatives;
      }

      public float minValue() {
         return this.minValue;
      }

      public float maxValue() {
         return this.maxValue;
      }
   }

   public interface CoordinateVisitor<I> {
      I visit(I var1);
   }
}
