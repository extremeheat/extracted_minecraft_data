package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;
import java.util.function.Function;

public final class Float2ShortMaps {
   public static final Float2ShortMaps.EmptyMap EMPTY_MAP = new Float2ShortMaps.EmptyMap();

   private Float2ShortMaps() {
      super();
   }

   public static ObjectIterator<Float2ShortMap.Entry> fastIterator(Float2ShortMap var0) {
      ObjectSet var1 = var0.float2ShortEntrySet();
      return var1 instanceof Float2ShortMap.FastEntrySet ? ((Float2ShortMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Float2ShortMap var0, Consumer<? super Float2ShortMap.Entry> var1) {
      ObjectSet var2 = var0.float2ShortEntrySet();
      if (var2 instanceof Float2ShortMap.FastEntrySet) {
         ((Float2ShortMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Float2ShortMap.Entry> fastIterable(Float2ShortMap var0) {
      final ObjectSet var1 = var0.float2ShortEntrySet();
      return (ObjectIterable)(var1 instanceof Float2ShortMap.FastEntrySet ? new ObjectIterable<Float2ShortMap.Entry>() {
         public ObjectIterator<Float2ShortMap.Entry> iterator() {
            return ((Float2ShortMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Float2ShortMap.Entry> var1x) {
            ((Float2ShortMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Float2ShortMap singleton(float var0, short var1) {
      return new Float2ShortMaps.Singleton(var0, var1);
   }

   public static Float2ShortMap singleton(Float var0, Short var1) {
      return new Float2ShortMaps.Singleton(var0, var1);
   }

   public static Float2ShortMap synchronize(Float2ShortMap var0) {
      return new Float2ShortMaps.SynchronizedMap(var0);
   }

   public static Float2ShortMap synchronize(Float2ShortMap var0, Object var1) {
      return new Float2ShortMaps.SynchronizedMap(var0, var1);
   }

   public static Float2ShortMap unmodifiable(Float2ShortMap var0) {
      return new Float2ShortMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Float2ShortFunctions.UnmodifiableFunction implements Float2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ShortMap map;
      protected transient ObjectSet<Float2ShortMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ShortCollection values;

      protected UnmodifiableMap(Float2ShortMap var1) {
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

      public void putAll(Map<? extends Float, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2ShortEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Short>> entrySet() {
         return this.float2ShortEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
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

      public short getOrDefault(float var1, short var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Float, ? super Short> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Float, ? super Short, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public short putIfAbsent(float var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public short replace(float var1, short var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(float var1, short var2, short var3) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsent(float var1, DoubleToIntFunction var2) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsentNullable(float var1, DoubleFunction<? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short computeIfAbsentPartial(float var1, Float2ShortFunction var2) {
         throw new UnsupportedOperationException();
      }

      public short computeIfPresent(float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short compute(float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      public short merge(float var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
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
      public Short replace(Float var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Short var2, Short var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(Float var1, Short var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfAbsent(Float var1, Function<? super Float, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfPresent(Float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short compute(Float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Short merge(Float var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Float2ShortFunctions.SynchronizedFunction implements Float2ShortMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ShortMap map;
      protected transient ObjectSet<Float2ShortMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ShortCollection values;

      protected SynchronizedMap(Float2ShortMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Float2ShortMap var1) {
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

      public void putAll(Map<? extends Float, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2ShortEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Short>> entrySet() {
         return this.float2ShortEntrySet();
      }

      public FloatSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
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

      public short getOrDefault(float var1, short var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Float, ? super Short> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Float, ? super Short, ? extends Short> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public short putIfAbsent(float var1, short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(float var1, short var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public short replace(float var1, short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(float var1, short var2, short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public short computeIfAbsent(float var1, DoubleToIntFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public short computeIfAbsentNullable(float var1, DoubleFunction<? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public short computeIfAbsentPartial(float var1, Float2ShortFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public short computeIfPresent(float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public short compute(float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public short merge(float var1, short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
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
      public Short replace(Float var1, Short var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Short var2, Short var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short putIfAbsent(Float var1, Short var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfAbsent(Float var1, Function<? super Float, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short computeIfPresent(Float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short compute(Float var1, BiFunction<? super Float, ? super Short, ? extends Short> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Short merge(Float var1, Short var2, BiFunction<? super Short, ? super Short, ? extends Short> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Float2ShortFunctions.Singleton implements Float2ShortMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2ShortMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient ShortCollection values;

      protected Singleton(float var1, short var2) {
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

      public void putAll(Map<? extends Float, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2ShortMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Short>> entrySet() {
         return this.float2ShortEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
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

   public static class EmptyMap extends Float2ShortFunctions.EmptyFunction implements Float2ShortMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Float, ? extends Short> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2ShortMap.Entry> float2ShortEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      public ShortCollection values() {
         return ShortSets.EMPTY_SET;
      }

      public Object clone() {
         return Float2ShortMaps.EMPTY_MAP;
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
