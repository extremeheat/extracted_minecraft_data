package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Comparator;

public abstract class AbstractReference2IntSortedMap<K> extends AbstractReference2IntMap<K> implements Reference2IntSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2IntSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractReference2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements IntIterator {
      protected final ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Reference2IntMap.Entry)this.i.next()).getIntValue();
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
         return new AbstractReference2IntSortedMap.ValuesIterator(Reference2IntSortedMaps.fastIterator(AbstractReference2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractReference2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2IntMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2IntMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2IntMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2IntSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2IntSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2IntSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2IntSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2IntSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2IntSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2IntSortedMap.KeySetIterator(AbstractReference2IntSortedMap.this.reference2IntEntrySet().iterator(new AbstractReference2IntMap.BasicEntry(var1, 0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2IntSortedMap.KeySetIterator(Reference2IntSortedMaps.fastIterator(AbstractReference2IntSortedMap.this));
      }
   }
}
