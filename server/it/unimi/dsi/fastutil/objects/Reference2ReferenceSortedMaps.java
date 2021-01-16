package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Reference2ReferenceSortedMaps {
   public static final Reference2ReferenceSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2ReferenceSortedMaps.EmptySortedMap();

   private Reference2ReferenceSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K, V> ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator(Reference2ReferenceSortedMap<K, V> var0) {
      ObjectSortedSet var1 = var0.reference2ReferenceEntrySet();
      return var1 instanceof Reference2ReferenceSortedMap.FastSortedEntrySet ? ((Reference2ReferenceSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K, V> ObjectBidirectionalIterable<Reference2ReferenceMap.Entry<K, V>> fastIterable(Reference2ReferenceSortedMap<K, V> var0) {
      ObjectSortedSet var1 = var0.reference2ReferenceEntrySet();
      Object var2;
      if (var1 instanceof Reference2ReferenceSortedMap.FastSortedEntrySet) {
         Reference2ReferenceSortedMap.FastSortedEntrySet var10000 = (Reference2ReferenceSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Reference2ReferenceSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K, V> Reference2ReferenceSortedMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Reference2ReferenceSortedMap<K, V> singleton(K var0, V var1) {
      return new Reference2ReferenceSortedMaps.Singleton(var0, var1);
   }

   public static <K, V> Reference2ReferenceSortedMap<K, V> singleton(K var0, V var1, Comparator<? super K> var2) {
      return new Reference2ReferenceSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K, V> Reference2ReferenceSortedMap<K, V> synchronize(Reference2ReferenceSortedMap<K, V> var0) {
      return new Reference2ReferenceSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K, V> Reference2ReferenceSortedMap<K, V> synchronize(Reference2ReferenceSortedMap<K, V> var0, Object var1) {
      return new Reference2ReferenceSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K, V> Reference2ReferenceSortedMap<K, V> unmodifiable(Reference2ReferenceSortedMap<K, V> var0) {
      return new Reference2ReferenceSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K, V> extends Reference2ReferenceMaps.UnmodifiableMap<K, V> implements Reference2ReferenceSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ReferenceSortedMap<K, V> sortedMap;

      protected UnmodifiableSortedMap(Reference2ReferenceSortedMap<K, V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2ReferenceEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ReferenceEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2ReferenceSortedMap<K, V> subMap(K var1, K var2) {
         return new Reference2ReferenceSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Reference2ReferenceSortedMap<K, V> headMap(K var1) {
         return new Reference2ReferenceSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Reference2ReferenceSortedMap<K, V> tailMap(K var1) {
         return new Reference2ReferenceSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K, V> extends Reference2ReferenceMaps.SynchronizedMap<K, V> implements Reference2ReferenceSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ReferenceSortedMap<K, V> sortedMap;

      protected SynchronizedSortedMap(Reference2ReferenceSortedMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Reference2ReferenceSortedMap<K, V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ReferenceEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ReferenceEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2ReferenceSortedMap<K, V> subMap(K var1, K var2) {
         return new Reference2ReferenceSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Reference2ReferenceSortedMap<K, V> headMap(K var1) {
         return new Reference2ReferenceSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Reference2ReferenceSortedMap<K, V> tailMap(K var1) {
         return new Reference2ReferenceSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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

   public static class Singleton<K, V> extends Reference2ReferenceMaps.Singleton<K, V> implements Reference2ReferenceSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K var1, V var2, Comparator<? super K> var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(K var1, V var2) {
         this(var1, var2, (Comparator)null);
      }

      final int compare(K var1, K var2) {
         return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractReference2ReferenceMap.BasicEntry(this.key, this.value), Reference2ReferenceSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ReferenceEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2ReferenceSortedMap<K, V> subMap(K var1, K var2) {
         return (Reference2ReferenceSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Reference2ReferenceSortedMaps.EMPTY_MAP);
      }

      public Reference2ReferenceSortedMap<K, V> headMap(K var1) {
         return (Reference2ReferenceSortedMap)(this.compare(this.key, var1) < 0 ? this : Reference2ReferenceSortedMaps.EMPTY_MAP);
      }

      public Reference2ReferenceSortedMap<K, V> tailMap(K var1) {
         return (Reference2ReferenceSortedMap)(this.compare(var1, this.key) <= 0 ? this : Reference2ReferenceSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K, V> extends Reference2ReferenceMaps.EmptyMap<K, V> implements Reference2ReferenceSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2ReferenceMap.Entry<K, V>> reference2ReferenceEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public Reference2ReferenceSortedMap<K, V> subMap(K var1, K var2) {
         return Reference2ReferenceSortedMaps.EMPTY_MAP;
      }

      public Reference2ReferenceSortedMap<K, V> headMap(K var1) {
         return Reference2ReferenceSortedMaps.EMPTY_MAP;
      }

      public Reference2ReferenceSortedMap<K, V> tailMap(K var1) {
         return Reference2ReferenceSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
