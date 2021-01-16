package it.unimi.dsi.fastutil.longs;

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
import java.util.function.Function;
import java.util.function.LongFunction;

public final class Long2ReferenceMaps {
   public static final Long2ReferenceMaps.EmptyMap EMPTY_MAP = new Long2ReferenceMaps.EmptyMap();

   private Long2ReferenceMaps() {
      super();
   }

   public static <V> ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator(Long2ReferenceMap<V> var0) {
      ObjectSet var1 = var0.long2ReferenceEntrySet();
      return var1 instanceof Long2ReferenceMap.FastEntrySet ? ((Long2ReferenceMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> void fastForEach(Long2ReferenceMap<V> var0, Consumer<? super Long2ReferenceMap.Entry<V>> var1) {
      ObjectSet var2 = var0.long2ReferenceEntrySet();
      if (var2 instanceof Long2ReferenceMap.FastEntrySet) {
         ((Long2ReferenceMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <V> ObjectIterable<Long2ReferenceMap.Entry<V>> fastIterable(Long2ReferenceMap<V> var0) {
      final ObjectSet var1 = var0.long2ReferenceEntrySet();
      return (ObjectIterable)(var1 instanceof Long2ReferenceMap.FastEntrySet ? new ObjectIterable<Long2ReferenceMap.Entry<V>>() {
         public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator() {
            return ((Long2ReferenceMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Long2ReferenceMap.Entry<V>> var1x) {
            ((Long2ReferenceMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <V> Long2ReferenceMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Long2ReferenceMap<V> singleton(long var0, V var2) {
      return new Long2ReferenceMaps.Singleton(var0, var2);
   }

   public static <V> Long2ReferenceMap<V> singleton(Long var0, V var1) {
      return new Long2ReferenceMaps.Singleton(var0, var1);
   }

   public static <V> Long2ReferenceMap<V> synchronize(Long2ReferenceMap<V> var0) {
      return new Long2ReferenceMaps.SynchronizedMap(var0);
   }

   public static <V> Long2ReferenceMap<V> synchronize(Long2ReferenceMap<V> var0, Object var1) {
      return new Long2ReferenceMaps.SynchronizedMap(var0, var1);
   }

   public static <V> Long2ReferenceMap<V> unmodifiable(Long2ReferenceMap<V> var0) {
      return new Long2ReferenceMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<V> extends Long2ReferenceFunctions.UnmodifiableFunction<V> implements Long2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ReferenceMap<V> map;
      protected transient ObjectSet<Long2ReferenceMap.Entry<V>> entries;
      protected transient LongSet keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Long2ReferenceMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Long, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.long2ReferenceEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, V>> entrySet() {
         return this.long2ReferenceEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.unmodifiable(this.map.keySet());
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

      public V getOrDefault(long var1, V var3) {
         return this.map.getOrDefault(var1, var3);
      }

      public void forEach(BiConsumer<? super Long, ? super V> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(long var1, V var3) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(long var1, Object var3) {
         throw new UnsupportedOperationException();
      }

      public V replace(long var1, V var3) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(long var1, V var3, V var4) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(long var1, LongFunction<? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsentPartial(long var1, Long2ReferenceFunction<? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V compute(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }

      public V merge(long var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
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
      public V replace(Long var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Long var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Long var1, Function<? super Long, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Long var1, BiFunction<? super Long, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V compute(Long var1, BiFunction<? super Long, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V merge(Long var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<V> extends Long2ReferenceFunctions.SynchronizedFunction<V> implements Long2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ReferenceMap<V> map;
      protected transient ObjectSet<Long2ReferenceMap.Entry<V>> entries;
      protected transient LongSet keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Long2ReferenceMap<V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Long2ReferenceMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Long, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.long2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, V>> entrySet() {
         return this.long2ReferenceEntrySet();
      }

      public LongSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = LongSets.synchronize(this.map.keySet(), this.sync);
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

      public V getOrDefault(long var1, V var3) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var3);
         }
      }

      public void forEach(BiConsumer<? super Long, ? super V> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Long, ? super V, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public V putIfAbsent(long var1, V var3) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var3);
         }
      }

      public boolean remove(long var1, Object var3) {
         synchronized(this.sync) {
            return this.map.remove(var1, var3);
         }
      }

      public V replace(long var1, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3);
         }
      }

      public boolean replace(long var1, V var3, V var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var3, var4);
         }
      }

      public V computeIfAbsent(long var1, LongFunction<? extends V> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var3);
         }
      }

      public V computeIfAbsentPartial(long var1, Long2ReferenceFunction<? extends V> var3) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var3);
         }
      }

      public V computeIfPresent(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var3);
         }
      }

      public V compute(long var1, BiFunction<? super Long, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.compute(var1, var3);
         }
      }

      public V merge(long var1, V var3, BiFunction<? super V, ? super V, ? extends V> var4) {
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
      public V replace(Long var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Long var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Long var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Long var1, Function<? super Long, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Long var1, BiFunction<? super Long, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V compute(Long var1, BiFunction<? super Long, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V merge(Long var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<V> extends Long2ReferenceFunctions.Singleton<V> implements Long2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Long2ReferenceMap.Entry<V>> entries;
      protected transient LongSet keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(long var1, V var3) {
         super(var1, var3);
      }

      public boolean containsValue(Object var1) {
         return this.value == var1;
      }

      public void putAll(Map<? extends Long, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractLong2ReferenceMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Long, V>> entrySet() {
         return this.long2ReferenceEntrySet();
      }

      public LongSet keySet() {
         if (this.keys == null) {
            this.keys = LongSets.singleton(this.key);
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
         return HashCommon.long2int(this.key) ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class EmptyMap<V> extends Long2ReferenceFunctions.EmptyFunction<V> implements Long2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Long, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Long2ReferenceMap.Entry<V>> long2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongSet keySet() {
         return LongSets.EMPTY_SET;
      }

      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      public Object clone() {
         return Long2ReferenceMaps.EMPTY_MAP;
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
