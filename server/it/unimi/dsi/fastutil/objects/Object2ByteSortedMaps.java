package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Object2ByteSortedMaps {
   public static final Object2ByteSortedMaps.EmptySortedMap EMPTY_MAP = new Object2ByteSortedMaps.EmptySortedMap();

   private Object2ByteSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K> ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> fastIterator(Object2ByteSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.object2ByteEntrySet();
      return var1 instanceof Object2ByteSortedMap.FastSortedEntrySet ? ((Object2ByteSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K> ObjectBidirectionalIterable<Object2ByteMap.Entry<K>> fastIterable(Object2ByteSortedMap<K> var0) {
      ObjectSortedSet var1 = var0.object2ByteEntrySet();
      Object var2;
      if (var1 instanceof Object2ByteSortedMap.FastSortedEntrySet) {
         Object2ByteSortedMap.FastSortedEntrySet var10000 = (Object2ByteSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Object2ByteSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K> Object2ByteSortedMap<K> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K> Object2ByteSortedMap<K> singleton(K var0, Byte var1) {
      return new Object2ByteSortedMaps.Singleton(var0, var1);
   }

   public static <K> Object2ByteSortedMap<K> singleton(K var0, Byte var1, Comparator<? super K> var2) {
      return new Object2ByteSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Object2ByteSortedMap<K> singleton(K var0, byte var1) {
      return new Object2ByteSortedMaps.Singleton(var0, var1);
   }

   public static <K> Object2ByteSortedMap<K> singleton(K var0, byte var1, Comparator<? super K> var2) {
      return new Object2ByteSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K> Object2ByteSortedMap<K> synchronize(Object2ByteSortedMap<K> var0) {
      return new Object2ByteSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K> Object2ByteSortedMap<K> synchronize(Object2ByteSortedMap<K> var0, Object var1) {
      return new Object2ByteSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K> Object2ByteSortedMap<K> unmodifiable(Object2ByteSortedMap<K> var0) {
      return new Object2ByteSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K> extends Object2ByteMaps.UnmodifiableMap<K> implements Object2ByteSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteSortedMap<K> sortedMap;

      protected UnmodifiableSortedMap(Object2ByteSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2ByteEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2ByteSortedMap<K> subMap(K var1, K var2) {
         return new Object2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Object2ByteSortedMap<K> headMap(K var1) {
         return new Object2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Object2ByteSortedMap<K> tailMap(K var1) {
         return new Object2ByteSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K> extends Object2ByteMaps.SynchronizedMap<K> implements Object2ByteSortedMap<K>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ByteSortedMap<K> sortedMap;

      protected SynchronizedSortedMap(Object2ByteSortedMap<K> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Object2ByteSortedMap<K> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2ByteEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2ByteSortedMap<K> subMap(K var1, K var2) {
         return new Object2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Object2ByteSortedMap<K> headMap(K var1) {
         return new Object2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Object2ByteSortedMap<K> tailMap(K var1) {
         return new Object2ByteSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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

   public static class Singleton<K> extends Object2ByteMaps.Singleton<K> implements Object2ByteSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Comparator<? super K> comparator;

      protected Singleton(K var1, byte var2, Comparator<? super K> var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(K var1, byte var2) {
         this(var1, var2, (Comparator)null);
      }

      final int compare(K var1, K var2) {
         return this.comparator == null ? ((Comparable)var1).compareTo(var2) : this.comparator.compare(var1, var2);
      }

      public Comparator<? super K> comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractObject2ByteMap.BasicEntry(this.key, this.value), Object2ByteSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return this.object2ByteEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2ByteSortedMap<K> subMap(K var1, K var2) {
         return (Object2ByteSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Object2ByteSortedMaps.EMPTY_MAP);
      }

      public Object2ByteSortedMap<K> headMap(K var1) {
         return (Object2ByteSortedMap)(this.compare(this.key, var1) < 0 ? this : Object2ByteSortedMaps.EMPTY_MAP);
      }

      public Object2ByteSortedMap<K> tailMap(K var1) {
         return (Object2ByteSortedMap)(this.compare(var1, this.key) <= 0 ? this : Object2ByteSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K> extends Object2ByteMaps.EmptyMap<K> implements Object2ByteSortedMap<K>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Object2ByteMap.Entry<K>> object2ByteEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<K, Byte>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public Object2ByteSortedMap<K> subMap(K var1, K var2) {
         return Object2ByteSortedMaps.EMPTY_MAP;
      }

      public Object2ByteSortedMap<K> headMap(K var1) {
         return Object2ByteSortedMaps.EMPTY_MAP;
      }

      public Object2ByteSortedMap<K> tailMap(K var1) {
         return Object2ByteSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
