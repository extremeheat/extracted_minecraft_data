package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public interface Double2FloatMap extends Double2FloatFunction, Map<Double, Float> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(float var1);

   float defaultReturnValue();

   ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, Float>> entrySet() {
      return this.double2FloatEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Float put(Double var1, Float var2) {
      return Double2FloatFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float get(Object var1) {
      return Double2FloatFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float remove(Object var1) {
      return Double2FloatFunction.super.remove(var1);
   }

   DoubleSet keySet();

   FloatCollection values();

   boolean containsKey(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Double2FloatFunction.super.containsKey(var1);
   }

   boolean containsValue(float var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Float)var1);
   }

   default float getOrDefault(double var1, float var3) {
      float var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default float putIfAbsent(double var1, float var3) {
      float var4 = this.get(var1);
      float var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(double var1, float var3) {
      float var4 = this.get(var1);
      if (Float.floatToIntBits(var4) == Float.floatToIntBits(var3) && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double var1, float var3, float var4) {
      float var5 = this.get(var1);
      if (Float.floatToIntBits(var5) == Float.floatToIntBits(var3) && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default float replace(double var1, float var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default float computeIfAbsent(double var1, DoubleUnaryOperator var3) {
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

   default float computeIfAbsentNullable(double var1, DoubleFunction<? extends Float> var3) {
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

   default float computeIfAbsentPartial(double var1, Double2FloatFunction var3) {
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

   default float computeIfPresent(double var1, BiFunction<? super Double, ? super Float, ? extends Float> var3) {
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

   default float compute(double var1, BiFunction<? super Double, ? super Float, ? extends Float> var3) {
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

   default float merge(double var1, float var3, BiFunction<? super Float, ? super Float, ? extends Float> var4) {
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
   default Float putIfAbsent(Double var1, Float var2) {
      return (Float)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Double var1, Float var2, Float var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Float replace(Double var1, Float var2) {
      return (Float)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float computeIfAbsent(Double var1, Function<? super Double, ? extends Float> var2) {
      return (Float)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float computeIfPresent(Double var1, BiFunction<? super Double, ? super Float, ? extends Float> var2) {
      return (Float)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float compute(Double var1, BiFunction<? super Double, ? super Float, ? extends Float> var2) {
      return (Float)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Float merge(Double var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
      return (Float)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Double, Float> {
      double getDoubleKey();

      /** @deprecated */
      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
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

   public interface FastEntrySet extends ObjectSet<Double2FloatMap.Entry> {
      ObjectIterator<Double2FloatMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Double2FloatMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
