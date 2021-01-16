package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongCollections;
import it.unimi.dsi.fastutil.longs.LongSets;
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
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

public final class Float2LongMaps {
   public static final Float2LongMaps.EmptyMap EMPTY_MAP = new Float2LongMaps.EmptyMap();

   private Float2LongMaps() {
      super();
   }

   public static ObjectIterator<Float2LongMap.Entry> fastIterator(Float2LongMap var0) {
      ObjectSet var1 = var0.float2LongEntrySet();
      return var1 instanceof Float2LongMap.FastEntrySet ? ((Float2LongMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Float2LongMap var0, Consumer<? super Float2LongMap.Entry> var1) {
      ObjectSet var2 = var0.float2LongEntrySet();
      if (var2 instanceof Float2LongMap.FastEntrySet) {
         ((Float2LongMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Float2LongMap.Entry> fastIterable(Float2LongMap var0) {
      final ObjectSet var1 = var0.float2LongEntrySet();
      return (ObjectIterable)(var1 instanceof Float2LongMap.FastEntrySet ? new ObjectIterable<Float2LongMap.Entry>() {
         public ObjectIterator<Float2LongMap.Entry> iterator() {
            return ((Float2LongMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Float2LongMap.Entry> var1x) {
            ((Float2LongMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Float2LongMap singleton(float var0, long var1) {
      return new Float2LongMaps.Singleton(var0, var1);
   }

   public static Float2LongMap singleton(Float var0, Long var1) {
      return new Float2LongMaps.Singleton(var0, var1);
   }

   public static Float2LongMap synchronize(Float2LongMap var0) {
      return new Float2LongMaps.SynchronizedMap(var0);
   }

   public static Float2LongMap synchronize(Float2LongMap var0, Object var1) {
      return new Float2LongMaps.SynchronizedMap(var0, var1);
   }

   public static Float2LongMap unmodifiable(Float2LongMap var0) {
      return new Float2LongMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Float2LongFunctions.UnmodifiableFunction implements Float2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2LongMap map;
      protected transient ObjectSet<Float2LongMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient LongCollection values;

      protected UnmodifiableMap(Float2LongMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(long var1) {
         return this.map.containsValue(var1);
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return this.map.containsValue(var1);
      }

      public void putAll(Map<? extends Float, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2LongMap.Entry> float2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.float2LongEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Long>> entrySet() {
         return this.float2LongEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.unmodifiable(this.map.keySet());
         }

         return this.keys;
      }

      public LongCollection values() {
         return this.values == null ? LongCollections.unmodifiable(this.map.values()) : this.values;
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

      public long getOrDefault(float var1, long var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Float, ? super Long> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Float, ? super Long, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public long putIfAbsent(float var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(float var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public long replace(float var1, long var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(float var1, long var2, long var4) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsent(float var1, DoubleToLongFunction var2) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsentNullable(float var1, DoubleFunction<? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long computeIfAbsentPartial(float var1, Float2LongFunction var2) {
         throw new UnsupportedOperationException();
      }

      public long computeIfPresent(float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long compute(float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      public long merge(float var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long getOrDefault(Object var1, Long var2) {
         return this.map.getOrDefault(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public boolean remove(Object var1, Object var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long replace(Float var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Long var2, Long var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(Float var1, Long var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfAbsent(Float var1, Function<? super Float, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfPresent(Float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long compute(Float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Long merge(Float var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Float2LongFunctions.SynchronizedFunction implements Float2LongMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2LongMap map;
      protected transient ObjectSet<Float2LongMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient LongCollection values;

      protected SynchronizedMap(Float2LongMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Float2LongMap var1) {
         super(var1);
         this.map = var1;
      }

      public boolean containsValue(long var1) {
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

      public void putAll(Map<? extends Float, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Float2LongMap.Entry> float2LongEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.float2LongEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Long>> entrySet() {
         return this.float2LongEntrySet();
      }

      public FloatSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = FloatSets.synchronize(this.map.keySet(), this.sync);
            }

            return this.keys;
         }
      }

      public LongCollection values() {
         synchronized(this.sync) {
            return this.values == null ? LongCollections.synchronize(this.map.values(), this.sync) : this.values;
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

      public long getOrDefault(float var1, long var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Float, ? super Long> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Float, ? super Long, ? extends Long> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public long putIfAbsent(float var1, long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(float var1, long var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public long replace(float var1, long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(float var1, long var2, long var4) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var4);
         }
      }

      public long computeIfAbsent(float var1, DoubleToLongFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public long computeIfAbsentNullable(float var1, DoubleFunction<? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public long computeIfAbsentPartial(float var1, Float2LongFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public long computeIfPresent(float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public long compute(float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public long merge(float var1, long var2, BiFunction<? super Long, ? super Long, ? extends Long> var4) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var4);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long getOrDefault(Object var1, Long var2) {
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
      public Long replace(Float var1, Long var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Float var1, Long var2, Long var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long putIfAbsent(Float var1, Long var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfAbsent(Float var1, Function<? super Float, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long computeIfPresent(Float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long compute(Float var1, BiFunction<? super Float, ? super Long, ? extends Long> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Long merge(Float var1, Long var2, BiFunction<? super Long, ? super Long, ? extends Long> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Float2LongFunctions.Singleton implements Float2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Float2LongMap.Entry> entries;
      protected transient FloatSet keys;
      protected transient LongCollection values;

      protected Singleton(float var1, long var2) {
         super(var1, var2);
      }

      public boolean containsValue(long var1) {
         return this.value == var1;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return (Long)var1 == this.value;
      }

      public void putAll(Map<? extends Float, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2LongMap.Entry> float2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractFloat2LongMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Float, Long>> entrySet() {
         return this.float2LongEntrySet();
      }

      public FloatSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSets.singleton(this.key);
         }

         return this.keys;
      }

      public LongCollection values() {
         if (this.values == null) {
            this.values = LongSets.singleton(this.value);
         }

         return this.values;
      }

      public boolean isEmpty() {
         return false;
      }

      public int hashCode() {
         return HashCommon.float2int(this.key) ^ HashCommon.long2int(this.value);
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

   public static class EmptyMap extends Float2LongFunctions.EmptyFunction implements Float2LongMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyMap() {
         super();
      }

      public boolean containsValue(long var1) {
         return false;
      }

      /** @deprecated */
      @Deprecated
      public boolean containsValue(Object var1) {
         return false;
      }

      public void putAll(Map<? extends Float, ? extends Long> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Float2LongMap.Entry> float2LongEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public FloatSet keySet() {
         return FloatSets.EMPTY_SET;
      }

      public LongCollection values() {
         return LongSets.EMPTY_SET;
      }

      public Object clone() {
         return Float2LongMaps.EMPTY_MAP;
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
