package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Reference2IntSortedMaps {
   public static final Reference2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2IntSortedMaps.EmptySortedMap();

   private Reference2IntSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K> ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> fastIterator(Reference2IntSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.reference2IntEntrySet();
      return var1 instanceof Reference2IntSortedMap.FastSortedEntrySet ? ((Reference2IntSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Reference2IntMap.Entry<K>> fastIterable(Reference2IntSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.reference2IntEntrySet();
      Object var2;
      if (var1 instanceof Reference2IntSortedMap.FastSortedEntrySet) {
         Reference2IntSortedMap.FastSortedEntrySet var10000 = (Reference2IntSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Reference2IntSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K> Reference2IntSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2IntSortedMap<K> singleton(K var0, Integer var1) {
      return new Reference2IntSortedMaps.Singleton(var0, var1);
   }

   public static <K> Reference2IntSortedMap<K> singleton(K var0, Integer var1, Comparator<? super K> var2) {
      return new Reference2IntSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Reference2IntSortedMap<K> singleton(K var0, int var1) {
      return new Reference2IntSortedMaps.Singleton(var0, var1);
   }

   public static <K> Reference2IntSortedMap<K> singleton(K var0, int var1, Comparator<? super K> var2) {
      return new Reference2IntSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Reference2IntSortedMap<K> synchronize(Reference2IntSortedMap<K> var0) {
      return new Reference2IntSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K> Reference2IntSortedMap<K> synchronize(Reference2IntSortedMap<K> var0, Object var1) {
      return new Reference2IntSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K> Reference2IntSortedMap<K> unmodifiable(Reference2IntSortedMap<K> var0) {
      return new Reference2IntSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K> extends Reference2IntMaps.UnmodifiableMap<K> implements Reference2IntSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2IntSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Reference2IntSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2IntEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Integer>> entrySet() {
         return this.reference2IntEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2IntSortedMap<K> subMap(K var1, K var2) {
         return new Reference2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Reference2IntSortedMap<K> headMap(K var1) {
         return new Reference2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Reference2IntSortedMap<K> tailMap(K var1) {
         return new Reference2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K> extends Reference2IntMaps.SynchronizedMap<K> implements Reference2IntSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2IntSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Reference2IntSortedMap<K> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Reference2IntSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Integer>> entrySet() {
         return this.reference2IntEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2IntSortedMap<K> subMap(K var1, K var2) {
         return new Reference2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Reference2IntSortedMap<K> headMap(K var1) {
         return new Reference2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Reference2IntSortedMap<K> tailMap(K var1) {
         return new Reference2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public K firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      public K lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }
   }

   public static class Singleton<K> extends Reference2IntMaps.Singleton<K> implements Reference2IntSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K var1, int var2, Comparator<? super K> var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(K var1, int var2) {
         this(var1, var2, (Comparator)null);
      }

      final int compare(K var1, K var2) {
         return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractReference2IntMap.BasicEntry(this.key, this.value), Reference2IntSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Integer>> entrySet() {
         return this.reference2IntEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2IntSortedMap<K> subMap(K var1, K var2) {
         return (Reference2IntSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Reference2IntSortedMaps.EMPTY_MAP);
      }

      public Reference2IntSortedMap<K> headMap(K var1) {
         return (Reference2IntSortedMap)(this.compare(this.key, var1) < 0 ? this : Reference2IntSortedMaps.EMPTY_MAP);
      }

      public Reference2IntSortedMap<K> tailMap(K var1) {
         return (Reference2IntSortedMap)(this.compare(var1, this.key) <= 0 ? this : Reference2IntSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K> extends Reference2IntMaps.EmptyMap<K> implements Reference2IntSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2IntMap.Entry<K>> reference2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public Reference2IntSortedMap<K> subMap(K var1, K var2) {
         return Reference2IntSortedMaps.EMPTY_MAP;
      }

      public Reference2IntSortedMap<K> headMap(K var1) {
         return Reference2IntSortedMaps.EMPTY_MAP;
      }

      public Reference2IntSortedMap<K> tailMap(K var1) {
         return Reference2IntSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
