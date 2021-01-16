package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2DoubleSortedMap extends AbstractChar2DoubleMap implements Char2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2DoubleSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractChar2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Char2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Char2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
         return new AbstractChar2DoubleSortedMap.ValuesIterator(Char2DoubleSortedMaps.fastIterator(AbstractChar2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractChar2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2DoubleMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2DoubleMap.Entry)this.i.previous()).getCharKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractCharSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(char var1) {
         return AbstractChar2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2DoubleSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2DoubleSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2DoubleSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2DoubleSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2DoubleSortedMap.KeySetIterator(AbstractChar2DoubleSortedMap.this.char2DoubleEntrySet().iterator(new AbstractChar2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2DoubleSortedMap.KeySetIterator(Char2DoubleSortedMaps.fastIterator(AbstractChar2DoubleSortedMap.this));
      }
   }
}
