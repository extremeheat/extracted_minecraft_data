package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2ByteMaps {
   public static final Float2ByteMaps.EmptyMap EMPTY_MAP = new Float2ByteMaps.EmptyMap();

   private Float2ByteMaps() {
      super();
   }

   public static ObjectIterator<Float2ByteMap.Entry> fastIterator(Float2ByteMap var0) {
      ObjectSet var1 = var0.float2ByteEntrySet();
      return var1 instanceof Float2ByteMap.FastEntrySet ? ((Float2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Float2ByteMap var0, Consumer<? super Float2ByteMap.Entry> var1) {
      ObjectSet var2 = var0.float2ByteEntrySet();
      if (var2 instanceof Float2ByteMap.FastEntrySet) {
         ((Float2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Float2ByteMap.Entry> fastIterable(Float2ByteMap var0) {
      final ObjectSet var1 = var0.float2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Float2ByteMap.FastEntrySet ? new ObjectIterable<Float2ByteMap.Entry>() {
         public ObjectIterator<Float2ByteMap.Entry> iterator() {
            return ((Float2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Float2ByteMap.Entry> var1x) {
            ((Float2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Float2ByteMap singleton(float var0, byte var1) {
      return new Float2ByteMaps.Singleton(var0, var1);
   }

   public static Float2ByteMap singleton(Float var0, Byte var1) {
      return new Float2ByteMaps.Singleton(var0, var1);
   }

   public static Float2ByteMap synchronize(Float2ByteMap var0) {
      return new Float2ByteMaps.SynchronizedMap(var0);
   }

   public static Float2ByteMap synchronize(Float2ByteMap var0, Object var1) {
      return new Float2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static Float2ByteMap unmodifiable(Float2ByteMap var0) {
      return new Float2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Float2ByteFunctions.UnmodifiableFunction implements Float2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteMap map;
      protected transient ObjectSet<Float2ByteMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Float2ByteMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(byte var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Float, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ByteCollection values() {
         return this.values == null ? ByteCollections.unmodifiable(this.map.values()) : this.values;
      }

      public boolean isEmpty() {
         return this.map.isEmpty();
      }

      public int hashCode() {
         return this.map.hashCode();
      }

      public boolean equals(Object var1) {
         return var1 == this ? true : this.map.equals(var1);
      }

      public byte getOrDefault(float var1, byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Float, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Float, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(float var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte replace(float var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(float var1, byte var2, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsent(float var1, DoubleToIntFunction var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentNullable(float var1, DoubleFunction<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentPartial(float var1, Float2ByteFunction var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfPresent(float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte compute(float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte merge(float var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte getOrDefault(Object var1, Byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte replace(Float var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Float var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Float var1, Function<? super Float, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Float var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Float2ByteFunctions.SynchronizedFunction implements Float2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteMap map;
      protected transient ObjectSet<Float2ByteMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Float2ByteMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Float2ByteMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(byte var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Float, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      public FloatSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ByteCollection values() {
         synchronized(this.sync) {
            return this.values == null ? ByteCollections.synchronize(this.map.values(), this.sync) : this.values;
         }
      }

      public boolean isEmpty() {
         synchronized(this.sync) {
            return this.map.isEmpty();
         }
      }

      public int hashCode() {
         synchronized(this.sync) {
            return this.map.hashCode();
         }
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            synchronized(this.sync) {
               return this.map.equals(var1);
            }
         }
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         synchronized(this.sync) {
            var1.defaultWriteObject();
         }
      }

      public byte getOrDefault(float var1, byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Float, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Float, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(float var1, byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(float var1, byte var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public byte replace(float var1, byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(float var1, byte var2, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public byte computeIfAbsent(float var1, DoubleToIntFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public byte computeIfAbsentNullable(float var1, DoubleFunction<? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public byte computeIfAbsentPartial(float var1, Float2ByteFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public byte computeIfPresent(float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public byte compute(float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public byte merge(float var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte getOrDefault(Object var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte replace(Float var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Float var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Float var1, Function<? super Float, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Float var1, BiFunction<? super Float, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Float var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Float2ByteFunctions.Singleton implements Float2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2ByteMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ByteCollection values;

      protected Singleton(float var1, byte var2) {
         super(var1, var2);
      }

      public boolean containsValue(byte var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Byte)var1 == this.value;
      }

      public void putAll(Map<? extends Float, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
         }

         return this.keys;
      }

      public ByteCollection values() {
         if (this.values == null) {
            this.values = ByteSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ this.value;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Map)) {
            return false;
         } else {
            Map var2 = (Map)var1;
            return var2.size() != 1 ? false : ((Entry)var2.entrySet().iterator().next()).equals(this.entrySet().iterator().next());
         }
      }

      public String toString() {
         return "{" + this.key + "=>" + this.value + "}";
      }
   }

   public static class EmptyMap extends Float2ByteFunctions.EmptyFunction implements Float2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(byte var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Float, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Float2ByteMaps.EMPTY_MAP;
      }

      public boolean isEmpty() {
         return true;
      }

      public int hashCode() {
         return 0;
      }

      public boolean equals(Object var1) {
         return !(var1 instanceof Map) ? false : ((Map)var1).isEmpty();
      }

      public String toString() {
         return "{}";
      }
   }
}
