package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Float2ByteSortedMaps {
   public static final Float2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Float2ByteSortedMaps.EmptySortedMap();

   private Float2ByteSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Float)var1.getKey(), (Float)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Float2ByteMap.Entry> fastIterator(Float2ByteSortedMap var0) {
      ObjectSortedSet var1 = var0.float2ByteEntrySet();
      return var1 instanceof Float2ByteSortedMap.FastSortedEntrySet ? ((Float2ByteSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Float2ByteMap.Entry> fastIterable(Float2ByteSortedMap var0) {
      ObjectSortedSet var1 = var0.float2ByteEntrySet();
      Object var2;
      if (var1 instanceof Float2ByteSortedMap.FastSortedEntrySet) {
         Float2ByteSortedMap.FastSortedEntrySet var10000 = (Float2ByteSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Float2ByteSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Float2ByteSortedMap singleton(Float var0, Byte var1) {
      return new Float2ByteSortedMaps.Singleton(var0, var1);
   }

   public static Float2ByteSortedMap singleton(Float var0, Byte var1, FloatComparator var2) {
      return new Float2ByteSortedMaps.Singleton(var0, var1, var2);
   }

   public static Float2ByteSortedMap singleton(float var0, byte var1) {
      return new Float2ByteSortedMaps.Singleton(var0, var1);
   }

   public static Float2ByteSortedMap singleton(float var0, byte var1, FloatComparator var2) {
      return new Float2ByteSortedMaps.Singleton(var0, var1, var2);
   }

   public static Float2ByteSortedMap synchronize(Float2ByteSortedMap var0) {
      return new Float2ByteSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Float2ByteSortedMap synchronize(Float2ByteSortedMap var0, Object var1) {
      return new Float2ByteSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Float2ByteSortedMap unmodifiable(Float2ByteSortedMap var0) {
      return new Float2ByteSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Float2ByteMaps.UnmodifiableMap implements Float2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2ByteSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2ByteEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      public Float2ByteSortedMap subMap(float var1, float var2) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Float2ByteSortedMap headMap(float var1) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Float2ByteSortedMap tailMap(float var1) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public float firstFloatKey() {
         return this.sortedMap.firstFloatKey();
      }

      public float lastFloatKey() {
         return this.sortedMap.lastFloatKey();
      }

      /** @deprecated */
      @Deprecated
      public Float firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Float lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap subMap(Float var1, Float var2) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap headMap(Float var1) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap tailMap(Float var1) {
         return new Float2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Float2ByteMaps.SynchronizedMap implements Float2ByteSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2ByteSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2ByteSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Float2ByteSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public FloatComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      public Float2ByteSortedMap subMap(float var1, float var2) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Float2ByteSortedMap headMap(float var1) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Float2ByteSortedMap tailMap(float var1) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public float firstFloatKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstFloatKey();
         }
      }

      public float lastFloatKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastFloatKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap subMap(Float var1, Float var2) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap headMap(Float var1) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap tailMap(Float var1) {
         return new Float2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Float2ByteMaps.Singleton implements Float2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float var1, byte var2, FloatComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(float var1, byte var2) {
         this(var1, var2, (FloatComparator)null);
      }

      final int compare(float var1, float var2) {
         return this.comparator == null ? Float.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public FloatComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractFloat2ByteMap.BasicEntry(this.key, this.value), Float2ByteSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return this.float2ByteEntrySet();
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      public Float2ByteSortedMap subMap(float var1, float var2) {
         return (Float2ByteSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Float2ByteSortedMaps.EMPTY_MAP);
      }

      public Float2ByteSortedMap headMap(float var1) {
         return (Float2ByteSortedMap)(this.compare(this.key, var1) < 0 ? this : Float2ByteSortedMaps.EMPTY_MAP);
      }

      public Float2ByteSortedMap tailMap(float var1) {
         return (Float2ByteSortedMap)(this.compare(var1, this.key) <= 0 ? this : Float2ByteSortedMaps.EMPTY_MAP);
      }

      public float firstFloatKey() {
         return this.key;
      }

      public float lastFloatKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap headMap(Float var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap tailMap(Float var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap subMap(Float var1, Float var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Float firstKey() {
         return this.firstFloatKey();
      }

      /** @deprecated */
      @Deprecated
      public Float lastKey() {
         return this.lastFloatKey();
      }
   }

   public static class EmptySortedMap extends Float2ByteMaps.EmptyMap implements Float2ByteSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public FloatComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Float2ByteMap.Entry> float2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      public Float2ByteSortedMap subMap(float var1, float var2) {
         return Float2ByteSortedMaps.EMPTY_MAP;
      }

      public Float2ByteSortedMap headMap(float var1) {
         return Float2ByteSortedMaps.EMPTY_MAP;
      }

      public Float2ByteSortedMap tailMap(float var1) {
         return Float2ByteSortedMaps.EMPTY_MAP;
      }

      public float firstFloatKey() {
         throw new NoSuchElementException();
      }

      public float lastFloatKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap headMap(Float var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap tailMap(Float var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2ByteSortedMap subMap(Float var1, Float var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Float firstKey() {
         return this.firstFloatKey();
      }

      /** @deprecated */
      @Deprecated
      public Float lastKey() {
         return this.lastFloatKey();
      }
   }
}
