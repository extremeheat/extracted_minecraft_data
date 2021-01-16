package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Object2IntMap<K> extends Object2IntFunction<K>, Map<K, Integer> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(int var1);

   int defaultReturnValue();

   ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Integer>> entrySet() {
      return this.object2IntEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Integer put(K var1, Integer var2) {
      return Object2IntFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer get(Object var1) {
      return Object2IntFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer remove(Object var1) {
      return Object2IntFunction.super.remove(var1);
   }

   ObjectSet<K> keySet();

   IntCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Integer)var1);
   }

   default int getOrDefault(Object var1, int var2) {
      int var3;
      return (var3 = this.getInt(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default int putIfAbsent(K var1, int var2) {
      int var3 = this.getInt(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(Object var1, int var2) {
      int var3 = this.getInt(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.removeInt(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, int var2, int var3) {
      int var4 = this.getInt(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default int replace(K var1, int var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default int computeIntIfAbsent(K var1, ToIntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.getInt(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         int var4 = var2.applyAsInt(var1);
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default int computeIntIfAbsentPartial(K var1, Object2IntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.getInt(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            int var5 = var2.getInt(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default int computeIntIfPresent(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.getInt(var1);
      int var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Integer var5 = (Integer)var2.apply(var1, var3);
         if (var5 == null) {
            this.removeInt(var1);
            return var4;
         } else {
            int var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default int computeInt(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
      Objects.requireNonNull(var2);
      int var3 = this.getInt(var1);
      int var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Integer var6 = (Integer)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.removeInt(var1);
         }

         return var4;
      } else {
         int var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default int mergeInt(K var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      Objects.requireNonNull(var3);
      int var4 = this.getInt(var1);
      int var5 = this.defaultReturnValue();
      int var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Integer var7 = (Integer)var3.apply(var4, var2);
         if (var7 == null) {
            this.removeInt(var1);
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
   default Integer putIfAbsent(K var1, Integer var2) {
      return (Integer)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K var1, Integer var2, Integer var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Integer replace(K var1, Integer var2) {
      return (Integer)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Integer merge(K var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
      return (Integer)super.merge(var1, var2, var3);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Integer> {
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

   public interface FastEntrySet<K> extends ObjectSet<Object2IntMap.Entry<K>> {
      ObjectIterator<Object2IntMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2IntMap.Entry<K>> var1) {
         this.forEach(var1);
      }
   }
}
