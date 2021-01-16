package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public interface Double2LongMap extends Double2LongFunction, Map<Double, Long> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(long var1);

   long defaultReturnValue();

   ObjectSet<Double2LongMap.Entry> double2LongEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, Long>> entrySet() {
      return this.double2LongEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Long put(Double var1, Long var2) {
      return Double2LongFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long get(Object var1) {
      return Double2LongFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long remove(Object var1) {
      return Double2LongFunction.super.remove(var1);
   }

   DoubleSet keySet();

   LongCollection values();

   boolean containsKey(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Double2LongFunction.super.containsKey(var1);
   }

   boolean containsValue(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Long)var1);
   }

   default long getOrDefault(double var1, long var3) {
      long var5;
      return (var5 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var5;
   }

   default long putIfAbsent(double var1, long var3) {
      long var5 = this.get(var1);
      long var7 = this.defaultReturnValue();
      if (var5 == var7 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var7;
      } else {
         return var5;
      }
   }

   default boolean remove(double var1, long var3) {
      long var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double var1, long var3, long var5) {
      long var7 = this.get(var1);
      if (var7 == var3 && (var7 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var5);
         return true;
      } else {
         return false;
      }
   }

   default long replace(double var1, long var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default long computeIfAbsent(double var1, DoubleToLongFunction var3) {
      Objects.requireNonNull(var3);
      long var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         long var6 = var3.applyAsLong(var1);
         this.put(var1, var6);
         return var6;
      } else {
         return var4;
      }
   }

   default long computeIfAbsentNullable(double var1, DoubleFunction<? extends Long> var3) {
      Objects.requireNonNull(var3);
      long var4 = this.get(var1);
      long var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         Long var8 = (Long)var3.apply(var1);
         if (var8 == null) {
            return var6;
         } else {
            long var9 = var8;
            this.put(var1, var9);
            return var9;
         }
      } else {
         return var4;
      }
   }

   default long computeIfAbsentPartial(double var1, Double2LongFunction var3) {
      Objects.requireNonNull(var3);
      long var4 = this.get(var1);
      long var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var6;
         } else {
            long var8 = var3.get(var1);
            this.put(var1, var8);
            return var8;
         }
      } else {
         return var4;
      }
   }

   default long computeIfPresent(double var1, BiFunction<? super Double, ? super Long, ? extends Long> var3) {
      Objects.requireNonNull(var3);
      long var4 = this.get(var1);
      long var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         return var6;
      } else {
         Long var8 = (Long)var3.apply(var1, var4);
         if (var8 == null) {
            this.remove(var1);
            return var6;
         } else {
            long var9 = var8;
            this.put(var1, var9);
            return var9;
         }
      }
   }

   default long compute(double var1, BiFunction<? super Double, ? super Long, ? extends Long> var3) {
      Objects.requireNonNull(var3);
      long var4 = this.get(var1);
      long var6 = this.defaultReturnValue();
      boolean var8 = var4 != var6 || this.containsKey(var1);
      Long var9 = (Long)var3.apply(var1, var8 ? var4 : null);
      if (var9 == null) {
         if (var8) {
            this.remove(var1);
         }

         return var6;
      } else {
         long var10 = var9;
         this.put(var1, var10);
         return var10;
      }
   }

   default long merge(double var1, long var3, BiFunction<? super Long, ? super Long, ? extends Long> var5) {
      Objects.requireNonNull(var5);
      long var6 = this.get(var1);
      long var8 = this.defaultReturnValue();
      long var10;
      if (var6 == var8 && !this.containsKey(var1)) {
         var10 = var3;
      } else {
         Long var12 = (Long)var5.apply(var6, var3);
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
   default Long getOrDefault(Object var1, Long var2) {
      return (Long)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long putIfAbsent(Double var1, Long var2) {
      return (Long)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Double var1, Long var2, Long var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Long replace(Double var1, Long var2) {
      return (Long)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long computeIfAbsent(Double var1, Function<? super Double, ? extends Long> var2) {
      return (Long)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long computeIfPresent(Double var1, BiFunction<? super Double, ? super Long, ? extends Long> var2) {
      return (Long)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long compute(Double var1, BiFunction<? super Double, ? super Long, ? extends Long> var2) {
      return (Long)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long merge(Double var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
      return (Long)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Double, Long> {
      double getDoubleKey();

      /** @deprecated */
      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
      }

      long getLongValue();

      long setValue(long var1);

      /** @deprecated */
      @Deprecated
      default Long getValue() {
         return this.getLongValue();
      }

      /** @deprecated */
      @Deprecated
      default Long setValue(Long var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Double2LongMap.Entry> {
      ObjectIterator<Double2LongMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Double2LongMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
