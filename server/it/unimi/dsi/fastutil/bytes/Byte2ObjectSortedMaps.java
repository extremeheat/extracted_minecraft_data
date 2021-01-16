package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;

public final class Byte2ObjectSortedMaps {
   public static final Byte2ObjectSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2ObjectSortedMaps.EmptySortedMap();

   private Byte2ObjectSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Byte)var1.getKey(), (Byte)var2.getKey());
      };
   }

   public static <V> ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> fastIterator(Byte2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.byte2ObjectEntrySet();
      return var1 instanceof Byte2ObjectSortedMap.FastSortedEntrySet ? ((Byte2ObjectSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static <V> ObjectBidirectionalIterable<Byte2ObjectMap.Entry<V>> fastIterable(Byte2ObjectSortedMap<V> var0) {
      ObjectSortedSet var1 = var0.byte2ObjectEntrySet();
      Object var2;
      if (var1 instanceof Byte2ObjectSortedMap.FastSortedEntrySet) {
         Byte2ObjectSortedMap.FastSortedEntrySet var10000 = (Byte2ObjectSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Byte2ObjectSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static <V> Byte2ObjectSortedMap<V> emptyMap() {
      return EMPTY_MAP;
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(Byte var0, V var1) {
      return new Byte2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(Byte var0, V var1, ByteComparator var2) {
      return new Byte2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(byte var0, V var1) {
      return new Byte2ObjectSortedMaps.Singleton(var0, var1);
   }

   public static <V> Byte2ObjectSortedMap<V> singleton(byte var0, V var1, ByteComparator var2) {
      return new Byte2ObjectSortedMaps.Singleton(var0, var1, var2);
   }

   public static <V> Byte2ObjectSortedMap<V> synchronize(Byte2ObjectSortedMap<V> var0) {
      return new Byte2ObjectSortedMaps.SynchronizedSortedMap(var0);
   }

   public static <V> Byte2ObjectSortedMap<V> synchronize(Byte2ObjectSortedMap<V> var0, Object var1) {
      return new Byte2ObjectSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static <V> Byte2ObjectSortedMap<V> unmodifiable(Byte2ObjectSortedMap<V> var0) {
      return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap<V> extends Byte2ObjectMaps.UnmodifiableMap<V> implements Byte2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectSortedMap<V> sortedMap;

      protected UnmodifiableSortedMap(Byte2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2ObjectEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2ObjectSortedMap<V> subMap(byte var1, byte var2) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Byte2ObjectSortedMap<V> headMap(byte var1) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Byte2ObjectSortedMap<V> tailMap(byte var1) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }

      public byte firstByteKey() {
         return this.sortedMap.firstByteKey();
      }

      public byte lastByteKey() {
         return this.sortedMap.lastByteKey();
      }

      /** @deprecated */
      @Deprecated
      public Byte firstKey() {
         return this.sortedMap.firstKey();
      }

      /** @deprecated */
      @Deprecated
      public Byte lastKey() {
         return this.sortedMap.lastKey();
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> subMap(Byte var1, Byte var2) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> headMap(Byte var1) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> tailMap(Byte var1) {
         return new Byte2ObjectSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap<V> extends Byte2ObjectMaps.SynchronizedMap<V> implements Byte2ObjectSortedMap<V>, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2ObjectSortedMap<V> sortedMap;

      protected SynchronizedSortedMap(Byte2ObjectSortedMap<V> var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Byte2ObjectSortedMap<V> var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2ObjectEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2ObjectSortedMap<V> subMap(byte var1, byte var2) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Byte2ObjectSortedMap<V> headMap(byte var1) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Byte2ObjectSortedMap<V> tailMap(byte var1) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }

      public byte firstByteKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstByteKey();
         }
      }

      public byte lastByteKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastByteKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte firstKey() {
         synchronized(this.sync) {
            return this.sortedMap.firstKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte lastKey() {
         synchronized(this.sync) {
            return this.sortedMap.lastKey();
         }
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> subMap(Byte var1, Byte var2) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> headMap(Byte var1) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> tailMap(Byte var1) {
         return new Byte2ObjectSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton<V> extends Byte2ObjectMaps.Singleton<V> implements Byte2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte var1, V var2, ByteComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(byte var1, V var2) {
         this(var1, var2, (ByteComparator)null);
      }

      final int compare(byte var1, byte var2) {
         return this.comparator == null ? Byte.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ByteComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractByte2ObjectMap.BasicEntry(this.key, this.value), Byte2ObjectSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return this.byte2ObjectEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2ObjectSortedMap<V> subMap(byte var1, byte var2) {
         return (Byte2ObjectSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Byte2ObjectSortedMaps.EMPTY_MAP);
      }

      public Byte2ObjectSortedMap<V> headMap(byte var1) {
         return (Byte2ObjectSortedMap)(this.compare(this.key, var1) < 0 ? this : Byte2ObjectSortedMaps.EMPTY_MAP);
      }

      public Byte2ObjectSortedMap<V> tailMap(byte var1) {
         return (Byte2ObjectSortedMap)(this.compare(var1, this.key) <= 0 ? this : Byte2ObjectSortedMaps.EMPTY_MAP);
      }

      public byte firstByteKey() {
         return this.key;
      }

      public byte lastByteKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> subMap(Byte var1, Byte var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Byte firstKey() {
         return this.firstByteKey();
      }

      /** @deprecated */
      @Deprecated
      public Byte lastKey() {
         return this.lastByteKey();
      }
   }

   public static class EmptySortedMap<V> extends Byte2ObjectMaps.EmptyMap<V> implements Byte2ObjectSortedMap<V>, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ByteComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2ObjectMap.Entry<V>> byte2ObjectEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, V>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      public Byte2ObjectSortedMap<V> subMap(byte var1, byte var2) {
         return Byte2ObjectSortedMaps.EMPTY_MAP;
      }

      public Byte2ObjectSortedMap<V> headMap(byte var1) {
         return Byte2ObjectSortedMaps.EMPTY_MAP;
      }

      public Byte2ObjectSortedMap<V> tailMap(byte var1) {
         return Byte2ObjectSortedMaps.EMPTY_MAP;
      }

      public byte firstByteKey() {
         throw new NoSuchElementException();
      }

      public byte lastByteKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2ObjectSortedMap<V> subMap(Byte var1, Byte var2) {
         return this.subMap(var1, var2);
      }

      /** @deprecated */
      @Deprecated
      public Byte firstKey() {
         return this.firstByteKey();
      }

      /** @deprecated */
      @Deprecated
      public Byte lastKey() {
         return this.lastByteKey();
      }
   }
}
