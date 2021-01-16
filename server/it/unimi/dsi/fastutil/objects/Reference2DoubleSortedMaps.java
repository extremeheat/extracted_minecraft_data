package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Reference2DoubleSortedMaps {
   public static final Reference2DoubleSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2DoubleSortedMaps.EmptySortedMap();

   private Reference2DoubleSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K> ObjectBidirectionalIterator<Reference2DoubleMap.Entry<K>> fastIterator(Reference2DoubleSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.reference2DoubleEntrySet();
      return var1 instanceof Reference2DoubleSortedMap.FastSortedEntrySet ? ((Reference2DoubleSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Reference2DoubleMap.Entry<K>> fastIterable(Reference2DoubleSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.reference2DoubleEntrySet();
      Object var2;
      if (var1 instanceof Reference2DoubleSortedMap.FastSortedEntrySet) {
         Reference2DoubleSortedMap.FastSortedEntrySet var10000 = (Reference2DoubleSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Reference2DoubleSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K> Reference2DoubleSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2DoubleSortedMap<K> singleton(K var0, Double var1) {
      return new Reference2DoubleSortedMaps.Singleton(var0, var1);
   }

   public static <K> Reference2DoubleSortedMap<K> singleton(K var0, Double var1, Comparator<? super K> var2) {
      return new Reference2DoubleSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Reference2DoubleSortedMap<K> singleton(K var0, double var1) {
      return new Reference2DoubleSortedMaps.Singleton(var0, var1);
   }

   public static <K> Reference2DoubleSortedMap<K> singleton(K var0, double var1, Comparator<? super K> var3) {
      return new Reference2DoubleSortedMaps.Singleton(var0, var1, var3);
   }

   public static <K> Reference2DoubleSortedMap<K> synchronize(Reference2DoubleSortedMap<K> var0) {
      return new Reference2DoubleSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K> Reference2DoubleSortedMap<K> synchronize(Reference2DoubleSortedMap<K> var0, Object var1) {
      return new Reference2DoubleSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K> Reference2DoubleSortedMap<K> unmodifiable(Reference2DoubleSortedMap<K> var0) {
      return new Reference2DoubleSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K> extends Reference2DoubleMaps.UnmodifiableMap<K> implements Reference2DoubleSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Reference2DoubleSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2DoubleEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Double>> entrySet() {
         return this.reference2DoubleEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2DoubleSortedMap<K> subMap(K var1, K var2) {
         return new Reference2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Reference2DoubleSortedMap<K> headMap(K var1) {
         return new Reference2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Reference2DoubleSortedMap<K> tailMap(K var1) {
         return new Reference2DoubleSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K> extends Reference2DoubleMaps.SynchronizedMap<K> implements Reference2DoubleSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2DoubleSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Reference2DoubleSortedMap<K> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Reference2DoubleSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2DoubleEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Double>> entrySet() {
         return this.reference2DoubleEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2DoubleSortedMap<K> subMap(K var1, K var2) {
         return new Reference2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Reference2DoubleSortedMap<K> headMap(K var1) {
         return new Reference2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Reference2DoubleSortedMap<K> tailMap(K var1) {
         return new Reference2DoubleSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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

   public static class Singleton<K> extends Reference2DoubleMaps.Singleton<K> implements Reference2DoubleSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K var1, double var2, Comparator<? super K> var4) {
         super(var1, var2);
         this.comparator = var4;
      }

      protected Singleton(K var1, double var2) {
         this(var1, var2, (Comparator)null);
      }

      final int compare(K var1, K var2) {
         return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractReference2DoubleMap.BasicEntry(this.key, this.value), Reference2DoubleSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Double>> entrySet() {
         return this.reference2DoubleEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2DoubleSortedMap<K> subMap(K var1, K var2) {
         return (Reference2DoubleSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Reference2DoubleSortedMaps.EMPTY_MAP);
      }

      public Reference2DoubleSortedMap<K> headMap(K var1) {
         return (Reference2DoubleSortedMap)(this.compare(this.key, var1) < 0 ? this : Reference2DoubleSortedMaps.EMPTY_MAP);
      }

      public Reference2DoubleSortedMap<K> tailMap(K var1) {
         return (Reference2DoubleSortedMap)(this.compare(var1, this.key) <= 0 ? this : Reference2DoubleSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K> extends Reference2DoubleMaps.EmptyMap<K> implements Reference2DoubleSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2DoubleMap.Entry<K>> reference2DoubleEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Double>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public Reference2DoubleSortedMap<K> subMap(K var1, K var2) {
         return Reference2DoubleSortedMaps.EMPTY_MAP;
      }

      public Reference2DoubleSortedMap<K> headMap(K var1) {
         return Reference2DoubleSortedMaps.EMPTY_MAP;
      }

      public Reference2DoubleSortedMap<K> tailMap(K var1) {
         return Reference2DoubleSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
