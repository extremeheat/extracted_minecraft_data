package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public interface Double2ShortMap extends Double2ShortFunction, Map<Double, Short> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(short var1);

   short defaultReturnValue();

   ObjectSet<Double2ShortMap.Entry> double2ShortEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, Short>> entrySet() {
      return this.double2ShortEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Short put(Double var1, Short var2) {
      return Double2ShortFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      return Double2ShortFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
      return Double2ShortFunction.super.remove(var1);
   }

   DoubleSet keySet();

   ShortCollection values();

   boolean containsKey(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Double2ShortFunction.super.containsKey(var1);
   }

   boolean containsValue(short var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Short)var1);
   }

   default short getOrDefault(double var1, short var3) {
      short var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default short putIfAbsent(double var1, short var3) {
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(double var1, short var3) {
      short var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double var1, short var3, short var4) {
      short var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default short replace(double var1, short var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default short computeIfAbsent(double var1, DoubleToIntFunction var3) {
      Objects.requireNonNull(var3);
      short var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         short var5 = SafeMath.safeIntToShort(var3.applyAsInt(var1));
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default short computeIfAbsentNullable(double var1, DoubleFunction<? extends Short> var3) {
      Objects.requireNonNull(var3);
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         Short var6 = (Short)var3.apply(var1);
         if (var6 == null) {
            return var5;
         } else {
            short var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var4;
      }
   }

   default short computeIfAbsentPartial(double var1, Double2ShortFunction var3) {
      Objects.requireNonNull(var3);
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            short var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default short computeIfPresent(double var1, BiFunction<? super Double, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Short var6 = (Short)var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            short var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      }
   }

   default short compute(double var1, BiFunction<? super Double, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Short var7 = (Short)var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         short var8 = var7;
         this.put(var1, var8);
         return var8;
      }
   }

   default short merge(double var1, short var3, BiFunction<? super Short, ? super Short, ? extends Short> var4) {
      Objects.requireNonNull(var4);
      short var5 = this.get(var1);
      short var6 = this.defaultReturnValue();
      short var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Short var8 = (Short)var4.apply(var5, var3);
         if (var8 == null) {
            this.remove(var1);
            return var6;
         }

         var7 = var8;
      }

      this.put(var1, var7);
      return var7;
   }

   /** @deprecated */
   @Deprecated
   default Short getOrDefault(Object var1, Short var2) {
      return (Short)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short putIfAbsent(Double var1, Short var2) {
      return (Short)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Double var1, Short var2, Short var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Short replace(Double var1, Short var2) {
      return (Short)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short computeIfAbsent(Double var1, Function<? super Double, ? extends Short> var2) {
      return (Short)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short computeIfPresent(Double var1, BiFunction<? super Double, ? super Short, ? extends Short> var2) {
      return (Short)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short compute(Double var1, BiFunction<? super Double, ? super Short, ? extends Short> var2) {
      return (Short)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short merge(Double var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      return (Short)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Double, Short> {
      double getDoubleKey();

      /** @deprecated */
      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
      }

      short getShortValue();

      short setValue(short var1);

      /** @deprecated */
      @Deprecated
      default Short getValue() {
         return this.getShortValue();
      }

      /** @deprecated */
      @Deprecated
      default Short setValue(Short var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Double2ShortMap.Entry> {
      ObjectIterator<Double2ShortMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Double2ShortMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
