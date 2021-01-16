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

public final class Byte2BooleanSortedMaps {
   public static final Byte2BooleanSortedMaps.EmptySortedMap EMPTY_MAP = new Byte2BooleanSortedMaps.EmptySortedMap();

   private Byte2BooleanSortedMaps() {
      super();
   }

   public static Comparator<? super Entry<Byte, ?>> entryComparator(ByteComparator var0) {
      return (var1, var2) -> {
         return var0.compare((Byte)var1.getKey(), (Byte)var2.getKey());
      };
   }

   public static ObjectBidirectionalIterator<Byte2BooleanMap.Entry> fastIterator(Byte2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2BooleanEntrySet();
      return var1 instanceof Byte2BooleanSortedMap.FastSortedEntrySet ? ((Byte2BooleanSortedMap.FastSortedEntrySet)var1).fastIterator() : var1.iterator();
   }

   public static ObjectBidirectionalIterable<Byte2BooleanMap.Entry> fastIterable(Byte2BooleanSortedMap var0) {
      ObjectSortedSet var1 = var0.byte2BooleanEntrySet();
      Object var2;
      if (var1 instanceof Byte2BooleanSortedMap.FastSortedEntrySet) {
         Byte2BooleanSortedMap.FastSortedEntrySet var10000 = (Byte2BooleanSortedMap.FastSortedEntrySet)var1;
         Objects.requireNonNull((Byte2BooleanSortedMap.FastSortedEntrySet)var1);
         var2 = var10000::fastIterator;
      } else {
         var2 = var1;
      }

      return (ObjectBidirectionalIterable)var2;
   }

   public static Byte2BooleanSortedMap singleton(Byte var0, Boolean var1) {
      return new Byte2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Byte2BooleanSortedMap singleton(Byte var0, Boolean var1, ByteComparator var2) {
      return new Byte2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2BooleanSortedMap singleton(byte var0, boolean var1) {
      return new Byte2BooleanSortedMaps.Singleton(var0, var1);
   }

   public static Byte2BooleanSortedMap singleton(byte var0, boolean var1, ByteComparator var2) {
      return new Byte2BooleanSortedMaps.Singleton(var0, var1, var2);
   }

   public static Byte2BooleanSortedMap synchronize(Byte2BooleanSortedMap var0) {
      return new Byte2BooleanSortedMaps.SynchronizedSortedMap(var0);
   }

   public static Byte2BooleanSortedMap synchronize(Byte2BooleanSortedMap var0, Object var1) {
      return new Byte2BooleanSortedMaps.SynchronizedSortedMap(var0, var1);
   }

   public static Byte2BooleanSortedMap unmodifiable(Byte2BooleanSortedMap var0) {
      return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(var0);
   }

   public static class UnmodifiableSortedMap extends Byte2BooleanMaps.UnmodifiableMap implements Byte2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2BooleanSortedMap sortedMap;

      protected UnmodifiableSortedMap(Byte2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         return this.sortedMap.comparator();
      }

      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.unmodifiable(this.sortedMap.byte2BooleanEntrySet());
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.unmodifiable(this.sortedMap.keySet());
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2BooleanSortedMap subMap(byte var1, byte var2) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      public Byte2BooleanSortedMap headMap(byte var1) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      public Byte2BooleanSortedMap tailMap(byte var1) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
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
      public Byte2BooleanSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.subMap(var1, var2));
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap headMap(Byte var1) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.headMap(var1));
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap tailMap(Byte var1) {
         return new Byte2BooleanSortedMaps.UnmodifiableSortedMap(this.sortedMap.tailMap(var1));
      }
   }

   public static class SynchronizedSortedMap extends Byte2BooleanMaps.SynchronizedMap implements Byte2BooleanSortedMap, Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final Byte2BooleanSortedMap sortedMap;

      protected SynchronizedSortedMap(Byte2BooleanSortedMap var1, Object var2) {
         super(var1, var2);
         this.sortedMap = var1;
      }

      protected SynchronizedSortedMap(Byte2BooleanSortedMap var1) {
         super(var1);
         this.sortedMap = var1;
      }

      public ByteComparator comparator() {
         synchronized(this.sync) {
            return this.sortedMap.comparator();
         }
      }

      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.synchronize(this.sortedMap.byte2BooleanEntrySet(), this.sync);
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.synchronize(this.sortedMap.keySet(), this.sync);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2BooleanSortedMap subMap(byte var1, byte var2) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      public Byte2BooleanSortedMap headMap(byte var1) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      public Byte2BooleanSortedMap tailMap(byte var1) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
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
      public Byte2BooleanSortedMap subMap(Byte var1, Byte var2) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.subMap(var1, var2), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap headMap(Byte var1) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.headMap(var1), this.sync);
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap tailMap(Byte var1) {
         return new Byte2BooleanSortedMaps.SynchronizedSortedMap(this.sortedMap.tailMap(var1), this.sync);
      }
   }

   public static class Singleton extends Byte2BooleanMaps.Singleton implements Byte2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteComparator comparator;

      protected Singleton(byte var1, boolean var2, ByteComparator var3) {
         super(var1, var2);
         this.comparator = var3;
      }

      protected Singleton(byte var1, boolean var2) {
         this(var1, var2, (ByteComparator)null);
      }

      final int compare(byte var1, byte var2) {
         return this.comparator == null ? Byte.compare(var1, var2) : this.comparator.compare(var1, var2);
      }

      public ByteComparator comparator() {
         return this.comparator;
      }

      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         if (this.entries == null) {
            this.entries = ObjectSortedSets.singleton(new AbstractByte2BooleanMap.BasicEntry(this.key, this.value), Byte2BooleanSortedMaps.entryComparator(this.comparator));
         }

         return (ObjectSortedSet)this.entries;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return this.byte2BooleanEntrySet();
      }

      public ByteSortedSet keySet() {
         if (this.keys == null) {
            this.keys = ByteSortedSets.singleton(this.key, this.comparator);
         }

         return (ByteSortedSet)this.keys;
      }

      public Byte2BooleanSortedMap subMap(byte var1, byte var2) {
         return (Byte2BooleanSortedMap)(this.compare(var1, this.key) <= 0 && this.compare(this.key, var2) < 0 ? this : Byte2BooleanSortedMaps.EMPTY_MAP);
      }

      public Byte2BooleanSortedMap headMap(byte var1) {
         return (Byte2BooleanSortedMap)(this.compare(this.key, var1) < 0 ? this : Byte2BooleanSortedMaps.EMPTY_MAP);
      }

      public Byte2BooleanSortedMap tailMap(byte var1) {
         return (Byte2BooleanSortedMap)(this.compare(var1, this.key) <= 0 ? this : Byte2BooleanSortedMaps.EMPTY_MAP);
      }

      public byte firstByteKey() {
         return this.key;
      }

      public byte lastByteKey() {
         return this.key;
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap subMap(Byte var1, Byte var2) {
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

   public static class EmptySortedMap extends Byte2BooleanMaps.EmptyMap implements Byte2BooleanSortedMap, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptySortedMap() {
         super();
      }

      public ByteComparator comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2BooleanMap.Entry> byte2BooleanEntrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      /** @deprecated */
      @Deprecated
      public ObjectSortedSet<Entry<Byte, Boolean>> entrySet() {
         return ObjectSortedSets.EMPTY_SET;
      }

      public ByteSortedSet keySet() {
         return ByteSortedSets.EMPTY_SET;
      }

      public Byte2BooleanSortedMap subMap(byte var1, byte var2) {
         return Byte2BooleanSortedMaps.EMPTY_MAP;
      }

      public Byte2BooleanSortedMap headMap(byte var1) {
         return Byte2BooleanSortedMaps.EMPTY_MAP;
      }

      public Byte2BooleanSortedMap tailMap(byte var1) {
         return Byte2BooleanSortedMaps.EMPTY_MAP;
      }

      public byte firstByteKey() {
         throw new NoSuchElementException();
      }

      public byte lastByteKey() {
         throw new NoSuchElementException();
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap headMap(Byte var1) {
         return this.headMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap tailMap(Byte var1) {
         return this.tailMap(var1);
      }

      /** @deprecated */
      @Deprecated
      public Byte2BooleanSortedMap subMap(Byte var1, Byte var2) {
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
