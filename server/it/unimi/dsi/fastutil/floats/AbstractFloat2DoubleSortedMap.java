package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2DoubleSortedMap extends AbstractFloat2DoubleMap implements Float2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2DoubleSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2DoubleSortedMap.KeySet();
   }

   public DoubleCollection values() {
      return new AbstractFloat2DoubleSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Float2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public double nextDouble() {
         return ((Float2DoubleMap.Entry)this.i.next()).getDoubleValue();
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
         return new AbstractFloat2DoubleSortedMap.ValuesIterator(Float2DoubleSortedMaps.fastIterator(AbstractFloat2DoubleSortedMap.this));
      }

      public boolean contains(double var1) {
         return AbstractFloat2DoubleSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2DoubleSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2DoubleMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2DoubleMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2DoubleMap.Entry)this.i.previous()).getFloatKey();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class KeySet extends AbstractFloatSortedSet {
      protected KeySet() {
         super();
      }

      public boolean contains(float var1) {
         return AbstractFloat2DoubleSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2DoubleSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2DoubleSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2DoubleSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2DoubleSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2DoubleSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2DoubleSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2DoubleSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2DoubleSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2DoubleSortedMap.KeySetIterator(AbstractFloat2DoubleSortedMap.this.float2DoubleEntrySet().iterator(new AbstractFloat2DoubleMap.BasicEntry(var1, 0.0D)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2DoubleSortedMap.KeySetIterator(Float2DoubleSortedMaps.fastIterator(AbstractFloat2DoubleSortedMap.this));
      }
   }
}
