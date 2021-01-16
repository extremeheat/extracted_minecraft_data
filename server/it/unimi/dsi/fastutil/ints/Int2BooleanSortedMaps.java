package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Int2BooleanSortedMaps {
   public static final Int2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Int2BooleanSortedMaps.EmptySortedMap();

   private Int2BooleanSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Integer)var1.getKey(), (Integer)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Int2BooleanMap.Entry> fastIterator(Int2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.int2BooleanEntrySet();
      return var1 instanceof Int2BooleanSortedMap.FastSortedEntrySet ? ((Int2BooleanSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Int2BooleanMap.Entry> fastIterable(Int2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.int2BooleanEntrySet();
      Object var2;
      if (var1 instanceof Int2BooleanSortedMap.FastSortedEntrySet) {
         Int2BooleanSortedMap.FastSortedEntrySet var10000 = (Int2BooleanSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Int2BooleanSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Int2BooleanSortedMap singleton(Integer var0, Boolean var1) {
      return new Int2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Int2BooleanSortedMap singleton(Integer var0, Boolean var1, IntComparator var2) {
      return new Int2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Int2BooleanSortedMap singleton(int var0, boolean var1) {
      return new Int2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Int2BooleanSortedMap singleton(int var0, boolean var1, IntComparator var2) {
      return new Int2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Int2BooleanSortedMap synchronize(Int2BooleanSortedMap var0) {
      return new Int2BooleanSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Int2BooleanSortedMap synchronize(Int2BooleanSortedMap var0, Object var1) {
      return new Int2BooleanSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Int2BooleanSortedMap unmodifiable(Int2BooleanSortedMap var0) {
      return new Int2BooleanSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Int2BooleanMaps.UnmodifiableMap implements Int2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Int2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2BooleanEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      public Int2BooleanSortedMap subMap(int var1, int var2) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Int2BooleanSortedMap headMap(int var1) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Int2BooleanSortedMap tailMap(int var1) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public int firstIntKey() {
         return this.sortedMap.firstIntKey();
      }

      public int lastIntKey() {
         return this.sortedMap.lastIntKey();
      }

      /** @deprecated */
      @Deprecated
      public Integer firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Integer lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap subMap(Integer var1, Integer var2) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap headMap(Integer var1) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap tailMap(Integer var1) {
         return new Int2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Int2BooleanMaps.SynchronizedMap implements Int2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Int2BooleanSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Int2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public IntComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      public Int2BooleanSortedMap subMap(int var1, int var2) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Int2BooleanSortedMap headMap(int var1) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Int2BooleanSortedMap tailMap(int var1) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public int firstIntKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstIntKey();
         }
      }

      public int lastIntKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastIntKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Integer lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap subMap(Integer var1, Integer var2) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap headMap(Integer var1) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap tailMap(Integer var1) {
         return new Int2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Int2BooleanMaps.Singleton implements Int2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int var1, boolean var2, IntComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(int var1, boolean var2) {
         this(var1, var2, (IntComparator)null);
      }

      final int compare(int var1, int var2) {
         return this.comparator == null ? Integer.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public IntComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractInt2BooleanMap.BasicEntry(this.key, this.value), Int2BooleanSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return this.int2BooleanEntrySet();
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      public Int2BooleanSortedMap subMap(int var1, int var2) {
         return (Int2BooleanSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Int2BooleanSortedMaps.EMPTY_MAP);
      }

      public Int2BooleanSortedMap headMap(int var1) {
         return (Int2BooleanSortedMap)(this.compare(this.key, var1) < 0 ? this : Int2BooleanSortedMaps.EMPTY_MAP);
      }

      public Int2BooleanSortedMap tailMap(int var1) {
         return (Int2BooleanSortedMap)(this.compare(var1, this.key) <= 0 ? this : Int2BooleanSortedMaps.EMPTY_MAP);
      }

      public int firstIntKey() {
         return this.key;
      }

      public int lastIntKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap headMap(Integer var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap tailMap(Integer var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap subMap(Integer var1, Integer var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Integer firstKey() {
         return this.firstIntKey();
      }

      /** @deprecated */
      @Deprecated
      public Integer lastKey() {
         return this.lastIntKey();
      }
   }

   public static class EmptySortedMap extends Int2BooleanMaps.EmptyMap implements Int2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public IntComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Int2BooleanMap.Entry> int2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      public Int2BooleanSortedMap subMap(int var1, int var2) {
         return Int2BooleanSortedMaps.EMPTY_MAP;
      }

      public Int2BooleanSortedMap headMap(int var1) {
         return Int2BooleanSortedMaps.EMPTY_MAP;
      }

      public Int2BooleanSortedMap tailMap(int var1) {
         return Int2BooleanSortedMaps.EMPTY_MAP;
      }

      public int firstIntKey() {
         throw new NoSuchElementException();
      }

      public int lastIntKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap headMap(Integer var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap tailMap(Integer var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2BooleanSortedMap subMap(Integer var1, Integer var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Integer firstKey() {
         return this.firstIntKey();
      }

      /** @deprecated */
      @Deprecated
      public Integer lastKey() {
         return this.lastIntKey();
      }
   }
}
