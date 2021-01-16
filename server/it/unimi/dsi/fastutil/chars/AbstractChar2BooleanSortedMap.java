package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2BooleanSortedMap extends AbstractChar2BooleanMap implements Char2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2BooleanSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2BooleanSortedMap.KeySet();
   }

   public BooleanCollection values() {
      return new AbstractChar2BooleanSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Char2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public boolean nextBoolean() {
         return ((Char2BooleanMap.Entry)this.i.next()).getBooleanValue();
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
         return new AbstractChar2BooleanSortedMap.ValuesIterator(Char2BooleanSortedMaps.fastIterator(AbstractChar2BooleanSortedMap.this));
      }

      public boolean contains(boolean var1) {
         return AbstractChar2BooleanSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2BooleanSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2BooleanMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2BooleanMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2BooleanMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2BooleanSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2BooleanSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2BooleanSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2BooleanSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2BooleanSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2BooleanSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2BooleanSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2BooleanSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2BooleanSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2BooleanSortedMap.KeySetIterator(AbstractChar2BooleanSortedMap.this.char2BooleanEntrySet().iterator(new AbstractChar2BooleanMap.BasicEntry(var1, false)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2BooleanSortedMap.KeySetIterator(Char2BooleanSortedMaps.fastIterator(AbstractChar2BooleanSortedMap.this));
      }
   }
}
