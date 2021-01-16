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

public final class Long2LongSortedMaps {
   public static final Long2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Long2LongSortedMaps.EmptySortedMap();

   private Long2LongSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Long)var1.getKey(), (Long)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Long2LongMap.Entry> fastIterator(Long2LongSortedMap var0) {
      ObjectSortedSet var1 = var0.long2LongEntrySet();
      return var1 instanceof Long2LongSortedMap.FastSortedEntrySet ? ((Long2LongSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Long2LongMap.Entry> fastIterable(Long2LongSortedMap var0) {
      ObjectSortedSet var1 = var0.long2LongEntrySet();
      Object var2;
      if (var1 instanceof Long2LongSortedMap.FastSortedEntrySet) {
         Long2LongSortedMap.FastSortedEntrySet var10000 = (Long2LongSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Long2LongSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Long2LongSortedMap singleton(Long var0, Long var1) {
      return new Long2LongSortedMaps.Singleton(var0, var1);
   }

   public static Long2LongSortedMap singleton(Long var0, Long var1, LongComparator var2) {
      return new Long2LongSortedMaps.Singleton(var0, var1, var2);
   }

   public static Long2LongSortedMap singleton(long var0, long var2) {
      return new Long2LongSortedMaps.Singleton(var0, var2);
   }

   public static Long2LongSortedMap singleton(long var0, long var2, LongComparator var4) {
      return new Long2LongSortedMaps.Singleton(var0, var2, var4);
   }

   public static Long2LongSortedMap synchronize(Long2LongSortedMap var0) {
      return new Long2LongSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Long2LongSortedMap synchronize(Long2LongSortedMap var0, Object var1) {
      return new Long2LongSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Long2LongSortedMap unmodifiable(Long2LongSortedMap var0) {
      return new Long2LongSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Long2LongMaps.UnmodifiableMap implements Long2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2LongSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2LongEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      public Long2LongSortedMap subMap(long var1, long var3) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var3));
      }

      public Long2LongSortedMap headMap(long var1) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Long2LongSortedMap tailMap(long var1) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Long2LongSortedMap subMap(Long var1, Long var2) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap headMap(Long var1) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap tailMap(Long var1) {
         return new Long2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Long2LongMaps.SynchronizedMap implements Long2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2LongSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2LongSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Long2LongSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2LongSortedMap subMap(long var1, long var3) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var3), this.sync);
      }

      public Long2LongSortedMap headMap(long var1) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Long2LongSortedMap tailMap(long var1) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Long2LongSortedMap subMap(Long var1, Long var2) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap headMap(Long var1) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap tailMap(Long var1) {
         return new Long2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Long2LongMaps.Singleton implements Long2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long var1, long var3, LongComparator var5) {
         super(var1, var3);
         this.comparator = var5;
      }

      protected Singleton(long var1, long var3) {
         this(var1, var3, (LongComparator)null);
      }

      final int compare(long var1, long var3) {
         return this.comparator == null ? Long.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public LongComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractLong2LongMap.BasicEntry(this.key, this.value), Long2LongSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return this.long2LongEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2LongSortedMap subMap(long var1, long var3) {
         return (Long2LongSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var3) < 0 ? this : Long2LongSortedMaps.EMPTY_MAP);
      }

      public Long2LongSortedMap headMap(long var1) {
         return (Long2LongSortedMap)(this.compare(this.key, var1) < 0 ? this : Long2LongSortedMaps.EMPTY_MAP);
      }

      public Long2LongSortedMap tailMap(long var1) {
         return (Long2LongSortedMap)(this.compare(var1, this.key) <= 0 ? this : Long2LongSortedMaps.EMPTY_MAP);
      }

      public long firstLongKey() {
         return this.key;
      }

      public long lastLongKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap subMap(Long var1, Long var2) {
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

   public static class EmptySortedMap extends Long2LongMaps.EmptyMap implements Long2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public LongComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Long2LongMap.Entry> long2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      public Long2LongSortedMap subMap(long var1, long var3) {
         return Long2LongSortedMaps.EMPTY_MAP;
      }

      public Long2LongSortedMap headMap(long var1) {
         return Long2LongSortedMaps.EMPTY_MAP;
      }

      public Long2LongSortedMap tailMap(long var1) {
         return Long2LongSortedMaps.EMPTY_MAP;
      }

      public long firstLongKey() {
         throw new NoSuchElementException();
      }

      public long lastLongKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2LongSortedMap subMap(Long var1, Long var2) {
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
