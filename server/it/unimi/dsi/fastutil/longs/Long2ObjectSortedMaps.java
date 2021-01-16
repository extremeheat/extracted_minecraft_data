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

public final class Long2ObjectSortedMaps {
   public static final Long2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Long2ObjectSortedMaps.EmptySortedMap();

   private Long2ObjectSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Long, ?>> entryComparator(LongComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Long)var1.getKey(), (Long)var2.getKey());
      };
   }

   public static <V> ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> fastIterator(Long2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.long2ObjectEntrySet();
      return var1 instanceof Long2ObjectSortedMap.FastSortedEntrySet ? ((Long2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Long2ObjectMap.Entry<V>> fastIterable(Long2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.long2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Long2ObjectSortedMap.FastSortedEntrySet) {
         Long2ObjectSortedMap.FastSortedEntrySet var10000 = (Long2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Long2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <V> Long2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Long2ObjectSortedMap<V> singleton(Long var0, V var1) {
      return new Long2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Long2ObjectSortedMap<V> singleton(Long var0, V var1, LongComparator var2) {
      return new Long2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Long2ObjectSortedMap<V> singleton(long var0, V var2) {
      return new Long2ObjectSortedMaps.Singleton(var0, var2);
   }

   public static <V> Long2ObjectSortedMap<V> singleton(long var0, V var2, LongComparator var3) {
      return new Long2ObjectSortedMaps.Singleton(var0, var2, var3);
   }

   public static <V> Long2ObjectSortedMap<V> synchronize(Long2ObjectSortedMap<V> var0) {
      return new Long2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <V> Long2ObjectSortedMap<V> synchronize(Long2ObjectSortedMap<V> var0, Object var1) {
      return new Long2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <V> Long2ObjectSortedMap<V> unmodifiable(Long2ObjectSortedMap<V> var0) {
      return new Long2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<V> extends Long2ObjectMaps.UnmodifiableMap<V> implements Long2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ObjectSortedMap<V> sortedMap;

      protected UnmodifiableSortedMap(Long2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.long2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, V>> entrySet() {
         return this.long2ObjectEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (LongSortedSet)this.keys;
      }

      public Long2ObjectSortedMap<V> subMap(long var1, long var3) {
         return new Long2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var3));
      }

      public Long2ObjectSortedMap<V> headMap(long var1) {
         return new Long2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Long2ObjectSortedMap<V> tailMap(long var1) {
         return new Long2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Long2ObjectSortedMap<V> subMap(Long var1, Long var2) {
         return new Long2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> headMap(Long var1) {
         return new Long2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> tailMap(Long var1) {
         return new Long2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap<V> extends Long2ObjectMaps.SynchronizedMap<V> implements Long2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Long2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Long2ObjectSortedMap<V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Long2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public LongComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.long2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, V>> entrySet() {
         return this.long2ObjectEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2ObjectSortedMap<V> subMap(long var1, long var3) {
         return new Long2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var3), this.sync);
      }

      public Long2ObjectSortedMap<V> headMap(long var1) {
         return new Long2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Long2ObjectSortedMap<V> tailMap(long var1) {
         return new Long2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Long2ObjectSortedMap<V> subMap(Long var1, Long var2) {
         return new Long2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> headMap(Long var1) {
         return new Long2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> tailMap(Long var1) {
         return new Long2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton<V> extends Long2ObjectMaps.Singleton<V> implements Long2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongComparator comparator;

      protected Singleton(long var1, V var3, LongComparator var4) {
         super(var1, var3);
         this.comparator = var4;
      }

      protected Singleton(long var1, V var3) {
         this(var1, var3, (LongComparator)null);
      }

      final int compare(long var1, long var3) {
         return this.comparator == null ? Long.compare(var1, var3) : this.comparator.compare(var1, var3);
      }

      public LongComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractLong2ObjectMap.BasicEntry(this.key, this.value), Long2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, V>> entrySet() {
         return this.long2ObjectEntrySet();
      }

      public LongSortedSet keySet() {
         if (this.keys == null) {
            this.keys = LongSortedSets.singleton(this.key, this.comparator);
         }

         return (LongSortedSet)this.keys;
      }

      public Long2ObjectSortedMap<V> subMap(long var1, long var3) {
         return (Long2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var3) < 0 ? this : Long2ObjectSortedMaps.EMPTY_MAP);
      }

      public Long2ObjectSortedMap<V> headMap(long var1) {
         return (Long2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Long2ObjectSortedMaps.EMPTY_MAP);
      }

      public Long2ObjectSortedMap<V> tailMap(long var1) {
         return (Long2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Long2ObjectSortedMaps.EMPTY_MAP);
      }

      public long firstLongKey() {
         return this.key;
      }

      public long lastLongKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> subMap(Long var1, Long var2) {
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

   public static class EmptySortedMap<V> extends Long2ObjectMaps.EmptyMap<V> implements Long2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public LongComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> long2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Long, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public LongSortedSet keySet() {
         return LongSortedSets.EMPTY_SET;
      }

      public Long2ObjectSortedMap<V> subMap(long var1, long var3) {
         return Long2ObjectSortedMaps.EMPTY_MAP;
      }

      public Long2ObjectSortedMap<V> headMap(long var1) {
         return Long2ObjectSortedMaps.EMPTY_MAP;
      }

      public Long2ObjectSortedMap<V> tailMap(long var1) {
         return Long2ObjectSortedMaps.EMPTY_MAP;
      }

      public long firstLongKey() {
         throw new NoSuchElementException();
      }

      public long lastLongKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> headMap(Long var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> tailMap(Long var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Long2ObjectSortedMap<V> subMap(Long var1, Long var2) {
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
