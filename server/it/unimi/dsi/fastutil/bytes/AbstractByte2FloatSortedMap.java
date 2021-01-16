package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2FloatSortedMap extends AbstractByte2FloatMap implements Byte2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2FloatSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractByte2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Byte2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Byte2FloatMap.Entry)this.i.next()).getFloatValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractFloatCollection {
      protected ValuesCollection() {
         super();
      }

      public FloatIterator iterator() {
         return new AbstractByte2FloatSortedMap.ValuesIterator(Byte2FloatSortedMaps.fastIterator(AbstractByte2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractByte2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2FloatMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2FloatMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2FloatSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2FloatSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2FloatSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2FloatSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2FloatSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2FloatSortedMap.KeySetIterator(AbstractByte2FloatSortedMap.this.byte2FloatEntrySet().iterator(new AbstractByte2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2FloatSortedMap.KeySetIterator(Byte2FloatSortedMaps.fastIterator(AbstractByte2FloatSortedMap.this));
      }
   }
}
