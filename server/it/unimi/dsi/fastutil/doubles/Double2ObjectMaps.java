package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public final class Double2ObjectMaps {
   public static final Double2ObjectMaps.EmptyMap EMPTY_MAP = new Double2ObjectMaps.EmptyMap();

   private Double2ObjectMaps() {
      super();
   }

   public static <V> ObjectIterator<Double2ObjectMap.Entry<V>> fastIterator(Double2ObjectMap<V> var0) {
      ObjectSet var1 = var0.double2ObjectEntrySet();
      return var1 instanceof Double2ObjectMap.FastEntrySet ? ((Double2ObjectMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> void fastForEach(Double2ObjectMap<V> var0, Consumer<? super Double2ObjectMap.Entry<V>> var1) {
      ObjectSet var2 = var0.double2ObjectEntrySet();
      if (var2 instanceof Double2ObjectMap.FastEntrySet) {
         ((Double2ObjectMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <V> ObjectIterable<Double2ObjectMap.Entry<V>> fastIterable(Double2ObjectMap<V> var0) {
      final ObjectSet var1 = var0.double2ObjectEntrySet();
      return (ObjectIterable)(var1 instanceof Double2ObjectMap.FastEntrySet ? new ObjectIterable<Double2ObjectMap.Entry<V>>() {
         public ObjectIterator<Double2ObjectMap.Entry<V>> iterator() {
            return ((Double2ObjectMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Double2ObjectMap.Entry<V>> var1x) {
            ((Double2ObjectMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <V> Double2ObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Double2ObjectMap<V> singleton(double var0, V var2) {
      return new Double2ObjectMaps.Singleton(var0, var2);
   }

   public static <V> Double2ObjectMap<V> singleton(Double var0, V var1) {
      return new Double2ObjectMaps.Singleton(var0, var1);
   }

   public static <V> Double2ObjectMap<V> synchronize(Double2ObjectMap<V> var0) {
      return new Double2ObjectMaps.SynchronizedMap(var0);
   }

   public static <V> Double2ObjectMap<V> synchronize(Double2ObjectMap<V> var0, Object var1) {
      return new Double2ObjectMaps.SynchronizedMap(var0, var1);
   }

   public static <V> Double2ObjectMap<V> unmodifiable(Double2ObjectMap<V> var0) {
      return new Double2ObjectMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<V> extends Double2ObjectFunctions.UnmodifiableFunction<V> implements Double2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ObjectMap<V> map;
      protected transient ObjectSet<Double2ObjectMap.Entry<V>> entries;
      protected transient DoubleSet keys;
      protected transient ObjectCollection<V> values;

      protected UnmodifiableMap(Double2ObjectMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Double, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.double2ObjectEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, V>> entrySet() {
         return this.double2ObjectEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ObjectCollection<V> values() {
         return this.values == null ? ObjectCollections.unmodifiable(this.map.values()) : this.values;
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

      public V getOrDefault(double var1, V var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Double, ? super V> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Double, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(double var1, V var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(double var1, Object var3) {
         throw new UnsupportedOperationException();
      }

      public V replace(double var1, V var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(double var1, V var3, V var4) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(double var1, DoubleFunction<? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsentPartial(double var1, Double2ObjectFunction<? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(double var1, BiFunction<? super Double, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V compute(double var1, BiFunction<? super Double, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V merge(double var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V getOrDefault(Object var1, V var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V replace(Double var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Double var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Double var1, Function<? super Double, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Double var1, BiFunction<? super Double, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V compute(Double var1, BiFunction<? super Double, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V merge(Double var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<V> extends Double2ObjectFunctions.SynchronizedFunction<V> implements Double2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Double2ObjectMap<V> map;
      protected transient ObjectSet<Double2ObjectMap.Entry<V>> entries;
      protected transient DoubleSet keys;
      protected transient ObjectCollection<V> values;

      protected SynchronizedMap(Double2ObjectMap<V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Double2ObjectMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Double, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.double2ObjectEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, V>> entrySet() {
         return this.double2ObjectEntrySet();
      }

      public DoubleSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = DoubleSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ObjectCollection<V> values() {
         synchronized(this.sync) {
            return this.values == null ? ObjectCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public V getOrDefault(double var1, V var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Double, ? super V> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Double, ? super V, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public V putIfAbsent(double var1, V var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(double var1, Object var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public V replace(double var1, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(double var1, V var3, V var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public V computeIfAbsent(double var1, DoubleFunction<? extends V> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public V computeIfAbsentPartial(double var1, Double2ObjectFunction<? extends V> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public V computeIfPresent(double var1, BiFunction<? super Double, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public V compute(double var1, BiFunction<? super Double, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public V merge(double var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var3, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public V getOrDefault(Object var1, V var2) {
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
      public V replace(Double var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Double var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Double var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Double var1, Function<? super Double, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Double var1, BiFunction<? super Double, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V compute(Double var1, BiFunction<? super Double, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V merge(Double var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<V> extends Double2ObjectFunctions.Singleton<V> implements Double2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Double2ObjectMap.Entry<V>> entries;
      protected transient DoubleSet keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(double var1, V var3) {
         super(var1, var3);
      }

      public boolean containsValue(Object var1) {
         return Objects.equals(this.value, var1);
      }

      public void putAll(Map<? extends Double, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractDouble2ObjectMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Double, V>> entrySet() {
         return this.double2ObjectEntrySet();
      }

      public DoubleSet keySet() {
         if (this.keys == null) {
            this.keys = DoubleSets.singleton(this.key);
         }

         return this.keys;
      }

      public ObjectCollection<V> values() {
         if (this.values == null) {
            this.values = ObjectSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.double2int(this.key) ^ (this.value == null ? 0 : this.value.hashCode());
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

   public static class EmptyMap<V> extends Double2ObjectFunctions.EmptyFunction<V> implements Double2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Double, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Double2ObjectMap.Entry<V>> double2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public DoubleSet keySet() {
         return DoubleSets.EMPTY_SET;
      }

      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      public Object clone() {
         return Double2ObjectMaps.EMPTY_MAP;
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
