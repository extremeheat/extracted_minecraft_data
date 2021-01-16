package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractReference2ReferenceSortedMap<K, V> extends AbstractReference2ReferenceMap<K, V> implements Reference2ReferenceSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ReferenceSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractReference2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Reference2ReferenceMap.Entry)this.i.next()).getValue();
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
         return new AbstractReference2ReferenceSortedMap.ValuesIterator(Reference2ReferenceSortedMaps.fastIterator(AbstractReference2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractReference2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2ReferenceMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2ReferenceMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ReferenceSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2ReferenceSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2ReferenceSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2ReferenceSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2ReferenceSortedMap.KeySetIterator(AbstractReference2ReferenceSortedMap.this.reference2ReferenceEntrySet().iterator(new AbstractReference2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ReferenceSortedMap.KeySetIterator(Reference2ReferenceSortedMaps.fastIterator(AbstractReference2ReferenceSortedMap.this));
      }
   }
}
