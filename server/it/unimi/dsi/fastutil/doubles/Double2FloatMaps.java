package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
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

public final class Double2FloatMaps {
   public static final Double2FloatMaps.EmptyMap EMPTY_MAP = new Double2FloatMaps.EmptyMap();

   private Double2FloatMaps() {
      super();
   }

   public static ObjectIterator<Double2FloatMap.Entry> fastIterator(Double2FloatMap var0) {
      ObjectSet var1 = var0.double2FloatEntrySet();
      return var1 instanceof Double2FloatMap.FastEntrySet ? ((Double2FloatMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Double2FloatMap var0, Consumer<? super Double2FloatMap.Entry> var1) {
      ObjectSet var2 = var0.double2FloatEntrySet();
      if (var2 instanceof Double2FloatMap.FastEntrySet) {
         ((Double2FloatMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Double2FloatMap.Entry> fastIterable(Double2FloatMap var0) {
      final ObjectSet var1 = var0.double2FloatEntrySet();
      return (ObjectIterable)(var1 instanceof Double2FloatMap.FastEntrySet ? new ObjectIterable<Double2FloatMap.Entry>() {
         public ObjectIterator<Double2FloatMap.Entry> iterator() {
            return ((Double2FloatMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2FloatMap.Entry> var1x) {
            ((Double2FloatMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Double2FloatMap singleton(double var0, float var2) {
      return new Double2FloatMaps.Singleton(var0, var2);
   }

   public static Double2FloatMap singleton(Double var0, Float var1) {
      return new Double2FloatMaps.Singleton(var0, var1);
   }

   public static Double2FloatMap synchronize(Double2FloatMap var0) {
      return new Double2FloatMaps.SynchronizedMap(var0);
   }

   public static Double2FloatMap synchronize(Double2FloatMap var0, Object var1) {
      return new Double2FloatMaps.SynchronizedMap(var0, var1);
   }

   public static Double2FloatMap unmodifiable(Double2FloatMap var0) {
      return new Double2FloatMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Double2FloatFunctions.UnmodifiableFunction implements Double2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2FloatMap map;
      protected transient ObjectSet<Double2FloatMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Double2FloatMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(float var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Double, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2FloatEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Float>> entrySet() {
         return this.double2FloatEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public FloatCollection values() {
         return this.values == null ? FloatCollections.unmodifiable(this.map.values()) : this.values;
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

      public float getOrDefault(double var1, float var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super Float> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super Float, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public float putIfAbsent(double var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public float replace(double var1, float var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, float var3, float var4) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsent(double var1, DoubleUnaryOperator var3) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentNullable(double var1, DoubleFunction<? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentPartial(double var1, Double2FloatFunction var3) {
         throw new UnsupportedOperationException();
      }

      public float computeIfPresent(double var1, BiFunction<? super Double, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      public float compute(double var1, BiFunction<? super Double, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      public float merge(double var1, float var3, BiFunction<? super Float, ? super Float, ? extends Float> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float getOrDefault(Object var1, Float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float replace(Double var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Float var2, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Double var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Double var1, Function<? super Double, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Double var1, BiFunction<? super Double, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Double var1, BiFunction<? super Double, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Double var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Double2FloatFunctions.SynchronizedFunction implements Double2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2FloatMap map;
      protected transient ObjectSet<Double2FloatMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Double2FloatMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2FloatMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(float var1) {
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

      public void putAll(Map<? extends Double, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Float>> entrySet() {
         return this.double2FloatEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public FloatCollection values() {
         synchronized(this.sync) {
            return this.values == null ? FloatCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public float getOrDefault(double var1, float var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super Float> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super Float, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public float putIfAbsent(double var1, float var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, float var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public float replace(double var1, float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, float var3, float var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public float computeIfAbsent(double var1, DoubleUnaryOperator var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public float computeIfAbsentNullable(double var1, DoubleFunction<? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var3);
         }
      }

      public float computeIfAbsentPartial(double var1, Double2FloatFunction var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public float computeIfPresent(double var1, BiFunction<? super Double, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public float compute(double var1, BiFunction<? super Double, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public float merge(double var1, float var3, BiFunction<? super Float, ? super Float, ? extends Float> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float getOrDefault(Object var1, Float var2) {
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
      public Float replace(Double var1, Float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, Float var2, Float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Double var1, Float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Double var1, Function<? super Double, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Double var1, BiFunction<? super Double, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Double var1, BiFunction<? super Double, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Double var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Double2FloatFunctions.Singleton implements Double2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2FloatMap.Entry> entries;
      protected transient DoubleSet keys;
      protected transient FloatCollection values;

      protected Singleton(double var1, float var3) {
         super(var1, var3);
      }

      public boolean containsValue(float var1) {
         return Float.floatToIntBits(this.value) == Float.floatToIntBits(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return Float.floatToIntBits((Float)var1) == Float.floatToIntBits(this.value);
      }

      public void putAll(Map<? extends Double, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, Float>> entrySet() {
         return this.double2FloatEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
         }

         return this.keys;
      }

      public FloatCollection values() {
         if (this.values == null) {
            this.values = FloatSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ HashCommon.float2int(this.value);
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

   public static class EmptyMap extends Double2FloatFunctions.EmptyFunction implements Double2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(float var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Double, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2FloatMap.Entry> double2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2FloatMaps.EMPTY_MAP;
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
