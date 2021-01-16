package it.unimi.dsi.fastutil.objects;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Reference2ReferenceMaps {
   public static final Reference2ReferenceMaps.EmptyMap EMPTY_MAP = new Reference2ReferenceMaps.EmptyMap();

   private Reference2ReferenceMaps() {
      super();
   }

   public static <K, V> ObjectIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator(Reference2ReferenceMap<K, V> var0) {
      ObjectSet var1 = var0.reference2ReferenceEntrySet();
      return var1 instanceof Reference2ReferenceMap.FastEntrySet ? ((Reference2ReferenceMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K, V> void fastForEach(Reference2ReferenceMap<K, V> var0, Consumer<? super Reference2ReferenceMap.Entry<K, V>> var1) {
      ObjectSet var2 = var0.reference2ReferenceEntrySet();
      if (var2 instanceof Reference2ReferenceMap.FastEntrySet) {
         ((Reference2ReferenceMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K, V> ObjectIterable<Reference2ReferenceMap.Entry<K, V>> fastIterable(Reference2ReferenceMap<K, V> var0) {
      final ObjectSet var1 = var0.reference2ReferenceEntrySet();
      return (ObjectIterable)(var1 instanceof Reference2ReferenceMap.FastEntrySet ? new ObjectIterable<Reference2ReferenceMap.Entry<K, V>>() {
         public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> iterator() {
            return ((Reference2ReferenceMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> var1x) {
            ((Reference2ReferenceMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K, V> Reference2ReferenceMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Reference2ReferenceMap<K, V> singleton(K var0, V var1) {
      return new Reference2ReferenceMaps.Singleton(var0, var1);
   }

   public static <K, V> Reference2ReferenceMap<K, V> synchronize(Reference2ReferenceMap<K, V> var0) {
      return new Reference2ReferenceMaps.SynchronizedMap(var0);
   }

   public static <K, V> Reference2ReferenceMap<K, V> synchronize(Reference2ReferenceMap<K, V> var0, Object var1) {
      return new Reference2ReferenceMaps.SynchronizedMap(var0, var1);
   }

   public static <K, V> Reference2ReferenceMap<K, V> unmodifiable(Reference2ReferenceMap<K, V> var0) {
      return new Reference2ReferenceMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K, V> extends Reference2ReferenceFunctions.UnmodifiableFunction<K, V> implements Reference2ReferenceMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ReferenceMap<K, V> map;
      protected transient ObjectSet<Reference2ReferenceMap.Entry<K, V>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Reference2ReferenceMap<K, V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2ReferenceEntrySet());
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.reference2ReferenceEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
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

   public static class SynchronizedMap<K, V> extends Reference2ReferenceFunctions.SynchronizedFunction<K, V> implements Reference2ReferenceMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ReferenceMap<K, V> map;
      protected transient ObjectSet<Reference2ReferenceMap.Entry<K, V>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Reference2ReferenceMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Reference2ReferenceMap<K, V> var1) {
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

      public ObjectSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.reference2ReferenceEntrySet();
      }

      public ReferenceSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
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

   public static class Singleton<K, V> extends Reference2ReferenceFunctions.Singleton<K, V> implements Reference2ReferenceMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2ReferenceMap.Entry<K, V>> entries;
      protected transient ReferenceSet<K> keys;
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

      public ObjectSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2ReferenceMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      public ObjectSet<Entry<K, V>> entrySet() {
         return this.reference2ReferenceEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
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
         return System.identityHashCode(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class EmptyMap<K, V> extends Reference2ReferenceFunctions.EmptyFunction<K, V> implements Reference2ReferenceMap<K, V>, Serializable, Cloneable {
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

      public ObjectSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      public Object clone() {
         return Reference2ReferenceMaps.EMPTY_MAP;
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
