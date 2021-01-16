package it.unimi.dsi.fastutil.bytes;

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
import java.util.function.Function;
import java.util.function.IntFunction;

public final class Byte2ObjectMaps {
   public static final Byte2ObjectMaps.EmptyMap EMPTY_MAP = new Byte2ObjectMaps.EmptyMap();

   private Byte2ObjectMaps() {
      super();
   }

   public static <V> ObjectIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectMap<V> var0) {
      ObjectSet var1 = var0.byte2ObjectEntrySet();
      return var1 instanceof Byte2ObjectMap.FastEntrySet ? ((Byte2ObjectMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> void fastForEach(Byte2ObjectMap<V> var0, Consumer<? super Byte2ObjectMap.Entry<V>> var1) {
      ObjectSet var2 = var0.byte2ObjectEntrySet();
      if (var2 instanceof Byte2ObjectMap.FastEntrySet) {
         ((Byte2ObjectMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <V> ObjectIterable<Byte2ObjectMap.Entry<V>> fastIterable(Byte2ObjectMap<V> var0) {
      final ObjectSet var1 = var0.byte2ObjectEntrySet();
      return (ObjectIterable)(var1 instanceof Byte2ObjectMap.FastEntrySet ? new ObjectIterable<Byte2ObjectMap.Entry<V>>() {
         public ObjectIterator<Byte2ObjectMap.Entry<V>> iterator() {
            return ((Byte2ObjectMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Byte2ObjectMap.Entry<V>> var1x) {
            ((Byte2ObjectMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <V> Byte2ObjectMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Byte2ObjectMap<V> singleton(byte var0, V var1) {
      return new Byte2ObjectMaps.Singleton(var0, var1);
   }

   public static <V> Byte2ObjectMap<V> singleton(Byte var0, V var1) {
      return new Byte2ObjectMaps.Singleton(var0, var1);
   }

   public static <V> Byte2ObjectMap<V> synchronize(Byte2ObjectMap<V> var0) {
      return new Byte2ObjectMaps.SynchronizedMap(var0);
   }

   public static <V> Byte2ObjectMap<V> synchronize(Byte2ObjectMap<V> var0, Object var1) {
      return new Byte2ObjectMaps.SynchronizedMap(var0, var1);
   }

   public static <V> Byte2ObjectMap<V> unmodifiable(Byte2ObjectMap<V> var0) {
      return new Byte2ObjectMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<V> extends Byte2ObjectFunctions.UnmodifiableFunction<V> implements Byte2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectMap<V> map;
      protected transient ObjectSet<Byte2ObjectMap.Entry<V>> entries;
      protected transient ByteSet keys;
      protected transient ObjectCollection<V> values;

      protected UnmodifiableMap(Byte2ObjectMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Byte, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2ObjectEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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

      public V getOrDefault(byte var1, V var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Byte, ? super V> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Byte, ? super V, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public V putIfAbsent(byte var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      public V replace(byte var1, V var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsent(byte var1, IntFunction<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfAbsentPartial(byte var1, Byte2ObjectFunction<? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V computeIfPresent(byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V compute(byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      public V merge(byte var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
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
      public V replace(Byte var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, V var2, V var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Byte var1, V var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Byte var1, Function<? super Byte, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V compute(Byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public V merge(Byte var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<V> extends Byte2ObjectFunctions.SynchronizedFunction<V> implements Byte2ObjectMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectMap<V> map;
      protected transient ObjectSet<Byte2ObjectMap.Entry<V>> entries;
      protected transient ByteSet keys;
      protected transient ObjectCollection<V> values;

      protected SynchronizedMap(Byte2ObjectMap<V> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Byte2ObjectMap<V> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(Object var1) {
         synchronized(this.sync) {
            return this.map.containsValue(var1);
         }
      }

      public void putAll(Map<? extends Byte, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2ObjectEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      public ByteSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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

      public V getOrDefault(byte var1, V var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Byte, ? super V> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Byte, ? super V, ? extends V> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public V putIfAbsent(byte var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(byte var1, Object var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public V replace(byte var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(byte var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public V computeIfAbsent(byte var1, IntFunction<? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public V computeIfAbsentPartial(byte var1, Byte2ObjectFunction<? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public V computeIfPresent(byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public V compute(byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public V merge(byte var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
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
      public V replace(Byte var1, V var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, V var2, V var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public V putIfAbsent(Byte var1, V var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfAbsent(Byte var1, Function<? super Byte, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V computeIfPresent(Byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V compute(Byte var1, BiFunction<? super Byte, ? super V, ? extends V> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public V merge(Byte var1, V var2, BiFunction<? super V, ? super V, ? extends V> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<V> extends Byte2ObjectFunctions.Singleton<V> implements Byte2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2ObjectMap.Entry<V>> entries;
      protected transient ByteSet keys;
      protected transient ObjectCollection<V> values;

      protected Singleton(byte var1, V var2) {
         super(var1, var2);
      }

      public boolean containsValue(Object var1) {
         return Objects.equals(this.value, var1);
      }

      public void putAll(Map<? extends Byte, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2ObjectMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
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

   public static class EmptyMap<V> extends Byte2ObjectFunctions.EmptyFunction<V> implements Byte2ObjectMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Byte, ? extends V> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      public ObjectCollection<V> values() {
         return ObjectSets.EMPTY_SET;
      }

      public Object clone() {
         return Byte2ObjectMaps.EMPTY_MAP;
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
