package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2DoubleSortedMap extends AbstractLong2DoubleMap implements Long2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2DoubleSortedMap() {
      super();
   }

   public LongSortedSet keySet() {
      return new AbstractLong2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractLong2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Long2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Long2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
         return new AbstractLong2DoubleSortedMap.ValuesIterator(Long2DoubleSortedMaps.fastIterator(AbstractLong2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractLong2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractLong2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Long2DoubleMap.Entry)this.i.next()).getLongKey();
      }

      public long previousLong() {
         return ((Long2DoubleMap.Entry)this.i.previous()).getLongKey();
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
         return AbstractLong2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractLong2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractLong2DoubleSortedMap.this.clear();
      }

      public LongComparator comparator() {
         return AbstractLong2DoubleSortedMap.this.comparator();
      }

      public long firstLong() {
         return AbstractLong2DoubleSortedMap.this.firstLongKey();
      }

      public long lastLong() {
         return AbstractLong2DoubleSortedMap.this.lastLongKey();
      }

      public LongSortedSet headSet(long var1) {
         return AbstractLong2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public LongSortedSet tailSet(long var1) {
         return AbstractLong2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public LongSortedSet subSet(long var1, long var3) {
         return AbstractLong2DoubleSortedMap.this.subMap(var1, var3).keySet();
      }

      public LongBidirectionalIterator iterator(long var1) {
         return new AbstractLong2DoubleSortedMap.KeySetIterator(AbstractLong2DoubleSortedMap.this.long2DoubleEntrySet().iterator(new AbstractLong2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public LongBidirectionalIterator iterator() {
         return new AbstractLong2DoubleSortedMap.KeySetIterator(Long2DoubleSortedMaps.fastIterator(AbstractLong2DoubleSortedMap.this));
      }
   }
}
