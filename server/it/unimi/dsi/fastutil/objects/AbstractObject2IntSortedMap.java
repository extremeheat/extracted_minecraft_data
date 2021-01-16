package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Comparator;

public abstract class AbstractObject2IntSortedMap<K> extends AbstractObject2IntMap<K> implements Object2IntSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2IntSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2IntSortedMap.KeySet();
   }

   public IntCollection values() {
      return new AbstractObject2IntSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements IntIterator {
      protected final ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2IntMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public int nextInt() {
         return ((Object2IntMap.Entry)this.i.next()).getIntValue();
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
         return new AbstractObject2IntSortedMap.ValuesIterator(Object2IntSortedMaps.fastIterator(AbstractObject2IntSortedMap.this));
      }

      public boolean contains(int var1) {
         return AbstractObject2IntSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2IntSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2IntMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2IntMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2IntMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2IntMap.Entry)this.i.previous()).getKey();
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
         return AbstractObject2IntSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2IntSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2IntSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2IntSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2IntSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2IntSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2IntSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2IntSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2IntSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2IntSortedMap.KeySetIterator(AbstractObject2IntSortedMap.this.object2IntEntrySet().iterator(new AbstractObject2IntMap.BasicEntry(var1, 0)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2IntSortedMap.KeySetIterator(Object2IntSortedMaps.fastIterator(AbstractObject2IntSortedMap.this));
      }
   }
}
