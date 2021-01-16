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

public final class Byte2LongSortedMaps {
   public static final Byte2LongSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2LongSortedMaps.EmptySortedMap();

   private Byte2LongSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Byte)var1.getKey(), (Byte)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Byte2LongMap.Entry> fastIterator(Byte2LongSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2LongEntrySet();
      return var1 instanceof Byte2LongSortedMap.FastSortedEntrySet ? ((Byte2LongSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2LongMap.Entry> fastIterable(Byte2LongSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2LongEntrySet();
      Object var2;
      if (var1 instanceof Byte2LongSortedMap.FastSortedEntrySet) {
         Byte2LongSortedMap.FastSortedEntrySet var10000 = (Byte2LongSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Byte2LongSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Byte2LongSortedMap singleton(Byte var0, Long var1) {
      return new Byte2LongSortedMaps.Singleton(var0, var1);
   }

   public static Byte2LongSortedMap singleton(Byte var0, Long var1, ByteComparator var2) {
      return new Byte2LongSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2LongSortedMap singleton(byte var0, long var1) {
      return new Byte2LongSortedMaps.Singleton(var0, var1);
   }

   public static Byte2LongSortedMap singleton(byte var0, long var1, ByteComparator var3) {
      return new Byte2LongSortedMaps.Singleton(var0, var1, var3);
   }

   public static Byte2LongSortedMap synchronize(Byte2LongSortedMap var0) {
      return new Byte2LongSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Byte2LongSortedMap synchronize(Byte2LongSortedMap var0, Object var1) {
      return new Byte2LongSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Byte2LongSortedMap unmodifiable(Byte2LongSortedMap var0) {
      return new Byte2LongSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Byte2LongMaps.UnmodifiableMap implements Byte2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2LongSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2LongSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2LongEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Long>> entrySet() {
         return this.byte2LongEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2LongSortedMap subMap(byte var1, byte var2) {
         return new Byte2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Byte2LongSortedMap headMap(byte var1) {
         return new Byte2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Byte2LongSortedMap tailMap(byte var1) {
         return new Byte2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Byte2LongSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap headMap(Byte var1) {
         return new Byte2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap tailMap(Byte var1) {
         return new Byte2LongSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Byte2LongMaps.SynchronizedMap implements Byte2LongSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2LongSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2LongSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Byte2LongSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2LongEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Long>> entrySet() {
         return this.byte2LongEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2LongSortedMap subMap(byte var1, byte var2) {
         return new Byte2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Byte2LongSortedMap headMap(byte var1) {
         return new Byte2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Byte2LongSortedMap tailMap(byte var1) {
         return new Byte2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Byte2LongSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap headMap(Byte var1) {
         return new Byte2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap tailMap(Byte var1) {
         return new Byte2LongSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Byte2LongMaps.Singleton implements Byte2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte var1, long var2, ByteComparator var4) {
         super(var1, var2);
         this.comparator = var4;
      }

      protected Singleton(byte var1, long var2) {
         this(var1, var2, (ByteComparator)null);
      }

      final int compare(byte var1, byte var2) {
         return this.comparator == null ? Byte.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ByteComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractByte2LongMap.BasicEntry(this.key, this.value), Byte2LongSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Long>> entrySet() {
         return this.byte2LongEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2LongSortedMap subMap(byte var1, byte var2) {
         return (Byte2LongSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Byte2LongSortedMaps.EMPTY_MAP);
      }

      public Byte2LongSortedMap headMap(byte var1) {
         return (Byte2LongSortedMap)(this.compare(this.key, var1) < 0 ? this : Byte2LongSortedMaps.EMPTY_MAP);
      }

      public Byte2LongSortedMap tailMap(byte var1) {
         return (Byte2LongSortedMap)(this.compare(var1, this.key) <= 0 ? this : Byte2LongSortedMaps.EMPTY_MAP);
      }

      public byte firstByteKey() {
         return this.key;
      }

      public byte lastByteKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap subMap(Byte var1, Byte var2) {
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

   public static class EmptySortedMap extends Byte2LongMaps.EmptyMap implements Byte2LongSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ByteComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2LongMap.Entry> byte2LongEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Long>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      public Byte2LongSortedMap subMap(byte var1, byte var2) {
         return Byte2LongSortedMaps.EMPTY_MAP;
      }

      public Byte2LongSortedMap headMap(byte var1) {
         return Byte2LongSortedMaps.EMPTY_MAP;
      }

      public Byte2LongSortedMap tailMap(byte var1) {
         return Byte2LongSortedMaps.EMPTY_MAP;
      }

      public byte firstByteKey() {
         throw new NoSuchElementException();
      }

      public byte lastByteKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2LongSortedMap subMap(Byte var1, Byte var2) {
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
