package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public interface Char2FloatMap extends Char2FloatFunction, Map<Character, Float> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(float var1);

   float defaultReturnValue();

   ObjectSet<Char2FloatMap.Entry> char2FloatEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Character, Float>> entrySet() {
      return this.char2FloatEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Float put(Character var1, Float var2) {
      return Char2FloatFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      return Char2FloatFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      return Char2FloatFunction.super.remove(var1);
   }

   CharSet keySet();

   FloatCollection values();

   boolean containsKey(char var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Char2FloatFunction.super.containsKey(var1);
   }

   boolean containsValue(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Float)var1);
   }

   default float getOrDefault(char var1, float var2) {
      float var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default float putIfAbsent(char var1, float var2) {
      float var3 = this.get(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(char var1, float var2) {
      float var3 = this.get(var1);
      if (Float.floatToIntBits(var3) == Float.floatToIntBits(var2) && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(char var1, float var2, float var3) {
      float var4 = this.get(var1);
      if (Float.floatToIntBits(var4) == Float.floatToIntBits(var2) && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default float replace(char var1, float var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default float computeIfAbsent(char var1, IntToDoubleFunction var2) {
      Objects.requireNonNull(var2);
      float var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         float var4 = SafeMath.safeDoubleToFloat(var2.applyAsDouble(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default float computeIfAbsentNullable(char var1, IntFunction<? extends Float> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.get(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Float var5 = (Float)var2.apply(var1);
         if (var5 == null) {
            return var4;
         } else {
            float var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var3;
      }
   }

   default float computeIfAbsentPartial(char var1, Char2FloatFunction var2) {
      Objects.requireNonNull(var2);
      float var3 = this.get(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            float var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default float computeIfPresent(char var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.get(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Float var5 = (Float)var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            float var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default float compute(char var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.get(var1);
      float var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Float var6 = (Float)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         float var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default float merge(char var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      Objects.requireNonNull(var3);
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      float var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Float var7 = (Float)var3.apply(var4, var2);
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
   default Float getOrDefault(Object var1, Float var2) {
      return (Float)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float putIfAbsent(Character var1, Float var2) {
      return (Float)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Character var1, Float var2, Float var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Float replace(Character var1, Float var2) {
      return (Float)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float computeIfAbsent(Character var1, Function<? super Character, ? extends Float> var2) {
      return (Float)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float computeIfPresent(Character var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
      return (Float)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float compute(Character var1, BiFunction<? super Character, ? super Float, ? extends Float> var2) {
      return (Float)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float merge(Character var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      return (Float)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Character, Float> {
      char getCharKey();

      /** @deprecated */
      @Deprecated
      default Character getKey() {
         return this.getCharKey();
      }

      float getFloatValue();

      float setValue(float var1);

      /** @deprecated */
      @Deprecated
      default Float getValue() {
         return this.getFloatValue();
      }

      /** @deprecated */
      @Deprecated
      default Float setValue(Float var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Char2FloatMap.Entry> {
      ObjectIterator<Char2FloatMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Char2FloatMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
