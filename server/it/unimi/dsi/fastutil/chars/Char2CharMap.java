package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Char2CharMap extends Char2CharFunction, Map<Character, Character> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(char var1);

   char defaultReturnValue();

   ObjectSet<Char2CharMap.Entry> char2CharEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Character, Character>> entrySet() {
      return this.char2CharEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Character put(Character var1, Character var2) {
      return Char2CharFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character get(Object var1) {
      return Char2CharFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character remove(Object var1) {
      return Char2CharFunction.super.remove(var1);
   }

   CharSet keySet();

   CharCollection values();

   boolean containsKey(char var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Char2CharFunction.super.containsKey(var1);
   }

   boolean containsValue(char var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Character)var1);
   }

   default char getOrDefault(char var1, char var2) {
      char var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default char putIfAbsent(char var1, char var2) {
      char var3 = this.get(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(char var1, char var2) {
      char var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(char var1, char var2, char var3) {
      char var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default char replace(char var1, char var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default char computeIfAbsent(char var1, IntUnaryOperator var2) {
      Objects.requireNonNull(var2);
      char var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         char var4 = SafeMath.safeIntToChar(var2.applyAsInt(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default char computeIfAbsentNullable(char var1, IntFunction<? extends Character> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.get(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Character var5 = (Character)var2.apply(var1);
         if (var5 == null) {
            return var4;
         } else {
            char var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var3;
      }
   }

   default char computeIfAbsentPartial(char var1, Char2CharFunction var2) {
      Objects.requireNonNull(var2);
      char var3 = this.get(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            char var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default char computeIfPresent(char var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.get(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Character var5 = (Character)var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            char var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default char compute(char var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.get(var1);
      char var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Character var6 = (Character)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         char var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default char merge(char var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
      Objects.requireNonNull(var3);
      char var4 = this.get(var1);
      char var5 = this.defaultReturnValue();
      char var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Character var7 = (Character)var3.apply(var4, var2);
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
   default Character getOrDefault(Object var1, Character var2) {
      return (Character)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character putIfAbsent(Character var1, Character var2) {
      return (Character)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Character var1, Character var2, Character var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Character replace(Character var1, Character var2) {
      return (Character)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character computeIfAbsent(Character var1, Function<? super Character, ? extends Character> var2) {
      return (Character)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character computeIfPresent(Character var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
      return (Character)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character compute(Character var1, BiFunction<? super Character, ? super Character, ? extends Character> var2) {
      return (Character)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character merge(Character var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
      return (Character)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Character, Character> {
      char getCharKey();

      /** @deprecated */
      @Deprecated
      default Character getKey() {
         return this.getCharKey();
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

   public interface FastEntrySet extends ObjectSet<Char2CharMap.Entry> {
      ObjectIterator<Char2CharMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Char2CharMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
