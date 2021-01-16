package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2DoubleSortedMap extends AbstractShort2DoubleMap implements Short2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2DoubleSortedMap() {
      super();
   }

   public ShortSortedSet keySet() {
      return new AbstractShort2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractShort2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Short2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Short2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
         return new AbstractShort2DoubleSortedMap.ValuesIterator(Short2DoubleSortedMaps.fastIterator(AbstractShort2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractShort2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractShort2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Short2DoubleMap.Entry)this.i.next()).getShortKey();
      }

      public short previousShort() {
         return ((Short2DoubleMap.Entry)this.i.previous()).getShortKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractShortSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(short var1) {
         return AbstractShort2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractShort2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractShort2DoubleSortedMap.this.clear();
      }

      public ShortComparator comparator() {
         return AbstractShort2DoubleSortedMap.this.comparator();
      }

      public short firstShort() {
         return AbstractShort2DoubleSortedMap.this.firstShortKey();
      }

      public short lastShort() {
         return AbstractShort2DoubleSortedMap.this.lastShortKey();
      }

      public ShortSortedSet headSet(short var1) {
         return AbstractShort2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public ShortSortedSet tailSet(short var1) {
         return AbstractShort2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public ShortSortedSet subSet(short var1, short var2) {
         return AbstractShort2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public ShortBidirectionalIterator iterator(short var1) {
         return new AbstractShort2DoubleSortedMap.KeySetIterator(AbstractShort2DoubleSortedMap.this.short2DoubleEntrySet().iterator(new AbstractShort2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2DoubleSortedMap.KeySetIterator(Short2DoubleSortedMaps.fastIterator(AbstractShort2DoubleSortedMap.this));
      }
   }
}
