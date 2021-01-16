package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Comparator;

public abstract class AbstractObject2FloatSortedMap<K> extends AbstractObject2FloatMap<K> implements Object2FloatSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2FloatSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractObject2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements FloatIterator {
      protected final ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Object2FloatMap.Entry)this.i.next()).getFloatValue();
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
         return new AbstractObject2FloatSortedMap.ValuesIterator(Object2FloatSortedMaps.fastIterator(AbstractObject2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractObject2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2FloatMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2FloatMap.Entry)this.i.previous()).getKey();
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
         return AbstractObject2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2FloatSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2FloatSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2FloatSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2FloatSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2FloatSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2FloatSortedMap.KeySetIterator(AbstractObject2FloatSortedMap.this.object2FloatEntrySet().iterator(new AbstractObject2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2FloatSortedMap.KeySetIterator(Object2FloatSortedMaps.fastIterator(AbstractObject2FloatSortedMap.this));
      }
   }
}
