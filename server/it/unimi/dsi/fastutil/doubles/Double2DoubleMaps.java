package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
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
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Double2DoubleMaps {
   public static final Double2DoubleMaps.EmptyMap EMPTY_MAP = new Double2DoubleMaps.EmptyMap();

   private Double2DoubleMaps() {
      super();
   }

   public static ObjectIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap var0) {
      ObjectSet var1 = var0.double2DoubleEntrySet();
      return var1 instanceof Double2DoubleMap.FastEntrySet ? ((Double2DoubleMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2DoubleMap var0, Consumer<? super Double2DoubleMap.Entry> var1) {
      ObjectSet var2 = var0.double2DoubleEntrySet();
      if (var2 instanceof Double2DoubleMap.FastEntrySet) {
         ((Double2DoubleMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2DoubleMap.Entry> fastIterable(Double2DoubleMap var0) {
      final ObjectSet var1 = var0.double2DoubleEntrySet();
      return (ObjectIterable)(var1 instanceof Double2DoubleMap.FastEntrySet ? new ObjectIterable<Double2DoubleMap.Entry>() {
         public ObjectIterator<Double2DoubleMap.Entry> iterator() {
            return ((Double2DoubleMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2DoubleMap.Entry> var1x) {
            ((Double2DoubleMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2DoubleMap singleton(double var0, double var2) {
      return new Double2DoubleMaps.Singleton(var0, var2);
   }

   public static Double2DoubleMap singleton(Double var0, Double var1) {
      return new Double2DoubleMaps.Singleton(var0, var1);
   }

   public static Double2DoubleMap synchronize(Double2DoubleMap var0) {
      return new Double2DoubleMaps.SynchronizedMap(var0);
   }

   public static Double2DoubleMap synchronize(Double2DoubleMap var0, Object var1) {
      return new Double2DoubleMaps.SynchronizedMap(var0, var1);
   }

   public static Double2DoubleMap unmodifiable(Double2DoubleMap var0) {
      return new Double2DoubleMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2DoubleFunctions.UnmodifiableFunction implements Double2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2DoubleMap map;
      protected transient ObjectSet<Double2DoubleMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Double2DoubleMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2DoubleEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
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

      public double getOrDefault(double var1, double var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Double> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public double putIfAbsent(double var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public double replace(double var1, double var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, double var3, double var5) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsent(double var1, DoubleUnaryOperator var3) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentNullable(double var1, DoubleFunction<? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentPartial(double var1, Double2DoubleFunction var3) {
         throw new UnsupportedOperationException();
      }

      public double computeIfPresent(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public double compute(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }

      public double merge(double var1, double var3, BiFunction<? super Double, ? super Double, ? extends Double> var5) {
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
      public Double replace(Double var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Double var2, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Double var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Double var1, Function<? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Double var1, BiFunction<? super Double, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Double var1, BiFunction<? super Double, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Double var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2DoubleFunctions.SynchronizedFunction implements Double2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2DoubleMap map;
      protected transient ObjectSet<Double2DoubleMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Double2DoubleMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2DoubleMap var1) {
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

      public void putAll(Map<? extends Double, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
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

      public double getOrDefault(double var1, double var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Double> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Double, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public double putIfAbsent(double var1, double var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, double var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public double replace(double var1, double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, double var3, double var5) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var5);
         }
      }

      public double computeIfAbsent(double var1, DoubleUnaryOperator var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public double computeIfAbsentNullable(double var1, DoubleFunction<? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public double computeIfAbsentPartial(double var1, Double2DoubleFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public double computeIfPresent(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public double compute(double var1, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public double merge(double var1, double var3, BiFunction<? super Double, ? super Double, ? extends Double> var5) {
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
      public Double replace(Double var1, Double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Double var2, Double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Double var1, Double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Double var1, Function<? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Double var1, BiFunction<? super Double, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Double var1, BiFunction<? super Double, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Double var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2DoubleFunctions.Singleton implements Double2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2DoubleMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient DoubleCollection values;

      protected Singleton(double var1, double var3) {
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

      public void putAll(Map<? extends Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Double>> entrySet() {
         return this.double2DoubleEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
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
         return HashCommon.double2int(this.key) ^ HashCommon.double2int(this.value);
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

   public static class EmptyMap extends Double2DoubleFunctions.EmptyFunction implements Double2DoubleMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2DoubleMaps.EMPTY_MAP;
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
