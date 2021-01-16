package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Long2DoubleSortedMaps {
   public static final Long2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Long2DoubleSortedMaps.EmptySortedMap();

   private Long2DoubleSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Long)var1.getKey(), (Long)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleSortedMap var0) {
      ObjectSortedSet var1 = var0.long2DoubleEntrySet();
      return var1 instanceof Long2DoubleSortedMap.FastSortedEntrySet ? ((Long2DoubleSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Long2DoubleMap.Entry> fastIterable(Long2DoubleSortedMap var0) {
      ObjectSortedSet var1 = var0.long2DoubleEntrySet();
      Object var2;
      if (var1 instanceof Long2DoubleSortedMap.FastSortedEntrySet) {
         Long2DoubleSortedMap.FastSortedEntrySet var10000 = (Long2DoubleSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Long2DoubleSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Long2DoubleSortedMap singleton(Long var0, Double var1) {
      return new Long2DoubleSortedMaps.Singleton(var0, var1);
   }

   public static Long2DoubleSortedMap singleton(Long var0, Double var1, LongComparator var2) {
      return new Long2DoubleSortedMaps.Singleton(var0, var1, var2);
   }

   public static Long2DoubleSortedMap singleton(long var0, double var2) {
      return new Long2DoubleSortedMaps.Singleton(var0, var2);
   }

   public static Long2DoubleSortedMap singleton(long var0, double var2, LongComparator var4) {
      return new Long2DoubleSortedMaps.Singleton(var0, var2, var4);
   }

   public static Long2DoubleSortedMap synchronize(Long2DoubleSortedMap var0) {
      return new Long2DoubleSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Long2DoubleSortedMap synchronize(Long2DoubleSortedMap var0, Object var1) {
      return new Long2DoubleSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Long2DoubleSortedMap unmodifiable(Long2DoubleSortedMap var0) {
      return new Long2DoubleSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Long2DoubleMaps.UnmodifiableMap implements Long2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2DoubleSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2DoubleEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      public Long2DoubleSortedMap subMap(long var1, long var3) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var3));
      }

      public Long2DoubleSortedMap headMap(long var1) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Long2DoubleSortedMap tailMap(long var1) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public long firstLongKey() {
         return this.sortedMap.firstLongKey();
      }

      public long lastLongKey() {
         return this.sortedMap.lastLongKey();
      }

      /** @deprecated */
      @Deprecated
      public Long firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Long lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap subMap(Long var1, Long var2) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap headMap(Long var1) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap tailMap(Long var1) {
         return new Long2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Long2DoubleMaps.SynchronizedMap implements Long2DoubleSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2DoubleSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2DoubleSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Long2DoubleSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2DoubleSortedMap subMap(long var1, long var3) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var3), this.sync);
      }

      public Long2DoubleSortedMap headMap(long var1) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Long2DoubleSortedMap tailMap(long var1) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public long firstLongKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstLongKey();
         }
      }

      public long lastLongKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastLongKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap subMap(Long var1, Long var2) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap headMap(Long var1) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap tailMap(Long var1) {
         return new Long2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Long2DoubleMaps.Singleton implements Long2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long var1, double var3, LongComparator var5) {
         super(var1, var3);
         this.comparator = var5;
      }

      protected Singleton(long var1, double var3) {
         this(var1, var3, (LongComparator)null);
      }

      final int compare(long var1, long var3) {
         return this.comparator == null ? Long.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public LongComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractLong2DoubleMap.BasicEntry(this.key, this.value), Long2DoubleSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return this.long2DoubleEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2DoubleSortedMap subMap(long var1, long var3) {
         return (Long2DoubleSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var3) < 0 ? this : Long2DoubleSortedMaps.EMPTY_MAP);
      }

      public Long2DoubleSortedMap headMap(long var1) {
         return (Long2DoubleSortedMap)(this.compare(this.key, var1) < 0 ? this : Long2DoubleSortedMaps.EMPTY_MAP);
      }

      public Long2DoubleSortedMap tailMap(long var1) {
         return (Long2DoubleSortedMap)(this.compare(var1, this.key) <= 0 ? this : Long2DoubleSortedMaps.EMPTY_MAP);
      }

      public long firstLongKey() {
         return this.key;
      }

      public long lastLongKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap subMap(Long var1, Long var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Long firstKey() {
         return this.firstLongKey();
      }

      /** @deprecated */
      @Deprecated
      public Long lastKey() {
         return this.lastLongKey();
      }
   }

   public static class EmptySortedMap extends Long2DoubleMaps.EmptyMap implements Long2DoubleSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public LongComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      public Long2DoubleSortedMap subMap(long var1, long var3) {
         return Long2DoubleSortedMaps.EMPTY_MAP;
      }

      public Long2DoubleSortedMap headMap(long var1) {
         return Long2DoubleSortedMaps.EMPTY_MAP;
      }

      public Long2DoubleSortedMap tailMap(long var1) {
         return Long2DoubleSortedMaps.EMPTY_MAP;
      }

      public long firstLongKey() {
         throw new NoSuchElementException();
      }

      public long lastLongKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2DoubleSortedMap subMap(Long var1, Long var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Long firstKey() {
         return this.firstLongKey();
      }

      /** @deprecated */
      @Deprecated
      public Long lastKey() {
         return this.lastLongKey();
      }
   }
}
