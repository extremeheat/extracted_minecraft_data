package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
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

public final class Object2ByteMaps {
   public static final Object2ByteMaps.EmptyMap EMPTY_MAP = new Object2ByteMaps.EmptyMap();

   private Object2ByteMaps() {
      super();
   }

   public static <K> ObjectIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteMap<K> var0) {
      ObjectSet var1 = var0.object2ByteEntrySet();
      return var1 instanceof Object2ByteMap.FastEntrySet ? ((Object2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Object2ByteMap<K> var0, Consumer<? super Object2ByteMap.Entry<K>> var1) {
      ObjectSet var2 = var0.object2ByteEntrySet();
      if (var2 instanceof Object2ByteMap.FastEntrySet) {
         ((Object2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Object2ByteMap.Entry<K>> fastIterable(Object2ByteMap<K> var0) {
      final ObjectSet var1 = var0.object2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Object2ByteMap.FastEntrySet ? new ObjectIterable<Object2ByteMap.Entry<K>>() {
         public ObjectIterator<Object2ByteMap.Entry<K>> iterator() {
            return ((Object2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Object2ByteMap.Entry<K>> var1x) {
            ((Object2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Object2ByteMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2ByteMap<K> singleton(K var0, byte var1) {
      return new Object2ByteMaps.Singleton(var0, var1);
   }

   public static <K> Object2ByteMap<K> singleton(K var0, Byte var1) {
      return new Object2ByteMaps.Singleton(var0, var1);
   }

   public static <K> Object2ByteMap<K> synchronize(Object2ByteMap<K> var0) {
      return new Object2ByteMaps.SynchronizedMap(var0);
   }

   public static <K> Object2ByteMap<K> synchronize(Object2ByteMap<K> var0, Object var1) {
      return new Object2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Object2ByteMap<K> unmodifiable(Object2ByteMap<K> var0) {
      return new Object2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Object2ByteFunctions.UnmodifiableFunction<K> implements Object2ByteMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteMap<K> map;
      protected transient ObjectSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Object2ByteMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(byte var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends K, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.object2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public ByteCollection values() {
         return this.values == null ? ByteCollections.unmodifiable(this.map.values()) : this.values;
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

      public byte getOrDefault(Object var1, byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super K, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super K, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(K var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte replace(K var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(K var1, byte var2, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeByteIfAbsent(K var1, ToIntFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeByteIfAbsentPartial(K var1, Object2ByteFunction<? super K> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeByteIfPresent(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeByte(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte mergeByte(K var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte getOrDefault(Object var1, Byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte replace(K var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(K var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      public Byte computeIfAbsent(K var1, Function<? super K, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public Byte computeIfPresent(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public Byte compute(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(K var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap<K> extends Object2ByteFunctions.SynchronizedFunction<K> implements Object2ByteMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteMap<K> map;
      protected transient ObjectSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Object2ByteMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Object2ByteMap<K> var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(byte var1) {
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

      public void putAll(Map<? extends K, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.object2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      public ObjectSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ObjectSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public ByteCollection values() {
         synchronized(this.sync) {
            return this.values == null ? ByteCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public byte getOrDefault(Object var1, byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super K, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super K, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(K var1, byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(Object var1, byte var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public byte replace(K var1, byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(K var1, byte var2, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public byte computeByteIfAbsent(K var1, ToIntFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeByteIfAbsent(var1, var2);
         }
      }

      public byte computeByteIfAbsentPartial(K var1, Object2ByteFunction<? super K> var2) {
         synchronized(this.sync) {
            return this.map.computeByteIfAbsentPartial(var1, var2);
         }
      }

      public byte computeByteIfPresent(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeByteIfPresent(var1, var2);
         }
      }

      public byte computeByte(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeByte(var1, var2);
         }
      }

      public byte mergeByte(K var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.mergeByte(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte getOrDefault(Object var1, Byte var2) {
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
      public Byte replace(K var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(K var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(K var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public Byte computeIfAbsent(K var1, Function<? super K, ? extends Byte> var2) {
         synchronized(this.sync) {
            return (Byte)this.map.computeIfAbsent(var1, var2);
         }
      }

      public Byte computeIfPresent(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return (Byte)this.map.computeIfPresent(var1, var2);
         }
      }

      public Byte compute(K var1, BiFunction<? super K, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return (Byte)this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(K var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton<K> extends Object2ByteFunctions.Singleton<K> implements Object2ByteMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Object2ByteMap.Entry<K>> entries;
      protected transient ObjectSet<K> keys;
      protected transient ByteCollection values;

      protected Singleton(K var1, byte var2) {
         super(var1, var2);
      }

      public boolean containsValue(byte var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Byte)var1 == this.value;
      }

      public void putAll(Map<? extends K, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractObject2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      public ObjectSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSets.singleton(this.key);
         }

         return this.keys;
      }

      public ByteCollection values() {
         if (this.values == null) {
            this.values = ByteSets.singleton(this.value);
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

   public static class EmptyMap<K> extends Object2ByteFunctions.EmptyFunction<K> implements Object2ByteMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(byte var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends K, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ObjectSet<K> keySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Object2ByteMaps.EMPTY_MAP;
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
