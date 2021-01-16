package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2IntSortedMap extends AbstractByte2IntMap implements Byte2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2IntSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractByte2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Byte2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Byte2IntMap.Entry)this.i.next()).getIntValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractIntCollection {
      protected ValuesCollection() {
         super();
      }

      public IntIterator iterator() {
         return new AbstractByte2IntSortedMap.ValuesIterator(Byte2IntSortedMaps.fastIterator(AbstractByte2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractByte2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2IntMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2IntMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2IntSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2IntSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2IntSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2IntSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2IntSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2IntSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2IntSortedMap.KeySetIterator(AbstractByte2IntSortedMap.this.byte2IntEntrySet().iterator(new AbstractByte2IntMap.BasicEntry(var1, 0)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2IntSortedMap.KeySetIterator(Byte2IntSortedMaps.fastIterator(AbstractByte2IntSortedMap.this));
      }
   }
}
