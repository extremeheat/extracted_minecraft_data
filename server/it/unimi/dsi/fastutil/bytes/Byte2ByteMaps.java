package it.unimi.dsi.fastutil.bytes;

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

public final class Byte2ByteMaps {
   public static final Byte2ByteMaps.EmptyMap EMPTY_MAP = new Byte2ByteMaps.EmptyMap();

   private Byte2ByteMaps() {
      super();
   }

   public static ObjectIterator<Byte2ByteMap.Entry> fastIterator(Byte2ByteMap var0) {
      ObjectSet var1 = var0.byte2ByteEntrySet();
      return var1 instanceof Byte2ByteMap.FastEntrySet ? ((Byte2ByteMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Byte2ByteMap var0, Consumer<? super Byte2ByteMap.Entry> var1) {
      ObjectSet var2 = var0.byte2ByteEntrySet();
      if (var2 instanceof Byte2ByteMap.FastEntrySet) {
         ((Byte2ByteMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Byte2ByteMap.Entry> fastIterable(Byte2ByteMap var0) {
      final ObjectSet var1 = var0.byte2ByteEntrySet();
      return (ObjectIterable)(var1 instanceof Byte2ByteMap.FastEntrySet ? new ObjectIterable<Byte2ByteMap.Entry>() {
         public ObjectIterator<Byte2ByteMap.Entry> iterator() {
            return ((Byte2ByteMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Byte2ByteMap.Entry> var1x) {
            ((Byte2ByteMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Byte2ByteMap singleton(byte var0, byte var1) {
      return new Byte2ByteMaps.Singleton(var0, var1);
   }

   public static Byte2ByteMap singleton(Byte var0, Byte var1) {
      return new Byte2ByteMaps.Singleton(var0, var1);
   }

   public static Byte2ByteMap synchronize(Byte2ByteMap var0) {
      return new Byte2ByteMaps.SynchronizedMap(var0);
   }

   public static Byte2ByteMap synchronize(Byte2ByteMap var0, Object var1) {
      return new Byte2ByteMaps.SynchronizedMap(var0, var1);
   }

   public static Byte2ByteMap unmodifiable(Byte2ByteMap var0) {
      return new Byte2ByteMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Byte2ByteFunctions.UnmodifiableFunction implements Byte2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ByteMap map;
      protected transient ObjectSet<Byte2ByteMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient ByteCollection values;

      protected UnmodifiableMap(Byte2ByteMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2ByteEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Byte>> entrySet() {
         return this.byte2ByteEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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

      public byte getOrDefault(byte var1, byte var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Byte, ? super Byte> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Byte, ? super Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public byte putIfAbsent(byte var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public byte replace(byte var1, byte var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, byte var2, byte var3) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsent(byte var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentNullable(byte var1, IntFunction<? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfAbsentPartial(byte var1, Byte2ByteFunction var2) {
         throw new UnsupportedOperationException();
      }

      public byte computeIfPresent(byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte compute(byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      public byte merge(byte var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
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
      public Byte replace(Byte var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Byte var2, Byte var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Byte var1, Byte var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Byte var1, Function<? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Byte var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Byte2ByteFunctions.SynchronizedFunction implements Byte2ByteMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ByteMap map;
      protected transient ObjectSet<Byte2ByteMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient ByteCollection values;

      protected SynchronizedMap(Byte2ByteMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Byte2ByteMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2ByteEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Byte>> entrySet() {
         return this.byte2ByteEntrySet();
      }

      public ByteSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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

      public byte getOrDefault(byte var1, byte var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Byte, ? super Byte> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Byte, ? super Byte, ? extends Byte> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public byte putIfAbsent(byte var1, byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(byte var1, byte var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public byte replace(byte var1, byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(byte var1, byte var2, byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public byte computeIfAbsent(byte var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public byte computeIfAbsentNullable(byte var1, IntFunction<? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public byte computeIfAbsentPartial(byte var1, Byte2ByteFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public byte computeIfPresent(byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public byte compute(byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public byte merge(byte var1, byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
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
      public Byte replace(Byte var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Byte var2, Byte var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte putIfAbsent(Byte var1, Byte var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfAbsent(Byte var1, Function<? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte compute(Byte var1, BiFunction<? super Byte, ? super Byte, ? extends Byte> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte merge(Byte var1, Byte var2, BiFunction<? super Byte, ? super Byte, ? extends Byte> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Byte2ByteFunctions.Singleton implements Byte2ByteMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2ByteMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient ByteCollection values;

      protected Singleton(byte var1, byte var2) {
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

      public void putAll(Map<? extends Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2ByteMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Byte>> entrySet() {
         return this.byte2ByteEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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

   public static class EmptyMap extends Byte2ByteFunctions.EmptyFunction implements Byte2ByteMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Byte, ? extends Byte> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2ByteMap.Entry> byte2ByteEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      public ByteCollection values() {
         return ByteSets.EMPTY_SET;
      }

      public Object clone() {
         return Byte2ByteMaps.EMPTY_MAP;
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
