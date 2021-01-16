package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2IntSortedMap extends AbstractLong2IntMap implements Long2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2IntSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractLong2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Long2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Long2IntMap.Entry)this.i.next()).getIntValue();
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
         return new AbstractLong2IntSortedMap.ValuesIterator(Long2IntSortedMaps.fastIterator(AbstractLong2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractLong2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2IntMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2IntMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2IntSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2IntSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2IntSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2IntSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2IntSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2IntSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2IntSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2IntSortedMap.KeySetIterator(AbstractLong2IntSortedMap.this.long2IntEntrySet().iterator(new AbstractLong2IntMap.BasicEntry(var1, 0)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2IntSortedMap.KeySetIterator(Long2IntSortedMaps.fastIterator(AbstractLong2IntSortedMap.this));
      }
   }
}
