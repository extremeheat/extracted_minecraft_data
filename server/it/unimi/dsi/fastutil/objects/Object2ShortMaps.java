package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortSets;
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

public final class Object2ShortMaps {
   public static final Object2ShortMaps.EmptyMap EMPTY_MAP = new Object2ShortMaps.EmptyMap();

   private Object2ShortMaps() {
      super();
   }

   public static <K> ObjectIterator<Object2ShortMap.Entry<K>> fastIterator(Object2ShortMap<K> var0) {
      ObjectSet var1 = var0.object2ShortEntrySet();
      return var1 instanceof Object2ShortMap.FastEntrySet ? ((Object2ShortMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Object2ShortMap<K> var0, Consumer<? super Object2ShortMap.Entry<K>> var1) {
      ObjectSet var2 = var0.object2ShortEntrySet();
      if (var2 instanceof Object2ShortMap.FastEntrySet) {
         ((Object2ShortMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Object2ShortMap.Entry<K>> fastIterable(Object2ShortMap<K> var0) {
      final ObjectSet var1 = var0.object2ShortEntrySet();
      return (ObjectIterable)(var1 instanceof Object2ShortMap.FastEntrySet ? new ObjectIterable<Object2ShortMap.Entry<K>>() {
         public ObjectIterator<Object2ShortMap.Entry<K>> iterator() {
            return ((Object2ShortMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2ShortMap.Entry<K>> var1x) {
            ((Object2ShortMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Object2ShortMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2ShortMap<K> singleton(K var0, short var1) {
      return new Object2ShortMaps.Singleton(var0, var1);
   }

   public static <K> Object2ShortMap<K> singleton(K var0, Short var1) {
      return new Object2ShortMaps.Singleton(var0, var1);
   }

   public static <K> Object2ShortMap<K> synchronize(Object2ShortMap<K> var0) {
      return new Object2ShortMaps.SynchronizedMap(var0);
   }

   public static <K> Object2ShortMap<K> synchronize(Object2ShortMap<K> var0, Object var1) {
      return new Object2ShortMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Object2ShortMap<K> unmodifiable(Object2ShortMap<K> var0) {
      return new Object2ShortMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Object2ShortFunctions.UnmodifiableFunction<K> implements Object2ShortMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ShortMap<K> map;
      protected transient ObjectSet<Object2ShortMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Object2ShortMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(short var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2ShortEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Short>> entrySet() {
         return this.object2ShortEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ShortCollection values() {
         return this.values == null ? ShortCollections.unmodifiable(this.map.values()) : this.values;
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

      public short getOrDefault(Object var1, short var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Short> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Short, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public short putIfAbsent(K var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public short replace(K var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, short var2, short var3) {
         throw new UnsupportedOperationException();
      }

      public short computeShortIfAbsent(K var1, ToIntFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public short computeShortIfAbsentPartial(K var1, Object2ShortFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public short computeShortIfPresent(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short computeShort(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short mergeShort(K var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short getOrDefault(Object var1, Short var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short replace(K var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Short var2, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(K var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      public Short computeIfAbsent(K var1, Function<? super K, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public Short computeIfPresent(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public Short compute(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short merge(K var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Object2ShortFunctions.SynchronizedFunction<K> implements Object2ShortMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ShortMap<K> map;
      protected transient ObjectSet<Object2ShortMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Object2ShortMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2ShortMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(short var1) {
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

      public void putAll(Map<? extends K, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Short>> entrySet() {
         return this.object2ShortEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ShortCollection values() {
         synchronized(this.sync) {
            return this.values == null ? ShortCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public short getOrDefault(Object var1, short var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Short> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Short, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public short putIfAbsent(K var1, short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, short var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public short replace(K var1, short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, short var2, short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public short computeShortIfAbsent(K var1, ToIntFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeShortIfAbsent(var1, var2);
         }
      }

      public short computeShortIfAbsentPartial(K var1, Object2ShortFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeShortIfAbsentPartial(var1, var2);
         }
      }

      public short computeShortIfPresent(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeShortIfPresent(var1, var2);
         }
      }

      public short computeShort(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeShort(var1, var2);
         }
      }

      public short mergeShort(K var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.mergeShort(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short getOrDefault(Object var1, Short var2) {
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
      public Short replace(K var1, Short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Short var2, Short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(K var1, Short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Short computeIfAbsent(K var1, Function<? super K, ? extends Short> var2) {
         synchronized(this.sync) {
            return (Short)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Short computeIfPresent(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return (Short)this.map.computeIfPresent(var1, var2);
         }
      }

      public Short compute(K var1, BiFunction<? super K, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return (Short)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short merge(K var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Object2ShortFunctions.Singleton<K> implements Object2ShortMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2ShortMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ShortCollection values;

      protected Singleton(K var1, short var2) {
         super(var1, var2);
      }

      public boolean containsValue(short var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Short)var1 == this.value;
      }

      public void putAll(Map<? extends K, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Short>> entrySet() {
         return this.object2ShortEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      public ShortCollection values() {
         if (this.values == null) {
            this.values = ShortSets.singleton(this.value);
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

   public static class EmptyMap<K> extends Object2ShortFunctions.EmptyFunction<K> implements Object2ShortMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(short var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ShortMap.Entry<K>> object2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2ShortMaps.EMPTY_MAP;
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
