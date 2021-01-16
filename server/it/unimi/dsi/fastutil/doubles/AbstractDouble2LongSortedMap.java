package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2LongSortedMap extends AbstractDouble2LongMap implements Double2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2LongSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractDouble2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Double2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Double2LongMap.Entry)this.i.next()).getLongValue();
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
         return new AbstractDouble2LongSortedMap.ValuesIterator(Double2LongSortedMaps.fastIterator(AbstractDouble2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractDouble2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2LongMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2LongMap.Entry)this.i.previous()).getDoubleKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(double var1) {
         return AbstractDouble2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2LongSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2LongSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2LongSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2LongSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2LongSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2LongSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2LongSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2LongSortedMap.KeySetIterator(AbstractDouble2LongSortedMap.this.double2LongEntrySet().iterator(new AbstractDouble2LongMap.BasicEntry(var1, 0L)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2LongSortedMap.KeySetIterator(Double2LongSortedMaps.fastIterator(AbstractDouble2LongSortedMap.this));
      }
   }
}
