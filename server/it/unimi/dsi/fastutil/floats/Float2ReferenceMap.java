package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public interface Float2ReferenceMap<V> extends Float2ReferenceFunction<V>, Map<Float, V> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(V var1);

   V defaultReturnValue();

   ObjectSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, V>> entrySet() {
      return this.float2ReferenceEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default V put(Float var1, V var2) {
      return Float2ReferenceFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V get(Object var1) {
      return Float2ReferenceFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default V remove(Object var1) {
      return Float2ReferenceFunction.super.remove(var1);
   }

   FloatSet keySet();

   ReferenceCollection<V> values();

   boolean containsKey(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Float2ReferenceFunction.super.containsKey(var1);
   }

   default V getOrDefault(float var1, V var2) {
      Object var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default V putIfAbsent(float var1, V var2) {
      Object var3 = this.get(var1);
      Object var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(float var1, Object var2) {
      Object var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float var1, V var2, V var3) {
      Object var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default V replace(float var1, V var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default V computeIfAbsent(float var1, DoubleFunction<? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         Object var4 = var2.apply((double)var1);
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default V computeIfAbsentPartial(float var1, Float2ReferenceFunction<? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3 = this.get(var1);
      Object var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            Object var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default V computeIfPresent(float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3 = this.get(var1);
      Object var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Object var5 = var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            this.put(var1, var5);
            return var5;
         }
      }
   }

   default V compute(float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
      Objects.requireNonNull(var2);
      Object var3 = this.get(var1);
      Object var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Object var6 = var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         this.put(var1, var6);
         return var6;
      }
   }

   default V merge(float var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      Objects.requireNonNull(var3);
      Objects.requireNonNull(var2);
      Object var4 = this.get(var1);
      Object var5 = this.defaultReturnValue();
      Object var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Object var7 = var3.apply(var4, var2);
         if (var7 == null) {
            this.remove(var1);
            return var5;
         }

         var6 = var7;
      }

      this.put(var1, var6);
      return var6;
   }

   /** @deprecated */
   @Deprecated
   default V getOrDefault(Object var1, V var2) {
      return super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V putIfAbsent(Float var1, V var2) {
      return super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Float var1, V var2, V var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default V replace(Float var1, V var2) {
      return super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V computeIfAbsent(Float var1, Function<? super Float, ? extends V> var2) {
      return super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V computeIfPresent(Float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
      return super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V compute(Float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
      return super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V merge(Float var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      return super.merge(var1, var2, var3);
   }

   public interface Entry<V> extends java.util.Map.Entry<Float, V> {
      float getFloatKey();

      /** @deprecated */
      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
      }
   }

   public interface FastEntrySet<V> extends ObjectSet<Float2ReferenceMap.Entry<V>> {
      ObjectIterator<Float2ReferenceMap.Entry<V>> fastIterator();

      default void fastForEach(Consumer<? super Float2ReferenceMap.Entry<V>> var1) {
         this.forEach(var1);
      }
   }
}
