package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public interface Double2DoubleMap extends Double2DoubleFunction, Map<Double, Double> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(double var1);

   double defaultReturnValue();

   ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, Double>> entrySet() {
      return this.double2DoubleEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Double put(Double var1, Double var2) {
      return Double2DoubleFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double get(Object var1) {
      return Double2DoubleFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double remove(Object var1) {
      return Double2DoubleFunction.super.remove(var1);
   }

   DoubleSet keySet();

   DoubleCollection values();

   boolean containsKey(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Double2DoubleFunction.super.containsKey(var1);
   }

   boolean containsValue(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Double)var1);
   }

   default double getOrDefault(double var1, double var3) {
      double var5;
      return (var5 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var5;
   }

   default double putIfAbsent(double var1, double var3) {
      double var5 = this.get(var1);
      double var7 = this.defaultReturnValue();
      if (var5 == var7 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var7;
      } else {
         return var5;
      }
   }

   default boolean remove(double var1, double var3) {
      double var5 = this.get(var1);
      if (Double.doubleToLongBits(var5) == Double.doubleToLongBits(var3) && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double var1, double var3, double var5) {
      double var7 = this.get(var1);
      if (Double.doubleToLongBits(var7) == Double.doubleToLongBits(var3) && (var7 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var5);
         return true;
      } else {
         return false;
      }
   }

   default double replace(double var1, double var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default double computeIfAbsent(double var1, DoubleUnaryOperator var3) {
      Objects.requireNonNull(var3);
      double var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         double var6 = var3.applyAsDouble(var1);
         this.put(var1, var6);
         return var6;
      } else {
         return var4;
      }
   }

   default double computeIfAbsentNullable(double var1, DoubleFunction<? extends Double> var3) {
      Objects.requireNonNull(var3);
      double var4 = this.get(var1);
      double var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         Double var8 = (Double)var3.apply(var1);
         if (var8 == null) {
            return var6;
         } else {
            double var9 = var8;
            this.put(var1, var9);
            return var9;
         }
      } else {
         return var4;
      }
   }

   default double computeIfAbsentPartial(double var1, Double2DoubleFunction var3) {
      Objects.requireNonNull(var3);
      double var4 = this.get(var1);
      double var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var6;
         } else {
            double var8 = var3.get(var1);
            this.put(var1, var8);
            return var8;
         }
      } else {
         return var4;
      }
   }

   default double computeIfPresent(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
      Objects.requireNonNull(var3);
      double var4 = this.get(var1);
      double var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         return var6;
      } else {
         Double var8 = (Double)var3.apply(var1, var4);
         if (var8 == null) {
            this.remove(var1);
            return var6;
         } else {
            double var9 = var8;
            this.put(var1, var9);
            return var9;
         }
      }
   }

   default double compute(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
      Objects.requireNonNull(var3);
      double var4 = this.get(var1);
      double var6 = this.defaultReturnValue();
      boolean var8 = var4 != var6 || this.containsKey(var1);
      Double var9 = (Double)var3.apply(var1, var8 ? var4 : null);
      if (var9 == null) {
         if (var8) {
            this.remove(var1);
         }

         return var6;
      } else {
         double var10 = var9;
         this.put(var1, var10);
         return var10;
      }
   }

   default double merge(double var1, double var3, BiFunction<? super Double, ? super Double, ? extends Double> var5) {
      Objects.requireNonNull(var5);
      double var6 = this.get(var1);
      double var8 = this.defaultReturnValue();
      double var10;
      if (var6 == var8 && !this.containsKey(var1)) {
         var10 = var3;
      } else {
         Double var12 = (Double)var5.apply(var6, var3);
         if (var12 == null) {
            this.remove(var1);
            return var8;
         }

         var10 = var12;
      }

      this.put(var1, var10);
      return var10;
   }

   /** @deprecated */
   @Deprecated
   default Double getOrDefault(Object var1, Double var2) {
      return (Double)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double putIfAbsent(Double var1, Double var2) {
      return (Double)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Double var1, Double var2, Double var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Double replace(Double var1, Double var2) {
      return (Double)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double computeIfAbsent(Double var1, Function<? super Double, ? extends Double> var2) {
      return (Double)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double computeIfPresent(Double var1, BiFunction<? super Double, ? super Double, ? extends Double> var2) {
      return (Double)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double compute(Double var1, BiFunction<? super Double, ? super Double, ? extends Double> var2) {
      return (Double)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Double merge(Double var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
      return (Double)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Double, Double> {
      double getDoubleKey();

      /** @deprecated */
      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
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

   public interface FastEntrySet extends ObjectSet<Double2DoubleMap.Entry> {
      ObjectIterator<Double2DoubleMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Double2DoubleMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
