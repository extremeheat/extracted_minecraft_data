package it.unimi.dsi.fastutil.doubles;

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

public final class Double2ByteMaps {
   public static final Double2ByteMaps.EmptyMap EMPTY_MAP = new Double2ByteMaps.EmptyMap();

   private Double2ByteMaps() {
      super();
   }

   public static ObjectIterator<Double2ByteMap.Entry> fastIterator(Double2ByteMap var0) {
      ObjectSet var1 = var0.double2ByteEntrySet();
      return var1 instanceof Double2ByteMap.FastEntrySet ? ((Double2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2ByteMap var0, Consumer<? super Double2ByteMap.Entry> var1) {
      ObjectSet var2 = var0.double2ByteEntrySet();
      if (var2 instanceof Double2ByteMap.FastEntrySet) {
         ((Double2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2ByteMap.Entry> fastIterable(Double2ByteMap var0) {
      final ObjectSet var1 = var0.double2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Double2ByteMap.FastEntrySet ? new ObjectIterable<Double2ByteMap.Entry>() {
         public ObjectIterator<Double2ByteMap.Entry> iterator() {
            return ((Double2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2ByteMap.Entry> var1x) {
            ((Double2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2ByteMap singleton(double var0, byte var2) {
      return new Double2ByteMaps.Singleton(var0, var2);
   }

   public static Double2ByteMap singleton(Double var0, Byte var1) {
      return new Double2ByteMaps.Singleton(var0, var1);
   }

   public static Double2ByteMap synchronize(Double2ByteMap var0) {
      return new Double2ByteMaps.SynchronizedMap(var0);
   }

   public static Double2ByteMap synchronize(Double2ByteMap var0, Object var1) {
      return new Double2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static Double2ByteMap unmodifiable(Double2ByteMap var0) {
      return new Double2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2ByteFunctions.UnmodifiableFunction implements Double2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteMap map;
      protected transient ObjectSet<Double2ByteMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Double2ByteMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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

      public byte getOrDefault(double var1, byte var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(double var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte replace(double var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, byte var3, byte var4) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsent(double var1, DoubleToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentNullable(double var1, DoubleFunction<? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentPartial(double var1, Double2ByteFunction var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfPresent(double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public byte compute(double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public byte merge(double var1, byte var3, BiFunction<? super Byte, ? super Byte, ? extends Byte> var4) {
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
      public Byte replace(Double var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Double var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Double var1, Function<? super Double, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Double var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2ByteFunctions.SynchronizedFunction implements Double2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ByteMap map;
      protected transient ObjectSet<Double2ByteMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Double2ByteMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2ByteMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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

      public byte getOrDefault(double var1, byte var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(double var1, byte var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, byte var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public byte replace(double var1, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, byte var3, byte var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public byte computeIfAbsent(double var1, DoubleToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public byte computeIfAbsentNullable(double var1, DoubleFunction<? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public byte computeIfAbsentPartial(double var1, Double2ByteFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public byte computeIfPresent(double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public byte compute(double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public byte merge(double var1, byte var3, BiFunction<? super Byte, ? super Byte, ? extends Byte> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
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
      public Byte replace(Double var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Double var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Double var1, Function<? super Double, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Double var1, BiFunction<? super Double, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Double var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2ByteFunctions.Singleton implements Double2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2ByteMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient ByteCollection values;

      protected Singleton(double var1, byte var3) {
         super(var1, var3);
      }

      public boolean containsValue(byte var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Byte)var1 == this.value;
      }

      public void putAll(Map<? extends Double, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Byte>> entrySet() {
         return this.double2ByteEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ this.value;
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

   public static class EmptyMap extends Double2ByteFunctions.EmptyFunction implements Double2ByteMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Double, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2ByteMap.Entry> double2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2ByteMaps.EMPTY_MAP;
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
