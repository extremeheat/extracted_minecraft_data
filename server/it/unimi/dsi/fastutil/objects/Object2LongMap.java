package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

public interface Object2LongMap<K> extends Object2LongFunction<K>, Map<K, Long> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(long var1);

   long defaultReturnValue();

   ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Long>> entrySet() {
      return this.object2LongEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Long put(K var1, Long var2) {
      return Object2LongFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long get(Object var1) {
      return Object2LongFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long remove(Object var1) {
      return Object2LongFunction.super.remove(var1);
   }

   ObjectSet<K> keySet();

   LongCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Long)var1);
   }

   default long getOrDefault(Object var1, long var2) {
      long var4;
      return (var4 = this.getLong(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var4;
   }

   default long putIfAbsent(K var1, long var2) {
      long var4 = this.getLong(var1);
      long var6 = this.defaultReturnValue();
      if (var4 == var6 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var6;
      } else {
         return var4;
      }
   }

   default boolean remove(Object var1, long var2) {
      long var4 = this.getLong(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.removeLong(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, long var2, long var4) {
      long var6 = this.getLong(var1);
      if (var6 == var2 && (var6 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default long replace(K var1, long var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default long computeLongIfAbsent(K var1, ToLongFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      long var3 = this.getLong(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         long var5 = var2.applyAsLong(var1);
         this.put(var1, var5);
         return var5;
      } else {
         return var3;
      }
   }

   default long computeLongIfAbsentPartial(K var1, Object2LongFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      long var3 = this.getLong(var1);
      long var5 = this.defaultReturnValue();
      if (var3 == var5 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var5;
         } else {
            long var7 = var2.getLong(var1);
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var3;
      }
   }

   default long computeLongIfPresent(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
      Objects.requireNonNull(var2);
      long var3 = this.getLong(var1);
      long var5 = this.defaultReturnValue();
      if (var3 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Long var7 = (Long)var2.apply(var1, var3);
         if (var7 == null) {
            this.removeLong(var1);
            return var5;
         } else {
            long var8 = var7;
            this.put(var1, var8);
            return var8;
         }
      }
   }

   default long computeLong(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
      Objects.requireNonNull(var2);
      long var3 = this.getLong(var1);
      long var5 = this.defaultReturnValue();
      boolean var7 = var3 != var5 || this.containsKey(var1);
      Long var8 = (Long)var2.apply(var1, var7 ? var3 : null);
      if (var8 == null) {
         if (var7) {
            this.removeLong(var1);
         }

         return var5;
      } else {
         long var9 = var8;
         this.put(var1, var9);
         return var9;
      }
   }

   default long mergeLong(K var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
      Objects.requireNonNull(var4);
      long var5 = this.getLong(var1);
      long var7 = this.defaultReturnValue();
      long var9;
      if (var5 == var7 && !this.containsKey(var1)) {
         var9 = var2;
      } else {
         Long var11 = (Long)var4.apply(var5, var2);
         if (var11 == null) {
            this.removeLong(var1);
            return var7;
         }

         var9 = var11;
      }

      this.put(var1, var9);
      return var9;
   }

   /** @deprecated */
   @Deprecated
   default Long getOrDefault(Object var1, Long var2) {
      return (Long)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long putIfAbsent(K var1, Long var2) {
      return (Long)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K var1, Long var2, Long var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Long replace(K var1, Long var2) {
      return (Long)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Long merge(K var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
      return (Long)super.merge(var1, var2, var3);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Long> {
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

   public interface FastEntrySet<K> extends ObjectSet<Object2LongMap.Entry<K>> {
      ObjectIterator<Object2LongMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2LongMap.Entry<K>> var1) {
         this.forEach(var1);
      }
   }
}
