package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public interface Float2DoubleMap extends Float2DoubleFunction, Map<Float, Double> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(double var1);

   double defaultReturnValue();

   ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, Double>> entrySet() {
      return this.float2DoubleEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Double put(Float var1, Double var2) {
      return Float2DoubleFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double get(Object var1) {
      return Float2DoubleFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double remove(Object var1) {
      return Float2DoubleFunction.super.remove(var1);
   }

   FloatSet keySet();

   DoubleCollection values();

   boolean containsKey(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Float2DoubleFunction.super.containsKey(var1);
   }

   boolean containsValue(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Double)var1);
   }

   default double getOrDefault(float var1, double var2) {
      double var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var4;
   }

   default double putIfAbsent(float var1, double var2) {
      double var4 = this.get(var1);
      double var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var6;
      } else {
         return var4;
      }
   }

   default boolean remove(float var1, double var2) {
      double var4 = this.get(var1);
      if (Double.doubleToLongBits(var4) == Double.doubleToLongBits(var2) && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float var1, double var2, double var4) {
      double var6 = this.get(var1);
      if (Double.doubleToLongBits(var6) == Double.doubleToLongBits(var2) && (var6 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default double replace(float var1, double var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default double computeIfAbsent(float var1, DoubleUnaryOperator var2) {
      Objects.requireNonNull(var2);
      double var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         double var5 = var2.applyAsDouble((double)var1);
         this.put(var1, var5);
         return var5;
      } else {
         return var3;
      }
   }

   default double computeIfAbsentNullable(float var1, DoubleFunction<? extends Double> var2) {
      Objects.requireNonNull(var2);
      double var3 = this.get(var1);
      double var5 = this.defaultReturnValue();
      if (var3 == var5 && !this.containsKey(var1)) {
         Double var7 = (Double)var2.apply((double)var1);
         if (var7 == null) {
            return var5;
         } else {
            double var8 = var7;
            this.put(var1, var8);
            return var8;
         }
      } else {
         return var3;
      }
   }

   default double computeIfAbsentPartial(float var1, Float2DoubleFunction var2) {
      Objects.requireNonNull(var2);
      double var3 = this.get(var1);
      double var5 = this.defaultReturnValue();
      if (var3 == var5 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var5;
         } else {
            double var7 = var2.get(var1);
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var3;
      }
   }

   default double computeIfPresent(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      double var3 = this.get(var1);
      double var5 = this.defaultReturnValue();
      if (var3 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Double var7 = (Double)var2.apply(var1, var3);
         if (var7 == null) {
            this.remove(var1);
            return var5;
         } else {
            double var8 = var7;
            this.put(var1, var8);
            return var8;
         }
      }
   }

   default double compute(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
      Objects.requireNonNull(var2);
      double var3 = this.get(var1);
      double var5 = this.defaultReturnValue();
      boolean var7 = var3 != var5 || this.containsKey(var1);
      Double var8 = (Double)var2.apply(var1, var7 ? var3 : null);
      if (var8 == null) {
         if (var7) {
            this.remove(var1);
         }

         return var5;
      } else {
         double var9 = var8;
         this.put(var1, var9);
         return var9;
      }
   }

   default double merge(float var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
      Objects.requireNonNull(var4);
      double var5 = this.get(var1);
      double var7 = this.defaultReturnValue();
      double var9;
      if (var5 == var7 && !this.containsKey(var1)) {
         var9 = var2;
      } else {
         Double var11 = (Double)var4.apply(var5, var2);
         if (var11 == null) {
            this.remove(var1);
            return var7;
         }

         var9 = var11;
      }

      this.put(var1, var9);
      return var9;
   }

   /** @deprecated */
   @Deprecated
   default Double getOrDefault(Object var1, Double var2) {
      return (Double)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double putIfAbsent(Float var1, Double var2) {
      return (Double)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Float var1, Double var2, Double var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Double replace(Float var1, Double var2) {
      return (Double)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double computeIfAbsent(Float var1, Function<? super Float, ? extends Double> var2) {
      return (Double)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double computeIfPresent(Float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
      return (Double)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double compute(Float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
      return (Double)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double merge(Float var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
      return (Double)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Float, Double> {
      float getFloatKey();

      /** @deprecated */
      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
      }

      double getDoubleValue();

      double setValue(double var1);

      /** @deprecated */
      @Deprecated
      default Double getValue() {
         return this.getDoubleValue();
      }

      /** @deprecated */
      @Deprecated
      default Double setValue(Double var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Float2DoubleMap.Entry> {
      ObjectIterator<Float2DoubleMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Float2DoubleMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
