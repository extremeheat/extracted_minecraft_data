package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2IntSortedMap extends AbstractChar2IntMap implements Char2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2IntSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractChar2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Char2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Char2IntMap.Entry)this.i.next()).getIntValue();
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
         return new AbstractChar2IntSortedMap.ValuesIterator(Char2IntSortedMaps.fastIterator(AbstractChar2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractChar2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2IntMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2IntMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2IntMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2IntSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2IntSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2IntSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2IntSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2IntSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2IntSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2IntSortedMap.KeySetIterator(AbstractChar2IntSortedMap.this.char2IntEntrySet().iterator(new AbstractChar2IntMap.BasicEntry(var1, 0)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2IntSortedMap.KeySetIterator(Char2IntSortedMaps.fastIterator(AbstractChar2IntSortedMap.this));
      }
   }
}
