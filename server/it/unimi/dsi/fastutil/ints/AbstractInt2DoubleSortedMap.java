package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2DoubleSortedMap extends AbstractInt2DoubleMap implements Int2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2DoubleSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractInt2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Int2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Int2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
         return new AbstractInt2DoubleSortedMap.ValuesIterator(Int2DoubleSortedMaps.fastIterator(AbstractInt2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractInt2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2DoubleMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2DoubleMap.Entry)this.i.previous()).getIntKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractIntSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(int var1) {
         return AbstractInt2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2DoubleSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2DoubleSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2DoubleSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2DoubleSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2DoubleSortedMap.KeySetIterator(AbstractInt2DoubleSortedMap.this.int2DoubleEntrySet().iterator(new AbstractInt2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2DoubleSortedMap.KeySetIterator(Int2DoubleSortedMaps.fastIterator(AbstractInt2DoubleSortedMap.this));
      }
   }
}
