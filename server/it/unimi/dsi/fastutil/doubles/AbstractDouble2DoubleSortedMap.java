package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2DoubleSortedMap extends AbstractDouble2DoubleMap implements Double2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2DoubleSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractDouble2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Double2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
         return new AbstractDouble2DoubleSortedMap.ValuesIterator(Double2DoubleSortedMaps.fastIterator(AbstractDouble2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractDouble2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2DoubleMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2DoubleMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2DoubleSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2DoubleSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2DoubleSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2DoubleSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2DoubleSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2DoubleSortedMap.KeySetIterator(AbstractDouble2DoubleSortedMap.this.double2DoubleEntrySet().iterator(new AbstractDouble2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2DoubleSortedMap.KeySetIterator(Double2DoubleSortedMaps.fastIterator(AbstractDouble2DoubleSortedMap.this));
      }
   }
}
