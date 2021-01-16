package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
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
import java.util.function.LongToDoubleFunction;

public final class Long2DoubleMaps {
   public static final Long2DoubleMaps.EmptyMap EMPTY_MAP = new Long2DoubleMaps.EmptyMap();

   private Long2DoubleMaps() {
      super();
   }

   public static ObjectIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleMap var0) {
      ObjectSet var1 = var0.long2DoubleEntrySet();
      return var1 instanceof Long2DoubleMap.FastEntrySet ? ((Long2DoubleMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Long2DoubleMap var0, Consumer<? super Long2DoubleMap.Entry> var1) {
      ObjectSet var2 = var0.long2DoubleEntrySet();
      if (var2 instanceof Long2DoubleMap.FastEntrySet) {
         ((Long2DoubleMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Long2DoubleMap.Entry> fastIterable(Long2DoubleMap var0) {
      final ObjectSet var1 = var0.long2DoubleEntrySet();
      return (ObjectIterable)(var1 instanceof Long2DoubleMap.FastEntrySet ? new ObjectIterable<Long2DoubleMap.Entry>() {
         public ObjectIterator<Long2DoubleMap.Entry> iterator() {
            return ((Long2DoubleMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2DoubleMap.Entry> var1x) {
            ((Long2DoubleMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Long2DoubleMap singleton(long var0, double var2) {
      return new Long2DoubleMaps.Singleton(var0, var2);
   }

   public static Long2DoubleMap singleton(Long var0, Double var1) {
      return new Long2DoubleMaps.Singleton(var0, var1);
   }

   public static Long2DoubleMap synchronize(Long2DoubleMap var0) {
      return new Long2DoubleMaps.SynchronizedMap(var0);
   }

   public static Long2DoubleMap synchronize(Long2DoubleMap var0, Object var1) {
      return new Long2DoubleMaps.SynchronizedMap(var0, var1);
   }

   public static Long2DoubleMap unmodifiable(Long2DoubleMap var0) {
      return new Long2DoubleMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Long2DoubleFunctions.UnmodifiableFunction implements Long2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleMap map;
      protected transient ObjectSet<Long2DoubleMap.Entry> entries;
      protected transient LongSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Long2DoubleMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(double var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Long, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2DoubleEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public DoubleCollection values() {
         return this.values == null ? DoubleCollections.unmodifiable(this.map.values()) : this.values;
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

      public double getOrDefault(long var1, double var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super Double> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public double putIfAbsent(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public double replace(long var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, double var3, double var5) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsent(long var1, LongToDoubleFunction var3) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentNullable(long var1, LongFunction<? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentPartial(long var1, Long2DoubleFunction var3) {
         throw new UnsupportedOperationException();
      }

      public double computeIfPresent(long var1, BiFunction<? super Long, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public double compute(long var1, BiFunction<? super Long, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public double merge(long var1, double var3, BiFunction<? super Double, ? super Double, ? extends Double> var5) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double getOrDefault(Object var1, Double var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double replace(Long var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Double var2, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Long var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Long var1, Function<? super Long, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Long var1, BiFunction<? super Long, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Long var1, BiFunction<? super Long, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Long var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Long2DoubleFunctions.SynchronizedFunction implements Long2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleMap map;
      protected transient ObjectSet<Long2DoubleMap.Entry> entries;
      protected transient LongSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Long2DoubleMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2DoubleMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(double var1) {
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

      public void putAll(Map<? extends Long, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public DoubleCollection values() {
         synchronized(this.sync) {
            return this.values == null ? DoubleCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public double getOrDefault(long var1, double var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super Double> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super Double, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public double putIfAbsent(long var1, double var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, double var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public double replace(long var1, double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, double var3, double var5) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var5);
         }
      }

      public double computeIfAbsent(long var1, LongToDoubleFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public double computeIfAbsentNullable(long var1, LongFunction<? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public double computeIfAbsentPartial(long var1, Long2DoubleFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public double computeIfPresent(long var1, BiFunction<? super Long, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public double compute(long var1, BiFunction<? super Long, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public double merge(long var1, double var3, BiFunction<? super Double, ? super Double, ? extends Double> var5) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var5);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double getOrDefault(Object var1, Double var2) {
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
      public Double replace(Long var1, Double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, Double var2, Double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Long var1, Double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Long var1, Function<? super Long, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Long var1, BiFunction<? super Long, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Long var1, BiFunction<? super Long, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Long var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Long2DoubleFunctions.Singleton implements Long2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2DoubleMap.Entry> entries;
      protected transient LongSet keys;
      protected transient DoubleCollection values;

      protected Singleton(long var1, double var3) {
         super(var1, var3);
      }

      public boolean containsValue(double var1) {
         return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return Double.doubleToLongBits((Double)var1) == Double.doubleToLongBits(this.value);
      }

      public void putAll(Map<? extends Long, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
         }

         return this.keys;
      }

      public DoubleCollection values() {
         if (this.values == null) {
            this.values = DoubleSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.long2int(this.key) ^ HashCommon.double2int(this.value);
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

   public static class EmptyMap extends Long2DoubleFunctions.EmptyFunction implements Long2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(double var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Long, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2DoubleMaps.EMPTY_MAP;
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
