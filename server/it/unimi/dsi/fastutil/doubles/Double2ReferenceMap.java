package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public interface Double2ReferenceMap<V> extends Double2ReferenceFunction<V>, Map<Double, V> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(V var1);

   V defaultReturnValue();

   ObjectSet<Double2ReferenceMap.Entry<V>> double2ReferenceEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, V>> entrySet() {
      return this.double2ReferenceEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default V put(Double var1, V var2) {
      return Double2ReferenceFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V get(Object var1) {
      return Double2ReferenceFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default V remove(Object var1) {
      return Double2ReferenceFunction.super.remove(var1);
   }

   DoubleSet keySet();

   ReferenceCollection<V> values();

   boolean containsKey(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Double2ReferenceFunction.super.containsKey(var1);
   }

   default V getOrDefault(double var1, V var3) {
      Object var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default V putIfAbsent(double var1, V var3) {
      Object var4 = this.get(var1);
      Object var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(double var1, Object var3) {
      Object var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double var1, V var3, V var4) {
      Object var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default V replace(double var1, V var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default V computeIfAbsent(double var1, DoubleFunction<? extends V> var3) {
      Objects.requireNonNull(var3);
      Object var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         Object var5 = var3.apply(var1);
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default V computeIfAbsentPartial(double var1, Double2ReferenceFunction<? extends V> var3) {
      Objects.requireNonNull(var3);
      Object var4 = this.get(var1);
      Object var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            Object var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default V computeIfPresent(double var1, BiFunction<? super Double, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      Object var4 = this.get(var1);
      Object var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Object var6 = var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default V compute(double var1, BiFunction<? super Double, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      Object var4 = this.get(var1);
      Object var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Object var7 = var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         this.put(var1, var7);
         return var7;
      }
   }

   default V merge(double var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
      Objects.requireNonNull(var4);
      Objects.requireNonNull(var3);
      Object var5 = this.get(var1);
      Object var6 = this.defaultReturnValue();
      Object var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Object var8 = var4.apply(var5, var3);
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
   default V getOrDefault(Object var1, V var2) {
      return super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V putIfAbsent(Double var1, V var2) {
      return super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Double var1, V var2, V var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default V replace(Double var1, V var2) {
      return super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V computeIfAbsent(Double var1, Function<? super Double, ? extends V> var2) {
      return super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V computeIfPresent(Double var1, BiFunction<? super Double, ? super V, ? extends V> var2) {
      return super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V compute(Double var1, BiFunction<? super Double, ? super V, ? extends V> var2) {
      return super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V merge(Double var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      return super.merge(var1, var2, var3);
   }

   public interface Entry<V> extends java.util.Map.Entry<Double, V> {
      double getDoubleKey();

      /** @deprecated */
      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
      }
   }

   public interface FastEntrySet<V> extends ObjectSet<Double2ReferenceMap.Entry<V>> {
      ObjectIterator<Double2ReferenceMap.Entry<V>> fastIterator();

      default void fastForEach(Consumer<? super Double2ReferenceMap.Entry<V>> var1) {
         this.forEach(var1);
      }
   }
}
