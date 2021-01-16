package it.unimi.dsi.fastutil.doubles;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Double2IntMaps {
   public static final Double2IntMaps.EmptyMap EMPTY_MAP = new Double2IntMaps.EmptyMap();

   private Double2IntMaps() {
      super();
   }

   public static ObjectIterator<Double2IntMap.Entry> fastIterator(Double2IntMap var0) {
      ObjectSet var1 = var0.double2IntEntrySet();
      return var1 instanceof Double2IntMap.FastEntrySet ? ((Double2IntMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2IntMap var0, Consumer<? super Double2IntMap.Entry> var1) {
      ObjectSet var2 = var0.double2IntEntrySet();
      if (var2 instanceof Double2IntMap.FastEntrySet) {
         ((Double2IntMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2IntMap.Entry> fastIterable(Double2IntMap var0) {
      final ObjectSet var1 = var0.double2IntEntrySet();
      return (ObjectIterable)(var1 instanceof Double2IntMap.FastEntrySet ? new ObjectIterable<Double2IntMap.Entry>() {
         public ObjectIterator<Double2IntMap.Entry> iterator() {
            return ((Double2IntMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2IntMap.Entry> var1x) {
            ((Double2IntMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2IntMap singleton(double var0, int var2) {
      return new Double2IntMaps.Singleton(var0, var2);
   }

   public static Double2IntMap singleton(Double var0, Integer var1) {
      return new Double2IntMaps.Singleton(var0, var1);
   }

   public static Double2IntMap synchronize(Double2IntMap var0) {
      return new Double2IntMaps.SynchronizedMap(var0);
   }

   public static Double2IntMap synchronize(Double2IntMap var0, Object var1) {
      return new Double2IntMaps.SynchronizedMap(var0, var1);
   }

   public static Double2IntMap unmodifiable(Double2IntMap var0) {
      return new Double2IntMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2IntFunctions.UnmodifiableFunction implements Double2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntMap map;
      protected transient ObjectSet<Double2IntMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Double2IntMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2IntEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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

      public int getOrDefault(double var1, int var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Integer> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Integer, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public int putIfAbsent(double var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public int replace(double var1, int var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, int var3, int var4) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsent(double var1, DoubleToIntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentNullable(double var1, DoubleFunction<? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentPartial(double var1, Double2IntFunction var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfPresent(double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public int compute(double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      public int merge(double var1, int var3, BiFunction<? super Integer, ? super Integer, ? extends Integer> var4) {
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
      public Integer replace(Double var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Integer var2, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Double var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Double var1, Function<? super Double, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Double var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2IntFunctions.SynchronizedFunction implements Double2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2IntMap map;
      protected transient ObjectSet<Double2IntMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Double2IntMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2IntMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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

      public int getOrDefault(double var1, int var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Integer> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Integer, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public int putIfAbsent(double var1, int var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, int var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public int replace(double var1, int var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, int var3, int var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public int computeIfAbsent(double var1, DoubleToIntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public int computeIfAbsentNullable(double var1, DoubleFunction<? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public int computeIfAbsentPartial(double var1, Double2IntFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public int computeIfPresent(double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public int compute(double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public int merge(double var1, int var3, BiFunction<? super Integer, ? super Integer, ? extends Integer> var4) {
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
      public Integer replace(Double var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Integer var2, Integer var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Double var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Double var1, Function<? super Double, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Double var1, BiFunction<? super Double, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Double var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2IntFunctions.Singleton implements Double2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2IntMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient IntCollection values;

      protected Singleton(double var1, int var3) {
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

      public void putAll(Map<? extends Double, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Integer>> entrySet() {
         return this.double2IntEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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

   public static class EmptyMap extends Double2IntFunctions.EmptyFunction implements Double2IntMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Double, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2IntMap.Entry> double2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2IntMaps.EMPTY_MAP;
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
