package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2FloatSortedMap extends AbstractFloat2FloatMap implements Float2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2FloatSortedMap() {
      super();
   }

   public FloatSortedSet keySet() {
      return new AbstractFloat2FloatSortedMap.KeySet();
   }

   public FloatCollection values() {
      return new AbstractFloat2FloatSortedMap.ValuesCollection();
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Float2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2FloatMap.Entry)this.i.next()).getFloatValue();
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
         return new AbstractFloat2FloatSortedMap.ValuesIterator(Float2FloatSortedMaps.fastIterator(AbstractFloat2FloatSortedMap.this));
      }

      public boolean contains(float var1) {
         return AbstractFloat2FloatSortedMap.this.containsValue(var1);
      }

      public int size() {
         return AbstractFloat2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2FloatSortedMap.this.clear();
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2FloatMap.Entry> var1) {
         super();
         this.i = var1;
      }

      public float nextFloat() {
         return ((Float2FloatMap.Entry)this.i.next()).getFloatKey();
      }

      public float previousFloat() {
         return ((Float2FloatMap.Entry)this.i.previous()).getFloatKey();
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
         return AbstractFloat2FloatSortedMap.this.containsKey(var1);
      }

      public int size() {
         return AbstractFloat2FloatSortedMap.this.size();
      }

      public void clear() {
         AbstractFloat2FloatSortedMap.this.clear();
      }

      public FloatComparator comparator() {
         return AbstractFloat2FloatSortedMap.this.comparator();
      }

      public float firstFloat() {
         return AbstractFloat2FloatSortedMap.this.firstFloatKey();
      }

      public float lastFloat() {
         return AbstractFloat2FloatSortedMap.this.lastFloatKey();
      }

      public FloatSortedSet headSet(float var1) {
         return AbstractFloat2FloatSortedMap.this.headMap(var1).keySet();
      }

      public FloatSortedSet tailSet(float var1) {
         return AbstractFloat2FloatSortedMap.this.tailMap(var1).keySet();
      }

      public FloatSortedSet subSet(float var1, float var2) {
         return AbstractFloat2FloatSortedMap.this.subMap(var1, var2).keySet();
      }

      public FloatBidirectionalIterator iterator(float var1) {
         return new AbstractFloat2FloatSortedMap.KeySetIterator(AbstractFloat2FloatSortedMap.this.float2FloatEntrySet().iterator(new AbstractFloat2FloatMap.BasicEntry(var1, 0.0F)));
      }

      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2FloatSortedMap.KeySetIterator(Float2FloatSortedMaps.fastIterator(AbstractFloat2FloatSortedMap.this));
      }
   }
}
