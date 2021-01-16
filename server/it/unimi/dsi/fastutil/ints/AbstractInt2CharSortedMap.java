package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2CharSortedMap extends AbstractInt2CharMap implements Int2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2CharSortedMap() {
      super();
   }

   public IntSortedSet keySet() {
      return new AbstractInt2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractInt2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Int2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Int2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractInt2CharSortedMap.ValuesIterator(Int2CharSortedMaps.fastIterator(AbstractInt2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractInt2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractInt2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2CharMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Int2CharMap.Entry)this.i.next()).getIntKey();
      }

      public int previousInt() {
         return ((Int2CharMap.Entry)this.i.previous()).getIntKey();
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
         return AbstractInt2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractInt2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractInt2CharSortedMap.this.clear();
      }

      public IntComparator comparator() {
         return AbstractInt2CharSortedMap.this.comparator();
      }

      public int firstInt() {
         return AbstractInt2CharSortedMap.this.firstIntKey();
      }

      public int lastInt() {
         return AbstractInt2CharSortedMap.this.lastIntKey();
      }

      public IntSortedSet headSet(int var1) {
         return AbstractInt2CharSortedMap.this.headMap(var1).keySet();
      }

      public IntSortedSet tailSet(int var1) {
         return AbstractInt2CharSortedMap.this.tailMap(var1).keySet();
      }

      public IntSortedSet subSet(int var1, int var2) {
         return AbstractInt2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public IntBidirectionalIterator iterator(int var1) {
         return new AbstractInt2CharSortedMap.KeySetIterator(AbstractInt2CharSortedMap.this.int2CharEntrySet().iterator(new AbstractInt2CharMap.BasicEntry(var1, '\u0000')));
      }

      public IntBidirectionalIterator iterator() {
         return new AbstractInt2CharSortedMap.KeySetIterator(Int2CharSortedMaps.fastIterator(AbstractInt2CharSortedMap.this));
      }
   }
}
