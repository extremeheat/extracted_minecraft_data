package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public interface Int2ByteMap extends Int2ByteFunction, Map<Integer, Byte> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(byte var1);

   byte defaultReturnValue();

   ObjectSet<Int2ByteMap.Entry> int2ByteEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<Integer, Byte>> entrySet() {
      return this.int2ByteEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Byte put(Integer var1, Byte var2) {
      return Int2ByteFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte get(Object var1) {
      return Int2ByteFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte remove(Object var1) {
      return Int2ByteFunction.super.remove(var1);
   }

   IntSet keySet();

   ByteCollection values();

   boolean containsKey(int var1);

   /** @deprecated */
   @Deprecated
   default boolean containsKey(Object var1) {
      return Int2ByteFunction.super.containsKey(var1);
   }

   boolean containsValue(byte var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Byte)var1);
   }

   default byte getOrDefault(int var1, byte var2) {
      byte var3;
      return (var3 = this.get(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default byte putIfAbsent(int var1, byte var2) {
      byte var3 = this.get(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(int var1, byte var2) {
      byte var3 = this.get(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.remove(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(int var1, byte var2, byte var3) {
      byte var4 = this.get(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default byte replace(int var1, byte var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default byte computeIfAbsent(int var1, IntUnaryOperator var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.get(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         byte var4 = SafeMath.safeIntToByte(var2.applyAsInt(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default byte computeIfAbsentNullable(int var1, IntFunction<? extends Byte> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.get(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         Byte var5 = (Byte)var2.apply(var1);
         if (var5 == null) {
            return var4;
         } else {
            byte var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      } else {
         return var3;
      }
   }

   default byte computeIfAbsentPartial(int var1, Int2ByteFunction var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.get(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            byte var5 = var2.get(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default byte computeIfPresent(int var1, BiFunction<? super Integer, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.get(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Byte var5 = (Byte)var2.apply(var1, var3);
         if (var5 == null) {
            this.remove(var1);
            return var4;
         } else {
            byte var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default byte compute(int var1, BiFunction<? super Integer, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.get(var1);
      byte var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Byte var6 = (Byte)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.remove(var1);
         }

         return var4;
      } else {
         byte var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default byte merge(int var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.get(var1);
      byte var5 = this.defaultReturnValue();
      byte var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Byte var7 = (Byte)var3.apply(var4, var2);
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
   default Byte getOrDefault(Object var1, Byte var2) {
      return (Byte)super.getOrDefault(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte putIfAbsent(Integer var1, Byte var2) {
      return (Byte)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(Integer var1, Byte var2, Byte var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Byte replace(Integer var1, Byte var2) {
      return (Byte)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte computeIfAbsent(Integer var1, Function<? super Integer, ? extends Byte> var2) {
      return (Byte)super.computeIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Byte, ? extends Byte> var2) {
      return (Byte)super.computeIfPresent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte compute(Integer var1, BiFunction<? super Integer, ? super Byte, ? extends Byte> var2) {
      return (Byte)super.compute(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte merge(Integer var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      return (Byte)super.merge(var1, var2, var3);
   }

   public interface Entry extends java.util.Map.Entry<Integer, Byte> {
      int getIntKey();

      /** @deprecated */
      @Deprecated
      default Integer getKey() {
         return this.getIntKey();
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

   public interface FastEntrySet extends ObjectSet<Int2ByteMap.Entry> {
      ObjectIterator<Int2ByteMap.Entry> fastIterator();

      default void fastForEach(Consumer<? super Int2ByteMap.Entry> var1) {
         this.forEach(var1);
      }
   }
}
