package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public interface Double2ByteMap extends Double2ByteFunction, Map<Double, Byte> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(byte var1);

   byte defaultReturnValue();

   ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Double, Byte>> entrySet() {
      return this.double2ByteEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Byte put(Double var1, Byte var2) {
      return Double2ByteFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte get(Object var1) {
      return Double2ByteFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte remove(Object var1) {
      return Double2ByteFunction.super.remove(var1);
   }

   DoubleSet keySet();

   ByteCollection values();

   boolean containsKey(double var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Double2ByteFunction.super.containsKey(var1);
   }

   boolean containsValue(byte var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Byte)var1);
   }

   default byte getOrDefault(double var1, byte var3) {
      byte var4;
      return (var4 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var3 : var4;
   }

   default byte putIfAbsent(double var1, byte var3) {
      byte var4 = this.get(var1);
      byte var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         this.put(var1, var3);
         return var5;
      } else {
         return var4;
      }
   }

   default boolean remove(double var1, byte var3) {
      byte var4 = this.get(var1);
      if (var4 == var3 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(double var1, byte var3, byte var4) {
      byte var5 = this.get(var1);
      if (var5 == var3 && (var5 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var4);
         return true;
      } else {
         return false;
      }
   }

   default byte replace(double var1, byte var3) {
      return this.containsKey(var1) ? this.put(var1, var3) : this.defaultReturnValue();
   }

   default byte computeIfAbsent(double var1, DoubleToIntFunction var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.get(var1);
      if (var4 == this.defaultReturnValue() && !this.containsKey(var1)) {
         byte var5 = SafeMath.safeIntToByte(var3.applyAsInt(var1));
         this.put(var1, var5);
         return var5;
      } else {
         return var4;
      }
   }

   default byte computeIfAbsentNullable(double var1, DoubleFunction<? extends Byte> var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.get(var1);
      byte var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         Byte var6 = (Byte)var3.apply(var1);
         if (var6 == null) {
            return var5;
         } else {
            byte var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      } else {
         return var4;
      }
   }

   default byte computeIfAbsentPartial(double var1, Double2ByteFunction var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.get(var1);
      byte var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         if (!var3.containsKey(var1)) {
            return var5;
         } else {
            byte var6 = var3.get(var1);
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var4;
      }
   }

   default byte computeIfPresent(double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.get(var1);
      byte var5 = this.defaultReturnValue();
      if (var4 == var5 && !this.containsKey(var1)) {
         return var5;
      } else {
         Byte var6 = (Byte)var3.apply(var1, var4);
         if (var6 == null) {
            this.remove(var1);
            return var5;
         } else {
            byte var7 = var6;
            this.put(var1, var7);
            return var7;
         }
      }
   }

   default byte compute(double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.get(var1);
      byte var5 = this.defaultReturnValue();
      boolean var6 = var4 != var5 || this.containsKey(var1);
      Byte var7 = (Byte)var3.apply(var1, var6 ? var4 : null);
      if (var7 == null) {
         if (var6) {
            this.remove(var1);
         }

         return var5;
      } else {
         byte var8 = var7;
         this.put(var1, var8);
         return var8;
      }
   }

   default byte merge(double var1, byte var3, BiFunction<? super Byte, ? super Byte, ? extends Byte> var4) {
      Objects.requireNonNull(var4);
      byte var5 = this.get(var1);
      byte var6 = this.defaultReturnValue();
      byte var7;
      if (var5 == var6 && !this.containsKey(var1)) {
         var7 = var3;
      } else {
         Byte var8 = (Byte)var4.apply(var5, var3);
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
   default Byte getOrDefault(Object var1, Byte var2) {
      return (Byte)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte putIfAbsent(Double var1, Byte var2) {
      return (Byte)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Double var1, Byte var2, Byte var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Byte replace(Double var1, Byte var2) {
      return (Byte)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte computeIfAbsent(Double var1, Function<? super Double, ? extends Byte> var2) {
      return (Byte)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte computeIfPresent(Double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var2) {
      return (Byte)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte compute(Double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var2) {
      return (Byte)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte merge(Double var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      return (Byte)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Double, Byte> {
      double getDoubleKey();

      /** @deprecated */
      @Deprecated
      default Double getKey() {
         return this.getDoubleKey();
      }

      byte getByteValue();

      byte setValue(byte var1);

      /** @deprecated */
      @Deprecated
      default Byte getValue() {
         return this.getByteValue();
      }

      /** @deprecated */
      @Deprecated
      default Byte setValue(Byte var1) {
         return this.setValue(var1);
      }
   }

   public interface FastEntrySet extends ObjectSet<Double2ByteMap.Entry> {
      ObjectIterator<Double2ByteMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Double2ByteMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
