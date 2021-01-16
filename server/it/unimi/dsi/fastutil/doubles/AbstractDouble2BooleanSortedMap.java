package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2BooleanSortedMap extends AbstractDouble2BooleanMap implements Double2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2BooleanSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractDouble2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Double2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Double2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
         return new AbstractDouble2BooleanSortedMap.ValuesIterator(Double2BooleanSortedMaps.fastIterator(AbstractDouble2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractDouble2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2BooleanMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2BooleanMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2BooleanSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2BooleanSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2BooleanSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2BooleanSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2BooleanSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2BooleanSortedMap.KeySetIterator(AbstractDouble2BooleanSortedMap.this.double2BooleanEntrySet().iterator(new AbstractDouble2BooleanMap.BasicEntry(var1, false)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2BooleanSortedMap.KeySetIterator(Double2BooleanSortedMaps.fastIterator(AbstractDouble2BooleanSortedMap.this));
      }
   }
}
