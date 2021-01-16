package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Reference2CharSortedMaps {
   public static final Reference2CharSortedMaps.EmptySortedMap EMPTY_MAP = new Reference2CharSortedMaps.EmptySortedMap();

   private Reference2CharSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K> ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.reference2CharEntrySet();
      return var1 instanceof Reference2CharSortedMap.FastSortedEntrySet ? ((Reference2CharSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Reference2CharMap.Entry<K>> fastIterable(Reference2CharSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.reference2CharEntrySet();
      Object var2;
      if (var1 instanceof Reference2CharSortedMap.FastSortedEntrySet) {
         Reference2CharSortedMap.FastSortedEntrySet var10000 = (Reference2CharSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Reference2CharSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K> Reference2CharSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Reference2CharSortedMap<K> singleton(K var0, Character var1) {
      return new Reference2CharSortedMaps.Singleton(var0, var1);
   }

   public static <K> Reference2CharSortedMap<K> singleton(K var0, Character var1, Comparator<? super K> var2) {
      return new Reference2CharSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Reference2CharSortedMap<K> singleton(K var0, char var1) {
      return new Reference2CharSortedMaps.Singleton(var0, var1);
   }

   public static <K> Reference2CharSortedMap<K> singleton(K var0, char var1, Comparator<? super K> var2) {
      return new Reference2CharSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Reference2CharSortedMap<K> synchronize(Reference2CharSortedMap<K> var0) {
      return new Reference2CharSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K> Reference2CharSortedMap<K> synchronize(Reference2CharSortedMap<K> var0, Object var1) {
      return new Reference2CharSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K> Reference2CharSortedMap<K> unmodifiable(Reference2CharSortedMap<K> var0) {
      return new Reference2CharSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K> extends Reference2CharMaps.UnmodifiableMap<K> implements Reference2CharSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2CharSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Reference2CharSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.reference2CharEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return this.reference2CharEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2CharSortedMap<K> subMap(K var1, K var2) {
         return new Reference2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Reference2CharSortedMap<K> headMap(K var1) {
         return new Reference2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Reference2CharSortedMap<K> tailMap(K var1) {
         return new Reference2CharSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K> extends Reference2CharMaps.SynchronizedMap<K> implements Reference2CharSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Reference2CharSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Reference2CharSortedMap<K> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Reference2CharSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.reference2CharEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return this.reference2CharEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2CharSortedMap<K> subMap(K var1, K var2) {
         return new Reference2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Reference2CharSortedMap<K> headMap(K var1) {
         return new Reference2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Reference2CharSortedMap<K> tailMap(K var1) {
         return new Reference2CharSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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

   public static class Singleton<K> extends Reference2CharMaps.Singleton<K> implements Reference2CharSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K var1, char var2, Comparator<? super K> var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(K var1, char var2) {
         this(var1, var2, (Comparator)null);
      }

      final int compare(K var1, K var2) {
         return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractReference2CharMap.BasicEntry(this.key, this.value), Reference2CharSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return this.reference2CharEntrySet();
      }

      public ReferenceSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ReferenceSortedSets.singleton(this.key, this.comparator);
         }

         return (ReferenceSortedSet)this.keys;
      }

      public Reference2CharSortedMap<K> subMap(K var1, K var2) {
         return (Reference2CharSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Reference2CharSortedMaps.EMPTY_MAP);
      }

      public Reference2CharSortedMap<K> headMap(K var1) {
         return (Reference2CharSortedMap)(this.compare(this.key, var1) < 0 ? this : Reference2CharSortedMaps.EMPTY_MAP);
      }

      public Reference2CharSortedMap<K> tailMap(K var1) {
         return (Reference2CharSortedMap)(this.compare(var1, this.key) <= 0 ? this : Reference2CharSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K> extends Reference2CharMaps.EmptyMap<K> implements Reference2CharSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Character>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ReferenceSortedSet<K> keySet() {
         return ReferenceSortedSets.EMPTY_SET;
      }

      public Reference2CharSortedMap<K> subMap(K var1, K var2) {
         return Reference2CharSortedMaps.EMPTY_MAP;
      }

      public Reference2CharSortedMap<K> headMap(K var1) {
         return Reference2CharSortedMaps.EMPTY_MAP;
      }

      public Reference2CharSortedMap<K> tailMap(K var1) {
         return Reference2CharSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
