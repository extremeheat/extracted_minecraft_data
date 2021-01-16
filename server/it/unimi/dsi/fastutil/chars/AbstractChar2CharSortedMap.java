package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2CharSortedMap extends AbstractChar2CharMap implements Char2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2CharSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractChar2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Char2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractChar2CharSortedMap.ValuesIterator(Char2CharSortedMaps.fastIterator(AbstractChar2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractChar2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2CharMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2CharMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2CharSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2CharSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2CharSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2CharSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2CharSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2CharSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2CharSortedMap.KeySetIterator(AbstractChar2CharSortedMap.this.char2CharEntrySet().iterator(new AbstractChar2CharMap.BasicEntry(var1, '\u0000')));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2CharSortedMap.KeySetIterator(Char2CharSortedMaps.fastIterator(AbstractChar2CharSortedMap.this));
      }
   }
}
