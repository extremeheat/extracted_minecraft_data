package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractDouble2ShortSortedMap extends AbstractDouble2ShortMap implements Double2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2ShortSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractDouble2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Double2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Double2ShortMap.Entry)this.i.next()).getShortValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractShortCollection {
      protected ValuesCollection() {
         super();
      }

      public ShortIterator iterator() {
         return new AbstractDouble2ShortSortedMap.ValuesIterator(Double2ShortSortedMaps.fastIterator(AbstractDouble2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractDouble2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2ShortMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2ShortMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ShortSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2ShortSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2ShortSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2ShortSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2ShortSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2ShortSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2ShortSortedMap.KeySetIterator(AbstractDouble2ShortSortedMap.this.double2ShortEntrySet().iterator(new AbstractDouble2ShortMap.BasicEntry(var1, (short)0)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2ShortSortedMap.KeySetIterator(Double2ShortSortedMaps.fastIterator(AbstractDouble2ShortSortedMap.this));
      }
   }
}
