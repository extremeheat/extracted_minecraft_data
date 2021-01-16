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

public final class Short2CharSortedMaps {
   public static final Short2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Short2CharSortedMaps.EmptySortedMap();

   private Short2CharSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Short)var1.getKey(), (Short)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Short2CharMap.Entry> fastIterator(Short2CharSortedMap var0) {
      ObjectSortedSet var1 = var0.short2CharEntrySet();
      return var1 instanceof Short2CharSortedMap.FastSortedEntrySet ? ((Short2CharSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Short2CharMap.Entry> fastIterable(Short2CharSortedMap var0) {
      ObjectSortedSet var1 = var0.short2CharEntrySet();
      Object var2;
      if (var1 instanceof Short2CharSortedMap.FastSortedEntrySet) {
         Short2CharSortedMap.FastSortedEntrySet var10000 = (Short2CharSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Short2CharSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Short2CharSortedMap singleton(Short var0, Character var1) {
      return new Short2CharSortedMaps.Singleton(var0, var1);
   }

   public static Short2CharSortedMap singleton(Short var0, Character var1, ShortComparator var2) {
      return new Short2CharSortedMaps.Singleton(var0, var1, var2);
   }

   public static Short2CharSortedMap singleton(short var0, char var1) {
      return new Short2CharSortedMaps.Singleton(var0, var1);
   }

   public static Short2CharSortedMap singleton(short var0, char var1, ShortComparator var2) {
      return new Short2CharSortedMaps.Singleton(var0, var1, var2);
   }

   public static Short2CharSortedMap synchronize(Short2CharSortedMap var0) {
      return new Short2CharSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Short2CharSortedMap synchronize(Short2CharSortedMap var0, Object var1) {
      return new Short2CharSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Short2CharSortedMap unmodifiable(Short2CharSortedMap var0) {
      return new Short2CharSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Short2CharMaps.UnmodifiableMap implements Short2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharSortedMap sortedMap;

      protected UnmodifiableSortedMap(Short2CharSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2CharEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2CharSortedMap subMap(short var1, short var2) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Short2CharSortedMap headMap(short var1) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Short2CharSortedMap tailMap(short var1) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Short2CharSortedMap subMap(Short var1, Short var2) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap headMap(Short var1) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap tailMap(Short var1) {
         return new Short2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Short2CharMaps.SynchronizedMap implements Short2CharSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2CharSortedMap sortedMap;

      protected SynchronizedSortedMap(Short2CharSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Short2CharSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ShortComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2CharSortedMap subMap(short var1, short var2) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Short2CharSortedMap headMap(short var1) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Short2CharSortedMap tailMap(short var1) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Short2CharSortedMap subMap(Short var1, Short var2) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap headMap(Short var1) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap tailMap(Short var1) {
         return new Short2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Short2CharMaps.Singleton implements Short2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short var1, char var2, ShortComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(short var1, char var2) {
         this(var1, var2, (ShortComparator)null);
      }

      final int compare(short var1, short var2) {
         return this.comparator == null ? Short.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ShortComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractShort2CharMap.BasicEntry(this.key, this.value), Short2CharSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return this.short2CharEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2CharSortedMap subMap(short var1, short var2) {
         return (Short2CharSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Short2CharSortedMaps.EMPTY_MAP);
      }

      public Short2CharSortedMap headMap(short var1) {
         return (Short2CharSortedMap)(this.compare(this.key, var1) < 0 ? this : Short2CharSortedMaps.EMPTY_MAP);
      }

      public Short2CharSortedMap tailMap(short var1) {
         return (Short2CharSortedMap)(this.compare(var1, this.key) <= 0 ? this : Short2CharSortedMaps.EMPTY_MAP);
      }

      public short firstShortKey() {
         return this.key;
      }

      public short lastShortKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap headMap(Short var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap tailMap(Short var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap subMap(Short var1, Short var2) {
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

   public static class EmptySortedMap extends Short2CharMaps.EmptyMap implements Short2CharSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ShortComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Short2CharMap.Entry> short2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      public Short2CharSortedMap subMap(short var1, short var2) {
         return Short2CharSortedMaps.EMPTY_MAP;
      }

      public Short2CharSortedMap headMap(short var1) {
         return Short2CharSortedMaps.EMPTY_MAP;
      }

      public Short2CharSortedMap tailMap(short var1) {
         return Short2CharSortedMaps.EMPTY_MAP;
      }

      public short firstShortKey() {
         throw new NoSuchElementException();
      }

      public short lastShortKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap headMap(Short var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap tailMap(Short var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2CharSortedMap subMap(Short var1, Short var2) {
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
