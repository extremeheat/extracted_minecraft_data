package it.unimi.dsi.fastutil.floats;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Float2DoubleMaps {
   public static final Float2DoubleMaps.EmptyMap EMPTY_MAP = new Float2DoubleMaps.EmptyMap();

   private Float2DoubleMaps() {
      super();
   }

   public static ObjectIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleMap var0) {
      ObjectSet var1 = var0.float2DoubleEntrySet();
      return var1 instanceof Float2DoubleMap.FastEntrySet ? ((Float2DoubleMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Float2DoubleMap var0, Consumer<? super Float2DoubleMap.Entry> var1) {
      ObjectSet var2 = var0.float2DoubleEntrySet();
      if (var2 instanceof Float2DoubleMap.FastEntrySet) {
         ((Float2DoubleMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Float2DoubleMap.Entry> fastIterable(Float2DoubleMap var0) {
      final ObjectSet var1 = var0.float2DoubleEntrySet();
      return (ObjectIterable)(var1 instanceof Float2DoubleMap.FastEntrySet ? new ObjectIterable<Float2DoubleMap.Entry>() {
         public ObjectIterator<Float2DoubleMap.Entry> iterator() {
            return ((Float2DoubleMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Float2DoubleMap.Entry> var1x) {
            ((Float2DoubleMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Float2DoubleMap singleton(float var0, double var1) {
      return new Float2DoubleMaps.Singleton(var0, var1);
   }

   public static Float2DoubleMap singleton(Float var0, Double var1) {
      return new Float2DoubleMaps.Singleton(var0, var1);
   }

   public static Float2DoubleMap synchronize(Float2DoubleMap var0) {
      return new Float2DoubleMaps.SynchronizedMap(var0);
   }

   public static Float2DoubleMap synchronize(Float2DoubleMap var0, Object var1) {
      return new Float2DoubleMaps.SynchronizedMap(var0, var1);
   }

   public static Float2DoubleMap unmodifiable(Float2DoubleMap var0) {
      return new Float2DoubleMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Float2DoubleFunctions.UnmodifiableFunction implements Float2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2DoubleMap map;
      protected transient ObjectSet<Float2DoubleMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Float2DoubleMap var1) {
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

      public void putAll(Map<? extends Float, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2DoubleEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Double>> entrySet() {
         return this.float2DoubleEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
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

      public double getOrDefault(float var1, double var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Float, ? super Double> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Float, ? super Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public double putIfAbsent(float var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double replace(float var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(float var1, double var2, double var4) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsent(float var1, DoubleUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentNullable(float var1, DoubleFunction<? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double computeIfAbsentPartial(float var1, Float2DoubleFunction var2) {
         throw new UnsupportedOperationException();
      }

      public double computeIfPresent(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double compute(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double merge(float var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
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
      public Double replace(Float var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Double var2, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Float var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Float var1, Function<? super Float, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Float var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Float2DoubleFunctions.SynchronizedFunction implements Float2DoubleMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2DoubleMap map;
      protected transient ObjectSet<Float2DoubleMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Float2DoubleMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Float2DoubleMap var1) {
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

      public void putAll(Map<? extends Float, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Double>> entrySet() {
         return this.float2DoubleEntrySet();
      }

      public FloatSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
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

      public double getOrDefault(float var1, double var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Float, ? super Double> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Float, ? super Double, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public double putIfAbsent(float var1, double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(float var1, double var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public double replace(float var1, double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(float var1, double var2, double var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var4);
         }
      }

      public double computeIfAbsent(float var1, DoubleUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public double computeIfAbsentNullable(float var1, DoubleFunction<? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public double computeIfAbsentPartial(float var1, Float2DoubleFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public double computeIfPresent(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public double compute(float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public double merge(float var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var4);
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
      public Double replace(Float var1, Double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Double var2, Double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(Float var1, Double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfAbsent(Float var1, Function<? super Float, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double computeIfPresent(Float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double compute(Float var1, BiFunction<? super Float, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double merge(Float var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Float2DoubleFunctions.Singleton implements Float2DoubleMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2DoubleMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient DoubleCollection values;

      protected Singleton(float var1, double var2) {
         super(var1, var2);
      }

      public boolean containsValue(double var1) {
         return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return Double.doubleToLongBits((Double)var1) == Double.doubleToLongBits(this.value);
      }

      public void putAll(Map<? extends Float, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Double>> entrySet() {
         return this.float2DoubleEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
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
         return HashCommon.float2int(this.key) ^ HashCommon.double2int(this.value);
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

   public static class EmptyMap extends Float2DoubleFunctions.EmptyFunction implements Float2DoubleMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Float, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2DoubleMap.Entry> float2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      public Object clone() {
         return Float2DoubleMaps.EMPTY_MAP;
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
