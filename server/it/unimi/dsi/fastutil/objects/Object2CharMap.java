package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.chars.CharCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Object2CharMap<K> extends Object2CharFunction<K>, Map<K, Character> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(char var1);

   char defaultReturnValue();

   ObjectSet<Object2CharMap.Entry<K>> object2CharEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Character>> entrySet() {
      return this.object2CharEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Character put(K var1, Character var2) {
      return Object2CharFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character get(Object var1) {
      return Object2CharFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character remove(Object var1) {
      return Object2CharFunction.super.remove(var1);
   }

   ObjectSet<K> keySet();

   CharCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(char var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Character)var1);
   }

   default char getOrDefault(Object var1, char var2) {
      char var3;
      return (var3 = this.getChar(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default char putIfAbsent(K var1, char var2) {
      char var3 = this.getChar(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(Object var1, char var2) {
      char var3 = this.getChar(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.removeChar(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, char var2, char var3) {
      char var4 = this.getChar(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default char replace(K var1, char var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default char computeCharIfAbsent(K var1, ToIntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.getChar(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         char var4 = SafeMath.safeIntToChar(var2.applyAsInt(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default char computeCharIfAbsentPartial(K var1, Object2CharFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.getChar(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            char var5 = var2.getChar(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default char computeCharIfPresent(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.getChar(var1);
      char var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Character var5 = (Character)var2.apply(var1, var3);
         if (var5 == null) {
            this.removeChar(var1);
            return var4;
         } else {
            char var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default char computeChar(K var1, BiFunction<? super K, ? super Character, ? extends Character> var2) {
      Objects.requireNonNull(var2);
      char var3 = this.getChar(var1);
      char var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Character var6 = (Character)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.removeChar(var1);
         }

         return var4;
      } else {
         char var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default char mergeChar(K var1, char var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
      Objects.requireNonNull(var3);
      char var4 = this.getChar(var1);
      char var5 = this.defaultReturnValue();
      char var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Character var7 = (Character)var3.apply(var4, var2);
         if (var7 == null) {
            this.removeChar(var1);
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
   default Character putIfAbsent(K var1, Character var2) {
      return (Character)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K var1, Character var2, Character var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Character replace(K var1, Character var2) {
      return (Character)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Character merge(K var1, Character var2, BiFunction<? super Character, ? super Character, ? extends Character> var3) {
      return (Character)super.merge(var1, var2, var3);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Character> {
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

   public interface FastEntrySet<K> extends ObjectSet<Object2CharMap.Entry<K>> {
      ObjectIterator<Object2CharMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Object2CharMap.Entry<K>> var1) {
         this.forEach(var1);
      }
   }
}
