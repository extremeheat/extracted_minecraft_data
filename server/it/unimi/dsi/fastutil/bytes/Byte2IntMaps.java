package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntSets;
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

public final class Byte2IntMaps {
   public static final Byte2IntMaps.EmptyMap EMPTY_MAP = new Byte2IntMaps.EmptyMap();

   private Byte2IntMaps() {
      super();
   }

   public static ObjectIterator<Byte2IntMap.Entry> fastIterator(Byte2IntMap var0) {
      ObjectSet var1 = var0.byte2IntEntrySet();
      return var1 instanceof Byte2IntMap.FastEntrySet ? ((Byte2IntMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Byte2IntMap var0, Consumer<? super Byte2IntMap.Entry> var1) {
      ObjectSet var2 = var0.byte2IntEntrySet();
      if (var2 instanceof Byte2IntMap.FastEntrySet) {
         ((Byte2IntMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Byte2IntMap.Entry> fastIterable(Byte2IntMap var0) {
      final ObjectSet var1 = var0.byte2IntEntrySet();
      return (ObjectIterable)(var1 instanceof Byte2IntMap.FastEntrySet ? new ObjectIterable<Byte2IntMap.Entry>() {
         public ObjectIterator<Byte2IntMap.Entry> iterator() {
            return ((Byte2IntMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Byte2IntMap.Entry> var1x) {
            ((Byte2IntMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Byte2IntMap singleton(byte var0, int var1) {
      return new Byte2IntMaps.Singleton(var0, var1);
   }

   public static Byte2IntMap singleton(Byte var0, Integer var1) {
      return new Byte2IntMaps.Singleton(var0, var1);
   }

   public static Byte2IntMap synchronize(Byte2IntMap var0) {
      return new Byte2IntMaps.SynchronizedMap(var0);
   }

   public static Byte2IntMap synchronize(Byte2IntMap var0, Object var1) {
      return new Byte2IntMaps.SynchronizedMap(var0, var1);
   }

   public static Byte2IntMap unmodifiable(Byte2IntMap var0) {
      return new Byte2IntMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Byte2IntFunctions.UnmodifiableFunction implements Byte2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2IntMap map;
      protected transient ObjectSet<Byte2IntMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Byte2IntMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2IntEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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

      public int getOrDefault(byte var1, int var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Byte, ? super Integer> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Byte, ? super Integer, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public int putIfAbsent(byte var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int replace(byte var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, int var2, int var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsent(byte var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentNullable(byte var1, IntFunction<? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentPartial(byte var1, Byte2IntFunction var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfPresent(byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int compute(byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int merge(byte var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
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
      public Integer replace(Byte var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Integer var2, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Byte var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Byte var1, Function<? super Byte, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Byte var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Byte2IntFunctions.SynchronizedFunction implements Byte2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2IntMap map;
      protected transient ObjectSet<Byte2IntMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Byte2IntMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Byte2IntMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      public ByteSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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

      public int getOrDefault(byte var1, int var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Byte, ? super Integer> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Byte, ? super Integer, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public int putIfAbsent(byte var1, int var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(byte var1, int var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public int replace(byte var1, int var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(byte var1, int var2, int var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public int computeIfAbsent(byte var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public int computeIfAbsentNullable(byte var1, IntFunction<? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public int computeIfAbsentPartial(byte var1, Byte2IntFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public int computeIfPresent(byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public int compute(byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public int merge(byte var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
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
      public Integer replace(Byte var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Integer var2, Integer var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Byte var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Byte var1, Function<? super Byte, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Byte var1, BiFunction<? super Byte, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Byte var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Byte2IntFunctions.Singleton implements Byte2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2IntMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient IntCollection values;

      protected Singleton(byte var1, int var2) {
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

      public void putAll(Map<? extends Byte, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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

   public static class EmptyMap extends Byte2IntFunctions.EmptyFunction implements Byte2IntMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Byte, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public Object clone() {
         return Byte2IntMaps.EMPTY_MAP;
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
