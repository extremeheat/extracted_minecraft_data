package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public interface Long2ShortMap extends Long2ShortFunction, Map<Long, Short> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(short var1);

   short defaultReturnValue();

   ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Short>> entrySet() {
      return this.long2ShortEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Short put(Long var1, Short var2) {
      return Long2ShortFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      return Long2ShortFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
      return Long2ShortFunction.super.remove(var1);
   }

   LongSet keySet();

   ShortCollection values();

   boolean containsKey(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Long2ShortFunction.super.containsKey(var1);
   }

   boolean containsValue(short var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Short)var1);
   }

   default short getOrDefault(long var1, short var3) {
      short var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default short putIfAbsent(long var1, short var3) {
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(long var1, short var3) {
      short var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, short var3, short var4) {
      short var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default short replace(long var1, short var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default short computeIfAbsent(long var1, LongToIntFunction var3) {
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

   default short computeIfAbsentNullable(long var1, LongFunction<? extends Short> var3) {
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

   default short computeIfAbsentPartial(long var1, Long2ShortFunction var3) {
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

   default short computeIfPresent(long var1, BiFunction<? super Long, ? super Short, ? extends Short> var3) {
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

   default short compute(long var1, BiFunction<? super Long, ? super Short, ? extends Short> var3) {
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

   default short merge(long var1, short var3, BiFunction<? super Short, ? super Short, ? extends Short> var4) {
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
   default Short putIfAbsent(Long var1, Short var2) {
      return (Short)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Long var1, Short var2, Short var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Short replace(Long var1, Short var2) {
      return (Short)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short computeIfAbsent(Long var1, Function<? super Long, ? extends Short> var2) {
      return (Short)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short computeIfPresent(Long var1, BiFunction<? super Long, ? super Short, ? extends Short> var2) {
      return (Short)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short compute(Long var1, BiFunction<? super Long, ? super Short, ? extends Short> var2) {
      return (Short)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short merge(Long var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      return (Short)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Long, Short> {
      long getLongKey();

      /** @deprecated */
      @Deprecated
      default Long getKey() {
         return this.getLongKey();
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

   public interface FastEntrySet extends ObjectSet<Long2ShortMap.Entry> {
      ObjectIterator<Long2ShortMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2ShortMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
