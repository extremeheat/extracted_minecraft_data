package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2ByteSortedMap extends AbstractByte2ByteMap implements Byte2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ByteSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractByte2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2ByteMap.Entry)this.i.next()).getByteValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractByteCollection {
      protected ValuesCollection() {
         super();
      }

      public ByteIterator iterator() {
         return new AbstractByte2ByteSortedMap.ValuesIterator(Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractByte2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2ByteMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2ByteMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2ByteSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2ByteSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2ByteSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2ByteSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2ByteSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2ByteSortedMap.KeySetIterator(AbstractByte2ByteSortedMap.this.byte2ByteEntrySet().iterator(new AbstractByte2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ByteSortedMap.KeySetIterator(Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this));
      }
   }
}
