package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractObject2ObjectSortedMap<K, V> extends AbstractObject2ObjectMap<K, V> implements Object2ObjectSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ObjectSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractObject2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Object2ObjectMap.Entry)this.i.next()).getValue();
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
         return new AbstractObject2ObjectSortedMap.ValuesIterator(Object2ObjectSortedMaps.fastIterator(AbstractObject2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractObject2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ObjectMap.Entry<K, V>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2ObjectMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2ObjectMap.Entry)this.i.previous()).getKey();
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
         return AbstractObject2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2ObjectSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2ObjectSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2ObjectSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2ObjectSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2ObjectSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2ObjectSortedMap.KeySetIterator(AbstractObject2ObjectSortedMap.this.object2ObjectEntrySet().iterator(new AbstractObject2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ObjectSortedMap.KeySetIterator(Object2ObjectSortedMaps.fastIterator(AbstractObject2ObjectSortedMap.this));
      }
   }
}
