package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Int2ShortMap extends Int2ShortFunction, Map<Integer, Short> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(short var1);

   short defaultReturnValue();

   ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Integer, Short>> entrySet() {
      return this.int2ShortEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Short put(Integer var1, Short var2) {
      return Int2ShortFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      return Int2ShortFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
      return Int2ShortFunction.super.remove(var1);
   }

   IntSet keySet();

   ShortCollection values();

   boolean containsKey(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Int2ShortFunction.super.containsKey(var1);
   }

   boolean containsValue(short var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Short)var1);
   }

   default short getOrDefault(int var1, short var2) {
      short var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default short putIfAbsent(int var1, short var2) {
      short var3 = this.get(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(int var1, short var2) {
      short var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(int var1, short var2, short var3) {
      short var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default short replace(int var1, short var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default short computeIfAbsent(int var1, IntUnaryOperator var2) {
      Objects.requireNonNull(var2);
      short var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         short var4 = SafeMath.safeIntToShort(var2.applyAsInt(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default short computeIfAbsentNullable(int var1, IntFunction<? extends Short> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.get(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Short var5 = (Short)var2.apply(var1);
         if (var5 == null) {
            return var4;
         } else {
            short var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var3;
      }
   }

   default short computeIfAbsentPartial(int var1, Int2ShortFunction var2) {
      Objects.requireNonNull(var2);
      short var3 = this.get(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            short var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default short computeIfPresent(int var1, BiFunction<? super Integer, ? super Short, ? extends Short> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.get(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Short var5 = (Short)var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            short var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default short compute(int var1, BiFunction<? super Integer, ? super Short, ? extends Short> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.get(var1);
      short var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Short var6 = (Short)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         short var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default short merge(int var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      short var4 = this.get(var1);
      short var5 = this.defaultReturnValue();
      short var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Short var7 = (Short)var3.apply(var4, var2);
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
   default Short getOrDefault(Object var1, Short var2) {
      return (Short)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short putIfAbsent(Integer var1, Short var2) {
      return (Short)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Integer var1, Short var2, Short var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Short replace(Integer var1, Short var2) {
      return (Short)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short computeIfAbsent(Integer var1, Function<? super Integer, ? extends Short> var2) {
      return (Short)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Short, ? extends Short> var2) {
      return (Short)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short compute(Integer var1, BiFunction<? super Integer, ? super Short, ? extends Short> var2) {
      return (Short)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short merge(Integer var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      return (Short)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Integer, Short> {
      int getIntKey();

      /** @deprecated */
      @Deprecated
      default Integer getKey() {
         return this.getIntKey();
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

   public interface FastEntrySet extends ObjectSet<Int2ShortMap.Entry> {
      ObjectIterator<Int2ShortMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Int2ShortMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
