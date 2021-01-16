package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2LongSortedMap extends AbstractByte2LongMap implements Byte2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2LongSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractByte2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Byte2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Byte2LongMap.Entry)this.i.next()).getLongValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractLongCollection {
      protected ValuesCollection() {
         super();
      }

      public LongIterator iterator() {
         return new AbstractByte2LongSortedMap.ValuesIterator(Byte2LongSortedMaps.fastIterator(AbstractByte2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractByte2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2LongMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2LongMap.Entry)this.i.previous()).getByteKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractByteSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(byte var1) {
         return AbstractByte2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2LongSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2LongSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2LongSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2LongSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2LongSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2LongSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2LongSortedMap.KeySetIterator(AbstractByte2LongSortedMap.this.byte2LongEntrySet().iterator(new AbstractByte2LongMap.BasicEntry(var1, 0L)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2LongSortedMap.KeySetIterator(Byte2LongSortedMaps.fastIterator(AbstractByte2LongSortedMap.this));
      }
   }
}
