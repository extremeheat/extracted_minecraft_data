package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Comparator;

public abstract class AbstractReference2ShortSortedMap<K> extends AbstractReference2ShortMap<K> implements Reference2ShortSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ShortSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ShortSortedMap.KeySet();
   }

   public ShortCollection values() {
      return new AbstractReference2ShortSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements ShortIterator {
      protected final ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public short nextShort() {
         return ((Reference2ShortMap.Entry)this.i.next()).getShortValue();
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
         return new AbstractReference2ShortSortedMap.ValuesIterator(Reference2ShortSortedMaps.fastIterator(AbstractReference2ShortSortedMap.this));
      }

      public boolean contains(short var1) {
         return AbstractReference2ShortSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ShortSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2ShortMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2ShortMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2ShortSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2ShortSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ShortSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2ShortSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2ShortSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2ShortSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2ShortSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2ShortSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2ShortSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2ShortSortedMap.KeySetIterator(AbstractReference2ShortSortedMap.this.reference2ShortEntrySet().iterator(new AbstractReference2ShortMap.BasicEntry(var1, (short)0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ShortSortedMap.KeySetIterator(Reference2ShortSortedMaps.fastIterator(AbstractReference2ShortSortedMap.this));
      }
   }
}
