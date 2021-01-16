package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSets;
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
import java.util.function.IntFunction;
import java.util.function.IntToLongFunction;

public final class Int2LongMaps {
   public static final Int2LongMaps.EmptyMap EMPTY_MAP = new Int2LongMaps.EmptyMap();

   private Int2LongMaps() {
      super();
   }

   public static ObjectIterator<Int2LongMap.Entry> fastIterator(Int2LongMap var0) {
      ObjectSet var1 = var0.int2LongEntrySet();
      return var1 instanceof Int2LongMap.FastEntrySet ? ((Int2LongMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Int2LongMap var0, Consumer<? super Int2LongMap.Entry> var1) {
      ObjectSet var2 = var0.int2LongEntrySet();
      if (var2 instanceof Int2LongMap.FastEntrySet) {
         ((Int2LongMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Int2LongMap.Entry> fastIterable(Int2LongMap var0) {
      final ObjectSet var1 = var0.int2LongEntrySet();
      return (ObjectIterable)(var1 instanceof Int2LongMap.FastEntrySet ? new ObjectIterable<Int2LongMap.Entry>() {
         public ObjectIterator<Int2LongMap.Entry> iterator() {
            return ((Int2LongMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Int2LongMap.Entry> var1x) {
            ((Int2LongMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Int2LongMap singleton(int var0, long var1) {
      return new Int2LongMaps.Singleton(var0, var1);
   }

   public static Int2LongMap singleton(Integer var0, Long var1) {
      return new Int2LongMaps.Singleton(var0, var1);
   }

   public static Int2LongMap synchronize(Int2LongMap var0) {
      return new Int2LongMaps.SynchronizedMap(var0);
   }

   public static Int2LongMap synchronize(Int2LongMap var0, Object var1) {
      return new Int2LongMaps.SynchronizedMap(var0, var1);
   }

   public static Int2LongMap unmodifiable(Int2LongMap var0) {
      return new Int2LongMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Int2LongFunctions.UnmodifiableFunction implements Int2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongMap map;
      protected transient ObjectSet<Int2LongMap.Entry> entries;
      protected transient IntSet keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Int2LongMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(long var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Integer, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2LongEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public LongCollection values() {
         return this.values == null ? LongCollections.unmodifiable(this.map.values()) : this.values;
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

      public long getOrDefault(int var1, long var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Integer, ? super Long> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Integer, ? super Long, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public long putIfAbsent(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long replace(int var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(int var1, long var2, long var4) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsent(int var1, IntToLongFunction var2) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsentNullable(int var1, IntFunction<? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsentPartial(int var1, Int2LongFunction var2) {
         throw new UnsupportedOperationException();
      }

      public long computeIfPresent(int var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long compute(int var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long merge(int var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long getOrDefault(Object var1, Long var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long replace(Integer var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Integer var1, Long var2, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(Integer var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfAbsent(Integer var1, Function<? super Integer, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long compute(Integer var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long merge(Integer var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Int2LongFunctions.SynchronizedFunction implements Int2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2LongMap map;
      protected transient ObjectSet<Int2LongMap.Entry> entries;
      protected transient IntSet keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Int2LongMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Int2LongMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(long var1) {
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

      public void putAll(Map<? extends Integer, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      public IntSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = IntSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public LongCollection values() {
         synchronized(this.sync) {
            return this.values == null ? LongCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public long getOrDefault(int var1, long var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Integer, ? super Long> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Integer, ? super Long, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public long putIfAbsent(int var1, long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(int var1, long var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public long replace(int var1, long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(int var1, long var2, long var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var4);
         }
      }

      public long computeIfAbsent(int var1, IntToLongFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public long computeIfAbsentNullable(int var1, IntFunction<? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public long computeIfAbsentPartial(int var1, Int2LongFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public long computeIfPresent(int var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public long compute(int var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public long merge(int var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long getOrDefault(Object var1, Long var2) {
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
      public Long replace(Integer var1, Long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Integer var1, Long var2, Long var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(Integer var1, Long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfAbsent(Integer var1, Function<? super Integer, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long compute(Integer var1, BiFunction<? super Integer, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long merge(Integer var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Int2LongFunctions.Singleton implements Int2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2LongMap.Entry> entries;
      protected transient IntSet keys;
      protected transient LongCollection values;

      protected Singleton(int var1, long var2) {
         super(var1, var2);
      }

      public boolean containsValue(long var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Long)var1 == this.value;
      }

      public void putAll(Map<? extends Integer, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Long>> entrySet() {
         return this.int2LongEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
         }

         return this.keys;
      }

      public LongCollection values() {
         if (this.values == null) {
            this.values = LongSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return this.key ^ HashCommon.long2int(this.value);
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

   public static class EmptyMap extends Int2LongFunctions.EmptyFunction implements Int2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(long var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Integer, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2LongMap.Entry> int2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      public Object clone() {
         return Int2LongMaps.EMPTY_MAP;
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
