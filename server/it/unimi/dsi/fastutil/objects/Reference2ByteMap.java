package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public interface Reference2ByteMap<K> extends Reference2ByteFunction<K>, Map<K, Byte> {
   int size();

   default void clear() {
      throw new UnsupportedOperationException();
   }

   void defaultReturnValue(byte var1);

   byte defaultReturnValue();

   ObjectSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet();

   /** @deprecated */
   @Deprecated
   default ObjectSet<java.util.Map.Entry<K, Byte>> entrySet() {
      return this.reference2ByteEntrySet();
   }

   /** @deprecated */
   @Deprecated
   default Byte put(K var1, Byte var2) {
      return Reference2ByteFunction.super.put(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte get(Object var1) {
      return Reference2ByteFunction.super.get(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte remove(Object var1) {
      return Reference2ByteFunction.super.remove(var1);
   }

   ReferenceSet<K> keySet();

   ByteCollection values();

   boolean containsKey(Object var1);

   boolean containsValue(byte var1);

   /** @deprecated */
   @Deprecated
   default boolean containsValue(Object var1) {
      return var1 == null ? false : this.containsValue((Byte)var1);
   }

   default byte getOrDefault(Object var1, byte var2) {
      byte var3;
      return (var3 = this.getByte(var1)) == this.defaultReturnValue() && !this.containsKey(var1) ? var2 : var3;
   }

   default byte putIfAbsent(K var1, byte var2) {
      byte var3 = this.getByte(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         this.put(var1, var2);
         return var4;
      } else {
         return var3;
      }
   }

   default boolean remove(Object var1, byte var2) {
      byte var3 = this.getByte(var1);
      if (var3 == var2 && (var3 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.removeByte(var1);
         return true;
      } else {
         return false;
      }
   }

   default boolean replace(K var1, byte var2, byte var3) {
      byte var4 = this.getByte(var1);
      if (var4 == var2 && (var4 != this.defaultReturnValue() || this.containsKey(var1))) {
         this.put(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   default byte replace(K var1, byte var2) {
      return this.containsKey(var1) ? this.put(var1, var2) : this.defaultReturnValue();
   }

   default byte computeByteIfAbsent(K var1, ToIntFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.getByte(var1);
      if (var3 == this.defaultReturnValue() && !this.containsKey(var1)) {
         byte var4 = SafeMath.safeIntToByte(var2.applyAsInt(var1));
         this.put(var1, var4);
         return var4;
      } else {
         return var3;
      }
   }

   default byte computeByteIfAbsentPartial(K var1, Reference2ByteFunction<? super K> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.getByte(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         if (!var2.containsKey(var1)) {
            return var4;
         } else {
            byte var5 = var2.getByte(var1);
            this.put(var1, var5);
            return var5;
         }
      } else {
         return var3;
      }
   }

   default byte computeByteIfPresent(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.getByte(var1);
      byte var4 = this.defaultReturnValue();
      if (var3 == var4 && !this.containsKey(var1)) {
         return var4;
      } else {
         Byte var5 = (Byte)var2.apply(var1, var3);
         if (var5 == null) {
            this.removeByte(var1);
            return var4;
         } else {
            byte var6 = var5;
            this.put(var1, var6);
            return var6;
         }
      }
   }

   default byte computeByte(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
      Objects.requireNonNull(var2);
      byte var3 = this.getByte(var1);
      byte var4 = this.defaultReturnValue();
      boolean var5 = var3 != var4 || this.containsKey(var1);
      Byte var6 = (Byte)var2.apply(var1, var5 ? var3 : null);
      if (var6 == null) {
         if (var5) {
            this.removeByte(var1);
         }

         return var4;
      } else {
         byte var7 = var6;
         this.put(var1, var7);
         return var7;
      }
   }

   default byte mergeByte(K var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      Objects.requireNonNull(var3);
      byte var4 = this.getByte(var1);
      byte var5 = this.defaultReturnValue();
      byte var6;
      if (var4 == var5 && !this.containsKey(var1)) {
         var6 = var2;
      } else {
         Byte var7 = (Byte)var3.apply(var4, var2);
         if (var7 == null) {
            this.removeByte(var1);
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
   default Byte putIfAbsent(K var1, Byte var2) {
      return (Byte)super.putIfAbsent(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean remove(Object var1, Object var2) {
      return super.remove(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default boolean replace(K var1, Byte var2, Byte var3) {
      return super.replace(var1, var2, var3);
   }

   /** @deprecated */
   @Deprecated
   default Byte replace(K var1, Byte var2) {
      return (Byte)super.replace(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   default Byte merge(K var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
      return (Byte)super.merge(var1, var2, var3);
   }

   public interface Entry<K> extends java.util.Map.Entry<K, Byte> {
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

   public interface FastEntrySet<K> extends ObjectSet<Reference2ByteMap.Entry<K>> {
      ObjectIterator<Reference2ByteMap.Entry<K>> fastIterator();

      default void fastForEach(Consumer<? super Reference2ByteMap.Entry<K>> var1) {
         this.forEach(var1);
      }
   }
}
