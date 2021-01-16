package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public interface Long2IntMap extends Long2IntFunction, Map<Long, Integer> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(int var1);

   int defaultReturnValue();

   ObjectSet<Long2IntMap.Entry> long2IntEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Integer>> entrySet() {
      return this.long2IntEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(Long var1, Integer var2) {
      return Long2IntFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      return Long2IntFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
      return Long2IntFunction.super.remove(var1);
   }

   LongSet keySet();

   IntCollection values();

   boolean containsKey(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Long2IntFunction.super.containsKey(var1);
   }

   boolean containsValue(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Integer)var1);
   }

   default int getOrDefault(long var1, int var3) {
      int var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default int putIfAbsent(long var1, int var3) {
      int var4 = this.get(var1);
      int var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(long var1, int var3) {
      int var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, int var3, int var4) {
      int var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default int replace(long var1, int var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default int computeIfAbsent(long var1, LongToIntFunction var3) {
      Objects.requireNonNull(var3);
      int var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         int var5 = var3.applyAsInt(var1);
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default int computeIfAbsentNullable(long var1, LongFunction<? extends Integer> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.get(var1);
      int var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         Integer var6 = (Integer)var3.apply(var1);
         if (var6 == null) {
            return var5;
         } else {
            int var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var4;
      }
   }

   default int computeIfAbsentPartial(long var1, Long2IntFunction var3) {
      Objects.requireNonNull(var3);
      int var4 = this.get(var1);
      int var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            int var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default int computeIfPresent(long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.get(var1);
      int var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Integer var6 = (Integer)var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            int var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      }
   }

   default int compute(long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.get(var1);
      int var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Integer var7 = (Integer)var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         int var8 = var7;
         this.put(var1, var8);
         return var8;
      }
   }

   default int merge(long var1, int var3, BiFunction<? super Integer, ? super Integer, ? extends Integer> var4) {
      Objects.requireNonNull(var4);
      int var5 = this.get(var1);
      int var6 = this.defaultReturnValue();
      int var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Integer var8 = (Integer)var4.apply(var5, var3);
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
   default Integer getOrDefault(Object var1, Integer var2) {
      return (Integer)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer putIfAbsent(Long var1, Integer var2) {
      return (Integer)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Long var1, Integer var2, Integer var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Integer replace(Long var1, Integer var2) {
      return (Integer)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer computeIfAbsent(Long var1, Function<? super Long, ? extends Integer> var2) {
      return (Integer)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer computeIfPresent(Long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var2) {
      return (Integer)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer compute(Long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var2) {
      return (Integer)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer merge(Long var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      return (Integer)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Long, Integer> {
      long getLongKey();

      /** @deprecated */
      @Deprecated
      default Long getKey() {
         return this.getLongKey();
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

   public interface FastEntrySet extends ObjectSet<Long2IntMap.Entry> {
      ObjectIterator<Long2IntMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2IntMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
