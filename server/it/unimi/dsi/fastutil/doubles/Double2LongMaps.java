package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public final class Double2LongMaps {
   public static final Double2LongMaps.EmptyMap EMPTY_MAP = new Double2LongMaps.EmptyMap();

   private Double2LongMaps() {
      super();
   }

   public static ObjectIterator<Double2LongMap.Entry> fastIterator(Double2LongMap var0) {
      ObjectSet var1 = var0.double2LongEntrySet();
      return var1 instanceof Double2LongMap.FastEntrySet ? ((Double2LongMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2LongMap var0, Consumer<? super Double2LongMap.Entry> var1) {
      ObjectSet var2 = var0.double2LongEntrySet();
      if (var2 instanceof Double2LongMap.FastEntrySet) {
         ((Double2LongMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2LongMap.Entry> fastIterable(Double2LongMap var0) {
      final ObjectSet var1 = var0.double2LongEntrySet();
      return (ObjectIterable)(var1 instanceof Double2LongMap.FastEntrySet ? new ObjectIterable<Double2LongMap.Entry>() {
         public ObjectIterator<Double2LongMap.Entry> iterator() {
            return ((Double2LongMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2LongMap.Entry> var1x) {
            ((Double2LongMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2LongMap singleton(double var0, long var2) {
      return new Double2LongMaps.Singleton(var0, var2);
   }

   public static Double2LongMap singleton(Double var0, Long var1) {
      return new Double2LongMaps.Singleton(var0, var1);
   }

   public static Double2LongMap synchronize(Double2LongMap var0) {
      return new Double2LongMaps.SynchronizedMap(var0);
   }

   public static Double2LongMap synchronize(Double2LongMap var0, Object var1) {
      return new Double2LongMaps.SynchronizedMap(var0, var1);
   }

   public static Double2LongMap unmodifiable(Double2LongMap var0) {
      return new Double2LongMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2LongFunctions.UnmodifiableFunction implements Double2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2LongMap map;
      protected transient ObjectSet<Double2LongMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Double2LongMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2LongEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Long>> entrySet() {
         return this.double2LongEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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

      public long getOrDefault(double var1, long var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Long> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Long, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public long putIfAbsent(double var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public long replace(double var1, long var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, long var3, long var5) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsent(double var1, DoubleToLongFunction var3) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsentNullable(double var1, DoubleFunction<? extends Long> var3) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsentPartial(double var1, Double2LongFunction var3) {
         throw new UnsupportedOperationException();
      }

      public long computeIfPresent(double var1, BiFunction<? super Double, ? super Long, ? extends Long> var3) {
         throw new UnsupportedOperationException();
      }

      public long compute(double var1, BiFunction<? super Double, ? super Long, ? extends Long> var3) {
         throw new UnsupportedOperationException();
      }

      public long merge(double var1, long var3, BiFunction<? super Long, ? super Long, ? extends Long> var5) {
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
      public Long replace(Double var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Long var2, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(Double var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfAbsent(Double var1, Function<? super Double, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfPresent(Double var1, BiFunction<? super Double, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long compute(Double var1, BiFunction<? super Double, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long merge(Double var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2LongFunctions.SynchronizedFunction implements Double2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2LongMap map;
      protected transient ObjectSet<Double2LongMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Double2LongMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2LongMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Long>> entrySet() {
         return this.double2LongEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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

      public long getOrDefault(double var1, long var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Long> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Long, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public long putIfAbsent(double var1, long var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, long var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public long replace(double var1, long var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, long var3, long var5) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var5);
         }
      }

      public long computeIfAbsent(double var1, DoubleToLongFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public long computeIfAbsentNullable(double var1, DoubleFunction<? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public long computeIfAbsentPartial(double var1, Double2LongFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public long computeIfPresent(double var1, BiFunction<? super Double, ? super Long, ? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public long compute(double var1, BiFunction<? super Double, ? super Long, ? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public long merge(double var1, long var3, BiFunction<? super Long, ? super Long, ? extends Long> var5) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var5);
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
      public Long replace(Double var1, Long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Long var2, Long var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(Double var1, Long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfAbsent(Double var1, Function<? super Double, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfPresent(Double var1, BiFunction<? super Double, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long compute(Double var1, BiFunction<? super Double, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long merge(Double var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2LongFunctions.Singleton implements Double2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2LongMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient LongCollection values;

      protected Singleton(double var1, long var3) {
         super(var1, var3);
      }

      public boolean containsValue(long var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Long)var1 == this.value;
      }

      public void putAll(Map<? extends Double, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Long>> entrySet() {
         return this.double2LongEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ HashCommon.long2int(this.value);
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

   public static class EmptyMap extends Double2LongFunctions.EmptyFunction implements Double2LongMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Double, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2LongMap.Entry> double2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2LongMaps.EMPTY_MAP;
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
