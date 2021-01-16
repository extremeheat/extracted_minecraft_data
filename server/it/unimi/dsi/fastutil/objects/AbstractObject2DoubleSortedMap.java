package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import java.util.Comparator;

public abstract class AbstractObject2DoubleSortedMap<K> extends AbstractObject2DoubleMap<K> implements Object2DoubleSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2DoubleSortedMap() {
      super();
   }

   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractObject2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<K> implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Object2DoubleMap.Entry)this.i.next()).getDoubleValue();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }
   }

   protected class ValuesCollection extends AbstractDoubleCollection {
      protected ValuesCollection() {
         super();
      }

      public DoubleIterator iterator() {
         return new AbstractObject2DoubleSortedMap.ValuesIterator(Object2DoubleSortedMaps.fastIterator(AbstractObject2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractObject2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractObject2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2DoubleMap.Entry<K>> var1) {
         super();
         this.i = var1;
      }

      public K next() {
         return ((Object2DoubleMap.Entry)this.i.next()).getKey();
      }

      public K previous() {
         return ((Object2DoubleMap.Entry)this.i.previous()).getKey();
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
         return AbstractObject2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractObject2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractObject2DoubleSortedMap.this.clear();
      }

      public Comparator<? super K> comparator() {
         return AbstractObject2DoubleSortedMap.this.comparator();
      }

      public K first() {
         return AbstractObject2DoubleSortedMap.this.firstKey();
      }

      public K last() {
         return AbstractObject2DoubleSortedMap.this.lastKey();
      }

      public ObjectSortedSet<K> headSet(K var1) {
         return AbstractObject2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public ObjectSortedSet<K> tailSet(K var1) {
         return AbstractObject2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public ObjectSortedSet<K> subSet(K var1, K var2) {
         return AbstractObject2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public ObjectBidirectionalIterator<K> iterator(K var1) {
         return new AbstractObject2DoubleSortedMap.KeySetIterator(AbstractObject2DoubleSortedMap.this.object2DoubleEntrySet().iterator(new AbstractObject2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2DoubleSortedMap.KeySetIterator(Object2DoubleSortedMaps.fastIterator(AbstractObject2DoubleSortedMap.this));
      }
   }
}
