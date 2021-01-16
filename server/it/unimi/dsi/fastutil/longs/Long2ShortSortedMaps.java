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

public final class Long2ShortSortedMaps {
   public static final Long2ShortSortedMaps.EmptySortedMap EMPTY_MAP = new Long2ShortSortedMaps.EmptySortedMap();

   private Long2ShortSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Long)var1.getKey(), (Long)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Long2ShortMap.Entry> fastIterator(Long2ShortSortedMap var0) {
      ObjectSortedSet var1 = var0.long2ShortEntrySet();
      return var1 instanceof Long2ShortSortedMap.FastSortedEntrySet ? ((Long2ShortSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Long2ShortMap.Entry> fastIterable(Long2ShortSortedMap var0) {
      ObjectSortedSet var1 = var0.long2ShortEntrySet();
      Object var2;
      if (var1 instanceof Long2ShortSortedMap.FastSortedEntrySet) {
         Long2ShortSortedMap.FastSortedEntrySet var10000 = (Long2ShortSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Long2ShortSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Long2ShortSortedMap singleton(Long var0, Short var1) {
      return new Long2ShortSortedMaps.Singleton(var0, var1);
   }

   public static Long2ShortSortedMap singleton(Long var0, Short var1, LongComparator var2) {
      return new Long2ShortSortedMaps.Singleton(var0, var1, var2);
   }

   public static Long2ShortSortedMap singleton(long var0, short var2) {
      return new Long2ShortSortedMaps.Singleton(var0, var2);
   }

   public static Long2ShortSortedMap singleton(long var0, short var2, LongComparator var3) {
      return new Long2ShortSortedMaps.Singleton(var0, var2, var3);
   }

   public static Long2ShortSortedMap synchronize(Long2ShortSortedMap var0) {
      return new Long2ShortSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Long2ShortSortedMap synchronize(Long2ShortSortedMap var0, Object var1) {
      return new Long2ShortSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Long2ShortSortedMap unmodifiable(Long2ShortSortedMap var0) {
      return new Long2ShortSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Long2ShortMaps.UnmodifiableMap implements Long2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2ShortSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2ShortEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      public Long2ShortSortedMap subMap(long var1, long var3) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var3));
      }

      public Long2ShortSortedMap headMap(long var1) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Long2ShortSortedMap tailMap(long var1) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Long2ShortSortedMap subMap(Long var1, Long var2) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap headMap(Long var1) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap tailMap(Long var1) {
         return new Long2ShortSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Long2ShortMaps.SynchronizedMap implements Long2ShortSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ShortSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2ShortSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Long2ShortSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2ShortEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2ShortSortedMap subMap(long var1, long var3) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var3), this.sync);
      }

      public Long2ShortSortedMap headMap(long var1) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Long2ShortSortedMap tailMap(long var1) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Long2ShortSortedMap subMap(Long var1, Long var2) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap headMap(Long var1) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap tailMap(Long var1) {
         return new Long2ShortSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Long2ShortMaps.Singleton implements Long2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long var1, short var3, LongComparator var4) {
         super(var1, var3);
         this.comparator = var4;
      }

      protected Singleton(long var1, short var3) {
         this(var1, var3, (LongComparator)null);
      }

      final int compare(long var1, long var3) {
         return this.comparator == null ? Long.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public LongComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractLong2ShortMap.BasicEntry(this.key, this.value), Long2ShortSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return this.long2ShortEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2ShortSortedMap subMap(long var1, long var3) {
         return (Long2ShortSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var3) < 0 ? this : Long2ShortSortedMaps.EMPTY_MAP);
      }

      public Long2ShortSortedMap headMap(long var1) {
         return (Long2ShortSortedMap)(this.compare(this.key, var1) < 0 ? this : Long2ShortSortedMaps.EMPTY_MAP);
      }

      public Long2ShortSortedMap tailMap(long var1) {
         return (Long2ShortSortedMap)(this.compare(var1, this.key) <= 0 ? this : Long2ShortSortedMaps.EMPTY_MAP);
      }

      public long firstLongKey() {
         return this.key;
      }

      public long lastLongKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap subMap(Long var1, Long var2) {
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

   public static class EmptySortedMap extends Long2ShortMaps.EmptyMap implements Long2ShortSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public LongComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Long2ShortMap.Entry> long2ShortEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Short>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      public Long2ShortSortedMap subMap(long var1, long var3) {
         return Long2ShortSortedMaps.EMPTY_MAP;
      }

      public Long2ShortSortedMap headMap(long var1) {
         return Long2ShortSortedMaps.EMPTY_MAP;
      }

      public Long2ShortSortedMap tailMap(long var1) {
         return Long2ShortSortedMaps.EMPTY_MAP;
      }

      public long firstLongKey() {
         throw new NoSuchElementException();
      }

      public long lastLongKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ShortSortedMap subMap(Long var1, Long var2) {
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
