package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractDouble2ReferenceSortedMap<V> extends AbstractDouble2ReferenceMap<V> implements Double2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2ReferenceSortedMap() {
      super();
   }

   public DoubleSortedSet keySet() {
      return new AbstractDouble2ReferenceSortedMap.KeySet();
   }

   public ReferenceCollection<V> values() {
      return new AbstractDouble2ReferenceSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public V next() {
         return ((Double2ReferenceMap.Entry)this.i.next()).getValue();
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
         return new AbstractDouble2ReferenceSortedMap.ValuesIterator(Double2ReferenceSortedMaps.fastIterator(AbstractDouble2ReferenceSortedMap.this));
      }

      public boolean contains(Object var1) {
         return AbstractDouble2ReferenceSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractDouble2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ReferenceSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator<V> implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2ReferenceMap.Entry<V>> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Double2ReferenceMap.Entry)this.i.next()).getDoubleKey();
      }

      public double previousDouble() {
         return ((Double2ReferenceMap.Entry)this.i.previous()).getDoubleKey();
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
         return AbstractDouble2ReferenceSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractDouble2ReferenceSortedMap.this.size();
      }

      public void clear() {
         AbstractDouble2ReferenceSortedMap.this.clear();
      }

      public DoubleComparator comparator() {
         return AbstractDouble2ReferenceSortedMap.this.comparator();
      }

      public double firstDouble() {
         return AbstractDouble2ReferenceSortedMap.this.firstDoubleKey();
      }

      public double lastDouble() {
         return AbstractDouble2ReferenceSortedMap.this.lastDoubleKey();
      }

      public DoubleSortedSet headSet(double var1) {
         return AbstractDouble2ReferenceSortedMap.this.headMap(var1).keySet();
      }

      public DoubleSortedSet tailSet(double var1) {
         return AbstractDouble2ReferenceSortedMap.this.tailMap(var1).keySet();
      }

      public DoubleSortedSet subSet(double var1, double var3) {
         return AbstractDouble2ReferenceSortedMap.this.subMap(var1, var3).keySet();
      }

      public DoubleBidirectionalIterator iterator(double var1) {
         return new AbstractDouble2ReferenceSortedMap.KeySetIterator(AbstractDouble2ReferenceSortedMap.this.double2ReferenceEntrySet().iterator(new AbstractDouble2ReferenceMap.BasicEntry(var1, (Object)null)));
      }

      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2ReferenceSortedMap.KeySetIterator(Double2ReferenceSortedMaps.fastIterator(AbstractDouble2ReferenceSortedMap.this));
      }
   }
}
