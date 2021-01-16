package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractChar2ReferenceSortedMap<V> extends AbstractChar2ReferenceMap<V> implements Char2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2ReferenceSortedMap() {
      super();
   }

   public CharSortedSet keySet() {
      return new AbstractChar2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractChar2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Char2ReferenceMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractReferenceCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractChar2ReferenceSortedMap.ValuesIterator(Char2ReferenceSortedMaps.fastIterator(AbstractChar2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractChar2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractChar2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Char2ReferenceMap.Entry)this.i.next()).getCharKey();
      }

      public char previousChar() {
         return ((Char2ReferenceMap.Entry)this.i.previous()).getCharKey();
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
         return AbstractChar2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractChar2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractChar2ReferenceSortedMap.this.clear();
      }

      public CharComparator comparator() {
         return AbstractChar2ReferenceSortedMap.this.comparator();
      }

      public char firstChar() {
         return AbstractChar2ReferenceSortedMap.this.firstCharKey();
      }

      public char lastChar() {
         return AbstractChar2ReferenceSortedMap.this.lastCharKey();
      }

      public CharSortedSet headSet(char var1) {
         return AbstractChar2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public CharSortedSet tailSet(char var1) {
         return AbstractChar2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public CharSortedSet subSet(char var1, char var2) {
         return AbstractChar2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public CharBidirectionalIterator iterator(char var1) {
         return new AbstractChar2ReferenceSortedMap.KeySetIterator(AbstractChar2ReferenceSortedMap.this.char2ReferenceEntrySet().iterator(new AbstractChar2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public CharBidirectionalIterator iterator() {
         return new AbstractChar2ReferenceSortedMap.KeySetIterator(Char2ReferenceSortedMaps.fastIterator(AbstractChar2ReferenceSortedMap.this));
      }
   }
}
