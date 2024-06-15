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
import org.apache.commons.lang3.mutable.MutableObject;

public interface CubicSpline<C, I extends ToFloatFunction<C>> extends ToFloatFunction<C> {
   @VisibleForDebug
   String parityString();

   CubicSpline<C, I> mapAll(CubicSpline.CoordinateVisitor<I> var1);

   static <C, I extends ToFloatFunction<C>> Codec<CubicSpline<C, I>> codec(Codec<I> var0) {
      MutableObject var1 = new MutableObject();

      record 1Point<C, I extends ToFloatFunction<C>>(float location, CubicSpline<C, I> value, float derivative) {
         _Point/* $VF was: 1Point*/(float location, CubicSpline<C, I> value, float derivative) {
            super();
            this.location = location;
            this.value = value;
            this.derivative = derivative;
         }
      }

      Codec var2 = RecordCodecBuilder.create(
         var1x -> var1x.group(
                  Codec.FLOAT.fieldOf("location").forGetter(1Point::location),
                  Codec.lazyInitialized(var1::getValue).fieldOf("value").forGetter(1Point::value),
                  Codec.FLOAT.fieldOf("derivative").forGetter(1Point::derivative)
               )
               .apply(var1x, (var0xx, var1xx, var2x) -> new 1Point(var0xx, var1xx, var2x))
      );
      Codec var3 = RecordCodecBuilder.create(
         var2x -> var2x.group(
                  var0.fieldOf("coordinate").forGetter(CubicSpline.Multipoint::coordinate),
                  ExtraCodecs.nonEmptyList(var2.listOf())
                     .fieldOf("points")
                     .forGetter(
                        var0xx -> IntStream.range(0, var0xx.locations.length)
                              .mapToObj(
                                 var1xx -> new 1Point(var0xx.locations()[var1xx], (CubicSpline<C, I>)var0xx.values().get(var1xx), var0xx.derivatives()[var1xx])
                              )
                              .toList()
                     )
               )
               .apply(var2x, (var0xx, var1xx) -> {
                  float[] var2xx = new float[var1xx.size()];
                  com.google.common.collect.ImmutableList.Builder var3x = ImmutableList.builder();
                  float[] var4 = new float[var1xx.size()];

                  for (int var5 = 0; var5 < var1xx.size(); var5++) {
                     1Point var6 = (1Point)var1xx.get(var5);
                     var2xx[var5] = var6.location();
                     var3x.add(var6.value());
                     var4[var5] = var6.derivative();
                  }

                  return CubicSpline.Multipoint.create((I)var0xx, var2xx, var3x.build(), var4);
               })
      );
      var1.setValue(
         Codec.either(Codec.FLOAT, var3)
            .xmap(
               var0x -> (CubicSpline)var0x.map(CubicSpline.Constant::new, var0xx -> var0xx),
               var0x -> var0x instanceof CubicSpline.Constant var1x ? Either.left(var1x.value()) : Either.right((CubicSpline.Multipoint)var0x)
            )
      );
      return (Codec<CubicSpline<C, I>>)var1.getValue();
   }

   static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> constant(float var0) {
      return new CubicSpline.Constant<>(var0);
   }

   static <C, I extends ToFloatFunction<C>> CubicSpline.Builder<C, I> builder(I var0) {
      return new CubicSpline.Builder<>((I)var0);
   }

   static <C, I extends ToFloatFunction<C>> CubicSpline.Builder<C, I> builder(I var0, ToFloatFunction<Float> var1) {
      return new CubicSpline.Builder<>((I)var0, var1);
   }

   public static final class Builder<C, I extends ToFloatFunction<C>> {
      private final I coordinate;
      private final ToFloatFunction<Float> valueTransformer;
      private final FloatList locations = new FloatArrayList();
      private final List<CubicSpline<C, I>> values = Lists.newArrayList();
      private final FloatList derivatives = new FloatArrayList();

      protected Builder(I var1) {
         this((I)var1, ToFloatFunction.IDENTITY);
      }

      protected Builder(I var1, ToFloatFunction<Float> var2) {
         super();
         this.coordinate = (I)var1;
         this.valueTransformer = var2;
      }

      public CubicSpline.Builder<C, I> addPoint(float var1, float var2) {
         return this.addPoint(var1, new CubicSpline.Constant<>(this.valueTransformer.apply(var2)), 0.0F);
      }

      public CubicSpline.Builder<C, I> addPoint(float var1, float var2, float var3) {
         return this.addPoint(var1, new CubicSpline.Constant<>(this.valueTransformer.apply(var2)), var3);
      }

      public CubicSpline.Builder<C, I> addPoint(float var1, CubicSpline<C, I> var2) {
         return this.addPoint(var1, var2, 0.0F);
      }

      private CubicSpline.Builder<C, I> addPoint(float var1, CubicSpline<C, I> var2, float var3) {
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
            return CubicSpline.Multipoint.create(
               this.coordinate, this.locations.toFloatArray(), ImmutableList.copyOf(this.values), this.derivatives.toFloatArray()
            );
         }
      }
   }

   @VisibleForDebug
   public static record Constant<C, I extends ToFloatFunction<C>>(float value) implements CubicSpline<C, I> {
      public Constant(float value) {
         super();
         this.value = value;
      }

      @Override
      public float apply(C var1) {
         return this.value;
      }

      @Override
      public String parityString() {
         return String.format(Locale.ROOT, "k=%.3f", this.value);
      }

      @Override
      public float minValue() {
         return this.value;
      }

      @Override
      public float maxValue() {
         return this.value;
      }

      @Override
      public CubicSpline<C, I> mapAll(CubicSpline.CoordinateVisitor<I> var1) {
         return this;
      }
   }

   public interface CoordinateVisitor<I> {
      I visit(I var1);
   }

   @VisibleForDebug
   public static record Multipoint<C, I extends ToFloatFunction<C>>(
      I coordinate, float[] locations, List<CubicSpline<C, I>> values, float[] derivatives, float minValue, float maxValue
   ) implements CubicSpline<C, I> {

      public Multipoint(I coordinate, float[] locations, List<CubicSpline<C, I>> values, float[] derivatives, float minValue, float maxValue) {
         super();
         validateSizes(locations, values, derivatives);
         this.coordinate = (I)coordinate;
         this.locations = locations;
         this.values = values;
         this.derivatives = derivatives;
         this.minValue = minValue;
         this.maxValue = maxValue;
      }

      static <C, I extends ToFloatFunction<C>> CubicSpline.Multipoint<C, I> create(I var0, float[] var1, List<CubicSpline<C, I>> var2, float[] var3) {
         validateSizes(var1, var2, var3);
         int var4 = var1.length - 1;
         float var5 = 1.0F / 0.0F;
         float var6 = -1.0F / 0.0F;
         float var7 = var0.minValue();
         float var8 = var0.maxValue();
         if (var7 < var1[0]) {
            float var9 = linearExtend(var7, var1, ((CubicSpline)var2.get(0)).minValue(), var3, 0);
            float var10 = linearExtend(var7, var1, ((CubicSpline)var2.get(0)).maxValue(), var3, 0);
            var5 = Math.min(var5, Math.min(var9, var10));
            var6 = Math.max(var6, Math.max(var9, var10));
         }

         if (var8 > var1[var4]) {
            float var31 = linearExtend(var8, var1, ((CubicSpline)var2.get(var4)).minValue(), var3, var4);
            float var34 = linearExtend(var8, var1, ((CubicSpline)var2.get(var4)).maxValue(), var3, var4);
            var5 = Math.min(var5, Math.min(var31, var34));
            var6 = Math.max(var6, Math.max(var31, var34));
         }

         for (CubicSpline var35 : var2) {
            var5 = Math.min(var5, var35.minValue());
            var6 = Math.max(var6, var35.maxValue());
         }

         for (int var33 = 0; var33 < var4; var33++) {
            float var36 = var1[var33];
            float var11 = var1[var33 + 1];
            float var12 = var11 - var36;
            CubicSpline var13 = (CubicSpline)var2.get(var33);
            CubicSpline var14 = (CubicSpline)var2.get(var33 + 1);
            float var15 = var13.minValue();
            float var16 = var13.maxValue();
            float var17 = var14.minValue();
            float var18 = var14.maxValue();
            float var19 = var3[var33];
            float var20 = var3[var33 + 1];
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

         return new CubicSpline.Multipoint<>((I)var0, var1, var2, var3, var5, var6);
      }

      private static float linearExtend(float var0, float[] var1, float var2, float[] var3, int var4) {
         float var5 = var3[var4];
         return var5 == 0.0F ? var2 : var2 + var5 * (var0 - var1[var4]);
      }

      private static <C, I extends ToFloatFunction<C>> void validateSizes(float[] var0, List<CubicSpline<C, I>> var1, float[] var2) {
         if (var0.length != var1.size() || var0.length != var2.length) {
            throw new IllegalArgumentException("All lengths must be equal, got: " + var0.length + " " + var1.size() + " " + var2.length);
         } else if (var0.length == 0) {
            throw new IllegalArgumentException("Cannot create a multipoint spline with no points");
         }
      }

      @Override
      public float apply(C var1) {
         float var2 = this.coordinate.apply((C)var1);
         int var3 = findIntervalStart(this.locations, var2);
         int var4 = this.locations.length - 1;
         if (var3 < 0) {
            return linearExtend(var2, this.locations, this.values.get(0).apply((C)var1), this.derivatives, 0);
         } else if (var3 == var4) {
            return linearExtend(var2, this.locations, this.values.get(var4).apply((C)var1), this.derivatives, var4);
         } else {
            float var5 = this.locations[var3];
            float var6 = this.locations[var3 + 1];
            float var7 = (var2 - var5) / (var6 - var5);
            ToFloatFunction var8 = this.values.get(var3);
            ToFloatFunction var9 = this.values.get(var3 + 1);
            float var10 = this.derivatives[var3];
            float var11 = this.derivatives[var3 + 1];
            float var12 = var8.apply(var1);
            float var13 = var9.apply(var1);
            float var14 = var10 * (var6 - var5) - (var13 - var12);
            float var15 = -var11 * (var6 - var5) + (var13 - var12);
            return Mth.lerp(var7, var12, var13) + var7 * (1.0F - var7) * Mth.lerp(var7, var14, var15);
         }
      }

      private static int findIntervalStart(float[] var0, float var1) {
         return Mth.binarySearch(0, var0.length, var2 -> var1 < var0[var2]) - 1;
      }

      @VisibleForTesting
      @Override
      public String parityString() {
         return "Spline{coordinate="
            + this.coordinate
            + ", locations="
            + this.toString(this.locations)
            + ", derivatives="
            + this.toString(this.derivatives)
            + ", values="
            + this.values.stream().map(CubicSpline::parityString).collect(Collectors.joining(", ", "[", "]"))
            + "}";
      }

      private String toString(float[] var1) {
         return "["
            + IntStream.range(0, var1.length)
               .mapToDouble(var1x -> (double)var1[var1x])
               .mapToObj(var0 -> String.format(Locale.ROOT, "%.3f", var0))
               .collect(Collectors.joining(", "))
            + "]";
      }

      @Override
      public CubicSpline<C, I> mapAll(CubicSpline.CoordinateVisitor<I> var1) {
         return create((I)var1.visit(this.coordinate), this.locations, this.values().stream().map(var1x -> var1x.mapAll(var1)).toList(), this.derivatives);
      }
   }
}
