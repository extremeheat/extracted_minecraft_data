package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2BooleanSortedMap extends AbstractByte2BooleanMap implements Byte2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2BooleanSortedMap() {
      super();
   }

   public ByteSortedSet keySet() {
      return new AbstractByte2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractByte2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Byte2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Byte2BooleanMap.Entry)this.i.next()).getBooleanValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractBooleanCollection {
      protected ValuesCollection() {
         super();
      }

      public BooleanIterator iterator() {
         return new AbstractByte2BooleanSortedMap.ValuesIterator(Byte2BooleanSortedMaps.fastIterator(AbstractByte2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractByte2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractByte2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Byte2BooleanMap.Entry)this.i.next()).getByteKey();
      }

      public byte previousByte() {
         return ((Byte2BooleanMap.Entry)this.i.previous()).getByteKey();
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
         return AbstractByte2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractByte2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractByte2BooleanSortedMap.this.clear();
      }

      public ByteComparator comparator() {
         return AbstractByte2BooleanSortedMap.this.comparator();
      }

      public byte firstByte() {
         return AbstractByte2BooleanSortedMap.this.firstByteKey();
      }

      public byte lastByte() {
         return AbstractByte2BooleanSortedMap.this.lastByteKey();
      }

      public ByteSortedSet headSet(byte var1) {
         return AbstractByte2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public ByteSortedSet tailSet(byte var1) {
         return AbstractByte2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public ByteSortedSet subSet(byte var1, byte var2) {
         return AbstractByte2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public ByteBidirectionalIterator iterator(byte var1) {
         return new AbstractByte2BooleanSortedMap.KeySetIterator(AbstractByte2BooleanSortedMap.this.byte2BooleanEntrySet().iterator(new AbstractByte2BooleanMap.BasicEntry(var1, false)));
      }

      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2BooleanSortedMap.KeySetIterator(Byte2BooleanSortedMaps.fastIterator(AbstractByte2BooleanSortedMap.this));
      }
   }
}
