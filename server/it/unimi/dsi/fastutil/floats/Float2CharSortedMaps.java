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

public final class Float2CharSortedMaps {
   public static final Float2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Float2CharSortedMaps.EmptySortedMap();

   private Float2CharSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Float, ?>> entryComparator(FloatComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Float)var1.getKey(), (Float)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Float2CharMap.Entry> fastIterator(Float2CharSortedMap var0) {
      ObjectSortedSet var1 = var0.float2CharEntrySet();
      return var1 instanceof Float2CharSortedMap.FastSortedEntrySet ? ((Float2CharSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Float2CharMap.Entry> fastIterable(Float2CharSortedMap var0) {
      ObjectSortedSet var1 = var0.float2CharEntrySet();
      Object var2;
      if (var1 instanceof Float2CharSortedMap.FastSortedEntrySet) {
         Float2CharSortedMap.FastSortedEntrySet var10000 = (Float2CharSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Float2CharSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Float2CharSortedMap singleton(Float var0, Character var1) {
      return new Float2CharSortedMaps.Singleton(var0, var1);
   }

   public static Float2CharSortedMap singleton(Float var0, Character var1, FloatComparator var2) {
      return new Float2CharSortedMaps.Singleton(var0, var1, var2);
   }

   public static Float2CharSortedMap singleton(float var0, char var1) {
      return new Float2CharSortedMaps.Singleton(var0, var1);
   }

   public static Float2CharSortedMap singleton(float var0, char var1, FloatComparator var2) {
      return new Float2CharSortedMaps.Singleton(var0, var1, var2);
   }

   public static Float2CharSortedMap synchronize(Float2CharSortedMap var0) {
      return new Float2CharSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Float2CharSortedMap synchronize(Float2CharSortedMap var0, Object var1) {
      return new Float2CharSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Float2CharSortedMap unmodifiable(Float2CharSortedMap var0) {
      return new Float2CharSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Float2CharMaps.UnmodifiableMap implements Float2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Float2CharSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public FloatComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.float2CharEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (FloatSortedSet)this.keys;
      }

      public Float2CharSortedMap subMap(float var1, float var2) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Float2CharSortedMap headMap(float var1) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Float2CharSortedMap tailMap(float var1) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Float2CharSortedMap subMap(Float var1, Float var2) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap headMap(Float var1) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap tailMap(Float var1) {
         return new Float2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Float2CharMaps.SynchronizedMap implements Float2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Float2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Float2CharSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Float2CharSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public FloatComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.float2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (FloatSortedSet)this.keys;
      }

      public Float2CharSortedMap subMap(float var1, float var2) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Float2CharSortedMap headMap(float var1) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Float2CharSortedMap tailMap(float var1) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Float2CharSortedMap subMap(Float var1, Float var2) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap headMap(Float var1) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap tailMap(Float var1) {
         return new Float2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Float2CharMaps.Singleton implements Float2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatComparator comparator;

      protected Singleton(float var1, char var2, FloatComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(float var1, char var2) {
         this(var1, var2, (FloatComparator)null);
      }

      final int compare(float var1, float var2) {
         return this.comparator == null ? Float.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public FloatComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractFloat2CharMap.BasicEntry(this.key, this.value), Float2CharSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return this.float2CharEntrySet();
      }

      public FloatSortedSet keySet() {
         if (this.keys == null) {
            this.keys = FloatSortedSets.singleton(this.key, this.comparator);
         }

         return (FloatSortedSet)this.keys;
      }

      public Float2CharSortedMap subMap(float var1, float var2) {
         return (Float2CharSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Float2CharSortedMaps.EMPTY_MAP);
      }

      public Float2CharSortedMap headMap(float var1) {
         return (Float2CharSortedMap)(this.compare(this.key, var1) < 0 ? this : Float2CharSortedMaps.EMPTY_MAP);
      }

      public Float2CharSortedMap tailMap(float var1) {
         return (Float2CharSortedMap)(this.compare(var1, this.key) <= 0 ? this : Float2CharSortedMaps.EMPTY_MAP);
      }

      public float firstFloatKey() {
         return this.key;
      }

      public float lastFloatKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap headMap(Float var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap tailMap(Float var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap subMap(Float var1, Float var2) {
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

   public static class EmptySortedMap extends Float2CharMaps.EmptyMap implements Float2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public FloatComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Float2CharMap.Entry> float2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Float, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public FloatSortedSet keySet() {
         return FloatSortedSets.EMPTY_SET;
      }

      public Float2CharSortedMap subMap(float var1, float var2) {
         return Float2CharSortedMaps.EMPTY_MAP;
      }

      public Float2CharSortedMap headMap(float var1) {
         return Float2CharSortedMaps.EMPTY_MAP;
      }

      public Float2CharSortedMap tailMap(float var1) {
         return Float2CharSortedMaps.EMPTY_MAP;
      }

      public float firstFloatKey() {
         throw new NoSuchElementException();
      }

      public float lastFloatKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap headMap(Float var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap tailMap(Float var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Float2CharSortedMap subMap(Float var1, Float var2) {
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
