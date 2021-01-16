package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Reference2ShortMap<K> extends Reference2ShortFunction<K>, Map<K, Short> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(short var1);

   short defaultReturnValue();

   ObjectSet<Reference2ShortMap.Entry<K>> reference2ShortEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Short>> entrySet() {
      return this.reference2ShortEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Short put(K var1, Short var2) {
      return Reference2ShortFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short get(Object var1) {
      return Reference2ShortFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short remove(Object var1) {
      return Reference2ShortFunction.super.remove(var1);
   }

   ReferenceSet<K> keySet();

   ShortCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(short var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Short)var1);
   }

   default short getOrDefault(Object var1, short var2) {
      short var3;
      return (var3 = this.getShort(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default short putIfAbsent(K var1, short var2) {
      short var3 = this.getShort(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(Object var1, short var2) {
      short var3 = this.getShort(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.removeShort(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, short var2, short var3) {
      short var4 = this.getShort(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default short replace(K var1, short var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default short computeShortIfAbsent(K var1, ToIntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.getShort(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         short var4 = SafeMath.safeIntToShort(var2.applyAsInt(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default short computeShortIfAbsentPartial(K var1, Reference2ShortFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.getShort(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            short var5 = var2.getShort(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default short computeShortIfPresent(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.getShort(var1);
      short var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Short var5 = (Short)var2.apply(var1, var3);
         if (var5 == null) {
            this.removeShort(var1);
            return var4;
         } else {
            short var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default short computeShort(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
      Objects.requireNonNull(var2);
      short var3 = this.getShort(var1);
      short var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Short var6 = (Short)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.removeShort(var1);
         }

         return var4;
      } else {
         short var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default short mergeShort(K var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      Objects.requireNonNull(var3);
      short var4 = this.getShort(var1);
      short var5 = this.defaultReturnValue();
      short var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Short var7 = (Short)var3.apply(var4, var2);
         if (var7 == null) {
            this.removeShort(var1);
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
   default Short putIfAbsent(K var1, Short var2) {
      return (Short)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K var1, Short var2, Short var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Short replace(K var1, Short var2) {
      return (Short)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Short merge(K var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
      return (Short)super.merge(var1, var2, var3);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Short> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2ShortMap.Entry<K>> {
      ObjectIterator<Reference2ShortMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2ShortMap.Entry<K>> var1) {
         this.forEach(var1);
      }
   }
}
