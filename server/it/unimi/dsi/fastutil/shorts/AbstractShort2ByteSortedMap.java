package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2ByteSortedMap extends AbstractShort2ByteMap implements Short2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2ByteSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractShort2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Short2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Short2ByteMap.Entry)this.i.next()).getByteValue();
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
         return new AbstractShort2ByteSortedMap.ValuesIterator(Short2ByteSortedMaps.fastIterator(AbstractShort2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractShort2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2ByteMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2ByteMap.Entry)this.i.previous()).getShortKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractShortSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(short var1) {
         return AbstractShort2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2ByteSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2ByteSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2ByteSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2ByteSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2ByteSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2ByteSortedMap.KeySetIterator(AbstractShort2ByteSortedMap.this.short2ByteEntrySet().iterator(new AbstractShort2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2ByteSortedMap.KeySetIterator(Short2ByteSortedMaps.fastIterator(AbstractShort2ByteSortedMap.this));
      }
   }
}
