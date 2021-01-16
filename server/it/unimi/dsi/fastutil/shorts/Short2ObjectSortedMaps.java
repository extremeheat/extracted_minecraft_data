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

public final class Short2ObjectSortedMaps {
   public static final Short2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Short2ObjectSortedMaps.EmptySortedMap();

   private Short2ObjectSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Short, ?>> entryComparator(ShortComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Short)var1.getKey(), (Short)var2.getKey());
      };
   }

   public static <V> ObjectBidirectionalIterator<Short2ObjectMap.Entry<V>> fastIterator(Short2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.short2ObjectEntrySet();
      return var1 instanceof Short2ObjectSortedMap.FastSortedEntrySet ? ((Short2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Short2ObjectMap.Entry<V>> fastIterable(Short2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.short2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Short2ObjectSortedMap.FastSortedEntrySet) {
         Short2ObjectSortedMap.FastSortedEntrySet var10000 = (Short2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Short2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <V> Short2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Short2ObjectSortedMap<V> singleton(Short var0, V var1) {
      return new Short2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Short2ObjectSortedMap<V> singleton(Short var0, V var1, ShortComparator var2) {
      return new Short2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Short2ObjectSortedMap<V> singleton(short var0, V var1) {
      return new Short2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Short2ObjectSortedMap<V> singleton(short var0, V var1, ShortComparator var2) {
      return new Short2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Short2ObjectSortedMap<V> synchronize(Short2ObjectSortedMap<V> var0) {
      return new Short2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <V> Short2ObjectSortedMap<V> synchronize(Short2ObjectSortedMap<V> var0, Object var1) {
      return new Short2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <V> Short2ObjectSortedMap<V> unmodifiable(Short2ObjectSortedMap<V> var0) {
      return new Short2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<V> extends Short2ObjectMaps.UnmodifiableMap<V> implements Short2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ObjectSortedMap<V> sortedMap;

      protected UnmodifiableSortedMap(Short2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ShortComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.short2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ObjectEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2ObjectSortedMap<V> subMap(short var1, short var2) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Short2ObjectSortedMap<V> headMap(short var1) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Short2ObjectSortedMap<V> tailMap(short var1) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Short2ObjectSortedMap<V> subMap(Short var1, Short var2) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> headMap(Short var1) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> tailMap(Short var1) {
         return new Short2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap<V> extends Short2ObjectMaps.SynchronizedMap<V> implements Short2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Short2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Short2ObjectSortedMap<V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Short2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ShortComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.short2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ObjectEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2ObjectSortedMap<V> subMap(short var1, short var2) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Short2ObjectSortedMap<V> headMap(short var1) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Short2ObjectSortedMap<V> tailMap(short var1) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Short2ObjectSortedMap<V> subMap(Short var1, Short var2) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> headMap(Short var1) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> tailMap(Short var1) {
         return new Short2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton<V> extends Short2ObjectMaps.Singleton<V> implements Short2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortComparator comparator;

      protected Singleton(short var1, V var2, ShortComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(short var1, V var2) {
         this(var1, var2, (ShortComparator)null);
      }

      final int compare(short var1, short var2) {
         return this.comparator == null ? Short.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ShortComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractShort2ObjectMap.BasicEntry(this.key, this.value), Short2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return this.short2ObjectEntrySet();
      }

      public ShortSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ShortSortedSets.singleton(this.key, this.comparator);
         }

         return (ShortSortedSet)this.keys;
      }

      public Short2ObjectSortedMap<V> subMap(short var1, short var2) {
         return (Short2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Short2ObjectSortedMaps.EMPTY_MAP);
      }

      public Short2ObjectSortedMap<V> headMap(short var1) {
         return (Short2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Short2ObjectSortedMaps.EMPTY_MAP);
      }

      public Short2ObjectSortedMap<V> tailMap(short var1) {
         return (Short2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Short2ObjectSortedMaps.EMPTY_MAP);
      }

      public short firstShortKey() {
         return this.key;
      }

      public short lastShortKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> headMap(Short var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> tailMap(Short var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> subMap(Short var1, Short var2) {
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

   public static class EmptySortedMap<V> extends Short2ObjectMaps.EmptyMap<V> implements Short2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ShortComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Short2ObjectMap.Entry<V>> short2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Short, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ShortSortedSet keySet() {
         return ShortSortedSets.EMPTY_SET;
      }

      public Short2ObjectSortedMap<V> subMap(short var1, short var2) {
         return Short2ObjectSortedMaps.EMPTY_MAP;
      }

      public Short2ObjectSortedMap<V> headMap(short var1) {
         return Short2ObjectSortedMaps.EMPTY_MAP;
      }

      public Short2ObjectSortedMap<V> tailMap(short var1) {
         return Short2ObjectSortedMaps.EMPTY_MAP;
      }

      public short firstShortKey() {
         throw new NoSuchElementException();
      }

      public short lastShortKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> headMap(Short var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> tailMap(Short var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Short2ObjectSortedMap<V> subMap(Short var1, Short var2) {
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
