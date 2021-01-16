package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;

public interface Long2ObjectMap<V> extends Long2ObjectFunction<V>, Map<Long, V> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(V var1);

   V defaultReturnValue();

   ObjectSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, V>> entrySet() {
      return this.long2ObjectEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default V put(Long var1, V var2) {
      return Long2ObjectFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V get(Object var1) {
      return Long2ObjectFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default V remove(Object var1) {
      return Long2ObjectFunction.super.remove(var1);
   }

   LongSet keySet();

   ObjectCollection<V> values();

   boolean containsKey(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Long2ObjectFunction.super.containsKey(var1);
   }

   default V getOrDefault(long var1, V var3) {
      Object var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default V putIfAbsent(long var1, V var3) {
      Object var4 = this.get(var1);
      Object var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(long var1, Object var3) {
      Object var4 = this.get(var1);
      if (Objects.equals(var4, var3) && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, V var3, V var4) {
      Object var5 = this.get(var1);
      if (Objects.equals(var5, var3) && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default V replace(long var1, V var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default V computeIfAbsent(long var1, LongFunction<? extends V> var3) {
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

   default V computeIfAbsentPartial(long var1, Long2ObjectFunction<? extends V> var3) {
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

   default V computeIfPresent(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
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

   default V compute(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
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

   default V merge(long var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
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
   default V putIfAbsent(Long var1, V var2) {
      return super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Long var1, V var2, V var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default V replace(Long var1, V var2) {
      return super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V computeIfAbsent(Long var1, Function<? super Long, ? extends V> var2) {
      return super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V computeIfPresent(Long var1, BiFunction<? super Long, ? super V, ? extends V> var2) {
      return super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V compute(Long var1, BiFunction<? super Long, ? super V, ? extends V> var2) {
      return super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default V merge(Long var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
      return super.merge(var1, var2, var3);
   }

   public interface Entry<V> extends java.util.Map.Entry<Long, V> {
      long getLongKey();

      /** @deprecated */
      @Deprecated
      default Long getKey() {
         return this.getLongKey();
      }
   }

   public interface FastEntrySet<V> extends ObjectSet<Long2ObjectMap.Entry<V>> {
      ObjectIterator<Long2ObjectMap.Entry<V>> fastIterator();

      default void fastForEach(Consumer<? super Long2ObjectMap.Entry<V>> var1) {
         this.forEach(var1);
      }
   }
}
