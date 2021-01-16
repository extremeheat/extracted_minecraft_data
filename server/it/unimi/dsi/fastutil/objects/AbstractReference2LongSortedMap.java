package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Comparator;

public abstract class AbstractReference2LongSortedMap<K> extends AbstractReference2LongMap<K> implements Reference2LongSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2LongSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2LongSortedMap.KeySet();
   }

   public LongCollection values() {
      return new AbstractReference2LongSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements LongIterator {
      protected final ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public long nextLong() {
         return ((Reference2LongMap.Entry)this.i.next()).getLongValue();
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
         return new AbstractReference2LongSortedMap.ValuesIterator(Reference2LongSortedMaps.fastIterator(AbstractReference2LongSortedMap.this));
      }

      public boolean contains(long var1) {
         return AbstractReference2LongSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2LongSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2LongMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2LongMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2LongMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2LongSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2LongSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2LongSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2LongSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2LongSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2LongSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2LongSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2LongSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2LongSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2LongSortedMap.KeySetIterator(AbstractReference2LongSortedMap.this.reference2LongEntrySet().iterator(new AbstractReference2LongMap.BasicEntry(var1, 0L)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2LongSortedMap.KeySetIterator(Reference2LongSortedMaps.fastIterator(AbstractReference2LongSortedMap.this));
      }
   }
}
