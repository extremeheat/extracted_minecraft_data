package it.unimi.dsi.fastutil.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Object2ObjectSortedMaps {
   public static final Object2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Object2ObjectSortedMaps.EmptySortedMap();

   private Object2ObjectSortedMaps() {
      super();
   }

   public static <K> Comparator<? super Entry<K, ?>> entryComparator(Comparator<? super K> var0) {
      return (var1, var2) -> {
         return var0.compare(var1.getKey(), var2.getKey());
      };
   }

   public static <K, V> ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Object2ObjectSortedMap<K, V> var0) {
      ObjectSortedSet var1 = var0.object2ObjectEntrySet();
      return var1 instanceof Object2ObjectSortedMap.FastSortedEntrySet ? ((Object2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <K, V> ObjectBidirectionalIterable<Object2ObjectMap.Entry<K, V>> fastIterable(Object2ObjectSortedMap<K, V> var0) {
      ObjectSortedSet var1 = var0.object2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Object2ObjectSortedMap.FastSortedEntrySet) {
         Object2ObjectSortedMap.FastSortedEntrySet var10000 = (Object2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Object2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <K, V> Object2ObjectSortedMap<K, V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <K, V> Object2ObjectSortedMap<K, V> singleton(K var0, V var1) {
      return new Object2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <K, V> Object2ObjectSortedMap<K, V> singleton(K var0, V var1, Comparator<? super K> var2) {
      return new Object2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <K, V> Object2ObjectSortedMap<K, V> synchronize(Object2ObjectSortedMap<K, V> var0) {
      return new Object2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <K, V> Object2ObjectSortedMap<K, V> synchronize(Object2ObjectSortedMap<K, V> var0, Object var1) {
      return new Object2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <K, V> Object2ObjectSortedMap<K, V> unmodifiable(Object2ObjectSortedMap<K, V> var0) {
      return new Object2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<K, V> extends Object2ObjectMaps.UnmodifiableMap<K, V> implements Object2ObjectSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ObjectSortedMap<K, V> sortedMap;

      protected UnmodifiableSortedMap(Object2ObjectSortedMap<K, V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.object2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return new Object2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Object2ObjectSortedMap<K, V> headMap(K var1) {
         return new Object2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Object2ObjectSortedMap<K, V> tailMap(K var1) {
         return new Object2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public K firstKey() {
         return this.sortedMap.firstKey();
      }

      public K lastKey() {
         return this.sortedMap.lastKey();
      }
   }

   public static class SynchronizedSortedMap<K, V> extends Object2ObjectMaps.SynchronizedMap<K, V> implements Object2ObjectSortedMap<K, V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Object2ObjectSortedMap<K, V> sortedMap;

      protected SynchronizedSortedMap(Object2ObjectSortedMap<K, V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Object2ObjectSortedMap<K, V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public Comparator<? super K> comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.object2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return new Object2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Object2ObjectSortedMap<K, V> headMap(K var1) {
         return new Object2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Object2ObjectSortedMap<K, V> tailMap(K var1) {
         return new Object2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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

   public static class Singleton<K, V> extends Object2ObjectMaps.Singleton<K, V> implements Object2ObjectSortedMap<K, V>, Serializable, Cloneable {
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

      public ObjectSortedSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractObject2ObjectMap.BasicEntry(this.key, this.value), Object2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return this.object2ObjectEntrySet();
      }

      public ObjectSortedSet<K> keySet() {
         if (this.keys == null) {
            this.keys = ObjectSortedSets.singleton(this.key, this.comparator);
         }

         return (ObjectSortedSet)this.keys;
      }

      public Object2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return (Object2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Object2ObjectSortedMaps.EMPTY_MAP);
      }

      public Object2ObjectSortedMap<K, V> headMap(K var1) {
         return (Object2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Object2ObjectSortedMaps.EMPTY_MAP);
      }

      public Object2ObjectSortedMap<K, V> tailMap(K var1) {
         return (Object2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Object2ObjectSortedMaps.EMPTY_MAP);
      }

      public K firstKey() {
         return this.key;
      }

      public K lastKey() {
         return this.key;
      }
   }

   public static class EmptySortedMap<K, V> extends Object2ObjectMaps.EmptyMap<K, V> implements Object2ObjectSortedMap<K, V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public Comparator<? super K> comparator() {
         return null;
      }

      public ObjectSortedSet<Object2ObjectMap.Entry<K, V>> object2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<Entry<K, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ObjectSortedSet<K> keySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public Object2ObjectSortedMap<K, V> subMap(K var1, K var2) {
         return Object2ObjectSortedMaps.EMPTY_MAP;
      }

      public Object2ObjectSortedMap<K, V> headMap(K var1) {
         return Object2ObjectSortedMaps.EMPTY_MAP;
      }

      public Object2ObjectSortedMap<K, V> tailMap(K var1) {
         return Object2ObjectSortedMaps.EMPTY_MAP;
      }

      public K firstKey() {
         throw new NoSuchElementException();
      }

      public K lastKey() {
         throw new NoSuchElementException();
      }
   }
}
