package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Object2ObjectMaps {
   public static final Object2ObjectMaps.EmptyMap EMPTY_MAP = new Object2ObjectMaps.EmptyMap();

   private Object2ObjectMaps() {
      super();
   }

   public static <K, V> ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Object2ObjectMap<K, V> var0) {
      ObjectSet var1 = var0.object2ObjectEntrySet();
      return var1 instanceof Object2ObjectMap.FastEntrySet ? ((Object2ObjectMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K, V> void fastForEach(Object2ObjectMap<K, V> var0, Consumer<? super Object2ObjectMap.Entry<K, V>> var1) {
      ObjectSet var2 = var0.object2ObjectEntrySet();
      if (var2 instanceof Object2ObjectMap.FastEntrySet) {
         ((Object2ObjectMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K, V> ObjectIterable<Object2ObjectMap.Entry<K, V>> fastIterable(Object2ObjectMap<K, V> var0) {
      final ObjectSet var1 = var0.object2ObjectEntrySet();
      return (ObjectIterable)(var1 instanceof Object2ObjectMap.FastEntrySet ? new ObjectIterable<Object2ObjectMap.Entry<K, V>>() {
         public ObjectIterator<Object2ObjectMap.Entry<K, V>> iterator() {
            return ((Object2ObjectMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2ObjectMap.Entry<K, V>> var1x) {
            ((Object2ObjectMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K, V> Object2ObjectMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Object2ObjectMap<K, V> singleton(K var0, V var1) {
      return new Object2ObjectMaps.Singleton(var0, var1);
   }

   public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> var0) {
      return new Object2ObjectMaps.SynchronizedMap(var0);
   }

   public static <K, V> Object2ObjectMap<K, V> synchronize(Object2ObjectMap<K, V> var0, Object var1) {
      return new Object2ObjectMaps.SynchronizedMap(var0, var1);
   }

   public static <K, V> Object2ObjectMap<K, V> unmodifiable(Object2ObjectMap<K, V> var0) {
      return new Object2ObjectMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K, V> extends Object2ObjectFunctions.UnmodifiableFunction<K, V> implements Object2ObjectMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ObjectMap<K, V> map;
      protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ObjectCollection<V> values;

      protected UnmodifiableMap(Object2ObjectMap<K, V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2ObjectEntrySet());
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
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

   public static class SynchronizedMap<K, V> extends Object2ObjectFunctions.SynchronizedFunction<K, V> implements Object2ObjectMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ObjectMap<K, V> map;
      protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ObjectCollection<V> values;

      protected SynchronizedMap(Object2ObjectMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2ObjectMap<K, V> var1) {
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

      public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2ObjectEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
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

   public static class Singleton<K, V> extends Object2ObjectFunctions.Singleton<K, V> implements Object2ObjectMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2ObjectMap.Entry<K, V>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(K var1, V var2) {
         super(var1, var2);
      }

      public boolean containsValue(Object var1) {
         return Objects.equals(this.value, var1);
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2ObjectMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
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
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
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

   public static class EmptyMap<K, V> extends Object2ObjectFunctions.EmptyFunction<K, V> implements Object2ObjectMap<K, V>, Serializable, Cloneable {
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

      public ObjectSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2ObjectMaps.EMPTY_MAP;
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
