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

public final class Int2ObjectSortedMaps {
   public static final Int2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Int2ObjectSortedMaps.EmptySortedMap();

   private Int2ObjectSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Integer, ?>> entryComparator(IntComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Integer)var1.getKey(), (Integer)var2.getKey());
      };
   }

   public static <V> ObjectBidirectionalIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.int2ObjectEntrySet();
      return var1 instanceof Int2ObjectSortedMap.FastSortedEntrySet ? ((Int2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Int2ObjectMap.Entry<V>> fastIterable(Int2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.int2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Int2ObjectSortedMap.FastSortedEntrySet) {
         Int2ObjectSortedMap.FastSortedEntrySet var10000 = (Int2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Int2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <V> Int2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Int2ObjectSortedMap<V> singleton(Integer var0, V var1) {
      return new Int2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Int2ObjectSortedMap<V> singleton(Integer var0, V var1, IntComparator var2) {
      return new Int2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Int2ObjectSortedMap<V> singleton(int var0, V var1) {
      return new Int2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Int2ObjectSortedMap<V> singleton(int var0, V var1, IntComparator var2) {
      return new Int2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Int2ObjectSortedMap<V> synchronize(Int2ObjectSortedMap<V> var0) {
      return new Int2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <V> Int2ObjectSortedMap<V> synchronize(Int2ObjectSortedMap<V> var0, Object var1) {
      return new Int2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <V> Int2ObjectSortedMap<V> unmodifiable(Int2ObjectSortedMap<V> var0) {
      return new Int2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<V> extends Int2ObjectMaps.UnmodifiableMap<V> implements Int2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ObjectSortedMap<V> sortedMap;

      protected UnmodifiableSortedMap(Int2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public IntComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.int2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, V>> entrySet() {
         return this.int2ObjectEntrySet();
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (IntSortedSet)this.keys;
      }

      public Int2ObjectSortedMap<V> subMap(int var1, int var2) {
         return new Int2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Int2ObjectSortedMap<V> headMap(int var1) {
         return new Int2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Int2ObjectSortedMap<V> tailMap(int var1) {
         return new Int2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Int2ObjectSortedMap<V> subMap(Integer var1, Integer var2) {
         return new Int2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> headMap(Integer var1) {
         return new Int2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> tailMap(Integer var1) {
         return new Int2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap<V> extends Int2ObjectMaps.SynchronizedMap<V> implements Int2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Int2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Int2ObjectSortedMap<V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Int2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public IntComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.int2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, V>> entrySet() {
         return this.int2ObjectEntrySet();
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (IntSortedSet)this.keys;
      }

      public Int2ObjectSortedMap<V> subMap(int var1, int var2) {
         return new Int2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Int2ObjectSortedMap<V> headMap(int var1) {
         return new Int2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Int2ObjectSortedMap<V> tailMap(int var1) {
         return new Int2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Int2ObjectSortedMap<V> subMap(Integer var1, Integer var2) {
         return new Int2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> headMap(Integer var1) {
         return new Int2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> tailMap(Integer var1) {
         return new Int2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton<V> extends Int2ObjectMaps.Singleton<V> implements Int2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final IntComparator comparator;

      protected Singleton(int var1, V var2, IntComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(int var1, V var2) {
         this(var1, var2, (IntComparator)null);
      }

      final int compare(int var1, int var2) {
         return this.comparator == null ? Integer.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public IntComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractInt2ObjectMap.BasicEntry(this.key, this.value), Int2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, V>> entrySet() {
         return this.int2ObjectEntrySet();
      }

      public IntSortedSet keySet() {
         if (this.keys == null) {
            this.keys = IntSortedSets.singleton(this.key, this.comparator);
         }

         return (IntSortedSet)this.keys;
      }

      public Int2ObjectSortedMap<V> subMap(int var1, int var2) {
         return (Int2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Int2ObjectSortedMaps.EMPTY_MAP);
      }

      public Int2ObjectSortedMap<V> headMap(int var1) {
         return (Int2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Int2ObjectSortedMaps.EMPTY_MAP);
      }

      public Int2ObjectSortedMap<V> tailMap(int var1) {
         return (Int2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Int2ObjectSortedMaps.EMPTY_MAP);
      }

      public int firstIntKey() {
         return this.key;
      }

      public int lastIntKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> headMap(Integer var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> tailMap(Integer var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> subMap(Integer var1, Integer var2) {
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

   public static class EmptySortedMap<V> extends Int2ObjectMaps.EmptyMap<V> implements Int2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public IntComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Integer, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public IntSortedSet keySet() {
         return IntSortedSets.EMPTY_SET;
      }

      public Int2ObjectSortedMap<V> subMap(int var1, int var2) {
         return Int2ObjectSortedMaps.EMPTY_MAP;
      }

      public Int2ObjectSortedMap<V> headMap(int var1) {
         return Int2ObjectSortedMaps.EMPTY_MAP;
      }

      public Int2ObjectSortedMap<V> tailMap(int var1) {
         return Int2ObjectSortedMaps.EMPTY_MAP;
      }

      public int firstIntKey() {
         throw new NoSuchElementException();
      }

      public int lastIntKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> headMap(Integer var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> tailMap(Integer var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Int2ObjectSortedMap<V> subMap(Integer var1, Integer var2) {
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
