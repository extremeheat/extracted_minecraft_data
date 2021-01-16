package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2ByteSortedMap extends AbstractChar2ByteMap implements Char2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2ByteSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2ByteSortedMap.KeySet();
   }

   public ByteCollection values() {
      return new AbstractChar2ByteSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Char2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public byte nextByte() {
         return ((Char2ByteMap.Entry)this.i.next()).getByteValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractByteCollection {
      protected ValuesCollection() {
         super();
      }

      public ByteIterator iterator() {
         return new AbstractChar2ByteSortedMap.ValuesIterator(Char2ByteSortedMaps.fastIterator(AbstractChar2ByteSortedMap.this));
      }

      public boolean contains(byte var1) {
         return AbstractChar2ByteSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ByteSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2ByteMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2ByteMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2ByteMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2ByteSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2ByteSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ByteSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2ByteSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2ByteSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2ByteSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2ByteSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2ByteSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2ByteSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2ByteSortedMap.KeySetIterator(AbstractChar2ByteSortedMap.this.char2ByteEntrySet().iterator(new AbstractChar2ByteMap.BasicEntry(var1, (byte)0)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2ByteSortedMap.KeySetIterator(Char2ByteSortedMaps.fastIterator(AbstractChar2ByteSortedMap.this));
      }
   }
}
