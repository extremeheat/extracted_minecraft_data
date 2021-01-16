package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

public interface Reference2FloatMap<K> extends Reference2FloatFunction<K>, Map<K, Float> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(float var1);

   float defaultReturnValue();

   ObjectSet<Reference2FloatMap.Entry<K>> reference2FloatEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Float>> entrySet() {
      return this.reference2FloatEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Float put(K var1, Float var2) {
      return Reference2FloatFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      return Reference2FloatFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      return Reference2FloatFunction.super.remove(var1);
   }

   ReferenceSet<K> keySet();

   FloatCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Float)var1);
   }

   default float getOrDefault(Object var1, float var2) {
      float var3;
      return (var3 = this.getFloat(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default float putIfAbsent(K var1, float var2) {
      float var3 = this.getFloat(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(Object var1, float var2) {
      float var3 = this.getFloat(var1);
      if (Float.floatToIntBits(var3) == Float.floatToIntBits(var2) && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.removeFloat(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, float var2, float var3) {
      float var4 = this.getFloat(var1);
      if (Float.floatToIntBits(var4) == Float.floatToIntBits(var2) && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default float replace(K var1, float var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default float computeFloatIfAbsent(K var1, ToDoubleFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.getFloat(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         float var4 = SafeMath.safeDoubleToFloat(var2.applyAsDouble(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default float computeFloatIfAbsentPartial(K var1, Reference2FloatFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.getFloat(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            float var5 = var2.getFloat(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default float computeFloatIfPresent(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.getFloat(var1);
      float var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Float var5 = (Float)var2.apply(var1, var3);
         if (var5 == null) {
            this.removeFloat(var1);
            return var4;
         } else {
            float var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default float computeFloat(K var1, BiFunction<? super K, ? super Float, ? extends Float> var2) {
      Objects.requireNonNull(var2);
      float var3 = this.getFloat(var1);
      float var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Float var6 = (Float)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.removeFloat(var1);
         }

         return var4;
      } else {
         float var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default float mergeFloat(K var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      Objects.requireNonNull(var3);
      float var4 = this.getFloat(var1);
      float var5 = this.defaultReturnValue();
      float var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Float var7 = (Float)var3.apply(var4, var2);
         if (var7 == null) {
            this.removeFloat(var1);
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
   default Float putIfAbsent(K var1, Float var2) {
      return (Float)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K var1, Float var2, Float var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Float replace(K var1, Float var2) {
      return (Float)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float merge(K var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      return (Float)super.merge(var1, var2, var3);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Float> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2FloatMap.Entry<K>> {
      ObjectIterator<Reference2FloatMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2FloatMap.Entry<K>> var1) {
         this.forEach(var1);
      }
   }
}
