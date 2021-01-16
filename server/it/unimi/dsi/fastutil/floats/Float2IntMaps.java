package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2IntMaps {
   public static final Float2IntMaps.EmptyMap EMPTY_MAP = new Float2IntMaps.EmptyMap();

   private Float2IntMaps() {
      super();
   }

   public static ObjectIterator<Float2IntMap.Entry> fastIterator(Float2IntMap var0) {
      ObjectSet var1 = var0.float2IntEntrySet();
      return var1 instanceof Float2IntMap.FastEntrySet ? ((Float2IntMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Float2IntMap var0, Consumer<? super Float2IntMap.Entry> var1) {
      ObjectSet var2 = var0.float2IntEntrySet();
      if (var2 instanceof Float2IntMap.FastEntrySet) {
         ((Float2IntMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Float2IntMap.Entry> fastIterable(Float2IntMap var0) {
      final ObjectSet var1 = var0.float2IntEntrySet();
      return (ObjectIterable)(var1 instanceof Float2IntMap.FastEntrySet ? new ObjectIterable<Float2IntMap.Entry>() {
         public ObjectIterator<Float2IntMap.Entry> iterator() {
            return ((Float2IntMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Float2IntMap.Entry> var1x) {
            ((Float2IntMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Float2IntMap singleton(float var0, int var1) {
      return new Float2IntMaps.Singleton(var0, var1);
   }

   public static Float2IntMap singleton(Float var0, Integer var1) {
      return new Float2IntMaps.Singleton(var0, var1);
   }

   public static Float2IntMap synchronize(Float2IntMap var0) {
      return new Float2IntMaps.SynchronizedMap(var0);
   }

   public static Float2IntMap synchronize(Float2IntMap var0, Object var1) {
      return new Float2IntMaps.SynchronizedMap(var0, var1);
   }

   public static Float2IntMap unmodifiable(Float2IntMap var0) {
      return new Float2IntMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Float2IntFunctions.UnmodifiableFunction implements Float2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2IntMap map;
      protected transient ObjectSet<Float2IntMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient IntCollection values;

      protected UnmodifiableMap(Float2IntMap var1) {
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

      public void putAll(Map<? extends Float, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2IntEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Integer>> entrySet() {
         return this.float2IntEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
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

      public int getOrDefault(float var1, int var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Float, ? super Integer> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Float, ? super Integer, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public int putIfAbsent(float var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public int replace(float var1, int var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(float var1, int var2, int var3) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsent(float var1, DoubleToIntFunction var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentNullable(float var1, DoubleFunction<? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfAbsentPartial(float var1, Float2IntFunction var2) {
         throw new UnsupportedOperationException();
      }

      public int computeIfPresent(float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int compute(float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      public int merge(float var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
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
      public Integer replace(Float var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Integer var2, Integer var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Float var1, Integer var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Float var1, Function<? super Float, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Float var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Float2IntFunctions.SynchronizedFunction implements Float2IntMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2IntMap map;
      protected transient ObjectSet<Float2IntMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient IntCollection values;

      protected SynchronizedMap(Float2IntMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Float2IntMap var1) {
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

      public void putAll(Map<? extends Float, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2IntEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Integer>> entrySet() {
         return this.float2IntEntrySet();
      }

      public FloatSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
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

      public int getOrDefault(float var1, int var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Float, ? super Integer> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Float, ? super Integer, ? extends Integer> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public int putIfAbsent(float var1, int var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(float var1, int var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public int replace(float var1, int var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(float var1, int var2, int var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public int computeIfAbsent(float var1, DoubleToIntFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public int computeIfAbsentNullable(float var1, DoubleFunction<? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public int computeIfAbsentPartial(float var1, Float2IntFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public int computeIfPresent(float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public int compute(float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public int merge(float var1, int var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
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
      public Integer replace(Float var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Integer var2, Integer var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer putIfAbsent(Float var1, Integer var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfAbsent(Float var1, Function<? super Float, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer computeIfPresent(Float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer compute(Float var1, BiFunction<? super Float, ? super Integer, ? extends Integer> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer merge(Float var1, Integer var2, BiFunction<? super Integer, ? super Integer, ? extends Integer> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Float2IntFunctions.Singleton implements Float2IntMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2IntMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient IntCollection values;

      protected Singleton(float var1, int var2) {
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

      public void putAll(Map<? extends Float, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2IntMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Integer>> entrySet() {
         return this.float2IntEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
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
         return HashCommon.float2int(this.key) ^ this.value;
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

   public static class EmptyMap extends Float2IntFunctions.EmptyFunction implements Float2IntMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Float, ? extends Integer> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2IntMap.Entry> float2IntEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      public IntCollection values() {
         return IntSets.EMPTY_SET;
      }

      public Object clone() {
         return Float2IntMaps.EMPTY_MAP;
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
