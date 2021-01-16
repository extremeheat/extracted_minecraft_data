package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2CharSortedMap extends AbstractDouble2CharMap implements Double2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2CharSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractDouble2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Double2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Double2CharMap.Entry)this.i.next()).getCharValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractCharCollection {
      protected ValuesCollection() {
         super();
      }

      public CharIterator iterator() {
         return new AbstractDouble2CharSortedMap.ValuesIterator(Double2CharSortedMaps.fastIterator(AbstractDouble2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractDouble2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2CharMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2CharMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2CharSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2CharSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2CharSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2CharSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2CharSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2CharSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2CharSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2CharSortedMap.KeySetIterator(AbstractDouble2CharSortedMap.this.double2CharEntrySet().iterator(new AbstractDouble2CharMap.BasicEntry(var1, '\u0000')));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2CharSortedMap.KeySetIterator(Double2CharSortedMaps.fastIterator(AbstractDouble2CharSortedMap.this));
      }
   }
}
