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

public final class Byte2FloatSortedMaps {
   public static final Byte2FloatSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2FloatSortedMaps.EmptySortedMap();

   private Byte2FloatSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Byte)var1.getKey(), (Byte)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2FloatEntrySet();
      return var1 instanceof Byte2FloatSortedMap.FastSortedEntrySet ? ((Byte2FloatSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2FloatMap.Entry> fastIterable(Byte2FloatSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2FloatEntrySet();
      Object var2;
      if (var1 instanceof Byte2FloatSortedMap.FastSortedEntrySet) {
         Byte2FloatSortedMap.FastSortedEntrySet var10000 = (Byte2FloatSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Byte2FloatSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Byte2FloatSortedMap singleton(Byte var0, Float var1) {
      return new Byte2FloatSortedMaps.Singleton(var0, var1);
   }

   public static Byte2FloatSortedMap singleton(Byte var0, Float var1, ByteComparator var2) {
      return new Byte2FloatSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2FloatSortedMap singleton(byte var0, float var1) {
      return new Byte2FloatSortedMaps.Singleton(var0, var1);
   }

   public static Byte2FloatSortedMap singleton(byte var0, float var1, ByteComparator var2) {
      return new Byte2FloatSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2FloatSortedMap synchronize(Byte2FloatSortedMap var0) {
      return new Byte2FloatSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Byte2FloatSortedMap synchronize(Byte2FloatSortedMap var0, Object var1) {
      return new Byte2FloatSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Byte2FloatSortedMap unmodifiable(Byte2FloatSortedMap var0) {
      return new Byte2FloatSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Byte2FloatMaps.UnmodifiableMap implements Byte2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2FloatSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2FloatEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2FloatSortedMap subMap(byte var1, byte var2) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Byte2FloatSortedMap headMap(byte var1) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Byte2FloatSortedMap tailMap(byte var1) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Byte2FloatSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap headMap(Byte var1) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap tailMap(Byte var1) {
         return new Byte2FloatSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Byte2FloatMaps.SynchronizedMap implements Byte2FloatSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2FloatSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2FloatSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Byte2FloatSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2FloatEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2FloatSortedMap subMap(byte var1, byte var2) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Byte2FloatSortedMap headMap(byte var1) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Byte2FloatSortedMap tailMap(byte var1) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Byte2FloatSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap headMap(Byte var1) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap tailMap(Byte var1) {
         return new Byte2FloatSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Byte2FloatMaps.Singleton implements Byte2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte var1, float var2, ByteComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(byte var1, float var2) {
         this(var1, var2, (ByteComparator)null);
      }

      final int compare(byte var1, byte var2) {
         return this.comparator == null ? Byte.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ByteComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractByte2FloatMap.BasicEntry(this.key, this.value), Byte2FloatSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return this.byte2FloatEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2FloatSortedMap subMap(byte var1, byte var2) {
         return (Byte2FloatSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Byte2FloatSortedMaps.EMPTY_MAP);
      }

      public Byte2FloatSortedMap headMap(byte var1) {
         return (Byte2FloatSortedMap)(this.compare(this.key, var1) < 0 ? this : Byte2FloatSortedMaps.EMPTY_MAP);
      }

      public Byte2FloatSortedMap tailMap(byte var1) {
         return (Byte2FloatSortedMap)(this.compare(var1, this.key) <= 0 ? this : Byte2FloatSortedMaps.EMPTY_MAP);
      }

      public byte firstByteKey() {
         return this.key;
      }

      public byte lastByteKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap subMap(Byte var1, Byte var2) {
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

   public static class EmptySortedMap extends Byte2FloatMaps.EmptyMap implements Byte2FloatSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ByteComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Float>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      public Byte2FloatSortedMap subMap(byte var1, byte var2) {
         return Byte2FloatSortedMaps.EMPTY_MAP;
      }

      public Byte2FloatSortedMap headMap(byte var1) {
         return Byte2FloatSortedMaps.EMPTY_MAP;
      }

      public Byte2FloatSortedMap tailMap(byte var1) {
         return Byte2FloatSortedMaps.EMPTY_MAP;
      }

      public byte firstByteKey() {
         throw new NoSuchElementException();
      }

      public byte lastByteKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2FloatSortedMap subMap(Byte var1, Byte var2) {
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
