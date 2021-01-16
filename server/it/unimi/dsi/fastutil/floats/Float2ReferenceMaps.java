package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.ReferenceCollections;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;

public final class Float2ReferenceMaps {
   public static final Float2ReferenceMaps.EmptyMap EMPTY_MAP = new Float2ReferenceMaps.EmptyMap();

   private Float2ReferenceMaps() {
      super();
   }

   public static <V> ObjectIterator<Float2ReferenceMap.Entry<V>> fastIterator(Float2ReferenceMap<V> var0) {
      ObjectSet var1 = var0.float2ReferenceEntrySet();
      return var1 instanceof Float2ReferenceMap.FastEntrySet ? ((Float2ReferenceMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> void fastForEach(Float2ReferenceMap<V> var0, Consumer<? super Float2ReferenceMap.Entry<V>> var1) {
      ObjectSet var2 = var0.float2ReferenceEntrySet();
      if (var2 instanceof Float2ReferenceMap.FastEntrySet) {
         ((Float2ReferenceMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <V> ObjectIterable<Float2ReferenceMap.Entry<V>> fastIterable(Float2ReferenceMap<V> var0) {
      final ObjectSet var1 = var0.float2ReferenceEntrySet();
      return (ObjectIterable)(var1 instanceof Float2ReferenceMap.FastEntrySet ? new ObjectIterable<Float2ReferenceMap.Entry<V>>() {
         public ObjectIterator<Float2ReferenceMap.Entry<V>> iterator() {
            return ((Float2ReferenceMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Float2ReferenceMap.Entry<V>> var1x) {
            ((Float2ReferenceMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <V> Float2ReferenceMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Float2ReferenceMap<V> singleton(float var0, V var1) {
      return new Float2ReferenceMaps.Singleton(var0, var1);
   }

   public static <V> Float2ReferenceMap<V> singleton(Float var0, V var1) {
      return new Float2ReferenceMaps.Singleton(var0, var1);
   }

   public static <V> Float2ReferenceMap<V> synchronize(Float2ReferenceMap<V> var0) {
      return new Float2ReferenceMaps.SynchronizedMap(var0);
   }

   public static <V> Float2ReferenceMap<V> synchronize(Float2ReferenceMap<V> var0, Object var1) {
      return new Float2ReferenceMaps.SynchronizedMap(var0, var1);
   }

   public static <V> Float2ReferenceMap<V> unmodifiable(Float2ReferenceMap<V> var0) {
      return new Float2ReferenceMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<V> extends Float2ReferenceFunctions.UnmodifiableFunction<V> implements Float2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ReferenceMap<V> map;
      protected transient ObjectSet<Float2ReferenceMap.Entry<V>> entries;
      protected transient FloatSet keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Float2ReferenceMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Float, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2ReferenceEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, V>> entrySet() {
         return this.float2ReferenceEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ReferenceCollection<V> values() {
         return this.values == null ? ReferenceCollections.unmodifiable(this.map.values()) : this.values;
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

      public V getOrDefault(float var1, V var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Float, ? super V> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Float, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(float var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public V replace(float var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(float var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(float var1, DoubleFunction<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsentPartial(float var1, Float2ReferenceFunction<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(float var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
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
      public V replace(Float var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Float var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Float var1, Function<? super Float, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V compute(Float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V merge(Float var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<V> extends Float2ReferenceFunctions.SynchronizedFunction<V> implements Float2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ReferenceMap<V> map;
      protected transient ObjectSet<Float2ReferenceMap.Entry<V>> entries;
      protected transient FloatSet keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Float2ReferenceMap<V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Float2ReferenceMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Float, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, V>> entrySet() {
         return this.float2ReferenceEntrySet();
      }

      public FloatSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ReferenceCollection<V> values() {
         synchronized(this.sync) {
            return this.values == null ? ReferenceCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public V getOrDefault(float var1, V var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Float, ? super V> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Float, ? super V, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public V putIfAbsent(float var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(float var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public V replace(float var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(float var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public V computeIfAbsent(float var1, DoubleFunction<? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public V computeIfAbsentPartial(float var1, Float2ReferenceFunction<? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public V computeIfPresent(float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public V compute(float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public V merge(float var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
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
      public V replace(Float var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Float var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Float var1, Function<? super Float, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V compute(Float var1, BiFunction<? super Float, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V merge(Float var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<V> extends Float2ReferenceFunctions.Singleton<V> implements Float2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2ReferenceMap.Entry<V>> entries;
      protected transient FloatSet keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(float var1, V var2) {
         super(var1, var2);
      }

      public boolean containsValue(Object var1) {
         return this.value == var1;
      }

      public void putAll(Map<? extends Float, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2ReferenceMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, V>> entrySet() {
         return this.float2ReferenceEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
         }

         return this.keys;
      }

      public ReferenceCollection<V> values() {
         if (this.values == null) {
            this.values = ReferenceSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class EmptyMap<V> extends Float2ReferenceFunctions.EmptyFunction<V> implements Float2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Float, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ReferenceMap.Entry<V>> float2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      public Object clone() {
         return Float2ReferenceMaps.EMPTY_MAP;
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
