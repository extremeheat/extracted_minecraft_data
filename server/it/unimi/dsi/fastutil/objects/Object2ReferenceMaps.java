package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Object2ReferenceMaps {
   public static final Object2ReferenceMaps.EmptyMap EMPTY_MAP = new Object2ReferenceMaps.EmptyMap();

   private Object2ReferenceMaps() {
      super();
   }

   public static <K, V> ObjectIterator<Object2ReferenceMap.Entry<K, V>> fastIterator(Object2ReferenceMap<K, V> var0) {
      ObjectSet var1 = var0.object2ReferenceEntrySet();
      return var1 instanceof Object2ReferenceMap.FastEntrySet ? ((Object2ReferenceMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K, V> void fastForEach(Object2ReferenceMap<K, V> var0, Consumer<? super Object2ReferenceMap.Entry<K, V>> var1) {
      ObjectSet var2 = var0.object2ReferenceEntrySet();
      if (var2 instanceof Object2ReferenceMap.FastEntrySet) {
         ((Object2ReferenceMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K, V> ObjectIterable<Object2ReferenceMap.Entry<K, V>> fastIterable(Object2ReferenceMap<K, V> var0) {
      final ObjectSet var1 = var0.object2ReferenceEntrySet();
      return (ObjectIterable)(var1 instanceof Object2ReferenceMap.FastEntrySet ? new ObjectIterable<Object2ReferenceMap.Entry<K, V>>() {
         public ObjectIterator<Object2ReferenceMap.Entry<K, V>> iterator() {
            return ((Object2ReferenceMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2ReferenceMap.Entry<K, V>> var1x) {
            ((Object2ReferenceMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K, V> Object2ReferenceMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Object2ReferenceMap<K, V> singleton(K var0, V var1) {
      return new Object2ReferenceMaps.Singleton(var0, var1);
   }

   public static <K, V> Object2ReferenceMap<K, V> synchronize(Object2ReferenceMap<K, V> var0) {
      return new Object2ReferenceMaps.SynchronizedMap(var0);
   }

   public static <K, V> Object2ReferenceMap<K, V> synchronize(Object2ReferenceMap<K, V> var0, Object var1) {
      return new Object2ReferenceMaps.SynchronizedMap(var0, var1);
   }

   public static <K, V> Object2ReferenceMap<K, V> unmodifiable(Object2ReferenceMap<K, V> var0) {
      return new Object2ReferenceMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K, V> extends Object2ReferenceFunctions.UnmodifiableFunction<K, V> implements Object2ReferenceMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ReferenceMap<K, V> map;
      protected transient ObjectSet<Object2ReferenceMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Object2ReferenceMap<K, V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2ReferenceEntrySet());
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ReferenceEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
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

      public V getOrDefault(Object var1, V var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public V replace(K var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K, V> extends Object2ReferenceFunctions.SynchronizedFunction<K, V> implements Object2ReferenceMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ReferenceMap<K, V> map;
      protected transient ObjectSet<Object2ReferenceMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Object2ReferenceMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2ReferenceMap<K, V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ReferenceEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
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

      public V getOrDefault(Object var1, V var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super V> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super V, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public V putIfAbsent(K var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public V replace(K var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public V computeIfPresent(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public V compute(K var1, BiFunction<? super K, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public V merge(K var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K, V> extends Object2ReferenceFunctions.Singleton<K, V> implements Object2ReferenceMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2ReferenceMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(K var1, V var2) {
         super(var1, var2);
      }

      public boolean containsValue(Object var1) {
         return this.value == var1;
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2ReferenceMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ReferenceEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
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
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class EmptyMap<K, V> extends Object2ReferenceFunctions.EmptyFunction<K, V> implements Object2ReferenceMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ReferenceMap.Entry<K, V>> object2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2ReferenceMaps.EMPTY_MAP;
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
