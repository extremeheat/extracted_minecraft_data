package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public final class Reference2DoubleMaps {
   public static final Reference2DoubleMaps.EmptyMap EMPTY_MAP = new Reference2DoubleMaps.EmptyMap();

   private Reference2DoubleMaps() {
      super();
   }

   public static <K> ObjectIterator<Reference2DoubleMap.Entry<K>> fastIterator(Reference2DoubleMap<K> var0) {
      ObjectSet var1 = var0.reference2DoubleEntrySet();
      return var1 instanceof Reference2DoubleMap.FastEntrySet ? ((Reference2DoubleMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Reference2DoubleMap<K> var0, Consumer<? super Reference2DoubleMap.Entry<K>> var1) {
      ObjectSet var2 = var0.reference2DoubleEntrySet();
      if (var2 instanceof Reference2DoubleMap.FastEntrySet) {
         ((Reference2DoubleMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Reference2DoubleMap.Entry<K>> fastIterable(Reference2DoubleMap<K> var0) {
      final ObjectSet var1 = var0.reference2DoubleEntrySet();
      return (ObjectIterable)(var1 instanceof Reference2DoubleMap.FastEntrySet ? new ObjectIterable<Reference2DoubleMap.Entry<K>>() {
         public ObjectIterator<Reference2DoubleMap.Entry<K>> iterator() {
            return ((Reference2DoubleMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Reference2DoubleMap.Entry<K>> var1x) {
            ((Reference2DoubleMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Reference2DoubleMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2DoubleMap<K> singleton(K var0, double var1) {
      return new Reference2DoubleMaps.Singleton(var0, var1);
   }

   public static <K> Reference2DoubleMap<K> singleton(K var0, Double var1) {
      return new Reference2DoubleMaps.Singleton(var0, var1);
   }

   public static <K> Reference2DoubleMap<K> synchronize(Reference2DoubleMap<K> var0) {
      return new Reference2DoubleMaps.SynchronizedMap(var0);
   }

   public static <K> Reference2DoubleMap<K> synchronize(Reference2DoubleMap<K> var0, Object var1) {
      return new Reference2DoubleMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Reference2DoubleMap<K> unmodifiable(Reference2DoubleMap<K> var0) {
      return new Reference2DoubleMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Reference2DoubleFunctions.UnmodifiableFunction<K> implements Reference2DoubleMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleMap<K> map;
      protected transient ObjectSet<Reference2DoubleMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient DoubleCollection values;

      protected UnmodifiableMap(Reference2DoubleMap<K> var1) {
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

      public void putAll(Map<? extends K, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2DoubleEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Double>> entrySet() {
         return this.reference2DoubleEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
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

      public double getOrDefault(Object var1, double var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Double> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Double, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public double putIfAbsent(K var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public double replace(K var1, double var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, double var2, double var4) {
         throw new UnsupportedOperationException();
      }

      public double computeDoubleIfAbsent(K var1, ToDoubleFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public double computeDoubleIfAbsentPartial(K var1, Reference2DoubleFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public double computeDoubleIfPresent(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double computeDouble(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public double mergeDouble(K var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
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
      public Double replace(K var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Double var2, Double var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(K var1, Double var2) {
         throw new UnsupportedOperationException();
      }

      public Double computeIfAbsent(K var1, Function<? super K, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public Double computeIfPresent(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      public Double compute(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Double merge(K var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Reference2DoubleFunctions.SynchronizedFunction<K> implements Reference2DoubleMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleMap<K> map;
      protected transient ObjectSet<Reference2DoubleMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient DoubleCollection values;

      protected SynchronizedMap(Reference2DoubleMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Reference2DoubleMap<K> var1) {
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

      public void putAll(Map<? extends K, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2DoubleEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Double>> entrySet() {
         return this.reference2DoubleEntrySet();
      }

      public ReferenceSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
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

      public double getOrDefault(Object var1, double var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Double> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Double, ? extends Double> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public double putIfAbsent(K var1, double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, double var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public double replace(K var1, double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, double var2, double var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var4);
         }
      }

      public double computeDoubleIfAbsent(K var1, ToDoubleFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeDoubleIfAbsent(var1, var2);
         }
      }

      public double computeDoubleIfAbsentPartial(K var1, Reference2DoubleFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeDoubleIfAbsentPartial(var1, var2);
         }
      }

      public double computeDoubleIfPresent(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeDoubleIfPresent(var1, var2);
         }
      }

      public double computeDouble(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return this.map.computeDouble(var1, var2);
         }
      }

      public double mergeDouble(K var1, double var2, BiFunction<? super Double, ? super Double, ? extends Double> var4) {
         synchronized(this.sync) {
            return this.map.mergeDouble(var1, var2, var4);
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
      public Double replace(K var1, Double var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Double var2, Double var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double putIfAbsent(K var1, Double var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Double computeIfAbsent(K var1, Function<? super K, ? extends Double> var2) {
         synchronized(this.sync) {
            return (Double)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Double computeIfPresent(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return (Double)this.map.computeIfPresent(var1, var2);
         }
      }

      public Double compute(K var1, BiFunction<? super K, ? super Double, ? extends Double> var2) {
         synchronized(this.sync) {
            return (Double)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Double merge(K var1, Double var2, BiFunction<? super Double, ? super Double, ? extends Double> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Reference2DoubleFunctions.Singleton<K> implements Reference2DoubleMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2DoubleMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient DoubleCollection values;

      protected Singleton(K var1, double var2) {
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

      public void putAll(Map<? extends K, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2DoubleMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Double>> entrySet() {
         return this.reference2DoubleEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
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
         return System.identityHashCode(this.key) ^ HashCommon.double2int(this.value);
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

   public static class EmptyMap<K> extends Reference2DoubleFunctions.EmptyFunction<K> implements Reference2DoubleMap<K>, Serializable, Cloneable {
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

      public void putAll(Map<? extends K, ? extends Double> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      public DoubleCollection values() {
         return DoubleSets.EMPTY_SET;
      }

      public Object clone() {
         return Reference2DoubleMaps.EMPTY_MAP;
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
