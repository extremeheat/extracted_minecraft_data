package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public final class Object2IntMaps {
   public static final Object2IntMaps.EmptyMap EMPTY_MAP = new Object2IntMaps.EmptyMap();

   private Object2IntMaps() {
      super();
   }

   public static <K> ObjectIterator<Object2IntMap.Entry<K>> fastIterator(Object2IntMap<K> var0) {
      ObjectSet var1 = var0.object2IntEntrySet();
      return var1 instanceof Object2IntMap.FastEntrySet ? ((Object2IntMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Object2IntMap<K> var0, Consumer<? super Object2IntMap.Entry<K>> var1) {
      ObjectSet var2 = var0.object2IntEntrySet();
      if (var2 instanceof Object2IntMap.FastEntrySet) {
         ((Object2IntMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Object2IntMap.Entry<K>> fastIterable(Object2IntMap<K> var0) {
      final ObjectSet var1 = var0.object2IntEntrySet();
      return (ObjectIterable)(var1 instanceof Object2IntMap.FastEntrySet ? new ObjectIterable<Object2IntMap.Entry<K>>() {
         public ObjectIterator<Object2IntMap.Entry<K>> iterator() {
            return ((Object2IntMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2IntMap.Entry<K>> var1x) {
            ((Object2IntMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Object2IntMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2IntMap<K> singleton(K var0, int var1) {
      return new Object2IntMaps.Singleton(var0, var1);
   }

   public static <K> Object2IntMap<K> singleton(K var0, Integer var1) {
      return new Object2IntMaps.Singleton(var0, var1);
   }

   public static <K> Object2IntMap<K> synchronize(Object2IntMap<K> var0) {
      return new Object2IntMaps.SynchronizedMap(var0);
   }

   public static <K> Object2IntMap<K> synchronize(Object2IntMap<K> var0, Object var1) {
      return new Object2IntMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Object2IntMap<K> unmodifiable(Object2IntMap<K> var0) {
      return new Object2IntMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Object2IntFunctions.UnmodifiableFunction<K> implements Object2IntMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2IntMap<K> map;
      protected transient ObjectSet<Object2IntMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Object2IntMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(int var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2IntEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Integer>> entrySet() {
         return this.object2IntEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public IntCollection values() {
         return this.values == null ? IntCollections.unmodifiable(this.map.values()) : this.values;
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

      public int getOrDefault(Object var1, int var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Integer> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Integer, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public int putIfAbsent(K var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int replace(K var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, int var2, int var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIntIfAbsent(K var1, ToIntFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIntIfAbsentPartial(K var1, Object2IntFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIntIfPresent(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int computeInt(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int mergeInt(K var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer getOrDefault(Object var1, Integer var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer replace(K var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Integer var2, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(K var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      public Integer computeIfAbsent(K var1, Function<? super K, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public Integer computeIfPresent(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public Integer compute(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(K var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Object2IntFunctions.SynchronizedFunction<K> implements Object2IntMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2IntMap<K> map;
      protected transient ObjectSet<Object2IntMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Object2IntMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2IntMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(int var1) {
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

      public void putAll(Map<? extends K, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Integer>> entrySet() {
         return this.object2IntEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public IntCollection values() {
         synchronized(this.sync) {
            return this.values == null ? IntCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public int getOrDefault(Object var1, int var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Integer> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Integer, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public int putIfAbsent(K var1, int var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, int var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public int replace(K var1, int var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, int var2, int var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public int computeIntIfAbsent(K var1, ToIntFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeIntIfAbsent(var1, var2);
         }
      }

      public int computeIntIfAbsentPartial(K var1, Object2IntFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeIntIfAbsentPartial(var1, var2);
         }
      }

      public int computeIntIfPresent(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIntIfPresent(var1, var2);
         }
      }

      public int computeInt(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeInt(var1, var2);
         }
      }

      public int mergeInt(K var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.mergeInt(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer getOrDefault(Object var1, Integer var2) {
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
      public Integer replace(K var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Integer var2, Integer var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(K var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Integer computeIfAbsent(K var1, Function<? super K, ? extends Integer> var2) {
         synchronized(this.sync) {
            return (Integer)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Integer computeIfPresent(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return (Integer)this.map.computeIfPresent(var1, var2);
         }
      }

      public Integer compute(K var1, BiFunction<? super K, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return (Integer)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(K var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Object2IntFunctions.Singleton<K> implements Object2IntMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2IntMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient IntCollection values;

      protected Singleton(K var1, int var2) {
         super(var1, var2);
      }

      public boolean containsValue(int var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Integer)var1 == this.value;
      }

      public void putAll(Map<? extends K, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Integer>> entrySet() {
         return this.object2IntEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      public IntCollection values() {
         if (this.values == null) {
            this.values = IntSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ this.value;
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

   public static class EmptyMap<K> extends Object2IntFunctions.EmptyFunction<K> implements Object2IntMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(int var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2IntMaps.EMPTY_MAP;
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
