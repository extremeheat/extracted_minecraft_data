package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2DoubleSortedMap extends AbstractByte2DoubleMap implements Byte2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2DoubleSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractByte2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Byte2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Byte2DoubleMap.Entry)this.i.next()).getDoubleValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractDoubleCollection {
      protected ValuesCollection() {
         super();
      }

      public DoubleIterator iterator() {
         return new AbstractByte2DoubleSortedMap.ValuesIterator(Byte2DoubleSortedMaps.fastIterator(AbstractByte2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractByte2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2DoubleMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2DoubleMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2DoubleSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2DoubleSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2DoubleSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2DoubleSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2DoubleSortedMap.KeySetIterator(AbstractByte2DoubleSortedMap.this.byte2DoubleEntrySet().iterator(new AbstractByte2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2DoubleSortedMap.KeySetIterator(Byte2DoubleSortedMaps.fastIterator(AbstractByte2DoubleSortedMap.this));
      }
   }
}
