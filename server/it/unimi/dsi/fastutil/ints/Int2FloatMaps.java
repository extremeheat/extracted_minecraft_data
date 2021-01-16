package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
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
import java.util.function.IntToDoubleFunction;

public final class Int2FloatMaps {
   public static final Int2FloatMaps.EmptyMap EMPTY_MAP = new Int2FloatMaps.EmptyMap();

   private Int2FloatMaps() {
      super();
   }

   public static ObjectIterator<Int2FloatMap.Entry> fastIterator(Int2FloatMap var0) {
      ObjectSet var1 = var0.int2FloatEntrySet();
      return var1 instanceof Int2FloatMap.FastEntrySet ? ((Int2FloatMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Int2FloatMap var0, Consumer<? super Int2FloatMap.Entry> var1) {
      ObjectSet var2 = var0.int2FloatEntrySet();
      if (var2 instanceof Int2FloatMap.FastEntrySet) {
         ((Int2FloatMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Int2FloatMap.Entry> fastIterable(Int2FloatMap var0) {
      final ObjectSet var1 = var0.int2FloatEntrySet();
      return (ObjectIterable)(var1 instanceof Int2FloatMap.FastEntrySet ? new ObjectIterable<Int2FloatMap.Entry>() {
         public ObjectIterator<Int2FloatMap.Entry> iterator() {
            return ((Int2FloatMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Int2FloatMap.Entry> var1x) {
            ((Int2FloatMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Int2FloatMap singleton(int var0, float var1) {
      return new Int2FloatMaps.Singleton(var0, var1);
   }

   public static Int2FloatMap singleton(Integer var0, Float var1) {
      return new Int2FloatMaps.Singleton(var0, var1);
   }

   public static Int2FloatMap synchronize(Int2FloatMap var0) {
      return new Int2FloatMaps.SynchronizedMap(var0);
   }

   public static Int2FloatMap synchronize(Int2FloatMap var0, Object var1) {
      return new Int2FloatMaps.SynchronizedMap(var0, var1);
   }

   public static Int2FloatMap unmodifiable(Int2FloatMap var0) {
      return new Int2FloatMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Int2FloatFunctions.UnmodifiableFunction implements Int2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2FloatMap map;
      protected transient ObjectSet<Int2FloatMap.Entry> entries;
      protected transient IntSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Int2FloatMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(float var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Integer, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.int2FloatEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public FloatCollection values() {
         return this.values == null ? FloatCollections.unmodifiable(this.map.values()) : this.values;
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

      public float getOrDefault(int var1, float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Integer, ? super Float> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Integer, ? super Float, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public float putIfAbsent(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float replace(int var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(int var1, float var2, float var3) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsent(int var1, IntToDoubleFunction var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentNullable(int var1, IntFunction<? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentPartial(int var1, Int2FloatFunction var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfPresent(int var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float compute(int var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float merge(int var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float getOrDefault(Object var1, Float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float replace(Integer var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Integer var1, Float var2, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Integer var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Integer var1, Function<? super Integer, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Integer var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Integer var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Int2FloatFunctions.SynchronizedFunction implements Int2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2FloatMap map;
      protected transient ObjectSet<Int2FloatMap.Entry> entries;
      protected transient IntSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Int2FloatMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Int2FloatMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(float var1) {
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

      public void putAll(Map<? extends Integer, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.int2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      public IntSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = IntSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public FloatCollection values() {
         synchronized(this.sync) {
            return this.values == null ? FloatCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public float getOrDefault(int var1, float var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Integer, ? super Float> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Integer, ? super Float, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public float putIfAbsent(int var1, float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(int var1, float var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public float replace(int var1, float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(int var1, float var2, float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public float computeIfAbsent(int var1, IntToDoubleFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public float computeIfAbsentNullable(int var1, IntFunction<? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public float computeIfAbsentPartial(int var1, Int2FloatFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public float computeIfPresent(int var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public float compute(int var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public float merge(int var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float getOrDefault(Object var1, Float var2) {
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
      public Float replace(Integer var1, Float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Integer var1, Float var2, Float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Integer var1, Float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Integer var1, Function<? super Integer, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Integer var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Integer var1, BiFunction<? super Integer, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Integer var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Int2FloatFunctions.Singleton implements Int2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Int2FloatMap.Entry> entries;
      protected transient IntSet keys;
      protected transient FloatCollection values;

      protected Singleton(int var1, float var2) {
         super(var1, var2);
      }

      public boolean containsValue(float var1) {
         return Float.floatToIntBits(this.value) == Float.floatToIntBits(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return Float.floatToIntBits((Float)var1) == Float.floatToIntBits(this.value);
      }

      public void putAll(Map<? extends Integer, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractInt2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Integer, Float>> entrySet() {
         return this.int2FloatEntrySet();
      }

      public IntSet keySet() {
         if (this.keys == null) {
            this.keys = IntSets.singleton(this.key);
         }

         return this.keys;
      }

      public FloatCollection values() {
         if (this.values == null) {
            this.values = FloatSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return this.key ^ HashCommon.float2int(this.value);
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

   public static class EmptyMap extends Int2FloatFunctions.EmptyFunction implements Int2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(float var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Integer, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Int2FloatMap.Entry> int2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public IntSet keySet() {
         return IntSets.EMPTY_SET;
      }

      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      public Object clone() {
         return Int2FloatMaps.EMPTY_MAP;
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
