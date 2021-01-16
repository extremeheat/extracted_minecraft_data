package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2IntSortedMap extends AbstractDouble2IntMap implements Double2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2IntSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractDouble2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Double2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Double2IntMap.Entry)this.i.next()).getIntValue();
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
         return new AbstractDouble2IntSortedMap.ValuesIterator(Double2IntSortedMaps.fastIterator(AbstractDouble2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractDouble2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2IntMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2IntMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2IntSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2IntSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2IntSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2IntSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2IntSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2IntSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2IntSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2IntSortedMap.KeySetIterator(AbstractDouble2IntSortedMap.this.double2IntEntrySet().iterator(new AbstractDouble2IntMap.BasicEntry(var1, 0)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2IntSortedMap.KeySetIterator(Double2IntSortedMaps.fastIterator(AbstractDouble2IntSortedMap.this));
      }
   }
}
