package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractChar2ShortSortedMap extends AbstractChar2ShortMap implements Char2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2ShortSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractChar2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Char2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Char2ShortMap.Entry)this.i.next()).getShortValue();
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
         return new AbstractChar2ShortSortedMap.ValuesIterator(Char2ShortSortedMaps.fastIterator(AbstractChar2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractChar2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2ShortMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2ShortMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2ShortMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ShortSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2ShortSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2ShortSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2ShortSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2ShortSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2ShortSortedMap.KeySetIterator(AbstractChar2ShortSortedMap.this.char2ShortEntrySet().iterator(new AbstractChar2ShortMap.BasicEntry(var1, (short)0)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2ShortSortedMap.KeySetIterator(Char2ShortSortedMaps.fastIterator(AbstractChar2ShortSortedMap.this));
      }
   }
}
