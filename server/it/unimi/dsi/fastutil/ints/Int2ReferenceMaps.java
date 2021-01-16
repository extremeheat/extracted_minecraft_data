package it.unimi.dsi.fastutil.ints;

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
import java.util.function.IntFunction;

public final class Int2ReferenceMaps {
   public static final Int2ReferenceMaps.EmptyMap EMPTY_MAP = new Int2ReferenceMaps.EmptyMap();

   private Int2ReferenceMaps() {
      super();
   }

   public static <V> ObjectIterator<Int2ReferenceMap.Entry<V>> fastIterator(Int2ReferenceMap<V> var0) {
      ObjectSet var1 = var0.int2ReferenceEntrySet();
      return var1 instanceof Int2ReferenceMap.FastEntrySet ? ((Int2ReferenceMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> void fastForEach(Int2ReferenceMap<V> var0, Consumer<? super Int2ReferenceMap.Entry<V>> var1) {
      ObjectSet var2 = var0.int2ReferenceEntrySet();
      if (var2 instanceof Int2ReferenceMap.FastEntrySet) {
         ((Int2ReferenceMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <V> ObjectIterable<Int2ReferenceMap.Entry<V>> fastIterable(Int2ReferenceMap<V> var0) {
      final ObjectSet var1 = var0.int2ReferenceEntrySet();
      return (ObjectIterable)(var1 instanceof Int2ReferenceMap.FastEntrySet ? new ObjectIterable<Int2ReferenceMap.Entry<V>>() {
         public ObjectIterator<Int2ReferenceMap.Entry<V>> iterator() {
            return ((Int2ReferenceMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Int2ReferenceMap.Entry<V>> var1x) {
            ((Int2ReferenceMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <V> Int2ReferenceMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Int2ReferenceMap<V> singleton(int var0, V var1) {
      return new Int2ReferenceMaps.Singleton(var0, var1);
   }

   public static <V> Int2ReferenceMap<V> singleton(Integer var0, V var1) {
      return new Int2ReferenceMaps.Singleton(var0, var1);
   }

   public static <V> Int2ReferenceMap<V> synchronize(Int2ReferenceMap<V> var0) {
      return new Int2ReferenceMaps.SynchronizedMap(var0);
   }

   public static <V> Int2ReferenceMap<V> synchronize(Int2ReferenceMap<V> var0, Object var1) {
      return new Int2ReferenceMaps.SynchronizedMap(var0, var1);
   }

   public static <V> Int2ReferenceMap<V> unmodifiable(Int2ReferenceMap<V> var0) {
      return new Int2ReferenceMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<V> extends Int2ReferenceFunctions.UnmodifiableFunction<V> implements Int2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ReferenceMap<V> map;
      protected transient ObjectSet<Int2ReferenceMap.Entry<V>> entries;
      protected transient IntSet keys;
      protected transient ReferenceCollection<V> values;

      protected UnmodifiableMap(Int2ReferenceMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Integer, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2ReferenceEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, V>> entrySet() {
         return this.int2ReferenceEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
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

      public V getOrDefault(int var1, V var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Integer, ? super V> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Integer, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(int var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(int var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public V replace(int var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(int var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(int var1, IntFunction<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsentPartial(int var1, Int2ReferenceFunction<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(int var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(int var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(int var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
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
      public V replace(Integer var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Integer var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Integer var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Integer var1, Function<? super Integer, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Integer var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V compute(Integer var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V merge(Integer var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<V> extends Int2ReferenceFunctions.SynchronizedFunction<V> implements Int2ReferenceMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ReferenceMap<V> map;
      protected transient ObjectSet<Int2ReferenceMap.Entry<V>> entries;
      protected transient IntSet keys;
      protected transient ReferenceCollection<V> values;

      protected SynchronizedMap(Int2ReferenceMap<V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Int2ReferenceMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Integer, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2ReferenceEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, V>> entrySet() {
         return this.int2ReferenceEntrySet();
      }

      public IntSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = IntSets.synchronize(this.map.keySet(), this.sync);
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

      public V getOrDefault(int var1, V var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Integer, ? super V> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Integer, ? super V, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public V putIfAbsent(int var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(int var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public V replace(int var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(int var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public V computeIfAbsent(int var1, IntFunction<? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public V computeIfAbsentPartial(int var1, Int2ReferenceFunction<? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public V computeIfPresent(int var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public V compute(int var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public V merge(int var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
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
      public V replace(Integer var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Integer var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Integer var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Integer var1, Function<? super Integer, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Integer var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V compute(Integer var1, BiFunction<? super Integer, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V merge(Integer var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<V> extends Int2ReferenceFunctions.Singleton<V> implements Int2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2ReferenceMap.Entry<V>> entries;
      protected transient IntSet keys;
      protected transient ReferenceCollection<V> values;

      protected Singleton(int var1, V var2) {
         super(var1, var2);
      }

      public boolean containsValue(Object var1) {
         return this.value == var1;
      }

      public void putAll(Map<? extends Integer, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2ReferenceMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, V>> entrySet() {
         return this.int2ReferenceEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
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
         return this.key ^ (this.value == null ? 0 : System.identityHashCode(this.value));
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

   public static class EmptyMap<V> extends Int2ReferenceFunctions.EmptyFunction<V> implements Int2ReferenceMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Integer, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2ReferenceMap.Entry<V>> int2ReferenceEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      public ReferenceCollection<V> values() {
         return ReferenceSets.EMPTY_SET;
      }

      public Object clone() {
         return Int2ReferenceMaps.EMPTY_MAP;
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
