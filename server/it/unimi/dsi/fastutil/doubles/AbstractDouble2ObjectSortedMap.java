package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractDouble2ObjectSortedMap<V> extends AbstractDouble2ObjectMap<V> implements Double2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2ObjectSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2ObjectSortedMap.KeySet();
   }

   public ObjectCollection<V> values() {
      return new AbstractDouble2ObjectSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Double2ObjectMap.Entry)this.i.next()).getValue();
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
         return new AbstractDouble2ObjectSortedMap.ValuesIterator(Double2ObjectSortedMaps.fastIterator(AbstractDouble2ObjectSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractDouble2ObjectSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ObjectSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2ObjectMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2ObjectMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2ObjectMap.Entry)this.i.previous()).getDoubleKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(double var1) {
         return AbstractDouble2ObjectSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2ObjectSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ObjectSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2ObjectSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2ObjectSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2ObjectSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2ObjectSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2ObjectSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2ObjectSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2ObjectSortedMap.KeySetIterator(AbstractDouble2ObjectSortedMap.this.double2ObjectEntrySet().iterator(new AbstractDouble2ObjectMap.BasicEntry(var1, (Object)null)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2ObjectSortedMap.KeySetIterator(Double2ObjectSortedMaps.fastIterator(AbstractDouble2ObjectSortedMap.this));
      }
   }
}
