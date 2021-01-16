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

public final class Reference2ByteMaps {
   public static final Reference2ByteMaps.EmptyMap EMPTY_MAP = new Reference2ByteMaps.EmptyMap();

   private Reference2ByteMaps() {
      super();
   }

   public static <K> ObjectIterator<Reference2ByteMap.Entry<K>> fastIterator(Reference2ByteMap<K> var0) {
      ObjectSet var1 = var0.reference2ByteEntrySet();
      return var1 instanceof Reference2ByteMap.FastEntrySet ? ((Reference2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> void fastForEach(Reference2ByteMap<K> var0, Consumer<? super Reference2ByteMap.Entry<K>> var1) {
      ObjectSet var2 = var0.reference2ByteEntrySet();
      if (var2 instanceof Reference2ByteMap.FastEntrySet) {
         ((Reference2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static <K> ObjectIterable<Reference2ByteMap.Entry<K>> fastIterable(Reference2ByteMap<K> var0) {
      final ObjectSet var1 = var0.reference2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Reference2ByteMap.FastEntrySet ? new ObjectIterable<Reference2ByteMap.Entry<K>>() {
         public ObjectIterator<Reference2ByteMap.Entry<K>> iterator() {
            return ((Reference2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Reference2ByteMap.Entry<K>> var1x) {
            ((Reference2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static <K> Reference2ByteMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2ByteMap<K> singleton(K var0, byte var1) {
      return new Reference2ByteMaps.Singleton(var0, var1);
   }

   public static <K> Reference2ByteMap<K> singleton(K var0, Byte var1) {
      return new Reference2ByteMaps.Singleton(var0, var1);
   }

   public static <K> Reference2ByteMap<K> synchronize(Reference2ByteMap<K> var0) {
      return new Reference2ByteMaps.SynchronizedMap(var0);
   }

   public static <K> Reference2ByteMap<K> synchronize(Reference2ByteMap<K> var0, Object var1) {
      return new Reference2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static <K> Reference2ByteMap<K> unmodifiable(Reference2ByteMap<K> var0) {
      return new Reference2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap<K> extends Reference2ByteFunctions.UnmodifiableFunction<K> implements Reference2ByteMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ByteMap<K> map;
      protected transient ObjectSet<Reference2ByteMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Reference2ByteMap<K> var1) {
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

      public ObjectSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.reference2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.reference2ByteEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.unmodifiable(this.map.keySet());
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

      public byte computeByteIfAbsentPartial(K var1, Reference2ByteFunction<? super K> var2) {
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

   public static class SynchronizedMap<K> extends Reference2ByteFunctions.SynchronizedFunction<K> implements Reference2ByteMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ByteMap<K> map;
      protected transient ObjectSet<Reference2ByteMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Reference2ByteMap<K> var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Reference2ByteMap<K> var1) {
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

      public ObjectSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.reference2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.reference2ByteEntrySet();
      }

      public ReferenceSet<K> keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ReferenceSets.synchronize(this.map.keySet(), this.sync);
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

      public byte computeByteIfAbsentPartial(K var1, Reference2ByteFunction<? super K> var2) {
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

   public static class Singleton<K> extends Reference2ByteFunctions.Singleton<K> implements Reference2ByteMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Reference2ByteMap.Entry<K>> entries;
      protected transient ReferenceSet<K> keys;
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

      public ObjectSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractReference2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<K, Byte>> entrySet() {
         return this.reference2ByteEntrySet();
      }

      public ReferenceSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSets.singleton(this.key);
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
         return System.identityHashCode(this.key) ^ this.value;
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

   public static class EmptyMap<K> extends Reference2ByteFunctions.EmptyFunction<K> implements Reference2ByteMap<K>, Serializable, Cloneable {
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

      public ObjectSet<Reference2ByteMap.Entry<K>> reference2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ReferenceSet<K> keySet() {
         return ReferenceSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Reference2ByteMaps.EMPTY_MAP;
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
