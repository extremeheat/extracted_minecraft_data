package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractReference2ObjectSortedMap<K, V> extends AbstractReference2ObjectMap<K, V> implements Reference2ObjectSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ObjectSortedMap() {
      super();
   }

   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractReference2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Reference2ObjectMap.Entry)this.i.next()).getValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      protected ValuesCollection() {
         super();
      }

      public ObjectIterator<V> iterator() {
         return new AbstractReference2ObjectSortedMap.ValuesIterator(Reference2ObjectSortedMaps.fastIterator(AbstractReference2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractReference2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractReference2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Reference2ObjectMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Reference2ObjectMap.Entry)this.i.previous()).getKey();
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
         return AbstractReference2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractReference2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractReference2ObjectSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractReference2ObjectSortedMap.this.comparator();
      }

      public K first() {
         return AbstractReference2ObjectSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractReference2ObjectSortedMap.this.lastKey();
      }

      public ReferenceSortedSet<K> headSet(K var1) {
         return AbstractReference2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public ReferenceSortedSet<K> tailSet(K var1) {
         return AbstractReference2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public ReferenceSortedSet<K> subSet(K var1, K var2) {
         return AbstractReference2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractReference2ObjectSortedMap.KeySetIterator(AbstractReference2ObjectSortedMap.this.reference2ObjectEntrySet().iterator(new AbstractReference2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ObjectSortedMap.KeySetIterator(Reference2ObjectSortedMaps.fastIterator(AbstractReference2ObjectSortedMap.this));
      }
   }
}
