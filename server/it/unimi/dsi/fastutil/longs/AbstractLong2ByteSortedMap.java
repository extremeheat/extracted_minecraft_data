package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2ByteSortedMap extends AbstractLong2ByteMap implements Long2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2ByteSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractLong2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Long2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Long2ByteMap.Entry)this.i.next()).getByteValue();
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
         return new AbstractLong2ByteSortedMap.ValuesIterator(Long2ByteSortedMaps.fastIterator(AbstractLong2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractLong2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2ByteMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2ByteMap.Entry)this.i.previous()).getLongKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractLongSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(long var1) {
         return AbstractLong2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2ByteSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2ByteSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2ByteSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2ByteSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2ByteSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2ByteSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2ByteSortedMap.KeySetIterator(AbstractLong2ByteSortedMap.this.long2ByteEntrySet().iterator(new AbstractLong2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2ByteSortedMap.KeySetIterator(Long2ByteSortedMaps.fastIterator(AbstractLong2ByteSortedMap.this));
      }
   }
}
