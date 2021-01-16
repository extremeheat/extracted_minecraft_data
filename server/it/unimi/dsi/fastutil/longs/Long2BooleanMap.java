package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;

public interface Long2BooleanMap extends Long2BooleanFunction, Map<Long, Boolean> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(boolean var1);

   boolean defaultReturnValue();

   ObjectSet<Long2BooleanMap.Entry> long2BooleanEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Boolean>> entrySet() {
      return this.long2BooleanEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Boolean put(Long var1, Boolean var2) {
      return Long2BooleanFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean get(Object var1) {
      return Long2BooleanFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean remove(Object var1) {
      return Long2BooleanFunction.super.remove(var1);
   }

   LongSet keySet();

   BooleanCollection values();

   boolean containsKey(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Long2BooleanFunction.super.containsKey(var1);
   }

   boolean containsValue(boolean var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Boolean)var1);
   }

   default boolean getOrDefault(long var1, boolean var3) {
      boolean var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default boolean putIfAbsent(long var1, boolean var3) {
      boolean var4 = this.get(var1);
      boolean var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(long var1, boolean var3) {
      boolean var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, boolean var3, boolean var4) {
      boolean var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, boolean var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default boolean computeIfAbsent(long var1, LongPredicate var3) {
      Objects.requireNonNull(var3);
      boolean var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         boolean var5 = var3.test(var1);
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean computeIfAbsentNullable(long var1, LongFunction<? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      boolean var4 = this.get(var1);
      boolean var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         Boolean var6 = (Boolean)var3.apply(var1);
         if (var6 == null) {
            return var5;
         } else {
            boolean var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var4;
      }
   }

   default boolean computeIfAbsentPartial(long var1, Long2BooleanFunction var3) {
      Objects.requireNonNull(var3);
      boolean var4 = this.get(var1);
      boolean var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            boolean var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default boolean computeIfPresent(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      boolean var4 = this.get(var1);
      boolean var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Boolean var6 = (Boolean)var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            boolean var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      }
   }

   default boolean compute(long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var3) {
      Objects.requireNonNull(var3);
      boolean var4 = this.get(var1);
      boolean var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Boolean var7 = (Boolean)var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         boolean var8 = var7;
         this.put(var1, var8);
         return var8;
      }
   }

   default boolean merge(long var1, boolean var3, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var4) {
      Objects.requireNonNull(var4);
      boolean var5 = this.get(var1);
      boolean var6 = this.defaultReturnValue();
      boolean var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Boolean var8 = (Boolean)var4.apply(var5, var3);
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
   default Boolean getOrDefault(Object var1, Boolean var2) {
      return (Boolean)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean putIfAbsent(Long var1, Boolean var2) {
      return (Boolean)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Long var1, Boolean var2, Boolean var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Boolean replace(Long var1, Boolean var2) {
      return (Boolean)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean computeIfAbsent(Long var1, Function<? super Long, ? extends Boolean> var2) {
      return (Boolean)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean computeIfPresent(Long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var2) {
      return (Boolean)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean compute(Long var1, BiFunction<? super Long, ? super Boolean, ? extends Boolean> var2) {
      return (Boolean)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Boolean merge(Long var1, Boolean var2, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> var3) {
      return (Boolean)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Long, Boolean> {
      long getLongKey();

      /** @deprecated */
      @Deprecated
      default Long getKey() {
         return this.getLongKey();
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

   public interface FastEntrySet extends ObjectSet<Long2BooleanMap.Entry> {
      ObjectIterator<Long2BooleanMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2BooleanMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
