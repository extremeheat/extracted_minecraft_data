package it.unimi.dsi.fastutil.shorts;

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

public final class Short2IntMaps {
   public static final Short2IntMaps.EmptyMap EMPTY_MAP = new Short2IntMaps.EmptyMap();

   private Short2IntMaps() {
      super();
   }

   public static ObjectIterator<Short2IntMap.Entry> fastIterator(Short2IntMap var0) {
      ObjectSet var1 = var0.short2IntEntrySet();
      return var1 instanceof Short2IntMap.FastEntrySet ? ((Short2IntMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Short2IntMap var0, Consumer<? super Short2IntMap.Entry> var1) {
      ObjectSet var2 = var0.short2IntEntrySet();
      if (var2 instanceof Short2IntMap.FastEntrySet) {
         ((Short2IntMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Short2IntMap.Entry> fastIterable(Short2IntMap var0) {
      final ObjectSet var1 = var0.short2IntEntrySet();
      return (ObjectIterable)(var1 instanceof Short2IntMap.FastEntrySet ? new ObjectIterable<Short2IntMap.Entry>() {
         public ObjectIterator<Short2IntMap.Entry> iterator() {
            return ((Short2IntMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Short2IntMap.Entry> var1x) {
            ((Short2IntMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Short2IntMap singleton(short var0, int var1) {
      return new Short2IntMaps.Singleton(var0, var1);
   }

   public static Short2IntMap singleton(Short var0, Integer var1) {
      return new Short2IntMaps.Singleton(var0, var1);
   }

   public static Short2IntMap synchronize(Short2IntMap var0) {
      return new Short2IntMaps.SynchronizedMap(var0);
   }

   public static Short2IntMap synchronize(Short2IntMap var0, Object var1) {
      return new Short2IntMaps.SynchronizedMap(var0, var1);
   }

   public static Short2IntMap unmodifiable(Short2IntMap var0) {
      return new Short2IntMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Short2IntFunctions.UnmodifiableFunction implements Short2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2IntMap map;
      protected transient ObjectSet<Short2IntMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Short2IntMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.short2IntEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.unmodifiable(this.map.keySet());
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

      public int getOrDefault(short var1, int var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Short, ? super Integer> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Short, ? super Integer, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public int putIfAbsent(short var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(short var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int replace(short var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(short var1, int var2, int var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsent(short var1, IntUnaryOperator var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentNullable(short var1, IntFunction<? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentPartial(short var1, Short2IntFunction var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfPresent(short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int compute(short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int merge(short var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
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
      public Integer replace(Short var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Integer var2, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Short var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Short var1, Function<? super Short, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Short var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Short2IntFunctions.SynchronizedFunction implements Short2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2IntMap map;
      protected transient ObjectSet<Short2IntMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Short2IntMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Short2IntMap var1) {
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

      public void putAll(Map<? extends Short, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.short2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      public ShortSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ShortSets.synchronize(this.map.keySet(), this.sync);
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

      public int getOrDefault(short var1, int var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Short, ? super Integer> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Short, ? super Integer, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public int putIfAbsent(short var1, int var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(short var1, int var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public int replace(short var1, int var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(short var1, int var2, int var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public int computeIfAbsent(short var1, IntUnaryOperator var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public int computeIfAbsentNullable(short var1, IntFunction<? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public int computeIfAbsentPartial(short var1, Short2IntFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public int computeIfPresent(short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public int compute(short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public int merge(short var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
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
      public Integer replace(Short var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Short var1, Integer var2, Integer var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Short var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Short var1, Function<? super Short, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Short var1, BiFunction<? super Short, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Short var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Short2IntFunctions.Singleton implements Short2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Short2IntMap.Entry> entries;
      protected transient ShortSet keys;
      protected transient IntCollection values;

      protected Singleton(short var1, int var2) {
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

      public void putAll(Map<? extends Short, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractShort2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Short, Integer>> entrySet() {
         return this.short2IntEntrySet();
      }

      public ShortSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSets.singleton(this.key);
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

   public static class EmptyMap extends Short2IntFunctions.EmptyFunction implements Short2IntMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Short, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Short2IntMap.Entry> short2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ShortSet keySet() {
         return ShortSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public Object clone() {
         return Short2IntMaps.EMPTY_MAP;
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
