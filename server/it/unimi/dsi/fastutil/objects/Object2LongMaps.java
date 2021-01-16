package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public final class Object2LongMaps {
   public static final Object2LongMaps.EmptyMap EMPTY_MAP = new Object2LongMaps.EmptyMap();

   private Object2LongMaps() {
      super();
   }

   public static <K> ObjectIterator<Object2LongMap.Entry<K>> fastIterator(Object2LongMap<K> var0) {
      ObjectSet var1 = var0.object2LongEntrySet();
      return var1 instanceof Object2LongMap.FastEntrySet ? ((Object2LongMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Object2LongMap<K> var0, Consumer<? super Object2LongMap.Entry<K>> var1) {
      ObjectSet var2 = var0.object2LongEntrySet();
      if (var2 instanceof Object2LongMap.FastEntrySet) {
         ((Object2LongMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Object2LongMap.Entry<K>> fastIterable(Object2LongMap<K> var0) {
      final ObjectSet var1 = var0.object2LongEntrySet();
      return (ObjectIterable)(var1 instanceof Object2LongMap.FastEntrySet ? new ObjectIterable<Object2LongMap.Entry<K>>() {
         public ObjectIterator<Object2LongMap.Entry<K>> iterator() {
            return ((Object2LongMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2LongMap.Entry<K>> var1x) {
            ((Object2LongMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Object2LongMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2LongMap<K> singleton(K var0, long var1) {
      return new Object2LongMaps.Singleton(var0, var1);
   }

   public static <K> Object2LongMap<K> singleton(K var0, Long var1) {
      return new Object2LongMaps.Singleton(var0, var1);
   }

   public static <K> Object2LongMap<K> synchronize(Object2LongMap<K> var0) {
      return new Object2LongMaps.SynchronizedMap(var0);
   }

   public static <K> Object2LongMap<K> synchronize(Object2LongMap<K> var0, Object var1) {
      return new Object2LongMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Object2LongMap<K> unmodifiable(Object2LongMap<K> var0) {
      return new Object2LongMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Object2LongFunctions.UnmodifiableFunction<K> implements Object2LongMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongMap<K> map;
      protected transient ObjectSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Object2LongMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(long var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2LongEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public LongCollection values() {
         return this.values == null ? LongCollections.unmodifiable(this.map.values()) : this.values;
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

      public long getOrDefault(Object var1, long var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Long> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Long, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public long putIfAbsent(K var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long replace(K var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, long var2, long var4) {
         throw new UnsupportedOperationException();
      }

      public long computeLongIfAbsent(K var1, ToLongFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public long computeLongIfAbsentPartial(K var1, Object2LongFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public long computeLongIfPresent(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long computeLong(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long mergeLong(K var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long getOrDefault(Object var1, Long var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long replace(K var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Long var2, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(K var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      public Long computeIfAbsent(K var1, Function<? super K, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public Long computeIfPresent(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public Long compute(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long merge(K var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Object2LongFunctions.SynchronizedFunction<K> implements Object2LongMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2LongMap<K> map;
      protected transient ObjectSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Object2LongMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2LongMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(long var1) {
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

      public void putAll(Map<? extends K, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public LongCollection values() {
         synchronized(this.sync) {
            return this.values == null ? LongCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public long getOrDefault(Object var1, long var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Long> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Long, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public long putIfAbsent(K var1, long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, long var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public long replace(K var1, long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, long var2, long var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var4);
         }
      }

      public long computeLongIfAbsent(K var1, ToLongFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeLongIfAbsent(var1, var2);
         }
      }

      public long computeLongIfAbsentPartial(K var1, Object2LongFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeLongIfAbsentPartial(var1, var2);
         }
      }

      public long computeLongIfPresent(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeLongIfPresent(var1, var2);
         }
      }

      public long computeLong(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeLong(var1, var2);
         }
      }

      public long mergeLong(K var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
         synchronized(this.sync) {
            return this.map.mergeLong(var1, var2, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long getOrDefault(Object var1, Long var2) {
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
      public Long replace(K var1, Long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Long var2, Long var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(K var1, Long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Long computeIfAbsent(K var1, Function<? super K, ? extends Long> var2) {
         synchronized(this.sync) {
            return (Long)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Long computeIfPresent(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return (Long)this.map.computeIfPresent(var1, var2);
         }
      }

      public Long compute(K var1, BiFunction<? super K, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return (Long)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long merge(K var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Object2LongFunctions.Singleton<K> implements Object2LongMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2LongMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient LongCollection values;

      protected Singleton(K var1, long var2) {
         super(var1, var2);
      }

      public boolean containsValue(long var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Long)var1 == this.value;
      }

      public void putAll(Map<? extends K, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Long>> entrySet() {
         return this.object2LongEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      public LongCollection values() {
         if (this.values == null) {
            this.values = LongSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ HashCommon.long2int(this.value);
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

   public static class EmptyMap<K> extends Object2LongFunctions.EmptyFunction<K> implements Object2LongMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(long var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2LongMap.Entry<K>> object2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2LongMaps.EMPTY_MAP;
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
