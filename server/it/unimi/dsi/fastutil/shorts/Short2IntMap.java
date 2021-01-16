package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Short2IntMap extends Short2IntFunction, Map<Short, Integer> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(int var1);

   int defaultReturnValue();

   ObjectSet<Short2IntMap.Entry> short2IntEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Short, Integer>> entrySet() {
      return this.short2IntEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(Short var1, Integer var2) {
      return Short2IntFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      return Short2IntFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
      return Short2IntFunction.super.remove(var1);
   }

   ShortSet keySet();

   IntCollection values();

   boolean containsKey(short var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Short2IntFunction.super.containsKey(var1);
   }

   boolean containsValue(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Integer)var1);
   }

   default int getOrDefault(short var1, int var2) {
      int var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default int putIfAbsent(short var1, int var2) {
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(short var1, int var2) {
      int var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(short var1, int var2, int var3) {
      int var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default int replace(short var1, int var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default int computeIfAbsent(short var1, IntUnaryOperator var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         int var4 = var2.applyAsInt(var1);
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default int computeIfAbsentNullable(short var1, IntFunction<? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Integer var5 = (Integer)var2.apply(var1);
         if (var5 == null) {
            return var4;
         } else {
            int var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var3;
      }
   }

   default int computeIfAbsentPartial(short var1, Short2IntFunction var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            int var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default int computeIfPresent(short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Integer var5 = (Integer)var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            int var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default int compute(short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Integer var6 = (Integer)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         int var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default int merge(short var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.get(var1);
      int var5 = this.defaultReturnValue();
      int var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Integer var7 = (Integer)var3.apply(var4, var2);
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
   default Integer getOrDefault(Object var1, Integer var2) {
      return (Integer)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer putIfAbsent(Short var1, Integer var2) {
      return (Integer)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Short var1, Integer var2, Integer var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Integer replace(Short var1, Integer var2) {
      return (Integer)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer computeIfAbsent(Short var1, Function<? super Short, ? extends Integer> var2) {
      return (Integer)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer computeIfPresent(Short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
      return (Integer)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer compute(Short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
      return (Integer)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer merge(Short var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      return (Integer)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Short, Integer> {
      short getShortKey();

      /** @deprecated */
      @Deprecated
      default Short getKey() {
         return this.getShortKey();
      }

      int getIntValue();

      int setValue(int var1);

      /** @deprecated */
      @Deprecated
      default Integer getValue() {
         return this.getIntValue();
      }

      /** @deprecated */
      @Deprecated
      default Integer setValue(Integer var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Short2IntMap.Entry> {
      ObjectIterator<Short2IntMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Short2IntMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
