package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
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
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Byte2ShortMaps {
   public static final Byte2ShortMaps.EmptyMap EMPTY_MAP = new Byte2ShortMaps.EmptyMap();

   private Byte2ShortMaps() {
      super();
   }

   public static ObjectIterator<Byte2ShortMap.Entry> fastIterator(Byte2ShortMap var0) {
      ObjectSet var1 = var0.byte2ShortEntrySet();
      return var1 instanceof Byte2ShortMap.FastEntrySet ? ((Byte2ShortMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Byte2ShortMap var0, Consumer<? super Byte2ShortMap.Entry> var1) {
      ObjectSet var2 = var0.byte2ShortEntrySet();
      if (var2 instanceof Byte2ShortMap.FastEntrySet) {
         ((Byte2ShortMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Byte2ShortMap.Entry> fastIterable(Byte2ShortMap var0) {
      final ObjectSet var1 = var0.byte2ShortEntrySet();
      return (ObjectIterable)(var1 instanceof Byte2ShortMap.FastEntrySet ? new ObjectIterable<Byte2ShortMap.Entry>() {
         public ObjectIterator<Byte2ShortMap.Entry> iterator() {
            return ((Byte2ShortMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Byte2ShortMap.Entry> var1x) {
            ((Byte2ShortMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Byte2ShortMap singleton(byte var0, short var1) {
      return new Byte2ShortMaps.Singleton(var0, var1);
   }

   public static Byte2ShortMap singleton(Byte var0, Short var1) {
      return new Byte2ShortMaps.Singleton(var0, var1);
   }

   public static Byte2ShortMap synchronize(Byte2ShortMap var0) {
      return new Byte2ShortMaps.SynchronizedMap(var0);
   }

   public static Byte2ShortMap synchronize(Byte2ShortMap var0, Object var1) {
      return new Byte2ShortMaps.SynchronizedMap(var0, var1);
   }

   public static Byte2ShortMap unmodifiable(Byte2ShortMap var0) {
      return new Byte2ShortMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Byte2ShortFunctions.UnmodifiableFunction implements Byte2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ShortMap map;
      protected transient ObjectSet<Byte2ShortMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Byte2ShortMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2ShortEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Short>> entrySet() {
         return this.byte2ShortEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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

      public short getOrDefault(byte var1, short var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Byte, ? super Short> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Byte, ? super Short, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public short putIfAbsent(byte var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public short replace(byte var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, short var2, short var3) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsent(byte var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsentNullable(byte var1, IntFunction<? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsentPartial(byte var1, Byte2ShortFunction var2) {
         throw new UnsupportedOperationException();
      }

      public short computeIfPresent(byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short compute(byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short merge(byte var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
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
      public Short replace(Byte var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Short var2, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(Byte var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfAbsent(Byte var1, Function<? super Byte, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short compute(Byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short merge(Byte var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Byte2ShortFunctions.SynchronizedFunction implements Byte2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ShortMap map;
      protected transient ObjectSet<Byte2ShortMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Byte2ShortMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Byte2ShortMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Short>> entrySet() {
         return this.byte2ShortEntrySet();
      }

      public ByteSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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

      public short getOrDefault(byte var1, short var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Byte, ? super Short> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Byte, ? super Short, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public short putIfAbsent(byte var1, short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(byte var1, short var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public short replace(byte var1, short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(byte var1, short var2, short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public short computeIfAbsent(byte var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public short computeIfAbsentNullable(byte var1, IntFunction<? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public short computeIfAbsentPartial(byte var1, Byte2ShortFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public short computeIfPresent(byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public short compute(byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public short merge(byte var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
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
      public Short replace(Byte var1, Short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Short var2, Short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(Byte var1, Short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfAbsent(Byte var1, Function<? super Byte, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short compute(Byte var1, BiFunction<? super Byte, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short merge(Byte var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Byte2ShortFunctions.Singleton implements Byte2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2ShortMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient ShortCollection values;

      protected Singleton(byte var1, short var2) {
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

      public void putAll(Map<? extends Byte, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Short>> entrySet() {
         return this.byte2ShortEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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

   public static class EmptyMap extends Byte2ShortFunctions.EmptyFunction implements Byte2ShortMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Byte, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ShortMap.Entry> byte2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      public Object clone() {
         return Byte2ShortMaps.EMPTY_MAP;
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
