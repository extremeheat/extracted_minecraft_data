package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollections;
import it.unimi.dsi.fastutil.bytes.ByteSets;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
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
import java.util.function.IntUnaryOperator;

public final class Short2ByteMaps {
   public static final Short2ByteMaps.EmptyMap EMPTY_MAP = new Short2ByteMaps.EmptyMap();

   private Short2ByteMaps() {
      super();
   }

   public static ObjectIterator<Short2ByteMap.Entry> fastIterator(Short2ByteMap var0) {
      ObjectSet var1 = var0.short2ByteEntrySet();
      return var1 instanceof Short2ByteMap.FastEntrySet ? ((Short2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Short2ByteMap var0, Consumer<? super Short2ByteMap.Entry> var1) {
      ObjectSet var2 = var0.short2ByteEntrySet();
      if (var2 instanceof Short2ByteMap.FastEntrySet) {
         ((Short2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Short2ByteMap.Entry> fastIterable(Short2ByteMap var0) {
      final ObjectSet var1 = var0.short2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Short2ByteMap.FastEntrySet ? new ObjectIterable<Short2ByteMap.Entry>() {
         public ObjectIterator<Short2ByteMap.Entry> iterator() {
            return ((Short2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Short2ByteMap.Entry> var1x) {
            ((Short2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Short2ByteMap singleton(short var0, byte var1) {
      return new Short2ByteMaps.Singleton(var0, var1);
   }

   public static Short2ByteMap singleton(Short var0, Byte var1) {
      return new Short2ByteMaps.Singleton(var0, var1);
   }

   public static Short2ByteMap synchronize(Short2ByteMap var0) {
      return new Short2ByteMaps.SynchronizedMap(var0);
   }

   public static Short2ByteMap synchronize(Short2ByteMap var0, Object var1) {
      return new Short2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static Short2ByteMap unmodifiable(Short2ByteMap var0) {
      return new Short2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Short2ByteFunctions.UnmodifiableFunction implements Short2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ByteMap map;
      protected transient ObjectSet<Short2ByteMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Short2ByteMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Byte>> entrySet() {
         return this.short2ByteEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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

      public byte getOrDefault(short var1, byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Short, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Short, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(short var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte replace(short var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(short var1, byte var2, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsent(short var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentNullable(short var1, IntFunction<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentPartial(short var1, Short2ByteFunction var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfPresent(short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte compute(short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte merge(short var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
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
      public Byte replace(Short var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Short var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Short var1, Function<? super Short, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Short var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Short2ByteFunctions.SynchronizedFunction implements Short2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ByteMap map;
      protected transient ObjectSet<Short2ByteMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Short2ByteMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Short2ByteMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Byte>> entrySet() {
         return this.short2ByteEntrySet();
      }

      public ShortSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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

      public byte getOrDefault(short var1, byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Short, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Short, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(short var1, byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(short var1, byte var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public byte replace(short var1, byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(short var1, byte var2, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public byte computeIfAbsent(short var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public byte computeIfAbsentNullable(short var1, IntFunction<? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public byte computeIfAbsentPartial(short var1, Short2ByteFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public byte computeIfPresent(short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public byte compute(short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public byte merge(short var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
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
      public Byte replace(Short var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Short var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Short var1, Function<? super Short, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Short var1, BiFunction<? super Short, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Short var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Short2ByteFunctions.Singleton implements Short2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2ByteMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient ByteCollection values;

      protected Singleton(short var1, byte var2) {
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

      public void putAll(Map<? extends Short, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Byte>> entrySet() {
         return this.short2ByteEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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
         return this.key ^ this.value;
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

   public static class EmptyMap extends Short2ByteFunctions.EmptyFunction implements Short2ByteMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Short, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2ByteMap.Entry> short2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Short2ByteMaps.EMPTY_MAP;
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
