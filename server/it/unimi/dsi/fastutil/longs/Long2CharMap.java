package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public interface Long2CharMap extends Long2CharFunction, Map<Long, Character> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(char var1);

   char defaultReturnValue();

   ObjectSet<Long2CharMap.Entry> long2CharEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Character>> entrySet() {
      return this.long2CharEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Character put(Long var1, Character var2) {
      return Long2CharFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character get(Object var1) {
      return Long2CharFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character remove(Object var1) {
      return Long2CharFunction.super.remove(var1);
   }

   LongSet keySet();

   CharCollection values();

   boolean containsKey(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Long2CharFunction.super.containsKey(var1);
   }

   boolean containsValue(char var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Character)var1);
   }

   default char getOrDefault(long var1, char var3) {
      char var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default char putIfAbsent(long var1, char var3) {
      char var4 = this.get(var1);
      char var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(long var1, char var3) {
      char var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, char var3, char var4) {
      char var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default char replace(long var1, char var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default char computeIfAbsent(long var1, LongToIntFunction var3) {
      Objects.requireNonNull(var3);
      char var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         char var5 = SafeMath.safeIntToChar(var3.applyAsInt(var1));
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default char computeIfAbsentNullable(long var1, LongFunction<? extends Character> var3) {
      Objects.requireNonNull(var3);
      char var4 = this.get(var1);
      char var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         Character var6 = (Character)var3.apply(var1);
         if (var6 == null) {
            return var5;
         } else {
            char var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var4;
      }
   }

   default char computeIfAbsentPartial(long var1, Long2CharFunction var3) {
      Objects.requireNonNull(var3);
      char var4 = this.get(var1);
      char var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            char var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default char computeIfPresent(long var1, BiFunction<? super Long, ? super Character, ? extends Character> var3) {
      Objects.requireNonNull(var3);
      char var4 = this.get(var1);
      char var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Character var6 = (Character)var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            char var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      }
   }

   default char compute(long var1, BiFunction<? super Long, ? super Character, ? extends Character> var3) {
      Objects.requireNonNull(var3);
      char var4 = this.get(var1);
      char var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Character var7 = (Character)var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         char var8 = var7;
         this.put(var1, var8);
         return var8;
      }
   }

   default char merge(long var1, char var3, BiFunction<? super Character, ? super Character, ? extends Character> var4) {
      Objects.requireNonNull(var4);
      char var5 = this.get(var1);
      char var6 = this.defaultReturnValue();
      char var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Character var8 = (Character)var4.apply(var5, var3);
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
   default Character getOrDefault(Object var1, Character var2) {
      return (Character)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character putIfAbsent(Long var1, Character var2) {
      return (Character)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Long var1, Character var2, Character var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Character replace(Long var1, Character var2) {
      return (Character)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character computeIfAbsent(Long var1, Function<? super Long, ? extends Character> var2) {
      return (Character)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character computeIfPresent(Long var1, BiFunction<? super Long, ? super Character, ? extends Character> var2) {
      return (Character)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character compute(Long var1, BiFunction<? super Long, ? super Character, ? extends Character> var2) {
      return (Character)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character merge(Long var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
      return (Character)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Long, Character> {
      long getLongKey();

      /** @deprecated */
      @Deprecated
      default Long getKey() {
         return this.getLongKey();
      }

      char getCharValue();

      char setValue(char var1);

      /** @deprecated */
      @Deprecated
      default Character getValue() {
         return this.getCharValue();
      }

      /** @deprecated */
      @Deprecated
      default Character setValue(Character var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Long2CharMap.Entry> {
      ObjectIterator<Long2CharMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2CharMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
