package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortSets;
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

public final class Long2ShortMaps {
   public static final Long2ShortMaps.EmptyMap EMPTY_MAP = new Long2ShortMaps.EmptyMap();

   private Long2ShortMaps() {
      super();
   }

   public static ObjectIterator<Long2ShortMap.Entry> fastIterator(Long2ShortMap var0) {
      ObjectSet var1 = var0.long2ShortEntrySet();
      return var1 instanceof Long2ShortMap.FastEntrySet ? ((Long2ShortMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Long2ShortMap var0, Consumer<? super Long2ShortMap.Entry> var1) {
      ObjectSet var2 = var0.long2ShortEntrySet();
      if (var2 instanceof Long2ShortMap.FastEntrySet) {
         ((Long2ShortMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Long2ShortMap.Entry> fastIterable(Long2ShortMap var0) {
      final ObjectSet var1 = var0.long2ShortEntrySet();
      return (ObjectIterable)(var1 instanceof Long2ShortMap.FastEntrySet ? new ObjectIterable<Long2ShortMap.Entry>() {
         public ObjectIterator<Long2ShortMap.Entry> iterator() {
            return ((Long2ShortMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2ShortMap.Entry> var1x) {
            ((Long2ShortMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Long2ShortMap singleton(long var0, short var2) {
      return new Long2ShortMaps.Singleton(var0, var2);
   }

   public static Long2ShortMap singleton(Long var0, Short var1) {
      return new Long2ShortMaps.Singleton(var0, var1);
   }

   public static Long2ShortMap synchronize(Long2ShortMap var0) {
      return new Long2ShortMaps.SynchronizedMap(var0);
   }

   public static Long2ShortMap synchronize(Long2ShortMap var0, Object var1) {
      return new Long2ShortMaps.SynchronizedMap(var0, var1);
   }

   public static Long2ShortMap unmodifiable(Long2ShortMap var0) {
      return new Long2ShortMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Long2ShortFunctions.UnmodifiableFunction implements Long2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortMap map;
      protected transient ObjectSet<Long2ShortMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Long2ShortMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(short var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Long, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2ShortEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ShortCollection values() {
         return this.values == null ? ShortCollections.unmodifiable(this.map.values()) : this.values;
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

      public short getOrDefault(long var1, short var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super Short> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super Short, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public short putIfAbsent(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public short replace(long var1, short var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, short var3, short var4) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsent(long var1, LongToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsentNullable(long var1, LongFunction<? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsentPartial(long var1, Long2ShortFunction var3) {
         throw new UnsupportedOperationException();
      }

      public short computeIfPresent(long var1, BiFunction<? super Long, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      public short compute(long var1, BiFunction<? super Long, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      public short merge(long var1, short var3, BiFunction<? super Short, ? super Short, ? extends Short> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short getOrDefault(Object var1, Short var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short replace(Long var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Short var2, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(Long var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfAbsent(Long var1, Function<? super Long, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfPresent(Long var1, BiFunction<? super Long, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short compute(Long var1, BiFunction<? super Long, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short merge(Long var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Long2ShortFunctions.SynchronizedFunction implements Long2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortMap map;
      protected transient ObjectSet<Long2ShortMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Long2ShortMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2ShortMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(short var1) {
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

      public void putAll(Map<? extends Long, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ShortCollection values() {
         synchronized(this.sync) {
            return this.values == null ? ShortCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public short getOrDefault(long var1, short var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super Short> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super Short, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public short putIfAbsent(long var1, short var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, short var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public short replace(long var1, short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, short var3, short var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public short computeIfAbsent(long var1, LongToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public short computeIfAbsentNullable(long var1, LongFunction<? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public short computeIfAbsentPartial(long var1, Long2ShortFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public short computeIfPresent(long var1, BiFunction<? super Long, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public short compute(long var1, BiFunction<? super Long, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public short merge(long var1, short var3, BiFunction<? super Short, ? super Short, ? extends Short> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short getOrDefault(Object var1, Short var2) {
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
      public Short replace(Long var1, Short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Short var2, Short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(Long var1, Short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfAbsent(Long var1, Function<? super Long, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfPresent(Long var1, BiFunction<? super Long, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short compute(Long var1, BiFunction<? super Long, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short merge(Long var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Long2ShortFunctions.Singleton implements Long2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2ShortMap.Entry> entries;
      protected transient LongSet keys;
      protected transient ShortCollection values;

      protected Singleton(long var1, short var3) {
         super(var1, var3);
      }

      public boolean containsValue(short var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Short)var1 == this.value;
      }

      public void putAll(Map<? extends Long, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
         }

         return this.keys;
      }

      public ShortCollection values() {
         if (this.values == null) {
            this.values = ShortSets.singleton(this.value);
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

   public static class EmptyMap extends Long2ShortFunctions.EmptyFunction implements Long2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(short var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Long, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2ShortMaps.EMPTY_MAP;
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
