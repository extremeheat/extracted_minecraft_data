package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2LongSortedMap extends AbstractChar2LongMap implements Char2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2LongSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractChar2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Char2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Char2LongMap.Entry)this.i.next()).getLongValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractLongCollection {
      protected ValuesCollection() {
         super();
      }

      public LongIterator iterator() {
         return new AbstractChar2LongSortedMap.ValuesIterator(Char2LongSortedMaps.fastIterator(AbstractChar2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractChar2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2LongMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2LongMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2LongMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2LongSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2LongSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2LongSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2LongSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2LongSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2LongSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2LongSortedMap.KeySetIterator(AbstractChar2LongSortedMap.this.char2LongEntrySet().iterator(new AbstractChar2LongMap.BasicEntry(var1, 0L)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2LongSortedMap.KeySetIterator(Char2LongSortedMaps.fastIterator(AbstractChar2LongSortedMap.this));
      }
   }
}
