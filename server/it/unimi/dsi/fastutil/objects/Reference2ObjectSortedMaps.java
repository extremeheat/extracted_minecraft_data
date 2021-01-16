package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Reference2ObjectSortedMaps {
   public static final Reference2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2ObjectSortedMaps.EmptySortedMap();

   private Reference2ObjectSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K, V> ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> fastIterator(Reference2ObjectSortedMap<K, V> var0) {
      ObjectSortedSet var1 = var0.reference2ObjectEntrySet();
      return var1 instanceof Reference2ObjectSortedMap.FastSortedEntrySet ? ((Reference2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K, V> ObjectBidirectionalIterable<Reference2ObjectMap.Entry<K, V>> fastIterable(Reference2ObjectSortedMap<K, V> var0) {
      ObjectSortedSet var1 = var0.reference2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Reference2ObjectSortedMap.FastSortedEntrySet) {
         Reference2ObjectSortedMap.FastSortedEntrySet var10000 = (Reference2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Reference2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> singleton(K var0, V var1) {
      return new Reference2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> singleton(K var0, V var1, Comparator<? super K> var2) {
      return new Reference2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> synchronize(Reference2ObjectSortedMap<K, V> var0) {
      return new Reference2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> synchronize(Reference2ObjectSortedMap<K, V> var0, Object var1) {
      return new Reference2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K, V> Reference2ObjectSortedMap<K, V> unmodifiable(Reference2ObjectSortedMap<K, V> var0) {
      return new Reference2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K, V> extends Reference2ObjectMaps.UnmodifiableMap<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ObjectSortedMap<K, V> sortedMap;

      protected UnmodifiableSortedMap(Reference2ObjectSortedMap<K, V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ObjectEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return new Reference2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Reference2ObjectSortedMap<K, V> headMap(K var1) {
         return new Reference2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Reference2ObjectSortedMap<K, V> tailMap(K var1) {
         return new Reference2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K, V> extends Reference2ObjectMaps.SynchronizedMap<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2ObjectSortedMap<K, V> sortedMap;

      protected SynchronizedSortedMap(Reference2ObjectSortedMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Reference2ObjectSortedMap<K, V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ObjectEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return new Reference2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Reference2ObjectSortedMap<K, V> headMap(K var1) {
         return new Reference2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Reference2ObjectSortedMap<K, V> tailMap(K var1) {
         return new Reference2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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

   public static class Singleton<K, V> extends Reference2ObjectMaps.Singleton<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable, Cloneable {
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

      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractReference2ObjectMap.BasicEntry(this.key, this.value), Reference2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.reference2ObjectEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return (Reference2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Reference2ObjectSortedMaps.EMPTY_MAP);
      }

      public Reference2ObjectSortedMap<K, V> headMap(K var1) {
         return (Reference2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Reference2ObjectSortedMaps.EMPTY_MAP);
      }

      public Reference2ObjectSortedMap<K, V> tailMap(K var1) {
         return (Reference2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Reference2ObjectSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K, V> extends Reference2ObjectMaps.EmptyMap<K, V> implements Reference2ObjectSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2ObjectMap.Entry<K, V>> reference2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public Reference2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return Reference2ObjectSortedMaps.EMPTY_MAP;
      }

      public Reference2ObjectSortedMap<K, V> headMap(K var1) {
         return Reference2ObjectSortedMaps.EMPTY_MAP;
      }

      public Reference2ObjectSortedMap<K, V> tailMap(K var1) {
         return Reference2ObjectSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
