package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongToDoubleFunction;

public interface Long2FloatMap extends Long2FloatFunction, Map<Long, Float> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(float var1);

   float defaultReturnValue();

   ObjectSet<Long2FloatMap.Entry> long2FloatEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Long, Float>> entrySet() {
      return this.long2FloatEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Float put(Long var1, Float var2) {
      return Long2FloatFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      return Long2FloatFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      return Long2FloatFunction.super.remove(var1);
   }

   LongSet keySet();

   FloatCollection values();

   boolean containsKey(long var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Long2FloatFunction.super.containsKey(var1);
   }

   boolean containsValue(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Float)var1);
   }

   default float getOrDefault(long var1, float var3) {
      float var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default float putIfAbsent(long var1, float var3) {
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(long var1, float var3) {
      float var4 = this.get(var1);
      if (Float.floatToIntBits(var4) == Float.floatToIntBits(var3) && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(long var1, float var3, float var4) {
      float var5 = this.get(var1);
      if (Float.floatToIntBits(var5) == Float.floatToIntBits(var3) && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default float replace(long var1, float var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default float computeIfAbsent(long var1, LongToDoubleFunction var3) {
      Objects.requireNonNull(var3);
      float var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         float var5 = SafeMath.safeDoubleToFloat(var3.applyAsDouble(var1));
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default float computeIfAbsentNullable(long var1, LongFunction<? extends Float> var3) {
      Objects.requireNonNull(var3);
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         Float var6 = (Float)var3.apply(var1);
         if (var6 == null) {
            return var5;
         } else {
            float var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var4;
      }
   }

   default float computeIfAbsentPartial(long var1, Long2FloatFunction var3) {
      Objects.requireNonNull(var3);
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            float var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default float computeIfPresent(long var1, BiFunction<? super Long, ? super Float, ? extends Float> var3) {
      Objects.requireNonNull(var3);
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Float var6 = (Float)var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            float var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      }
   }

   default float compute(long var1, BiFunction<? super Long, ? super Float, ? extends Float> var3) {
      Objects.requireNonNull(var3);
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Float var7 = (Float)var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         float var8 = var7;
         this.put(var1, var8);
         return var8;
      }
   }

   default float merge(long var1, float var3, BiFunction<? super Float, ? super Float, ? extends Float> var4) {
      Objects.requireNonNull(var4);
      float var5 = this.get(var1);
      float var6 = this.defaultReturnValue();
      float var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Float var8 = (Float)var4.apply(var5, var3);
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
   default Float getOrDefault(Object var1, Float var2) {
      return (Float)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float putIfAbsent(Long var1, Float var2) {
      return (Float)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Long var1, Float var2, Float var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Float replace(Long var1, Float var2) {
      return (Float)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float computeIfAbsent(Long var1, Function<? super Long, ? extends Float> var2) {
      return (Float)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float computeIfPresent(Long var1, BiFunction<? super Long, ? super Float, ? extends Float> var2) {
      return (Float)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float compute(Long var1, BiFunction<? super Long, ? super Float, ? extends Float> var2) {
      return (Float)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float merge(Long var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      return (Float)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Long, Float> {
      long getLongKey();

      /** @deprecated */
      @Deprecated
      default Long getKey() {
         return this.getLongKey();
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

   public interface FastEntrySet extends ObjectSet<Long2FloatMap.Entry> {
      ObjectIterator<Long2FloatMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Long2FloatMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
