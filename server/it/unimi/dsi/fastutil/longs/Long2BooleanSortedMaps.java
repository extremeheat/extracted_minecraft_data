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

public final class Long2BooleanSortedMaps {
   public static final Long2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Long2BooleanSortedMaps.EmptySortedMap();

   private Long2BooleanSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Long)var1.getKey(), (Long)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Long2BooleanMap.Entry> fastIterator(Long2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.long2BooleanEntrySet();
      return var1 instanceof Long2BooleanSortedMap.FastSortedEntrySet ? ((Long2BooleanSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Long2BooleanMap.Entry> fastIterable(Long2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.long2BooleanEntrySet();
      Object var2;
      if (var1 instanceof Long2BooleanSortedMap.FastSortedEntrySet) {
         Long2BooleanSortedMap.FastSortedEntrySet var10000 = (Long2BooleanSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Long2BooleanSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Long2BooleanSortedMap singleton(Long var0, Boolean var1) {
      return new Long2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Long2BooleanSortedMap singleton(Long var0, Boolean var1, LongComparator var2) {
      return new Long2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Long2BooleanSortedMap singleton(long var0, boolean var2) {
      return new Long2BooleanSortedMaps.Singleton(var0, var2);
   }

   public static Long2BooleanSortedMap singleton(long var0, boolean var2, LongComparator var3) {
      return new Long2BooleanSortedMaps.Singleton(var0, var2, var3);
   }

   public static Long2BooleanSortedMap synchronize(Long2BooleanSortedMap var0) {
      return new Long2BooleanSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Long2BooleanSortedMap synchronize(Long2BooleanSortedMap var0, Object var1) {
      return new Long2BooleanSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Long2BooleanSortedMap unmodifiable(Long2BooleanSortedMap var0) {
      return new Long2BooleanSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Long2BooleanMaps.UnmodifiableMap implements Long2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Long2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2BooleanEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      public Long2BooleanSortedMap subMap(long var1, long var3) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var3));
      }

      public Long2BooleanSortedMap headMap(long var1) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Long2BooleanSortedMap tailMap(long var1) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Long2BooleanSortedMap subMap(Long var1, Long var2) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap headMap(Long var1) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap tailMap(Long var1) {
         return new Long2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Long2BooleanMaps.SynchronizedMap implements Long2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Long2BooleanSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Long2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2BooleanSortedMap subMap(long var1, long var3) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var3), this.sync);
      }

      public Long2BooleanSortedMap headMap(long var1) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Long2BooleanSortedMap tailMap(long var1) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Long2BooleanSortedMap subMap(Long var1, Long var2) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap headMap(Long var1) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap tailMap(Long var1) {
         return new Long2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Long2BooleanMaps.Singleton implements Long2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long var1, boolean var3, LongComparator var4) {
         super(var1, var3);
         this.comparator = var4;
      }

      protected Singleton(long var1, boolean var3) {
         this(var1, var3, (LongComparator)null);
      }

      final int compare(long var1, long var3) {
         return this.comparator == null ? Long.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public LongComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractLong2BooleanMap.BasicEntry(this.key, this.value), Long2BooleanSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return this.long2BooleanEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2BooleanSortedMap subMap(long var1, long var3) {
         return (Long2BooleanSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var3) < 0 ? this : Long2BooleanSortedMaps.EMPTY_MAP);
      }

      public Long2BooleanSortedMap headMap(long var1) {
         return (Long2BooleanSortedMap)(this.compare(this.key, var1) < 0 ? this : Long2BooleanSortedMaps.EMPTY_MAP);
      }

      public Long2BooleanSortedMap tailMap(long var1) {
         return (Long2BooleanSortedMap)(this.compare(var1, this.key) <= 0 ? this : Long2BooleanSortedMaps.EMPTY_MAP);
      }

      public long firstLongKey() {
         return this.key;
      }

      public long lastLongKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap subMap(Long var1, Long var2) {
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

   public static class EmptySortedMap extends Long2BooleanMaps.EmptyMap implements Long2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public LongComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Long2BooleanMap.Entry> long2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      public Long2BooleanSortedMap subMap(long var1, long var3) {
         return Long2BooleanSortedMaps.EMPTY_MAP;
      }

      public Long2BooleanSortedMap headMap(long var1) {
         return Long2BooleanSortedMaps.EMPTY_MAP;
      }

      public Long2BooleanSortedMap tailMap(long var1) {
         return Long2BooleanSortedMaps.EMPTY_MAP;
      }

      public long firstLongKey() {
         throw new NoSuchElementException();
      }

      public long lastLongKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2BooleanSortedMap subMap(Long var1, Long var2) {
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
