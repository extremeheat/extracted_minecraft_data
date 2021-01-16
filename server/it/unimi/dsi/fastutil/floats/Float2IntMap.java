package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public interface Float2IntMap extends Float2IntFunction, Map<Float, Integer> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(int var1);

   int defaultReturnValue();

   ObjectSet<Float2IntMap.Entry> float2IntEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Float, Integer>> entrySet() {
      return this.float2IntEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(Float var1, Integer var2) {
      return Float2IntFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      return Float2IntFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
      return Float2IntFunction.super.remove(var1);
   }

   FloatSet keySet();

   IntCollection values();

   boolean containsKey(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Float2IntFunction.super.containsKey(var1);
   }

   boolean containsValue(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Integer)var1);
   }

   default int getOrDefault(float var1, int var2) {
      int var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default int putIfAbsent(float var1, int var2) {
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(float var1, int var2) {
      int var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(float var1, int var2, int var3) {
      int var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default int replace(float var1, int var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default int computeIfAbsent(float var1, DoubleToIntFunction var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         int var4 = var2.applyAsInt((double)var1);
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default int computeIfAbsentNullable(float var1, DoubleFunction<? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.get(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Integer var5 = (Integer)var2.apply((double)var1);
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

   default int computeIfAbsentPartial(float var1, Float2IntFunction var2) {
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

   default int computeIfPresent(float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
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

   default int compute(float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
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

   default int merge(float var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
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
   default Integer putIfAbsent(Float var1, Integer var2) {
      return (Integer)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Float var1, Integer var2, Integer var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Integer replace(Float var1, Integer var2) {
      return (Integer)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer computeIfAbsent(Float var1, Function<? super Float, ? extends Integer> var2) {
      return (Integer)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer computeIfPresent(Float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
      return (Integer)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer compute(Float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
      return (Integer)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer merge(Float var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      return (Integer)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Float, Integer> {
      float getFloatKey();

      /** @deprecated */
      @Deprecated
      default Float getKey() {
         return this.getFloatKey();
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

   public interface FastEntrySet extends ObjectSet<Float2IntMap.Entry> {
      ObjectIterator<Float2IntMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Float2IntMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
