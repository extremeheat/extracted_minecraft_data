package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Comparator;

public abstract class AbstractReference2FloatSortedMap<K> extends AbstractReference2FloatMap<K> implements Reference2FloatSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2FloatSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractReference2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements FloatIterator {
      protected final ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Reference2FloatMap.Entry)this.i.next()).getFloatValue();
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
         return new AbstractReference2FloatSortedMap.ValuesIterator(Reference2FloatSortedMaps.fastIterator(AbstractReference2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractReference2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2FloatMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2FloatMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2FloatMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2FloatSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2FloatSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2FloatSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2FloatSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2FloatSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2FloatSortedMap.KeySetIterator(AbstractReference2FloatSortedMap.this.reference2FloatEntrySet().iterator(new AbstractReference2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2FloatSortedMap.KeySetIterator(Reference2FloatSortedMaps.fastIterator(AbstractReference2FloatSortedMap.this));
      }
   }
}
