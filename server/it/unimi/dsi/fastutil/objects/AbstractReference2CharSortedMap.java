package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Comparator;

public abstract class AbstractReference2CharSortedMap<K> extends AbstractReference2CharMap<K> implements Reference2CharSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2CharSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2CharSortedMap.KeySet();
   }

   public CharCollection values() {
      return new AbstractReference2CharSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements CharIterator {
      protected final ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public char nextChar() {
         return ((Reference2CharMap.Entry)this.i.next()).getCharValue();
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
         return new AbstractReference2CharSortedMap.ValuesIterator(Reference2CharSortedMaps.fastIterator(AbstractReference2CharSortedMap.this));
      }

      public boolean contains(char var1) {
         return AbstractReference2CharSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2CharSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2CharMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2CharMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      protected KeySet() {
         super();
      }

      public boolean contains(Object var1) {
         return AbstractReference2CharSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2CharSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2CharSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2CharSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2CharSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2CharSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2CharSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2CharSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2CharSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2CharSortedMap.KeySetIterator(AbstractReference2CharSortedMap.this.reference2CharEntrySet().iterator(new AbstractReference2CharMap.BasicEntry(var1, '\u0000')));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2CharSortedMap.KeySetIterator(Reference2CharSortedMaps.fastIterator(AbstractReference2CharSortedMap.this));
      }
   }
}
