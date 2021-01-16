package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractByte2ShortSortedMap extends AbstractByte2ShortMap implements Byte2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ShortSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractByte2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Byte2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Byte2ShortMap.Entry)this.i.next()).getShortValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractShortCollection {
      protected ValuesCollection() {
         super();
      }

      public ShortIterator iterator() {
         return new AbstractByte2ShortSortedMap.ValuesIterator(Byte2ShortSortedMaps.fastIterator(AbstractByte2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractByte2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2ShortMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2ShortMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ShortSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2ShortSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2ShortSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2ShortSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2ShortSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2ShortSortedMap.KeySetIterator(AbstractByte2ShortSortedMap.this.byte2ShortEntrySet().iterator(new AbstractByte2ShortMap.BasicEntry(var1, (short)0)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ShortSortedMap.KeySetIterator(Byte2ShortSortedMaps.fastIterator(AbstractByte2ShortSortedMap.this));
      }
   }
}
