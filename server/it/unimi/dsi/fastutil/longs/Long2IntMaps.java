package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
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

public final class Long2IntMaps {
   public static final Long2IntMaps.EmptyMap EMPTY_MAP = new Long2IntMaps.EmptyMap();

   private Long2IntMaps() {
      super();
   }

   public static ObjectIterator<Long2IntMap.Entry> fastIterator(Long2IntMap var0) {
      ObjectSet var1 = var0.long2IntEntrySet();
      return var1 instanceof Long2IntMap.FastEntrySet ? ((Long2IntMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Long2IntMap var0, Consumer<? super Long2IntMap.Entry> var1) {
      ObjectSet var2 = var0.long2IntEntrySet();
      if (var2 instanceof Long2IntMap.FastEntrySet) {
         ((Long2IntMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Long2IntMap.Entry> fastIterable(Long2IntMap var0) {
      final ObjectSet var1 = var0.long2IntEntrySet();
      return (ObjectIterable)(var1 instanceof Long2IntMap.FastEntrySet ? new ObjectIterable<Long2IntMap.Entry>() {
         public ObjectIterator<Long2IntMap.Entry> iterator() {
            return ((Long2IntMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2IntMap.Entry> var1x) {
            ((Long2IntMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Long2IntMap singleton(long var0, int var2) {
      return new Long2IntMaps.Singleton(var0, var2);
   }

   public static Long2IntMap singleton(Long var0, Integer var1) {
      return new Long2IntMaps.Singleton(var0, var1);
   }

   public static Long2IntMap synchronize(Long2IntMap var0) {
      return new Long2IntMaps.SynchronizedMap(var0);
   }

   public static Long2IntMap synchronize(Long2IntMap var0, Object var1) {
      return new Long2IntMaps.SynchronizedMap(var0, var1);
   }

   public static Long2IntMap unmodifiable(Long2IntMap var0) {
      return new Long2IntMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Long2IntFunctions.UnmodifiableFunction implements Long2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2IntMap map;
      protected transient ObjectSet<Long2IntMap.Entry> entries;
      protected transient LongSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Long2IntMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(int var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Long, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2IntEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Integer>> entrySet() {
         return this.long2IntEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public IntCollection values() {
         return this.values == null ? IntCollections.unmodifiable(this.map.values()) : this.values;
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

      public int getOrDefault(long var1, int var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super Integer> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super Integer, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public int putIfAbsent(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public int replace(long var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsent(long var1, LongToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentNullable(long var1, LongFunction<? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentPartial(long var1, Long2IntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfPresent(long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public int compute(long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public int merge(long var1, int var3, BiFunction<? super Integer, ? super Integer, ? extends Integer> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer getOrDefault(Object var1, Integer var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer replace(Long var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Integer var2, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Long var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Long var1, Function<? super Long, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Long var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Long2IntFunctions.SynchronizedFunction implements Long2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2IntMap map;
      protected transient ObjectSet<Long2IntMap.Entry> entries;
      protected transient LongSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Long2IntMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2IntMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(int var1) {
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

      public void putAll(Map<? extends Long, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Integer>> entrySet() {
         return this.long2IntEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public IntCollection values() {
         synchronized(this.sync) {
            return this.values == null ? IntCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public int getOrDefault(long var1, int var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super Integer> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super Integer, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public int putIfAbsent(long var1, int var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, int var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public int replace(long var1, int var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, int var3, int var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public int computeIfAbsent(long var1, LongToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public int computeIfAbsentNullable(long var1, LongFunction<? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public int computeIfAbsentPartial(long var1, Long2IntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public int computeIfPresent(long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public int compute(long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public int merge(long var1, int var3, BiFunction<? super Integer, ? super Integer, ? extends Integer> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer getOrDefault(Object var1, Integer var2) {
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
      public Integer replace(Long var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Integer var2, Integer var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Long var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Long var1, Function<? super Long, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Long var1, BiFunction<? super Long, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Long var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Long2IntFunctions.Singleton implements Long2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2IntMap.Entry> entries;
      protected transient LongSet keys;
      protected transient IntCollection values;

      protected Singleton(long var1, int var3) {
         super(var1, var3);
      }

      public boolean containsValue(int var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Integer)var1 == this.value;
      }

      public void putAll(Map<? extends Long, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Integer>> entrySet() {
         return this.long2IntEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
         }

         return this.keys;
      }

      public IntCollection values() {
         if (this.values == null) {
            this.values = IntSets.singleton(this.value);
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

   public static class EmptyMap extends Long2IntFunctions.EmptyFunction implements Long2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(int var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Long, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2IntMap.Entry> long2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2IntMaps.EMPTY_MAP;
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
