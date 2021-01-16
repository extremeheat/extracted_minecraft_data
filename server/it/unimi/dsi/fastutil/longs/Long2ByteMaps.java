package it.unimi.dsi.fastutil.longs;

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
import java.util.function.Function;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public final class Long2ByteMaps {
   public static final Long2ByteMaps.EmptyMap EMPTY_MAP = new Long2ByteMaps.EmptyMap();

   private Long2ByteMaps() {
      super();
   }

   public static ObjectIterator<Long2ByteMap.Entry> fastIterator(Long2ByteMap var0) {
      ObjectSet var1 = var0.long2ByteEntrySet();
      return var1 instanceof Long2ByteMap.FastEntrySet ? ((Long2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Long2ByteMap var0, Consumer<? super Long2ByteMap.Entry> var1) {
      ObjectSet var2 = var0.long2ByteEntrySet();
      if (var2 instanceof Long2ByteMap.FastEntrySet) {
         ((Long2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Long2ByteMap.Entry> fastIterable(Long2ByteMap var0) {
      final ObjectSet var1 = var0.long2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Long2ByteMap.FastEntrySet ? new ObjectIterable<Long2ByteMap.Entry>() {
         public ObjectIterator<Long2ByteMap.Entry> iterator() {
            return ((Long2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2ByteMap.Entry> var1x) {
            ((Long2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Long2ByteMap singleton(long var0, byte var2) {
      return new Long2ByteMaps.Singleton(var0, var2);
   }

   public static Long2ByteMap singleton(Long var0, Byte var1) {
      return new Long2ByteMaps.Singleton(var0, var1);
   }

   public static Long2ByteMap synchronize(Long2ByteMap var0) {
      return new Long2ByteMaps.SynchronizedMap(var0);
   }

   public static Long2ByteMap synchronize(Long2ByteMap var0, Object var1) {
      return new Long2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static Long2ByteMap unmodifiable(Long2ByteMap var0) {
      return new Long2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Long2ByteFunctions.UnmodifiableFunction implements Long2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteMap map;
      protected transient ObjectSet<Long2ByteMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Long2ByteMap var1) {
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

      public void putAll(Map<? extends Long, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Byte>> entrySet() {
         return this.long2ByteEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
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

      public byte getOrDefault(long var1, byte var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte replace(long var1, byte var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, byte var3, byte var4) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsent(long var1, LongToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentNullable(long var1, LongFunction<? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentPartial(long var1, Long2ByteFunction var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfPresent(long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public byte compute(long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      public byte merge(long var1, byte var3, BiFunction<? super Byte, ? super Byte, ? extends Byte> var4) {
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
      public Byte replace(Long var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Long var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Long var1, Function<? super Long, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Long var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Long2ByteFunctions.SynchronizedFunction implements Long2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ByteMap map;
      protected transient ObjectSet<Long2ByteMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Long2ByteMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2ByteMap var1) {
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

      public void putAll(Map<? extends Long, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Byte>> entrySet() {
         return this.long2ByteEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
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

      public byte getOrDefault(long var1, byte var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(long var1, byte var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, byte var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public byte replace(long var1, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, byte var3, byte var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public byte computeIfAbsent(long var1, LongToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public byte computeIfAbsentNullable(long var1, LongFunction<? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public byte computeIfAbsentPartial(long var1, Long2ByteFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public byte computeIfPresent(long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public byte compute(long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public byte merge(long var1, byte var3, BiFunction<? super Byte, ? super Byte, ? extends Byte> var4) {
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
      public Byte replace(Long var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Long var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Long var1, Function<? super Long, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Long var1, BiFunction<? super Long, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Long var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Long2ByteFunctions.Singleton implements Long2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2ByteMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ByteCollection values;

      protected Singleton(long var1, byte var3) {
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

      public void putAll(Map<? extends Long, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Byte>> entrySet() {
         return this.long2ByteEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
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
         return HashCommon.long2int(this.key) ^ this.value;
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

   public static class EmptyMap extends Long2ByteFunctions.EmptyFunction implements Long2ByteMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Long, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ByteMap.Entry> long2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2ByteMaps.EMPTY_MAP;
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
