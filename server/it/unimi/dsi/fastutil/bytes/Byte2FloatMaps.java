package it.unimi.dsi.fastutil.bytes;

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

public final class Byte2FloatMaps {
   public static final Byte2FloatMaps.EmptyMap EMPTY_MAP = new Byte2FloatMaps.EmptyMap();

   private Byte2FloatMaps() {
      super();
   }

   public static ObjectIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatMap var0) {
      ObjectSet var1 = var0.byte2FloatEntrySet();
      return var1 instanceof Byte2FloatMap.FastEntrySet ? ((Byte2FloatMap.FastEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static void fastForEach(Byte2FloatMap var0, Consumer<? super Byte2FloatMap.Entry> var1) {
      ObjectSet var2 = var0.byte2FloatEntrySet();
      if (var2 instanceof Byte2FloatMap.FastEntrySet) {
         ((Byte2FloatMap.FastEntrySet)var2).fastForEach(var1);
      } else {
         var2.forEach(var1);
      }

   }

   public static ObjectIterable<Byte2FloatMap.Entry> fastIterable(Byte2FloatMap var0) {
      final ObjectSet var1 = var0.byte2FloatEntrySet();
      return (ObjectIterable)(var1 instanceof Byte2FloatMap.FastEntrySet ? new ObjectIterable<Byte2FloatMap.Entry>() {
         public ObjectIterator<Byte2FloatMap.Entry> iterator() {
            return ((Byte2FloatMap.FastEntrySet)var1).fastIterator();
         }

         public void forEach(Consumer<? super Byte2FloatMap.Entry> var1x) {
            ((Byte2FloatMap.FastEntrySet)var1).fastForEach(var1x);
         }
      } : var1);
   }

   public static Byte2FloatMap singleton(byte var0, float var1) {
      return new Byte2FloatMaps.Singleton(var0, var1);
   }

   public static Byte2FloatMap singleton(Byte var0, Float var1) {
      return new Byte2FloatMaps.Singleton(var0, var1);
   }

   public static Byte2FloatMap synchronize(Byte2FloatMap var0) {
      return new Byte2FloatMaps.SynchronizedMap(var0);
   }

   public static Byte2FloatMap synchronize(Byte2FloatMap var0, Object var1) {
      return new Byte2FloatMaps.SynchronizedMap(var0, var1);
   }

   public static Byte2FloatMap unmodifiable(Byte2FloatMap var0) {
      return new Byte2FloatMaps.UnmodifiableMap(var0);
   }

   public static class UnmodifiableMap extends Byte2FloatFunctions.UnmodifiableFunction implements Byte2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatMap map;
      protected transient ObjectSet<Byte2FloatMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient FloatCollection values;

      protected UnmodifiableMap(Byte2FloatMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.unmodifiable(this.map.byte2FloatEntrySet());
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.unmodifiable(this.map.keySet());
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

      public float getOrDefault(byte var1, float var2) {
         return this.map.getOrDefault(var1, var2);
      }

      public void forEach(BiConsumer<? super Byte, ? super Float> var1) {
         this.map.forEach(var1);
      }

      public void replaceAll(BiFunction<? super Byte, ? super Float, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public float putIfAbsent(byte var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(byte var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public float replace(byte var1, float var2) {
         throw new UnsupportedOperationException();
      }

      public boolean replace(byte var1, float var2, float var3) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsent(byte var1, IntToDoubleFunction var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentNullable(byte var1, IntFunction<? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfAbsentPartial(byte var1, Byte2FloatFunction var2) {
         throw new UnsupportedOperationException();
      }

      public float computeIfPresent(byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float compute(byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      public float merge(byte var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
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
      public Float replace(Byte var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Float var2, Float var3) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Byte var1, Float var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Byte var1, Function<? super Byte, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         throw new UnsupportedOperationException();
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Byte var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         throw new UnsupportedOperationException();
      }
   }

   public static class SynchronizedMap extends Byte2FloatFunctions.SynchronizedFunction implements Byte2FloatMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatMap map;
      protected transient ObjectSet<Byte2FloatMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient FloatCollection values;

      protected SynchronizedMap(Byte2FloatMap var1, Object var2) {
         super(var1, var2);
         this.map = var1;
      }

      protected SynchronizedMap(Byte2FloatMap var1) {
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

      public void putAll(Map<? extends Byte, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.putAll(var1);
         }
      }

      public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         synchronized(this.sync) {
            if (this.entries == null) {
               this.entries = ObjectSets.synchronize(this.map.byte2FloatEntrySet(), this.sync);
            }

            return this.entries;
         }
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      public ByteSet keySet() {
         synchronized(this.sync) {
            if (this.keys == null) {
               this.keys = ByteSets.synchronize(this.map.keySet(), this.sync);
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

      public float getOrDefault(byte var1, float var2) {
         synchronized(this.sync) {
            return this.map.getOrDefault(var1, var2);
         }
      }

      public void forEach(BiConsumer<? super Byte, ? super Float> var1) {
         synchronized(this.sync) {
            this.map.forEach(var1);
         }
      }

      public void replaceAll(BiFunction<? super Byte, ? super Float, ? extends Float> var1) {
         synchronized(this.sync) {
            this.map.replaceAll(var1);
         }
      }

      public float putIfAbsent(byte var1, float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      public boolean remove(byte var1, float var2) {
         synchronized(this.sync) {
            return this.map.remove(var1, var2);
         }
      }

      public float replace(byte var1, float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      public boolean replace(byte var1, float var2, float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      public float computeIfAbsent(byte var1, IntToDoubleFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      public float computeIfAbsentNullable(byte var1, IntFunction<? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentNullable(var1, var2);
         }
      }

      public float computeIfAbsentPartial(byte var1, Byte2FloatFunction var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsentPartial(var1, var2);
         }
      }

      public float computeIfPresent(byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      public float compute(byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      public float merge(byte var1, float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
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
      public Float replace(Byte var1, Float var2) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public boolean replace(Byte var1, Float var2, Float var3) {
         synchronized(this.sync) {
            return this.map.replace(var1, var2, var3);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float putIfAbsent(Byte var1, Float var2) {
         synchronized(this.sync) {
            return this.map.putIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfAbsent(Byte var1, Function<? super Byte, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfAbsent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float computeIfPresent(Byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.computeIfPresent(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float compute(Byte var1, BiFunction<? super Byte, ? super Float, ? extends Float> var2) {
         synchronized(this.sync) {
            return this.map.compute(var1, var2);
         }
      }

      /** @deprecated */
      @Deprecated
      public Float merge(Byte var1, Float var2, BiFunction<? super Float, ? super Float, ? extends Float> var3) {
         synchronized(this.sync) {
            return this.map.merge(var1, var2, var3);
         }
      }
   }

   public static class Singleton extends Byte2FloatFunctions.Singleton implements Byte2FloatMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected transient ObjectSet<Byte2FloatMap.Entry> entries;
      protected transient ByteSet keys;
      protected transient FloatCollection values;

      protected Singleton(byte var1, float var2) {
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

      public void putAll(Map<? extends Byte, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSets.singleton(new AbstractByte2FloatMap.BasicEntry(this.key, this.value));
         }

         return this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      public ByteSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSets.singleton(this.key);
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

   public static class EmptyMap extends Byte2FloatFunctions.EmptyFunction implements Byte2FloatMap, Serializable, Cloneable {
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

      public void putAll(Map<? extends Byte, ? extends Float> var1) {
         throw new UnsupportedOperationException();
      }

      public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         return ObjectSets.EMPTY_SET;
      }

      public ByteSet keySet() {
         return ByteSets.EMPTY_SET;
      }

      public FloatCollection values() {
         return FloatSets.EMPTY_SET;
      }

      public Object clone() {
         return Byte2FloatMaps.EMPTY_MAP;
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
