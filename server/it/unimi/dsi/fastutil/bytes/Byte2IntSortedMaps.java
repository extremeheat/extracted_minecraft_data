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

public final class Byte2IntSortedMaps {
   public static final Byte2IntSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2IntSortedMaps.EmptySortedMap();

   private Byte2IntSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Byte)var1.getKey(), (Byte)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Byte2IntMap.Entry> fastIterator(Byte2IntSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2IntEntrySet();
      return var1 instanceof Byte2IntSortedMap.FastSortedEntrySet ? ((Byte2IntSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2IntMap.Entry> fastIterable(Byte2IntSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2IntEntrySet();
      Object var2;
      if (var1 instanceof Byte2IntSortedMap.FastSortedEntrySet) {
         Byte2IntSortedMap.FastSortedEntrySet var10000 = (Byte2IntSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Byte2IntSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Byte2IntSortedMap singleton(Byte var0, Integer var1) {
      return new Byte2IntSortedMaps.Singleton(var0, var1);
   }

   public static Byte2IntSortedMap singleton(Byte var0, Integer var1, ByteComparator var2) {
      return new Byte2IntSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2IntSortedMap singleton(byte var0, int var1) {
      return new Byte2IntSortedMaps.Singleton(var0, var1);
   }

   public static Byte2IntSortedMap singleton(byte var0, int var1, ByteComparator var2) {
      return new Byte2IntSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2IntSortedMap synchronize(Byte2IntSortedMap var0) {
      return new Byte2IntSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Byte2IntSortedMap synchronize(Byte2IntSortedMap var0, Object var1) {
      return new Byte2IntSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Byte2IntSortedMap unmodifiable(Byte2IntSortedMap var0) {
      return new Byte2IntSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Byte2IntMaps.UnmodifiableMap implements Byte2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2IntSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2IntSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2IntEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2IntSortedMap subMap(byte var1, byte var2) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Byte2IntSortedMap headMap(byte var1) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Byte2IntSortedMap tailMap(byte var1) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Byte2IntSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap headMap(Byte var1) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap tailMap(Byte var1) {
         return new Byte2IntSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Byte2IntMaps.SynchronizedMap implements Byte2IntSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2IntSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2IntSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Byte2IntSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2IntEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2IntSortedMap subMap(byte var1, byte var2) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Byte2IntSortedMap headMap(byte var1) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Byte2IntSortedMap tailMap(byte var1) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Byte2IntSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap headMap(Byte var1) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap tailMap(Byte var1) {
         return new Byte2IntSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Byte2IntMaps.Singleton implements Byte2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte var1, int var2, ByteComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(byte var1, int var2) {
         this(var1, var2, (ByteComparator)null);
      }

      final int compare(byte var1, byte var2) {
         return this.comparator == null ? Byte.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ByteComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractByte2IntMap.BasicEntry(this.key, this.value), Byte2IntSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return this.byte2IntEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2IntSortedMap subMap(byte var1, byte var2) {
         return (Byte2IntSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Byte2IntSortedMaps.EMPTY_MAP);
      }

      public Byte2IntSortedMap headMap(byte var1) {
         return (Byte2IntSortedMap)(this.compare(this.key, var1) < 0 ? this : Byte2IntSortedMaps.EMPTY_MAP);
      }

      public Byte2IntSortedMap tailMap(byte var1) {
         return (Byte2IntSortedMap)(this.compare(var1, this.key) <= 0 ? this : Byte2IntSortedMaps.EMPTY_MAP);
      }

      public byte firstByteKey() {
         return this.key;
      }

      public byte lastByteKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap subMap(Byte var1, Byte var2) {
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

   public static class EmptySortedMap extends Byte2IntMaps.EmptyMap implements Byte2IntSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ByteComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2IntMap.Entry> byte2IntEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Integer>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      public Byte2IntSortedMap subMap(byte var1, byte var2) {
         return Byte2IntSortedMaps.EMPTY_MAP;
      }

      public Byte2IntSortedMap headMap(byte var1) {
         return Byte2IntSortedMaps.EMPTY_MAP;
      }

      public Byte2IntSortedMap tailMap(byte var1) {
         return Byte2IntSortedMaps.EMPTY_MAP;
      }

      public byte firstByteKey() {
         throw new NoSuchElementException();
      }

      public byte lastByteKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2IntSortedMap subMap(Byte var1, Byte var2) {
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
