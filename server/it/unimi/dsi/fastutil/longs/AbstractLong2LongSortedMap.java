package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2LongSortedMap extends AbstractLong2LongMap implements Long2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2LongSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractLong2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Long2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2LongMap.Entry)this.i.next()).getLongValue();
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
         return new AbstractLong2LongSortedMap.ValuesIterator(Long2LongSortedMaps.fastIterator(AbstractLong2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractLong2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2LongMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2LongMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2LongSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2LongSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2LongSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2LongSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2LongSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2LongSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2LongSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2LongSortedMap.KeySetIterator(AbstractLong2LongSortedMap.this.long2LongEntrySet().iterator(new AbstractLong2LongMap.BasicEntry(var1, 0L)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2LongSortedMap.KeySetIterator(Long2LongSortedMaps.fastIterator(AbstractLong2LongSortedMap.this));
      }
   }
}
