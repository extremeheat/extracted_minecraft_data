package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Short2LongSortedMaps {
   public static final Short2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Short2LongSortedMaps.EmptySortedMap();

   private Short2LongSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Short)var1.getKey(), (Short)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Short2LongMap.Entry> fastIterator(Short2LongSortedMap var0) {
      ObjectSortedSet var1 = var0.short2LongEntrySet();
      return var1 instanceof Short2LongSortedMap.FastSortedEntrySet ? ((Short2LongSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Short2LongMap.Entry> fastIterable(Short2LongSortedMap var0) {
      ObjectSortedSet var1 = var0.short2LongEntrySet();
      Object var2;
      if (var1 instanceof Short2LongSortedMap.FastSortedEntrySet) {
         Short2LongSortedMap.FastSortedEntrySet var10000 = (Short2LongSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Short2LongSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Short2LongSortedMap singleton(Short var0, Long var1) {
      return new Short2LongSortedMaps.Singleton(var0, var1);
   }

   public static Short2LongSortedMap singleton(Short var0, Long var1, ShortComparator var2) {
      return new Short2LongSortedMaps.Singleton(var0, var1, var2);
   }

   public static Short2LongSortedMap singleton(short var0, long var1) {
      return new Short2LongSortedMaps.Singleton(var0, var1);
   }

   public static Short2LongSortedMap singleton(short var0, long var1, ShortComparator var3) {
      return new Short2LongSortedMaps.Singleton(var0, var1, var3);
   }

   public static Short2LongSortedMap synchronize(Short2LongSortedMap var0) {
      return new Short2LongSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Short2LongSortedMap synchronize(Short2LongSortedMap var0, Object var1) {
      return new Short2LongSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Short2LongSortedMap unmodifiable(Short2LongSortedMap var0) {
      return new Short2LongSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Short2LongMaps.UnmodifiableMap implements Short2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2LongSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2LongSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2LongEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return this.short2LongEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2LongSortedMap subMap(short var1, short var2) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Short2LongSortedMap headMap(short var1) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Short2LongSortedMap tailMap(short var1) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public short firstShortKey() {
         return this.sortedMap.firstShortKey();
      }

      public short lastShortKey() {
         return this.sortedMap.lastShortKey();
      }

      /** @deprecated */
      @Deprecated
      public Short firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Short lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap subMap(Short var1, Short var2) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap headMap(Short var1) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap tailMap(Short var1) {
         return new Short2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Short2LongMaps.SynchronizedMap implements Short2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2LongSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2LongSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Short2LongSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ShortComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return this.short2LongEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2LongSortedMap subMap(short var1, short var2) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Short2LongSortedMap headMap(short var1) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Short2LongSortedMap tailMap(short var1) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public short firstShortKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstShortKey();
         }
      }

      public short lastShortKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastShortKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap subMap(Short var1, Short var2) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap headMap(Short var1) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap tailMap(Short var1) {
         return new Short2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Short2LongMaps.Singleton implements Short2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short var1, long var2, ShortComparator var4) {
         super(var1, var2);
         this.comparator = var4;
      }

      protected Singleton(short var1, long var2) {
         this(var1, var2, (ShortComparator)null);
      }

      final int compare(short var1, short var2) {
         return this.comparator == null ? Short.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ShortComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractShort2LongMap.BasicEntry(this.key, this.value), Short2LongSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return this.short2LongEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2LongSortedMap subMap(short var1, short var2) {
         return (Short2LongSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Short2LongSortedMaps.EMPTY_MAP);
      }

      public Short2LongSortedMap headMap(short var1) {
         return (Short2LongSortedMap)(this.compare(this.key, var1) < 0 ? this : Short2LongSortedMaps.EMPTY_MAP);
      }

      public Short2LongSortedMap tailMap(short var1) {
         return (Short2LongSortedMap)(this.compare(var1, this.key) <= 0 ? this : Short2LongSortedMaps.EMPTY_MAP);
      }

      public short firstShortKey() {
         return this.key;
      }

      public short lastShortKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap headMap(Short var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap tailMap(Short var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap subMap(Short var1, Short var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Short firstKey() {
         return this.firstShortKey();
      }

      /** @deprecated */
      @Deprecated
      public Short lastKey() {
         return this.lastShortKey();
      }
   }

   public static class EmptySortedMap extends Short2LongMaps.EmptyMap implements Short2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ShortComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Short2LongMap.Entry> short2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      public Short2LongSortedMap subMap(short var1, short var2) {
         return Short2LongSortedMaps.EMPTY_MAP;
      }

      public Short2LongSortedMap headMap(short var1) {
         return Short2LongSortedMaps.EMPTY_MAP;
      }

      public Short2LongSortedMap tailMap(short var1) {
         return Short2LongSortedMaps.EMPTY_MAP;
      }

      public short firstShortKey() {
         throw new NoSuchElementException();
      }

      public short lastShortKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap headMap(Short var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap tailMap(Short var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2LongSortedMap subMap(Short var1, Short var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Short firstKey() {
         return this.firstShortKey();
      }

      /** @deprecated */
      @Deprecated
      public Short lastKey() {
         return this.lastShortKey();
      }
   }
}
