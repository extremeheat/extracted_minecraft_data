package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2FloatSortedMap extends AbstractChar2FloatMap implements Char2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2FloatSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractChar2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Char2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Char2FloatMap.Entry)this.i.next()).getFloatValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractFloatCollection {
      protected ValuesCollection() {
         super();
      }

      public FloatIterator iterator() {
         return new AbstractChar2FloatSortedMap.ValuesIterator(Char2FloatSortedMaps.fastIterator(AbstractChar2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractChar2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2FloatMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2FloatMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2FloatSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2FloatSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2FloatSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2FloatSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2FloatSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2FloatSortedMap.KeySetIterator(AbstractChar2FloatSortedMap.this.char2FloatEntrySet().iterator(new AbstractChar2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2FloatSortedMap.KeySetIterator(Char2FloatSortedMaps.fastIterator(AbstractChar2FloatSortedMap.this));
      }
   }
}
