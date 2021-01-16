package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2FloatSortedMap extends AbstractDouble2FloatMap implements Double2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2FloatSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractDouble2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Double2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Double2FloatMap.Entry)this.i.next()).getFloatValue();
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
         return new AbstractDouble2FloatSortedMap.ValuesIterator(Double2FloatSortedMaps.fastIterator(AbstractDouble2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractDouble2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2FloatMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2FloatMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2FloatSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2FloatSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2FloatSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2FloatSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2FloatSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2FloatSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2FloatSortedMap.KeySetIterator(AbstractDouble2FloatSortedMap.this.double2FloatEntrySet().iterator(new AbstractDouble2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2FloatSortedMap.KeySetIterator(Double2FloatSortedMaps.fastIterator(AbstractDouble2FloatSortedMap.this));
      }
   }
}
