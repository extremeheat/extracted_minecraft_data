package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractObject2ReferenceSortedMap<K, V> extends AbstractObject2ReferenceMap<K, V> implements Object2ReferenceSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ReferenceSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractObject2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Object2ReferenceMap.Entry)this.i.next()).getValue();
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
         return new AbstractObject2ReferenceSortedMap.ValuesIterator(Object2ReferenceSortedMaps.fastIterator(AbstractObject2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractObject2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2ReferenceMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2ReferenceMap.Entry)this.i.previous()).getKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      protected KeySet() {
         super();
      }

      public boolean contains(Object var1) {
         return AbstractObject2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ReferenceSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2ReferenceSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2ReferenceSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2ReferenceSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2ReferenceSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2ReferenceSortedMap.KeySetIterator(AbstractObject2ReferenceSortedMap.this.object2ReferenceEntrySet().iterator(new AbstractObject2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ReferenceSortedMap.KeySetIterator(Object2ReferenceSortedMaps.fastIterator(AbstractObject2ReferenceSortedMap.this));
      }
   }
}
