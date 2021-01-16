package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public interface Int2BooleanMap extends Int2BooleanFunction, Map<Integer, Boolean> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(boolean var1);

   boolean defaultReturnValue();

   ObjectSet<Int2BooleanMap.Entry> int2BooleanEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Integer, Boolean>> entrySet() {
      return this.int2BooleanEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Boolean put(Integer var1, Boolean var2) {
      return Int2BooleanFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean get(Object var1) {
      return Int2BooleanFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean remove(Object var1) {
      return Int2BooleanFunction.super.remove(var1);
   }

   IntSet keySet();

   BooleanCollection values();

   boolean containsKey(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Int2BooleanFunction.super.containsKey(var1);
   }

   boolean containsValue(boolean var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Boolean)var1);
   }

   default boolean getOrDefault(int var1, boolean var2) {
      boolean var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default boolean putIfAbsent(int var1, boolean var2) {
      boolean var3 = this.get(var1);
      boolean var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(int var1, boolean var2) {
      boolean var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(int var1, boolean var2, boolean var3) {
      boolean var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(int var1, boolean var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default boolean computeIfAbsent(int var1, IntPredicate var2) {
      Objects.requireNonNull(var2);
      boolean var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         boolean var4 = var2.test(var1);
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean computeIfAbsentNullable(int var1, IntFunction<? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      boolean var3 = this.get(var1);
      boolean var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Boolean var5 = (Boolean)var2.apply(var1);
         if (var5 == null) {
            return var4;
         } else {
            boolean var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var3;
      }
   }

   default boolean computeIfAbsentPartial(int var1, Int2BooleanFunction var2) {
      Objects.requireNonNull(var2);
      boolean var3 = this.get(var1);
      boolean var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            boolean var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default boolean computeIfPresent(int var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      boolean var3 = this.get(var1);
      boolean var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Boolean var5 = (Boolean)var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            boolean var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default boolean compute(int var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      Objects.requireNonNull(var2);
      boolean var3 = this.get(var1);
      boolean var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Boolean var6 = (Boolean)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         boolean var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default boolean merge(int var1, boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      boolean var4 = this.get(var1);
      boolean var5 = this.defaultReturnValue();
      boolean var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Boolean var7 = (Boolean)var3.apply(var4, var2);
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
   default Boolean getOrDefault(Object var1, Boolean var2) {
      return (Boolean)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean putIfAbsent(Integer var1, Boolean var2) {
      return (Boolean)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Integer var1, Boolean var2, Boolean var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Boolean replace(Integer var1, Boolean var2) {
      return (Boolean)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean computeIfAbsent(Integer var1, Function<? super Integer, ? extends Boolean> var2) {
      return (Boolean)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      return (Boolean)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean compute(Integer var1, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> var2) {
      return (Boolean)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean merge(Integer var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
      return (Boolean)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Integer, Boolean> {
      int getIntKey();

      /** @deprecated */
      @Deprecated
      default Integer getKey() {
         return this.getIntKey();
      }

      boolean getBooleanValue();

      boolean setValue(boolean var1);

      /** @deprecated */
      @Deprecated
      default Boolean getValue() {
         return this.getBooleanValue();
      }

      /** @deprecated */
      @Deprecated
      default Boolean setValue(Boolean var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Int2BooleanMap.Entry> {
      ObjectIterator<Int2BooleanMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Int2BooleanMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
